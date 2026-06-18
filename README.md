# Community Library — Multi-Scene JavaFX Application

A small JavaFX desktop application built on top of an existing library domain model.
It demonstrates **multiple scenes, scene switching, and user interactions**.

## Scenes

The application has three scenes and navigates between them like this:

```
Login  ->  Librarian Dashboard  <->  Add Book
```

1. **Login** (`LoginScene`)
   A sign-in form with a username field, a password field, and a **Log in** button.
   Credentials are checked against the librarian in `data/users.csv`:
   - username = the librarian's name (`shantanu`)
   - password = the librarian's ID (`password`)

   A hint is shown on screen. On success it opens the Dashboard.

2. **Librarian Dashboard** (`DashboardScene`)
   - Shows the full catalogue in a **table** (ID, Title, Author, Type, Available).
   - **Update availability** — select a row and click to toggle it between
     available / checked out; the table and status line update live.
   - **Add book** — navigates to the Add Book scene.
   - **Log out** — returns to the Login scene.

3. **Add Book** (`AddBookScene`)
   A form (ID, Title, Author, Genre, Pages) with:
   - **Add** — validates the input and adds a new `Book` to the catalogue
     (via the existing `Librarian.addNewItem(...)`); the book appears on the
     Dashboard table immediately. You can add several in a row.
   - **Back to dashboard** — returns to the Dashboard.

## Project structure

```
PRG500/
├── pom.xml                     Maven build (JavaFX + OpenCSV)
├── data/
│   ├── items.csv               Catalogue data
│   └── users.csv               Users (members + librarian)
└── src/
    ├── LibraryApp.java         JavaFX entry point: kinda like managers, switches scenes
    ├── LoginScene.java         Scene 1 — login form
    ├── DashboardScene.java     Scene 2 — catalogue table + librarian actions
    ├── AddBookScene.java       Scene 3 — add-a-book form
    │
    ├── LibraryItem.java        Domain: LibraryItem + Book/Magazine/DVD/AudioBook
    ├── User.java               Domain: User + Member/Librarian
    ├── BorrowTransaction.java  Domain: borrow/return transaction
    ├── LibraryLoader.java      Loads the CSV files into the domain objects
    └── Main.java               Original (kept for reference)
```

The JavaFX layer (the four scene/app classes) is built **on top of the unchanged
domain model**. `LibraryApp` loads the data once with `LibraryLoader`, keeps the
catalogue in an `ObservableList` so the table refreshes automatically, and hands
that data to each scene.

## Notes

- JavaFX `21.0.4` and Java `21` are used (see `pom.xml`).
- The catalogue and users are read from the CSV files in `data/` at start-up,
  so edits there are picked up on the next launch.
