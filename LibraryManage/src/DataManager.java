import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.table.DefaultTableModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DataManager {
    private static DataManager instance = null; // 单例对象
    private List<Book> books; // 图书列表
    private List<Student> students; // 学生列表
    private List<BorrowRecord> borrowRecords; // 借阅列表

    private ObjectMapper objectMapper; // Jackson库对象

//     构造函数私有化，防止外部实例化
    private DataManager() {
        books = new ArrayList<>();
        students = new ArrayList<>();
        borrowRecords = new ArrayList<>();
//    初始化Jackson库对象
        objectMapper = new ObjectMapper();
    }


    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<BorrowRecord> getBorrows() {
        return borrowRecords;
    }

    public void setBorrows(List<BorrowRecord> borrows) {
        this.borrowRecords = borrows;
    }

    // 获取单例对象
    public static DataManager getInstance() {
        if (instance == null) {
            // 加锁，防止并发创建多个实例
            synchronized (DataManager.class) {
                // 再次检查是否有实例被创建
                if (instance == null) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    // 添加图书
    public void addBook(Book book) {
        books.add(book);
        try {
            objectMapper.writeValue(new File("books.json"), books); // 将整个List写入文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 查找图书
    public List<Book> findBook(String keyword) {
        List<Book> result = new ArrayList<>();
        File file = new File("books.json");
        if (file.exists() && file.length() > 0) {
            try {
                books = objectMapper.readValue(file, new TypeReference<List<Book>>() {});
            } catch (IOException ex) {
                ex.printStackTrace();
                books = new ArrayList<>();
            }
        }
        for (Book book : books) {
            if (String.valueOf(book.getId()).contains(keyword) || book.getName().contains(keyword) || book.getAuthor().contains(keyword)) {
                result.add(book);
            }
        }
        return result;
    }

    // 根据图书编号更新图书信息
    public void updateBookById(int id, int newId, String name, String author, String publisher, boolean borrowed) {
        for (Book book : books) {
            if (book.getId() == id) {
                book.setId(newId);
                book.setName(name);
                book.setAuthor(author);
                book.setPublisher(publisher);
                book.setBorrowed(borrowed);
                break;
            }
        }
        try {
            objectMapper.writeValue(new File("books.json"), books); // 将更新后的图书列表保存到文件中
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 添加学生
    public void addStudent(Student student) {
        students.add(student);
        try {
            objectMapper.writeValue(new File("students.json"), students);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 查找学生
    public List<Student> findStudent(String keyword) {
        List<Student> result = new ArrayList<>();
        File file = new File("students.json");
        if (file.exists() && file.length() > 0) {
            try {
                students = objectMapper.readValue(file, new TypeReference<List<Student>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
                students = new ArrayList<>();
            }
        }
        for (Student student : students) {
            if (String.valueOf(student.getId()).contains(keyword) || student.getName().contains(keyword) || student.getMajor().contains(keyword)) {
                result.add(student);
            }
        }
        return result;
    }


    public void updateStudentById(int id, int newId, String name, String gender, int age, String major) {
        for (Student student : students) {
            if (student.getId() == id) {
                student.setId(newId);
                student.setName(name);
                student.setGender(gender);
                student.setAge(age);
                student.setMajor(major);
                break;
            }
        }
        try {
            objectMapper.writeValue(new File("students.json"), students); // 将更新后的图书列表保存到文件中
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 添加借阅信息
    public void addBorrow(BorrowRecord borrowRecord) {
        borrowRecords.add(borrowRecord);
        try {
            objectMapper.writeValue(new File("borrowRecords.json"), borrowRecords); // 序列化当前对象到文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 查找借阅信息
    public List<BorrowRecord> findBorrow(String keyword) {
        List<BorrowRecord> result = new ArrayList<>();
        File file = new File("borrowRecords.json");
        if (file.exists() && file.length() > 0) {
            // 从文件中读取数据
            try {
                borrowRecords = objectMapper.readValue(file, new TypeReference<List<BorrowRecord>>() {
                });
            } catch (IOException ex) {
                // 处理文件读取异常
                ex.printStackTrace();
                borrowRecords = new ArrayList<>();
            }
        }
        for (BorrowRecord borrowRecord : borrowRecords) {
            if (String.valueOf(borrowRecord.getBookId()).contains(keyword) || borrowRecord.getBookName().contains(keyword) || borrowRecord.getStudentName().contains(keyword)) {
                result.add(borrowRecord);
            }
        }
        return result;

    }

    public void updateBorrowRecordById(int bookId, int newbookId, String bookName, int studentId, String studentName, String borrowDate, String returnDate) {
        for (BorrowRecord borrowRecord : borrowRecords) {
            if (borrowRecord.getBookId() == bookId) {
                borrowRecord.setBookId(newbookId);
                borrowRecord.setBookName(studentName);
                borrowRecord.setStudentId(studentId);
                borrowRecord.setStudentName(studentName);
                borrowRecord.setBorrowDate(borrowDate);
                borrowRecord.setReturnDate(returnDate);
                break;
            }
        }
        try {
            objectMapper.writeValue(new File("borrowRecords.json"), borrowRecords); // 将更新后的图书列表保存到文件中
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 根据图书编号删除图书
    public void deleteBookById(int id) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == id) {
                books.remove(i);
                break;
            }
        }
        try {
            objectMapper.writeValue(new File("books.json"), books); // 序列化当前对象到文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 删除学生信息
    public void deleteStuedentById(int id) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == id) {
                students.remove(i);
                break;
            }
        }
        try {
            objectMapper.writeValue(new File("students.json"), students); // 序列化当前对象到文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 删除借阅信息
    public void deleteBorrow(int id) {
        for (int i = 0; i < borrowRecords.size(); i++) {
            if (borrowRecords.get(i).getBookId() == id) {
                borrowRecords.remove(i);
                break;
            }
        }
        try {
            objectMapper.writeValue(new File("borrowRecords.json"), borrowRecords); // 序列化当前对象到文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void BookloadToTable(DefaultTableModel tableModel) {
        File file = new File("books.json");
        if (file.exists() && file.length() > 0) {
            try {
                books = objectMapper.readValue(file, new TypeReference<List<Book>>() {
                });
            } catch (IOException ex) {
                // 处理文件读取异常
                ex.printStackTrace();
                books = new ArrayList<>();
            }
        }

        // 清空表格
        tableModel.setRowCount(0);
        // 循环添加数据到表格中
        for (Book book : books) {
            Object[] rowData = {book.getId(), book.getName(), book.getAuthor(), book.getPublisher(), book.isBorrowed() ? "已借出" : "未借出"};
            tableModel.addRow(rowData);
        }
    }

    public void BorrowRecordloadToTable(DefaultTableModel tableModel) {
        File file = new File("borrowRecords.json");
        if (file.exists() && file.length() > 0) {
            // 从文件中读取数据
            if (file.exists() && file.length() > 0) {
                try {
                    borrowRecords = objectMapper.readValue(file, new TypeReference<List<BorrowRecord>>() {
                    });
                } catch (IOException ex) {
                    // 处理文件读取异常
                    ex.printStackTrace();
                    borrowRecords = new ArrayList<>();
                }
            }
            // 清空表格
            tableModel.setRowCount(0);
            // 循环添加数据到表格中
            for (BorrowRecord borrowRecord : borrowRecords) {
                Object[] rowData = {borrowRecord.getBookId(), borrowRecord.getBookName(), borrowRecord.getStudentId(), borrowRecord.getStudentName(), borrowRecord.getBorrowDate(), borrowRecord.getReturnDate()};
                tableModel.addRow(rowData);
            }
        }
    }


    public void StudentloadToTable (DefaultTableModel tableModel){
        File file = new File("students.json");
        if (file.exists() && file.length() > 0) {
            // 从文件中读取数据
            try {
                students = objectMapper.readValue(file, new TypeReference<List<Student>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
                students = new ArrayList<>(); // 读取文件失败时创建一个空的List
            }
        }
        // 清空表格
        tableModel.setRowCount(0);
        // 循环添加数据到表格中
        for (Student student : students) {
            Object[] rowData = {student.getId(), student.getName(), student.getGender(), student.getAge(), student.getMajor()};
            tableModel.addRow(rowData);
        }
    }
}


