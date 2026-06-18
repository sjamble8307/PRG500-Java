package com.sjamble.banking.controllers;

import com.sjamble.banking.models.Account;
import com.sjamble.banking.services.AccountService;

/** Public API layer. Overrides createAccount() to return a BankingController so
 *  protected deposit() and withdraw() can be called on it legally. */
public class BankingController extends AccountService {

    public BankingController(String accountNumber, String ownerName) { super(accountNumber, ownerName); }
    public BankingController() { super("N/A", ""); }

    @Override
    public Account createAccount(String ownerName) {
        BankingController account = new BankingController(UTILITY.getAccountNumber(), ownerName);
        System.out.println("[AccountService] Account created: " + account);
        return account;
    }

    /** Demonstrates account creation, deposits, and withdrawals. */
    public Account manageAccount() {
        System.out.println("========================================");
        System.out.println("  Banking System Demo");
        System.out.println("========================================\n");

        BankingController account = (BankingController) createAccount("Shantanu Jamble");
        account.deposit(1000.00);
        account.deposit(500.00);
        account.withdraw(300.00);
        account.withdraw(5000.00); // overdraft – rejected

        System.out.println("\n[BankingController] Final account state: " + account);
        return account;
    }
}
