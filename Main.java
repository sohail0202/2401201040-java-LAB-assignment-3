import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Simple menu-driven student management (single file).
// Save as Main.java -> javac Main.java -> java Main

// Custom exception for missing student
class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String msg) { super(msg); }
}

// Loader thread to simulate loading
class Loader implements Runnable {
    @Override
    public void run() {
        try {
            System.out.print("Loading");
            for (int i = 0; i < 5; i++) {
                Thread.sleep(300);
                System.out.print(".");
            }
            System.out.println();
        } catch (InterruptedException e) {
            System.out.println("Loading interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}

// Simple POJO for Student using wrapper types
class Student {
    Integer rollNo;
    String name;
    String email;
    String course;
    Double marks;

    public Student(Integer rollNo, String name, String email, String course, Double marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.email = email;
        this.course = course;
        this.marks = marks;
    }
}

// Manager that handles add & display using exception handling and loader thread
class StudentManager {
    private final List<Student> students = new ArrayList<>();
    private final Scanner sc;

    public StudentManager(Scanner sc) {
        this.sc = sc;
    }

    public void addStudent() {
        try {
            System.out.print("Enter Roll No (Integer): ");
            String rs = sc.nextLine().trim();
            if (rs.isEmpty()) throw new IllegalArgumentException("Roll No cannot be empty.");
            Integer roll = Integer.valueOf(rs); // wrapper + autoboxing

            // Check duplicate roll
            for (Student s : students) {
                if (s.rollNo.equals(roll)) {
                    System.out.println("A student with this Roll No already exists.");
                    return;
                }
            }

            System.out.print("Enter Name: ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");

            System.out.print("Enter Email: ");
            String email = sc.nextLine().trim();
            if (email.isEmpty()) throw new IllegalArgumentException("Email cannot be empty.");

            System.out.print("Enter Course: ");
            String course = sc.nextLine().trim();
            if (course.isEmpty()) throw new IllegalArgumentException("Course cannot be empty.");

            System.out.print("Enter Marks: ");
            String ms = sc.nextLine().trim();
            if (ms.isEmpty()) throw new IllegalArgumentException("Marks cannot be empty.");
            Double marks = Double.valueOf(ms); // wrapper
            if (marks < 0 || marks > 100) throw new IllegalArgumentException("Marks must be between 0 and 100.");

            // simulate loading
            Thread loader = new Thread(new Loader());
            loader.start();
            try { loader.join(); } catch (InterruptedException ie) {
                System.out.println("Interrupted while loading.");
                Thread.currentThread().interrupt();
            }

            // add student
            students.add(new Student(roll, name, email, course, marks));
            System.out.println("Student added successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Enter valid integers/floats for roll and marks.");
        } catch (IllegalArgumentException e) {
            System.out.println("Input Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
        } finally {
            System.out.println("Input process completed.");
        }
    }

    // Display a student by roll no
    public void displayStudent() {
        try {
            if (students.isEmpty()) throw new StudentNotFoundException("No student records available.");

            System.out.print("Enter Roll No to display: ");
            String rs = sc.nextLine().trim();
            if (rs.isEmpty()) throw new IllegalArgumentException("Roll No cannot be empty.");
            Integer roll = Integer.valueOf(rs);

            Student found = null;
            for (Student s : students) {
                if (s.rollNo.equals(roll)) { found = s; break; }
            }
            if (found == null) throw new StudentNotFoundException("Student with Roll No " + roll + " not found.");

            System.out.println("Roll No: " + found.rollNo);
            System.out.println("Name: " + found.name);
            System.out.println("Email: " + found.email);
            System.out.println("Course: " + found.course);
            System.out.println("Marks: " + found.marks);
            System.out.println("Grade: " + calculateGrade(found.marks));

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format for Roll No.");
        } catch (StudentNotFoundException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
        }
    }

    private String calculateGrade(Double marks) {
        if (marks == null) return "N/A";
        if (marks >= 90) return "A";
        if (marks >= 75) return "B";
        if (marks >= 60) return "C";
        if (marks >= 40) return "D";
        return "F";
    }
}

// MAIN: menu-driven loop (Add Student, Display Student, Exit)
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentManager mgr = new StudentManager(sc);

        while (true) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1. Add Student");
            System.out.println("2. Display Student");
            System.out.println("3. Exit");
            System.out.print("Choose option (1-3): ");

            String choice = sc.nextLine().trim();
            if (choice.isEmpty()) {
                System.out.println("Please enter a choice.");
                continue;
            }

            switch (choice) {
                case "1":
                    mgr.addStudent();
                    break;
                case "2":
                    mgr.displayStudent();
                    break;
                case "3":
                    System.out.println("Exiting program. Goodbye!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid option. Enter 1, 2 or 3.");
            }
        }
    }
}
