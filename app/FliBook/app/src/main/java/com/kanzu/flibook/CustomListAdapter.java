package com.kanzu.flibook;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] names;
    private final String[] authors;
    private final String[] genres;

    public CustomListAdapter(Activity context, String[] names, String[] authors, String[] genres) {
        super(context, R.layout.mylist, names);

        this.context = context;
        this.names=names;
        this.authors=authors;
        this.genres=genres;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView nameText = (TextView) rowView.findViewById(R.id.name);
        TextView authorText = (TextView) rowView.findViewById(R.id.author);
        TextView genresText = (TextView) rowView.findViewById(R.id.genres);

        nameText.setText(names[position]);
        authorText.setText(authors[position]);
        genresText.setText(genres[position]);

        return rowView;

    };
}
