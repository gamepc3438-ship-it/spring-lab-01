package kz.iitu.springlab.catalog;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll(String author) {
        List<Book> books = repository.findAll();

        if (author == null || author.isBlank()) {
            return books;
        }

        String search = author.toLowerCase(Locale.ROOT);
        return books.stream()
                .filter(book -> book.author().toLowerCase(Locale.ROOT).contains(search))
                .toList();
    }

    public Optional<Book> findById(long id) {
        return repository.findById(id);
    }

    public Book getById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(Book book) {
        boolean duplicate = repository.findAll().stream()
                .anyMatch(existing ->
                        existing.title().trim().equalsIgnoreCase(book.title().trim())
                                && existing.author().trim().equalsIgnoreCase(book.author().trim()));

        if (duplicate) {
            throw new DuplicateTitleException(book.title(), book.author());
        }

        Book newBook = new Book(
                null,
                book.title(),
                book.author(),
                book.year(),
                book.genre(),
                book.createdAt());

        return repository.save(newBook);
    }

    public Optional<Book> replace(long id, Book book) {
        Optional<Book> existing = repository.findById(id);

        if (existing.isEmpty()) {
            return Optional.empty();
        }

        Book replacement = new Book(
                id,
                book.title(),
                book.author(),
                book.year(),
                book.genre(),
                existing.get().createdAt());

        return Optional.of(repository.save(replacement));
    }

    public boolean delete(long id) {
        return repository.deleteById(id);
    }
}