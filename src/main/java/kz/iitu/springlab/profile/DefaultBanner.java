package kz.iitu.springlab.profile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev & !prod")
public class DefaultBanner implements ProfileBanner {

    @Override
    public String message() {
        return "DEFAULT";
    }
}