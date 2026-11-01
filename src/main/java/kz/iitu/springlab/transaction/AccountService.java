package kz.iitu.springlab.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Account from = findAccount(fromId);
        Account to = findAccount(toId);

        from.debit(amount);
        to.credit(amount);

        accountRepository.save(from);
        accountRepository.save(to);
    }

    @Transactional
    public void transferWithForcedRollback(
            Long fromId,
            Long toId,
            BigDecimal amount
    ) {
        Account from = findAccount(fromId);
        findAccount(toId);

        from.debit(amount);
        accountRepository.save(from);
        accountRepository.flush();

        throw new IllegalStateException("Forced rollback after debit");
    }

    private Account findAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Account not found: " + id
                ));
    }
}