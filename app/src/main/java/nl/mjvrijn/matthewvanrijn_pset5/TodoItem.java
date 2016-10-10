package nl.mjvrijn.matthewvanrijn_pset5;

/* A data class to hold information about a task.*/
public class TodoItem {
    private long id;
    private String task;
    private boolean done;

    public TodoItem(long id, String task, boolean done) {
        this.id = id;
        this.task = task;
        this.done = done;
    }

    public long getID() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean d) {
        done = d;
    }
}
