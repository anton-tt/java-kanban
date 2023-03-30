package tasks;
import basic.*;
import managers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EpicTask extends Task {
    private Status status;
    private List<Integer> subTasks;

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