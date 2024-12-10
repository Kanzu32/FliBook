package com.kanzu.flibook;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.kanzu.flibook.ui.fromfile.FromFileFragment;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MyThread implements Runnable{

    BookData book;
    Activity context;
    String type;
    public MyThread(BookData book, Activity context, String type) {
        this.book = book;
        this.context = context;
        this.type = type;
    }

    @Override
    public void run() {
        try {
            Network.downloadTask(book, context, type);
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Скачивание закончилось.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("FileError")) {
                try {
                    Network.downloadTask(book, context, "fb2");
                    Storage.delete(new File(context.getExternalFilesDir("books"), book.name + ".fb2"));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Скачивание закончилось.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
