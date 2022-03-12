package com.kanzu.flibook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Network{
    static private final String siteURL = "https://flibusta.is/";
    static private final String bookList = "https://flibusta.is/makebooklist"; //DON'T USE??
    static private final String proxyHost = "proxy-nossl.antizapret.prostovpn.org";
    static private final int proxyPort = 29976;

    static private Document doc;
    public Network() throws IOException {}

    // TODO download[+], search[+], bookData[+], onlineRead??

    static public FutureTask getPageCountTask(String name) {
        Callable task = () -> {
            int count = 0;
            ArrayList<BookData> result = new ArrayList<BookData>();
            Connection jsoupConnection = Jsoup.connect("https://flibusta.is/makebooklist?ab=ab1&sort=rating&t=" + name + "&page=1999")
                    .proxy(proxyHost, proxyPort)
                    .followRedirects(true);

            doc = jsoupConnection.get();
            Elements items = doc.select(".item-list");
            if (items.size() == 0) {
                count = 1;
            } else {
                count = Integer.parseInt(items.first().select(".pager > .last").text());
            }
            return count;
        };
        FutureTask<String> future = new FutureTask<>(task);
        new Thread(future).start();
        return future;
    }

    static public FutureTask searchTask(String name, int pageNumber) throws IOException, ExecutionException, InterruptedException {
        Callable task = () -> {
            System.setProperty("http.proxyHost", "proxy-ssl.antizapret.prostovpn.org");
            System.setProperty("http.proxyPort", "3143");
            ArrayList<BookData> result = new ArrayList<BookData>();
            Connection jsoupConnection = Jsoup.connect("https://flibusta.is/makebooklist?ab=ab1&sort=rating&t=" + name + "&page=" + (pageNumber-1))
                    .proxy(proxyHost, proxyPort)
                    .followRedirects(true);

            doc = jsoupConnection.get();
            Elements items = doc.select("form > div");

            String prevGenres = "";

            for (Element item : items) {
                String genres;
                String bookName, author = "";
                int id;
                // GENRES
                try {
                    if (item.select("div > .genre").size() == 0) {
                        genres = prevGenres;
                    } else {
                        genres = item.select("div > .genre").text();
                    }
                } catch (NullPointerException e) {
                    genres = prevGenres;
                }
                // NAME, AUTHOR, ID
                Element el = item.select("div > a").get(0);
                bookName = el.text();
                id = Integer.parseInt(el.attr("href").substring(3));
                for (Element i : item.select("[href^=/a/]")) {
                    author += i.text() + ", ";
                }
                author = author.substring(0, author.length() - 2);
                result.add(new BookData(id, bookName, author, genres));
                prevGenres = genres;

            }
            return result;
        };
        FutureTask<String> future = new FutureTask<>(task);
        new Thread(future).start();
        return future;

    }

    static public FutureTask getDataTask(BookData book) throws IOException, ExecutionException, InterruptedException {
        Callable task = () -> {
            doc = Jsoup.connect(siteURL+"/b/"+book.id).proxy(proxyHost, proxyPort).get();
            doc = Jsoup.connect(siteURL+"/b/"+book.id).proxy(proxyHost, proxyPort).get();
            Element item = doc.select("h2:contains(Аннотация) + p").first();
            String description;
            ArrayList<String> downloadTypes = new ArrayList<String>();
            Bitmap bitmap;

            // DESCRIPTION
            try {
                description = item.text();
                book.description = description;
            }
            catch (NullPointerException e) {description = "Описание отсутствует.";}

            // DOWNLOAD FORMATS ( only fb2 supported right now!! )
            if (doc.select("a:contains(fb2)").size() > 0) {
                downloadTypes.add("fb2");
            }
            if (doc.select("a:contains(epub)").size() > 0) {
                downloadTypes.add("epub");
            }
            book.downloadTypes = downloadTypes;

            // IMAGE

            Elements imgs = doc.select("img[title=Cover image]");
            if (imgs.size() > 0) {
                InetSocketAddress sa = new InetSocketAddress(proxyHost, proxyPort);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);
                String imgPath = imgs.first().attr("src");
                HttpURLConnection connection = (HttpURLConnection) new URL(siteURL + imgPath).openConnection(proxy);
                bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                book.hasCover = true;
                book.img = bitmap;

            } else {
                book.hasCover = false;
            }
            return book;
        };
        FutureTask<String> future = new FutureTask<>(task);
        new Thread(future).start();
        return future;
    }

    static public FutureTask downloadTask(BookData book, Context context, String type) throws IOException, ExecutionException, InterruptedException {
        Callable task = () -> {
            InetSocketAddress sa = new InetSocketAddress(proxyHost, proxyPort);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);
            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection connection = (HttpURLConnection)new URL(siteURL+"/b/"+book.id+"/"+type).openConnection(proxy);
            while (connection.getResponseCode() == 302) {
                connection = (HttpURLConnection)new URL(connection.getHeaderField("Location")).openConnection(proxy);
            }
            InputStream in = connection.getInputStream();
            return Storage.write(in, book, type, context);
        };
        FutureTask<String> future = new FutureTask<>(task);
        new Thread(future).start();
        return future;

    }


}
