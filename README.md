# 🛒 Java AWT-Based E-Commerce Platform

This project is a desktop-based e-commerce platform built using **Java AWT** for GUI and **JDBC with MySQL** for backend operations. It simulates a full-featured online store, enabling user registration, login, product management, shopping cart handling, billing, and payment options.

---

## 📌 Features

- 🔐 User Registration & Login (with validation)
- 🛍️ Product Listing by Category (with images & ratings)
- 🛒 Add to Cart & Checkout
- 🧾 Billing Dashboard with cart summary
- 💳 Payment Options: UPI, Card, Net Banking
- 🧾 Receipt generation & saving
- 👤 User-specific data handling (via UID)
- 📦 Admin-like product operations: Add / Update / Delete

---

## 🧰 Tech Stack

| Layer       | Technology Used           |
|------------|----------------------------|
| Frontend   | Java AWT (Abstract Window Toolkit) |
| Backend    | Java JDBC                  |
| Database   | MySQL                      |
| Build Tool | javac, java (via CLI or IDE) |

---

## 🗂️ Project Structure
E-Commerce-Project/
│
├── Login.java # User login interface
├── registerform.java # Registration form with field validations
├── UserDashboard.java # Product list, cart, and product operations
├── BillingDashboard.java # Checkout and billing summary
├── PaymentOptionsFrame.java # Payment method selection and final payment
├── DBMS.docx # Documentation (ER model, schema, explanation)


---

## 🧮 Database Schema (MySQL)

Three core tables are used:

- `registration` – stores user data
- `products` – product catalog (with seller/user ID FK)
- `payments` – user payment history

Refer to [`DBMS.docx`](./DBMS.docx) for schema details and SQL queries.

---

## 💻 How to Run

### 1. Setup MySQL

- Create a database named `ecommerce`
- Run the SQL queries in `DBMS.docx` to create the tables

### 2. Configure DB Credentials

Update DB credentials (`root`, `password`) in Java files:
```java
Connection con = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/ecommerce", "root", "your_password");
**###3. Compile & Run (Example)**

javac *.java
java Login

🎨 GUI Preview
Registration & Login Forms with image backgrounds

Dynamic product listing with rating and images

Cart summary with billing and receipt

🧑‍💻 Contributors
@gopisuvarna
