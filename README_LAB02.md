# Laboratory work 2

Student: М. И. — replace with full surname and name before submission.
Group: IT1-2404IS. Register number: 17. Variant: 17 % 12 + 1 = 6, masking.

This is a separate copy of the student's laboratory 1 project, with its Git history and original endpoints retained.

## Open and run in IntelliJ IDEA

1. Open this folder or its `pom.xml` as a Maven project.
2. Select installed JDK 25 and allow Maven import to finish.
3. Run `kz.iitu.springlab.SpringLab01Application` with the green Run button.
4. Open the following URLs in your browser:
   - http://localhost:8080/api/hello
   - http://localhost:8080/api/lab2/notify?text=IITU
   - http://localhost:8080/api/lab2/lifecycle
   - http://localhost:8080/api/lab2/scopes (refresh to compare IDs)
   - http://localhost:8080/api/lab2/custom?text=IITU%202026
   - http://localhost:8080/api/lab2/custom?text=Room%20314%2C%20floor%202
   - http://localhost:8080/api/lab2/custom?text=Spring%20Boot
5. Stop once with the IDE Stop button and inspect the `@PreDestroy` log.

The masking bean replaces every Unicode decimal digit with `*`, keeps other characters, has `@Order(3)`, and is selected with `@Qualifier("masking")`.

## Ambiguity experiment

Temporarily comment out `@Primary` in `EmailNotifier`, then rerun. The application should fail because regular Notifier candidates are ambiguous. Restore `@Primary` and rerun before normal use.

## Checks

Run the Maven `test` goal in IDEA, or run `mvnw.cmd test` with JAVA_HOME pointing to JDK 25. Tests cover primary and qualifier selection, ordered injection, masking and bean scopes.
