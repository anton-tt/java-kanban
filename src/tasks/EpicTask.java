package tasks;

import basic.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EpicTask extends Task {
    private List<Integer> subTasks;
   // private LocalDateTime startTime;
   // private Duration duration;
  //  private LocalDateTime endTime;

    public EpicTask(String name, String description) {
        super(name, description, Status.NEW);
        subTasks = new ArrayList<>();
    }

    public EpicTask(String name, String description, int id) {
        super(name, description, id, Status.NEW);
        subTasks = new ArrayList<>();
    }

    public List<Integer> getEpictaskSubtasks() {
        return subTasks;
    }
    public void setEpictaskSubtasks(List<Integer> newSubtasks) {
        subTasks = newSubtasks;
    }

    /*public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime newStartTime) {
        startTime = newStartTime;
    }

    public Duration getDuration() {
        return duration;
    }
    public void setDuration(Duration newDuration) {
        duration = newDuration;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime newEndTime) {
        endTime = newEndTime;
    }
*/
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EpicTask otherEpicTask = (EpicTask) obj;
        return Objects.equals(name, otherEpicTask .name) && Objects.equals(description, otherEpicTask .description) && Objects.equals(status, otherEpicTask.status) && Objects.equals(getEpictaskSubtasks(), otherEpicTask.getEpictaskSubtasks()) && (getId() == otherEpicTask.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getId(), name, description, status, getEpictaskSubtasks());
    }

}