package nl.mjvrijn.matthewvanrijn_pset5;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

/* MainActivity.java is the main and only activity of the app. */
public class MainActivity extends AppCompatActivity {
    private FrameLayout listFrame;
    private FrameLayout menuFrame;
    private ListFragment listFragment;
    private MenuFragment menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the two fragments and attach them to their FrameLayouts
        menuFragment = new MenuFragment();
        listFragment = new ListFragment();

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.menu_container, menuFragment).commit();
        fm.beginTransaction().replace(R.id.list_container, listFragment).commit();

        listFrame = (FrameLayout) findViewById(R.id.list_container);
        menuFrame = (FrameLayout) findViewById(R.id.menu_container);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Read the previous application state from the shared preferences
        SharedPreferences prefs = getSharedPreferences("state", MODE_PRIVATE);
        String lastList = prefs.getString("lastList", null);
        boolean onMenu = prefs.getBoolean("onMenu", true);

        // Set the currently selected list to the last viewed one
        TodoManager.getInstance(this).setCurrentList(lastList);

        // Set up the app correctly for starting on the menu or on a list
        menuFrame.setVisibility(View.VISIBLE);

        if(onMenu) {
            setTitle("Lists");
        } else {
            goToListFragment();
            setTitle(TodoManager.getInstance(this).getCurrentList().getName());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Store the current application state in case the activity is not resumed
        SharedPreferences.Editor editor = getSharedPreferences("state", MODE_PRIVATE).edit();
        editor.putString("lastList", TodoManager.getInstance(this).getCurrentList().getName());
        editor.putBoolean("onMenu", listFrame.getVisibility() == View.INVISIBLE);
        editor.apply();
    }

    /* This method sets the correct visibilities and title for when a TodoList has been selected
     * from the menu. */
    public void goToListFragment() {

        // In portrait mode the menu should not show while the list is visible
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            menuFrame.setVisibility(View.INVISIBLE);
        }

        // Make the list visible
        listFrame.setVisibility(View.VISIBLE);

        // Set the title and reload the menu bar
        setTitle(TodoManager.getInstance(this).getCurrentList().getName());
        invalidateOptionsMenu();
        listFragment.updateList();
    }

    /* This method reverses the actions taken by goToListFragment() when the back button is
     * pressed. It returns to the list menu. This only has an effect when in portrait. */
    @Override
    public void onBackPressed() {
        if(menuFrame.getVisibility() == View.INVISIBLE) {
            setTitle("Lists");
            menuFrame.setVisibility(View.VISIBLE);
            listFrame.setVisibility(View.INVISIBLE);
            invalidateOptionsMenu();
        } else {
            super.onBackPressed();
        }
    }

    /* This method displays the necessary dialog box for creating a new list. This box contains
     * an edittext and an OK button. */
    public void addList() {
        final EditText inputField = (EditText) getLayoutInflater().inflate(R.layout.fragment_menu_dialog, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.list_add_dialog_title));
        dialog.setMessage(getResources().getString(R.string.list_add_dialog_message));
        dialog.setView(inputField);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = inputField.getText().toString().trim();

                if(!name.equals("")) {
                    TodoManager.getInstance(getApplicationContext()).addList(name);
                }

                Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.list_add_toast), name), Toast.LENGTH_LONG).show();

                menuFragment.updateList();
            }
        });

        // Ensure the keyboard is automatically opened when the EditText gets focus
        AlertDialog ad = dialog.create();
        ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        ad.show();
    }

    /* This method links the action of the add button on the ListFragment to the ListFragment
     * object itself, so that a task can be added.  */
    public void addTask(View v) {
        listFragment.addTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);

        // The add button for adding a list is made visible when the menu fragment is present.
        menu.findItem(R.id.action_add).setVisible(menuFrame.getVisibility() == View.VISIBLE);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add) {
            addList();
        }

        return true;
    }

    /* This utility method is called by the fragments when they make changes to the data that affect
     * the other fragment. It causes the data to be updated and the adapter to be notified for
     * both fragment ListViews. */
    public void notifyAllAdapters() {
        listFragment.updateList();
        menuFragment.updateList();
    }
}
