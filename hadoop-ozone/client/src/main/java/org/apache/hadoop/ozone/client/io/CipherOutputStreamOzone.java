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

package org.apache.hadoop.ozone.client.io;

import java.io.OutputStream;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

/**
 * Wrap javax.crypto.CipherOutputStream with the method to return wrapped
 * output stream.
 */
public class CipherOutputStreamOzone extends CipherOutputStream
    implements KeyMetadataAware {

  private OutputStream output;

  public CipherOutputStreamOzone(OutputStream output, Cipher cipher) {
    super(output, cipher);
    this.output = output;
  }

  protected CipherOutputStreamOzone(OutputStream output) {
    super(output);
    this.output = output;
  }

  public OutputStream getWrappedStream() {
    return output;
  }

  @Override
  public Map<String, String> getMetadata() {
    return ((KeyMetadataAware)getWrappedStream()).getMetadata();
  }
}
