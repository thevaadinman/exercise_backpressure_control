package foo.v5archstudygroup.exercises.backpressure.server;

import foo.v5archstudygroup.exercises.backpressure.messages.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This is the class that simulates the data processing.
 */
@Service
public class DataProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);
    private static final int MAX_THREADS = 10;

    /**
     * You are not allowed to increase the number of threads, but otherwise you can do whatever changes you want
     * to the executor service.
     */
    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(64));
    // private final Deque<Messages.ProcessingRequest> requestQueue = new ArrayDeque<>();  // BRAINFART
    private final AtomicLong processed = new AtomicLong();

    public DataProcessor() {
        /*
        // BRAINFART
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (threadPool.getActiveCount() < MAX_THREADS && !requestQueue.isEmpty()) {
                        threadPool.submit(() -> doProcess(requestQueue.removeFirst()));
                    }

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        */
    }

    @PreDestroy
    void destroy() {
        // Always remember to clean up your threads
        threadPool.shutdown();
    }

    public void enqueue(Messages.ProcessingRequest request) {
        threadPool.submit(() -> doProcess(request));
    }

    /**
     * You are not allowed to change this method!
     */
    private void doProcess(Messages.ProcessingRequest request) {
        try {
            // Pretend we are doing something really heavy with this request. The more bytes we have, the longer it takes
            // to complete
            Thread.sleep(request.getPayload().size() / 8192);
            LOGGER.info("Done processing request {}. Processed: {}", request.getUuid(), processed.incrementAndGet());
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        System.gc();
    }
}
