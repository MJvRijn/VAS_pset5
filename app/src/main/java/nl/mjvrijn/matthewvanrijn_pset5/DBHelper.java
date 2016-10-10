package nl.mjvrijn.matthewvanrijn_pset5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/* DBHelper manages all interactions between the list data objects and the SQLite database. */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "todo.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    /* Read a to-do list from the database and return it as a list of items */
    public ArrayList<TodoItem> readTasks(String table) {
        ArrayList<TodoItem> output = new ArrayList<>();

        SQLiteDatabase database = getWritableDatabase();
        String query = String.format("SELECT _id , task , done FROM %s", table);
        Cursor c = database.rawQuery(query, null);

        while(c.moveToNext()) {
            long id = c.getLong(c.getColumnIndex("_id"));
            String task = c.getString(c.getColumnIndex("task"));
            boolean done = c.getInt(c.getColumnIndex("done")) == 1;

            output.add(new TodoItem(id, task, done));
        }

        c.close();
        database.close();

        return output;
    }

    /* Add a task given by a string to the database */
    public TodoItem addTask(String table, String task) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues entry = new ContentValues();
        entry.put("task", task);
        entry.put("done", 0);

        long id = database.insert(table, null, entry);
        database.close();

        return new TodoItem(id, task, false);
    }

    /* Add a table for a new TodoList to the database and add a reference to it in the lists table */
    public void addTable(String name, String table) {
        String query = "CREATE TABLE %s ( _id INTEGER PRIMARY KEY AUTOINCREMENT , task TEXT , done INTEGER )";
        getWritableDatabase().execSQL(String.format(query, table));

        ContentValues entry = new ContentValues();
        entry.put("tab", table);
        entry.put("name", name);

        getWritableDatabase().insert("lists", null, entry);

    }

    /* Remove the table of a deleted ToDoList and remove its reference from the lists table. */
    public void removeTable(TodoList toRemove) {
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + toRemove.getTable());
        getWritableDatabase().delete("lists", "name='" + toRemove.getName() + "'", null);

    }

    /* Remove a given task from the database. */
    public void removeTask(String table, TodoItem toRemove) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(table, "_id = ?", new String[]{String.valueOf(toRemove.getID())});
    }

    /* Toggle whether a task is completed in the database */
    public void toggleCompleted(String table, TodoItem task) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues entry = new ContentValues();
        entry.put("task", task.getTask());

        if(task.isDone()) {
            entry.put("done", 0);
        } else {
            entry.put("done", 1);
        }

        database.update(table, entry, "_id = ?", new String[]{String.valueOf(task.getID())});
        database.close();
    }

    /* Read all tables referenced in the lists table and turn them into a list of lists. */
    public ArrayList<TodoList> getLists() {
        ArrayList<TodoList> output = new ArrayList<>();

        Cursor c = getWritableDatabase().rawQuery("SELECT tab, name FROM lists", null);

        while(c.moveToNext()) {
            String table = c.getString(c.getColumnIndex("tab"));
            String name = c.getString(c.getColumnIndex("name"));
            output.add(new TodoList(this, name, table));
        }

        c.close();

        return output;
    }

    /* Function to initialise the DB table when the database is first created. */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE lists ( _id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT , tab TEXT )";
        db.execSQL(query);
    }

    /* Function to update the database when a structural change is made. Since none have been made
     * this function does not do anything. */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
