package tasks;

import basic.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private final int epicTaskId;

    public SubTask(String name, String description, Status status,  String dateTime, long interval, int epicTaskId) {
        super(name, description, status, dateTime, interval);
        this.epicTaskId = epicTaskId;
    }

    public SubTask(String name, String description, int id, Status status, LocalDateTime startTime, Duration duration, int epicTaskId) {
        super(name, description, id, status, startTime, duration);
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
        return Objects.equals(name, otherSubTask.name) && Objects.equals(description, otherSubTask.description) && Objects.equals(status, otherSubTask.status) && (getSubtaskEpictaskId() == otherSubTask.getSubtaskEpictaskId()) && (getId() == otherSubTask.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getId(), name, description, status,getSubtaskEpictaskId());
    }

}