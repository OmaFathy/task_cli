package org.example;

import org.example.Service.TaskManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("Welcome to the Task CLI. Type your commands:");

        // Loop to continuously accept user input until they exit
        while (true) {
            try {
                // Display prompt for user input
                System.out.print("task-cli> ");
                input = scanner.nextLine().trim();

                // Exit the loop if the user types "exit"
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting the Task CLI. Goodbye!");
                    break;
                }

                // Process the input
                if (!input.isEmpty()) {
                    manager.processInput(input);
                } else {
                    System.out.println("Invalid command. Please type a valid command.");
                }
            } catch (Exception e) {
                System.err.println("An error occurred while processing input: " + e.getMessage());
            }
        }

        // Close the scanner to avoid resource leaks
        scanner.close();
    }
}
