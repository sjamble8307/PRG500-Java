package model;

import java.util.ArrayList;
import java.util.List;

// Base class for all library users.
public abstract class User {

    private String userId;
    private String name;
    private String contactInfo;
    protected List<LibraryItem> borrowedItems = new ArrayList<>();

    public User(String userId, String name, String contactInfo) {
        this.userId = userId;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public void displayInfo() {
        System.out.println("User ID: " + userId + " | Name: " + name + " | Contact: " + contactInfo);
    }

    public String getUserId()       { return userId; }
    public String getName()         { return name; }
    public String getContactInfo()  { return contactInfo; }

    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public List<LibraryItem> getBorrowedItems() { return borrowedItems; }
}

// ─────────────────────────────────────────────────────────────────────────────

class Member extends User {
    private int maxBorrowLimit;
    public Member(String userId, String name, String contactInfo, int maxBorrowLimit) {
        super(userId, name, contactInfo);
        this.maxBorrowLimit = maxBorrowLimit;
    }

    // Member-specific action: borrow an item from the library catalogue.
    public boolean borrowItem(LibraryItem item) {
        if (item.isCheckedOut()) {
            System.out.println("  '" + item.getTitle() + "' is already checked out.");
            return false;
        }
        if (borrowedItems.size() >= maxBorrowLimit) {
            System.out.println("  " + getName() + " has reached the borrow limit of " + maxBorrowLimit + ".");
            return false;
        }
        item.setCheckedOut(true);
        borrowedItems.add(item);
        System.out.println("  " + getName() + " borrowed '" + item.getTitle() + "'.");
        return true;
    }
    // Return an item back to the library.
    public boolean returnItem(LibraryItem item) {
        if (borrowedItems.remove(item)) {
            item.setCheckedOut(false);
            System.out.println("  " + getName() + " returned '" + item.getTitle() + "'.");
            return true;
        }
        System.out.println("  " + getName() + " does not have '" + item.getTitle() + "' checked out.");
        return false;
    }
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("  [Member] Borrow Limit: " + maxBorrowLimit
                + " | Currently Borrowed: " + borrowedItems.size());
    }

    public int getMaxBorrowLimit() { return maxBorrowLimit; }
}

// ─────────────────────────────────────────────────────────────────────────────

class Librarian extends User {
    private String employeeId;
    // The librarian manages the master catalogue.
    private List<LibraryItem> catalogue;

    public Librarian(String userId, String name, String contactInfo,
                     String employeeId, List<LibraryItem> catalogue) {
        super(userId, name, contactInfo);
        this.employeeId = employeeId;
        this.catalogue = catalogue;
    }

    // Librarian-specific action: add a new item to the library catalogue.
    public void addNewItem(LibraryItem item) {
        catalogue.add(item);
        System.out.println("  Librarian " + getName() + " added '" + item.getTitle() + "' to the catalogue.");
    }
    // Remove an item from the catalogue (e.g., lost or damaged).
    public boolean removeItem(LibraryItem item) {
        if (catalogue.remove(item)) {
            System.out.println("  Librarian " + getName() + " removed '" + item.getTitle() + "' from the catalogue.");
            return true;
        }
        System.out.println("  Item '" + item.getTitle() + "' not found in catalogue.");
        return false;
    }
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("  [Librarian] Employee ID: " + employeeId
                + " | Catalogue Size: " + catalogue.size());
    }

    public String getEmployeeId() { return employeeId; }
}
