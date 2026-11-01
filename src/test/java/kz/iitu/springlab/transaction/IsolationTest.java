package kz.iitu.springlab.transaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class IsolationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager entityManager;

    private Long aliceId;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();

        Account alice = accountRepository.save(
                new Account("Alice", new BigDecimal("1000.00"))
        );

        aliceId = alice.getId();
    }

    @Test
    void readCommittedSeesUpdateOnSecondRead() throws Exception {
        IsolationResult result =
                runScenario(TransactionDefinition.ISOLATION_READ_COMMITTED);

        assertMoney("1000.00", result.firstRead());
        assertMoney("1200.00", result.secondRead());

        System.out.println(
                "READ_COMMITTED: first=" + result.firstRead()
                        + ", second=" + result.secondRead()
        );
    }

    @Test
    void repeatableReadKeepsTheFirstSnapshot() throws Exception {
        IsolationResult result =
                runScenario(TransactionDefinition.ISOLATION_REPEATABLE_READ);

        assertMoney("1000.00", result.firstRead());
        assertMoney("1000.00", result.secondRead());

        System.out.println(
                "REPEATABLE_READ: first=" + result.firstRead()
                        + ", second=" + result.secondRead()
        );
    }

    private IsolationResult runScenario(int isolationLevel) throws Exception {
        CountDownLatch firstReadDone = new CountDownLatch(1);
        CountDownLatch allowSecondRead = new CountDownLatch(1);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        TransactionTemplate readerTransaction =
                new TransactionTemplate(transactionManager);
        readerTransaction.setIsolationLevel(isolationLevel);

        try {
            Future<IsolationResult> reader = executor.submit(() ->
                    readerTransaction.execute(status -> {
                        BigDecimal firstRead = findAlice().getBalance();
                        firstReadDone.countDown();

                        await(allowSecondRead);

                        entityManager.clear();
                        BigDecimal secondRead = findAlice().getBalance();

                        return new IsolationResult(firstRead, secondRead);
                    })
            );

            assertTrue(
                    firstReadDone.await(10, TimeUnit.SECONDS),
                    "The reader transaction did not perform its first read"
            );

            TransactionTemplate writerTransaction =
                    new TransactionTemplate(transactionManager);

            writerTransaction.executeWithoutResult(status -> {
                Account alice = findAlice();
                alice.credit(new BigDecimal("200.00"));
                accountRepository.save(alice);
                accountRepository.flush();
            });

            allowSecondRead.countDown();

            return reader.get(10, TimeUnit.SECONDS);
        } finally {
            allowSecondRead.countDown();
            executor.shutdownNow();
        }
    }

    private void await(CountDownLatch latch) {
        try {
            if (!latch.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException(
                        "The writer did not finish in time"
                );
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "The reader transaction was interrupted",
                    exception
            );
        }
    }

    private Account findAlice() {
        return accountRepository.findById(aliceId).orElseThrow();
    }

    private void assertMoney(String expected, BigDecimal actual) {
        assertEquals(
                0,
                new BigDecimal(expected).compareTo(actual),
                "Unexpected account balance"
        );
    }

    private record IsolationResult(
            BigDecimal firstRead,
            BigDecimal secondRead
    ) {
    }
}