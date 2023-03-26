package main;

import html.HTMLCreator;

public class Invoice {
    private final String vendor;
    private final int index;
    private final double invoiceAmount;


    private final String path;
    private final String departement;

    public String getPath() {
        return path;
    }
    @Override
    public String toString() {
        return String.format("Eintrag %d: Fachschaft: %s Firma: %s Preis: %.2f €%n", index, departement, vendor, invoiceAmount);
    }

    public int getIndex() {
        return index;
    }

    public String getVendor() {
        return vendor;
    }

    public double getInvoiceAmount() {
        return invoiceAmount;
    }

    Invoice(int index, String category, String vendor, double value, String location) {
        if (HTMLCreator.ABK_TEXT.get(category.toUpperCase()) == null) {
            this.vendor = vendor;
            this.departement = "SONSTIGES"; // if category is not found in predefined list call it miscellaneous
        } else {
            this.vendor = vendor.replace("_", " ");
            this.departement = category;
        }
        this.path = location;
        this.index = index;
        this.invoiceAmount = value;
    }

}
