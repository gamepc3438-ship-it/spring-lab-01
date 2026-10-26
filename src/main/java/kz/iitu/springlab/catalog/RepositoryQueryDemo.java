package kz.iitu.springlab.catalog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(2)
public class RepositoryQueryDemo implements CommandLineRunner {

    private final BookRepository bookRepository;

    public RepositoryQueryDemo(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (bookRepository.count() == 0) {
            return;
        }

        System.out.println("=== DERIVED QUERIES ===");
        System.out.println("findByGenre: "
                + bookRepository.findByGenre(Genre.SCIENCE).size());

        System.out.println("findByTitleContainingIgnoreCase: "
                + bookRepository.findByTitleContainingIgnoreCase("Java").size());

        System.out.println("findByYearBetweenOrderByYearDesc: "
                + bookRepository.findByYearBetweenOrderByYearDesc(2000, 2025).size());

        System.out.println("findTop3ByGenreOrderByYearDesc: "
                + bookRepository.findTop3ByGenreOrderByYearDesc(Genre.SCIENCE).size());

        System.out.println("countByAuthorName: "
                + bookRepository.countByAuthorName("Joshua Bloch"));

        System.out.println("=== JPQL QUERIES ===");
        System.out.println("search: "
                + bookRepository.search(Genre.SCIENCE, 2000).size());

        System.out.println("archiveOlderThan: "
                + bookRepository.archiveOlderThan(2010));

        System.out.println("=== N+1 BEFORE: findAll ===");
        List<Book> booksWithoutFetch = bookRepository.findAll();
        booksWithoutFetch.forEach(book ->
                System.out.println(book.title() + " — " + book.getAuthor().getName()));

        System.out.println("=== N+1 AFTER: findAllWithAuthor ===");
        List<Book> booksWithFetch = bookRepository.findAllWithAuthor();
        booksWithFetch.forEach(book ->
                System.out.println(book.title() + " — " + book.getAuthor().getName()));

        Pageable pageable = PageRequest.of(0, 3, Sort.by("title"));

        System.out.println("=== PAGE: result query and count query ===");
        Page<Book> page = bookRepository.findByGenre(Genre.SCIENCE, pageable);
        System.out.println("Page elements: " + page.getNumberOfElements());
        System.out.println("Page total elements: " + page.getTotalElements());
        System.out.println("Page has next: " + page.hasNext());

        System.out.println("=== SLICE: result query only ===");
        Slice<Book> slice = bookRepository.findSliceByGenre(Genre.SCIENCE, pageable);
        System.out.println("Slice elements: " + slice.getNumberOfElements());
        System.out.println("Slice has next: " + slice.hasNext());

        System.out.println("=== ENTITY QUERY: all selected entity fields ===");
        List<Book> entityBooks = bookRepository.findAllWithAuthor();
        entityBooks.forEach(book ->
                System.out.println(book.title() + ", "
                        + book.year() + ", " + book.getAuthor().getName()));

        System.out.println("=== PROJECTION QUERY: three selected fields ===");
        List<BookSummary> summaries = bookRepository.findBookSummaries();
        summaries.forEach(summary ->
                System.out.println(summary.getTitle() + ", "
                        + summary.getYear() + ", " + summary.getAuthorName()));
    }
}