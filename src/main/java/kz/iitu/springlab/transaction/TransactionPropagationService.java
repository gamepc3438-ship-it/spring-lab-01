package kz.iitu.springlab.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionPropagationService {

    private final AccountRepository accountRepository;
    private final AuditService auditService;

    public TransactionPropagationService(
            AccountRepository accountRepository,
            AuditService auditService
    ) {
        this.accountRepository = accountRepository;
        this.auditService = auditService;
    }

    @Transactional
    public void rollbackWithRequired(Long accountId) {
        debitTen(accountId);
        auditService.writeRequired("REQUIRED audit record");

        throw new IllegalStateException("Forced outer rollback");
    }

    @Transactional
    public void rollbackWithRequiresNew(Long accountId) {
        debitTen(accountId);
        auditService.writeRequiresNew("REQUIRES_NEW audit record");

        throw new IllegalStateException("Forced outer rollback");
    }

    private void debitTen(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Account not found: " + accountId
                ));

        account.debit(new BigDecimal("10.00"));
        accountRepository.save(account);
        accountRepository.flush();
    }
}