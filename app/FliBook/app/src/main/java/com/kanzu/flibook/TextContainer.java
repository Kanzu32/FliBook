package com.kanzu.flibook;

public class TextContainer {
    String text = "";
    void add(String str) {
        if (text.length() == 0) {
            text = str;
        } else {
            text += " " + str;
        }

    }
}
