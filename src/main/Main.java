package main;

import html.HTMLCreator;


import java.awt.*;
import java.io.*;

public class Main {
    public static void main(String args[]) throws IOException {
        FilenameParser fileNameParser = new FilenameParser(System.getProperty("user.dir"));
        fileNameParser.parseDirectory();

        HTMLCreator htmlCreator = new HTMLCreator(fileNameParser.getStringDepartmentSet(),
                fileNameParser.getStringDepartmentMap());

        htmlCreator.buildHtmlPage();
        htmlCreator.writeHTMLFile("Budget.html");

        System.out.println("Analyse komplett! Öffne Html-Datei...");
        // open html on default browser
        Desktop.getDesktop().browse(new File("Budget.html").toURI());
    }

}
