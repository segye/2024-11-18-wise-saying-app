package entity;

public class WiseSaying {
    private int id;
    private String message;
    private String author;

    public WiseSaying(int id, String message, String author) {
        this.id = id;
        this.message = message;
        this.author = author;
    }

    public WiseSaying() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return id + " / " + author + " / " + message;
    }
}
