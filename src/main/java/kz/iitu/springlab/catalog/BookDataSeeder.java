package kz.iitu.springlab.catalog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1)
public class BookDataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;

    public BookDataSeeder(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            TagRepository tagRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (bookRepository.count() > 0) {
            return;
        }

        Author joshua = authorRepository.save(
                new Author("Joshua Bloch", "United States"));
        Author robert = authorRepository.save(
                new Author("Robert Martin", "United States"));
        Author craig = authorRepository.save(
                new Author("Craig Walls", "United States"));

        Tag java = tagRepository.save(new Tag("Java"));
        Tag design = tagRepository.save(new Tag("Design"));
        Tag spring = tagRepository.save(new Tag("Spring"));
        Tag bestPractices = tagRepository.save(new Tag("Best Practices"));

        addBook("Effective Java", joshua, 2018, Genre.SCIENCE,
                java, bestPractices);
        addBook("Java Puzzlers", joshua, 2005, Genre.OTHER,
                java, design);
        addBook("Java Concurrency in Practice", joshua, 2006, Genre.SCIENCE,
                java);

        addBook("Clean Code", robert, 2008, Genre.OTHER,
                design, bestPractices);
        addBook("The Clean Coder", robert, 2011, Genre.OTHER,
                bestPractices);
        addBook("Clean Architecture", robert, 2017, Genre.OTHER,
                design, bestPractices);

        addBook("Spring in Action", craig, 2022, Genre.SCIENCE,
                spring, java);
        addBook("Spring Boot Up and Running", craig, 2021, Genre.SCIENCE,
                spring);
    }

    private void addBook(
            String title,
            Author author,
            int year,
            Genre genre,
            Tag... tags) {
        Book book = new Book(null, title, author, year, genre, null);
        author.addBook(book);

        for (Tag tag : tags) {
            book.addTag(tag);
        }

        bookRepository.save(book);
    }
}