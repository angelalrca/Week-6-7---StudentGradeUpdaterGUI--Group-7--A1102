package week.pkg6.pkg7;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import org.apache.commons.csv.*;

public class StudentGradeUpdaterGUI extends JFrame {

    private JTextField studentIdField, studentNameField, q1Field, q2Field, q3Field, q4Field, averageField;
    private JButton addButton, updateButton, deleteButton;
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    private static final String CSV_FILE = "student_grades.csv";
    private static final String[] COLUMN_NAMES = {"Student ID", "Student Name", "Q1", "Q2", "Q3", "Q4", "Average"};

    public StudentGradeUpdaterGUI() {
        setTitle("Student Grade Updater");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Padding

        JLabel studentIdLabel = new JLabel("Student ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(studentIdLabel, gbc);
        studentIdField = new JTextField(10); // Adjust width for preferred size
        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(studentIdField, gbc);

        JLabel studentNameLabel = new JLabel("Student Name:");
        gbc.gridx = 2;
        gbc.gridy = 0;
        inputPanel.add(studentNameLabel, gbc);
        studentNameField = new JTextField(20); // Adjust width for preferred size
        gbc.gridx = 3;
        gbc.gridy = 0;
        inputPanel.add(studentNameField, gbc);

        JLabel q1Label = new JLabel("Q1:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(q1Label, gbc);
        q1Field = new JTextField(5); // Adjust width for preferred size
        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(q1Field, gbc);

        JLabel q2Label = new JLabel("Q2:");
        gbc.gridx = 2;
        gbc.gridy = 1;
        inputPanel.add(q2Label, gbc);
        q2Field = new JTextField(5); // Adjust width for preferred size
        gbc.gridx = 3;
        gbc.gridy = 1;
        inputPanel.add(q2Field, gbc);

        JLabel q3Label = new JLabel("Q3:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(q3Label, gbc);
        q3Field = new JTextField(5); // Adjust width for preferred size
        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(q3Field, gbc);

        JLabel q4Label = new JLabel("Q4:");
        gbc.gridx = 2;
        gbc.gridy = 2;
        inputPanel.add(q4Label, gbc);
        q4Field = new JTextField(5); // Adjust width for preferred size
        gbc.gridx = 3;
        gbc.gridy = 2;
        inputPanel.add(q4Field, gbc);

        JLabel averageLabel = new JLabel("Average:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(averageLabel, gbc);
        averageField = new JTextField(5);
        averageField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 3;
        inputPanel.add(averageField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addGradeToCSV();
            }
        });
        buttonPanel.add(addButton);

        updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateGradeInCSV();
            }
        });
        buttonPanel.add(updateButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteGradeFromCSV();
            }
        });
        buttonPanel.add(deleteButton);

        statusLabel = new JLabel("");

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        dataTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(dataTable);

        // Double click listener for table to populate fields for update
        dataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = dataTable.getSelectedRow();
                if (row >= 0) {
                    studentIdField.setText((String) dataTable.getValueAt(row, 0));
                    studentNameField.setText((String) dataTable.getValueAt(row, 1));
                    q1Field.setText((String) dataTable.getValueAt(row, 2));
                    q2Field.setText((String) dataTable.getValueAt(row, 3));
                    q3Field.setText((String) dataTable.getValueAt(row, 4));
                    q4Field.setText((String) dataTable.getValueAt(row, 5));
                    averageField.setText((String) dataTable.getValueAt(row, 6));
                }
            }
        });

        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateAndSetAverage(); }
            public void removeUpdate(DocumentEvent e) { calculateAndSetAverage(); }
            public void changedUpdate(DocumentEvent e) { calculateAndSetAverage(); }
        };

        q1Field.getDocument().addDocumentListener(docListener);
        q2Field.getDocument().addDocumentListener(docListener);
        q3Field.getDocument().addDocumentListener(docListener);
        q4Field.getDocument().addDocumentListener(docListener);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding for main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);

        setVisible(true);
        refreshTable(); // Load initial data into the table
    }

    private void calculateAndSetAverage() {
        try {
            double q1 = Double.parseDouble(q1Field.getText());
            double q2 = Double.parseDouble(q2Field.getText());
            double q3 = Double.parseDouble(q3Field.getText());
            double q4 = Double.parseDouble(q4Field.getText());
            double average = (q1 + q2 + q3 + q4) / 4;
            averageField.setText(String.format("%.2f", average));
        } catch (NumberFormatException e) {
            // Ignored because it means one of the fields is not properly filled yet
        }
    }

    private void addGradeToCSV() {
        String studentId = studentIdField.getText().trim();
        String studentName = studentNameField.getText().trim();
        String q1 = q1Field.getText().trim();
        String q2 = q2Field.getText().trim();
        String q3 = q3Field.getText().trim();
        String q4 = q4Field.getText().trim();
        String average = averageField.getText().trim();

        try (CSVPrinter printer = new CSVPrinter(new FileWriter(CSV_FILE, true), CSVFormat.DEFAULT)) {
            printer.printRecord(studentId, studentName, q1, q2, q3, q4, average);
            printer.flush();
            statusLabel.setText("Grade added successfully.");
            clearFields();
            refreshTable();
        } catch (IOException e) {
            statusLabel.setText("Error adding grade: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateGradeInCSV() {
        String studentId = studentIdField.getText().trim();
        String studentName = studentNameField.getText().trim();
        String q1 = q1Field.getText().trim();
        String q2 = q2Field.getText().trim();
        String q3 = q3Field.getText().trim();
        String q4 = q4Field.getText().trim();
        String average = averageField.getText().trim();

        try {
            File inputFile = new File(CSV_FILE);
            File tempFile = new File("temp.csv");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(studentId)) {
                    writer.write(studentId + "," + studentName + "," + q1 + "," + q2 + "," + q3 + "," + q4 + "," + average + "\n");
                    found = true;
                } else {
                    writer.write(line + "\n");
                }
            }

            writer.close();
            reader.close();

            if (!found) {
                statusLabel.setText("Student ID not found.");
            } else {
                statusLabel.setText("Grade updated successfully.");
                clearFields();
                inputFile.delete();
                tempFile.renameTo(new File(CSV_FILE));
                refreshTable();
            }

        } catch (IOException e) {
            statusLabel.setText("Error updating grade: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteGradeFromCSV() {
        String studentId = studentIdField.getText().trim();

        try {
            File inputFile = new File(CSV_FILE);
            File tempFile = new File("temp.csv");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(studentId)) {
                    found = true;
                    continue; // Skip writing this line
                }
                writer.write(line + "\n");
            }

            writer.close();
            reader.close();

            if (!found) {
                statusLabel.setText("Student ID not found.");
            } else {
                statusLabel.setText("Grade deleted successfully.");
                clearFields();
                inputFile.delete();
                tempFile.renameTo(new File(CSV_FILE));
                refreshTable();
            }

        } catch (IOException e) {
            statusLabel.setText("Error deleting grade: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // Clear current table data
        try (CSVParser parser = new CSVParser(new FileReader(CSV_FILE), CSVFormat.DEFAULT)) {
            for (CSVRecord record : parser) {
                Object[] rowData = new Object[COLUMN_NAMES.length];
                for (int i = 0; i < COLUMN_NAMES.length; i++) {
                    if (i < record.size()) {
                        rowData[i] = record.get(i);
                    } else {
                        rowData[i] = ""; // Default to empty string if column doesn't exist
                    }
                }
                tableModel.addRow(rowData);
            }
        } catch (IOException e) {
            statusLabel.setText("Error refreshing table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        studentIdField.setText("");
        studentNameField.setText("");
        q1Field.setText("");
        q2Field.setText("");
        q3Field.setText("");
        q4Field.setText("");
        averageField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentGradeUpdaterGUI();
        });
    }
}


