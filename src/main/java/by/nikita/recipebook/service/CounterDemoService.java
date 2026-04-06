package by.nikita.recipebook.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class CounterDemoService {

    private long unsafeCounter = 0;
    private long synchronizedCounter = 0;
    private final AtomicLong atomicCounter = new AtomicLong(0);

    public void incrementUnsafe() {
        long currentValue = unsafeCounter;
        Thread.yield();
        unsafeCounter = currentValue + 1;
    }

    public synchronized void incrementSynchronized() {
        synchronizedCounter++;
    }

    public void incrementAtomic() {
        atomicCounter.incrementAndGet();
    }

    public long getUnsafeCounter() {
        return unsafeCounter;
    }

    public long getSynchronizedCounter() {
        return synchronizedCounter;
    }

    public long getAtomicCounter() {
        return atomicCounter.get();
    }
}
