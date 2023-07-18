import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoginFrame extends JFrame implements ActionListener {
    private JLabel userLabel, passwordLabel;
    private JTextField userText;
    private JPasswordField passwordText;
    private JButton loginButton, resetButton, registerButton;

    public LoginFrame() {
        setTitle("登录");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(null);

        userLabel = new JLabel("用户名:");
        userLabel.setBounds(10, 10, 80, 25);
        panel.add(userLabel);

        userText = new JTextField(20);
        userText.setBounds(100, 10, 160, 25);
        panel.add(userText);

        passwordLabel = new JLabel("密码:");
        passwordLabel.setBounds(10, 40, 80, 25);
        panel.add(passwordLabel);

        passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 40, 160, 25);
        panel.add(passwordText);

        loginButton = new JButton("登录");
        loginButton.setBounds(10, 80, 80, 25);
        loginButton.addActionListener(this);
        panel.add(loginButton);

        resetButton = new JButton("清空");
        resetButton.setBounds(100, 80, 80, 25);
        resetButton.addActionListener(this);
        panel.add(resetButton);

        registerButton = new JButton("注册");
        registerButton.setBounds(190, 80, 80, 25);
        registerButton.addActionListener(this);
        panel.add(registerButton);

        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String user = userText.getText();
            String password = new String(passwordText.getPassword());

//             读取用户信息文件
//            使用ObjectMapper来转化对象为Json
            ObjectMapper objectMapper = new ObjectMapper();
            List<User> userList = new ArrayList<>();
            try {
                File file = new File("users.json");
                if (file.exists()) {
                    userList = objectMapper.readValue(file, new TypeReference<List<User>>() {
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // 验证用户信息
            boolean isValidUser = false;
            for (User u : userList) {
                if (u.getUsername().equals(user) && u.getPassword().equals(password)) {
                    isValidUser = true;
                    break;
                }
            }

            // 根据验证结果进行处理
            if (isValidUser) {
                dispose();
                new MainFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == resetButton) {
            userText.setText("");
            passwordText.setText("");
        } else if (e.getSource() == registerButton) {
            dispose();
            new RegisterFrame();
            this.setVisible(false);
        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }

}
