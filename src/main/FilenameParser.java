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

    public Map<String, Department> getStringDepartmentMap() {
        return stringDepartmentMap;
    }

    private Map<String, Department> stringDepartmentMap;
    private HashSet<String> stringDepartmentSet;
    private final String path;

    private static final int INDEX = 0;
    private static final int CATEGORY = 1;
    private static final int DEPARTMENT = 2;
    private static final int VENDOR = 3;

    FilenameParser(String location) {
        stringDepartmentSet = new HashSet<>();
        stringDepartmentMap = new HashMap<>();
        path = location;
    }
    public HashSet<String> getStringDepartmentSet() {
        return stringDepartmentSet;
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
                    String location = path + "\\" + currentFileName;
                    if(invoiceInfo[CATEGORY].equals("5710")) {
                        Department currentDepartment;
                        invoiceInfo[DEPARTMENT] = invoiceInfo[DEPARTMENT].toUpperCase();

                        if (invoiceInfo.length > 3) { // expected length
                            // if a new category is found it needs a new entry in map
                            if ((currentDepartment = stringDepartmentMap.get(invoiceInfo[DEPARTMENT])) == null) {
                                stringDepartmentSet.add(invoiceInfo[DEPARTMENT]);

                                currentDepartment = new Department(invoiceInfo[DEPARTMENT]);
                                stringDepartmentMap.put(invoiceInfo[DEPARTMENT], currentDepartment);
                            }

                            currentDepartment.addInvoice(new Invoice(Integer.parseInt(invoiceInfo[INDEX]),
                                    invoiceInfo[DEPARTMENT],
                                    invoiceInfo[VENDOR],
                                    price,
                                    location));

                        } else { // length >= 3 means no category is described in filename, so use miscelleanous category
                                if((currentDepartment = stringDepartmentMap.get("SONSTIGES")) == null){
                                    currentDepartment = new Department("SONSTIGES");
                                    stringDepartmentMap.put("SONSTIGES", currentDepartment);
                                }
                            currentDepartment.addInvoice(new Invoice(Integer.parseInt(invoiceInfo[INDEX]),
                                    "SONSTIGES",
                                    invoiceInfo[2], // here vendor info
                                    price,
                                    location));
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
