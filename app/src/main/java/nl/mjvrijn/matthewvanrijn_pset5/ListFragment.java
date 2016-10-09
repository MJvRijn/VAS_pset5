package nl.mjvrijn.matthewvanrijn_pset5;


import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListFragment extends Fragment {
    private TodoManager manager;
    private ListViewAdapter adapter;
    private ListView listView;
    private EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_list, container, false);

        listView = (ListView) view.findViewById(R.id.list_listview);
        editText = (EditText) view.findViewById(R.id.list_edittext);

        listView.setAdapter(adapter);
        setUpListeners();
        updateList();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = TodoManager.getInstance(getActivity());
        adapter = new ListViewAdapter(getActivity(), 0, manager.getData());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    /* Define and set listeners and actions for item presses, long presses and keyboard enter presses. */
    public void setUpListeners() {

        // On a long press; display a confirmation dialog box which calls removeTask on yes
        // and does nothing on no.
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final TodoItem toRemove = adapter.getItem(position);

                new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.item_remove_dialog_title))
                        .setMessage(String.format(getResources().getString(R.string.item_remove_dialog_message),
                                toRemove.getTask()))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeTask(toRemove);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                }).show();

                return true;
            }
        });

        // On a normal press; check the task off.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                manager.getCurrentList().toggleCompleted(adapter.getItem(position));
                updateList();
            }
        });

        // When the enter is pressed on the keyboard; add the task in the edittext field.
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addTask();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /* Create a task given by the contents of the EditText field, and add it to the list. */
    public void addTask() {
        String task = editText.getText().toString().trim();

        // Do not add empty tasks
        if(!task.equals("")) {
            manager.getCurrentList().addTask(task);
            editText.setText("");
            Toast.makeText(getActivity(), String.format(getResources().getString(R.string.item_add_toast),
                    task), Toast.LENGTH_LONG).show();

            updateList();
        }
    }

    /* Remove a given task from the list. */
    public void removeTask(TodoItem t) {
        manager.getCurrentList().removeTask(t);
        Toast.makeText(getActivity(), String.format(getResources().getString(R.string.item_remove_toast),
                t.getTask()), Toast.LENGTH_LONG).show();

        updateList();
    }

    public void updateList() {
        manager.updateData();
        adapter.notifyDataSetChanged();
    }
}
