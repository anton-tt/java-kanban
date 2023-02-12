package tasks;
import basic.*;
import managers.*;
import java.util.Objects;

public class SubTask extends Task {
    final private int epicTaskId;

    public SubTask(String name, String description, int id, Status status, int epicTaskId) {
        super(name, description, id, status);
        this.epicTaskId = epicTaskId;
    }

    public int getSubtaskEpictaskId() {
        return epicTaskId;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SubTask otherSubTask = (SubTask) obj;
        return Objects.equals(name, otherSubTask.name) && Objects.equals(description, otherSubTask.description) && Objects.equals(status, otherSubTask.status) && (getSubtaskEpictaskId() == getSubtaskEpictaskId()) && (getId() == otherSubTask.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getId(), name, description, status,getSubtaskEpictaskId());
    }
}