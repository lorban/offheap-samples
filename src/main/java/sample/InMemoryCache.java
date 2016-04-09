/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample;

import org.terracotta.offheapstore.MapInternals;
import org.terracotta.offheapstore.buffersource.OffHeapBufferSource;
import org.terracotta.offheapstore.concurrent.ConcurrentOffHeapClockCache;
import org.terracotta.offheapstore.paging.PageSource;
import org.terracotta.offheapstore.paging.UpfrontAllocatingPageSource;
import org.terracotta.offheapstore.storage.OffHeapBufferStorageEngine;
import org.terracotta.offheapstore.storage.PointerSize;
import org.terracotta.offheapstore.storage.portability.SerializablePortability;
import org.terracotta.offheapstore.util.Factory;

/**
 * @author Ludovic Orban
 */
public class InMemoryCache {

  public static void main(String[] args) throws Exception {
    // total offheap memory allocation size
    long offheapSize = 128 * 1024 * 1024;
    // the PageSource allocates offheap memory per chunk; no allocation greater than the chunk size can be served
    int chunkSize = 16 * 1024 * 1024;
    // the page size is how much memory at a time the storage engine allocates
    int pageSize = 32 * 1024;
    // how many slots in the hash table
    int tableSize = 128 * 1024;
    // how many segments in the concurrent map
    int concurrency = 128;

    PageSource source = new UpfrontAllocatingPageSource(new OffHeapBufferSource(), offheapSize, chunkSize);

    SerializablePortability keyPortability = new SerializablePortability();
    SerializablePortability valuePortability = new SerializablePortability();

    Factory<OffHeapBufferStorageEngine<String, String>> storageEngineFactory = OffHeapBufferStorageEngine.createFactory(PointerSize.INT, source, pageSize, keyPortability, valuePortability, false, false);

    ConcurrentOffHeapClockCache<String, String> map = new ConcurrentOffHeapClockCache<String, String>(source, storageEngineFactory, tableSize, concurrency);

    map.put("1", "one");
    map.put("2", "two");

    MapInternals statistics = map;
    System.out.println("AllocatedMemory: " + statistics.getAllocatedMemory());
    System.out.println("DataSize: " + statistics.getDataSize());
    System.out.println("TableCapacity: " + statistics.getTableCapacity());

    map.destroy();
  }

}
