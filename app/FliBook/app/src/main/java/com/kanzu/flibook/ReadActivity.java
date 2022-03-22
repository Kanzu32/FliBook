package com.kanzu.flibook;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kursx.parser.fb2.Body;
import com.kursx.parser.fb2.Element;
import com.kursx.parser.fb2.FictionBook;
import com.kursx.parser.fb2.Section;

import org.jsoup.select.Elements;

import java.io.File;

public class ReadActivity extends AppCompatActivity {
    TextContainer text = new TextContainer();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_activity);
        Context context = getApplicationContext();
        File file = new File(context.getExternalFilesDir("books"), "Преступление и наказание. Идиот.fb2");
        TextView field = (TextView) findViewById(R.id.readField);
        try {
            FictionBook fictionBook = new FictionBook(file);
            text.add(fictionBook.getTitle());
            nodeParse(fictionBook.getBody());
            field.setText(text.text);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void nodeParse(Body body) {
        for (Section item : body.getSections()) {
            if (item.getSections().size() == 0) {
                for (Element content : item.getElements()) {
                    text.add(content.getText());
                }
            } else {
                for (Section i : body.getSections()) {
                    nodeParse(i);
                }
            }
        }
    }

    void nodeParse(Section body) {
        for (Section item : body.getSections()) {
            if (item.getSections().size() == 0) {
                for (Element content : item.getElements()) {
                    text.add(content.getText());
                }
            } else {
                for (Section i : body.getSections()) {
                    nodeParse(i);
                }
            }
        }
    }

}
