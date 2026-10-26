package kz.iitu.springlab.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository repository, AuthorRepository authorRepository) {
        this.repository = repository;
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public Page<Book> find(Genre genre, String author, Pageable pageable) {
        boolean hasAuthor = author != null && !author.isBlank();

        if (genre != null && hasAuthor) {
            return repository.findByGenreAndAuthor_NameContainingIgnoreCase(
                    genre, author.trim(), pageable);
        }

        if (genre != null) {
            return repository.findByGenre(genre, pageable);
        }

        if (hasAuthor) {
            return repository.findByAuthor_NameContainingIgnoreCase(
                    author.trim(), pageable);
        }

        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Book> findAll(String author) {
        List<Book> books = repository.findAllWithAuthor();

        if (author == null || author.isBlank()) {
            return books;
        }

        String search = author.toLowerCase(Locale.ROOT);
        return books.stream()
                .filter(book -> book.author().toLowerCase(Locale.ROOT).contains(search))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Book> findById(long id) {
        return repository.findByIdWithAuthor(id);
    }

    @Transactional(readOnly = true)
    public Book getById(long id) {
        return repository.findByIdWithAuthor(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional
    public Book create(Book book) {
        boolean duplicate = repository.findAllWithAuthor().stream()
                .anyMatch(existing ->
                        existing.title().trim().equalsIgnoreCase(book.title().trim())
                                && existing.author().trim().equalsIgnoreCase(book.author().trim()));

        if (duplicate) {
            throw new DuplicateTitleException(book.title(), book.author());
        }

        Author author = findOrCreateAuthor(book.author());

        Book newBook = new Book(
                null,
                book.title(),
                author,
                book.year(),
                book.genre(),
                null);

        author.addBook(newBook);
        return repository.save(newBook);
    }

    @Transactional
    public Optional<Book> replace(long id, Book book) {
        Optional<Book> existing = repository.findByIdWithAuthor(id);

        if (existing.isEmpty()) {
            return Optional.empty();
        }

        Book current = existing.get();
        current.setTitle(book.title());
        current.setAuthor(findOrCreateAuthor(book.author()));
        current.setYear(book.year());
        current.setGenre(book.genre());

        return Optional.of(repository.save(current));
    }

    @Transactional
    public boolean delete(long id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }

    private Author findOrCreateAuthor(String name) {
        String cleanName = name.trim();

        return authorRepository.findByNameIgnoreCase(cleanName)
                .orElseGet(() -> authorRepository.save(new Author(cleanName, "Unknown")));
    }
}