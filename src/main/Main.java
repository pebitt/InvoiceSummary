package main;

import html.HTMLCreator;


import java.io.*;

public class Main {
    public static void main(String args[]) throws IOException {
        FilenameParser fileNameParser = new FilenameParser(System.getProperty("user.dir"));
        fileNameParser.parseDirectory();
        System.out.println("Analyse komplett! Öffne Html-Datei...");

        HTMLCreator htmlCreator = new HTMLCreator(fileNameParser.getCategorySet(),
                fileNameParser.getStringCategoryMap());

        htmlCreator.buildHtmlPage();
        htmlCreator.outputHTML();
    }

}
