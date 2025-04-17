package kr.hhplus.be.ecommerce.support;

import kr.hhplus.be.ecommerce.IntegrationTestSupport;
import kr.hhplus.be.ecommerce.support.database.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public abstract class ConcurrencyTestSupport extends IntegrationTestSupport {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void tearDown() {
        databaseCleaner.clean();
    }

    protected void executeConcurrency(int threadCount, Runnable runnable) {
        executeConcurrency(IntStream.range(0, threadCount)
            .mapToObj(i -> runnable)
            .toList());
    }

    protected void executeConcurrency(List<Runnable> runnables) {
        ExecutorService executorService = Executors.newFixedThreadPool(runnables.size());

        List<CompletableFuture<Void>> futures = runnables.stream()
            .map(runnable -> CompletableFuture.runAsync(() -> {
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executorService))
            .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
    }
}
