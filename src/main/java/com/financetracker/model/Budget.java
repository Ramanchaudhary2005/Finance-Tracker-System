package com.financetracker.model;

import java.io.Serializable;
import java.time.YearMonth;

public class Budget implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String category;
    private final double limit;
    private final YearMonth month;

    public Budget(String category, double limit, YearMonth month) {
        this.category = category;
        this.limit = limit;
        this.month = month;
    }

    public String getCategory() {
        return category;
    }

    public double getLimit() {
        return limit;
    }

    public YearMonth getMonth() {
        return month;
    }
}

