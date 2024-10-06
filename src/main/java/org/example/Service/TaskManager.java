package org.example.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.Entity.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private static final String FILE_PATH = "tasks.json";
    private ObjectMapper objectMapper;
    private List<Task> tasks;

    public TaskManager() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register the JavaTimeModule
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        tasks = new ArrayList<>();
        loadTasks(); // Load tasks from the JSON file on initialization
    }

    private void loadTasks() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                // If the file doesn't exist, create it with an empty JSON array
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, new ArrayList<Task>());
            } else {
                // Check if the file is empty
                if (file.length() == 0) {
                    tasks = new ArrayList<>(); // Initialize with an empty list
                } else {
                    tasks = objectMapper.readValue(file, new TypeReference<List<Task>>() {});
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading tasks from file: " + e.getMessage());
        }
    }

    private String handleCommand(String input) {
        String[] parts = input.split(" ", 2);
        String command = parts[0].toLowerCase();
        return command;
    }

    public void processInput(String input) {
        String command = handleCommand(input);

        try {
            switch (command) {
                case "add":
                    String[] taskParts = input.split(" ", 2);
                    if (taskParts.length > 1) {
                        String description = taskParts[1].trim().replaceAll("^\"|\"$", ""); // Remove surrounding quotes
                        addTask(description, "pending");
                    } else {
                        System.out.println("No description provided for the task.");
                    }
                    break;
                case "update":
                    taskParts = input.split(" ", 3);
                    if (taskParts.length > 2) {
                        String idStr = taskParts[1];
                        int id = Integer.parseInt(idStr);
                        String description = taskParts[2].trim().replaceAll("^\"|\"$", ""); // Remove surrounding quotes
                        updateTask(id, description);
                    } else {
                        System.out.println("Invalid command. Usage: update <id> <description>");
                    }
                    break;
                case "delete":
                    taskParts = input.split(" ", 2);
                    if (taskParts.length > 1) {
                        String idStr = taskParts[1];
                        int id = Integer.parseInt(idStr);
                        deleteTask(id);
                    } else {
                        System.out.println("Invalid command. Usage: delete <id>");
                    }
                    break;
                case "mark-in-progress":
                    taskParts = input.split(" ", 2);
                    if (taskParts.length > 1) {
                        String idStr = taskParts[1];
                        int id = Integer.parseInt(idStr);
                        markTaskInProgress(id);
                    } else {
                        System.out.println("Invalid command. Usage: mark-in-progress <id>");
                    }
                    break;
                case "mark-done":
                    taskParts = input.split(" ", 2);
                    if (taskParts.length > 1) {
                        String idStr = taskParts[1];
                        int id = Integer.parseInt(idStr);
                        markTaskDone(id);
                    } else {
                        System.out.println("Invalid command. Usage: mark-done <id>");
                    }
                    break;
                case "list":
                    taskParts = input.split(" ", 2);
                    if (taskParts.length == 1 || taskParts[1].equals("")) {
                        listAllTasks();
                    } else if (taskParts[1].equals("done")) {
                        listAllDoneTasks();
                    } else if (taskParts[1].equals("todo")) {
                        listAllTodoTasks();
                    } else if (taskParts[1].equals("in-progress")) {
                        listAllInProgressTasks();
                    }
                    break;
                default:
                    System.out.println("Unknown command: " + command);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid ID format. ID must be an integer.");
        } catch (Exception e) {
            System.err.println("An error occurred while processing the command: " + e.getMessage());
        }
    }

    private void saveTasks() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), tasks);
        } catch (IOException e) {
            System.err.println("Error saving tasks to file: " + e.getMessage());
        }
    }

    public void addTask(String description, String status) {
        try {
            Task newTask = new Task(description, status);
            tasks.add(newTask);
            saveTasks(); // Save the updated list to the JSON file
        } catch (Exception e) {
            System.err.println("Error adding task: " + e.getMessage());
        }
    }

    public void updateTask(int id, String description) {
        try {
            for (Task task : tasks) {
                if (task.getId() == id) {
                    task.setDescription(description);
                    saveTasks(); // Save the updated task to the JSON file
                    return;
                }
            }
            System.err.println("Task with ID " + id + " not found.");
        } catch (Exception e) {
            System.err.println("Error updating task: " + e.getMessage());
        }
    }

    public void deleteTask(int id) {
        try {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == id) {
                    tasks.remove(i);
                    saveTasks(); // Save the updated list to the JSON file
                    return;
                }
            }
            System.err.println("Task with ID " + id + " not found.");
        } catch (Exception e) {
            System.err.println("Error deleting task: " + e.getMessage());
        }
    }

    public void listAllTasks() {
        try {
            for (Task task : tasks) {
                System.out.println(task.getId() + ". " + task.getDescription() + " - " + task.getStatus());
            }
        } catch (Exception e) {
            System.err.println("Error listing tasks: " + e.getMessage());
        }
    }

    public void listAllDoneTasks() {
        try {
            for (Task task : tasks) {
                if (task.getStatus().equals("done")) {
                    System.out.println(task.getId() + ". " + task.getDescription() + " - " + task.getStatus());
                }
            }
        } catch (Exception e) {
            System.err.println("Error listing done tasks: " + e.getMessage());
        }
    }

    public void listAllTodoTasks() {
        try {
            for (Task task : tasks) {
                if (task.getStatus().equals("todo")) {
                    System.out.println(task.getId() + ". " + task.getDescription() + " - " + task.getStatus());
                }
            }
        } catch (Exception e) {
            System.err.println("Error listing todo tasks: " + e.getMessage());
        }
    }

    public void listAllInProgressTasks() {
        try {
            for (Task task : tasks) {
                if (task.getStatus().equals("in-progress")) {
                    System.out.println(task.getId() + ". " + task.getDescription() + " - " + task.getStatus());
                }
            }
        } catch (Exception e) {
            System.err.println("Error listing in-progress tasks: " + e.getMessage());
        }
    }

    public void markTaskInProgress(int id) {
        try {
            for (Task task : tasks) {
                if (task.getId() == id) {
                    task.setStatus("in-progress");
                    saveTasks(); // Save the updated task to the JSON file
                    return;
                }
            }
            System.err.println("Task with ID " + id + " not found.");
        } catch (Exception e) {
            System.err.println("Error marking task in-progress: " + e.getMessage());
        }
    }

    public void markTaskDone(int id) {
        try {
            for (Task task : tasks) {
                if (task.getId() == id) {
                    task.setStatus("done");
                    saveTasks(); // Save the updated task to the JSON file
                    return;
                }
            }
            System.err.println("Task with ID " + id + " not found.");
        } catch (Exception e) {
            System.err.println("Error marking task done: " + e.getMessage());
        }
    }
}
