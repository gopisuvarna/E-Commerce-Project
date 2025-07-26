# 🛒 Java AWT-Based E-Commerce Platform

A desktop-based e-commerce application built using **Java AWT** for GUI and **JDBC with MySQL** for backend functionality. It simulates a full-featured online store, enabling user registration, login, product management, shopping cart handling, billing, and payment options.

---

## 🚀 Features

- 🔐 User registration and login
- 🛍️ Product listing with categories, images, and ratings
- 🛒 Add to cart & reset cart functionality
- 💳 Payment options: UPI, Card, Net Banking
- 🧾 Billing summary and downloadable receipt
- 👤 User ID-based session handling
- 🧑‍💻 Product CRUD operations for sellers

---

## 🛠️ Tech Stack

| Layer       | Technology             |
|------------|-------------------------|
| GUI        | Java AWT                |
| Backend    | JDBC                    |
| Database   | MySQL                   |
| Language   | Java                    |
| Image I/O  | `javax.imageio`, `AWT`  |

---

## 🗃️ Database Schema

Three core tables:

1. `registration`: Stores user data  
2. `products`: Contains product listings (FK: `uid → registration.rid`)  
3. `payments`: Stores payment records (FK: `uid → registration.rid`)  

> SQL schema is provided in [`DBMS.docx`](./DBMS.docx)

---

## 💻 How to Run

### 1. Create MySQL Database

- Create a database: `ecommerce`
- Run the SQL code from `DBMS.docx` to create tables.

### 2. Update JDBC Connection

In Java files like `Login.java`, `registerform.java`, update:
```java
Connection con = DriverManager.getConnection(
  "jdbc:mysql://localhost:3306/ecommerce", "root", "your_password");
```

### 3. Compile and Run

If you're using a terminal or command prompt:

```bash
javac *.java
java Login
```

Or, if you're using an IDE like IntelliJ IDEA or Eclipse:

- Import the project
- Ensure JDK and MySQL JDBC driver are configured
- Set `Login.java` as the entry point
- Click **Run**

---

## 📂 Project Structure

```
E-Commerce-Project/
├── Login.java
├── registerform.java
├── UserDashboard.java
├── BillingDashboard.java
├── PaymentOptionsFrame.java
├── DBMS.docx
```

---

## 📸 GUI Highlights

- Smart cart interface with product cards and category filtering
- Dynamic billing screen with transparent overlays
- Receipt file generation on checkout

(*You can add screenshots in this section if needed.*)

---

## 👤 Author

- [@gopisuvarna](https://github.com/gopisuvarna)

---

## 📌 Future Improvements

- Admin dashboard for managing all users & products  
- User order history tracking  
- Enhanced UI/UX with JavaFX  
- Email notifications for orders

---

## 📄 License

This project is for educational/demo use. No license specified.
