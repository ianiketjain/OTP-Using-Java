import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Main {

    static class Account {
        private static int nextAccountNumber = 1001;

        int accountNumber;
        String accountHolderName;
        String passwordHash;
        double balance;
        String accountStatus; // active/inactive/closed
        List<String> transactionHistory;
        LocalDateTime createdDate;
        LocalDateTime lastLoginDate;

        Account(String name, String password, double initialDeposit) {
            this.accountNumber = nextAccountNumber++;
            this.accountHolderName = name;
            this.passwordHash = hashPassword(password);
            this.balance = initialDeposit;
            this.accountStatus = "active";
            this.transactionHistory = new ArrayList<>();
            this.createdDate = LocalDateTime.now();
            this.lastLoginDate = null;
        }

        private String hashPassword(String password) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(password.getBytes());
                StringBuilder hex = new StringBuilder();
                for (byte b : hash) {
                    hex.append(String.format("%02x", b));
                }
                return hex.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Hashing algorithm not found!");
            }
        }

        boolean verifyPassword(String password) {
            return hashPassword(password).equals(this.passwordHash);
        }

        void updatePassword(String newPassword) {
            this.passwordHash = hashPassword(newPassword);
        }
    }

    private static final Scanner sc = new Scanner(System.in);
    private static final Map<Integer, Account> accounts = new HashMap<>();
    private static Account currentSession = null;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static void main(String[] args) {
        while (true) {
            displayMenu();
            int choice = validateIntInput("Enter your choice: ");
            switch (choice) {
                case 1 -> createAccount();
                case 2 -> loginLogout();
                case 3 -> changePassword();
                case 4 -> depositWithdraw("deposit");
                case 5 -> depositWithdraw("withdraw");
                case 6 -> viewAccount();
                case 7 -> closeAccount();
                case 8 -> { System.out.println("Exiting system. Thank you for your time!"); return; }
                default -> System.out.println("Invalid choice! Please try again.\n");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("=== BANKING SYSTEM ===");
        System.out.println("1. Open New Account");
        System.out.println("2. Login/Logout");
        System.out.println("3. Change Password");
        System.out.println("4. Deposit Money");
        System.out.println("5. Withdraw Money");
        System.out.println("6. View Account Details");
        System.out.println("7. Close Account");
        System.out.println("8. Exit System");
        System.out.println("-------------------------------------------");
    }

    private static void createAccount() {
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        double initialDeposit = validateDoubleInput("Enter initial deposit amount: ");
        System.out.print("Set your password: ");
        String password = sc.nextLine();

        Account newAccount = new Account(name, password, initialDeposit);
        accounts.put(newAccount.accountNumber, newAccount);
        newAccount.transactionHistory.add("Account created with initial deposit: $" + initialDeposit);
        System.out.println("Account successfully created! Your account number: " + newAccount.accountNumber + "\n");
    }

    private static void loginLogout() {
        if (currentSession == null) {
            int accNumber = validateIntInput("Enter account number: ");
            if (!accounts.containsKey(accNumber)) {
                System.out.println("Account not found!\n");
                return;
            }
            Account acc = accounts.get(accNumber);
            if (!acc.accountStatus.equals("active")) {
                System.out.println("Account is not active!\n");
                return;
            }
            System.out.print("Enter password: ");
            String password = sc.nextLine();
            if (acc.verifyPassword(password)) {
                currentSession = acc;
                acc.lastLoginDate = LocalDateTime.now();
                System.out.println("Login successful! Welcome, " + acc.accountHolderName + "\n");
            } else {
                System.out.println("Incorrect password!\n");
            }
        } else {
            System.out.println("Logged out successfully from account: " + currentSession.accountNumber + "\n");
            currentSession = null;
        }
    }

    private static void changePassword() {
        if (!isLoggedIn()) return;
        System.out.print("Enter current password: ");
        String currentPass = sc.nextLine();
        if (currentSession.verifyPassword(currentPass)) {
            System.out.print("Enter new password: ");
            String newPass = sc.nextLine();
            currentSession.updatePassword(newPass);
            System.out.println("Password updated successfully!\n");
        } else {
            System.out.println("Incorrect current password!\n");
        }
    }

    private static void depositWithdraw(String action) {
        if (!isLoggedIn()) return;
        double amount = validateDoubleInput("Enter amount to " + action + ": ");
        if (action.equals("withdraw") && amount > currentSession.balance) {
            System.out.println("Insufficient balance!\n");
            return;
        }
        if (action.equals("deposit")) {
            currentSession.balance += amount;
            currentSession.transactionHistory.add("Deposited: $" + amount + " on " + dtf.format(LocalDateTime.now()));
        } else {
            currentSession.balance -= amount;
            currentSession.transactionHistory.add("Withdrawn: $" + amount + " on " + dtf.format(LocalDateTime.now()));
        }
        System.out.println(action.substring(0,1).toUpperCase() + action.substring(1) + " successful. Current Balance: $" + currentSession.balance + "\n");
    }

    private static void viewAccount() {
        if (!isLoggedIn()) return;
        Account acc = currentSession;
        System.out.println("=== ACCOUNT DETAILS ===");
        System.out.println("Account Number: " + acc.accountNumber);
        System.out.println("Account Holder: " + acc.accountHolderName);
        System.out.println("Balance: $" + acc.balance);
        System.out.println("Status: " + acc.accountStatus);
        System.out.println("Created On: " + dtf.format(acc.createdDate));
        System.out.println("Last Login: " + (acc.lastLoginDate != null ? dtf.format(acc.lastLoginDate) : "Never"));
        System.out.println("Transaction History:");
        if (acc.transactionHistory.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            acc.transactionHistory.forEach(System.out::println);
        }
        System.out.println();
    }

    private static void closeAccount() {
        if (!isLoggedIn()) return;
        System.out.print("Confirm password to close account: ");
        String password = sc.nextLine();
        if (currentSession.verifyPassword(password)) {
            currentSession.accountStatus = "closed";
            System.out.println("Account " + currentSession.accountNumber + " closed successfully!\n");
            currentSession = null;
        } else {
            System.out.println("Incorrect password! Account not closed.\n");
        }
    }

    // Utility Methods
    private static boolean isLoggedIn() {
        if (currentSession == null) {
            System.out.println("You must log in first!\n");
            return false;
        }
        return true;
    }

    private static int validateIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = Integer.parseInt(sc.nextLine());
                if (input < 0) throw new NumberFormatException();
                return input;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a positive integer.");
            }
        }
    }

    private static double validateDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double input = Double.parseDouble(sc.nextLine());
                if (input < 0) throw new NumberFormatException();
                return input;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a positive number.");
            }
        }
    }
}
