package main;

import html.HTMLCreator;

public class Invoice {
    private final String vendor;
    private final int index;
    private final double invoiceAmount;
    private final String category;

    @Override
    public String toString() {
        return String.format("Eintrag %d: Kategorie: %s Text: %s Preis: %.2f €%n", index, category, vendor, invoiceAmount);
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

    public String getCategory() {
        return category;
    }

    Invoice(int index, String category, String vendor, double value) {
        if (HTMLCreator.ABK_TEXT.get(category.toUpperCase()) == null) {
            this.vendor = vendor;
            this.category = "SONSTIGES"; // if category is not found in predefined list call it miscellaneous
        } else {
            this.vendor = vendor.replace("_", " ");
            this.category = category;
        }
        this.index = index;
        this.invoiceAmount = value;
    }

}
