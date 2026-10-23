package com.github.apermar1301;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class Greeting {

    private static final List<String> GREETING_TEMPLATES = List.of(
            "¡Hola, %s!",
            "¡Buenos días, %s!",
            "¿Cómo estás, %s?",
            "¡Qué alegría verte, %s!",
            "¡Un placer saludarte, %s!",
            "¡Bienvenido, %s!",
            "¡Saludos cordiales, %s!",
            "¡Encantado de saludarte, %s!",
            "¡Mucho gusto, %s!",
            "¡Qué bueno tenerte aquí, %s!"
    );

    public static void main(String[] args) {
        new Greeting().run();
    }

    public void run() {
        processGreetings();
    }

    private void processGreetings() {
        Random random = new Random();
        int lastGreetingIndex = -1;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String name = line.trim();
                if (name.isEmpty()) {
                    continue;
                }

                int greetingIndex;
                do {
                    greetingIndex = random.nextInt(GREETING_TEMPLATES.size());
                } while (greetingIndex == lastGreetingIndex && GREETING_TEMPLATES.size() > 1);
                lastGreetingIndex = greetingIndex;

                String greeting = String.format(GREETING_TEMPLATES.get(greetingIndex), name);
                System.out.println(greeting);
            }
        } catch (IOException e) {
            System.err.println("Error reading standard input: " + e.getMessage());
            System.exit(1);
        }
    }
}
