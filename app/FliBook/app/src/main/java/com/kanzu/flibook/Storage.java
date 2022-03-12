package com.kanzu.flibook;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// TODO write[+] getList[-] getBook[-]

public class Storage {
    static public Boolean write(InputStream data, BookData book, String type, Context context) throws IOException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            System.out.println("Хранилище недоступно!!!");
            return false;
        }

        if (type == "epub") {
            File file = new File(context.getExternalFilesDir("books"), book.name + "." + type);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file, false);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = data.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        if (type == "fb2") {

            File zipFile = new File(context.getExternalFilesDir("books"), "temp.zip");
            if (!zipFile.exists()) {
                zipFile.getParentFile().mkdirs();
            }
            FileOutputStream outputStream = null;
            try {
                zipFile.createNewFile();
                outputStream = new FileOutputStream(zipFile, false);
                int c;
                while ((c = data.read()) != -1) {
                    outputStream.write(c);
                }
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            ZipInputStream zipInput = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry = zipInput.getNextEntry();
            while (zipEntry != null) {
                File file = new File(context.getExternalFilesDir("books"), book.name + "." + type);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                }
                try {
                    file.createNewFile();
                    outputStream = new FileOutputStream(file, false);
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = zipInput.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, len);
                    }
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                zipEntry = zipInput.getNextEntry();
            }
            zipInput.close();
            zipFile.delete();
        }

        return true;
    }

    static public ArrayList<File> scanBooksTask(Context context) {
        File file = context.getExternalFilesDir("books");
        return new ArrayList<File>(Arrays.asList(file.listFiles()));
    }

}
