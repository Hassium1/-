import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.List;


public class BorrowDialog extends JDialog {
    private JLabel bookIdLabel;
    private JLabel bookNameLabel;
    private JLabel studentIdLabel;
    private JLabel studentNameLabel ;
    private JLabel borrowDateLabel;
    private JLabel returnDateLabel;

    private JTextField bookIdField;
    private JTextField bookNameField;
    private JTextField studentIdField;
    private JTextField studentNameField;
    private JTextField borrowDateField;
    private JTextField returnDateField;

    private JButton borrowButton;
    private JButton returnButton;
    private JButton searchButton;
    private JButton updateButton;

    private JTable table;
    private DefaultTableModel model;



    public BorrowDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        setSize(800, 600);
//        设置对话框窗口位置，null则在中央
        setLocationRelativeTo(null);
//        关闭窗口，释放对话框的所有资源
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        bookIdLabel = new JLabel("图书编号：");
        bookNameLabel = new JLabel("图书名字：");
        studentIdLabel = new JLabel("学生学号：");
        studentNameLabel=new JLabel("学生名字");
        borrowDateLabel = new JLabel("借阅日期：");
        returnDateLabel = new JLabel("归还日期：");

        bookIdField= new JTextField();
        bookNameField = new JTextField();
        studentIdField= new JTextField();
        studentNameField=new JTextField();
        borrowDateField = new JTextField();
        returnDateField = new JTextField();

        borrowButton = new JButton("借阅");
        returnButton = new JButton("归还");
        searchButton = new JButton("查询");
        updateButton = new JButton("修改");



//         创建表格
        String[] columnNames = {"图书编号", "图书名字","学生学号", "学生名字", "借阅日期", "归还日期"};
        Object[][] rowData = {};
        DefaultTableModel tableModel = new DefaultTableModel(rowData, columnNames);
        JTable table = new JTable(tableModel);

//         调用loadToTable方法将数据添加到表格中
        DataManager.getInstance().BorrowRecordloadToTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);

//         定时更新表格
        Timer timer = new Timer(1000, e -> {
            int[] selectedRows = table.getSelectedRows();
            table.clearSelection();
            DataManager.getInstance().BorrowRecordloadToTable(tableModel);
            for (int row : selectedRows) {
                table.addRowSelectionInterval(row, row);
            }
        });
        timer.start();

        // 设置布局
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new GridLayout(6, 2));
        topPanel.add(bookIdLabel);
        topPanel.add(bookIdField);
        topPanel.add(bookNameLabel);
        topPanel.add(bookNameField);
        topPanel.add(studentIdLabel);
        topPanel.add(studentIdField);
        topPanel.add(studentNameLabel);
        topPanel.add(studentNameField);
        topPanel.add(borrowDateLabel);
        topPanel.add(borrowDateField);
        topPanel.add(returnDateLabel);
        topPanel.add(returnDateField);


        JPanel bottomPanel=new JPanel();
        bottomPanel.add(borrowButton);
        bottomPanel.add(returnButton);
        bottomPanel.add(searchButton);
        bottomPanel.add(updateButton);



        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel,BorderLayout.SOUTH);

//         添加监听器
        borrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int bookId = Integer.parseInt(bookIdField.getText());
                String bookName = bookNameField.getText();
                int studentId = Integer.parseInt(studentIdField.getText());
                String studentName = studentNameField.getText();
                String borrowDate = borrowDateField.getText();
                String returnDate = returnDateField.getText();

//                 检查输入的日期格式是否正确
                if (!isValidDate(borrowDate) || !isValidDate(returnDate)) {
                    JOptionPane.showMessageDialog(BorrowDialog.this, "日期格式不正确，请输入yyyy-MM-dd格式的日期");
                    return;
                }
                BorrowRecord record = new BorrowRecord(bookId, bookName,studentId,studentName, borrowDate, returnDate);
                DataManager dataManager = DataManager.getInstance();
//                 查找输入的图书信息是否存在
//                通过ID或者名字找到然后保存在表格中
                List<Book> booksofid = dataManager.findBook(String.valueOf(bookId));
                List<Book> booksofname = dataManager.findBook(String.valueOf(bookName));
                if (booksofid.isEmpty()||booksofname.isEmpty()) {
                    JOptionPane.showMessageDialog(BorrowDialog.this, "该图书不存在，请重新输入");
                    return;
                }


//                 查找输入的学生信息是否存在
                List<Student> studentsofid = dataManager.findStudent(String.valueOf(studentId));
                List<Student> studentsofname = dataManager.findStudent(String.valueOf(studentName));
                if (studentsofid.isEmpty()||studentsofname.isEmpty()) {
                    JOptionPane.showMessageDialog(BorrowDialog.this, "学生不存在，请重新输入");
                    return;
                }
//                 判断借出时间是否小于归还时间
                if (borrowDate.compareTo(returnDate) >= 0) {
                    JOptionPane.showMessageDialog(BorrowDialog.this, "借还日期输入错误，请重新输入");
                    return;
                }

                // 将借阅记录添加到列表中
                dataManager.addBorrow(record);
                // 修改图书状态为已借出
                Book book = booksofid.get(0);
                book.setBorrowed(true);
                dataManager.updateBookById(book.getId(), book.getId(), book.getName(), book.getAuthor(), book.getPublisher(), book.isBorrowed());
                // 清空输入框
                bookIdField.setText("");
                bookNameField.setText("");
                studentIdField.setText("");
                studentNameField.setText("");
                borrowDateField.setText("");
                returnDateField.setText("");
            }
        });
// 归还
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取选中的行
                int[] selectedRows = table.getSelectedRows();
                // 循环删除选中的借阅记录
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    // 获取选中行的图书编号
                    int id = (int) table.getValueAt(selectedRows[i], 0);
                    // 在 DataManager 中删除该借阅记录
                    DataManager.getInstance().deleteBorrow(id);
                    // 在表格中删除该行
                    tableModel.removeRow(selectedRows[i]);
                    List<Book> books = DataManager.getInstance().findBook(String.valueOf(id));
                    if (!books.isEmpty()) {
                        Book book = books.get(0);
                        book.setBorrowed(false);
                        DataManager.getInstance().updateBookById(book.getId(), book.getId(), book.getName(), book.getAuthor(), book.getPublisher(), book.isBorrowed());
                        JOptionPane.showMessageDialog(BorrowDialog.this, "归还成功");
                    }
                }

                }

        });
//查找
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = JOptionPane.showInputDialog("请输入要查找的关键字");

                if (keyword.isEmpty()) {
                    JOptionPane.showMessageDialog(BorrowDialog.this, "请输入要查找的关键字！");
                    return;
                }
                DataManager dataManager = DataManager.getInstance();
                java.util.List<BorrowRecord> resultList = dataManager.findBorrow(keyword);

                if (resultList.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "没有找到符合条件的图书！");
                } else {
                    // 先清空表格的所有选中状态
                    table.clearSelection();
                    // 遍历结果集，将匹配的图书在表格中标记
                    for (BorrowRecord borrowRecord : resultList) {
                        int index = tableModel.getDataVector().indexOf(new Vector<>(Arrays.asList(borrowRecord.getBookId(), borrowRecord.getBookName(), borrowRecord.getStudentName(), borrowRecord.getBorrowDate(), borrowRecord.getReturnDate())));
                        if (index != -1) {
                            table.addRowSelectionInterval(index, index);
                        }
                    }
                }
            }
        });
//  修改
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "请先选择要修改的记录！");
                    return;
                }
                int newbookId = Integer.parseInt(bookIdField.getText());
                String bookName = bookNameField.getText();
                int studentId = Integer.parseInt(studentIdField.getText());
                String studentName = studentNameField.getText();
                String borrowDate = borrowDateField.getText();
                String returnDate = returnDateField.getText();


                // 获取选中行的图书编号
                int bookId = (int) table.getValueAt(selectedRow, 0);
                // 在 DataManager 中更新该图书信息
                DataManager.getInstance().updateBorrowRecordById(bookId,newbookId, bookName,studentId, studentName, borrowDate,returnDate);
                // 更新表格中该行的数据
                table.setValueAt(newbookId, selectedRow, 0);
                table.setValueAt(bookName, selectedRow, 1);
                table.setValueAt(studentId, selectedRow, 2);
                table.setValueAt(studentName, selectedRow, 3);
                table.setValueAt(borrowDate, selectedRow, 4);
                table.setValueAt(returnDate, selectedRow, 5);
            }
        });
    }

    // 判断日期格式是否正确
    private boolean isValidDate(String date) {
        try {
            String[] arr = date.split("-");
            if (arr.length != 3) {
                return false;
            }
            int year = Integer.parseInt(arr[0]);
            int month = Integer.parseInt(arr[1]);
            int day = Integer.parseInt(arr[2]);
            if (year < 1900 || year > 2100 || month < 1 || month > 12 || day < 1 || day > 31) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }

    }
    public static void main(String[] args) {
        BorrowDialog dialog = new BorrowDialog(null, "借阅信息管理", true);
        dialog.setVisible(true);
    }
}
