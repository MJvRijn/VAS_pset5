package nl.mjvrijn.matthewvanrijn_pset5;

import java.util.ArrayList;

/* A class to hold and frocess information about a todo list.*/
public class TodoList {
    private String name;
    private String table;
    private ArrayList<TodoItem> items;
    private DBHelper database;

    public TodoList(DBHelper db, String n, String t) {
        database = db;
        name = n;
        table = t;
        items = new ArrayList<>();

        readTasks();
    }

    /* Read the TodoItems from the database */
    private void readTasks() {
        items.clear();
        items.addAll(database.readTasks(table));
    }

    /* Return the number of tasks on the list that have been completed. */
    public int numComplete() {
        int counter = 0;
        for(TodoItem item : items) {
            if(item.isDone()) {
                counter++;
            }
        }

        return counter;
    }

    /* return the number of tasks on the list. */
    public int size() {
        return items.size();
    }

    /* Add a task to the list and database. */
    public void addTask(String task) {
        TodoItem tdi = database.addTask(table, task);
        items.add(tdi);
    }

    /* Remove a task from the list and database. */
    public void removeTask(TodoItem tdi) {
        database.removeTask(table, tdi);
        items.remove(tdi);

    }

    /* Toggle a tasks completion status. */
    public void toggleCompleted(TodoItem tdi) {
        database.toggleCompleted(table, tdi);
        tdi.setDone(!tdi.isDone());
    }

    /* -- Getters/Setters */
    public String getName() {
        return name;
    }

    public String getTable() {
        return table;
    }

    public ArrayList<TodoItem> getItems() {
        return items;
    }

}
