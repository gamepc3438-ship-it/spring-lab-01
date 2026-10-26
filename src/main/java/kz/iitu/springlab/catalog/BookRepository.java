package kz.iitu.springlab.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select b from Book b join fetch b.author")
    List<Book> findAllWithAuthor();

    @Query("select b from Book b join fetch b.author where b.id = :id")
    Optional<Book> findByIdWithAuthor(@Param("id") Long id);

    List<Book> findByGenre(Genre genre);

    List<Book> findByTitleContainingIgnoreCase(String fragment);

    List<Book> findByYearBetweenOrderByYearDesc(int from, int to);

    List<Book> findTop3ByGenreOrderByYearDesc(Genre genre);

    long countByAuthorName(String name);

    @Query("""
            select b from Book b
            where b.genre = :genre and b.year >= :year
            order by b.title
            """)
    List<Book> search(
            @Param("genre") Genre genre,
            @Param("year") int year);

    @Modifying(clearAutomatically = true)
    @Query("update Book b set b.archived = true where b.year < :year")
    int archiveOlderThan(@Param("year") int year);

    @Override
    @EntityGraph(attributePaths = "author")
    Page<Book> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "author")
    Page<Book> findByGenre(Genre genre, Pageable pageable);

    @EntityGraph(attributePaths = "author")
    Page<Book> findByAuthor_NameContainingIgnoreCase(
            String author,
            Pageable pageable);

    @EntityGraph(attributePaths = "author")
    Page<Book> findByGenreAndAuthor_NameContainingIgnoreCase(
            Genre genre,
            String author,
            Pageable pageable);

    @Query("select b from Book b where b.genre = :genre")
    @EntityGraph(attributePaths = "author")
    Slice<Book> findSliceByGenre(
            @Param("genre") Genre genre,
            Pageable pageable);

    @Query("""
            select b.title as title,
                   b.year as year,
                   a.name as authorName
            from Book b
            join b.author a
            order by b.title
            """)
    List<BookSummary> findBookSummaries();
}