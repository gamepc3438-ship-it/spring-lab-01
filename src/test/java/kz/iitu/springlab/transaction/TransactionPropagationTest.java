package kz.iitu.springlab.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TransactionPropagationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private TransactionPropagationService propagationService;

    private Long aliceId;

    @BeforeEach
    void setUp() {
        auditRepository.deleteAll();
        accountRepository.deleteAll();

        Account alice = accountRepository.save(
                new Account("Alice", new BigDecimal("1000.00"))
        );

        aliceId = alice.getId();
    }

    @Test
    void requiredAuditRollsBackWithOuterTransaction() {
        assertThrows(
                IllegalStateException.class,
                () -> propagationService.rollbackWithRequired(aliceId)
        );

        assertEquals(0, auditRepository.count());
        assertMoney("1000.00", findAlice().getBalance());

        System.out.println(
                "REQUIRED: audit rows=" + auditRepository.count()
                        + ", Alice balance=" + findAlice().getBalance()
        );
    }

    @Test
    void requiresNewAuditSurvivesOuterRollback() {
        assertThrows(
                IllegalStateException.class,
                () -> propagationService.rollbackWithRequiresNew(aliceId)
        );

        assertEquals(1, auditRepository.count());
        assertEquals(
                "REQUIRES_NEW audit record",
                auditRepository.findAll().get(0).getMessage()
        );
        assertMoney("1000.00", findAlice().getBalance());

        System.out.println(
                "REQUIRES_NEW: audit rows=" + auditRepository.count()
                        + ", Alice balance=" + findAlice().getBalance()
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