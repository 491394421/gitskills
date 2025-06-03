import java.util.*;

class Student {
    private String name;
    private String studentId;
    private Map<String, Double> courseScores;

    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
        this.courseScores = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getStudentId() {
        return studentId;
    }

    public Map<String, Double> getCourseScores() {
        return courseScores;
    }

    public void addCourseScore(String course, double score) {
        courseScores.put(course, score);
    }

    public double getScoreByCourse(String course) {
        return courseScores.getOrDefault(course, -1.0);
    }
}

class GradeManager {
    private Map<String, Student> students;

    public GradeManager() {
        this.students = new HashMap<>();
    }

    public boolean addStudent(String name, String studentId) {
        if (students.containsKey(studentId)) {
            return false;
        }
        students.put(studentId, new Student(name, studentId));
        return true;
    }

    public boolean addScore(String studentId, String course, double score) {
        if (!students.containsKey(studentId)) {
            return false;
        }
        students.get(studentId).addCourseScore(course, score);
        return true;
    }

    public List<Student> getStudentsByName(String name) {
        List<Student> result = new ArrayList<>();
        for (Student student : students.values()) {
            if (student.getName().equals(name)) {
                result.add(student);
            }
        }
        return result;
    }

    public Student getStudentById(String studentId) {
        return students.get(studentId);
    }

    public List<Student> getStudentsByCourse(String course) {
        List<Student> result = new ArrayList<>();
        for (Student student : students.values()) {
            if (student.getCourseScores().containsKey(course)) {
                result.add(student);
            }
        }
        return result;
    }

    public Map<String, Double> getCourseStatistics(String course) {
        List<Student> studentsInCourse = getStudentsByCourse(course);
        if (studentsInCourse.isEmpty()) {
            return null;
        }

        double sum = 0;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (Student student : studentsInCourse) {
            double score = student.getScoreByCourse(course);
            sum += score;
            max = Math.max(max, score);
            min = Math.min(min, score);
        }

        Map<String, Double> statistics = new HashMap<>();
        statistics.put("average", sum / studentsInCourse.size());
        statistics.put("max", max);
        statistics.put("min", min);
        return statistics;
    }
}

public class StudentGradeManager {
    private static final Scanner scanner = new Scanner(System.in);
    private static final GradeManager gradeManager = new GradeManager();

    public static void main(String[] args) {
        while (true) {
            displayMainMenu();
            int choice = getValidatedIntInput("请输入选项序号：", 1, 4);
            
            switch (choice) {
                case 1:
                    recordGrade();
                    break;
                case 2:
                    queryGrades();
                    break;
                case 3:
                    statisticsGrades();
                    break;
                case 4:
                    System.out.println("感谢使用学生成绩管理系统，再见！");
                    scanner.close();
                    return;
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("=================================");
        System.out.println("欢迎使用学生成绩管理系统");
        System.out.println("=================================");
        System.out.println("请选择操作：");
        System.out.println("1. 记录学生成绩");
        System.out.println("2. 查询学生成绩");
        System.out.println("3. 统计课程成绩");
        System.out.println("4. 退出系统");
    }

    private static void recordGrade() {
        System.out.println("\n===== 记录学生成绩 =====");
        String name = getNonEmptyInput("请输入学生姓名：");
        String studentId = getNonEmptyInput("请输入学生学号：");

        if (!gradeManager.addStudent(name, studentId)) {
            System.out.println("错误：学号已存在！");
            return;
        }

        String course = getNonEmptyInput("请输入课程名称：");
        double score = getValidatedDoubleInput("请输入成绩（0-100）：", 0, 100);

        if (gradeManager.addScore(studentId, course, score)) {
            System.out.println("成绩已成功记录！");
        } else {
            System.out.println("错误：记录成绩失败！");
        }
    }

    private static void queryGrades() {
        System.out.println("\n===== 查询学生成绩 =====");
        System.out.println("请选择查询方式：");
        System.out.println("1. 按学生姓名查询");
        System.out.println("2. 按学生学号查询");
        System.out.println("3. 按课程名称查询");
        int queryType = getValidatedIntInput("请输入选项序号：", 1, 3);

        switch (queryType) {
            case 1:
                queryByName();
                break;
            case 2:
                queryById();
                break;
            case 3:
                queryByCourse();
                break;
        }
    }

    private static void queryByName() {
        String name = getNonEmptyInput("请输入学生姓名：");
        List<Student> students = gradeManager.getStudentsByName(name);
        
        if (students.isEmpty()) {
            System.out.println("未找到该学生的成绩记录！");
            return;
        }

        for (Student student : students) {
            Map<String, Double> scores = student.getCourseScores();
            for (Map.Entry<String, Double> entry : scores.entrySet()) {
                System.out.printf("姓名：%s, 学号：%s, 课程：%s, 成绩：%.1f\n",
                        student.getName(), student.getStudentId(), entry.getKey(), entry.getValue());
            }
        }
    }

    private static void queryById() {
        String studentId = getNonEmptyInput("请输入学生学号：");
        Student student = gradeManager.getStudentById(studentId);
        
        if (student == null) {
            System.out.println("未找到该学生的成绩记录！");
            return;
        }

        Map<String, Double> scores = student.getCourseScores();
        if (scores.isEmpty()) {
            System.out.println("该学生没有成绩记录！");
            return;
        }

        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            System.out.printf("姓名：%s, 学号：%s, 课程：%s, 成绩：%.1f\n",
                    student.getName(), student.getStudentId(), entry.getKey(), entry.getValue());
        }
    }

    private static void queryByCourse() {
        String course = getNonEmptyInput("请输入课程名称：");
        List<Student> students = gradeManager.getStudentsByCourse(course);
        
        if (students.isEmpty()) {
            System.out.println("未找到该课程的成绩记录！");
            return;
        }

        for (Student student : students) {
            double score = student.getScoreByCourse(course);
            System.out.printf("姓名：%s, 学号：%s, 课程：%s, 成绩：%.1f\n",
                    student.getName(), student.getStudentId(), course, score);
        }
    }

    private static void statisticsGrades() {
        System.out.println("\n===== 统计课程成绩 =====");
        String course = getNonEmptyInput("请输入课程名称：");
        Map<String, Double> stats = gradeManager.getCourseStatistics(course);

        if (stats == null) {
            System.out.println("未找到该课程的成绩记录！");
            return;
        }

        System.out.printf("课程：%s\n", course);
        System.out.printf("平均分：%.2f\n", stats.get("average"));
        System.out.printf("最高分：%.1f\n", stats.get("max"));
        System.out.printf("最低分：%.1f\n", stats.get("min"));
    }

    private static String getNonEmptyInput(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("输入不能为空，请重新输入！");
            }
        } while (input.isEmpty());
        return input;
    }

    private static int getValidatedIntInput(String prompt, int min, int max) {
        int value;
        do {
            System.out.print(prompt);
            while (!scanner.hasNextInt()) {
                System.out.println("输入无效，请输入一个整数！");
                scanner.next();
                System.out.print(prompt);
            }
            value = scanner.nextInt();
            scanner.nextLine(); // 消耗换行符
            if (value < min || value > max) {
                System.out.printf("输入无效，请输入%d-%d之间的整数！\n", min, max);
            }
        } while (value < min || value > max);
        return value;
    }

    private static double getValidatedDoubleInput(String prompt, double min, double max) {
        double value;
        do {
            System.out.print(prompt);
            while (!scanner.hasNextDouble()) {
                System.out.println("输入无效，请输入一个数字！");
                scanner.next();
                System.out.print(prompt);
            }
            value = scanner.nextDouble();
            scanner.nextLine(); // 消耗换行符
            if (value < min || value > max) {
                System.out.printf("输入无效，请输入%.1f-%.1f之间的数字！\n", min, max);
            }
        } while (value < min || value > max);
        return value;
    }
}    