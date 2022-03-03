package com.kanzu.flibook;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

// TODO write[.] getList[-] getBook[-] unzip[-]

public class Storage {
    static public Boolean write(InputStream data, String fileType, Context context) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            System.out.println("Хранилище недоступно!!!");
            return false;
        }

        File file = new File(context.getExternalFilesDir("books"), "filenameExternal123.zip");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file, true);
            int c;
            while ((c = data.read()) != -1) {
                System.out.println(c);
                outputStream.write(c);
            }

            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static public File unzip(File file, Context context) {
        return file;
    }
}
