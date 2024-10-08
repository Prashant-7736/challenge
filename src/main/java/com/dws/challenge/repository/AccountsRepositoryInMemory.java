package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.AccountTransfer;
import com.dws.challenge.exception.AccountTransferException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

    @Override
    public void accountTransfer(AccountTransfer accountTransfer) throws AccountTransferException {
        Account accountFrom = accounts.get(accountTransfer.getAccountFrom());
        Account accountTo = accounts.get(accountTransfer.getAccountTo());
        BigDecimal amount = accountTransfer.getAmount();
        // validate both the accounts
        validateAccountId(accountTransfer.getAccountFrom(), accountFrom);
        validateAccountId(accountTransfer.getAccountFrom(), accountTo);
        transferAmount(accountTo, accountFrom, amount);

    }

    private synchronized void transferAmount(Account accountTo, Account accountFrom, BigDecimal amount) {
        BigDecimal creditedAmount = accountTo.getBalance().add(amount);
        BigDecimal debitedAmount = accountFrom.getBalance().subtract(amount);
        if (debitedAmount.signum()==-1){
            throw new AccountTransferException(
                    "Insufficient balance to proceed with transaction");
        }
        accountTo.setBalance(creditedAmount);
        accountFrom.setBalance(debitedAmount);
    }

    private static void validateAccountId(String accountId, Account account) {
        if (account == null) {
            throw new AccountTransferException(
                    "Invalid Account id " + accountId + ",Hence unable to proceed with transaction");
        }
    }

}
