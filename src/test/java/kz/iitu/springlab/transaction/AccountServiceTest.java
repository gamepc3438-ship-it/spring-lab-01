package kz.iitu.springlab.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    private Long aliceId;
    private Long bobId;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();

        Account alice = accountRepository.save(
                new Account("Alice", new BigDecimal("1000.00"))
        );
        Account bob = accountRepository.save(
                new Account("Bob", new BigDecimal("500.00"))
        );

        aliceId = alice.getId();
        bobId = bob.getId();
    }

    @Test
    void successfulTransferChangesBothBalances() {
        accountService.transfer(
                aliceId,
                bobId,
                new BigDecimal("100.00")
        );

        BigDecimal aliceBalance = findAccount(aliceId).getBalance();
        BigDecimal bobBalance = findAccount(bobId).getBalance();

        assertMoney("900.00", aliceBalance);
        assertMoney("600.00", bobBalance);

        System.out.println(
                "Successful transfer: Alice=" + aliceBalance
                        + ", Bob=" + bobBalance
        );
    }

    @Test
    void insufficientFundsLeaveBothBalancesUnchanged() {
        assertThrows(
                IllegalStateException.class,
                () -> accountService.transfer(
                        aliceId,
                        bobId,
                        new BigDecimal("1500.00")
                )
        );

        BigDecimal aliceBalance = findAccount(aliceId).getBalance();
        BigDecimal bobBalance = findAccount(bobId).getBalance();

        assertMoney("1000.00", aliceBalance);
        assertMoney("500.00", bobBalance);

        System.out.println(
                "Insufficient funds: balances unchanged; Alice="
                        + aliceBalance + ", Bob=" + bobBalance
        );
    }

    @Test
    void uncheckedExceptionRollsBackDebit() {
        assertThrows(
                IllegalStateException.class,
                () -> accountService.transferWithForcedRollback(
                        aliceId,
                        bobId,
                        new BigDecimal("100.00")
                )
        );

        BigDecimal aliceBalance = findAccount(aliceId).getBalance();
        BigDecimal bobBalance = findAccount(bobId).getBalance();

        assertMoney("1000.00", aliceBalance);
        assertMoney("500.00", bobBalance);

        System.out.println(
                "Rollback verified: balances unchanged; Alice="
                        + aliceBalance + ", Bob=" + bobBalance
        );
    }

    private Account findAccount(Long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    private void assertMoney(String expected, BigDecimal actual) {
        assertEquals(
                0,
                new BigDecimal(expected).compareTo(actual),
                "Unexpected account balance"
        );
    }
}