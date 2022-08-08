package com.kanzu.flibook;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.folioreader.Config;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.util.AppUtil;
import com.kanzu.flibook.ui.library.LibraryFragment;

import java.io.File;

public class CustomFileListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] names;

    public CustomFileListAdapter(Activity context, String[] names) {
        super(context, R.layout.mylist2, names);

        this.context = context;
        this.names=names;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist2, null,true);

        TextView nameText = (TextView) rowView.findViewById(R.id.name);
        nameText.setText(names[position]);

        return rowView;

    };
}
