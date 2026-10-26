package kz.iitu.springlab.catalog;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String name;

    @Column(nullable = false, length = 80)
    private String country;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();

    protected Author() {
    }

    public Author(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public List<Book> getBooks() {
        return List.copyOf(books);
    }

    public void addBook(Book book) {
        if (!books.contains(book)) {
            books.add(book);
        }
        if (book.getAuthor() != this) {
            book.setAuthor(this);
        }
    }

    public void removeBook(Book book) {
        if (books.remove(book) && book.getAuthor() == this) {
            book.setAuthor(null);
        }
    }

    void addBookReference(Book book) {
        if (!books.contains(book)) {
            books.add(book);
        }
    }

    void removeBookReference(Book book) {
        books.remove(book);
    }
}