package kz.iitu.springlab.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookNotFoundException;
import kz.iitu.springlab.catalog.BookService;
import kz.iitu.springlab.web.dto.BookMapper;
import kz.iitu.springlab.web.dto.BookRequest;
import kz.iitu.springlab.web.dto.BookResponse;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Books", description = "Book catalogue API")
@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService service;
    private final BookMapper mapper;

    public BookRestController(BookService service, BookMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "List books")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books returned")
    })
    @GetMapping
    public List<BookResponse> list(
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "10") int limit) {

        List<Book> books = service.findAll(author).stream()
                .limit(limit)
                .toList();

        return mapper.toResponseList(books);
    }

    @Operation(summary = "Get a book by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "No book with this ID",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> find(
            @Parameter(description = "Identifier of the book", example = "42")
            @PathVariable long id) {

        Book book = service.getById(id);
        return ResponseEntity.ok(mapper.toResponse(book));
    }

    @Operation(summary = "Create a book")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Book created",
                    headers = @Header(
                            name = "Location",
                            description = "URI of the created book",
                            schema = @Schema(type = "string")),
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request validation failed",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "A book with this title and author already exists",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<BookResponse> create(
            @RequestBody @Validated(BookRequest.OnCreate.class) BookRequest request) {

        Book saved = service.create(mapper.toBook(request));

        return ResponseEntity
                .created(URI.create("/api/books/" + saved.id()))
                .body(mapper.toResponse(saved));
    }

    @Operation(summary = "Update a book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request validation failed",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "No book with this ID",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> replace(
            @Parameter(description = "Identifier of the book", example = "42")
            @PathVariable long id,
            @RequestBody @Validated(BookRequest.OnUpdate.class) BookRequest request) {

        if (request.id().longValue() != id) {
            return ResponseEntity.badRequest().build();
        }

        Book updated = service.replace(id, mapper.toBook(request))
                .orElseThrow(() -> new BookNotFoundException(id));

        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @Operation(summary = "Delete a book")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "No book with this ID",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifier of the book", example = "42")
            @PathVariable long id) {

        if (!service.delete(id)) {
            throw new BookNotFoundException(id);
        }

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a book citation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book citation returned"),
            @ApiResponse(
                    responseCode = "404",
                    description = "No book with this ID",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping(value = "/{id}/citation", produces = "text/plain")
    public ResponseEntity<String> citation(
            @Parameter(description = "Identifier of the book", example = "42")
            @PathVariable long id) {

        Book book = service.getById(id);

        String text = book.author() + ". "
                + book.title() + ". "
                + book.year() + ".";

        return ResponseEntity.ok(text);
    }
}