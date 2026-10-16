package kz.iitu.springlab.catalog;

import java.time.Instant;

public record Book(
        Long id,
        String title,
        String author,
        int year,
        Genre genre,
        Instant createdAt) {

    public Book {
        if (genre == null) {
            genre = Genre.OTHER;
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Book(Long id, String title, String author, int year) {
        this(id, title, author, year, Genre.OTHER, Instant.now());
    }
}