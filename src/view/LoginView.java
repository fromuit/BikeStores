package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.ExecutionException;
import model.Administration.User;
import service.AuthenticationService;
import utils.SessionManager;
import utils.ValidationException;

public class LoginView extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JCheckBox chkRememberMe;
    private JLabel lblStatus;
    private JProgressBar progressBar;
    private AuthenticationService authService;

    public LoginView() {
        this.authService = new AuthenticationService();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupWindow();
    }

    private void initializeComponents() {
        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);
        btnLogin = new JButton("Đăng nhập");
        btnExit = new JButton("Thoát");
        chkRememberMe = new JCheckBox("Nhớ tài khoản");
        lblStatus = new JLabel(" ");
        progressBar = new JProgressBar();

        // Style components
        btnLogin.setPreferredSize(new Dimension(100, 35));
        btnExit.setPreferredSize(new Dimension(100, 35));

        // Set background and foreground for btnLogin
        btnLogin.setBackground(new Color(0, 123, 255)); // Xanh
        btnLogin.setForeground(Color.WHITE); // Trắng
        btnLogin.setFocusPainted(false);

        // Set background and foreground for btnExit
        btnExit.setBackground(Color.WHITE);
        btnExit.setForeground(Color.BLACK);

        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);

        lblStatus.setForeground(Color.RED);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 123, 255));
        JLabel titleLabel = new JLabel("Phần mềm quản lý chuỗi cửa hàng Xe Đạp");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);

        // Login form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Tên tài khoản:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtPassword, gbc);

        // Remember me
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(chkRememberMe, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        // Progress bar
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(progressBar, gbc);

        // Status label
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(lblStatus, gbc);

        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.add(new JLabel("© 2025 IE303.P21 - Công nghệ Java"));
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        btnLogin.addActionListener(e -> performLogin());
        btnExit.addActionListener(e -> System.exit(0));

        // Enter key support
        KeyListener enterKeyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
        };

        txtUsername.addKeyListener(enterKeyListener);
        txtPassword.addKeyListener(enterKeyListener);

        // Clear status when typing
        txtUsername.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) { clearStatus(); }
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
        });

        txtPassword.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) { clearStatus(); }
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
        });
    }

    private void setupWindow() {
        setTitle("Đăng nhập - Phần mềm quản lý chuỗi cửa hàng Xe đạp");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);

        // Set focus to username field
        SwingUtilities.invokeLater(() -> txtUsername.requestFocus());
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Phải nhập đầy đủ tên tài khoản và mật khẩu!");
            return;
        }

        // Show loading state
        setLoginInProgress(true);

        // Perform authentication in background thread
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.authenticate(username, password);
            }

            @Override
            protected void done() {
                setLoginInProgress(false);
                try {
                    User user = get();
                    onLoginSuccess(user);
                } catch (InterruptedException | ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof ValidationException) {
                        showError(cause.getMessage());
                    } else {
                        showError("Đăng nhập thất bại. Xin vui lòng thử lại");
                    }
                    txtPassword.setText("");
                    txtPassword.requestFocus();
                }
            }
        };

        worker.execute();
    }

    private void onLoginSuccess(User user) {
        SessionManager.getInstance().setCurrentUser(user);
        showSuccess("Đăng nhập thành công, chào mừng " + user.getUsername());

        // Small delay to show success message
        Timer timer = new Timer(1000, e -> {
            new MainFrame().setVisible(true);
            dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void setLoginInProgress(boolean inProgress) {
        btnLogin.setEnabled(!inProgress);
        txtUsername.setEnabled(!inProgress);
        txtPassword.setEnabled(!inProgress);
        progressBar.setVisible(inProgress);

        if (inProgress) {
            lblStatus.setText("Authenticating...");
            lblStatus.setForeground(Color.BLUE);
        }
    }

    private void showError(String message) {
        lblStatus.setText(message);
        lblStatus.setForeground(Color.RED);
    }

    private void showSuccess(String message) {
        lblStatus.setText(message);
        lblStatus.setForeground(new Color(0, 128, 0));
    }

    private void clearStatus() {
        lblStatus.setText(" ");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException
                    | UnsupportedLookAndFeelException e) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    
                }
            }
            new LoginView().setVisible(true);
        });
    }
}