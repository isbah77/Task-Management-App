public class Task extends BaseTask {

    public Task(int id, String title, String deadline, String description, String priority, String status, String category) {
        super(id, title, deadline, description, priority, status, category);
    }

    // Overriding a method from BaseTask (polymorphism)
    @Override
    public void displayTaskDetails() {
        System.out.println("📌 Task: " + title + " | Priority: " + priority + " | Status: " + status);
    }

    // Overriding toString for custom representation
    @Override
    public String toString() {
        return "📋 Task Details:\n" +
                "🔹 Title: " + title + "\n" +
                "📅 Deadline: " + deadline + "\n" +
                "📝 Description: " + description + "\n" +
                "🚦 Priority: " + priority + "\n" +
                "✅ Status: " + status + "\n" +
                "📂 Category: " + category + "\n";
    }
}
