package kz.iitu.springlab.web;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

public class CoverUrlValidator
        implements ConstraintValidator<ValidCoverUrl, String> {

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of(".jpg", ".jpeg", ".png", ".webp");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // URL необязательный: пустое значение пропускает отдельная проверка.
        if (value == null || value.isBlank()) {
            return true;
        }

        try {
            URI uri = new URI(value.trim());
            String scheme = uri.getScheme();

            if (scheme == null
                    || !(scheme.equalsIgnoreCase("http")
                    || scheme.equalsIgnoreCase("https"))
                    || uri.getHost() == null) {
                return false;
            }

            String path = uri.getPath();
            if (path == null) {
                return false;
            }

            String lowerPath = path.toLowerCase(Locale.ROOT);
            return ALLOWED_EXTENSIONS.stream().anyMatch(lowerPath::endsWith);
        } catch (URISyntaxException ex) {
            return false;
        }
    }
}