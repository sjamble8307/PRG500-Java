package com.sjamble.banking;

import com.sjamble.banking.controllers.BankingController;
import com.sjamble.banking.models.Account;

/** Entry point – runs the Banking System demo. */
public class Main {

    public static void main(String[] args) {
        BankingController controller = new BankingController();
        Account account = controller.manageAccount();

        System.out.println("\n--- Result ---");
        System.out.println("Account details:");
        System.out.println("  Account Number : " + account.getAccountNumber());
        System.out.println("  Owner Name     : " + account.getOwnerName());
        System.out.printf( "  Balance        : %.2f%n", account.getBalance());
        System.out.println("\n========================================");
    }
}
