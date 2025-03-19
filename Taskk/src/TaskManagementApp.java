import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskManagementApp extends Application {

    private TableView<Task> taskTable = new TableView<>();
    private ObservableList<Task> tasksList = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        DatabaseManager.initializeDatabase();

        // Table view setup
        setupTableView();

        // Buttons
        Button addButton = new Button("Add Task");
        Button deleteButton = new Button("Delete Task");
        Button completeButton = new Button("Complete Task");
        Button sortButton = new Button("Sort Tasks");
        Button reminderButton = new Button("Reminders");
        Button viewAllButton = new Button("View All Tasks");
        Button searchButton = new Button("Search Task");

        addButton.setOnAction(e -> showAddTaskDialog());
        deleteButton.setOnAction(e -> deleteTask());
        completeButton.setOnAction(e -> markTaskCompleted());
        sortButton.setOnAction(e -> sortTasks());
        reminderButton.setOnAction(e -> showReminders());
        viewAllButton.setOnAction(e -> loadTasks(true)); // Load all tasks when viewing all
        searchButton.setOnAction(e -> showSearchDialog());

        // Style buttons (for consistency)
        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(completeButton);
        styleButton(sortButton);
        styleButton(reminderButton);
        styleButton(viewAllButton);
        styleButton(searchButton);

        // Layout
        VBox buttonBox = new VBox(10);
        buttonBox.setPadding(new javafx.geometry.Insets(10));
        buttonBox.getChildren().addAll(
                addButton, deleteButton, completeButton, sortButton, reminderButton, viewAllButton, searchButton
        );

        // Adding the table view to the layout
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new javafx.geometry.Insets(10));
        mainLayout.getChildren().addAll(buttonBox, taskTable);

        // Scene setup
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Task Management App");
        primaryStage.show();

        loadTasks(false); // Initially load only non-completed tasks
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-font-size: 14px; -fx-background-color: #ADD8E6; -fx-text-fill: black; -fx-padding: 10px; -fx-font-weight: bold;");
    }

    private void setupTableView() {
        TableColumn<Task, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));

        TableColumn<Task, String> deadlineColumn = new TableColumn<>("Deadline");
        deadlineColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadline()));

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        TableColumn<Task, String> priorityColumn = new TableColumn<>("Priority");
        priorityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriority()));

        TableColumn<Task, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        TableColumn<Task, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));

        taskTable.getColumns().addAll(titleColumn, deadlineColumn, descriptionColumn, priorityColumn, statusColumn, categoryColumn);
    }

    private void loadTasks(boolean showAllTasks) {
        List<Task> tasks = DatabaseManager.getAllTasks();
        ObservableList<Task> filteredTasks = FXCollections.observableArrayList();

        // If showAllTasks is true, include completed tasks as well
        for (Task task : tasks) {
            if (showAllTasks || !task.getStatus().equals("Completed")) {
                filteredTasks.add(task);
            }
        }

        tasksList.setAll(filteredTasks);
        taskTable.setItems(tasksList);

        // Debugging: Print tasks in the table
        System.out.println("Tasks Loaded: " + filteredTasks);
    }

    private void showAddTaskDialog() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Add Task");
        dialog.setHeaderText("Enter task details");

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField deadlineField = new TextField();
        deadlineField.setPromptText("Deadline (yyyy-MM-dd)");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        ComboBox<String> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll("Low", "Medium", "High");
        priorityComboBox.setPromptText("Select Priority");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        VBox dialogVbox = new VBox(10);
        dialogVbox.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Deadline:"), deadlineField,
                new Label("Description:"), descriptionField,
                new Label("Priority:"), priorityComboBox,
                new Label("Category:"), categoryField
        );

        dialog.getDialogPane().setContent(dialogVbox);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButtonType) {
                String title = titleField.getText();
                String deadline = deadlineField.getText();
                String description = descriptionField.getText();
                String priority = priorityComboBox.getValue();
                String category = categoryField.getText();

                return new Task(0, title, deadline, description, priority, "Pending", category);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(task -> {
            boolean success = DatabaseManager.addTask(task);
            if (success) {
                loadTasks(false); // Only load non-completed tasks
            } else {
                showAlert("Error", "Failed to save task.");
            }
        });
    }

    private void deleteTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Delete Task");
            confirmationAlert.setHeaderText("Are you sure you want to delete this task?");
            confirmationAlert.setContentText("This action cannot be undone.");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    DatabaseManager.deleteTask(selectedTask.getId());
                    loadTasks(false); // Load non-completed tasks
                }
            });
        } else {
            showAlert("Error", "Please select a task to delete.");
        }
    }

    private void markTaskCompleted() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Complete Task");
            confirmationAlert.setHeaderText("Are you sure you want to mark this task as completed?");
            confirmationAlert.setContentText("This action cannot be undone.");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    DatabaseManager.markTaskCompleted(selectedTask.getId());
                    selectedTask.setStatus("Completed");  // Update status of task
                    taskTable.refresh();  // Refresh table view to show updated task
                }
            });
        } else {
            showAlert("Error", "Please select a task to mark as completed.");
        }
    }

    private void sortTasks() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Deadline", "Deadline", "Priority", "Status");
        dialog.setTitle("Sort Tasks");
        dialog.setHeaderText("Choose how you want to sort the tasks");

        dialog.showAndWait().ifPresent(choice -> {
            switch (choice) {
                case "Deadline":
                    tasksList.sort((task1, task2) -> task1.getDeadline().compareTo(task2.getDeadline()));
                    break;
                case "Priority":
                    tasksList.sort((task1, task2) -> task1.getPriority().compareTo(task2.getPriority()));
                    break;
                case "Status":
                    tasksList.sort((task1, task2) -> task1.getStatus().compareTo(task2.getStatus()));
                    break;
            }
            taskTable.setItems(tasksList);
        });
    }

    private void showReminders() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        ObservableList<Task> reminderTasks = FXCollections.observableArrayList();
        StringBuilder message = new StringBuilder();

        for (Task task : tasksList) {
            LocalDate deadline = LocalDate.parse(task.getDeadline(), DATE_FORMAT);

            if (deadline.isBefore(today)) {
                reminderTasks.add(task);
                message.append("⚠️ Task Overdue: ").append(task.getTitle()).append("\n");
            } else if (deadline.isEqual(today)) {
                reminderTasks.add(task);
                message.append("📅 Task Due Today: ").append(task.getTitle()).append("\n");
            } else if (deadline.isEqual(tomorrow)) {
                reminderTasks.add(task);
                message.append("⏳ Task Due Tomorrow: ").append(task.getTitle()).append("\n");
            }
        }

        if (reminderTasks.isEmpty()) {
            showAlert("No Reminders", "No overdue tasks or tasks due today/tomorrow.");
        } else {
            taskTable.setItems(reminderTasks);
            showAlert("Task Reminders", message.toString());
        }
    }

    private void showSearchDialog() {
        TextInputDialog searchDialog = new TextInputDialog();
        searchDialog.setTitle("Search Task");
        searchDialog.setHeaderText("Search by task title or description:");
        searchDialog.showAndWait().ifPresent(searchText -> {
            ObservableList<Task> filteredTasks = FXCollections.observableArrayList();
            for (Task task : tasksList) {
                if (task.getTitle().toLowerCase().contains(searchText.toLowerCase()) || task.getDescription().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredTasks.add(task);
                }
            }
            taskTable.setItems(filteredTasks);
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
