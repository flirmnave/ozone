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

package org.apache.hadoop.hdds.utils.db.cache;

import java.util.Objects;

/**
 * CacheKey for the RocksDB table.
 * @param <KEY>
 */
public class CacheKey<KEY> implements Comparable<CacheKey<KEY>> {

  private final KEY key;

  public CacheKey(KEY key) {
    Objects.requireNonNull(key, "Key Should not be null in CacheKey");
    this.key = key;
  }

  public KEY getCacheKey() {
    return key;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CacheKey<?> cacheKey = (CacheKey<?>) o;
    return Objects.equals(key, cacheKey.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }

  @Override
  public int compareTo(CacheKey<KEY> other) {
    if (Objects.equals(key, other.key)) {
      return 0;
    } else {
      return key.toString().compareTo(other.key.toString());
    }
  }
}
