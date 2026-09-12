package kz.iitu.springlab.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class HelloController {

    @Value("${app.owner:unknown}")
    private String owner;

    @GetMapping("/hello")
    public Greeting hello(@RequestParam(defaultValue = "world") String name) {
        return new Greeting("Hello, " + name + "!", owner, LocalDateTime.now());
    }

    @GetMapping("/info")
    public Info info() {
        return new Info(owner,
                System.getProperty("java.version"),
                Runtime.getRuntime().availableProcessors());
    }

    public record Greeting(String message, String owner, LocalDateTime timestamp) { }

    public record Info(String owner, String javaVersion, int cpuCores) { }

    @GetMapping("/wordcount")
    public WordCount wordcount(@RequestParam(defaultValue = "Spring Boot Lab Application") String text) {
        if (text == null || text.trim().isEmpty()) {
            return new WordCount(0, 0, "");
        }

        String[] words = text.trim().split("\\s+");
        int wordCount = words.length;
        int charCount = text.length();

        String longestWord = "";
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Zа-яА-Я0-9]", "");
            if (cleanWord.length() > longestWord.length()) {
                longestWord = cleanWord;
            }
        }

        return new WordCount(wordCount, charCount, longestWord);
    }

    public record WordCount(int wordCount, int charCount, String longestWord) { }
}