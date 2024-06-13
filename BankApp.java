import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

class Bank {
    static int totalBalance = 0;
    static int totalLoan = 0;
    static boolean loanStatus = true;
    static ArrayList<Account> accountList = new ArrayList<>();
    String name;

    public Bank(String name) {
        this.name = name;
    }

    public int generateAutoAccNumber() {
        return (int) (Math.random() * (200 - 100 + 1) + 100);
    }

    public Account createAccount(String name, String email, String password, String accountType) {
        int accountNumber = generateAutoAccNumber();
        Account user;
        switch (accountType) {
            case "Savings":
                user = new SavingsAccount(name, email, password, accountNumber);
                break;
            case "Current":
                user = new CurrentAccount(name, email, password, accountNumber);
                break;
            case "Admin":
                user = new Admin(name, email, password, accountNumber);
                break;
            default:
                System.out.println("Invalid account type!");
                return null;
        }
        accountList.add(user);
        return user;
    }

    public void deleteAccount(int accountNumber) {
        for (Account user : accountList) {
            if (user.accountNumber == accountNumber) {
                accountList.remove(user);
                totalBalance -= user.balance;
                totalLoan -= user.loans;
                System.out.println("Account with account number " + accountNumber + " deleted.");
                return;
            }
        }
        System.out.println("Account with account number " + accountNumber + " not found.");
    }

    public void showUsers() {
        for (Account user : accountList) {
            System.out.println("Name : " + user.name + " || Email : " + user.email + " || Account Type : " + user.accountType + " || Account Number : " + user.accountNumber);
        }
    }

    public void totalBalances() {
        int total = 0;
        for (Account user : accountList) {
            System.out.println("Name: " + user.name + " || Account Number: " + user.accountNumber + " || Balance: " + user.balance);
            total += user.balance;
        }
        System.out.println("Total Balance: " + total);
    }

    public void totalLoans() {
        for (Account user : accountList) {
            System.out.println("Name: " + user.name + " || Account Number: " + user.accountNumber + " || Loans: " + user.loans);
        }
        System.out.println("Total Loans: " + totalLoan);
    }

    public void loansStatus(int accountNumber, boolean status) {
        for (Account user : accountList) {
            if (user.accountNumber == accountNumber) {
                user.setLoanStatus(status);
                return;
            }
        }
        System.out.println("User with account number " + accountNumber + " not found.");
    }
}

class Account {
    String name, email, password, accountType;
    int accountNumber, balance = 0, loans = 0, loanCount = 2;
    boolean loanStatus = true;
    ArrayList<String> transactionHistory = new ArrayList<>();

    public Account(String name, String email, String password, int accountNumber, String accountType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
    }

    public void deposit(int amount) {
        if (amount > 0) {
            balance += amount;
            transactionHistory.add("Deposit " + amount);
            Bank.totalBalance += amount;
            System.out.println("Deposited " + amount + " taka. Now your balance is " + balance + " taka.");
        } else {
            System.out.println("Invalid balance!");
        }
    }

    public void withdraw(int amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            Bank.totalBalance -= amount;
            transactionHistory.add("Withdraw " + amount);
            System.out.println("Withdrawn " + amount + " taka. Now your balance is " + balance + " taka.");
        } else {
            System.out.println("Not enough money in your account. Please deposit first!");
        }
    }

    public void checkAvailableBalance() {
        System.out.println("Your current balance is " + balance + " taka.");
    }

    public void checkTransactionHistory() {
        System.out.println("Transaction History: " + transactionHistory);
    }

    public void transfer(int amount, int receive) {
        Account other = null;
        for (Account user : Bank.accountList) {
            if (user.accountNumber == receive) {
                other = user;
                break;
            }
        }
        if (amount > 0 && balance >= amount && other != null) {
            balance -= amount;
            other.balance += amount;
            transactionHistory.add("Transferred: " + amount + " to " + other.name);
            System.out.println("Transferred " + amount + " taka to " + other.name + " successfully.");
        } else {
            System.out.println("Error: Insufficient balance for transfer.");
        }
    }

    public void setLoanStatus(boolean status) {
        loanStatus = status;
        System.out.println("Loan status for " + name + " is now " + (status ? "Enabled" : "Disabled") + ".");
    }

    public void takeLoan(int amount) {
        if (loanCount > 0 && loanStatus && loanCount < 3) {
            balance += amount;
            loans += amount;
            Bank.totalLoan += amount;
            transactionHistory.add("Loan taken: " + amount);
            System.out.println("Loan of " + amount + " taka credited to your account. Your balance is " + balance + ".");
            loanCount -= 1;
        } else if (!loanStatus) {
            System.out.println("Loan feature is currently turned off by the bank.");
        } else {
            System.out.println("You have already taken the maximum number of loans.");
        }
    }

    public void showInfo() {
        System.out.println("Information of " + accountType + " account by " + name);
        System.out.println("Account Type: " + accountType);
        System.out.println("Account Name: " + name);
        System.out.println("Email Address: " + email);
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Balance: " + balance);
        System.out.println("Loan Taken: " + loans);
        System.out.println("Transaction History: " + transactionHistory);
        for (String transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }
}

class SavingsAccount extends Account {
    public SavingsAccount(String name, String email, String password, int accountNumber) {
        super(name, email, password, accountNumber, "Savings");
    }
}

class CurrentAccount extends Account {
    public CurrentAccount(String name, String email, String password, int accountNumber) {
        super(name, email, password, accountNumber, "Current");
    }
}

class Admin extends Account {
    public Admin(String name, String email, String password, int accountNumber) {
        super(name, email, password, accountNumber, "Admin");
    }
}



public class BankApp extends Frame {

    private Bank bank;
    private Admin admin;
    private Account currentUser;

    public BankApp() {
        bank = new Bank("Pubali Bank");

        // Set up the main frame
        setTitle("Bank Application");
        setSize(600, 500);
        setLayout(new FlowLayout());

        // Add welcome label
        Label welcomeLabel = new Label("Welcome to " + bank.name);
        add(welcomeLabel);

        // Add admin login button
        Button adminLoginButton = new Button("Admin Login");
        adminLoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAdminLoginDialog();
            }
        });
        add(adminLoginButton);

        // Add admin register button
        Button adminRegisterButton = new Button("Admin Register");
        adminRegisterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAdminRegisterDialog();
            }
        });
        add(adminRegisterButton);

        // Window close event
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private void showAdminLoginDialog() {
        Dialog loginDialog = new Dialog(this, "Admin Login", true);
        loginDialog.setSize(600, 400);
        loginDialog.setLayout(new GridLayout(3, 2));

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label passwordLabel = new Label("Password:");
        TextField passwordField = new TextField();
        passwordField.setEchoChar('*');

        Button loginButton = new Button("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String password = passwordField.getText();
                // Authenticate admin
                for (Account account : Bank.accountList) {
                    if (account.name.equals(name) && account.password.equals(password) && account.accountType.equals("Admin")) {
                        admin = (Admin) account;
                        loginDialog.dispose();
                        showAdminMenu();
                        return;
                    }
                }
                showMessage("Login failed. Incorrect username or password.");
            }
        });

        loginDialog.add(nameLabel);
        loginDialog.add(nameField);
        loginDialog.add(passwordLabel);
        loginDialog.add(passwordField);
        loginDialog.add(new Label());
        loginDialog.add(loginButton);

        // Window close event
        loginDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                loginDialog.dispose();
            }
        });

        loginDialog.setVisible(true);
    }

    private void showAdminRegisterDialog() {
        Dialog registerDialog = new Dialog(this, "Admin Register", true);
        registerDialog.setSize(600, 500);
        registerDialog.setLayout(new GridLayout(4, 2));

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("Password:");
        TextField passwordField = new TextField();
        passwordField.setEchoChar('*');

        Button registerButton = new Button("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String password = passwordField.getText();
                // Register admin
                admin = (Admin) bank.createAccount(name, email, password, "Admin");
                registerDialog.dispose();
                showAdminMenu();
                showMessage("Account Create Successfully.");
            }
        });

        registerDialog.add(nameLabel);
        registerDialog.add(nameField);
        registerDialog.add(emailLabel);
        registerDialog.add(emailField);
        registerDialog.add(passwordLabel);
        registerDialog.add(passwordField);
        registerDialog.add(new Label());
        registerDialog.add(registerButton);

        registerDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                registerDialog.dispose();
            }
        });

        registerDialog.setVisible(true);
    }

    private void showUserLoginDialog() {
        Dialog loginDialog = new Dialog(this, "User Login", true);
        loginDialog.setSize(600, 400);
        loginDialog.setLayout(new GridLayout(3, 2));

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label passwordLabel = new Label("Password:");
        TextField passwordField = new TextField();
        passwordField.setEchoChar('*');

        Button loginButton = new Button("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String password = passwordField.getText();
                // Authenticate admin
                for (Account account : Bank.accountList) {
                    if (account.name.equals(name) && account.password.equals(password) && account.accountType.equals("Savings")) {
                        // admin = (Admin) account;
                        currentUser = (Account) account;
                        loginDialog.dispose();
                        showUserMenu();
                        showMessage("Account Login Successfully");
                        return;
                    }
                }
                showMessage("Login failed. Incorrect username or password.");
            }
        });

        loginDialog.add(nameLabel);
        loginDialog.add(nameField);
        loginDialog.add(passwordLabel);
        loginDialog.add(passwordField);
        loginDialog.add(new Label());
        loginDialog.add(loginButton);

        // Window close event
        loginDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                loginDialog.dispose();
            }
        });

        loginDialog.setVisible(true);
    }
    
    private void addInitialComponents() {
        // Clear all components from the frame
        removeAll();
    
        // Reset the title
        setTitle("Bank Application");
    
        // Add welcome label
        Label welcomeLabel = new Label("Welcome to " + bank.name);
        add(welcomeLabel);
    
        // Add admin login button
        Button adminLoginButton = new Button("Admin Login");
        adminLoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAdminLoginDialog();
            }
        });
        add(adminLoginButton);
    
        // Add admin register button
        Button adminRegisterButton = new Button("Admin Register");
        adminRegisterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAdminRegisterDialog();
            }
        });
        add(adminRegisterButton);
    
        validate();
        repaint();
    }

    private void userLoginPage() {
        // Clear all components from the frame
        removeAll();
    
        // Reset the title
        setTitle("Bank Application");
    
        // Add welcome label
        Label welcomeLabel = new Label("Welcome to " + bank.name);
        add(welcomeLabel);
    
        // Add admin login button
        Button userLoginButton = new Button("User Login");
        userLoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showUserLoginDialog();
            }
        });
        add(userLoginButton);

        // Add admin login button
        Button adminPageButton = new Button("Admin Page");
        adminPageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addInitialComponents();
            }
        });
        add(adminPageButton);
    
        validate();
        repaint();
    }
    
    private void showAdminMenu() {
        removeAll();
        setTitle("Admin Menu");

        Label welcomeLabel = new Label("Welcome, " + admin.name);
        add(welcomeLabel);

        Button createUserButton = new Button("Create User Account");
        createUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCreateUserDialog();
            }
        });
        add(createUserButton);



        Button deleteUserButton = new Button("Delete User Account");
        deleteUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDeleteUserDialog();
            }
        });
        add(deleteUserButton);

        Button userListButton = new Button("User Account List");
        userListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showUserListDialog();
            }
        });
        add(userListButton);

        Button totalBalanceButton = new Button("Total Available Balance");
        totalBalanceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTotalBalanceDialog();
            }
        });
        add(totalBalanceButton);

        Button totalLoansButton = new Button("Total Loans");
        totalLoansButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTotalLoansDialog();
            }
        });
        add(totalLoansButton);

        Button loanStatusButton = new Button("Loan Status");
        loanStatusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoanStatusDialog();
            }
        });
        add(loanStatusButton);

        Button userPageButton = new Button("User Page");
        userPageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                userLoginPage();
            }
        });
        add(userPageButton);

        Button logoutButton = new Button("Logout");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                admin = null;
                removeAll();
                validate();
                repaint();
                addInitialComponents();
    
            }
        });
        add(logoutButton);

        validate();
        repaint();
    }

    private void showUserMenu() {
        removeAll();
        setTitle("User Menu");

        Label welcomeLabel = new Label("Welcome, " + currentUser.name);
        add(welcomeLabel);

        Button depositAmountButton = new Button("Deposit Amount");
        depositAmountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDepositUserDialog();
            }
        });
        add(depositAmountButton);



        Button withdrawUserButton = new Button("Withdraw Amount"); 
        withdrawUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showWithdrawUserDialog();
            }
        });
        add(withdrawUserButton);

        Button checkAvailableBalanceListButton = new Button("Check Available Balance");
        checkAvailableBalanceListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkAvailableBalanceListDialog();
            }
        });
        add(checkAvailableBalanceListButton);

        Button transactionHistoryButton = new Button("Transaction History");
        transactionHistoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkTransactionHistoryListDialog();
            }
        });
        add(transactionHistoryButton);

        Button transferBalanceButton = new Button("Transfer Balance");
        transferBalanceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTransferUserDialog();
            }
        });
        add(transferBalanceButton);

        Button takeLoanButton = new Button("Take Loan");
        takeLoanButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoanUserDialog();
            }
        });
        add(takeLoanButton);

        Button showInformantionButton = new Button("Show Information List");
        showInformantionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showUserInfoDialog();
            }
        });
        add(showInformantionButton);

        Button logoutButton = new Button("Logout");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                admin = null;
                removeAll();
                validate();
                repaint();
                userLoginPage();
    
            }
        });
        add(logoutButton);

        validate();
        repaint();
    }

    private void showCreateUserDialog() {
        Dialog createUserDialog = new Dialog(this, "Create User Account", true);
        createUserDialog.setSize(600, 500);
        createUserDialog.setLayout(new GridLayout(4, 2));

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("Password:");
        TextField passwordField = new TextField();
        passwordField.setEchoChar('*');

        Button createButton = new Button("Create");
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String password = passwordField.getText();
                bank.createAccount(name, email, password, "Savings");
                showMessage("Account created successfully.");
                createUserDialog.dispose();
                validate();
                repaint();
                userLoginPage();
            }
        });

        createUserDialog.add(nameLabel);
        createUserDialog.add(nameField);
        createUserDialog.add(emailLabel);
        createUserDialog.add(emailField);
        createUserDialog.add(passwordLabel);
        createUserDialog.add(passwordField);
        createUserDialog.add(new Label());
        createUserDialog.add(createButton);

        createUserDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                createUserDialog.dispose();
            }
        });

        createUserDialog.setVisible(true);
        validate();
        repaint();
    
    }

    private void showDepositUserDialog() {
        Dialog depositUserDialog = new Dialog(this, "Deposit Balance", true);
        depositUserDialog.setSize(300, 300);
        depositUserDialog.setLayout(new GridLayout(2, 2));

        Label nameLabel = new Label("Deposit:");
        TextField nameField = new TextField();

        Button depositButton = new Button("Deposit");
        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String depositText = nameField.getText();
                try {
                    int depositAmount = Integer.parseInt(depositText);
                    currentUser.deposit(depositAmount);
                    showMessage("Successfully Deposit to " + depositAmount + "Taka");
                    depositUserDialog.dispose();
                } catch (NumberFormatException ex) {
                    // Handle the case where the user entered invalid input for the deposit amount
                    JOptionPane.showMessageDialog(null, "Invalid deposit amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        depositUserDialog.add(nameLabel);
        depositUserDialog.add(nameField);
        depositUserDialog.add(new Label());
        depositUserDialog.add(depositButton);

        depositUserDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                depositUserDialog.dispose();
            }
        });

        depositUserDialog.setVisible(true);
    
    }

    private void showWithdrawUserDialog() {
        Dialog withdrawUserDialog = new Dialog(this, "Withdraw Balance", true);
        withdrawUserDialog.setSize(300, 300);
        withdrawUserDialog.setLayout(new GridLayout(2, 2));

        Label nameLabel = new Label("Withdraw:");
        TextField nameField = new TextField();

        Button withdrawButton = new Button("Withdraw");
        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String withdrawText = nameField.getText();
                try {
                    int withdrawAmount = Integer.parseInt(withdrawText);
                    currentUser.withdraw(withdrawAmount);
                    showMessage("Successfully Withdraw to " + withdrawAmount + "Taka");
                    withdrawUserDialog.dispose();
                } catch (NumberFormatException ex) {
                    // Handle the case where the user entered invalid input for the deposit amount
                    JOptionPane.showMessageDialog(null, "Invalid withdraw amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        withdrawUserDialog.add(nameLabel);
        withdrawUserDialog.add(nameField);
        withdrawUserDialog.add(new Label());
        withdrawUserDialog.add(withdrawButton);

        withdrawUserDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                withdrawUserDialog.dispose();
            }
        });

        withdrawUserDialog.setVisible(true);
    
    }

    private void checkAvailableBalanceListDialog() {
        Dialog checkAvailableBalanceDialog = new Dialog(this, "Check Available Balance", true);
        checkAvailableBalanceDialog.setSize(600, 500);
        checkAvailableBalanceDialog.setLayout(new GridLayout(Bank.accountList.size(), 1));

        for (Account user : Bank.accountList) {
            Label userLabel = new Label("Name: " + user.name + " - Available Balance: " + user.balance);
            checkAvailableBalanceDialog.add(userLabel);
        }

        checkAvailableBalanceDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                checkAvailableBalanceDialog.dispose();
            }
        });

        checkAvailableBalanceDialog.setVisible(true);
    }

    private void checkTransactionHistoryListDialog() {
        Dialog checkTransactionHistoryDialog = new Dialog(this, "Transaction History", true);
        checkTransactionHistoryDialog.setSize(600, 500);
        checkTransactionHistoryDialog.setLayout(new GridLayout(Bank.accountList.size(), 1));

        Label userLabel = new Label("Transaction History" + currentUser.transactionHistory);
        checkTransactionHistoryDialog.add(userLabel);
        
        checkTransactionHistoryDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                checkTransactionHistoryDialog.dispose();
            }
        });

        checkTransactionHistoryDialog.setVisible(true);
    }

    private void showTransferUserDialog() {
        Dialog depositUserDialog = new Dialog(this, "Transfer Balance", true);
        depositUserDialog.setSize(300, 300);
        depositUserDialog.setLayout(new GridLayout(3, 3));

        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();
        Label acoountLabel = new Label("Account:");
        TextField accountField = new TextField();

        Button depositButton = new Button("Transfer");
        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String transferText = amountField.getText();
                String accountText = accountField.getText();
                try {
                    int transferAmount = Integer.parseInt(transferText);
                    int accountNumber = Integer.parseInt(accountText);
                    currentUser.transfer(transferAmount,accountNumber);
                    showMessage("Successfully transfer " + transferAmount + "Taka to" + accountNumber + " Number");
                    depositUserDialog.dispose();
                } catch (NumberFormatException ex) {
                    // Handle the case where the user entered invalid input for the deposit amount
                    JOptionPane.showMessageDialog(null, "Invalid account number. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        depositUserDialog.add(amountLabel);
        depositUserDialog.add(amountField);
        depositUserDialog.add(acoountLabel);
        depositUserDialog.add(accountField);
        depositUserDialog.add(new Label());
        depositUserDialog.add(depositButton);

        depositUserDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                depositUserDialog.dispose();
            }
        });

        depositUserDialog.setVisible(true);
    
    }

    private void showLoanUserDialog() {
        Dialog loanUserDialog = new Dialog(this, "Loan Information", true);
        loanUserDialog.setSize(300, 300);
        loanUserDialog.setLayout(new GridLayout(3, 3));

        Label loanLabel = new Label("Loan Amount");
        TextField loanField = new TextField();

        Button depositButton = new Button("Take Loan");
        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String loanText = loanField.getText();
                try {
                    int loanAmount = Integer.parseInt(loanText);
                    currentUser.takeLoan(loanAmount);
                    showMessage("Successfully take loan " + loanAmount + "Taka");
                    loanUserDialog.dispose();
                } catch (NumberFormatException ex) {
                    // Handle the case where the user entered invalid input for the deposit amount
                    JOptionPane.showMessageDialog(null, "Invalid loan balance. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loanUserDialog.add(loanLabel);
        loanUserDialog.add(loanField);
        loanUserDialog.add(new Label());
        loanUserDialog.add(depositButton);

        loanUserDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                loanUserDialog.dispose();
            }
        });

        loanUserDialog.setVisible(true);
    
    }

    private void showUserInfoDialog() {
        Dialog userInfoDialog = new Dialog(this, "User Account List", true);
        userInfoDialog.setSize(600, 500);
        userInfoDialog.setLayout(new GridLayout(Bank.accountList.size(), 1));

        
        Label userLabel = new Label("Name: " + currentUser.name + "||----||" +
                             "Email: " + currentUser.email + "||----||" +
                             "Account Type: " + currentUser.accountType + "||----||" +
                             "Account Number: " + currentUser.accountNumber + "||----||" +
                             "Loan: " + currentUser.loans + "||----||" + 
                             "Account Balance" + currentUser.balance + "||----||" +
                             "Transaction History" + currentUser.transactionHistory);
        userInfoDialog.add(userLabel);

        // Label userLabel = new Label();
        // userInfoDialog.add(userLabel);
        // System.out.println(currentUser.name);

        userInfoDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                userInfoDialog.dispose();
            }
        });

        userInfoDialog.setVisible(true);
    }

    private void showDeleteUserDialog() {
        Dialog deleteUserDialog = new Dialog(this, "Delete User Account", true);
        deleteUserDialog.setSize(600, 500);
        deleteUserDialog.setLayout(new GridLayout(2, 2));

        Label accountNumberLabel = new Label("Account Number:");
        TextField accountNumberField = new TextField();

        Button deleteButton = new Button("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                bank.deleteAccount(accountNumber);
                showMessage("Successfully Delete Account " + accountNumber);
                deleteUserDialog.dispose();
            }
        });

        deleteUserDialog.add(accountNumberLabel);
        deleteUserDialog.add(accountNumberField);
        deleteUserDialog.add(new Label());
        deleteUserDialog.add(deleteButton);

        deleteUserDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                deleteUserDialog.dispose();
            }
        });

        deleteUserDialog.setVisible(true);
    }

    private void showUserListDialog() {
        Dialog userListDialog = new Dialog(this, "User Account List", true);
        userListDialog.setSize(600, 500);
        userListDialog.setLayout(new GridLayout(Bank.accountList.size(), 1));

        for (Account user : Bank.accountList) {
            Label userLabel = new Label("Name: " + user.name + " || Email: " + user.email + " || Account Type: " + user.accountType + " || Account Number: " + user.accountNumber);
            userListDialog.add(userLabel);
        }

        userListDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                userListDialog.dispose();
            }
        });

        userListDialog.setVisible(true);
    }

    private void showTotalBalanceDialog() {
        Dialog totalBalanceDialog = new Dialog(this, "Total Available Balance", true);
        totalBalanceDialog.setSize(600, 500);
        totalBalanceDialog.setLayout(new GridLayout(2, 1));

        int totalBalance = 0;
        for (Account user : Bank.accountList) {
            totalBalance += user.balance;
        }

        Label totalBalanceLabel = new Label("Total Balance: " + totalBalance);
        totalBalanceDialog.add(totalBalanceLabel);

        totalBalanceDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                totalBalanceDialog.dispose();
            }
        });

        totalBalanceDialog.setVisible(true);
    }

    private void showTotalLoansDialog() {
        Dialog totalLoansDialog = new Dialog(this, "Total Loans", true);
        totalLoansDialog.setSize(600, 500);
        totalLoansDialog.setLayout(new GridLayout(2, 1));

        int totalLoans = 0;
        for (Account user : Bank.accountList) {
            totalLoans += user.loans;
        }

        Label totalLoansLabel = new Label("Total Loans: " + totalLoans);
        totalLoansDialog.add(totalLoansLabel);

        totalLoansDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                totalLoansDialog.dispose();
            }
        });

        totalLoansDialog.setVisible(true);
    }

    private void showLoanStatusDialog() {
        Dialog loanStatusDialog = new Dialog(this, "Loan Status", true);
        loanStatusDialog.setSize(600, 500);
        loanStatusDialog.setLayout(new GridLayout(3, 2));

        Label accountNumberLabel = new Label("Account Number:");
        TextField accountNumberField = new TextField();
        Label statusLabel = new Label("Status (true/false):");
        TextField statusField = new TextField();

        Button updateButton = new Button("Update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                boolean status = Boolean.parseBoolean(statusField.getText());
                bank.loansStatus(accountNumber, status);
                showMessage("Currently loan status is " + status);
                loanStatusDialog.dispose();
            }
        });

        loanStatusDialog.add(accountNumberLabel);
        loanStatusDialog.add(accountNumberField);
        loanStatusDialog.add(statusLabel);
        loanStatusDialog.add(statusField);
        loanStatusDialog.add(new Label());
        loanStatusDialog.add(updateButton);

        loanStatusDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                loanStatusDialog.dispose();
            }
        });

        loanStatusDialog.setVisible(true);
    }

    private void showMessage(String message) {
        Dialog messageDialog = new Dialog(this, "Message", true);
        messageDialog.setSize(600, 500);
        messageDialog.setLayout(new FlowLayout());

        Label messageLabel = new Label(message);
        messageDialog.add(messageLabel);

        Button okButton = new Button("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                messageDialog.dispose();
            }
        });
        messageDialog.add(okButton);

        messageDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                messageDialog.dispose();
            }
        });

        messageDialog.setVisible(true);
    }

    public static void main(String[] args) {
        new BankApp();
    }
}
