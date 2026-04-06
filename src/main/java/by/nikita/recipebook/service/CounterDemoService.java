package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.dto.RaceConditionDemoDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

    public synchronized RaceConditionDemoDTO demonstrateRaceCondition(int threads,
                                                                      int incrementsPerThread)
        throws InterruptedException {
        resetCounters();

        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                readyLatch.countDown();
                await(startLatch);

                for (int j = 0; j < incrementsPerThread; j++) {
                    incrementUnsafe();
                    incrementSynchronized();
                    incrementAtomic();
                }

                doneLatch.countDown();
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();

        executorService.shutdown();
        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        return new RaceConditionDemoDTO(
            threads,
            incrementsPerThread,
            (long) threads * incrementsPerThread,
            getUnsafeCounter(),
            getSynchronizedCounter(),
            getAtomicCounter()
        );
    }

    private void resetCounters() {
        unsafeCounter = 0;
        synchronizedCounter = 0;
        atomicCounter.set(0);
    }

    private void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread interrupted while waiting for race-condition demo", ex);
        }
    }
}
