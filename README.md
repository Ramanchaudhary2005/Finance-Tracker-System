FinanceTracker
==============

Personal finance tracker with both a simple CLI experience and a lightweight Jakarta Servlet/JSP web UI. It lets you log income and expenses, set monthly budgets, and view summaries stored locally (JSON for the web app, `.dat` for the CLI).

Features
- Add income/expense transactions with category, description, payment method, recurring flag, and tags.
- Set category budgets per month and view current budget list.
- Dashboard cards for income, expense, balance; latest 20 transactions; monthly rollups.
- Local JSON persistence (`transactions.json`, `budgets.json`) for the web UI; `.dat` files for the CLI.
- Built with Java 17, JSP/JSTL, and Gson; packaged as a WAR for Servlet 5 containers.

Tech Stack
- Java 17
- Jakarta Servlet 5 + JSP + JSTL
- Gson for JSON persistence
- Maven for build/package

Project Layout
- `src/main/java/com/financetracker` – web models, services, and servlets.
- `src/main/webapp` – JSP views, static assets, and `web.xml`.
- `src/main/java/FinanceTracker.java` – standalone CLI version (console).
- `transactions.json`, `budgets.json` – web data storage (created at runtime).
- `transactions.dat`, `budgets.dat` – CLI data storage.
- `target/finance-tracker-web-3.0.war` – packaged web artifact after build.

Prerequisites
- JDK 17+
- Maven 3.8+ (for building)
- A Jakarta Servlet 5 compatible container (e.g., Tomcat 10+) to run the WAR

Getting Started (Web UI)
1) Install dependencies and build:
   - `mvn clean package`
2) Deploy the WAR:
   - Copy `target/finance-tracker-web-3.0.war` to your servlet container’s `webapps` (Tomcat 10+) or deploy via your container’s admin console.
3) Run the container and open:
   - `http://localhost:8080/finance-tracker-web-3.0/dashboard` (context path may differ based on container config).
4) Add transactions or budgets from the dashboard. Data persists to `transactions.json` and `budgets.json` in the working directory.

Using the CLI Version (optional)
- Run `javac src/main/java/FinanceTracker.java` then `java FinanceTracker`, or execute the prebuilt JAR if present: `java -jar target/finance-tracker-maven-2.0-jar-with-dependencies.jar`.
- CLI data is stored in `transactions.dat` and `budgets.dat` alongside the executable.

Key Endpoints (web)
- `GET /dashboard` – render dashboard with summary, recent transactions, budgets, monthly rollups.
- `POST /transactions` – add a transaction (form fields: `type`, `amount`, `category`, `description`, `date`, `paymentMethod`, `recurring`, `tags`).
- `POST /budgets` – add/update a monthly budget (fields: `category`, `limit`, `month`).

Data Persistence Notes
- Web mode writes to JSON in the current working directory. If running under a container, ensure the app has write permission where it executes.
- CLI mode uses Java serialization `.dat` files and keeps data local to the run directory.

Build / Clean Commands
- Build WAR: `mvn clean package`
- Clean artifacts: `mvn clean`

Troubleshooting
- If JSP taglib errors appear, confirm you are using a Servlet 5 compatible container (e.g., Tomcat 10+) and that the WAR is deployed with its bundled JSTL libs.
- If data is not saving, check write permissions for the working directory where the app runs; `transactions.json` and `budgets.json` must be writable.

License
- Academic/learning use. Add your preferred license if distributing.
