public class BaseTask {
    protected int id;
    protected String title;
    protected String deadline;
    protected String description;
    protected String priority;
    protected String status;
    protected String category;

    public BaseTask(int id, String title, String deadline, String description, String priority, String status, String category) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.category = category;
    }

    // ✅ Getter Methods
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDeadline() { return deadline; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public String getCategory() { return category; }

    // ✅ Setter Method (Fix for "cannot find symbol method setStatus")
    public void setStatus(String status) {
        this.status = status;
    }

    // Common method for all tasks
    public void displayTaskDetails() {
        System.out.println("Task: " + title + " | Status: " + status);
    }

    @Override
    public String toString() {
        return "Task{id=" + id + ", title='" + title + "', deadline='" + deadline + "', description='" + description + "', priority='" + priority + "', status='" + status + "', category='" + category + "'}";
    }
}
