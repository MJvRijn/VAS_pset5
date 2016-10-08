package nl.mjvrijn.matthewvanrijn_pset5;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MenuFragment extends Fragment {
    private TodoManager manager;
    private MenuAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_menu, container, false);
        ListView listView = (ListView) view.findViewById(R.id.menu_listview);
        listView.setAdapter(adapter);

        setUpListeners(listView);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = TodoManager.getInstance(getActivity());
        adapter = new MenuAdapter(getActivity(), 0, manager.getListData());

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    /* Define and set listeners and actions for item presses, long presses and keyboard enter presses. */
    public void setUpListeners(ListView listView) {

        // On a long press; display a confirmation dialog box which calls removeTask on yes
        // and does nothing on no.
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final TodoList toRemove = adapter.getItem(position);

                new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.list_remove_dialog_title))
                        .setMessage(String.format(getResources().getString(R.string.list_remove_dialog_message),
                                toRemove.getName()))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                manager.removeList(adapter.getItem(position));
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), String.format(getResources().getString(R.string.list_remove_toast), toRemove.getName()), Toast.LENGTH_LONG).show();
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
                manager.setCurrentList(position);
                ((MainActivity) getActivity()).goToListFragment();
            }
        });
    }
}
