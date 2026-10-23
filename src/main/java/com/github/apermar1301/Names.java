package com.github.apermar1301;

import java.util.List;
import java.util.Random;

public class Names {

    private static final List<String> NAMES = List.of(
            "Antonio",
            "Beatriz",
            "Carlos",
            "Diana",
            "Eduardo",
            "Fernanda",
            "Gabriel",
            "Helena",
            "Ignacio",
            "Julia",
            "Lucas",
            "Marina",
            "Nicolás",
            "Olivia",
            "Pablo",
            "Rosa",
            "Sergio",
            "Teresa",
            "Valeria",
            "Alejandro"
    );

    public static void main(String[] args) {
        new Names().run(args);
    }

    public void run(String[] args) {
        int count = validateAndParseCount(args);
        generateRandomNames(count);
    }

    private int validateAndParseCount(String[] args) {
        if (args == null || args.length != 1) {
            System.err.println("Error: Must provide the count of names to generate.");
            System.exit(1);
        }

        try {
            int count = Integer.parseInt(args[0].trim());
            if (count < 0) {
                System.err.println("Error: Name count cannot be negative.");
                System.exit(1);
            }
            return count;
        } catch (NumberFormatException e) {
            System.err.println("Error: Argument must be a valid integer: " + args[0]);
            System.exit(1);
            return 0;
        }
    }

    private void generateRandomNames(int count) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int randomIndex = random.nextInt(NAMES.size());
            System.out.println(NAMES.get(randomIndex));
        }
    }
}
