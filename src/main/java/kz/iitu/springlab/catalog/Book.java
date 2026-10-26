package kz.iitu.springlab.catalog;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @ManyToMany
    @JoinTable(
            name = "book_tag",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "publication_year", nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Genre genre = Genre.OTHER;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean archived;

    @Version
    private long version;

    protected Book() {
    }

    public Book(Long id, String title, Author author, int year, Genre genre, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre == null ? Genre.OTHER : genre;
        this.createdAt = createdAt;
    }

    public Book(Long id, String title, String authorName, int year, Genre genre, Instant createdAt) {
        this(id, title, new Author(authorName, "Unknown"), year, genre, createdAt);
    }

    public Book(Long id, String title, String authorName, int year) {
        this(id, title, authorName, year, Genre.OTHER, null);
    }

    @PrePersist
    private void setCreatedAtBeforeInsert() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String author() {
        return author == null ? null : author.getName();
    }

    public int year() {
        return year;
    }

    public Genre genre() {
        return genre;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        if (this.author == author) {
            return;
        }

        Author previous = this.author;
        this.author = author;

        if (previous != null) {
            previous.removeBookReference(this);
        }
        if (author != null) {
            author.addBookReference(this);
        }
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        if (tags.add(tag)) {
            tag.addBookReference(this);
        }
    }

    public void removeTag(Tag tag) {
        if (tags.remove(tag)) {
            tag.removeBookReference(this);
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public long getVersion() {
        return version;
    }
}