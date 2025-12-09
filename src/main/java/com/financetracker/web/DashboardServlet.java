package com.financetracker.web;

import com.financetracker.model.Budget;
import com.financetracker.model.Summary;
import com.financetracker.model.Transaction;
import com.financetracker.service.FinanceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DashboardServlet extends HttpServlet {
    private final FinanceService financeService = FinanceService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Transaction> recentTransactions = financeService.getRecentTransactions(20);
        List<Budget> budgets = financeService.getBudgets();
        Summary summary = financeService.getSummary();
        Map<?, Summary> monthly = financeService.getMonthlySummaries();

        req.setAttribute("summary", summary);
        req.setAttribute("transactions", recentTransactions);
        req.setAttribute("budgets", budgets);
        req.setAttribute("monthly", monthly);

        req.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(req, resp);
    }
}

