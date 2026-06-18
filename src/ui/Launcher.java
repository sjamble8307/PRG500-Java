package ui;

/**
 * Thin launcher that does NOT extend {@code javafx.application.Application}.
 *
 * When the JVM is given a main class that directly extends Application it
 * performs an early module-path check for the JavaFX runtime and throws
 * "JavaFX runtime components are missing" before reaching our code — even
 * though the JavaFX JARs are on the classpath via Maven.
 *
 * Indirecting through this plain class sidesteps that check.
 * Point your IDE run-configuration (and exec-maven-plugin) at ui.Launcher.
 */
public class Launcher {
    public static void main(String[] args) {
        LibraryApp.main(args);
    }
}

