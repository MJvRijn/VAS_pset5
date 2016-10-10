package nl.mjvrijn.matthewvanrijn_pset5;

import android.content.Context;

import java.util.ArrayList;

/* This class is a singleton class that manages the collection of ToDoLists. */
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
        // On the first instance request the manager can get the application context which allows
        // it to connect to the database.
        if(database == null) {
            database = new DBHelper(c);
            readTodos();
        }
        return instance;
    }

    /* Read all TodoLists from the database. */
    public static void readTodos() {
        todoLists.clear();
        todoLists.addAll(database.getLists());

        if(todoLists.size() == 0) {
            // Create an example list if there are no other lists.
            createExample();
        } else {
            currentList = todoLists.get(0);
        }
    }

    /* This method places all TodoItems from a TodoList into the list that is connected to the
     * adapter of the ListView in the ListFragment. */
    public void updateData() {
        data.clear();
        data.addAll(currentList.getItems());
    }

    /* Allow setting the current list by ListView position. This is used when the user clicks on
     * a list. */
    public void setCurrentList(int pos) {
        currentList = todoLists.get(pos);
    }

    /* Allow setting the current list by list name. This is used when restoring a previous state. */
    public void setCurrentList(String name) {
        for(TodoList list : todoLists) {
            if(list.getName().equals(name)) {
                currentList = list;
                break;
            }
        }
    }

    /* Create a new TodoList */
    public void addList(String name) {
        String table = name.toLowerCase().replace(' ', '_');

        database.addTable(name, table);
        todoLists.add(new TodoList(database, name, table));
    }

    /* Delete a TodoList */
    public void removeList(TodoList list) {
        database.removeTable(list);
        todoLists.remove(list);

        // Handle the case where the list to be deleted is currently being viewed.
        if(list == currentList) {
            if(todoLists.size() > 0) {
                currentList = todoLists.get(0);
            } else {
                createExample();
            }
        }
    }

    /* Create an example list telling the user how to use the app. This is used as a workaround
     * for a bug which causes a crash if there are no TodoLists. */
    private static void createExample() {
        database.addTable("Example List", "example_list");
        currentList = new TodoList(database, "Example List", "example_list");
        currentList.addTask("Add new tasks in the box below");
        currentList.addTask("Tap tasks to mark them as complete");
        todoLists.add(currentList);
    }

    /* -- Getters/Setters */
    public ArrayList<TodoItem> getData() {
        return data;
    }

    public TodoList getCurrentList() {
        return currentList;
    }

    public ArrayList<TodoList> getListData() {
        return todoLists;
    }
}
