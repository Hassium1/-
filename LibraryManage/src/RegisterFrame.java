import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RegisterFrame extends JFrame implements ActionListener{
    private JLabel userLabel, passwordLabel;
    private JTextField userText;
    private JPasswordField passwordText;
    private JButton registerButton;

    private ObjectMapper objectMapper; // Jackson库对象

    public RegisterFrame(){
        setTitle("注册");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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

        registerButton = new JButton("注册");
        registerButton.setBounds(100, 80, 80, 25);
        registerButton.addActionListener(this);
        panel.add(registerButton);

        objectMapper = new ObjectMapper(); // 初始化Jackson库对象

        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == registerButton){
            String user = userText.getText();
            String password = new String(passwordText.getPassword());

            // 读取已注册用户信息
            List<User> users = new ArrayList<>();
            File file = new File("users.json");

            if(file.exists() && file.length() != 0){
                try {
                    JsonNode rootNode = objectMapper.readTree(file);
                    if (rootNode.isArray()) {
                        User[] userArray = objectMapper.readValue(rootNode.traverse(), User[].class);
                        users = new ArrayList<>(Arrays.asList(userArray));
                    } else {
                        // 如果 JSON 数据不是数组，则尝试将其转换为单个 User 对象
                        User userObj = objectMapper.readValue(rootNode.traverse(), User.class);
                        users = new ArrayList<>(Collections.singletonList(userObj));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    users = new ArrayList<>(); // 读取文件失败时创建一个空的List
                }
            }else{
                // 如果文件不存在或为空，创建一个空的List
                users = new ArrayList<>();
            }


            // 检查用户名是否已存在
            for (User u : users) {
                if (u.getUsername().equals(user)) {
                    JOptionPane.showMessageDialog(this, "该用户名已被注册，请重新输入");
                    return;
                }
            }

            // 将新用户信息添加到列表中
            User newUser = new User(user, password);
            users.add(newUser);

            // 将用户列表写入文件
            try {
                objectMapper.writeValue(new File("users.json"), users);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(this, "注册成功");
            dispose();
           new LoginFrame();
        }
    }
}
