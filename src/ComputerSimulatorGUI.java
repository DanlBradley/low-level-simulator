package src;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ComputerSimulatorGUI extends JFrame {
    private Computer computer;

    private JTextField[] gprFields = new JTextField[4];
    private JTextField[] ixrFields = new JTextField[4]; // index 0 unused
    private JTextField pcField, marField, mbrField, irField;
    private JTextField ccField, mfrField;

    // Binary display
    private JTextArea binaryDisplay;

    // Octal input
    private JTextField octalInput;

    private JTextField programFileField;

    // Cache display
    private JTextArea cacheDisplay;

    // Printer output
    private JTextArea printerOutput;

    // Console input
    private JTextField consoleInput;

    private ByteArrayOutputStream outputStream;

    public ComputerSimulatorGUI() {
        computer = new Computer();
        setupOutputStream();
        setupUI();

        programFileField.setText("data/load.txt");
    }

    private void setupOutputStream() {
        outputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(outputStream);
        System.setOut(ps);
    }

    private void setupUI() {
        setTitle("CSCI 6461 Machine Simulator");
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(173, 216, 230));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = createLeftPanel();

        JPanel rightPanel = createRightPanel();

        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("CSCI 6461 Machine Simulator");
        title.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        JPanel registersPanel = createRegistersPanel();
        panel.add(registersPanel);
        panel.add(Box.createVerticalStrut(20));

        JPanel controlsPanel = createControlsPanel();
        panel.add(controlsPanel);
        panel.add(Box.createVerticalStrut(10));

        JPanel programPanel = createProgramPanel();
        panel.add(programPanel);

        return panel;
    }

    private JPanel createRegistersPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("GPR"), gbc);

        for (int i = 0; i < 4; i++) {
            gbc.gridy = i + 1;
            gbc.gridx = 0;
            panel.add(new JLabel(String.valueOf(i)), gbc);

            gbc.gridx = 1;
            gprFields[i] = createRegisterField();
            panel.add(gprFields[i], gbc);

            gbc.gridx = 2;
            JButton btn = createSmallButton();
            panel.add(btn, gbc);
        }

        gbc.gridx = 3; gbc.gridy = 0;
        panel.add(new JLabel("IXR"), gbc);

        for (int i = 1; i <= 3; i++) {
            gbc.gridy = i;
            gbc.gridx = 3;
            panel.add(new JLabel(String.valueOf(i)), gbc);

            gbc.gridx = 4;
            ixrFields[i] = createRegisterField();
            panel.add(ixrFields[i], gbc);

            gbc.gridx = 5;
            JButton btn = createSmallButton();
            panel.add(btn, gbc);
        }

        // PC, MAR, MBR, IR
        gbc.gridx = 6; gbc.gridy = 0;
        panel.add(new JLabel("PC"), gbc);
        gbc.gridy = 1;
        pcField = createRegisterField();
        panel.add(pcField, gbc);
        gbc.gridy = 2;
        JButton pcBtn = createSmallButton();
        panel.add(pcBtn, gbc);

        gbc.gridy = 0;
        gbc.gridx = 8;
        panel.add(new JLabel("MAR"), gbc);
        gbc.gridy = 1;
        marField = createRegisterField();
        panel.add(marField, gbc);
        gbc.gridy = 2;
        JButton marBtn = createSmallButton();
        panel.add(marBtn, gbc);

        gbc.gridy = 0;
        gbc.gridx = 10;
        panel.add(new JLabel("MBR"), gbc);
        gbc.gridy = 1;
        mbrField = createRegisterField();
        panel.add(mbrField, gbc);
        gbc.gridy = 2;
        JButton mbrBtn = createSmallButton();
        panel.add(mbrBtn, gbc);

        gbc.gridy = 0;
        gbc.gridx = 12;
        panel.add(new JLabel("IR"), gbc);
        gbc.gridy = 1;
        irField = createRegisterField();
        panel.add(irField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 8;
        panel.add(new JLabel("CC"), gbc);
        gbc.gridy = 4;
        ccField = new JTextField("OUDE", 8);
        panel.add(ccField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 10;
        panel.add(new JLabel("MFR"), gbc);
        gbc.gridy = 4;
        mfrField = new JTextField("MOTR", 8);
        panel.add(mfrField, gbc);

        return panel;
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel binaryLabel = new JLabel("BINARY");
        binaryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(binaryLabel);

        binaryDisplay = new JTextArea(3, 30);
        binaryDisplay.setEditable(false);
        binaryDisplay.setBackground(Color.WHITE);
        JScrollPane binaryScroll = new JScrollPane(binaryDisplay);
        binaryScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(binaryScroll);

        panel.add(Box.createVerticalStrut(10));

        JPanel octalPanel = new JPanel(new FlowLayout());
        octalPanel.setOpaque(false);
        octalPanel.add(new JLabel("OCTAL INPUT"));
        octalInput = new JTextField("0", 10);
        octalPanel.add(octalInput);
        panel.add(octalPanel);

        panel.add(Box.createVerticalStrut(10));

        JPanel buttonsPanel = createButtonsPanel();
        panel.add(buttonsPanel);

        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 5, 5));
        panel.setOpaque(false);

        JButton loadBtn = createButton("Load");
        JButton runBtn = createButton("Run");
        JButton iplBtn = createButton("IPL");

        JButton loadPlusBtn = createButton("Load+");
        JButton stepBtn = createButton("Step");

        JButton storeBtn = createButton("Store");
        JButton haltBtn = createButton("Halt");

        JButton storePlusBtn = createButton("Store+");

        iplBtn.addActionListener(e -> ipl());
        stepBtn.addActionListener(e -> step());
        runBtn.addActionListener(e -> run());
        haltBtn.addActionListener(e -> halt());

        panel.add(loadBtn);
        panel.add(runBtn);
        panel.add(iplBtn);
        panel.add(loadPlusBtn);
        panel.add(stepBtn);
        panel.add(new JLabel());
        panel.add(storeBtn);
        panel.add(haltBtn);
        panel.add(storePlusBtn);

        iplBtn.setBackground(new Color(255, 100, 100));

        return panel;
    }

    private JPanel createProgramPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.add(new JLabel("Program File"));
        programFileField = new JTextField(40);
        panel.add(programFileField);
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel cacheLabel = new JLabel("Cache Content");
        cacheLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cacheLabel);

        cacheDisplay = new JTextArea(15, 50);
        cacheDisplay.setEditable(false);
        cacheDisplay.setFont(new Font("Monospaced", Font.PLAIN, 10));
        JScrollPane cacheScroll = new JScrollPane(cacheDisplay);
        cacheScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cacheScroll);

        panel.add(Box.createVerticalStrut(10));

        // Printer
        JLabel printerLabel = new JLabel("Printer");
        printerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(printerLabel);

        printerOutput = new JTextArea(15, 50);
        printerOutput.setEditable(false);
        printerOutput.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane printerScroll = new JScrollPane(printerOutput);
        printerScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(printerScroll);

        panel.add(Box.createVerticalStrut(10));

        // Console Input
        JLabel consoleLabel = new JLabel("Console Input");
        consoleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(consoleLabel);

        consoleInput = new JTextField(50);
        consoleInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(consoleInput);

        return panel;
    }

    private JTextField createRegisterField() {
        JTextField field = new JTextField(12);
        field.setEditable(false);
        return field;
    }

    private JButton createSmallButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(20, 20));
        btn.setBackground(new Color(100, 180, 220));
        return btn;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(100, 30));
        btn.setBackground(new Color(100, 180, 220));
        return btn;
    }

    private void ipl() {
        String programFile = programFileField.getText();
        outputStream.reset();
        computer = new Computer();
        computer.IPL(programFile, 14);
        updateDisplay();
        updatePrinterOutput();
    }

    private void step() {
        outputStream.reset();
        computer.singleStep();
        updateDisplay();
        updatePrinterOutput();
    }

    private void run() {
        outputStream.reset();
        computer.run();
        updateDisplay();
        updatePrinterOutput();
    }

    private void halt() {
        updateDisplay();
    }

    private void updateDisplay() {
        // Update GPRs
        for (int i = 0; i < 4; i++) {
            gprFields[i].setText(String.valueOf(computer.cpu.R[i]));
        }

        // Update IXRs
        for (int i = 1; i <= 3; i++) {
            ixrFields[i].setText(String.valueOf(computer.cpu.IX[i]));
        }

        pcField.setText(String.valueOf(computer.cpu.PC));
        marField.setText(String.valueOf(computer.cpu.MAR));
        mbrField.setText(String.valueOf(computer.cpu.MBR));
        irField.setText(String.valueOf(computer.cpu.IR & 0xFFFF));

        updateBinaryDisplay();

        updateCacheDisplay();
    }

    private void updateBinaryDisplay() {
        int ir = computer.cpu.IR & 0xFFFF;
        String binary = String.format("%16s", Integer.toBinaryString(ir)).replace(' ', '0');

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < binary.length(); i++) {
            if (i > 0 && i % 4 == 0) formatted.append(" ");
            formatted.append(binary.charAt(i));
        }

        binaryDisplay.setText(formatted.toString());
    }

    private void updateCacheDisplay() {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 8; col++) {
                int addr = row * 8 + col;
                short value = computer.memory.read(addr);
                sb.append(String.format("%03d %06d ", addr, value & 0xFFFF));
            }
            sb.append("\n");
        }

        cacheDisplay.setText(sb.toString());
    }

    private void updatePrinterOutput() {
        String output = outputStream.toString();
        printerOutput.setText(output);

        // Auto-scroll to bottom
        printerOutput.setCaretPosition(printerOutput.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ComputerSimulatorGUI gui = new ComputerSimulatorGUI();
            gui.setVisible(true);
        });
    }
}