package view;

import model.Administration.User;
import utils.SessionManager;
import javax.swing.*;

public class LoginView extends JFrame {
    private JTextField txtUsername = new JTextField(15);
    private JPasswordField txtPassword = new JPasswordField(15);
    private JButton btnLogin = new JButton("Login");

    public LoginView() {
        setTitle("Login");
        setLayout(new java.awt.GridLayout(3,2));
        add(new JLabel("Username:")); add(txtUsername);
        add(new JLabel("Password:")); add(txtPassword);
        add(btnLogin);

        btnLogin.addActionListener(e -> login());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void login() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        // For demo: hardcoded users. Replace with DB check in real app.
        User user = null;
        if (username.equals("chief") && password.equals("123")) {
            user = new User(1, "chief", "123", User.UserRole.CHIEF_MANAGER, null);
        } else if (username.equals("manager") && password.equals("123")) {
            user = new User(2, "manager", "123", User.UserRole.STORE_MANAGER, 1);
        } else if (username.equals("employee") && password.equals("123")) {
            user = new User(3, "employee", "123", User.UserRole.EMPLOYEE, 2);
        }
        if (user != null) {
            SessionManager.getInstance().setCurrentUser(user);
            new MainFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials");
        }
    }
}