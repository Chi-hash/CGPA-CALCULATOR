package cgpa.calculator;
import java.util.Scanner;

public class CgpaCalc {
	// Method to calculate CGPA
    public static double calculateCGPA() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== CGPA CALCULATOR (5.0 SCALE) ===");
        System.out.print("How many courses? ");
        int numCourses = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        double totalGradePoints = 0;
        double totalCreditHours = 0;
        int failedCourses = 0;
        
        for (int i = 1; i <= numCourses; i++) {
            System.out.println("\nCourse " + i + ":");
            System.out.print("Enter course name: ");
            String courseName = scanner.nextLine();
            
            System.out.print("Enter marks obtained (0-100): ");
            double marks = scanner.nextDouble();
            
            System.out.print("Enter credit hours: ");
            double creditHours = scanner.nextDouble();
            scanner.nextLine(); // Consume newline
            
            System.out.print("Is this a GEDS course? (yes/no): ");
            String isGEDS = scanner.nextLine().toLowerCase();
            boolean gedsCourse = isGEDS.equals("yes") || isGEDS.equals("y");
            
            // Get grade based on marks
            String grade = getGradeFromMarks(marks);
            double gradePoint = convertGradeToPoint5Scale(grade);
            
            if (gradePoint >= 0) {
                totalGradePoints += gradePoint * creditHours;
                totalCreditHours += creditHours;
                
                // Check if course is passed
                boolean passed = checkIfPassed(grade, gedsCourse);
                
                System.out.printf("\nCourse: %s\n", courseName);
                System.out.printf("Marks: %.1f\n", marks);
                System.out.printf("Grade: %s (%.1f points on 5.0 scale)\n", grade, gradePoint);
                System.out.printf("Credit Hours: %.1f\n", creditHours);
                System.out.printf("Status: %s\n", passed ? "PASSED" : "FAILED");
                System.out.printf("Points Earned: %.2f\n\n", gradePoint * creditHours);
                
                if (!passed) {
                    failedCourses++;
                }
            } else {
                System.out.println("Invalid marks! Please enter between 0-100.");
                i--; // Repeat this course
            }
        }
        
        if (totalCreditHours > 0) {
            double cgpa = totalGradePoints / totalCreditHours;
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("=== FINAL RESULT ===");
            System.out.printf("Total Courses: %d\n", numCourses);
            System.out.printf("Courses Passed: %d\n", numCourses - failedCourses);
            System.out.printf("Courses Failed: %d\n", failedCourses);
            System.out.printf("Total Credit Hours: %.2f\n", totalCreditHours);
            System.out.printf("Total Grade Points: %.2f\n", totalGradePoints);
            System.out.printf("Your CGPA: %.2f / 5.00\n", cgpa);
            System.out.printf("Percentage Equivalent: %.2f%%\n", (cgpa / 5.0) * 100);
            
            // Display grading scale
            displayGradingScale5Scale();
            
            return cgpa;
        }
        return 0;
    }
    
    // Method to convert marks to grade letter
    public static String getGradeFromMarks(double marks) {
        if (marks >= 80 && marks <= 100) {
            return "A";
        } else if (marks >= 60 && marks <= 79) {
            return "B";
        } else if (marks >= 50 && marks <= 59) {
            return "C";
        } else if (marks >= 45 && marks <= 49) {
            return "D";
        } else if (marks >= 40 && marks <= 44) {
            return "E";
        } else if (marks >= 0 && marks <= 39) {
            return "F";
        } else {
            return "Invalid";
        }
    }
    
    // Method to convert grade letter to points (5.0 scale)
    public static double convertGradeToPoint5Scale(String grade) {
        grade = grade.toUpperCase();
        
        switch (grade) {
            case "A": return 5.0;  // A = 5.0 on 5.0 scale
            case "B": return 4.0;  // B = 4.0 on 5.0 scale
            case "C": return 3.0;  // C = 3.0 on 5.0 scale
            case "D": return 2.0;  // D = 2.0 on 5.0 scale
            case "E": return 1.0;  // E = 1.0 on 5.0 scale (you should confirm this)
            case "F": return 0.0;  // F = 0.0 on 5.0 scale
            default: return -1;    // Invalid grade
        }
    }
    
    // Method to check if course is passed
    public static boolean checkIfPassed(String grade, boolean isGEDSCourse) {
        grade = grade.toUpperCase();
        
        if (isGEDSCourse) {
            // For GEDS courses, passing grade is D or better
            return grade.equals("A") || grade.equals("B") || 
                   grade.equals("C") || grade.equals("D");
        } else {
            // For other courses, passing grade is C or better
            return grade.equals("A") || grade.equals("B") || grade.equals("C");
        }
    }
    
    // Method to display grading scale for 5.0 scale
    public static void displayGradingScale5Scale() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== GRADING SCALE (5.0 SCALE) ===");
        System.out.println("Marks   | Grade | Points (5.0 scale)");
        System.out.println("--------|-------|-------------------");
        System.out.println("80-100  |   A   | 5.0 points");
        System.out.println("60-79   |   B   | 4.0 points");
        System.out.println("50-59   |   C   | 3.0 points");
        System.out.println("45-49   |   D   | 2.0 points");
        System.out.println("40-44   |   E   | 1.0 point");
        System.out.println("0-39    |   F   | 0.0 points");
        System.out.println("\n=== PASSING REQUIREMENTS ===");
        System.out.println("GEDS Courses: Minimum D grade (2.0 points)");
        System.out.println("Other Courses: Minimum C grade (3.0 points)");
        System.out.println("=".repeat(50));
    }
    
    // Method to convert 5.0 scale to percentage
    public static double convertToPercentage(double cgpa5Scale) {
        return (cgpa5Scale / 5.0) * 100;
    }
    
    // Method to classify performance
    public static String classifyPerformance(double cgpa5Scale) {
        if (cgpa5Scale >= 4.5) {
            return "First Class with Distinction";
        } else if (cgpa5Scale >= 4.0) {
            return "First Class";
        } else if (cgpa5Scale >= 3.5) {
            return "Second Class Upper";
        } else if (cgpa5Scale >= 3.0) {
            return "Second Class Lower";
        } else if (cgpa5Scale >= 2.0) {
            return "Third Class";
        } else if (cgpa5Scale >= 1.0) {
            return "Pass";
        } else {
            return "Fail";
        }
    }
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║      CGPA CALCULATOR (5.0 SCALE SYSTEM)          ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("\nGrading System:");
        System.out.println("• A: 80-100  = 5.0 points");
        System.out.println("• B: 60-79   = 4.0 points");
        System.out.println("• C: 50-59   = 3.0 points");
        System.out.println("• D: 45-49   = 2.0 points");
        System.out.println("• E: 40-44   = 1.0 point");
        System.out.println("• F: 0-39    = 0.0 points");
        System.out.println("\nPassing Requirements:");
        System.out.println("• GEDS courses: Minimum D (2.0 points)");
        System.out.println("• Other courses: Minimum C (3.0 points)");
        
        double cgpa = calculateCGPA();
        
        // Performance classification
        System.out.println("\n" + "═".repeat(50));
        System.out.println("=== PERFORMANCE CLASSIFICATION ===");
        String classification = classifyPerformance(cgpa);
        System.out.printf("CGPA: %.2f/5.00\n", cgpa);
        System.out.printf("Percentage: %.2f%%\n", convertToPercentage(cgpa));
        System.out.printf("Classification: %s\n", classification);
        
        // Give some advice based on CGPA
        System.out.println("\n=== RECOMMENDATION ===");
        if (cgpa >= 4.5) {
            System.out.println("Outstanding performance! Keep up the excellent work!");
        } else if (cgpa >= 4.0) {
            System.out.println("Excellent performance! You're doing great!");
        } else if (cgpa >= 3.5) {
            System.out.println("Very good performance! Room for slight improvement.");
        } else if (cgpa >= 3.0) {
            System.out.println("Good performance. Focus on weak areas.");
        } else if (cgpa >= 2.0) {
            System.out.println("Satisfactory. Consider getting academic support.");
        } else if (cgpa >= 1.0) {
            System.out.println("Needs significant improvement. Seek academic counseling.");
        } else {
            System.out.println("Critical situation. Please meet with your academic advisor.");
        }
        System.out.println("═".repeat(50));
    }
}
