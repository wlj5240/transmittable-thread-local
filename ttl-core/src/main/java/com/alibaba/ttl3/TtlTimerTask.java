package com.alibaba.ttl3;

import com.alibaba.crr.composite.Backup;
import com.alibaba.crr.composite.Capture;
import com.alibaba.ttl3.spi.TtlEnhanced;
import com.alibaba.ttl3.spi.TtlWrapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.jetbrains.annotations.Contract;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.alibaba.ttl3.transmitter.Transmitter.*;

/**
 * {@link TtlTimerTask} decorate {@link TimerTask} to get {@link TransmittableThreadLocal} value
 * and transmit it to the time of {@link TtlTimerTask} execution, needed when use {@link TtlTimerTask} to {@link TimerTask}.
 * <p>
 * Use factory method {@link #get(TimerTask)} to create instance.
 * <p>
 * <B><I>CAUTION:</I></B><br>
 * The {@link TtlTimerTask} make the method {@link TimerTask#scheduledExecutionTime()} in
 * the origin {@link TimerTask} lose effectiveness! Use {@code TTL Java Agent} instead.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see Timer
 * @see TimerTask
 * @see <a href="https://alibaba.github.io/Alibaba-Java-Coding-Guidelines/#concurrency">
 * Alibaba Java Coding Guidelines - Concurrency -
 * Item 10: [Mandatory] Run multiple TimeTask by using ScheduledExecutorService
 * rather than Timer because Timer will kill all running threads
 * in case of failing to catch exceptions.</a>
 * @deprecated Use {@link TtlRunnable}, {@link java.util.concurrent.ScheduledExecutorService}
 * instead of {@link Timer}, {@link TimerTask}.
 */
@Deprecated
public final class TtlTimerTask extends TimerTask implements TtlWrapper<TimerTask>, TtlEnhanced {
    private final AtomicReference<Capture> capturedRef;
    private final TimerTask timerTask;
    private final boolean releaseTtlValueReferenceAfterRun;

    private TtlTimerTask(@NonNull TimerTask timerTask, boolean releaseTtlValueReferenceAfterRun) {
        this.capturedRef = new AtomicReference<>(capture());
        this.timerTask = timerTask;
        this.releaseTtlValueReferenceAfterRun = releaseTtlValueReferenceAfterRun;
    }

    /**
     * wrap method {@link TimerTask#run()}.
     */
    @Override
    public void run() {
        final Capture captured = capturedRef.get();
        if (captured == null || releaseTtlValueReferenceAfterRun && !capturedRef.compareAndSet(captured, null)) {
            throw new IllegalStateException("TTL value reference is released after run!");
        }

        final Backup backup = replay(captured);
        try {
            timerTask.run();
        } finally {
            restore(backup);
        }
    }

    @Override
    public boolean cancel() {
        timerTask.cancel();
        return super.cancel();
    }

    /**
     * return original/unwrapped {@link TimerTask}.
     */
    @NonNull
    public TimerTask getTimerTask() {
        return unwrap();
    }

    /**
     * unwrap to original/unwrapped {@link TimerTask}.
     *
     * @see TtlWrappers#unwrap(Object)
     */
    @NonNull
    @Override
    public TimerTask unwrap() {
        return timerTask;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " - " + timerTask.toString();
    }

    /**
     * Factory method, wrap input {@link TimerTask} to {@link TtlTimerTask}.
     * <p>
     * This method is idempotent.
     *
     * @param timerTask input {@link TimerTask}
     * @return Wrapped {@link TimerTask}
     */
    @Nullable
    @Contract(value = "null -> null; !null -> !null", pure = true)
    public static TtlTimerTask get(@Nullable TimerTask timerTask) {
        return get(timerTask, false, false);
    }

    /**
     * Factory method, wrap input {@link TimerTask} to {@link TtlTimerTask}.
     * <p>
     * This method is idempotent.
     *
     * @param timerTask                        input {@link TimerTask}
     * @param releaseTtlValueReferenceAfterRun release TTL value reference after run, avoid memory leak even if {@link TtlTimerTask} is referred.
     * @return Wrapped {@link TimerTask}
     */
    @Nullable
    @Contract(value = "null, _ -> null; !null, _ -> !null", pure = true)
    public static TtlTimerTask get(@Nullable TimerTask timerTask, boolean releaseTtlValueReferenceAfterRun) {
        return get(timerTask, releaseTtlValueReferenceAfterRun, false);
    }

    /**
     * Factory method, wrap input {@link TimerTask} to {@link TtlTimerTask}.
     * <p>
     * This method is idempotent.
     *
     * @param timerTask                        input {@link TimerTask}
     * @param releaseTtlValueReferenceAfterRun release TTL value reference after run, avoid memory leak even if {@link TtlTimerTask} is referred.
     * @param idempotent                       is idempotent or not. {@code true} will cover up bugs! <b>DO NOT</b> set, only when you know why.
     * @return Wrapped {@link TimerTask}
     */
    @Nullable
    @Contract(value = "null, _, _ -> null; !null, _, _ -> !null", pure = true)
    public static TtlTimerTask get(@Nullable TimerTask timerTask, boolean releaseTtlValueReferenceAfterRun, boolean idempotent) {
        if (timerTask == null) return null;

        if (timerTask instanceof TtlEnhanced) {
            // avoid redundant decoration, and ensure idempotency
            if (idempotent) return (TtlTimerTask) timerTask;
            else throw new IllegalStateException("Already TtlTimerTask!");
        }
        return new TtlTimerTask(timerTask, releaseTtlValueReferenceAfterRun);
    }

    /**
     * Unwrap {@link TtlTimerTask} to the original/underneath one.
     * <p>
     * this method is {@code null}-safe, when input {@code TimerTask} parameter is {@code null}, return {@code null};
     * if input {@code TimerTask} parameter is not a {@link TtlTimerTask} just return input {@code TimerTask}.
     *
     * @see #get(TimerTask)
     */
    @Nullable
    @Contract(value = "null -> null; !null -> !null", pure = true)
    public static TimerTask unwrap(@Nullable TimerTask timerTask) {
        if (!(timerTask instanceof TtlTimerTask)) return timerTask;
        else return ((TtlTimerTask) timerTask).getTimerTask();
    }
}
