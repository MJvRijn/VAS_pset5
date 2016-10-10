package nl.mjvrijn.matthewvanrijn_pset5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/* This adapter connects the ListView in the ListFragment with the data set. */
public class ListViewAdapter extends ArrayAdapter<TodoItem> {

    public ListViewAdapter(Context context, int textViewID, ArrayList<TodoItem> data) {
        super(context, textViewID, data);
    }

    /* Inflate the display elements of a single entry of the ListView. */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Use convertView to recycle old views instead of creating new ones
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.content_list_entry, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.list_entry_textview);
        ImageView checkMark = (ImageView) convertView.findViewById(R.id.list_entry_check);

        // Set the task text
        textView.setText(getItem(position).getTask());

        // Set the tick and text colour based on whether the task has been completed.
        if(getItem(position).isDone()) {
            textView.setTextColor(getContext().getResources().getColor(R.color.textDone));
            checkMark.setVisibility(View.VISIBLE);
        } else {
            textView.setTextColor(getContext().getResources().getColor(R.color.textNotDone));
            checkMark.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
