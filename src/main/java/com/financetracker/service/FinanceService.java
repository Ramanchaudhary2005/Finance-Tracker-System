package com.financetracker.service;

import com.financetracker.model.Budget;
import com.financetracker.model.Summary;
import com.financetracker.model.Transaction;
import com.financetracker.serialization.LocalDateAdapter;
import com.financetracker.serialization.YearMonthAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Simple in-memory store with JSON persistence for transactions and budgets.
 * Thread-safe for servlet usage.
 */
public class FinanceService {
    private static final FinanceService INSTANCE = new FinanceService();

    private final Path transactionsFile = Path.of("transactions.json");
    private final Path budgetsFile = Path.of("budgets.json");
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Gson gson;

    private List<Transaction> transactions = new ArrayList<>();
    private List<Budget> budgets = new ArrayList<>();

    private FinanceService() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(YearMonth.class, new YearMonthAdapter())
                .setPrettyPrinting()
                .create();
        loadData();
    }

    public static FinanceService getInstance() {
        return INSTANCE;
    }

    public List<Transaction> getRecentTransactions(int limit) {
        lock.readLock().lock();
        try {
            return transactions.stream()
                    .sorted(Comparator.comparing(Transaction::getDate).reversed())
                    .limit(limit)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Budget> getBudgets() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(budgets);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Summary getSummary() {
        lock.readLock().lock();
        try {
            double totalIncome = transactions.stream()
                    .filter(t -> "income".equalsIgnoreCase(t.getType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            double totalExpense = transactions.stream()
                    .filter(t -> "expense".equalsIgnoreCase(t.getType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            Map<String, Double> categoryTotals = transactions.stream()
                    .collect(Collectors.groupingBy(Transaction::getCategory,
                            Collectors.summingDouble(Transaction::getAmount)));

            return new Summary(totalIncome, totalExpense, categoryTotals);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<YearMonth, Summary> getMonthlySummaries() {
        lock.readLock().lock();
        try {
            Map<YearMonth, List<Transaction>> grouped = transactions.stream()
                    .collect(Collectors.groupingBy(t -> YearMonth.from(t.getDate())));

            Map<YearMonth, Summary> summaries = new HashMap<>();
            grouped.forEach((month, txs) -> {
                double income = txs.stream()
                        .filter(t -> "income".equalsIgnoreCase(t.getType()))
                        .mapToDouble(Transaction::getAmount)
                        .sum();
                double expense = txs.stream()
                        .filter(t -> "expense".equalsIgnoreCase(t.getType()))
                        .mapToDouble(Transaction::getAmount)
                        .sum();
                Map<String, Double> cats = txs.stream()
                        .collect(Collectors.groupingBy(Transaction::getCategory,
                                Collectors.summingDouble(Transaction::getAmount)));
                summaries.put(month, new Summary(income, expense, cats));
            });
            return summaries;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addTransaction(String type, double amount, String category, String description,
                               LocalDate date, String paymentMethod, boolean recurring, String tags) {
        Transaction tx = new Transaction(type, amount, category, description, date, paymentMethod, recurring, tags);
        lock.writeLock().lock();
        try {
            transactions.add(tx);
            saveTransactions();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addBudget(String category, double limit, YearMonth month) {
        Budget budget = new Budget(category, limit, month);
        lock.writeLock().lock();
        try {
            budgets.removeIf(b -> b.getCategory().equalsIgnoreCase(category) && b.getMonth().equals(month));
            budgets.add(budget);
            saveBudgets();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void loadData() {
        lock.writeLock().lock();
        try {
            transactions = readList(transactionsFile, new TypeToken<List<Transaction>>() {}.getType());
            budgets = readList(budgetsFile, new TypeToken<List<Budget>>() {}.getType());
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void saveTransactions() {
        writeList(transactionsFile, transactions);
    }

    private void saveBudgets() {
        writeList(budgetsFile, budgets);
    }

    private <T> List<T> readList(Path file, Type type) {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(file)) {
            List<T> data = gson.fromJson(reader, type);
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private <T> void writeList(Path file, List<T> data) {
        try (Writer writer = Files.newBufferedWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            // Swallow to keep UI responsive; in production log this.
        }
    }
}

