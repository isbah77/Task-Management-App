
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:DatabaseManager.db";  // SQLite DB file

    // Initialize the database and create the 'tasks' table if it doesn't exist
    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS tasks ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "title TEXT, "
                    + "deadline TEXT, "
                    + "description TEXT, "
                    + "priority TEXT, "
                    + "status TEXT, "
                    + "category TEXT)";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fetch all tasks from the database
    public static List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM tasks";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("deadline"),
                        rs.getString("description"),
                        rs.getString("priority"),
                        rs.getString("status"),
                        rs.getString("category")
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    // Add a new task to the database
    public static boolean addTask(Task task) {
        String insertSQL = "INSERT INTO tasks (title, deadline, description, priority, status, category) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDeadline());
            pstmt.setString(3, task.getDescription());
            pstmt.setString(4, task.getPriority());
            pstmt.setString(5, task.getStatus());
            pstmt.setString(6, task.getCategory());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a task from the database by ID
    public static void deleteTask(int taskId) {
        String deleteSQL = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mark a task as completed
    public static void markTaskCompleted(int taskId) {
        String updateSQL = "UPDATE tasks SET status = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.setString(1, "Completed");
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
