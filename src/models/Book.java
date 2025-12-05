package models;

public class Book {
    private String isbn;
    private String title;
    private String description;
    private String imageUrl;
    private boolean issued;
    private String issuedToEmployeeId;

    public Book(String isbn, String title, String description, String imageUrl, boolean issued, String issuedToEmployeeId) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.issued = issued;
        this.issuedToEmployeeId = issuedToEmployeeId;
    }

    public String getIsbn() { return isbn; }

    public String getTitle() { return title; }

    public boolean isIssued() { return issued; }

    public String getIssuedToEmployeeId() { return issuedToEmployeeId; }

    public void setIssued(boolean issued) {this.issued = issued;}

    public void setIssuedToEmployeeId(String issuedToEmployeeId) {
        this.issuedToEmployeeId = issuedToEmployeeId;
    }
}