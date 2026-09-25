package kz.iitu.springlab.web;

import kz.iitu.springlab.config.AppProperties;
import kz.iitu.springlab.profile.ProfileBanner;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/lab3")
public class Lab3Controller {

    private final AppProperties properties;
    private final ProfileBanner banner;
    private final Environment environment;

    public Lab3Controller(
            AppProperties properties,
            ProfileBanner banner,
            Environment environment
    ) {
        this.properties = properties;
        this.banner = banner;
        this.environment = environment;
    }

    @GetMapping("/config")
    public Map<String, Object> config() {

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("owner", properties.owner());
        result.put("group", properties.group());
        result.put("mailRetryCount", properties.mail().retryCount());
        result.put("mailEnabled", properties.mail().enabled());
        result.put("serverPort", environment.getProperty("server.port"));
        result.put("activeProfiles",
                Arrays.asList(environment.getActiveProfiles()));
        result.put("banner", banner.message());

        return result;
    }
}