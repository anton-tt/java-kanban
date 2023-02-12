package tasks;
import basic.*;
import managers.*;
import java.util.Objects;

public class Task {
    private final int id;
    public final String name;
    public final String description;
    public Status status;

    public Task(String name, String description, int id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status newStatus) {
        status = newStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return Objects.equals(name, otherTask.name) && Objects.equals(description, otherTask.description) && Objects.equals(status, otherTask.status) && (getId() == otherTask.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getId(), name, description, status);
    }

}