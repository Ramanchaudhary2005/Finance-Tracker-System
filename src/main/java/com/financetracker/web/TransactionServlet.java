package com.financetracker.web;

import com.financetracker.service.FinanceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

public class TransactionServlet extends HttpServlet {
    private final FinanceService financeService = FinanceService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String type = req.getParameter("type");
        double amount = parseDouble(req.getParameter("amount"));
        String category = valueOrDefault(req.getParameter("category"), "General");
        String description = valueOrDefault(req.getParameter("description"), "");
        String paymentMethod = valueOrDefault(req.getParameter("paymentMethod"), "Unknown");
        boolean recurring = "on".equalsIgnoreCase(req.getParameter("recurring"));
        String tags = valueOrDefault(req.getParameter("tags"), "");

        LocalDate date = parseDate(req.getParameter("date"));

        if (amount > 0 && ("income".equalsIgnoreCase(type) || "expense".equalsIgnoreCase(type))) {
            financeService.addTransaction(type.toLowerCase(), amount, category, description, date, paymentMethod, recurring, tags);
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

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private String valueOrDefault(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}

