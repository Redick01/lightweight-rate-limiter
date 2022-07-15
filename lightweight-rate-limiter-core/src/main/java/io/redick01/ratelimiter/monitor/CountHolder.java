package io.redick01.ratelimiter.monitor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Redick01
 */
public class CountHolder {

    private final AtomicInteger COUNT = new AtomicInteger(0);

    public void add() {
        COUNT.addAndGet(1);
    }

    public Integer getCount() {
        return this.COUNT.get();
    }
}
