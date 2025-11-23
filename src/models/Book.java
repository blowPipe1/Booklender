package models;

public class Book {
    private String isbn;
    private String title;
    private boolean issued;
    private String issuedToEmployeeId;

    public Book(String isbn, String title, boolean issued, String issuedToEmployeeId) {
        this.isbn = isbn;
        this.title = title;
        this.issued = issued;
        this.issuedToEmployeeId = issuedToEmployeeId;
    }

    public String getIsbn() { return isbn; }

    public String getTitle() { return title; }

    public boolean isIssued() { return issued; }

    public String getIssuedToEmployeeId() { return issuedToEmployeeId; }
}