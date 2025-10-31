package src;

import java.util.LinkedHashMap;

public class Cache {
    // 1st value is the address, 2nd value is the data
    private final LinkedHashMap<Short, Short> cacheMap;
    private static final int CACHE_SIZE = 16;
    private final Memory memory = new Memory();

    //From projct description page 8:
    /**
     * 3.3 Simple Cache
     * You will, in part II, implement a simple cache, which sits between memory and the rest
     * of the processor.
     * The cache is just a vector having a format similar to that described in the lecture notes.
     * It should be a fully associative, unified cache.
     * What do you need to do?
     * 1. What are the fields of the cache line?
     * 2. Use a simple FIFO algorithm to replace cache lines.
     * 3. How many cache lines? With 2048 words, probably 16 cache lines is enough.
     * 4. Need to think about how to demonstrate caching works.
     * HINT: When you run your simulator, you may want to write a trace file of things that are
     * happening inside your simulator. You can use this to help debug your simulator.
     * REMEMBER: More trace data is always useful!!
     */

    //What are the principal operations of a cache?
    //1.   Instruction atempts to read data.
    //1a.  First, the cache checks if data is in the cache.
    //1b.  If not, cache requests data from memory in a FIFO order.
    //1b2. Cache must maintain addresses of memory as well as data. So each item in the cache is like a mapping.
    //1c.  If data is in the cache, it is returned directly.
    //2.   Instruction writes to memory.
    //2a.  Cache does a "pass-thru" write to memory (so it always calls the cache, cache checks if existing cache
    // value needs updating, if so, it updates the cache. Cache always writes to memory after checking cache.

    public Cache() {
        cacheMap = new LinkedHashMap<>();
    }
    //Public methods:
    //1.   read(address)
    public short read(int address) {
        System.out.println("Cache READ: addr=" + address);
        System.out.println("Cache map: " + cacheMap.toString() + "\n");
        short addressShort = (short) (address & 0xFFFF);
        if (isCacheHit(addressShort)) {
            return cacheMap.get(addressShort);
        } else {
            short data = memory.read(address);
            updateCache(addressShort, data);
            return data;
        }
    }
    //2.   write(address, data)
    public void write(int address, int data) {
        System.out.println("Cache WRITE: addr=" + address + " data=" + data);
        System.out.println("Cache map: " + cacheMap.toString() + "\n");
        short addressShort = (short) (address & 0xFFFF);
        short dataShort = (short) (data & 0xFFFF);
        memory.write(addressShort, dataShort);
        updateCache(addressShort, dataShort);
    }
    //3.   reset()
    public void reset() {
        cacheMap.clear();
    }
    //4.   getCacheMap()
    public LinkedHashMap<Short, Short> getCacheMap() {
        return cacheMap;
    }
    //5.   getCacheSize()
    public int getCacheSize() {
        return CACHE_SIZE;
    }
    //6. getMemory()
    public Memory getMemory() {
        return memory;
    }

    //Private methods:
    //1.   updateCache(address, data)
    private void updateCache(short address, short data) {
        if (isCacheFull()) {
            Short value = cacheMap.remove(cacheMap.keySet().iterator().next());
            System.out.println("Cache is full, removing line " + value);
        }
        cacheMap.put(address, data);
    }
    //2.   replaceCacheLine()
    //3.   isCacheFull()
    private boolean isCacheFull() {
        return cacheMap.size() >= CACHE_SIZE;
    }
    //4.   isCacheHit(address)
    private boolean isCacheHit(short address) {
        if (cacheMap.containsKey(address)) {
            System.out.println("Cache hit: " + address);
            return true;
        } else {
            System.out.println("Cache miss: " + address);
            return false;
        }
    }
}
