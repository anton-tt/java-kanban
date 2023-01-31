import java.util.HashMap;

public class EpicTask extends Task {
    private Status status;
    private HashMap<SubTask, Status> subTasks;

    public EpicTask(String name, String description, int id, HashMap<SubTask, Status> subTasks) {
        super(name, description, id, null);
        this.subTasks = subTasks;
    }

    public HashMap<SubTask, Status> getEpictaskSubtasks() {
        return subTasks;
    }
    public void setEpictaskSubtasks(HashMap<SubTask, Status> newSubtasks) {
        subTasks = newSubtasks;
    }

}