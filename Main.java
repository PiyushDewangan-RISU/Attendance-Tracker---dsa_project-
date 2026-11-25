
import java.util.*;
import java.io.*;

public class Main {

    static final int MAX_STUDENTS = 500;
    static final int MAX_DAYS = 366;

    static String[] names = new String[MAX_STUDENTS];
    static String[] rollNumbers = new String[MAX_STUDENTS];

    // 2D Array: Rows = Students, Columns = Days
    static String[][] attendance =
            new String[MAX_STUDENTS][MAX_DAYS];

    // HashMap stores roll numbers and their row positions
    static HashMap<String, Integer> studentMap =
            new HashMap<>();

    static int studentCount = 0;
    static int totalDays = 0;

    static Scanner sc = new Scanner(System.in);

    static final String FILE_NAME = "attendance.txt";

    public static void main(String[] args) {

        loadData();

        while (true) {

            System.out.println("\n===== ATTENDANCE TRACKER =====");
            System.out.println("1. Add Student");
            System.out.println("2. Mark Attendance");
            System.out.println("3. View All Students");
            System.out.println("4. View Attendance Percentage");
            System.out.println("5. Sort by Percentage");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");

            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid menu option.");
                continue;
            }

            switch (choice) {

                case 1:
                    addStudent();
                    break;

                case 2:
                    markAttendance();
                    break;

                case 3:
                    viewAllStudents();
                    break;

                case 4:
                    viewPercentage();
                    break;

                case 5:
                    sortByPercentage();
                    break;

                case 6:
                    saveData();
                    System.out.println("Data saved successfully.");
                    System.out.println("Thank you!");
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // 1. Add Student
    static void addStudent() {

        if (studentCount >= MAX_STUDENTS) {
            System.out.println("Student limit reached.");
            return;
        }

        System.out.print("Enter student name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty() || name.contains("|")) {
            System.out.println("Invalid student name.");
            return;
        }

        String roll = String.format("R%03d", studentCount + 101);

        names[studentCount] = name;
        rollNumbers[studentCount] = roll;

        // Initialize attendance array
        for (int i = 0; i < MAX_DAYS; i++) {
            attendance[studentCount][i] = "-";
        }

        studentMap.put(roll, studentCount);

        studentCount++;

        System.out.println(
                "Student added. Roll: " + roll + " | " + name
        );

        saveData();
    }

    // 2. Mark Attendance
    static void markAttendance() {

        if (studentCount == 0) {
            System.out.println("No students registered.");
            return;
        }

        System.out.print("Enter day number: ");

        int day;

        try {
            day = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid day number.");
            return;
        }

        if (day < 1 || day > MAX_DAYS) {
            System.out.println("Invalid day number.");
            return;
        }

        System.out.print("Enter roll number: ");
        String roll = sc.nextLine().trim().toUpperCase();

        // Search student using HashMap
        if (!studentMap.containsKey(roll)) {
            System.out.println("Roll number " + roll + " not found.");
            return;
        }

        int row = studentMap.get(roll);

        System.out.print("Enter attendance (P/A): ");
        String status = sc.nextLine().trim().toUpperCase();

        if (!status.equals("P") && !status.equals("A")) {
            System.out.println("Invalid attendance. Enter P or A.");
            return;
        }

        attendance[row][day - 1] = status;

        if (day > totalDays) {
            totalDays = day;
        }

        System.out.println(
                "Attendance marked for Day " + day + "."
        );

        saveData();
    }

    // 3. View All Students
    static void viewAllStudents() {

        if (studentCount == 0) {
            System.out.println("No students found.");
            return;
        }

        System.out.println("\n===== STUDENT RECORDS =====");

        for (int i = 0; i < studentCount; i++) {

            System.out.println(
                    "Roll: " + rollNumbers[i] +
                    " | Name: " + names[i]
            );

            System.out.print("Attendance: ");

            for (int j = 0; j < totalDays; j++) {

                System.out.print(
                        "Day " + (j + 1) + ": " +
                        attendance[i][j] + "  "
                );
            }

            System.out.println();
        }
    }

    // 4. Calculate Attendance Percentage
    static double calculatePercentage(int row) {

        int presentDays = 0;
        int recordedDays = 0;

        for (int j = 0; j < totalDays; j++) {

            if (attendance[row][j].equals("P")) {
                presentDays++;
                recordedDays++;
            }
            else if (attendance[row][j].equals("A")) {
                recordedDays++;
            }
        }

        if (recordedDays == 0) {
            return 0.0;
        }

        return (presentDays * 100.0) / recordedDays;
    }

    // View Attendance Percentage
    static void viewPercentage() {

        if (studentCount == 0) {
            System.out.println("No students found.");
            return;
        }

        System.out.println("\n===== ATTENDANCE PERCENTAGE =====");

        for (int i = 0; i < studentCount; i++) {

            int present = 0;
            int recorded = 0;

            for (int j = 0; j < totalDays; j++) {

                if (attendance[i][j].equals("P")) {
                    present++;
                    recorded++;
                }
                else if (attendance[i][j].equals("A")) {
                    recorded++;
                }
            }

            double percentage = calculatePercentage(i);

            System.out.printf(
                    "%s | %s | Present: %d/%d | %.1f%%%n",
                    rollNumbers[i],
                    names[i],
                    present,
                    recorded,
                    percentage
            );
        }
    }

    // 5. Sort Students by Attendance Percentage
    static void sortByPercentage() {

        if (studentCount == 0) {
            System.out.println("No students found.");
            return;
        }

        Integer[] order = new Integer[studentCount];

        for (int i = 0; i < studentCount; i++) {
            order[i] = i;
        }

        // Sort in descending order of percentage
        Arrays.sort(order, (a, b) ->
                Double.compare(
                        calculatePercentage(b),
                        calculatePercentage(a)
                )
        );

        System.out.println("\n===== SORTED BY PERCENTAGE =====");

        int rank = 1;

        for (int i : order) {

            int present = 0;
            int recorded = 0;

            for (int j = 0; j < totalDays; j++) {

                if (attendance[i][j].equals("P")) {
                    present++;
                    recorded++;
                }
                else if (attendance[i][j].equals("A")) {
                    recorded++;
                }
            }

            double percentage = calculatePercentage(i);

            System.out.printf(
                    "%d. %s | %s | %d/%d | %.1f%%%n",
                    rank,
                    rollNumbers[i],
                    names[i],
                    present,
                    recorded,
                    percentage
            );

            rank++;
        }
    }

    // 6. Save Data to File
    static void saveData() {

        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(FILE_NAME))) {

            writer.println(studentCount);
            writer.println(totalDays);

            for (int i = 0; i < studentCount; i++) {

                writer.print(rollNumbers[i] + "|" + names[i]);

                for (int j = 0; j < totalDays; j++) {
                    writer.print("|" + attendance[i][j]);
                }

                writer.println();
            }

        } catch (IOException e) {
            System.out.println(
                    "Error saving data: " + e.getMessage()
            );
        }
    }

    // 7. Load Data from File
    static void loadData() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line = reader.readLine();

            if (line == null) {
                return;
            }

            studentCount = Integer.parseInt(line);

            line = reader.readLine();

            if (line == null) {
                studentCount = 0;
                return;
            }

            totalDays = Integer.parseInt(line);

            if (studentCount < 0 || studentCount > MAX_STUDENTS ||
                    totalDays < 0 || totalDays > MAX_DAYS) {

                throw new IOException("Invalid file data.");
            }

            for (int i = 0; i < studentCount; i++) {

                line = reader.readLine();

                if (line == null) {
                    throw new IOException(
                            "Incomplete student records."
                    );
                }

                String[] data = line.split("\\|", -1);

                if (data.length != totalDays + 2) {
                    throw new IOException(
                            "Invalid student record."
                    );
                }

                rollNumbers[i] = data[0];
                names[i] = data[1];

                for (int j = 0; j < MAX_DAYS; j++) {
                    attendance[i][j] = "-";
                }

                for (int j = 0; j < totalDays; j++) {

                    String status = data[j + 2];

                    if (!status.equals("P") &&
                            !status.equals("A") &&
                            !status.equals("-")) {

                        throw new IOException(
                                "Invalid attendance status."
                        );
                    }

                    attendance[i][j] = status;
                }

                studentMap.put(rollNumbers[i], i);
            }

            System.out.println(
                    "Previous attendance data loaded."
            );

        } catch (IOException | NumberFormatException e) {

            System.out.println(
                    "Error loading data: " + e.getMessage()
            );

            studentCount = 0;
            totalDays = 0;
            studentMap.clear();
        }
    }
}
