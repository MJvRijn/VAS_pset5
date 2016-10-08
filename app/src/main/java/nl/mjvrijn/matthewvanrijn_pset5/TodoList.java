package nl.mjvrijn.matthewvanrijn_pset5;

import java.util.ArrayList;

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

    private void readTasks() {
        items.clear();
        items.addAll(database.readTasks(table));
    }

    public String getName() {
        return name;
    }

    public String getTable() {
        return table;
    }

    public ArrayList<TodoItem> getItems() {
        return items;
    }

    public int numComplete() {
        int counter = 0;
        for(TodoItem item : items) {
            if(item.isDone()) {
                counter++;
            }
        }

        return counter;
    }

    public int size() {
        return items.size();
    }

    public void addTask(String task) {
        TodoItem tdi = database.addTask(table, task);
        items.add(tdi);
    }

    public void removeTask(TodoItem tdi) {
        database.removeTask(table, tdi);
        items.remove(tdi);

    }

    public void toggleCompleted(TodoItem tdi) {
        database.toggleCompleted(table, tdi);
        tdi.setDone(!tdi.isDone());
    }

}
