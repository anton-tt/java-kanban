public class SubTask  extends Task {
    private EpicTask epicTask;

    public SubTask(String name, String description, int id, Status status, EpicTask epicTask) {
        super(name, description, id, status);
        this.epicTask = epicTask;
    }

    public EpicTask getSubtaskEpictask() {
        return epicTask;
    }

}