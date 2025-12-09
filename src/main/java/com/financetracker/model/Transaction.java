package com.financetracker.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String type; // income or expense
    private final double amount;
    private final String category;
    private final String description;
    private final LocalDate date;
    private final String paymentMethod;
    private final boolean recurring;
    private final String tags;

    public Transaction(String type, double amount, String category, String description,
                       LocalDate date, String paymentMethod, boolean recurring, String tags) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.recurring = recurring;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public String getTags() {
        return tags;
    }
}

