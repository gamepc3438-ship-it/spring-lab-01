package kz.iitu.springlab.profile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProdBanner implements ProfileBanner {

    @Override
    public String message() {
        return "PRODUCTION";
    }
}