package kz.iitu.springlab.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import kz.iitu.springlab.catalog.Genre;
import kz.iitu.springlab.web.NoDigits;

public record BookRequest(
        @Null(groups = OnCreate.class, message = "id must be omitted when creating a book")
        @NotNull(groups = OnUpdate.class, message = "id is required when updating a book")
        Long id,

        @NotBlank(message = "{book.title.required}")
        @Size(max = 120, message = "{book.title.size}")
        String title,

        @NotBlank(message = "{book.author.required}")
        @NoDigits
        String author,

        @NotNull(message = "{book.year.required}")
        @Min(value = 1450, message = "{book.year.range}")
        @Max(value = 2100, message = "{book.year.range}")
        Integer year,

        @NotNull(message = "{book.genre.required}")
        Genre genre) {

    public interface OnCreate extends Default {
    }

    public interface OnUpdate extends Default {
    }
}