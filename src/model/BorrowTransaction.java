package model;

import java.time.LocalDate;

// Represents a single borrowing transaction between a Member and a LibraryItem.
public class BorrowTransaction {
    private String transactionId;
    private Member member;
    private LibraryItem item;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean completed;

    public BorrowTransaction(String transactionId, Member member, LibraryItem item, int loanDays) {
        this.transactionId = transactionId;
        this.member = member;
        this.item = item;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(loanDays);
        this.completed = false;
    }

    // Executes the borrow action; returns true if the transaction succeeded.
    public boolean executeTransaction() {
        System.out.println("\n[Transaction " + transactionId + "] Borrow request:");
        boolean success = member.borrowItem(item);
        if (success) {
            System.out.println("  Due date: " + dueDate);
        }
        return success;
    }

    // Completes the transaction by having the member return the item.
    public boolean completeTransaction() {
        System.out.println("\n[Transaction " + transactionId + "] Return request:");
        boolean success = member.returnItem(item);
        if (success) {
            completed = true;
        }
        return success;
    }

    public void displayInfo() {
        System.out.println("Transaction ID: " + transactionId
                + " | Member: " + member.getName()
                + " | Item: " + item.getTitle()
                + " | Borrow Date: " + borrowDate
                + " | Due: " + dueDate
                + " | Completed: " + completed);
    }

    public String getTransactionId()    { return transactionId; }
    public Member getMember()           { return member; }
    public LibraryItem getItem()        { return item; }
    public LocalDate getBorrowDate()    { return borrowDate; }
    public LocalDate getDueDate()       { return dueDate; }
    public boolean isCompleted()        { return completed; }
}
