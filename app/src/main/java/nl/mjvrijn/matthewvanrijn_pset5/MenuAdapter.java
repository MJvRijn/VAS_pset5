package nl.mjvrijn.matthewvanrijn_pset5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/* This adapter connects the ListView with the data set. */
public class MenuAdapter extends ArrayAdapter<TodoList> {

    public MenuAdapter(Context context, int textViewID, ArrayList<TodoList> data) {
        super(context, textViewID, data);
    }

    /* Inflate the display elements of a single entry of the ListView. */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Use convertView to recycle old views instead of creating new ones
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_menu_entry, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.menu_entry_textview);
        TextView counter = (TextView) convertView.findViewById(R.id.menu_entry_counter);

        // Set the task text
        TodoList list = getItem(position);
        textView.setText(list.getName());
        counter.setText(String.format("%d/%d", list.numComplete(), list.size()));


        return convertView;
    }
}
