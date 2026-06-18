package com.sjamble.banking.models;

/** Bank account with private fields, public getters, and a protected balance mutator. */
public class Account {

    private final String accountNumber;
    private double balance;
    private final String ownerName;

    public Account(String accountNumber, String ownerName) {
        this.accountNumber = accountNumber;
        this.ownerName     = ownerName;
        this.balance       = 0.0;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName()     { return ownerName; }
    public double getBalance()       { return balance; }

    /** Protected: only subclasses may adjust the balance. */
    protected void updateBalance(double amount) {
        this.balance += amount;
    }

    @Override
    public String toString() {
        return String.format("Account{number='%s', owner='%s', balance=%.2f}",
                accountNumber, ownerName, balance);
    }
}
