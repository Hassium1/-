
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {
    private JMenuBar menuBar;
    private JMenu bookMenu;
    private JMenu studentMenu;
    private JMenu borrowMenu;
    private JTabbedPane tabbedPane;
    private JTable bookTable;
    private JTable studentTable;
    private JTable borrowTable;

    public MainFrame() {
        setTitle("图书管理系统");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuBar = new JMenuBar();
        bookMenu = new JMenu("图书信息管理");
        studentMenu = new JMenu("学生信息管理");
        borrowMenu = new JMenu("图书借阅信息管理");

        menuBar.add(bookMenu);
        menuBar.add(studentMenu);
        menuBar.add(borrowMenu);

        setJMenuBar(menuBar);
        bookMenu.add(new AbstractAction("图书信息管理") {
            @Override
            public void actionPerformed(ActionEvent e) {
                BookDialog dialog = new BookDialog(MainFrame.this, "图书信息管理", true);
                dialog.setVisible(true);
            }
        });

        studentMenu.add(new AbstractAction("学生信息管理") {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentDialog dialog = new StudentDialog(MainFrame.this, "学生信息管理", true);
                dialog.setVisible(true);
            }
        });
        borrowMenu.add(new AbstractAction("图书借阅信息管理") {
            @Override
            public void actionPerformed(ActionEvent e) {
                BorrowDialog dialog = new BorrowDialog(MainFrame.this, "借阅信息管理", true);
                dialog.setVisible(true);
            }
        });

        tabbedPane = new JTabbedPane();
        bookTable = new JTable();
        studentTable = new JTable();
        borrowTable = new JTable();

        // 设置表格数据模型
        DefaultTableModel bookTableModel = new DefaultTableModel(new Object[]{"编号", "书名", "作者", "出版社", "状态"}, 0);
        bookTable.setModel(bookTableModel);
        DefaultTableModel studentTableModel = new DefaultTableModel(new Object[]{"学号", "姓名", "性别", "年龄", "专业"}, 0);
        studentTable.setModel(studentTableModel);
        DefaultTableModel borrowTableModel = new DefaultTableModel(new Object[]{"图书编号", "图书名称", "学生学号","学生姓名", "借出时间", "归还时间"}, 0);
        borrowTable.setModel(borrowTableModel);

        // 将数据加载到表格中
        DataManager.getInstance().BookloadToTable(bookTableModel);
        // 定时更新表格
        Timer timer1 = new Timer(1000, e -> {
            int[] selectedRows = bookTable.getSelectedRows();
            bookTable.clearSelection();
            DataManager.getInstance().BookloadToTable(bookTableModel);
            for (int row : selectedRows) {
                bookTable.addRowSelectionInterval(row, row);
            }
        });
        timer1.start();
        DataManager.getInstance().StudentloadToTable(studentTableModel);
        // 定时更新表格
        Timer timer2 = new Timer(1000, e -> {
            int[] selectedRows = studentTable.getSelectedRows();
            studentTable.clearSelection();
            DataManager.getInstance().StudentloadToTable(studentTableModel);
            for (int row : selectedRows) {
                studentTable.addRowSelectionInterval(row, row);
            }
        });
        timer2.start();
        DataManager.getInstance().BorrowRecordloadToTable(borrowTableModel);
        // 定时更新表格
        Timer timer3 = new Timer(1000, e -> {
            int[] selectedRows = borrowTable.getSelectedRows();
            borrowTable.clearSelection();
            DataManager.getInstance().BorrowRecordloadToTable(borrowTableModel);
            for (int row : selectedRows) {
                borrowTable.addRowSelectionInterval(row, row);
            }
        });
        timer3.start();

        // 将表格添加到选项卡中
        tabbedPane.addTab("图书信息", new JScrollPane(bookTable));
        tabbedPane.addTab("学生信息", new JScrollPane(studentTable));
        tabbedPane.addTab("借阅信息", new JScrollPane(borrowTable));

        getContentPane().add(tabbedPane);
                setVisible(true);
            }
}