package nl.mjvrijn.matthewvanrijn_pset5;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class TodoManager {
    private static DBHelper database;
    private static ArrayList<TodoList> todoLists;
    private static ArrayList<TodoItem> data;
    private static int currentList;
    private static TodoManager instance = new TodoManager();

    private TodoManager() {
        todoLists = new ArrayList<>();
        data = new ArrayList<>();
        currentList = 0;
    }

    public static TodoManager getInstance(Context c) {
        if(database == null) {
            database = new DBHelper(c);
            readTodos();

        }
        return instance;
    }

    public static void readTodos() {
        todoLists.clear();
        todoLists.addAll(database.getLists());
    }

    public void updateData() {
        data.clear();
        data.addAll(todoLists.get(currentList).getItems());
    }

    public ArrayList<TodoItem> getData() {
        return data;
    }

    public TodoList getCurrentList() {
        return todoLists.get(currentList);
    }

    public ArrayList<TodoList> getListData() {
        return todoLists;
    }

    public void setCurrentList(int pos) {
        currentList = pos;
    }

    public void addList(String name) {
        String table = name.toLowerCase().replace(' ', '_');

        database.addTable(name, table);
        todoLists.add(new TodoList(database, name, table));
    }

    public void removeList(TodoList list) {
        database.removeTable(list);
        todoLists.remove(list);
    }
}
