import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // frame setup
        JFrame frame = new JFrame("Bank Account Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout(10, 10));

        // account operations panel
        JPanel accountPanel = new JPanel();
        accountPanel.setLayout(new BoxLayout(accountPanel, BoxLayout.Y_AXIS));
        accountPanel.setBorder(BorderFactory.createTitledBorder("Account Operations"));

        // account creation fields
        JPanel fieldPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField nameField = new JTextField(10);
        JTextField accountNumberField = new JTextField(10);
        JTextField balanceField = new JTextField(10);
        fieldPanel.add(new JLabel("Name:"));
        fieldPanel.add(nameField);
        fieldPanel.add(new JLabel("Account Number:"));
        fieldPanel.add(accountNumberField);
        fieldPanel.add(new JLabel("Initial Balance:"));
        fieldPanel.add(balanceField);
        accountPanel.add(fieldPanel);

        // create and lookup buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createButton = new JButton("Create Account");
        JButton lookupButton = new JButton("Lookup Account");
        JButton viewAllAccountsButton = new JButton("View All Accounts");
        buttonPanel.add(createButton);
        buttonPanel.add(lookupButton);
        buttonPanel.add(viewAllAccountsButton);
        accountPanel.add(buttonPanel);

        // panel for transactions
        JPanel transactionPanel = new JPanel();
        transactionPanel.setLayout(new BoxLayout(transactionPanel, BoxLayout.Y_AXIS));
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Transactions"));
        JTextField transactionAmountField = new JTextField(10);
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JPanel transactFieldPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        transactFieldPanel.add(new JLabel("Amount:"));
        transactFieldPanel.add(transactionAmountField);
        transactionPanel.add(transactFieldPanel);
        JPanel transactButtonPanel = new JPanel(new FlowLayout());
        transactButtonPanel.add(depositButton);
        transactButtonPanel.add(withdrawButton);
        transactionPanel.add(transactButtonPanel);

        frame.add(accountPanel, BorderLayout.NORTH);
        frame.add(transactionPanel, BorderLayout.SOUTH);

        // listeners
        createButton.addActionListener(e -> {
            String name = nameField.getText();
            String accountNumber = accountNumberField.getText();
            double initialBalance = Double.parseDouble(balanceField.getText());
            boolean created = Account.createAccount(name, accountNumber, initialBalance);
            if (created) {
                JOptionPane.showMessageDialog(frame, "Account successfully created.");
                showAccountDetails(accountNumber);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to create account. It may already exist.");
            }
        });

        lookupButton.addActionListener(e -> {
            String accountNumber = accountNumberField.getText();
            showAccountDetails(accountNumber);
        });

        depositButton.addActionListener(e -> {
            String accountNumber = accountNumberField.getText();
            double amount = Double.parseDouble(transactionAmountField.getText());
            Account account = Account.getAccountByNumber(accountNumber);
            if (account != null && account.deposit(amount)) {
                JOptionPane.showMessageDialog(frame, "Deposit successful.");
                showAccountDetails(accountNumber);
            } else {
                JOptionPane.showMessageDialog(frame, "Deposit failed. Check account number.");
            }
        });

        withdrawButton.addActionListener(e -> {
            String accountNumber = accountNumberField.getText();
            double amount = Double.parseDouble(transactionAmountField.getText());
            Account account = Account.getAccountByNumber(accountNumber);
            if (account != null && account.withdraw(amount)) {
                JOptionPane.showMessageDialog(frame, "Withdrawal successful.");
                showAccountDetails(accountNumber);
            } else {
                JOptionPane.showMessageDialog(frame, "Withdrawal failed. Customer may be broke!.");
            }
        });

        // View accounts
        viewAllAccountsButton.addActionListener(e -> showAllAccounts());
        frame.setVisible(true);
    }

    private static void showAccountDetails(String accountNumber) {
        Account account = Account.getAccountByNumber(accountNumber);
        if (account != null) {
            JFrame detailsFrame = new JFrame("Account Details");
            detailsFrame.setSize(350, 300);
            JTextArea detailsArea = new JTextArea(5, 30);
            detailsArea.setEditable(false);

            List<TransactionRecord> transactions = Transaction.getLastFiveTransactions(accountNumber);
            StringBuilder details = new StringBuilder();
            details.append("Account Holder: ").append(account.getName()).append("\n");
            details.append("Current Balance: $").append(account.getBalance()).append("\n");
            details.append("Recent Transactions:\n");
            for (TransactionRecord tr : transactions) {
                details.append(tr).append("\n");
            }

            detailsArea.setText(details.toString());
            detailsFrame.add(new JScrollPane(detailsArea));
            detailsFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Account not found.");
        }
    }

    private static void showAllAccounts() {
        JFrame listFrame = new JFrame("List of All Accounts");
        listFrame.setSize(400, 300);
        JTextArea listArea = new JTextArea();
        listArea.setEditable(false);

        List<Account> accounts = Account.getAllAccounts();
        StringBuilder builder = new StringBuilder();
        for (Account account : accounts) {
            builder.append("Account Number: ").append(account.getAccountNumber())
                    .append(", Name: ").append(account.getName()).append("\n");
        }
        listArea.setText(builder.toString());

        listFrame.add(new JScrollPane(listArea));
        listFrame.setVisible(true);
    }
}
