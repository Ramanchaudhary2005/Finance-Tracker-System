package com.financetracker.web;

import com.financetracker.service.FinanceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.YearMonth;

public class BudgetServlet extends HttpServlet {
    private final FinanceService financeService = FinanceService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String category = valueOrDefault(req.getParameter("category"), "General");
        double limit = parseDouble(req.getParameter("limit"));
        YearMonth month = parseMonth(req.getParameter("month"));

        if (limit > 0) {
            financeService.addBudget(category, limit, month);
        }

        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private YearMonth parseMonth(String value) {
        try {
            return YearMonth.parse(value);
        } catch (Exception e) {
            return YearMonth.now();
        }
    }

    private String valueOrDefault(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}

