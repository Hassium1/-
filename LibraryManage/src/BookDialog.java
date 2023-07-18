import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class BookDialog extends JDialog {
    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel authorLabel;
    private JLabel publisherLabel;
    private JLabel borrowedLabel;

    private JTextField idField;
    private JTextField nameField;
    private JTextField authorField;
    private JTextField publisherField;
    private JTextField borrowedField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton searchButton;


    public BookDialog(Frame owner, String title, boolean modal) {
//        调用父类的构造函数
//        创建子类的对象时，Java虚拟机首先执行父类的构造方法，然后再执行子类的构造方法。
//        在多级继承的情况下，将从继承树的最上层的父类开始，依次执行各个类的构造方法，这可以保证子类对象从所有直接或间接父类中继承的实例变量都被正确地初始化
        super(owner, title, modal);
        setSize(800, 600);
        setLocationRelativeTo(null);
//        关闭窗口，释放对话框的所有资源
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        idLabel = new JLabel("编号：");
        nameLabel = new JLabel("名称：");
        authorLabel = new JLabel("作者：");
        publisherLabel = new JLabel("出版社：");




        idField = new JTextField();
        nameField = new JTextField();
        authorField = new JTextField();
        publisherField = new JTextField();

        addButton = new JButton("新增");
        updateButton = new JButton("修改");
        deleteButton = new JButton("删除");
        searchButton = new JButton("查找");

        // 创建表格
        String[] columnNames = {"编号", "名称", "作者", "出版社", "借阅状态"};
        Object[][] rowData = {};
        DefaultTableModel tableModel = new DefaultTableModel(rowData, columnNames);
        JTable table = new JTable(tableModel);


// 调用loadToTable方法将数据添加到表格中
        DataManager.getInstance().BookloadToTable(tableModel);


        JScrollPane scrollPane = new JScrollPane(table);

        // 定时更新表格
        Timer timer = new Timer(1000, e -> {
            int[] selectedRows = table.getSelectedRows();
            table.clearSelection();
            DataManager.getInstance().BookloadToTable(tableModel);
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
        inputPanel.add(authorLabel);
        inputPanel.add(authorField);
        inputPanel.add(publisherLabel);
        inputPanel.add(publisherField);


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

//  新增功能
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String author = authorField.getText();
                String publisher = publisherField.getText();
                boolean borrowed = false;

                Book book = new Book(id, name, author, publisher, borrowed);
                DataManager dataManager = DataManager.getInstance();
                dataManager.addBook(book);


                idField.setText("");
                nameField.setText("");
                authorField.setText("");
                publisherField.setText("");
            }
        });
//  删除功能
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
                    DataManager.getInstance().deleteBookById(id);
                    // 在表格中删除该行
                    tableModel.removeRow(selectedRows[i]);
                }
            }
        });

//   查找功能
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = JOptionPane.showInputDialog("请输入要查找的关键字");

                DataManager dataManager = DataManager.getInstance();
                List<Book> resultList = dataManager.findBook(keyword);


                if (resultList.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "没有找到符合条件的图书！");
                } else {
                    // 先清空表格的所有选中状态
                    table.clearSelection();
                    // 遍历结果集，将匹配的图书在表格中标记
                    for (Book book : resultList) {
                        int index = tableModel.getDataVector().indexOf(new Vector<>(Arrays.asList(book.getId(), book.getName(), book.getAuthor(), book.getPublisher(), book.isBorrowed() ? "已借出" : "未借出")));
                        if (index != -1) {
                            table.addRowSelectionInterval(index, index);
                        }
                    }
                }
            }
        });

// 修改功能
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "请先选择要修改的图书！");
                    return;
                }
                //检查是否有空缺的项目
                if (idField.getText().isEmpty() || nameField.getText().isEmpty() || authorField.getText().isEmpty() || publisherField.getText().isEmpty() ) {
                    JOptionPane.showMessageDialog(BookDialog.this, "请不要留下空的信息");
                    return;
                }

                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String author = authorField.getText();
                String publisher = publisherField.getText();
                boolean borrowed = false;

                // 获取选中行的图书编号
                int bookId = (int) table.getValueAt(selectedRow, 0);
                // 在 DataManager 中更新该图书信息
                DataManager.getInstance().updateBookById(bookId, id, name, author, publisher, borrowed);
                // 更新表格中该行的数据
                table.setValueAt(id, selectedRow, 0);
                table.setValueAt(name, selectedRow, 1);
                table.setValueAt(author, selectedRow, 2);
                table.setValueAt(publisher, selectedRow, 3);
                table.setValueAt(borrowed ? "已借出" : "未借出", selectedRow, 4);
            }
        });
    }
        public static void main(String[] args) {
                BookDialog dialog = new BookDialog(null, "图书信息管理", true);
                dialog.setVisible(true);

            }

        }
