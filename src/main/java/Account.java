import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private String name;
    private String accountNumber;
    private double balance;

    public Account(String name, String accountNumber, double balance) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public static boolean createAccount(String name, String accountNumber, double initialBalance) {
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO accounts (name, account_number, balance) VALUES (?, ?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setString(2, accountNumber);
            pstmt.setDouble(3, initialBalance);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                // Record the initial balance as a transaction
                Transaction.recordTransaction(accountNumber, "initial deposit", initialBalance);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
        return false;
    }

    public static Account getAccountByNumber(String accountNumber) {
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT name, balance FROM accounts WHERE account_number = ?")) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Account(rs.getString("name"), accountNumber, rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            System.out.println("Account not found: " + e.getMessage());
        }
        return null;
    }

    public boolean deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            if (updateBalance()) {
                Transaction.recordTransaction(this.accountNumber, "deposit", amount);
                return true;
            }
        }
        return false;
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            if (updateBalance()) {
                Transaction.recordTransaction(this.accountNumber, "withdrawal", amount);
                return true;
            }
        }
        return false;
    }

    private boolean updateBalance() {
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE accounts SET balance = ? WHERE account_number = ?")) {
            pstmt.setDouble(1, this.balance);
            pstmt.setString(2, this.accountNumber);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    // retrieve all accounts
    public static List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        try (Connection conn = DatabaseUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, account_number FROM accounts")) {
            while (rs.next()) {
                String name = rs.getString("name");
                String accountNumber = rs.getString("account_number");
                accounts.add(new Account(name, accountNumber, 0)); // Only display name and account number
            }
        } catch (SQLException e) {
            System.out.println("Error fetching accounts: " + e.getMessage());
        }
        return accounts;
    }
}
