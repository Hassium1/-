
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class StudentDialog extends JDialog {
    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel genderLabel;
    private JLabel ageLabel;
    private JLabel majorLabel;
    private JTextField idField;
    private JTextField nameField;
    private JTextField genderField;
    private JTextField ageField;
    private JTextField majorField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton searchButton;





    public StudentDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        idLabel = new JLabel("学号：");
        nameLabel = new JLabel("姓名：");
        genderLabel = new JLabel("性别：");
        ageLabel = new JLabel("年龄：");
        majorLabel = new JLabel("专业：");

        idField = new JTextField();
        nameField = new JTextField();
        genderField = new JTextField();
        ageField = new JTextField();
        majorField = new JTextField();

        addButton = new JButton("新增");
        updateButton = new JButton("修改");
        deleteButton = new JButton("删除");
        searchButton = new JButton("查找");



        // 创建表格
        String[] columnNames = {"学号", "姓名", "性别", "年龄", "专业"};
        Object[][] rowData = {};
        DefaultTableModel tableModel = new DefaultTableModel(rowData, columnNames);
        JTable table = new JTable(tableModel);


// 调用loadToTable方法将数据添加到表格中
        DataManager.getInstance().StudentloadToTable(tableModel);



        JScrollPane scrollPane = new JScrollPane(table);

        // 定时更新表格
        Timer timer = new Timer(1000, e -> {
            int[] selectedRows = table.getSelectedRows();
            table.clearSelection();
            DataManager.getInstance().StudentloadToTable(tableModel);
            for (int row : selectedRows) {
                table.addRowSelectionInterval(row, row);
            }
        });
        timer.start();

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(genderLabel);
        inputPanel.add(genderField);
        inputPanel.add(ageLabel);
        inputPanel.add(ageField);
        inputPanel.add(majorLabel);
        inputPanel.add(majorField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);



        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String gender = genderField.getText();
                int age = Integer.parseInt(ageField.getText());
                String major = majorField.getText();
                if (idField.getText().isEmpty() || nameField.getText().isEmpty() || genderField.getText().isEmpty() || ageField.getText().isEmpty()||majorField.getText().isEmpty() ) {
                    JOptionPane.showMessageDialog(StudentDialog.this, "请不要留下空的信息");
                    return;
                }

                Student student = new Student(id, name, gender, age, major);
                DataManager dataManager = DataManager.getInstance();
                dataManager.addStudent(student);

                idField.setText("");
                nameField.setText("");
                genderField.setText("");
                ageField.setText("");
                majorField.setText("");
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "请先选择要修改的图书！");
                    return;
                }
                if (idField.getText().isEmpty() || nameField.getText().isEmpty() || genderField.getText().isEmpty() || ageField.getText().isEmpty()||majorField.getText().isEmpty() ) {
                    JOptionPane.showMessageDialog(StudentDialog.this, "请不要留下空的信息");
                    return;
                }
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String gender = genderField.getText();
                int age = Integer.parseInt(ageField.getText());
                String major = majorField.getText();
                boolean borrowed = false;

                // 获取选中行的图书编号
                int bookId = (int) table.getValueAt(selectedRow, 0);
                // 在 DataManager 中更新该图书信息
                DataManager.getInstance().updateStudentById(bookId, id, name, gender, age, major);
        // 更新表格和输入框
                    table.setValueAt(id, selectedRow, 0);
                    table.setValueAt(name, selectedRow, 1);
                    table.setValueAt(gender, selectedRow, 2);
                    table.setValueAt(age, selectedRow, 3);
                    table.setValueAt(major, selectedRow, 4);

                    idField.setText("");
                    nameField.setText("");
                    genderField.setText("");
                    ageField.setText("");
                    majorField.setText("");

            }
        });




        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取选中的行
                int[] selectedRows = table.getSelectedRows();
                // 循环删除选中的图书
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    // 获取选中行的图书编号
                    int id = (int) table.getValueAt(selectedRows[i], 0);
                    // 在 DataManager 中删除该图书
                    DataManager.getInstance().deleteStuedentById(id);
                    // 在表格中删除该行
                    tableModel.removeRow(selectedRows[i]);
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = JOptionPane.showInputDialog("请输入要查找的关键字");
                DataManager dataManager = DataManager.getInstance();
                java.util.List<Student> resultList = dataManager.findStudent(keyword);



                if (resultList.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "没有找到符合条件的学生！");
                } else {
                    // 先清空表格的所有选中状态
                    table.clearSelection();
                    for (Student student : resultList) {
                        int index = tableModel.getDataVector().indexOf(new Vector<>(Arrays.asList(student.getId(), student.getName(), student.getGender(), student.getAge(), student.getMajor() )));
                        if (index != -1) {
                            table.addRowSelectionInterval(index, index);
                        }
                    }
                }
            }
        });

    }



    public static void main(String[] args) {
        StudentDialog dialog = new StudentDialog(null, "学生信息管理", true);
        dialog.setVisible(true);
    }
}