/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.ozone.s3;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Input stream implementation to read body with chunked signatures. This should also work
 * with the chunked payloads with trailer.
 *
 * Note that there are no actual chunk signature verification taking place. The InputStream only
 * returns the actual chunk payload from chunked signatures format.
 *
 * See
 * - https://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-streaming.html
 * - https://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-streaming-trailers.html
 */
public class SignedChunksInputStream extends InputStream {

  private final Pattern signatureLinePattern =
      Pattern.compile("([0-9A-Fa-f]+);chunk-signature=.*");

  private final InputStream originalStream;

  /**
   * Size of the chunk payload. If zero, the signature line should be parsed to
   * retrieve the subsequent chunk payload size.
   */
  private int remainingData = 0;

  public SignedChunksInputStream(InputStream inputStream) {
    originalStream = inputStream;
  }

  @Override
  public int read() throws IOException {
    if (remainingData > 0) {
      int curr = originalStream.read();
      remainingData--;
      if (remainingData == 0) {
        //read the "\r\n" at the end of the data section
        originalStream.read();
        originalStream.read();
      }
      return curr;
    } else {
      remainingData = readContentLengthFromHeader();
      if (remainingData == -1) {
        return -1;
      }
      return read();
    }
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    if (b == null) {
      throw new NullPointerException();
    } else if (off < 0 || len < 0 || len > b.length - off) {
      throw new IndexOutOfBoundsException();
    } else if (len == 0) {
      return 0;
    }
    int currentOff = off;
    int currentLen = len;
    int totalReadBytes = 0;
    int realReadLen = 0;
    int maxReadLen = 0;
    do {
      if (remainingData > 0) {
        // The chunk payload size has been decoded, now read the actual chunk payload
        maxReadLen = Math.min(remainingData, currentLen);
        realReadLen = originalStream.read(b, currentOff, maxReadLen);
        if (realReadLen == -1) {
          break;
        }
        currentOff += realReadLen;
        currentLen -= realReadLen;
        totalReadBytes += realReadLen;
        remainingData -= realReadLen;
        if (remainingData == 0) {
          //read the "\r\n" at the end of the data section
          originalStream.read();
          originalStream.read();
        }
      } else {
        remainingData = readContentLengthFromHeader();
        if (remainingData == -1) {
          break;
        }
      }
    } while (currentLen > 0);
    return totalReadBytes > 0 ? totalReadBytes : -1;
  }

  private int readContentLengthFromHeader() throws IOException {
    int prev = -1;
    int curr = 0;
    StringBuilder buf = new StringBuilder();

    //read everything until the next \r\n
    while (!eol(prev, curr) && curr != -1) {
      int next = originalStream.read();
      if (next != -1) {
        buf.append((char) next);
      }
      prev = curr;
      curr = next;
    }
    // Example
    // The chunk data sent:
    //  10000;chunk-signature=b474d8862b1487a5145d686f57f013e54db672cee1c953b3010fb58501ef5aa2
    //  <65536-bytes>
    //
    // 10000 will be read and decoded from base-16 representation to 65536, which is the size of
    // the subsequent chunk payload.
    String signatureLine = buf.toString().trim();
    if (signatureLine.isEmpty()) {
      return -1;
    }

    //parse the data length.
    Matcher matcher = signatureLinePattern.matcher(signatureLine);
    if (matcher.matches()) {
      return Integer.parseInt(matcher.group(1), 16);
    } else {
      throw new IOException("Invalid signature line: " + signatureLine);
    }
  }

  private boolean eol(int prev, int curr) {
    return prev == 13 && curr == 10;
  }
}
