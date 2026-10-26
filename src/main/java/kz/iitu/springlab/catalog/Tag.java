package kz.iitu.springlab.catalog;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80, unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Book> books = new HashSet<>();

    protected Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Book> getBooks() {
        return Set.copyOf(books);
    }

    public void addBook(Book book) {
        if (!books.contains(book)) {
            books.add(book);
        }
        if (!book.getTags().contains(this)) {
            book.addTag(this);
        }
    }

    public void removeBook(Book book) {
        if (books.remove(book)) {
            book.removeTag(this);
        }
    }

    void addBookReference(Book book) {
        books.add(book);
    }

    void removeBookReference(Book book) {
        books.remove(book);
    }
}