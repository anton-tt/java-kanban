public class Task {
    public String name;
    public String description;
    private int id;
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


    /*public void setStatus(Status newStatus) {

        status = newStatus;
        }*/
}

/* @Override
    public String toString() {
        String result = "RequiredTask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'';


    }
    }*/


/*public abstract class Task {

    private int id;
    private String name;

    public Task(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public abstract Status getStatus();


}
    /*public Status getStatus() {
        return status;
    }

    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE
    }*/


