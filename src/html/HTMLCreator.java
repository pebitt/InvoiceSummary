package html;

import main.Department;
import main.Invoice;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class HTMLCreator {
    private StringBuilder htmlCode;
    private StringBuilder misc;
    private static final String PATH_SKELETON = "src\\resources\\skeleton.html";
    private Map<String, Department> categoryMap;
    private HashSet<String> categorySet;
    public static final Map<String, String> ABK_TEXT =
            Arrays.stream(new String[][] {
                    { "GEO", "Geographie" },
                    { "L", "Latein" },
                    { "M", "Mathematik" },
                    { "SP", "Spanisch" },
                    { "F", "Französisch" },
                    { "INF", "Informatik" },
                    { "G", "Geschichte und Sozialkunde" },
                    { "PSY", "Schulpsychologie und Beratung" },
                    { "E", "Englisch" },
                    { "S", "Sport" },
                    { "IT", "Hardware und audiovisuelle Medien" },
                    { "ETH", "Ethik" },
                    { "C", "Chemie" },
                    { "K", "Katholische Religion" },
                    { "EV", "Evangelische Religion" },
                    { "PH", "Pysik" },
                    { "MU", "Musik" },
                    { "B", "Biologie" },
                    { "D", "Deutsch" },
                    { "WR", "Wirtschaft & Recht" },
                    { "NT", "Natur & Technik" },
                    { "KU", "Kunst" },

            }).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

    public HTMLCreator(HashSet<String> categorySet, Map<String, Department> categoryMap) {

        this.categorySet = categorySet;
        this.categoryMap = categoryMap;

        misc = new StringBuilder();
        htmlCode = new StringBuilder();
    }
    public void buildHtmlPage(){
        createHtmlSkeleton(PATH_SKELETON);

        // inject tables for all departments into the html code
        for (String currentCategoryShortcut : categorySet) {
            Department currentCategory = categoryMap.get(currentCategoryShortcut);
            if (ABK_TEXT.get(currentCategoryShortcut) != null)
                injectHtmlSnippet(createHtmlTable(currentCategory), "</div>");
        }
        Department f = categoryMap.get("SONSTIGES");
        if (f != null)
            injectHtmlSnippet(createHtmlTable(f), "</div>");
    }

    private void createHtmlSkeleton(String pathSkeleton)  {
        File skeletonHTMLFile = new File(pathSkeleton);
        String codeLine;
        BufferedReader reader;

        // read from skeleton.html
        try {
            reader = new BufferedReader(new FileReader(skeletonHTMLFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            while((codeLine = reader.readLine()) != null)
                htmlCode.append(codeLine);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // add time stamp
        SimpleDateFormat dtf = new SimpleDateFormat("dd. MMMM yyyy");
        Calendar calendar = Calendar.getInstance();

        Date dateObj = calendar.getTime();
        String formattedDate = dtf.format(dateObj);
        injectHtmlSnippet(" vom " + formattedDate, "</h1>");

    }

    public String createHtmlTable(Department category) {
        StringBuilder page = new StringBuilder();

        if (category.getDepartment().equals("SONSTIGES")) {
            page.append("<br><br><h3> Sonstige Rechnungen der Stelle 5710</h3>");

            page.append("<table style=\"width:100%\"> ");
            page.append("  <tr>" +
                    "    <th style= \"width:10%\">Rechnungs-Nr.</th>\n" +
                    "    <th style= \"width:70%\">Firma</th>\n" +
                    "    <th>Rechnungsbetrag</th>\n" +
                    "  </tr>");

            page.append("<p>Gesamtausgaben: " +
                    String.format("%.2f", Math.round(category.getTotalCategoryAmount() * 100.0) / 100.0) +
                    " €&ensp; </p>");

        } else { // any other category
            page.append("<br>");
            page.append("<h3>Fachschaft " +
                    ABK_TEXT.get(category.getDepartment()) +
                    " (" +
                    category.getDepartment() +
                    ")</h3>"
            );

            page.append("<p>Vorgesehenes Budget: " +
                    String.format("%.2f", Math.round(category.getBudget() * 100.0) / 100.0) +
                    " €&ensp; <br>");

            page.append("Gesamtausgaben: " +
                    String.format("%.2f", Math.round(category.getTotalCategoryAmount() * 100.0) / 100.0) +
                    " €&ensp; <br>");

            page.append("<mark>Kontostand: " +
                    String.format("%.2f", Math.round(category.getDifference() * 100.0) / 100.0) +
                    " €</p></mark>");

            page.append("<h2> Einzelposten in Tabellenform: </h2> ");
            page.append("<table> ");
            page.append("  <tr>" +
                    "    <th style= \"width:10%\">Rechnungs-Index</th>\n" +
                    "    <th style= \"width:70%\"><b>Firma</b></th>\n" +
                    "    <th><b>Rechnungsbetrag</b></th>\n" +
                    "  </tr>");
        }
        // append the tables rows
        for (Invoice currentInvoice : category.getInvoiceList()) {
            page.append(createTableRowHTML(currentInvoice));
        }

        page.append("</table>");

        return page.toString();
    }
    private String createTableRowHTML(Invoice invoice){
        return new StringBuilder().append("  <tr>" +
                        "    <td><a href=\"File:\\" + invoice.getPath() + "\"target=\"_blank\">" + invoice.getIndex() + "</td></a>\n" +
                        "    <td>" + invoice.getVendor() + "</td>\n" +
                        "    <td>" + String.format("%.2f", invoice.getInvoiceAmount()) +" €</td>\n" +
                        "  </tr>")
                .toString();
    }
    // inject new code to the codePage right before stringOffset
    private void injectHtmlSnippet(String code, String stringOffset) {
        int offset;

        offset = htmlCode.indexOf(stringOffset);
        htmlCode.insert(offset, code);
    }

   public void writeHTMLFile(String path) throws IOException {
       FileWriter fstream = new FileWriter(path);
       BufferedWriter out = new BufferedWriter(fstream);
       out.write(htmlCode.toString());
       out.close();
    }
}