package cgpa.calculator;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CgpaCalcGUI extends JFrame implements ActionListener {
    
    private static final long serialVersionUID = 1L;
    
    // Color palette
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);    // Blue
    private final Color SECONDARY_COLOR = new Color(39, 174, 96);   // Green
    private final Color ACCENT_COLOR = new Color(231, 76, 60);      // Red
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = new Color(52, 73, 94);
    
    // Components
    private JTextField courseField, marksField, creditField;
    private JCheckBox gedsCheck;
    private JTable courseTable;
    private DefaultTableModel courseTableModel;
    private JButton addButton, calculateButton, clearButton;
    private JLabel statsLabel;
    private JLabel totalCoursesValueLabel;
    private JLabel passedValueLabel;
    private JLabel failedValueLabel;
    private JLabel cgpaValueLabel;
    private JLabel summaryLabel;
    private JLabel statusLabel;
    private JTextField semesterField, sessionField, semesterCgpaField;
    private JTable semesterTable;
    private DefaultTableModel semesterTableModel;
    private JButton addSemesterButton, clearSemestersButton;
    private JLabel semesterSummaryLabel;
    
    // Data storage
    private ArrayList<Course> courses = new ArrayList<>();
    private ArrayList<SemesterRecord> semesters = new ArrayList<>();
    
    // Inner class for course
    class Course {
        String name;
        double marks;
        double credits;
        boolean isGEDS;
        String grade;
        double points;
        
        Course(String name, double marks, double credits, boolean isGEDS) {
            this.name = name;
            this.marks = marks;
            this.credits = credits;
            this.isGEDS = isGEDS;
            calculateGrade();
        }
        
        void calculateGrade() {
            if (marks >= 80 && marks <= 100) {
                grade = "A";
                points = 5.0;
            } else if (marks >= 60 && marks < 80) {
                grade = "B";
                points = 4.0;
            } else if (marks >= 50 && marks < 60) {
                grade = "C";
                points = 3.0;
            } else if (marks >= 45 && marks < 50) {
                grade = "D";
                points = 2.0;
            } else if (marks >= 40 && marks < 45) {
                grade = "E";
                points = 1.0;
            } else if (marks >= 0 && marks < 40) {
                grade = "F";
                points = 0.0;
            } else {
                grade = "Invalid";
                points = -1;
            }
        }
        
        boolean isPassed() {
            if (isGEDS) {
                return points >= 2.0;
            } else {
                return points >= 3.0;
            }
        }
        
        Color getGradeColor() {
            switch(grade) {
                case "A": return new Color(46, 204, 113);  // Green
                case "B": return new Color(52, 152, 219);  // Blue
                case "C": return new Color(155, 89, 182);  // Purple
                case "D": return new Color(241, 196, 15);  // Yellow
                case "E": return new Color(230, 126, 34);  // Orange
                case "F": return ACCENT_COLOR;             // Red
                default: return Color.GRAY;
            }
        }
    }

    private static class Stats {
        int total;
        int passed;
        int failed;
        double totalPoints;
        double totalCredits;
        double cgpa;
        double percentage;
        double highestMark;
        double lowestMark;
        String easiestCourse;
        String hardestCourse;

        Stats() {
            highestMark = -1;
            lowestMark = 101;
            easiestCourse = "";
            hardestCourse = "";
        }
    }

    private static class SemesterRecord {
        final String semester;
        final String session;
        final double cgpa;

        SemesterRecord(String semester, String session, double cgpa) {
            this.semester = semester;
            this.session = session;
            this.cgpa = cgpa;
        }
    }
    
    public CgpaCalcGUI() {
        setupWindow();
        createComponents();
        setVisible(true);
    }
    
    private void setupWindow() {
        setTitle("CGPA Calculator Pro - 5.0 Scale");
        setSize(1080, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(15, 15));
        
        // Add padding around the window
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }
    
    private void createComponents() {
        // Create main container with shadow effect
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.addTab("GPA Calculator", createGpaTab());
        tabs.addTab("CGPA Tracker", createCgpaTab());
        mainPanel.add(tabs, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Left: title + subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("CGPA CALCULATOR");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("5.0 Scale Grading System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(236, 240, 241));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        titlePanel.add(subtitle);

        // Right: grade scale indicator
        JPanel scalePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        scalePanel.setOpaque(false);
        
        String[] grades = {"A", "B", "C", "D", "E", "F"};
        String[] ranges = {"80-100", "60-79", "50-59", "45-49", "40-44", "0-39"};
        
        for (int i = 0; i < grades.length; i++) {
            JLabel gradeLabel = new JLabel(grades[i] + " " + ranges[i]);
            gradeLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            gradeLabel.setForeground(Color.WHITE);
            gradeLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            gradeLabel.setOpaque(true);
            gradeLabel.setBackground(new Color(255, 255, 255, 30));
            gradeLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1));
            scalePanel.add(gradeLabel);
        }

        header.add(titlePanel, BorderLayout.WEST);
        header.add(scalePanel, BorderLayout.EAST);
        
        return header;
    }

    private JPanel createGpaTab() {
        JPanel tab = new JPanel(new BorderLayout(10, 10));
        tab.setBackground(BACKGROUND_COLOR);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND_COLOR);

        JComponent inputPanel = createInputPanel();
        centerPanel.add(inputPanel, BorderLayout.WEST);

        JPanel displayPanel = createDisplayPanel();
        centerPanel.add(displayPanel, BorderLayout.CENTER);

        JComponent statsPanel = createStatsPanel();
        centerPanel.add(statsPanel, BorderLayout.EAST);

        tab.add(centerPanel, BorderLayout.CENTER);
        tab.add(createBottomPanel(), BorderLayout.SOUTH);
        return tab;
    }

    private JPanel createCgpaTab() {
        JPanel tab = new JPanel(new BorderLayout(10, 10));
        tab.setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setPreferredSize(new Dimension(360, 0));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel("CGPA TRACKER");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(title);
        
        JLabel subtitle = new JLabel("Add each semester's CGPA to compute cumulative CGPA");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(subtitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        formPanel.add(createFormField("Semester", semesterField = new JTextField(12)));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFormField("Session (e.g. 2023/2024)", sessionField = new JTextField(12)));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFormField("Semester CGPA (0-5)", semesterCgpaField = new JTextField(8)));
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBackground(CARD_COLOR);
        addSemesterButton = createStyledButton("Add Semester", PRIMARY_COLOR);
        clearSemestersButton = createStyledButton("Clear Semesters", ACCENT_COLOR);
        addSemesterButton.setPreferredSize(new Dimension(140, 40));
        clearSemestersButton.setPreferredSize(new Dimension(150, 40));
        actions.add(addSemesterButton);
        actions.add(clearSemestersButton);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(actions);

        addSemesterButton.addActionListener(e -> addSemesterRecord());
        clearSemestersButton.addActionListener(e -> clearSemesters());

        JPanel tablePanel = new JPanel(new BorderLayout(8, 8));
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel listTitle = new JLabel("SEMESTER LIST");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        listTitle.setForeground(TEXT_COLOR);
        tablePanel.add(listTitle, BorderLayout.NORTH);

        String[] columns = {"No.", "Semester", "Session", "CGPA"};
        semesterTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        semesterTable = new JTable(semesterTableModel);
        semesterTable.setRowHeight(26);
        semesterTable.setShowGrid(true);
        semesterTable.setGridColor(new Color(235, 235, 235));
        semesterTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        semesterTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        semesterTable.getTableHeader().setBackground(new Color(245, 245, 245));
        semesterTable.getTableHeader().setForeground(new Color(80, 80, 80));

        JScrollPane semesterScroll = new JScrollPane(semesterTable);
        semesterScroll.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 1));
        semesterScroll.getViewport().setBackground(Color.WHITE);
        tablePanel.add(semesterScroll, BorderLayout.CENTER);

        semesterSummaryLabel = new JLabel("Add semesters to see cumulative CGPA.");
        semesterSummaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        semesterSummaryLabel.setForeground(new Color(80, 80, 80));
        semesterSummaryLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        tablePanel.add(semesterSummaryLabel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(formPanel, BorderLayout.WEST);
        topPanel.add(tablePanel, BorderLayout.CENTER);

        tab.add(topPanel, BorderLayout.CENTER);
        return tab;
    }
    
    private JComponent createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setMinimumSize(new Dimension(300, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Panel title with icon
        JLabel panelTitle = new JLabel("ADD NEW COURSE");
        panelTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panelTitle.setForeground(TEXT_COLOR);
        panelTitle.setHorizontalAlignment(SwingConstants.LEFT);
        panelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(panelTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Input fields with labels
        panel.add(createFormField("Course Name", courseField = new JTextField(15)));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createFormField("Marks (0-100)", marksField = new JTextField(15)));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        panel.add(createFormField("Credit Hours", creditField = new JTextField(15)));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // GEDS checkbox with custom styling
        JPanel gedsPanel = new JPanel(new BorderLayout());
        gedsPanel.setBackground(CARD_COLOR);
        gedsCheck = new JCheckBox("This is a GEDS Course");
        gedsCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gedsCheck.setForeground(TEXT_COLOR);
        gedsCheck.setBackground(CARD_COLOR);
        gedsCheck.setFocusPainted(false);
        gedsPanel.add(gedsCheck, BorderLayout.WEST);
        gedsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gedsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        panel.add(gedsPanel);

        creditField.addActionListener(e -> addCourse());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(120, 120, 120));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(statusLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add Course button
        addButton = createStyledButton("Add Course", PRIMARY_COLOR);
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        addButton.addActionListener(this);
        panel.add(addButton);

        panel.setPreferredSize(new Dimension(320, panel.getPreferredSize().height));
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setPreferredSize(new Dimension(320, 0));
        
        return scrollPane;
    }
    
    private JPanel createFormField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(CARD_COLOR);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        fieldLabel.setForeground(new Color(100, 100, 100));
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setMinimumSize(new Dimension(260, 40));
        
        panel.add(fieldLabel, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 45));
        button.setMaximumSize(new Dimension(200, 45));
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Panel header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_COLOR);

        JLabel title = new JLabel("YOUR COURSES");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_COLOR);

        statsLabel = new JLabel("0 courses");
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statsLabel.setForeground(new Color(150, 150, 150));

        header.add(title, BorderLayout.WEST);
        header.add(statsLabel, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"No.", "Course", "Marks", "Credits", "Grade", "Points", "Status", "GEDS"};
        courseTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(courseTableModel);
        courseTable.setRowHeight(28);
        courseTable.setShowGrid(true);
        courseTable.setShowVerticalLines(true);
        courseTable.setShowHorizontalLines(true);
        courseTable.setGridColor(new Color(235, 235, 235));
        courseTable.setIntercellSpacing(new Dimension(1, 1));
        courseTable.setFillsViewportHeight(true);
        courseTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        courseTable.getTableHeader().setBackground(new Color(245, 245, 245));
        courseTable.getTableHeader().setForeground(new Color(80, 80, 80));
        courseTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(new EmptyBorder(0, 6, 0, 6));
        for (int i = 0; i < courseTable.getColumnCount(); i++) {
            courseTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        adjustTableColumnWidths();

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Summary
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(new Color(250, 250, 250));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        summaryLabel = new JLabel("Add courses to see summary.");
        summaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        summaryLabel.setForeground(new Color(120, 120, 120));
        summaryPanel.add(summaryLabel, BorderLayout.WEST);
        panel.add(summaryPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private JComponent createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        
        // Panel title
        JLabel title = new JLabel("QUICK STATS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Stats cards
        totalCoursesValueLabel = new JLabel("0");
        panel.add(createStatCard("Total Courses", totalCoursesValueLabel, PRIMARY_COLOR));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        passedValueLabel = new JLabel("0");
        panel.add(createStatCard("Passed", passedValueLabel, SECONDARY_COLOR));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        failedValueLabel = new JLabel("0");
        panel.add(createStatCard("Failed", failedValueLabel, ACCENT_COLOR));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        cgpaValueLabel = new JLabel("0.00");
        panel.add(createStatCard("Current CGPA", cgpaValueLabel, new Color(155, 89, 182)));
        
        panel.add(Box.createVerticalGlue());
        
        // Grading scale card
        JPanel scaleCard = new JPanel();
        scaleCard.setLayout(new BoxLayout(scaleCard, BoxLayout.Y_AXIS));
        scaleCard.setBackground(new Color(240, 248, 255));
        scaleCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 220, 240), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel scaleTitle = new JLabel("Grading Scale (5.0)");
        scaleTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        scaleTitle.setForeground(new Color(41, 128, 185));
        scaleTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        scaleCard.add(scaleTitle);
        scaleCard.add(Box.createRigidArea(new Dimension(0, 10)));
        
        String[] grades = {"A: 5.0", "B: 4.0", "C: 3.0", "D: 2.0", "E: 1.0", "F: 0.0"};
        for (String grade : grades) {
            JLabel gradeLabel = new JLabel(grade);
            gradeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            gradeLabel.setForeground(new Color(100, 100, 100));
            gradeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            scaleCard.add(gradeLabel);
        }
        
        scaleCard.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel passingLabel = new JLabel("Passing: C (3.0) for regular");
        passingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        passingLabel.setForeground(new Color(150, 150, 150));
        passingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        scaleCard.add(passingLabel);
        
        JLabel gedsLabel = new JLabel("GEDS: D (2.0) minimum");
        gedsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gedsLabel.setForeground(new Color(150, 150, 150));
        gedsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        scaleCard.add(gedsLabel);
        
        panel.add(scaleCard);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setPreferredSize(new Dimension(260, 0));
        
        return scrollPane;
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(250, 250, 250));
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(90, 90, 90));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(valueLabel);
        
        return card;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Calculate button
        calculateButton = createStyledButton("Calculate Final CGPA", SECONDARY_COLOR);
        calculateButton.addActionListener(this);
        calculateButton.setEnabled(false);
        
        // Clear button
        clearButton = createStyledButton("Clear All Courses", ACCENT_COLOR);
        clearButton.addActionListener(this);
        clearButton.setEnabled(false);
        
        panel.add(calculateButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addCourse();
        } else if (e.getSource() == calculateButton) {
            calculateCGPA();
        } else if (e.getSource() == clearButton) {
            clearAll();
        }
    }
    
    private void addCourse() {
        try {
            String name = courseField.getText().trim().toUpperCase();
            String marksText = marksField.getText().trim();
            String creditText = creditField.getText().trim();
            boolean isGEDS = gedsCheck.isSelected();
            
            // Validation
            if (name.isEmpty()) {
                showStyledMessage("Please enter a course name", "Input Required", JOptionPane.WARNING_MESSAGE);
                courseField.requestFocus();
                setStatus("Course name is required.", ACCENT_COLOR);
                return;
            }
            
            if (marksText.isEmpty() || creditText.isEmpty()) {
                showStyledMessage("Please enter both marks and credit hours", "Input Required", JOptionPane.WARNING_MESSAGE);
                setStatus("Marks and credit hours are required.", ACCENT_COLOR);
                return;
            }
            
            double marks = Double.parseDouble(marksText);
            double credits = Double.parseDouble(creditText);
            
            if (marks < 0 || marks > 100) {
                showStyledMessage("Marks must be between 0 and 100", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                marksField.requestFocus();
                setStatus("Marks must be between 0 and 100.", ACCENT_COLOR);
                return;
            }
            
            if (credits < 0 || credits > 10) {
                showStyledMessage("Credit hours must be between 0 and 10", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                creditField.requestFocus();
                setStatus("Credit hours must be between 0 and 10.", ACCENT_COLOR);
                return;
            }
            
            // Create and add course
            Course course = new Course(name, marks, credits, isGEDS);
            
            if (course.points < 0) {
                showStyledMessage("Invalid marks entered", "Error", JOptionPane.ERROR_MESSAGE);
                setStatus("Invalid marks entered.", ACCENT_COLOR);
                return;
            }
            
            courses.add(course);
            
            // Update display
            updateDisplay();
            
            // Clear fields
            courseField.setText("");
            marksField.setText("");
            creditField.setText("");
            gedsCheck.setSelected(false);
            courseField.requestFocus();
            setStatus("Added: " + name + " (" + course.grade + ")", new Color(46, 125, 50));
                
        } catch (NumberFormatException ex) {
            showStyledMessage("Please enter valid numbers for marks and credits", "Input Error", JOptionPane.ERROR_MESSAGE);
            setStatus("Please enter valid numbers for marks and credits.", ACCENT_COLOR);
        }
    }
    
    private void updateDisplay() {
        courseTableModel.setRowCount(0);
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            courseTableModel.addRow(new Object[] {
                i + 1,
                c.name,
                String.format("%.1f", c.marks),
                String.format("%.1f", c.credits),
                c.grade,
                String.format("%.1f", c.points),
                c.isPassed() ? "Pass" : "Fail",
                c.isGEDS ? "Yes" : "No"
            });
        }
        adjustTableColumnWidths();

        Stats stats = computeStats();
        updateStatsPanel(stats);
        updateSummary(stats);

        if (stats.total > 0) {
            statsLabel.setText(stats.total + " courses - GPA: " + String.format("%.2f", stats.cgpa));
        } else {
            statsLabel.setText("0 courses");
        }

        calculateButton.setEnabled(stats.total > 0);
        clearButton.setEnabled(stats.total > 0);
    }
    
    private void updateStatsPanel(Stats stats) {
        totalCoursesValueLabel.setText(String.valueOf(stats.total));
        passedValueLabel.setText(String.valueOf(stats.passed));
        failedValueLabel.setText(String.valueOf(stats.failed));
        cgpaValueLabel.setText(String.format("%.2f", stats.cgpa));
        cgpaValueLabel.setForeground(getCgpaColor(stats.cgpa));
    }

    private void updateSummary(Stats stats) {
        if (stats.total == 0) {
            summaryLabel.setText("Add courses to see summary.");
            return;
        }
        summaryLabel.setText(String.format(
            "GPA: %.2f/5.00 (%.2f%%)   Credits: %.2f   Points: %.2f",
            stats.cgpa, stats.percentage, stats.totalCredits, stats.totalPoints
        ));
    }

    private void addSemesterRecord() {
        String semester = semesterField.getText().trim().toUpperCase();
        String session = sessionField.getText().trim().toUpperCase();
        String cgpaText = semesterCgpaField.getText().trim();

        if (semester.isEmpty() || session.isEmpty() || cgpaText.isEmpty()) {
            showStyledMessage("Enter semester, session, and semester CGPA.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double cgpa;
        try {
            cgpa = Double.parseDouble(cgpaText);
        } catch (NumberFormatException ex) {
            showStyledMessage("Semester CGPA must be a valid number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cgpa < 0 || cgpa > 5.0) {
            showStyledMessage("Semester CGPA must be between 0.00 and 5.00.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        semesters.add(new SemesterRecord(semester, session, cgpa));
        updateSemesterDisplay();
        semesterField.setText("");
        sessionField.setText("");
        semesterCgpaField.setText("");
    }

    private void clearSemesters() {
        if (semesters.isEmpty()) {
            showStyledMessage("No semesters to clear.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        semesters.clear();
        updateSemesterDisplay();
    }

    private void updateSemesterDisplay() {
        if (semesterTableModel == null) {
            return;
        }
        semesterTableModel.setRowCount(0);
        double total = 0;
        for (int i = 0; i < semesters.size(); i++) {
            SemesterRecord record = semesters.get(i);
            semesterTableModel.addRow(new Object[] {
                i + 1,
                record.semester,
                record.session,
                String.format("%.2f", record.cgpa)
            });
            total += record.cgpa;
        }
        if (semesterSummaryLabel != null) {
            if (semesters.isEmpty()) {
                semesterSummaryLabel.setText("Add semesters to see cumulative CGPA.");
            } else {
                double cumulative = total / semesters.size();
                semesterSummaryLabel.setText(String.format(
                    "Cumulative CGPA: %.2f/5.00 (based on %d semesters)",
                    cumulative, semesters.size()
                ));
            }
        }
    }

    private Stats computeStats() {
        Stats stats = new Stats();
        stats.total = courses.size();
        if (stats.total == 0) {
            return stats;
        }
        for (Course c : courses) {
            stats.totalPoints += c.points * c.credits;
            stats.totalCredits += c.credits;
            if (c.isPassed()) {
                stats.passed++;
            }
            if (c.marks > stats.highestMark) {
                stats.highestMark = c.marks;
                stats.easiestCourse = c.name;
            }
            if (c.marks < stats.lowestMark) {
                stats.lowestMark = c.marks;
                stats.hardestCourse = c.name;
            }
        }
        stats.failed = stats.total - stats.passed;
        if (stats.totalCredits > 0) {
            stats.cgpa = stats.totalPoints / stats.totalCredits;
            stats.percentage = (stats.cgpa / 5.0) * 100;
        }
        return stats;
    }

    private void setStatus(String message, Color color) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setForeground(color);
        }
    }


    private void adjustTableColumnWidths() {
        if (courseTable == null) {
            return;
        }
        int padding = 16;
        for (int col = 0; col < courseTable.getColumnCount(); col++) {
            int maxWidth = 0;
            TableCellRenderer headerRenderer = courseTable.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                courseTable, courseTable.getColumnName(col), false, false, -1, col);
            maxWidth = Math.max(maxWidth, headerComp.getPreferredSize().width);

            for (int row = 0; row < courseTable.getRowCount(); row++) {
                TableCellRenderer renderer = courseTable.getCellRenderer(row, col);
                Component comp = courseTable.prepareRenderer(renderer, row, col);
                maxWidth = Math.max(maxWidth, comp.getPreferredSize().width);
            }
            courseTable.getColumnModel().getColumn(col).setPreferredWidth(maxWidth + padding);
        }
    }

    private Color getCgpaColor(double cgpa) {
        if (cgpa >= 4.0) {
            return SECONDARY_COLOR;
        }
        if (cgpa >= 3.0) {
            return PRIMARY_COLOR;
        }
        if (cgpa >= 2.0) {
            return new Color(241, 196, 15);
        }
        return ACCENT_COLOR;
    }
    
    private void calculateCGPA() {
        Stats stats = computeStats();
        if (stats.total == 0) {
            showStyledMessage(
                "<html><div style='text-align: center;'>" +
                "No courses added yet.<br><br>" +
                "Please add at least one course to calculate your CGPA." +
                "</div></html>",
                "No Data",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        double gpa = stats.cgpa;
        String classification = getClassification(gpa);
        String recommendation = getRecommendation(gpa);

        StringBuilder html = new StringBuilder();
        html.append("<html><div style='font-family: Segoe UI; font-size: 11pt;'>");
        html.append("<b>CGPA Calculation Results</b><br><br>");
        html.append("<b>Summary</b><br>");
        html.append("Total Courses: ").append(stats.total).append("<br>");
        html.append("Passed: ").append(stats.passed).append("<br>");
        html.append("Failed: ").append(stats.failed).append("<br>");
        html.append(String.format("Total Credits: %.2f<br>", stats.totalCredits));
        html.append(String.format("Total Points: %.2f<br><br>", stats.totalPoints));
        html.append("<b>Results</b><br>");
        html.append(String.format("GPA: %.2f / 5.00<br>", gpa));
        html.append(String.format("Percentage: %.2f%%<br>", (gpa / 5.0) * 100));
        html.append("Classification: ").append(classification).append("<br><br>");
        html.append("<b>Course Analysis</b><br>");
        html.append(String.format("Highest Mark: %.1f", stats.highestMark)).append("<br>");
        html.append("Easiest Course: ").append(stats.easiestCourse.isEmpty() ? "-" : stats.easiestCourse).append("<br>");
        html.append(String.format("Lowest Mark: %.1f", stats.lowestMark)).append("<br>");
        html.append("Hardest Course: ").append(stats.hardestCourse.isEmpty() ? "-" : stats.hardestCourse).append("<br><br>");
        html.append("<b>Recommendation</b><br>");
        html.append(recommendation);
        html.append("</div></html>");

        JLabel resultLabel = new JLabel(html.toString());
        resultLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JOptionPane.showMessageDialog(this, resultLabel,
            "CGPA Calculation Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getClassification(double cgpa) {
        if (cgpa >= 4.5) return "First Class with Distinction";
        else if (cgpa >= 4.0) return "First Class";
        else if (cgpa >= 3.5) return "Second Class Upper";
        else if (cgpa >= 3.0) return "Second Class Lower";
        else if (cgpa >= 2.0) return "Third Class";
        else if (cgpa >= 1.0) return "Pass";
        else return "Fail";
    }
    
    private String getRecommendation(double cgpa) {
        if (cgpa >= 4.5) return "Outstanding! Maintain this excellence and consider advanced courses.";
        else if (cgpa >= 4.0) return "Excellent work! Keep up the good performance.";
        else if (cgpa >= 3.5) return "Very good! Focus on improving weak areas for even better results.";
        else if (cgpa >= 3.0) return "Good performance. Review subjects where you scored below B.";
        else if (cgpa >= 2.0) return "Satisfactory. Consider seeking academic support or tutoring.";
        else if (cgpa >= 1.0) return "Needs improvement. Please meet with your academic advisor.";
        else return "Critical situation. Immediate academic counseling required.";
    }
    
    private void clearAll() {
        if (courses.isEmpty()) {
            showStyledMessage("No courses to clear.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><div style='text-align: center; width: 250px;'>" +
            "<b>Clear All Courses?</b><br><br>" +
            "You're about to delete <b>" + courses.size() + "</b> courses.<br>" +
            "This action cannot be undone.<br><br>" +
            "Are you sure you want to continue?" +
            "</div></html>",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            courses.clear();
            updateDisplay();
            setStatus("All courses cleared.", new Color(120, 120, 120));
        }
    }
    
    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, 
            "<html><div style='font-family: Segoe UI; font-size: 12pt; padding: 10px;'>" + message + "</div></html>",
            title,
            messageType);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set modern look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Customize JOptionPane
                UIManager.put("OptionPane.background", new Color(245, 245, 245));
                UIManager.put("Panel.background", new Color(245, 245, 245));
                UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 12));
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Create and show the GUI
            new CgpaCalcGUI();
        });
    }
}