package com.kanzu.flibook;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kursx.parser.fb2.Body;
import com.kursx.parser.fb2.Element;
import com.kursx.parser.fb2.Epigraph;
import com.kursx.parser.fb2.FictionBook;
import com.kursx.parser.fb2.Section;
import com.kursx.parser.fb2.Subtitle;

import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;

public class ReadActivity extends AppCompatActivity {
    TextContainer text = new TextContainer();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_activity);
        Context context = getApplicationContext();
        String name = getIntent().getExtras().getString("name");
        File file = new File(context.getExternalFilesDir("books"), name);
        TextView field = (TextView) findViewById(R.id.readField);
        field.clearComposingText();
        try {
            FictionBook fictionBook = new FictionBook(file);

            text.add(fictionBook.getDescription().getSrcTitleInfo().getBookTitle());
            for (Section sec: fictionBook.getBody().getSections()) {
                nodeParse(sec);
            }
            TextView textField = (TextView) findViewById(R.id.readField);
            textField.setMovementMethod(new ScrollingMovementMethod());
            textField.setText(text.text);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void nodeParse(Section node) {
        ArrayList<Section> sections = node.getSections();
        if (sections.size() == 0) {

            if (node.getAnnotation() != null) {
                for (Element annotation : node.getAnnotation().getElements()) {
                    text.add(annotation.getText());
                }
            }
            if (node.getEpigraphs() != null) {
                for (Epigraph epigraph : node.getEpigraphs()) {
                    for (Element el : epigraph.getElements()) {
                        text.add(el.getText());
                    }
                }
            }
            if (node.getImage() != null) {
                ;
            }
            for (Element el : node.getElements()) {
                text.add(el.getText());
            }
        } else {
            for (int i = 0; i < sections.size(); i++) {
                nodeParse(sections.get(i));
            }
        }
    }

}
