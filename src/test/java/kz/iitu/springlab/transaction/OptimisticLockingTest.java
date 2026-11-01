package kz.iitu.springlab.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class OptimisticLockingTest {

    @Autowired
    private AccountRepository accountRepository;

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
    void staleUpdateIsRejected() {
        Account firstCopy = findAlice();
        Account staleCopy = findAlice();

        long initialVersion = firstCopy.getVersion();
        assertEquals(initialVersion, staleCopy.getVersion());

        firstCopy.credit(new BigDecimal("100.00"));
        Account savedFirstCopy = accountRepository.saveAndFlush(firstCopy);

        assertEquals(initialVersion + 1, savedFirstCopy.getVersion());

        staleCopy.credit(new BigDecimal("200.00"));

        assertThrows(
                OptimisticLockingFailureException.class,
                () -> accountRepository.saveAndFlush(staleCopy)
        );

        Account finalAccount = findAlice();

        assertMoney("1100.00", finalAccount.getBalance());
        assertEquals(initialVersion + 1, finalAccount.getVersion());

        System.out.println(
                "Optimistic locking conflict confirmed: stale version="
                        + initialVersion
                        + ", saved version=" + finalAccount.getVersion()
                        + ", balance=" + finalAccount.getBalance()
        );
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
}