package nl.mjvrijn.matthewvanrijn_pset5;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.Toast;

/* MainActivity.java is the main and only activity of the app. */
public class MainActivity extends AppCompatActivity {
    private ListFrame listFrame;
    private MenuFrame menuFrame;

    /* Set up the app */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListFragment listFragment = new ListFragment();
        MenuFragment menuFragment = new MenuFragment();

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(R.id.list_container, menuFragment).commit();
        fm.beginTransaction().add(R.id.menu_container, menuFragment).commit();


        if(savedInstanceState == null) {
            /getFragmentManager().beginTransaction().replace(R.id.fragment_container, menuFragment).commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = getSharedPreferences("state", MODE_PRIVATE).edit();
        editor.putString("lastList", TodoManager.getInstance(this).getCurrentList().getName());
        editor.putBoolean("onMenu", isOnMenu());
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();


        SharedPreferences prefs = getSharedPreferences("state", MODE_PRIVATE);
        String lastList = prefs.getString("lastList", null);
        boolean onMenu = prefs.getBoolean("onMenu", true);

        TodoManager.getInstance(this).setCurrentList(lastList);

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
            }
        });

        AlertDialog ad = dialog.create();
        ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        ad.show();


    }

    public void goToListFragment() {
        setTitle(TodoManager.getInstance(this).getCurrentList().getName());

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, listFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() != 0) {
            setTitle("Lists");
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);

        menu.findItem(R.id.action_add).setVisible(isOnMenu());

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
        Fragment f = getFragmentManager().findFragmentById(R.id.fragment_container);

        return f instanceof MenuFragment;
    }
}
