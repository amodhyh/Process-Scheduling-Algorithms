import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SchedulingSimulator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SchedulingSimulator::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("CPU Scheduling Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel(new BorderLayout());
        
        // Table for process input with editable text fields
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        
        table.setFillsViewportHeight(true);
        JScrollPane tableScrollPane = new JScrollPane(table);
        
        // Button to add rows
        JButton addRowButton = new JButton("Add Process");
        addRowButton.addActionListener(e -> tableModel.addRow(new Object[]{"", "", "", ""}));
        
        // Dropdown to select scheduling algorithm
        String[] algorithms = {"FCFS", "Round Robin", "Shortest Process Next", "Shortest Remaining Time Next", "Priority Scheduling"};
        JComboBox<String> algorithmSelection = new JComboBox<>(algorithms);

        // Buttons
        JButton runButton = new JButton("Run");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addRowButton);
        buttonPanel.add(runButton);
        buttonPanel.add(cancelButton);

        // Gantt Chart Placeholder
        JLabel ganttChartLabel = new JLabel("Gantt Chart Output Here", SwingConstants.CENTER);
        ganttChartLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        ganttChartLabel.setPreferredSize(new Dimension(700, 100));
        
        // Layout management
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(algorithmSelection, BorderLayout.NORTH);
        topPanel.add(tableScrollPane, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(ganttChartLabel, BorderLayout.SOUTH);
        
        frame.add(panel);
        frame.setVisible(true);

        // Action Listener for Run Button
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAlgorithm = (String) algorithmSelection.getSelectedItem();
                executeSchedulingAlgorithm(selectedAlgorithm, tableModel, ganttChartLabel);
            }
        });
    }

    private static void executeSchedulingAlgorithm(String algorithm, DefaultTableModel tableModel, JLabel ganttChartLabel) {
        // Retrieve process data from table
        int rowCount = tableModel.getRowCount();
        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            try {
                int processId = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
                int arrivalTime = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
                int burstTime = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                int priority = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
                processes.add(new Process(processId, arrivalTime, burstTime, priority));
            } catch (Exception ex) {
                ganttChartLabel.setText("Invalid input in table. Please enter valid numbers.");
                return;
            }
        }

        List<Integer> executionOrder = new ArrayList<>();

        switch (algorithm) {
            case "FCFS":
                executionOrder = fcfsScheduling(processes);
                break;
            case "Round Robin":
                executionOrder = roundRobinScheduling(processes, 2); // Default quantum of 2
                break;
            case "Shortest Process Next":
                executionOrder = spnScheduling(processes);
                break;
            case "Shortest Remaining Time Next":
                executionOrder = srtfScheduling(processes);
                break;
            case "Priority Scheduling":
                executionOrder = priorityScheduling(processes);
                break;
        }

        ganttChartLabel.setText("Execution Order: " + executionOrder.toString());
    }

    // Process class for storing process details
    static class Process {
        int id, arrivalTime, burstTime, priority;
        Process(int id, int arrivalTime, int burstTime, int priority) {
            this.id = id;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.priority = priority;
        }
    }

    // Scheduling algorithms implementations
    private static List<Integer> fcfsScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        List<Integer> executionOrder = new ArrayList<>();
        for (Process p : processes) {
            executionOrder.add(p.id);
        }
        return executionOrder;
    }

    private static List<Integer> roundRobinScheduling(List<Process> processes, int quantum) {
        return Collections.emptyList();
    }

    private static List<Integer> spnScheduling(List<Process> processes) {
        return Collections.emptyList();
    }

    private static List<Integer> srtfScheduling(List<Process> processes) {
        return Collections.emptyList();
    }

    private static List<Integer> priorityScheduling(List<Process> processes) {
        return Collections.emptyList();
    }
}
