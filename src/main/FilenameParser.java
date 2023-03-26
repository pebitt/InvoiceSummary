package main;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FilenameParser {
    private static final String FILENAMEPATTERN = "[0-9][0-9][0-9]_5710_[^__].*";

    public Map<String, Department> getStringCategoryMap() {
        return stringCategoryMap;
    }

    private Map<String, Department> stringCategoryMap;
    private HashSet<String> categorySet;
    private String path;

    private static final int INVOICE_INDEX = 0;
    private static final int INVOICE_TYPE = 1;
    private static final int INVOICE_CATEGORY = 2;
    private static final int INVOICE_VENDOR = 3;

    FilenameParser(String location) {
        categorySet = new HashSet<>();
        stringCategoryMap = new HashMap<>();
        path = location;
    }
    public HashSet<String> getCategorySet() {
        return categorySet;
    }
    void parseDirectory()  {
        File workingDirectory = new File(path);

        String[] filteredList = createFilteredFileList(workingDirectory);

        for (String currentFileName : filteredList) {
            try {
                if (checkFileType(Files.probeContentType(
                        new File(currentFileName).toPath()))) {

                    String[] invoiceInfo = parseInvoiceDetails(currentFileName);
                    Double price = parseInvoicePrice(currentFileName);

                    if(invoiceInfo[INVOICE_TYPE].equals("5710")) {
                        Department currentCategory;
                        invoiceInfo[INVOICE_CATEGORY] = invoiceInfo[INVOICE_CATEGORY].toUpperCase();

                        if (invoiceInfo.length > 3) { // expected length
                            // if a new category is found it needs a new entry in map
                            if ((currentCategory = stringCategoryMap.get(invoiceInfo[INVOICE_CATEGORY])) == null) {
                                categorySet.add(invoiceInfo[INVOICE_CATEGORY]);

                                currentCategory = new Department(invoiceInfo[INVOICE_CATEGORY]);
                                stringCategoryMap.put(invoiceInfo[INVOICE_CATEGORY], currentCategory);
                            }

                            currentCategory.addInvoice(new Invoice(Integer.parseInt(invoiceInfo[INVOICE_INDEX]),
                                    invoiceInfo[INVOICE_CATEGORY],
                                    invoiceInfo[INVOICE_VENDOR],
                                    price));

                        } else { // length >= 3 means no category is described, so use miscelleanous category
                                if((currentCategory = stringCategoryMap.get("SONSTIGES")) == null){
                                    currentCategory = new Department("SONSTIGES");
                                    stringCategoryMap.put("SONSTIGES", currentCategory);
                                }
                            currentCategory.addInvoice(new Invoice(Integer.parseInt(invoiceInfo[INVOICE_INDEX]),
                                    "SONSTIGES",
                                    invoiceInfo[2], // here vendor info
                                    price));
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String[] parseInvoiceDetails(String currentFileName) {
        String details = currentFileName.split("__")[0];

        return details.split("_",4);
    }

    private static String[] createFilteredFileList(File directory) {
        return directory.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.matches(FILENAMEPATTERN))
                    return true;
                else
                    return false;
            }
        });
    }

    private boolean checkFileType(String probeContentType) {
        return probeContentType.contains("pdf") ? true : false;
    }

    private Double parseInvoicePrice(String priceTag) {
        // cut off the filetype at the second half of the string the end
        String price = priceTag.split("__")[1].split("\\.pdf") [0];

        try { // replace comma with dot for parsing to double
            return Double.parseDouble(price.replace(",", "."));
        }
        catch (Exception e){
            e.printStackTrace();
            return 0.0;
        }
    }
}
