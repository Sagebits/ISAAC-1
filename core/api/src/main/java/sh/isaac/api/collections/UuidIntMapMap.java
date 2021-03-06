/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * You may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributions from 2013-2017 where performed either by US government 
 * employees, or under US Veterans Health Administration contracts. 
 *
 * US Veterans Health Administration contributions by government employees
 * are work of the U.S. Government and are not subject to copyright
 * protection in the United States. Portions contributed by government 
 * employees are USGovWork (17USC §105). Not subject to copyright. 
 * 
 * Contribution by contractors to the US Veterans Health Administration
 * during this period are contractually contributed under the
 * Apache License, Version 2.0.
 *
 * See: https://www.usa.gov/government-works
 * 
 * Contributions prior to 2013:
 *
 * Copyright (C) International Health Terminology Standards Development Organisation.
 * Licensed under the Apache License, Version 2.0.
 *
 */
package sh.isaac.api.collections;

//~--- JDK imports ------------------------------------------------------------
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
//~--- non-JDK imports --------------------------------------------------------
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import sh.isaac.api.ConfigurationService.BuildMode;
import sh.isaac.api.Get;
import sh.isaac.api.collections.uuidnidmap.ConcurrentUuidToIntHashMap;
import sh.isaac.api.collections.uuidnidmap.UuidToIntMap;
import sh.isaac.api.externalizable.ByteArrayDataBuffer;
import sh.isaac.api.memory.DiskSemaphore;
import sh.isaac.api.memory.HoldInMemoryCache;
import sh.isaac.api.memory.MemoryManagedReference;
import sh.isaac.api.memory.WriteToDiskCache;
import sh.isaac.api.util.UUIDUtil;

//~--- classes ----------------------------------------------------------------
/**
 * Created by kec on 7/27/14.
 */
public class UuidIntMapMap
        implements UuidToIntMap {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * The Constant DEFAULT_TOTAL_MAP_SIZE.
     */
    private static final int DEFAULT_TOTAL_MAP_SIZE = 15000000;

    /**
     * The Constant NUMBER_OF_MAPS.
     */
    private static final int NUMBER_OF_MAPS = 256;

    /**
     * The Constant DEFAULT_MAP_SIZE.
     */
    private static final int DEFAULT_MAP_SIZE = DEFAULT_TOTAL_MAP_SIZE / NUMBER_OF_MAPS;

    /**
     * The Constant MIN_LOAD_FACTOR.
     */
    private static final double MIN_LOAD_FACTOR = 0.75;

    /**
     * The Constant MAX_LOAD_FACTOR.
     */
    private static final double MAX_LOAD_FACTOR = 0.9;

    /**
     * The Constant NEXT_NID_PROVIDER.
     */
    private final AtomicInteger NEXT_NID_PROVIDER = new AtomicInteger(Integer.MIN_VALUE);

    //~--- fields --------------------------------------------------------------

    /**
     * The maps.
     */
    @SuppressWarnings("unchecked")
    private final MemoryManagedReference<ConcurrentUuidToIntHashMap>[] maps = new MemoryManagedReference[NUMBER_OF_MAPS];

    /**
     * The nid to primordial cache.
     */
    private Cache<Integer, UUID[]> nidToPrimordialCache = null;

    /**
     * The lock.
     */
    ReentrantLock lock = new ReentrantLock();

    /**
     * The folder.
     */
    private final File folder;

    //~--- constructors --------------------------------------------------------
    /**
     * Instantiates a new uuid int map map.
     *
     * @param folder the folder
     */
    private UuidIntMapMap(File folder) {
        folder.mkdirs();
        this.folder = folder;

        for (int i = 0; i < this.maps.length; i++) {
            this.maps[i] = new MemoryManagedReference<>(null, new File(folder, i + "-uuid-nid.map"));
            WriteToDiskCache.addToCache(this.maps[i]);
        }

        //Loader utility enables this when doing IBDF file creation to to get from nid back to UUID  - this prevents it from doing table scans.
        if (Get.configurationService().isInDBBuildMode(BuildMode.IBDF)) {
            enableInverseCache();
        }
        
        File params = new File(folder, "map.params");
        try {
         if (params.isFile()) {
              ByteArrayDataBuffer badb = new ByteArrayDataBuffer(Files.readAllBytes(params.toPath()));
              NEXT_NID_PROVIDER.set(badb.getInt());
           }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

        LOG.debug("Created UuidIntMapMap: " + this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean cacheContainsNid(int nid) {
        if (this.nidToPrimordialCache != null) {
            return this.nidToPrimordialCache.getIfPresent(nid) != null;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(UUID key) {
        return getMap(key).containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(int value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates the.
     *
     * @param folder the folder
     * @return the uuid int map map
     */
    public static UuidIntMapMap create(File folder) {
        return new UuidIntMapMap(folder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean put(UUID uuidKey, int value) {
        final int mapIndex = getMapIndex(uuidKey);
        final long[] keyAsArray = UUIDUtil.convert(uuidKey);
        final ConcurrentUuidToIntHashMap map = getMap(mapIndex);
        final long stamp = map.getStampedLock()
                .writeLock();

        try {
            final boolean returnValue = map.put(keyAsArray, value, stamp);

            this.maps[mapIndex].elementUpdated();
            if (returnValue) {
               updateCache(value, uuidKey);
            }
            return returnValue;
        } finally {
            map.getStampedLock()
                    .unlockWrite(stamp);
        }
    }

    /**
     * Report stats.
     *
     * @param log the log
     */
    public void reportStats(Logger log) {
        for (int i = 0; i < NUMBER_OF_MAPS; i++) {
            log.info("UUID map: " + i + " " + getMap(i).getStats());
        }
    }

    /**
     * The number of UUIDs mapped to nids.
     *
     * @return the int
     */
    public int size() {
        int size = 0;

        for (int i = 0; i < this.maps.length; i++) {
            size += getMap(i).size();
        }

        return size;
    }

    /**
     * Write.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void write()
            throws IOException {
        for (int i = 0; i < NUMBER_OF_MAPS; i++) {
            final ConcurrentUuidToIntHashMap map = this.maps[i].get();

            if ((map != null) && this.maps[i].hasUnwrittenUpdate()) {
                this.maps[i].write();
            }
        }
        
        ByteArrayDataBuffer badb = new ByteArrayDataBuffer();
        badb.putInt(NEXT_NID_PROVIDER.get());

        Files.write(new File(folder, "map.params").toPath(), badb.getData());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMemoryInUse() {
        int memoryInUse = 0;
        for (MemoryManagedReference<ConcurrentUuidToIntHashMap> map : maps) {
            if (map.get() != null) {
                memoryInUse += map.get().getMemoryInUse();
            }
        }
        return memoryInUse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDiskSpaceUsed() {
        int memoryInUse = 0;
        for (int mapIndex = 0; mapIndex < this.maps.length; mapIndex++) {
            memoryInUse += getMapDiskUsage(mapIndex);
        }
        return memoryInUse;
    }

    protected int getMapDiskUsage(int i) {
        final File mapFile = new File(this.folder, i + "-uuid-nid.map");

        if (mapFile.exists()) {
            return (int) mapFile.length();
        }
        return 0;
    }

    /**
     * Read map from disk.
     *
     * @param i the i
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void readMapFromDisk(int i)
            throws IOException {
        this.lock.lock();

        try {
            if (this.maps[i].get() == null) {
                final File mapFile = new File(this.folder, i + "-uuid-nid.map");

                if (mapFile.exists()) {
                    DiskSemaphore.acquire();

                    try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(mapFile)))) {
                        this.maps[i] = new MemoryManagedReference<>(ConcurrentUuidToIntHashMap.deserialize(in), mapFile);
                        WriteToDiskCache.addToCache(this.maps[i]);
                        LOG.debug("UuidIntMapMap restored: " + i + " from: " + this + " file: " + mapFile.getAbsolutePath());
                    } finally {
                        DiskSemaphore.release();
                    }
                } else {
                    this.maps[i] = new MemoryManagedReference<>(new ConcurrentUuidToIntHashMap(DEFAULT_MAP_SIZE,
                            MIN_LOAD_FACTOR,
                            MAX_LOAD_FACTOR),
                            new File(this.folder, i + "-uuid-nid.map"));
                    WriteToDiskCache.addToCache(this.maps[i]);
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Update cache.
     *
     * @param nid the nid
     * @param uuidKey the uuid key
     */
    private void updateCache(int nid, UUID uuidKey) {
        if (this.nidToPrimordialCache != null) {
            synchronized (nidToPrimordialCache) {
                final UUID[] temp = this.nidToPrimordialCache.getIfPresent(nid);
                UUID[] temp1;
    
                if (temp == null) {
                    temp1 = new UUID[]{uuidKey};
                } else {
                    temp1 = Arrays.copyOf(temp, temp.length + 1);
                    temp1[temp.length] = uuidKey;
                }
    
                this.nidToPrimordialCache.put(nid, temp1);
            }
        }
    }

    //~--- get methods ---------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public OptionalInt get(UUID key) {
        return getMap(key).get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID[] getKeysForValue(int nid) {
        if (this.nidToPrimordialCache != null) {
            final UUID[] cacheHit = this.nidToPrimordialCache.getIfPresent(nid);

            if ((cacheHit != null) && (cacheHit.length > 0)) {
                return cacheHit;
            }
        }

        final ArrayList<UUID> uuids = new ArrayList<>();

        for (int index = 0; index < this.maps.length; index++) {
            getMap(index).keysOf(nid)
                    .stream()
                    .forEach(uuid -> {
                        uuids.add(uuid);
                    });
        }

        final UUID[] temp = uuids.toArray(new UUID[uuids.size()]);

        if ((this.nidToPrimordialCache != null) && (temp.length > 0)) {
            this.nidToPrimordialCache.put(nid, temp);
        }

        return temp;
    }

    /**
     * Gets the map.
     *
     * @param index the index
     * @return the map
     * @throws RuntimeException the runtime exception
     */
    protected ConcurrentUuidToIntHashMap getMap(int index)
            throws RuntimeException {
        ConcurrentUuidToIntHashMap result = this.maps[index].get();

        while (result == null) {
            try {
                readMapFromDisk(index);
                result = this.maps[index].get();
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        this.maps[index].elementRead();
        HoldInMemoryCache.addToCache(this.maps[index]);
        return result;
    }

    /**
     * Gets the map.
     *
     * @param key the key
     * @return the map
     */
    private ConcurrentUuidToIntHashMap getMap(UUID key) {
        if (key == null) {
            throw new IllegalStateException("UUIDs cannot be null. ");
        }

        final int index = getMapIndex(key);

        return getMap(index);
    }

    /**
     * Gets the map index.
     *
     * @param key the key
     * @return the map index
     */
    private int getMapIndex(UUID key) {
        return (((byte) key.hashCode())) - Byte.MIN_VALUE;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxNid() {
      return NEXT_NID_PROVIDER.get();
    }
    
    //~--- get methods ---------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public int getWithGeneration(UUID uuidKey) {
        final long[] keyAsArray = UUIDUtil.convert(uuidKey);
        final int mapIndex = getMapIndex(uuidKey);
        OptionalInt nid = getMap(mapIndex).get(keyAsArray);

        if (nid.isPresent()) {
            return nid.getAsInt();
        }

        final ConcurrentUuidToIntHashMap map = getMap(mapIndex);
        final long stamp = map.getStampedLock()
                .writeLock();

        try {
            nid = map.get(keyAsArray, stamp);

            if (nid.isPresent()) {
                return nid.getAsInt();
            }

            int intNid = NEXT_NID_PROVIDER.incrementAndGet();

            this.maps[mapIndex].elementUpdated();
            map.put(keyAsArray, intNid, stamp);
            updateCache(intNid, uuidKey);
            return intNid;
        } finally {
            map.getStampedLock()
                    .unlockWrite(stamp);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean inverseCacheEnabled() {
        return nidToPrimordialCache != null;
    }

    /**
     * @see sh.isaac.api.collections.uuidnidmap.UuidToIntMap#enableInverseCache()
     */
    @Override
    public void enableInverseCache()
    {
        if (this.nidToPrimordialCache == null) {
            this.nidToPrimordialCache = Caffeine.newBuilder().build();
        }
    }
}
