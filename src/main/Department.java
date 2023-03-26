package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


public class Department {
    private final String category;
    private double totalCategoryAmount = 0.0f;
    private double remainingBudget = 0.0f;

    public double getBudget() {
        return budget;
    }
    private double budget;

    private ArrayList<Invoice> invoiceList;

    public double getDifference() {
        return remainingBudget;
    }

    static final Map<String, Double> BUDGETMAP = Arrays.stream(new Object[][]{
            { "GEO", 900.00 },
            { "L", 300.00 },
            { "M", 800.00 },
            { "S", 300.00},
            { "F", 500.00},
            { "INF", 500.00 },
            { "G", 1000.00 },
            { "PSY", 0.00 },
            { "E", 800.00 },
            { "SP", 300.00 },
            { "IT", 0.00 },
            { "ETH", 300.00 },
            { "C", 3000.00 },
            { "K", 300.00 },
            { "EV", 300.00 },
            { "PH", 1000.00 },
            { "MU", 1000.00 },
            { "B", 500.00 },
            { "D", 500.00 },
            { "WR", 600.00 },
            { "NT", 1000.00 },
            { "KU", 1000.00 },
    }).collect(Collectors.toMap(kv -> (String) kv[0], kv -> (Double) kv[1]));

    public String getCategory() {
        return category;
    }

    public double getTotalCategoryAmount() {
        return totalCategoryAmount;
    }

    public ArrayList<Invoice> getInvoiceList() {
        return invoiceList;
    }

    Department(String category) {
        Double b = BUDGETMAP.get(category);
        if (b != null) {
            budget = b.doubleValue();
        } else
            budget = 0.0f;

        this.category = category;
        invoiceList = new ArrayList<>();
    }

    public void addInvoice(Invoice fd){
        invoiceList.add(fd);
        totalCategoryAmount += fd.getInvoiceAmount();
        remainingBudget = budget - totalCategoryAmount;
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();

        s.append(String.format("Gesamtsumme Ausgaben: %.2f € %nEinzelposten:%n%n", totalCategoryAmount));

        for (Invoice currentInvoice : invoiceList)
            s.append(currentInvoice.toString());

        return s.toString();
    }
}
