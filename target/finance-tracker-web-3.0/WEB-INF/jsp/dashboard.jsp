<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Finance Tracker</title>
    <link rel="stylesheet" href="<c:url value='/static/style.css'/>">
</head>
<body>
<div class="container">
    <header>
        <div>
            <h1>Finance Tracker</h1>
            <p class="muted">Track income, expenses, and budgets from the browser.</p>
        </div>
        <div class="pill">Web Edition</div>
    </header>

    <section class="grid cards">
        <div class="card">
            <p class="muted">Total Income</p>
            <h2 class="text-green">₹<c:out value="${summary.totalIncome}"/></h2>
        </div>
        <div class="card">
            <p class="muted">Total Expense</p>
            <h2 class="text-red">₹<c:out value="${summary.totalExpense}"/></h2>
        </div>
        <div class="card">
            <p class="muted">Balance</p>
            <h2 class="${summary.balance >= 0 ? 'text-green' : 'text-red'}">
                ₹<c:out value="${summary.balance}"/>
            </h2>
        </div>
    </section>

    <section class="grid two-col">
        <div class="card">
            <h3>Add Transaction</h3>
            <form method="post" action="<c:url value='/transactions'/>" class="form">
                <label>Type
                    <select name="type" required>
                        <option value="income">Income</option>
                        <option value="expense">Expense</option>
                    </select>
                </label>
                <label>Amount (₹)
                    <input type="number" name="amount" min="0.01" step="0.01" required>
                </label>
                <label>Category
                    <input type="text" name="category" placeholder="e.g. Salary, Food" required>
                </label>
                <label>Description
                    <input type="text" name="description" placeholder="Optional note">
                </label>
                <label>Date
                    <input type="date" name="date" value="<%= java.time.LocalDate.now() %>">
                </label>
                <label>Payment Method
                    <select name="paymentMethod">
                        <option>Cash</option>
                        <option>Card</option>
                        <option>UPI</option>
                        <option>Bank Transfer</option>
                    </select>
                </label>
                <label class="checkbox">
                    <input type="checkbox" name="recurring"> Recurring
                </label>
                <label>Tags
                    <input type="text" name="tags" placeholder="comma,separated">
                </label>
                <button type="submit">Add Transaction</button>
            </form>
        </div>

        <div class="card">
            <h3>Set Budget</h3>
            <form method="post" action="<c:url value='/budgets'/>" class="form">
                <label>Category
                    <input type="text" name="category" placeholder="e.g. Food" required>
                </label>
                <label>Limit (₹)
                    <input type="number" name="limit" min="0.01" step="0.01" required>
                </label>
                <label>Month
                    <input type="month" name="month" value="<%= java.time.YearMonth.now() %>">
                </label>
                <button type="submit">Save Budget</button>
            </form>
        </div>
    </section>

    <section class="card">
        <div class="section-header">
            <h3>Recent Transactions</h3>
            <span class="muted">Latest 20 items</span>
        </div>
        <c:choose>
            <c:when test="${empty transactions}">
                <p class="muted">No transactions yet.</p>
            </c:when>
            <c:otherwise>
                <div class="table">
                    <div class="table-head">
                        <span>Date</span>
                        <span>Type</span>
                        <span>Category</span>
                        <span>Description</span>
                        <span>Amount</span>
                    </div>
                    <c:forEach var="t" items="${transactions}">
                        <div class="table-row">
                            <span><c:out value="${t.date}"/></span>
                            <span class="${t.type == 'income' ? 'pill pill-green' : 'pill pill-red'}">
                                <c:out value="${t.type}"/>
                            </span>
                            <span><c:out value="${t.category}"/></span>
                            <span><c:out value="${t.description}"/></span>
                            <span>₹<c:out value="${t.amount}"/></span>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>

    <section class="grid two-col">
        <div class="card">
            <div class="section-header">
                <h3>Budgets</h3>
                <span class="muted">Current and upcoming</span>
            </div>
            <c:choose>
                <c:when test="${empty budgets}">
                    <p class="muted">No budgets set.</p>
                </c:when>
                <c:otherwise>
                    <div class="table">
                        <div class="table-head">
                            <span>Month</span>
                            <span>Category</span>
                            <span>Limit</span>
                        </div>
                        <c:forEach var="b" items="${budgets}">
                            <div class="table-row">
                                <span><c:out value="${b.month}"/></span>
                                <span><c:out value="${b.category}"/></span>
                                <span>₹<c:out value="${b.limit}"/></span>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="card">
            <div class="section-header">
                <h3>Monthly Summary</h3>
                <span class="muted">Income vs Expense</span>
            </div>
            <c:choose>
                <c:when test="${empty monthly}">
                    <p class="muted">No monthly data yet.</p>
                </c:when>
                <c:otherwise>
                    <div class="table">
                        <div class="table-head">
                            <span>Month</span>
                            <span>Income</span>
                            <span>Expense</span>
                            <span>Balance</span>
                        </div>
                        <c:forEach var="entry" items="${monthly}">
                            <div class="table-row">
                                <span><c:out value="${entry.key}"/></span>
                                <span>₹<c:out value="${entry.value.totalIncome}"/></span>
                                <span>₹<c:out value="${entry.value.totalExpense}"/></span>
                                <span>₹<c:out value="${entry.value.balance}"/></span>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
</div>
</body>
</html>

