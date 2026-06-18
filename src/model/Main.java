package model;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        List<LibraryItem> catalogue = LibraryLoader.loadItems("data/items.csv");
        List<User> users            = LibraryLoader.loadUsers("data/users.csv", catalogue);

        Member    alice     = (Member)    users.stream().filter(u -> u instanceof Member).toList().get(0);
        Member    bob       = (Member)    users.stream().filter(u -> u instanceof Member).toList().get(1);
        Librarian librarian = (Librarian) users.stream().filter(u -> u instanceof Librarian).findFirst().orElseThrow();

        // --- Catalogue ---
        System.out.println("===== Catalogue =====");
        catalogue.forEach(LibraryItem::displayInfo);

        // --- Borrow ---
        System.out.println("\n===== Borrowing =====");
        LibraryItem dune        = catalogue.stream().filter(i -> i.getItemId().equals("B001")).findFirst().orElseThrow();
        LibraryItem interstellar = catalogue.stream().filter(i -> i.getItemId().equals("D001")).findFirst().orElseThrow();
        LibraryItem natGeo      = catalogue.stream().filter(i -> i.getItemId().equals("M001")).findFirst().orElseThrow();

        new BorrowTransaction("T001", alice, dune,        14).executeTransaction();
        new BorrowTransaction("T002", bob,   dune,        14).executeTransaction(); // fails – already out
        new BorrowTransaction("T003", alice, interstellar, 7).executeTransaction();
        new BorrowTransaction("T004", bob,   natGeo,       7).executeTransaction();

        // --- Return ---
        System.out.println("\n===== Return =====");
        alice.returnItem(dune);

        // --- Librarian adds a new item ---
        System.out.println("\n===== Librarian Action =====");
        librarian.addNewItem(new Book("B006", "Brave New World", "Aldous Huxley", "Dystopia", 311));

        // --- Updated catalogue ---
        System.out.println("\n===== Updated Catalogue =====");
        catalogue.forEach(LibraryItem::displayInfo);
    }
}
