package com.alibaba.ttl3.internal.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentMap;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Most source code of this file is copied from spring v5.3.24:
//
// https://github.com/spring-projects/spring-framework/blob/v5.3.24/spring-core/src/main/java/org/springframework/util/CollectionUtils.java
//
// with adoption:
// - adjust visible modifier and code format
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@ApiStatus.Internal
public final class Utils {
    /**
     * Default load factor for {@link HashMap}/{@link LinkedHashMap} variants.
     *
     * @see #newHashMap(int)
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * Instantiate a new {@link HashMap} with an initial capacity
     * that can accommodate the specified number of elements without
     * any immediate resize/rehash operations to be expected.
     * <p>This differs from the regular {@link HashMap} constructor
     * which takes an initial capacity relative to a load factor
     * but is effectively aligned with the JDK's
     * {@link java.util.concurrent.ConcurrentHashMap#ConcurrentHashMap(int)}.
     *
     * @param expectedSize the expected number of elements (with a corresponding
     *                     capacity to be derived so that no resize/rehash operations are needed)
     */
    public static <K, V> HashMap<K, V> newHashMap(int expectedSize) {
        return new HashMap<>(computeMapInitialCapacity(expectedSize), DEFAULT_LOAD_FACTOR);
    }

    /**
     * Instantiate a new {@code ConcurrentWeakHashMap} with an initial capacity
     * that can accommodate the specified number of elements without
     * any immediate resize/rehash operations to be expected.
     *
     * @param expectedSize the expected number of elements (with a corresponding
     *                     capacity to be derived so that no resize/rehash operations are needed)
     */
    public static <K, V> ConcurrentMap<K, V> newConcurrentWeakHashMap(int expectedSize) {
        return new ConcurrentReferenceHashMap<>(computeMapInitialCapacity(expectedSize), ConcurrentReferenceHashMap.ReferenceType.WEAK);
    }

    private static int computeMapInitialCapacity(int expectedSize) {
        return (int) Math.ceil(expectedSize / (double) DEFAULT_LOAD_FACTOR);
    }

    /**
     * @see <a href="https://scala-lang.org/api/3.1.3/scala/util/control/NonFatal$.html"><code>scala.util.control</code></a>
     * @see <a href="https://github.com/scala/scala/blob/v2.13.9/src/library/scala/util/control/NonFatal.scala#L35-L43">github.com/scala/scala/blob/v2.13.9/src/library/scala/util/control/NonFatal.scala</a>
     */
    public static void propagateIfFatal(@Nullable Throwable throwable) {
        if (throwable == null) return;

        if (throwable instanceof VirtualMachineError
                || throwable instanceof ThreadDeath
                || throwable instanceof InterruptedException
                || throwable instanceof LinkageError) {
            sneakyThrow(throwable);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
        throw (E) e;
    }
}
