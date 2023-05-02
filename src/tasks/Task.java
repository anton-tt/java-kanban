package tasks;

import basic.*;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    public final String name;
    public final String description;
    public Status status;
    private LocalDateTime startTime;
    private Duration duration;
    private LocalDateTime endTime;

    public Task(String name, String description, Status status, String dateTime, long interval) {
        this.name = name;
        this.description = description;
        id = 0;
        this.status = status;
        startTime = generateStartTimeTask(dateTime);
        duration = generateDurationTask(interval);
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, int id, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }


    public Task(String name, String description, int id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }
    public void setId(int newId) {
        id = newId;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status newStatus) {
        status = newStatus;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime newEndTime) {
        endTime = newEndTime;
    }


    public LocalDateTime generateStartTimeTask(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(dateTime, formatter);
        return startTime;
    }
    public Duration generateDurationTask(long minutes) {
        Duration duration = Duration.ofMinutes(minutes);
        return duration;
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




    public void setStartTime(LocalDateTime newStartTime) {
        startTime = newStartTime;
    }
    public void setDuration(Duration newDuration) {
        duration = newDuration;
    }
}