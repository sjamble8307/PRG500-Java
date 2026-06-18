package com.sjamble.banking.services;

import com.sjamble.banking.models.Account;
import com.sjamble.banking.utils.Utility;

/**
 * Business logic layer. Extends Account so deposit() and withdraw() can call
 * the protected updateBalance() via 'this' (cross-package protected access rule).
 */
public class AccountService extends Account {

    protected static final Utility UTILITY = new Utility(); // protected for subclass override

    public AccountService(String accountNumber, String ownerName) {
        super(accountNumber, ownerName);
    }

    /** Non-static so BankingController can override and return its own type. */
    public Account createAccount(String ownerName) {
        AccountService account = new AccountService(UTILITY.getAccountNumber(), ownerName);
        System.out.println("[AccountService] Account created: " + account);
        return account;
    }

    /** Protected: adds funds after validating the amount. */
    protected void deposit(double amount) {
        if (amount <= 0) { System.out.println("[AccountService] Deposit amount must be positive."); return; }
        updateBalance(amount);
        System.out.printf("[AccountService] Deposited %.2f -> %s%n", amount, this);
    }

    /** Protected: deducts funds; rejects overdrafts. */
    protected void withdraw(double amount) {
        if (amount <= 0) { System.out.println("[AccountService] Withdrawal amount must be positive."); return; }
        if (getBalance() < amount) {
            System.out.printf("[AccountService] Insufficient funds (balance=%.2f, requested=%.2f).%n", getBalance(), amount);
            return;
        }
        updateBalance(-amount);
        System.out.printf("[AccountService] Withdrew %.2f -> %s%n", amount, this);
    }
}
