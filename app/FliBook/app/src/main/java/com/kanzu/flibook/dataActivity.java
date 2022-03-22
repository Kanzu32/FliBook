package com.kanzu.flibook;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class dataActivity extends AppCompatActivity {
    BookData book = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_activity);

        ImageView img = (ImageView) findViewById(R.id.coverImage);
        TextView name = (TextView) findViewById(R.id.nameField);
        TextView author = (TextView) findViewById(R.id.authorField);
        TextView genres = (TextView) findViewById(R.id.genresField);
        TextView description = (TextView) findViewById(R.id.descriptionField);
        Bundle arguments = getIntent().getExtras();

        book = arguments.getParcelable(BookData.class.getSimpleName());
        try {
            book = (BookData) Network.getDataTask(book).get();
        } catch (Exception e) { e.printStackTrace(); }

        name.setText(book.name);
        author.setText(book.author);
        genres.setText(book.genres);
        description.setText(book.description);
        if (book.hasCover) {
            img.setImageBitmap(book.img);
        } else {
            img.setVisibility(View.VISIBLE);
        }
    }

    public void downloadButtonOnClick (View view) throws InterruptedException, ExecutionException, IOException {
        Network.downloadTask(book, this, "fb2").get();
    }
}
