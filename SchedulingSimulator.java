import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;



public class SchedulingSimulator {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SchedulingSimulator::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("CPU Scheduling Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        // Table for process input
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(Color.GRAY);
        table.setSelectionForeground(Color.WHITE);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Buttons
        JButton addRowButton = new JButton("Add Process");
        addRowButton.addActionListener(e -> tableModel.addRow(new Object[]{"", "", "", ""}));

        JButton runButton = new JButton("Run");
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> tableModel.setRowCount(0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addRowButton);
        buttonPanel.add(runButton);
        buttonPanel.add(clearButton);

        // Result Labels Panel
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel[] labels = new JLabel[]{
            new JLabel("FCFS"), new JLabel(""),
            new JLabel("Round Robin"), new JLabel(""),
            new JLabel("Shortest Process Next"), new JLabel(""),
            new JLabel("Shortest Remaining Time Next"), new JLabel(""),
            new JLabel("Priority Scheduling"), new JLabel("")
        };

        for (JLabel label : labels) {
            resultPanel.add(label);
        }

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(tableScrollPane, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.WEST);
        panel.add(resultPanel, BorderLayout.CENTER);

        frame.add(panel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        // Run button action
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Process> processes = new ArrayList<>();

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    try {
                        int processId = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
                        int arrivalTime = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
                        int burstTime = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                        int priority = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
                        processes.add(new Process(processId, arrivalTime, burstTime, priority));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid input. Please enter valid numbers.");
                        return;
                    }
                }

                double minAvgWaitingTime = Double.MAX_VALUE;
                JLabel bestMethodLabel = null;

                double fcfsAvgWT = executeAndDisplay("FCFS", processes, labels[1]);
                if (fcfsAvgWT < minAvgWaitingTime) {
                    minAvgWaitingTime = fcfsAvgWT;
                    bestMethodLabel = labels[0];
                }

                double rrAvgWT = executeAndDisplay("Round Robin", processes, labels[3]);
                if (rrAvgWT < minAvgWaitingTime) {
                    minAvgWaitingTime = rrAvgWT;
                    bestMethodLabel = labels[2];
                }

                double spnAvgWT = executeAndDisplay("Shortest Process Next", processes, labels[5]);
                if (spnAvgWT < minAvgWaitingTime) {
                    minAvgWaitingTime = spnAvgWT;
                    bestMethodLabel = labels[4];
                }

                double srtfAvgWT = executeAndDisplay("Shortest Remaining Time Next", processes, labels[7]);
                if (srtfAvgWT < minAvgWaitingTime) {
                    minAvgWaitingTime = srtfAvgWT;
                    bestMethodLabel = labels[6];
                }

                double priorityAvgWT = executeAndDisplay("Priority Scheduling", processes, labels[9]);
                if (priorityAvgWT < minAvgWaitingTime) {
                    minAvgWaitingTime = priorityAvgWT;
                    bestMethodLabel = labels[8];
                }

                if (bestMethodLabel != null) {
                    bestMethodLabel.setForeground(Color.RED);
                }
            }
        });
    }

    private static double executeAndDisplay(String algorithm, List<Process> processes, JLabel resultLabel) {
        List<Integer> executionOrder;
        double avgWaitingTime;

        switch (algorithm) {
            case "FCFS":
                executionOrder = fcfsScheduling(processes);
                avgWaitingTime = calculateAvgWaitingTime(processes);
                break;
            case "Round Robin":
                executionOrder = roundRobinScheduling(processes, 2);
                avgWaitingTime = calculateAvgWaitingTime(processes);
                break;
            case "Shortest Process Next":
                executionOrder = spnScheduling(processes);
                avgWaitingTime = calculateAvgWaitingTime(processes);
                break;
            case "Shortest Remaining Time Next":
                executionOrder = srtfScheduling(processes);
                avgWaitingTime = calculateAvgWaitingTime(processes);
                break;
            case "Priority Scheduling":
                executionOrder = priorityScheduling(processes);
                avgWaitingTime = calculateAvgWaitingTime(processes);
                break;
            default:
                executionOrder = new ArrayList<>();
                avgWaitingTime = 0;
        }

        resultLabel.setText("<html>Order: " + executionOrder + "<br>Avg WT: " + avgWaitingTime + "</html>");
        return avgWaitingTime;
    }

    static class Process {
        int id, arrivalTime, burstTime, priority;
        Process(int id, int arrivalTime, int burstTime, int priority) {
            this.id = id;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.priority = priority;
        }
    }

    private static List<Integer> fcfsScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        List<Integer> executionOrder = new ArrayList<>();
        for (Process p : processes) executionOrder.add(p.id);
        return executionOrder;
    }

    private static List<Integer> roundRobinScheduling(List<Process> processes, int quantum) {
        Queue<Process> queue = new LinkedList<>(processes);
        List<Integer> executionOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            Process p = queue.poll();
            executionOrder.add(p.id);
            if (p.burstTime > quantum) {
                p.burstTime -= quantum;
                queue.add(p);
            }
        }
        return executionOrder;
    }

    private static List<Integer> spnScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.burstTime));
        List<Integer> executionOrder = new ArrayList<>();
        for (Process p : processes) executionOrder.add(p.id);
        return executionOrder;
    }

    private static List<Integer> srtfScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.burstTime));
        return fcfsScheduling(processes);
    }

    private static List<Integer> priorityScheduling(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.priority));
        return fcfsScheduling(processes);
    }

    private static double calculateAvgWaitingTime(List<Process> processes) {
        int totalWaitingTime = 0;
        for (int i = 0; i < processes.size(); i++) {
            totalWaitingTime += i * processes.get(i).burstTime;
        }
        return (double) totalWaitingTime / processes.size();
    }
}
