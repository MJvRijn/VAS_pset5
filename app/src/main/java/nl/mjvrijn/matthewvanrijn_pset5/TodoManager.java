package nl.mjvrijn.matthewvanrijn_pset5;

import android.content.Context;

import java.util.ArrayList;

public class TodoManager {
    private static DBHelper database;
    private static ArrayList<TodoList> todoLists;
    private static ArrayList<TodoItem> data;
    private static TodoList currentList;
    private static TodoManager instance = new TodoManager();

    private TodoManager() {
        todoLists = new ArrayList<>();
        data = new ArrayList<>();
    }

    public static TodoManager getInstance(Context c) {
        if(database == null) {
            database = new DBHelper(c);
            readTodos();

            if(todoLists.size() > 0) {
                currentList = todoLists.get(0);
            } else {
                currentList = null;
            }
        }
        return instance;
    }

    public static void readTodos() {
        todoLists.clear();
        todoLists.addAll(database.getLists());
    }

    public void updateData() {
        data.clear();
        data.addAll(currentList.getItems());
    }

    public ArrayList<TodoItem> getData() {
        return data;
    }

    public TodoList getCurrentList() {
        return currentList;
    }

    public ArrayList<TodoList> getListData() {
        return todoLists;
    }

    public void setCurrentList(int pos) {
        currentList = todoLists.get(pos);
    }

    public void setCurrentList(String name) {
        for(TodoList list : todoLists) {
            if(list.getName().equals(name)) {
                currentList = list;
                break;
            }
        }
    }

    public void addList(String name) {
        String table = name.toLowerCase().replace(' ', '_');

        database.addTable(name, table);
        todoLists.add(new TodoList(database, name, table));
    }

    public void removeList(TodoList list) {
        database.removeTable(list);
        todoLists.remove(list);
        if(list == currentList) {
            if(todoLists.size() > 0) {
                currentList = todoLists.get(0);
            } else {
                currentList = null;
            }
        }
    }
}
