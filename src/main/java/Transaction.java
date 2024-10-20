import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    public static void recordTransaction(String accountNumber, String type, double amount) {
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO transactions (account_number, type, amount, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)")) {
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error recording transaction: " + e.getMessage());
        }
    }

    public static List<TransactionRecord> getLastFiveTransactions(String accountNumber) {
        List<TransactionRecord> records = new ArrayList<>();
        try (Connection conn = DatabaseUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT type, amount, timestamp FROM transactions WHERE account_number = ? ORDER BY timestamp DESC LIMIT 5")) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                String timestamp = rs.getString("timestamp");
                records.add(new TransactionRecord(type, amount, timestamp));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        }
        return records;
    }
}

class TransactionRecord {
    private String type;
    private double amount;
    private String timestamp;

    public TransactionRecord(String type, double amount, String timestamp) {
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("%s: %s $%.2f", timestamp, type, amount);
    }
}
