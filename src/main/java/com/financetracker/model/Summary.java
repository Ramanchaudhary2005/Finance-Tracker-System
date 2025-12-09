package com.financetracker.model;

import java.util.Map;

public class Summary {
    private final double totalIncome;
    private final double totalExpense;
    private final double balance;
    private final Map<String, Double> categoryTotals;

    public Summary(double totalIncome, double totalExpense, Map<String, Double> categoryTotals) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = totalIncome - totalExpense;
        this.categoryTotals = categoryTotals;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public double getBalance() {
        return balance;
    }

    public Map<String, Double> getCategoryTotals() {
        return categoryTotals;
    }
}

