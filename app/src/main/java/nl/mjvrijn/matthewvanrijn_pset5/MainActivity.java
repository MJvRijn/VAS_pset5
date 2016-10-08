package nl.mjvrijn.matthewvanrijn_pset5;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/* MainActivity.java is the main and only activity of the app. */
public class MainActivity extends AppCompatActivity {
    private ListFragment listFragment;
    private MenuFragment menuFragment;

    /* Set up the app */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listFragment = new ListFragment();
        menuFragment = new MenuFragment();

        if(savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, menuFragment, "ACTIVE_FRAGMENT").commit();
        }

        if(getFragmentManager().getBackStackEntryCount() != 0) {
            setTitle(TodoManager.getInstance(this).getCurrentList().getName());
        } else {
            setTitle("Lists");
        }

    }

    public void addTask(View v) {
        listFragment.addTask();
    }

    public void addList(View v) {
        EditText inputField = new EditText(this);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.list_remove_dialog_title));
        dialog.setMessage(getResources().getString(R.string.list_remove_dialog_message));
        dialog.setView(inputField);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TodoManager.getInstance(getApplicationContext()).addList("Bob");
            }
        });
        dialog.show();
    }

    public void goToListFragment() {
        setTitle(TodoManager.getInstance(this).getCurrentList().getName());

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, listFragment, "ACTIVE_FRAGMENT");
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
}
