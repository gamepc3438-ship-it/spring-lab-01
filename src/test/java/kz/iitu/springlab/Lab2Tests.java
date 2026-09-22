package kz.iitu.springlab;

import kz.iitu.springlab.notify.MaskingNotifier;
import kz.iitu.springlab.notify.NotificationService;
import kz.iitu.springlab.scope.TicketOffice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class Lab2Tests {
    @Autowired NotificationService notifications;
    @Autowired TicketOffice tickets;

    @Test void primaryQualifierAndOrder() {
        assertEquals("email: IITU123", notifications.viaPrimary("IITU123"));
        assertEquals("console: IITU123", notifications.viaConsole("IITU123"));
        assertEquals(List.of("console: IITU123", "email: IITU123", "IITU***", "noop"),
                notifications.viaAll("IITU123"));
    }

    @Test void maskingVariantSix() {
        var notifier = new MaskingNotifier();
        assertEquals("IITU ****", notifier.send("IITU 2026"));
        assertEquals("Room ***, floor *", notifier.send("Room 314, floor 2"));
        assertEquals("Spring Boot", notifier.send("Spring Boot"));
        assertEquals("***", notifier.send("١२3"));
    }

    @Test void prototypeAndSingleton() {
        var first = tickets.demo();
        var second = tickets.demo();
        assertEquals(first.get("office"), second.get("office"));
        assertEquals(first.get("injectedDirectly"), second.get("injectedDirectly"));
        assertNotEquals(first.get("viaProvider"), second.get("viaProvider"));
    }
}
