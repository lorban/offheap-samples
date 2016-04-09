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
import org.terracotta.offheapstore.OffHeapHashMap;
import org.terracotta.offheapstore.disk.paging.MappedPageSource;
import org.terracotta.offheapstore.paging.PageSource;
import org.terracotta.offheapstore.storage.OffHeapBufferStorageEngine;
import org.terracotta.offheapstore.storage.PointerSize;
import org.terracotta.offheapstore.storage.portability.SerializablePortability;
import org.terracotta.offheapstore.util.Factory;

import java.io.File;

/**
 * @author Ludovic Orban
 */
public class OnDiskTransient {

  public static void main(String[] args) throws Exception {
    // total offheap memory allocation size
    long offheapSize = 128 * 1024 * 1024;
    // the page size is how much memory at a time the storage engine allocates
    int pageSize = 32 * 1024;

    PageSource source = new MappedPageSource(new File("offheap.data"), offheapSize);

    SerializablePortability keyPortability = new SerializablePortability();
    SerializablePortability valuePortability = new SerializablePortability();

    Factory<OffHeapBufferStorageEngine<String, String>> storageEngineFactory = OffHeapBufferStorageEngine.createFactory(PointerSize.INT, source, pageSize, keyPortability, valuePortability, false, false);

    OffHeapHashMap<String, String> map = new OffHeapHashMap<String, String>(source, storageEngineFactory.newInstance());

    map.put("1", "one");
    map.put("2", "two");

    MapInternals statistics = map;
    System.out.println("AllocatedMemory: " + statistics.getAllocatedMemory());
    System.out.println("DataSize: " + statistics.getDataSize());
    System.out.println("TableCapacity: " + statistics.getTableCapacity());

    map.destroy();
  }

}
