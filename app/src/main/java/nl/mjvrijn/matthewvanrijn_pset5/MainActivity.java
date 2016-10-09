package nl.mjvrijn.matthewvanrijn_pset5;

import android.app.Fragment;
import android.app.FragmentTransaction;
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

    /* Set up the app */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menuFragment = new MenuFragment();
        listFragment = new ListFragment();

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.menu_container, menuFragment).commit();
        fm.beginTransaction().replace(R.id.list_container, listFragment).commit();

        listFrame = (FrameLayout) findViewById(R.id.list_container);
        menuFrame = (FrameLayout) findViewById(R.id.menu_container);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = getSharedPreferences("state", MODE_PRIVATE).edit();
        editor.putString("lastList", TodoManager.getInstance(this).getCurrentList().getName());
        editor.putBoolean("onMenu", listFrame.getVisibility() == View.INVISIBLE);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("state", MODE_PRIVATE);
        String lastList = prefs.getString("lastList", null);
        boolean onMenu = prefs.getBoolean("onMenu", true);

        TodoManager.getInstance(this).setCurrentList(lastList);

        menuFrame.setVisibility(View.VISIBLE);

        if(onMenu) {
            setTitle("Lists");
        } else {
            goToListFragment();
            setTitle(TodoManager.getInstance(this).getCurrentList().getName());
        }

    }

    public void addTask(View v) {
        listFragment.addTask();
    }

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

        AlertDialog ad = dialog.create();
        ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        ad.show();
    }

    public void goToListFragment() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            menuFrame.setVisibility(View.INVISIBLE);
        }

        listFrame.setVisibility(View.VISIBLE);

        setTitle(TodoManager.getInstance(this).getCurrentList().getName());
        invalidateOptionsMenu();
        listFragment.updateList();
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);

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

    public boolean isOnMenu() {
        return menuFrame.getVisibility() == View.VISIBLE;
    }
}
