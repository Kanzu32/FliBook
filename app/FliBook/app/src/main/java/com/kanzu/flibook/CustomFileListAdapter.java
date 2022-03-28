package com.kanzu.flibook;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomFileListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] names;
    private final String[] authors;
    private final Bitmap[] imges;

    public CustomFileListAdapter(Activity context, String[] names, String[] authors, Bitmap[] imges) {
        super(context, R.layout.mylist2, names);

        this.context = context;
        this.names=names;
        this.authors=authors;
        this.imges=imges;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist2, null,true);

        TextView nameText = (TextView) rowView.findViewById(R.id.name);
        TextView authorText = (TextView) rowView.findViewById(R.id.author);
        ImageView image = (ImageView) rowView.findViewById(R.id.imageView);

        nameText.setText(names[position]);
        authorText.setText(authors[position]);
        if (imges[position] != null) {
            image.setImageBitmap(imges[position]);
        }

        return rowView;

    };
}
