        import javax.swing.*;
        import java.awt.*;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;
        import java.util.ArrayList;
        import java.util.Comparator;
        import java.util.List;
        import java.util.PriorityQueue;
        import java.io.*;

        public class SchedulerGUI {
            private final JFrame frame;
            private final JTextArea processListArea;
            private final JTextArea processListArea2;
            private final JTextField cpuField, memoryField, arrivalField, priorityField;
            private final JComboBox<String> algorithmComboBox;
            private final List<ProcessClass> processList;
            private final JLabel timeLabel;
            private final int max_memory = 600;
            private int currentId = 0;
            private int availableMemory = max_memory;


            public SchedulerGUI() {
                processList = new ArrayList<>();

                frame = new JFrame("Process Scheduler");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1000, 800);
                frame.setMinimumSize(new Dimension(1000, 800));
                frame.setLayout(new BorderLayout());
                frame.setResizable(true);


                // Панель для отображения процессов
                processListArea = new JTextArea();
                processListArea2 = new JTextArea();
                JPanel areaPanel = new JPanel();
                areaPanel.setLayout(new GridLayout(2, 1));
                areaPanel.add(new JScrollPane(processListArea));
                areaPanel.add(new JScrollPane(processListArea2));

                frame.add(areaPanel, BorderLayout.CENTER);


                // Панель для добавления новых процессов
                JPanel addProcessPanel = new JPanel();
                addProcessPanel.setLayout(new GridLayout(6, 2));

                cpuField = new JTextField();
                memoryField = new JTextField();
                arrivalField = new JTextField();
                priorityField = new JTextField();

                addProcessPanel.add(new JLabel("CPU Time:"));
                addProcessPanel.add(cpuField);
                addProcessPanel.add(new JLabel("Memory:"));
                addProcessPanel.add(memoryField);
                addProcessPanel.add(new JLabel("Arrival Time:"));
                addProcessPanel.add(arrivalField);
                addProcessPanel.add(new JLabel("Priority:"));
                addProcessPanel.add(priorityField);

                JButton addButton = new JButton("Add Process");
                addButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int id = currentId++;
                        int cpuTime = Integer.parseInt(cpuField.getText());
                        int memory = Integer.parseInt(memoryField.getText());
                        int arrivalTime = Integer.parseInt(arrivalField.getText());
                        int priority = Integer.parseInt(priorityField.getText());

                        ProcessClass process = new ProcessClass(id, cpuTime, memory, arrivalTime, priority);
                        processList.add(process);

                        updateProcessList();
                    }
                });

                JButton clearDataButton = new JButton("Clear All");
                clearDataButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        processList.clear();
                        currentId = 0;
                        updateProcessList();
                    }
                });
                /*
                JButton stopAll = new JButton("Reset Scheduler");
                clearDataButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (schedulerThread.isAlive()) {
                            schedulerThread.interrupt();
                        }
                        processListArea2.setText("");
                    }
                });

                 */


                JButton openFileButton = new JButton("Open File");
                openFileButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fileChooser = new JFileChooser();
                        int result = fileChooser.showOpenDialog(frame);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            readProcessesFromFile(selectedFile);
                        }
                    }
                });



                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout());
                buttonPanel.add(addButton);
                buttonPanel.add(clearDataButton);
                //buttonPanel.add(stopAll);
                buttonPanel.add(openFileButton);

                JPanel topPanel = new JPanel();
                topPanel.setLayout(new BorderLayout());
                topPanel.add(addProcessPanel, BorderLayout.CENTER);
                topPanel.add(buttonPanel, BorderLayout.SOUTH);
                frame.add(topPanel, BorderLayout.NORTH);


                // Панель выбора алгоритма планирования
                JPanel algorithmPanel = new JPanel();
                algorithmComboBox = new JComboBox<>(new String[]{"FCFS", "Priority"});
                algorithmPanel.add(new JLabel("Select Algorithm:"));
                algorithmPanel.add(algorithmComboBox);
                frame.add(algorithmPanel, BorderLayout.SOUTH);

                // Кнопка запуска планировщика
                JButton runButton = new JButton("Run Scheduler");
                runButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        processListArea2.setText("");
                        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
                        if (selectedAlgorithm.equals("FCFS")) {
                            runFCFS();
                        } else if (selectedAlgorithm.equals("Priority")) {
                            runPriorityScheduling();
                        }
                    }
                });
                frame.add(runButton, BorderLayout.EAST);

                // Отображение системного времени
                JPanel timePanel = new JPanel();
                timeLabel = new JLabel("System Time: 0");
                timePanel.add(timeLabel);
                frame.add(timePanel, BorderLayout.WEST);


                frame.setVisible(true);
            }

            private void readProcessesFromFile(File file) {
                processList.clear();

                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    currentId = 0;
                    String line;
                    while ((line = br.readLine()) != null) {
                        // Предполагается, что поля в файле разделены запятыми
                        String[] parts = line.split(",");
                        int id = Integer.parseInt(parts[0].trim());
                        int cpuTime = Integer.parseInt(parts[1].trim());
                        int memory = Integer.parseInt(parts[2].trim());
                        int arrivalTime = Integer.parseInt(parts[3].trim());
                        int priority = Integer.parseInt(parts[4].trim());

                        ProcessClass process = new ProcessClass(id, cpuTime, memory, arrivalTime, priority);
                        processList.add(process);
                    }
                    updateProcessList();
                } catch (IOException e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, "Error reading file: " + e.getMessage(), "File Read Error", JOptionPane.ERROR_MESSAGE));
                }
            }


            private void updateSystemTime(int time) {
                SwingUtilities.invokeLater(() -> timeLabel.setText("System Time: " + time));
            }


            private void updateProcessList() {
                processListArea.setText("");
                for (ProcessClass process : processList) {
                    processListArea.append(process.toString() + "\n");
                }
            }

            private void resetProcessFlags() {
                for (ProcessClass process : processList) {
                    process.setCompleted(false);
                    process.setAdded(false);
                }
            }

            public void runFCFS() {
                new Thread(() -> {
                    int systemTime = 0;
                    resetProcessFlags();
                    updateSystemTime(systemTime);
                    List<ProcessClass> availableProcesses = new ArrayList<>();


                    processList.sort(Comparator.comparingInt(ProcessClass::getArrivalTime));


                    while (true) {


                        for (ProcessClass process : processList) {
                            if (process.getArrivalTime() <= systemTime && !process.isCompleted() && !process.isAdded()) {
                                availableProcesses.add(process);
                                process.setAdded(true);
                            }
                        }


                        if (!availableProcesses.isEmpty()) {
                            ProcessClass currentProcess = availableProcesses.remove(0);
                            SwingUtilities.invokeLater(() -> processListArea2.append("Preparing the process with ID: " + currentProcess.getId() + "\n"));
                            try {
                                Thread.sleep(1000);
                                systemTime++;
                                updateSystemTime(systemTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            SwingUtilities.invokeLater(() -> processListArea2.append("Running the process with ID: " + currentProcess.getId() + "\n"));


                            for (int i = 0; i < currentProcess.getCpuTime(); i++) {
                                try {
                                    Thread.sleep(1000);
                                    systemTime++;
                                    updateSystemTime(systemTime);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }


                            currentProcess.setCompleted(true);
                            SwingUtilities.invokeLater(() -> processListArea2.append("Process with ID: " + currentProcess.getId() + " completed.\n"));
                        } else {

                            boolean allCompleted = true;
                            for (ProcessClass process : processList) {
                                if (!process.isCompleted()) {
                                    allCompleted = false;
                                    break;
                                }
                            }

                            if (allCompleted) {
                                break;
                            }

                            try {
                                Thread.sleep(1000);
                                systemTime++;
                                updateSystemTime(systemTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

            }

            public void runPriorityScheduling() {
                new Thread(() -> {
                    int systemTime = 0;
                    resetProcessFlags();
                    updateSystemTime(systemTime);
                    PriorityQueue<ProcessClass> availableProcesses =
                            new PriorityQueue<ProcessClass>(Comparator.comparingInt(ProcessClass::getPriority)
                                    .thenComparing(ProcessClass::getArrivalTime));
                    while (true) {
                        for (ProcessClass process : processList) {
                            if (process.getArrivalTime() <= systemTime && !process.isCompleted() && !process.isAdded()) {
                                if (max_memory - process.getMemory() < 0 && !process.isBlocked()) {
                                    SwingUtilities.invokeLater(() -> processListArea2.append("Process with ID" + process.getId() + "has more memory than the limit\n"));
                                    process.setBlocked(true);

                                }
                                if (availableMemory - process.getMemory() < 0 && !process.isBlocked()) {
                                    process.setBlocked(true);
                                }
                                else {
                                    availableProcesses.add(process);
                                    process.setAdded(true);

                                }

                            }
                        }
                        if (!availableProcesses.isEmpty()) {
                            ProcessClass currentProcess = availableProcesses.poll(); // Берем первый процесс из доступных
                            SwingUtilities.invokeLater(() -> processListArea2.append("Preparing the process with ID: " + currentProcess.getId() + "\n"));
                            try {
                                Thread.sleep(1000);
                                systemTime++;
                                updateSystemTime(systemTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            SwingUtilities.invokeLater(() -> processListArea2.append("Running the process with ID: " + currentProcess.getId() + "\n"));
                            for (int i = 0; i < currentProcess.getCpuTime(); i++) {
                                try {
                                    Thread.sleep(1000);
                                    systemTime++;
                                    updateSystemTime(systemTime);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            currentProcess.setCompleted(true);
                            SwingUtilities.invokeLater(() -> processListArea2.append("Process ID: " + currentProcess.getId() + " completed.\n"));
                        } else {
                            boolean allCompleted = true;
                            for (ProcessClass process : processList) {
                                if (!process.isCompleted()) {
                                    allCompleted = false;
                                    break;
                                }
                            }
                            if (allCompleted) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                                systemTime++;
                                updateSystemTime(systemTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
            public static void main(String[] args) {
                new SchedulerGUI();
            }
        }