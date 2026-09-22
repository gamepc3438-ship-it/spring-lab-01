package kz.iitu.springlab.notify;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component("masking")
@Order(3)
public class MaskingNotifier implements Notifier {
    private static final Logger log = LoggerFactory.getLogger(MaskingNotifier.class);

    @PostConstruct
    void init() {
        log.info("CUSTOM >> masking notifier initialized");
    }

    @Override
    public String send(String message) {
        return message.replaceAll("\\p{Nd}", "*");
    }

    @Override
    public String channel() {
        return "masking";
    }
}
