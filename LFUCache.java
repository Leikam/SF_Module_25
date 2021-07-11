package module_25.LFUCacheTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import module_25.ICache;

/* Задание 25.5.2 – Least Frequently Used cache */
public class LFUCache implements ICache {

    private final int[] cache;
    private final HashMap<Integer, CacheSeq> callRegistry;
    private final int size;
    private int realSize;

    public LFUCache(int pageSize, int size) {
        this.cache = new int[pageSize];
        Arrays.fill(this.cache, -1);
        this.callRegistry = new HashMap<>();
        this.realSize = 0;
        this.size = size;
    }

    @Override
    public String toString() {
        return String.format("LFUCache (%d/%d): %s\n%s", realSize, size, Arrays.toString(cache), new TreeSet<>(callRegistry.values()));
    }

    public void get(int page) {
        if (contains(page)) {
            incrementCallsCounter(page);
            /* return page */
        } else {
            if (realSize == size) {
                final List<CacheSeq> sortedRegistry = new TreeSet<>(this.callRegistry.values())
                    .stream()
                    .limit(size)
                    .peek(cache -> {
                        if (!this.contains(cache.id)) {
                            logNegative("\nout of cache: " + cache + "\n");
                        }
                    })
                    .collect(Collectors.toList());

                CacheSeq least = sortedRegistry.get(size - 1);

                incrementCallsCounter(page);
                if (callRegistry.get(page).compareTo(least) <= 0) {
                    this.cache[page] = page;
                    System.out.println("out = " + least.id);
                    System.out.println("in = " + page);
                }
            } else {
                incrementCallsCounter(page);
                cache[page] = page;
                realSize++;
            }
        }


        /* return page */

        // debug
        final List<Integer> sortedHead = new ArrayList<>(this.callRegistry.values())
            .stream()
            .sorted()
            .limit(size)
            .map(cacheSeq -> cacheSeq.id)
            .collect(Collectors.toList());
        System.out.println("sorted register = " + sortedHead);

        final List<Integer> sortedTail = new ArrayList<>(this.callRegistry.values())
            .stream()
            .sorted()
            .skip(size)
            .map(cacheSeq -> cacheSeq.id)
            .collect(Collectors.toList());
        System.out.println("sorted tail = " + sortedTail);

//        final ArrayList<Integer> debugCache = new ArrayList<>();
//        for (int i = 0; i < this.cache.length; i++) {
//            if (contains(i)) {
//                debugCache.add(i);
//            }
//        }
//        System.out.println("cache = " + debugCache);
        /// debug
    }

    public boolean contains(int page) {
        return this.cache[page] != -1;
    }

    private void incrementCallsCounter(int page) {
        if (callRegistry.containsKey(page)) {
            final CacheSeq cacheSeq = callRegistry.get(page);
            cacheSeq.calls++;
            cacheSeq.modified = System.nanoTime();
        } else {
            callRegistry.put(page, new CacheSeq(page, 1));
        }
    }

    public static class CacheSeq implements Comparable<CacheSeq> {
        public int id;
        public long modified;
        public int calls;

        public CacheSeq(int id, int calls) {
            this.id = id;
            this.calls = calls;
            this.modified = System.nanoTime();
        }

        @Override
        public int compareTo(CacheSeq o) {
            final int res = o.calls - this.calls;
            if (res == 0) {
                return (int) (o.modified - this.modified);
            }
            return res;
        }

        @Override
        public String toString() {
//            return "#"+ id +"[" + calls + ", " + modified + "]";
            return "#"+ id +"[" + calls + "]";
        }
    }

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public static void logPositive(String info, Object... args) {
        System.out.printf((ANSI_BLUE + info + ANSI_RESET), args);
    }

    public static void logNegative(String error, Object... args) {
        System.out.printf((ANSI_RED + error + ANSI_RESET), args);
    }

}







