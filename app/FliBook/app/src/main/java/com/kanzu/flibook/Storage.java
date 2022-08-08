package com.kanzu.flibook;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import com.adobe.dp.epub.io.OCFContainerWriter;
import com.adobe.dp.epub.opf.Publication;
import com.adobe.dp.fb2.FB2Document;
import com.adobe.dp.fb2.convert.Converter;
import com.kanzu.flibook.ui.fromfile.FromFileFragment;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Storage {
    static public Boolean write(InputStream data, BookData book, String type, Context context) throws IOException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            System.out.println("Хранилище недоступно!!!");
            return false;
        }

        if (type.equals("epub") || type.equals("pdf")) {
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

        if (type.equals("fb2")) {

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
            File file = new File("");
            while (zipEntry != null) {
                file = new File(context.getExternalFilesDir("books"), book.name + "." + type);
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
            String name = file.getName();
            FromFileFragment.convertFile(file, name.substring(0, name.lastIndexOf('.')), context);

        }

        return true;
    }

    static public ArrayList<File> scanBooksTask(Context context) {
        File file = context.getExternalFilesDir("books");
        return new ArrayList<File>(Arrays.asList(file.listFiles()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    static public Path unzip(File name, Context context) throws IOException {
        Path source = name.toPath();
        Path target = new File(context.getExternalFilesDir("tmp"), name.getName()).toPath();
        new ZipFile(source.toFile())
                .extractAll(target.toString());
        return target;
    }

    static public void zip(File file, String newFilename, Context context) throws IOException {
        File zipFile = new File(context.getExternalFilesDir("books"), newFilename + ".epub");
        if (zipFile.exists()) {
            zipFile.delete();
        }

        ZipParameters parameters = new ZipParameters();
        parameters.setIncludeRootFolder(false);
        new ZipFile(zipFile).addFolder(file, parameters);
    }

    public static void update(File file) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String oldContent = "";
        String line = reader.readLine();

        while (line != null)
        {
            oldContent = oldContent + line + System.lineSeparator();
            line = reader.readLine();
        }

        String newContent = "";
        newContent = oldContent.replaceAll("<dcns:", "<dc:");
        newContent = newContent.replaceAll("</dcns:", "</dc:");

        FileWriter writer = new FileWriter(file);
        writer.write(newContent);
        reader.close();
        writer.close();
    }

    public static void delete(File file) {
        file.delete();
    }

    public static void rename(File file, String newName) {
        file.renameTo(new File(file.getParentFile(), newName));
    }

}
