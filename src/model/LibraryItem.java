package model;

// Base class for all items in the library.
// Encapsulates shared attributes with private fields and protected/public access via getters/setters.
public abstract class LibraryItem {

    private String itemId;
    private String title;
    private String author;
    private boolean checkedOut;

    public LibraryItem(String itemId, String title, String author) {
        this.itemId = itemId;
        this.title = title;
        this.author = author;
        this.checkedOut = false;
    }

    // Polymorphic method — each subclass overrides this to include type-specific details.
    public void displayInfo() {
        System.out.println("ID: " + itemId + " | Title: " + title + " | Author/Creator: " + author
                + " | Available: " + !checkedOut);
    }

    // Factory used by the UI layer (in another package) to create a Book without
    // needing direct access to the package-private Book subclass.
    public static LibraryItem createBook(String itemId, String title, String author,
                                         String genre, int pageCount) {
        return new Book(itemId, title, author, genre, pageCount);
    }

    public static LibraryItem createMagazine(String itemId, String title, String publisher,
                                             String issueDate, int issueNumber) {
        return new Magazine(itemId, title, publisher, issueDate, issueNumber);
    }

    public static LibraryItem createDVD(String itemId, String title, String director,
                                        int durationMinutes, String rating) {
        return new DVD(itemId, title, director, durationMinutes, rating);
    }

    public static LibraryItem createAudioBook(String itemId, String title, String author,
                                              String genre, int pageCount,
                                              String narrator, double durationHours) {
        return new AudioBook(itemId, title, author, genre, pageCount, narrator, durationHours);
    }

    public String getItemId()       { return itemId; }
    public String getTitle()        { return title; }
    public String getAuthor()       { return author; }
    public boolean isCheckedOut()   { return checkedOut; }

    public void setCheckedOut(boolean checkedOut) { this.checkedOut = checkedOut; }

    /**
     * Serialises this item back to a CSV row matching the items.csv schema:
     * itemId, type, title, author, field1, field2, field3, field4, checkedOut
     */
    public abstract String[] toCsvRow();
}

// ─────────────────────────────────────────────────────────────────────────────

class Book extends LibraryItem {

    private String genre;
    private int pageCount;

    public Book(String itemId, String title, String author, String genre, int pageCount) {
        super(itemId, title, author);
        this.genre = genre;
        this.pageCount = pageCount;
    }

    // Overrides the base displayInfo() to append Book-specific details.
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("  [Book] Genre: " + genre + " | Pages: " + pageCount);
    }

    public String getGenre()    { return genre; }
    public int getPageCount()   { return pageCount; }

    @Override
    public String[] toCsvRow() {
        return new String[]{
            getItemId(), "Book", getTitle(), getAuthor(),
            genre, String.valueOf(pageCount), "", "",
            String.valueOf(isCheckedOut())
        };
    }
}

// ─────────────────────────────────────────────────────────────────────────────

class Magazine extends LibraryItem {

    private String issueDate;
    private int issueNumber;

    public Magazine(String itemId, String title, String author, String issueDate, int issueNumber) {
        super(itemId, title, author);
        this.issueDate = issueDate;
        this.issueNumber = issueNumber;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("  [Magazine] Issue #" + issueNumber + " | Date: " + issueDate);
    }

    public String getIssueDate()    { return issueDate; }
    public int getIssueNumber()     { return issueNumber; }

    @Override
    public String[] toCsvRow() {
        return new String[]{
            getItemId(), "Magazine", getTitle(), getAuthor(),
            issueDate, String.valueOf(issueNumber), "", "",
            String.valueOf(isCheckedOut())
        };
    }
}

// ─────────────────────────────────────────────────────────────────────────────

class DVD extends LibraryItem {

    private int durationMinutes;
    private String rating;

    public DVD(String itemId, String title, String director, int durationMinutes, String rating) {
        super(itemId, title, director);
        this.durationMinutes = durationMinutes;
        this.rating = rating;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("  [DVD] Duration: " + durationMinutes + " min | Rating: " + rating);
    }

    public int getDurationMinutes() { return durationMinutes; }
    public String getRating()       { return rating; }

    @Override
    public String[] toCsvRow() {
        return new String[]{
            getItemId(), "DVD", getTitle(), getAuthor(),
            String.valueOf(durationMinutes), rating, "", "",
            String.valueOf(isCheckedOut())
        };
    }
}

// ─────────────────────────────────────────────────────────────────────────────

// Future expansion example: an AudioBook is a specialised Book with extra playback info.
class AudioBook extends Book {

    private String narrator;
    private double durationHours;

    public AudioBook(String itemId, String title, String author, String genre,
                     int pageCount, String narrator, double durationHours) {
        super(itemId, title, author, genre, pageCount);
        this.narrator = narrator;
        this.durationHours = durationHours;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("  [AudioBook] Narrator: " + narrator + " | Duration: " + durationHours + "h");
    }

    public String getNarrator()      { return narrator; }
    public double getDurationHours() { return durationHours; }

    @Override
    public String[] toCsvRow() {
        return new String[]{
            getItemId(), "AudioBook", getTitle(), getAuthor(),
            getGenre(), String.valueOf(getPageCount()),
            narrator, String.valueOf(durationHours),
            String.valueOf(isCheckedOut())
        };
    }
}
