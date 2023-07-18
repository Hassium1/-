public class Book {
    private int id;
    private String name;
    private String author;
    private String publisher;
    private Boolean borrowed;

    public Book() {
        // 空构造函数，用于 Jackson 数据绑定库
    }

    public Book(int id, String name, String author, String publisher,Boolean borrowed) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.borrowed = borrowed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(Boolean borrowed) {
        this.borrowed = borrowed;
    }
}
