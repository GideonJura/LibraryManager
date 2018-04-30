public class Book {
    private String bookName;
    private String bookISBN;
    private String bookPublisher;
    private boolean borrowedOut;
    private User owner;

    public String getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(String bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public Book(String bookName, String bookISBN, String bookPublisher){
        this.bookISBN = bookISBN;
        this.bookName = bookName;
        this.bookPublisher = bookPublisher;

        this.owner = null;
        this.borrowedOut = false;
    }

    public boolean borrowBook(User borrower){
        if(this.borrowedOut){
            return false;
        } else{
            owner = borrower;
            borrowedOut = true;
            return true;
        }
    }

    public void returnBook(){
        owner = null;
        borrowedOut = false;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public User getOwner() {

        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isBorrowedOut() {
        return borrowedOut;
    }

    public void setBorrowedOut(boolean borrowedOut) {
        this.borrowedOut = borrowedOut;
    }
}
