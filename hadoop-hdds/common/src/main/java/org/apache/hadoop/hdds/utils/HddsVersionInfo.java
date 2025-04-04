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

package org.apache.hadoop.hdds.utils;

import org.apache.hadoop.hdds.annotation.InterfaceAudience;
import org.apache.hadoop.hdds.annotation.InterfaceStability;
import org.apache.hadoop.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class returns build information about Hadoop components.
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public final class HddsVersionInfo {

  private static final Logger LOG = LoggerFactory.getLogger(
      HddsVersionInfo.class);

  public static final VersionInfo HDDS_VERSION_INFO =
      new VersionInfo("hdds");

  private HddsVersionInfo() { }

  public static void main(String[] args) {
    System.out.println("Using HDDS " + HDDS_VERSION_INFO.getVersion());
    System.out.println(
        "Source code repository " + HDDS_VERSION_INFO.getUrl() + " -r " +
            HDDS_VERSION_INFO.getRevision());
    System.out.println(
        "Compiled with protoc " + HDDS_VERSION_INFO.getHadoopProtoc2Version() +
            ", " + HDDS_VERSION_INFO.getGrpcProtocVersion() +
            " and " + HDDS_VERSION_INFO.getHadoopProtoc3Version());
    System.out.println(
        "From source with checksum " + HDDS_VERSION_INFO.getSrcChecksum());
    System.out.println(
        "Compiled on platform " + HDDS_VERSION_INFO.getCompilePlatform());
    if (LOG.isDebugEnabled()) {
      LOG.debug("This command was run using " +
          ClassUtil.findContainingJar(HddsVersionInfo.class));
    }
  }
}
