import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

// Transaction class to represent each financial transaction
class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String type; // "income" or "expense"
    private double amount;
    private String category;
    private String description;
    private LocalDate date;
    private String paymentMethod; // "cash", "card", "upi", etc.
    private boolean recurring; // for recurring transactions
    private String tags; // comma-separated tags

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

    public String getId() { return id; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public String getPaymentMethod() { return paymentMethod; }
    public boolean isRecurring() { return recurring; }
    public String getTags() { return tags; }

    @Override
    public String toString() {
        return String.format("[%s] %s | ₹%.2f | %s | %s | %s | %s %s", 
            id, date, amount, type.toUpperCase(), category, description, 
            paymentMethod, recurring ? "(Recurring)" : "");
    }
}

// Budget class for budget management
class Budget implements Serializable {
    private static final long serialVersionUID = 1L;
    private String category;
    private double limit;
    private YearMonth month;

    public Budget(String category, double limit, YearMonth month) {
        this.category = category;
        this.limit = limit;
        this.month = month;
    }

    public String getCategory() { return category; }
    public double getLimit() { return limit; }
    public YearMonth getMonth() { return month; }
}

// Main Finance Tracker Application
public class FinanceTracker {
    private List<Transaction> transactions;
    private List<Budget> budgets;
    private Scanner scanner;
    private static final String DATA_FILE = "transactions.dat";
    private static final String BUDGET_FILE = "budgets.dat";

    public FinanceTracker() {
        transactions = new ArrayList<>();
        budgets = new ArrayList<>();
        scanner = new Scanner(System.in);
        loadTransactions();
        loadBudgets();
    }

    public void run() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   Advanced Finance Tracker v2.0        ║");
        System.out.println("╚════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addTransaction("income");
                    break;
                case 2:
                    addTransaction("expense");
                    break;
                case 3:
                    viewAllTransactions();
                    break;
                case 4:
                    viewSummary();
                    break;
                case 5:
                    viewByCategory();
                    break;
                case 6:
                    viewMonthlySummary();
                    break;
                case 7:
                    manageBudgets();
                    break;
                case 8:
                    searchTransactions();
                    break;
                case 9:
                    viewExpenseAnalysis();
                    break;
                case 10:
                    deleteTransaction();
                    break;
                case 11:
                    generateReport();
                    break;
                case 12:
                    saveData();
                    System.out.println("\n✓ All data saved successfully!");
                    running = false;
                    break;
                default:
                    System.out.println("\n✗ Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MAIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("1.  Add Income");
        System.out.println("2.  Add Expense");
        System.out.println("3.  View All Transactions");
        System.out.println("4.  View Summary");
        System.out.println("5.  View by Category");
        System.out.println("6.  View Monthly Summary");
        System.out.println("7.  Manage Budgets");
        System.out.println("8.  Search Transactions");
        System.out.println("9.  View Expense Analysis");
        System.out.println("10. Delete Transaction");
        System.out.println("11. Generate Report");
        System.out.println("12. Save & Exit");
        System.out.println("=".repeat(50));
    }

    private void addTransaction(String type) {
        System.out.println("\n--- Add " + type.toUpperCase() + " ---");
        
        double amount = getDoubleInput("Enter amount: ₹");
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        
        System.out.println("Payment methods: 1. Cash, 2. Card, 3. UPI, 4. Bank Transfer");
        int method = getIntInput("Select payment method (1-4): ");
        String paymentMethod = getPaymentMethod(method);
        
        System.out.print("Is this a recurring transaction? (y/n): ");
        boolean recurring = scanner.nextLine().toLowerCase().equals("y");
        
        System.out.print("Enter tags (comma-separated, optional): ");
        String tags = scanner.nextLine();
        
        Transaction t = new Transaction(type, amount, category, description, 
                                       LocalDate.now(), paymentMethod, recurring, tags);
        transactions.add(t);
        
        System.out.println("\n✓ " + type.toUpperCase() + " added successfully! ID: " + t.getId());
    }

    private String getPaymentMethod(int choice) {
        return switch (choice) {
            case 1 -> "Cash";
            case 2 -> "Card";
            case 3 -> "UPI";
            case 4 -> "Bank Transfer";
            default -> "Unknown";
        };
    }

    private void viewAllTransactions() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("ALL TRANSACTIONS");
        System.out.println("=".repeat(100));
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        transactions.stream()
            .sorted(Comparator.comparing(Transaction::getDate).reversed())
            .forEach(System.out::println);
        System.out.println("=".repeat(100));
        System.out.println("Total transactions: " + transactions.size());
    }

    private void viewSummary() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("FINANCIAL SUMMARY");
        System.out.println("=".repeat(50));

        double totalIncome = transactions.stream()
            .filter(t -> t.getType().equals("income"))
            .mapToDouble(Transaction::getAmount)
            .sum();

        double totalExpense = transactions.stream()
            .filter(t -> t.getType().equals("expense"))
            .mapToDouble(Transaction::getAmount)
            .sum();

        double balance = totalIncome - totalExpense;

        System.out.printf("Total Income:   ₹%.2f\n", totalIncome);
        System.out.printf("Total Expense:  ₹%.2f\n", totalExpense);
        System.out.println("-".repeat(50));
        System.out.printf("Balance:        ₹%.2f %s\n", 
            Math.abs(balance), 
            balance >= 0 ? "✓" : "✗");
        System.out.println("=".repeat(50));
    }

    private void viewByCategory() {
        System.out.println("\n--- View by Category ---");
        Map<String, Double> categoryMap = new HashMap<>();

        for (Transaction t : transactions) {
            categoryMap.put(t.getCategory(), 
                categoryMap.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
        }

        if (categoryMap.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("\nCategory-wise breakdown:");
        categoryMap.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .forEach(e -> System.out.printf("  %s: ₹%.2f\n", e.getKey(), e.getValue()));
    }

    private void viewMonthlySummary() {
        System.out.println("\n--- Monthly Summary ---");
        Map<YearMonth, Double> monthlyIncome = new HashMap<>();
        Map<YearMonth, Double> monthlyExpense = new HashMap<>();

        for (Transaction t : transactions) {
            YearMonth month = YearMonth.from(t.getDate());
            if (t.getType().equals("income")) {
                monthlyIncome.put(month, monthlyIncome.getOrDefault(month, 0.0) + t.getAmount());
            } else {
                monthlyExpense.put(month, monthlyExpense.getOrDefault(month, 0.0) + t.getAmount());
            }
        }

        monthlyIncome.keySet().stream()
            .sorted(Comparator.reverseOrder())
            .forEach(month -> {
                double income = monthlyIncome.getOrDefault(month, 0.0);
                double expense = monthlyExpense.getOrDefault(month, 0.0);
                System.out.printf("%s | Income: ₹%.2f | Expense: ₹%.2f | Net: ₹%.2f\n",
                    month, income, expense, income - expense);
            });
    }

    private void manageBudgets() {
        System.out.println("\n--- Budget Management ---");
        System.out.println("1. Set Budget");
        System.out.println("2. View Budgets");
        System.out.println("3. Check Budget Status");
        int choice = getIntInput("Enter choice: ");

        switch (choice) {
            case 1:
                setBudget();
                break;
            case 2:
                viewBudgets();
                break;
            case 3:
                checkBudgetStatus();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void setBudget() {
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        double limit = getDoubleInput("Enter budget limit: ₹");
        
        YearMonth month = YearMonth.now();
        budgets.removeIf(b -> b.getCategory().equals(category) && b.getMonth().equals(month));
        budgets.add(new Budget(category, limit, month));
        
        System.out.println("✓ Budget set successfully!");
    }

    private void viewBudgets() {
        if (budgets.isEmpty()) {
            System.out.println("No budgets set.");
            return;
        }
        
        System.out.println("\nCurrent Budgets:");
        budgets.forEach(b -> System.out.printf("  %s (%s): ₹%.2f\n", b.getCategory(), b.getMonth(), b.getLimit()));
    }

    private void checkBudgetStatus() {
        YearMonth currentMonth = YearMonth.now();
        
        Map<String, Double> currentExpenses = new HashMap<>();
        transactions.stream()
            .filter(t -> t.getType().equals("expense") && YearMonth.from(t.getDate()).equals(currentMonth))
            .forEach(t -> currentExpenses.put(t.getCategory(), 
                currentExpenses.getOrDefault(t.getCategory(), 0.0) + t.getAmount()));

        System.out.println("\nBudget Status for " + currentMonth + ":");
        budgets.stream()
            .filter(b -> b.getMonth().equals(currentMonth))
            .forEach(b -> {
                double spent = currentExpenses.getOrDefault(b.getCategory(), 0.0);
                double percentage = (spent / b.getLimit()) * 100;
                String status = percentage > 100 ? "✗ EXCEEDED" : percentage > 80 ? "⚠ WARNING" : "✓ OK";
                System.out.printf("  %s: ₹%.2f / ₹%.2f (%.1f%%) %s\n", 
                    b.getCategory(), spent, b.getLimit(), percentage, status);
            });
    }

    private void searchTransactions() {
        System.out.println("\n--- Search Transactions ---");
        System.out.println("1. By Category");
        System.out.println("2. By Date Range");
        System.out.println("3. By Amount Range");
        System.out.println("4. By Tags");
        int choice = getIntInput("Enter search type: ");

        List<Transaction> results = new ArrayList<>();

        switch (choice) {
            case 1:
                System.out.print("Enter category: ");
                String category = scanner.nextLine();
                results = transactions.stream()
                    .filter(t -> t.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
                break;
            case 2:
                LocalDate from = getDateInput("Enter from date (yyyy-MM-dd): ");
                LocalDate to = getDateInput("Enter to date (yyyy-MM-dd): ");
                results = transactions.stream()
                    .filter(t -> !t.getDate().isBefore(from) && !t.getDate().isAfter(to))
                    .collect(Collectors.toList());
                break;
            case 3:
                double minAmount = getDoubleInput("Enter minimum amount: ₹");
                double maxAmount = getDoubleInput("Enter maximum amount: ₹");
                results = transactions.stream()
                    .filter(t -> t.getAmount() >= minAmount && t.getAmount() <= maxAmount)
                    .collect(Collectors.toList());
                break;
            case 4:
                System.out.print("Enter tag: ");
                String tag = scanner.nextLine();
                results = transactions.stream()
                    .filter(t -> t.getTags().contains(tag))
                    .collect(Collectors.toList());
                break;
        }

        if (results.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            System.out.println("\nSearch Results (" + results.size() + "):");
            results.forEach(System.out::println);
        }
    }

    private LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                System.out.println("✗ Invalid date format. Use yyyy-MM-dd");
            }
        }
    }

    private void viewExpenseAnalysis() {
        System.out.println("\n--- Expense Analysis ---");
        
        Map<String, Double> expensesByCategory = transactions.stream()
            .filter(t -> t.getType().equals("expense"))
            .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));

        if (expensesByCategory.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        double totalExpense = expensesByCategory.values().stream().mapToDouble(Double::doubleValue).sum();
        
        System.out.println("\nExpense Distribution:");
        expensesByCategory.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .forEach(e -> {
                double percentage = (e.getValue() / totalExpense) * 100;
                int bars = (int) (percentage / 5);
                System.out.printf("  %s (%.1f%%) %s ₹%.2f\n", 
                    e.getKey(), percentage, "█".repeat(bars), e.getValue());
            });
    }

    private void deleteTransaction() {
        System.out.print("\nEnter transaction ID to delete: ");
        String id = scanner.nextLine();

        boolean removed = transactions.removeIf(t -> t.getId().equals(id));
        
        if (removed) {
            System.out.println("✓ Transaction deleted successfully!");
        } else {
            System.out.println("✗ Transaction not found.");
        }
    }

    private void generateReport() {
        System.out.println("\n--- Generate Report ---");
        System.out.println("1. Monthly Report");
        System.out.println("2. Category Report");
        System.out.println("3. Payment Method Report");
        int choice = getIntInput("Select report type: ");

        switch (choice) {
            case 1:
                generateMonthlyReport();
                break;
            case 2:
                generateCategoryReport();
                break;
            case 3:
                generatePaymentMethodReport();
                break;
        }
    }

    private void generateMonthlyReport() {
        System.out.print("Enter month (yyyy-MM): ");
        YearMonth month = YearMonth.parse(scanner.nextLine());
        
        List<Transaction> monthTransactions = transactions.stream()
            .filter(t -> YearMonth.from(t.getDate()).equals(month))
            .sorted(Comparator.comparing(Transaction::getDate))
            .collect(Collectors.toList());

        double income = monthTransactions.stream()
            .filter(t -> t.getType().equals("income"))
            .mapToDouble(Transaction::getAmount)
            .sum();
        
        double expense = monthTransactions.stream()
            .filter(t -> t.getType().equals("expense"))
            .mapToDouble(Transaction::getAmount)
            .sum();

        System.out.println("\n═══════════════════════════════════════");
        System.out.println("MONTHLY REPORT - " + month);
        System.out.println("═══════════════════════════════════════");
        System.out.printf("Total Income:    ₹%.2f\n", income);
        System.out.printf("Total Expense:   ₹%.2f\n", expense);
        System.out.printf("Net Balance:     ₹%.2f\n", income - expense);
        System.out.println("═══════════════════════════════════════");
    }

    private void generateCategoryReport() {
        Map<String, Double> categoryTotals = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));

        System.out.println("\n═══════════════════════════════════════");
        System.out.println("CATEGORY REPORT");
        System.out.println("═══════════════════════════════════════");
        categoryTotals.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .forEach(e -> System.out.printf("%-20s : ₹%.2f\n", e.getKey(), e.getValue()));
        System.out.println("═══════════════════════════════════════");
    }

    private void generatePaymentMethodReport() {
        Map<String, Double> methodTotals = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getPaymentMethod, Collectors.summingDouble(Transaction::getAmount)));

        System.out.println("\n═══════════════════════════════════════");
        System.out.println("PAYMENT METHOD REPORT");
        System.out.println("═══════════════════════════════════════");
        methodTotals.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .forEach(e -> System.out.printf("%-20s : ₹%.2f\n", e.getKey(), e.getValue()));
        System.out.println("═══════════════════════════════════════");
    }

    private void saveData() {
        saveTransactions();
        saveBudgets();
    }

    private void saveTransactions() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(transactions);
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    private void saveBudgets() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(BUDGET_FILE))) {
            oos.writeObject(budgets);
        } catch (IOException e) {
            System.out.println("Error saving budgets: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTransactions() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            transactions = (List<Transaction>) ois.readObject();
            System.out.println("✓ Loaded " + transactions.size() + " transactions from file.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Note: Starting with empty transaction list.");
        }
    }

    @SuppressWarnings("unchecked")
    private void loadBudgets() {
        File file = new File(BUDGET_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(BUDGET_FILE))) {
            budgets = (List<Budget>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Note: No budgets loaded.");
        }
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("✗ Please enter a valid number.");
            }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(scanner.nextLine());
                if (value < 0) {
                    System.out.println("✗ Amount cannot be negative.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("✗ Please enter a valid amount.");
            }
        }
    }

    public static void main(String[] args) {
        FinanceTracker tracker = new FinanceTracker();
        tracker.run();
    }
}