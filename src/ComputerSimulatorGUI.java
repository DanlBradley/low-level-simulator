package src;

import javax.swing.*;
import java.awt.*;

public class ComputerSimulatorGUI extends JFrame {
    private Computer computer;

    private JTextField[] gprFields = new JTextField[4];
    private JTextField[] ixrFields = new JTextField[4];
    private JTextField pcField, marField, mbrField, irField;
    private JTextField ccField, mfrField;

    private JTextArea binaryDisplay;
    private JTextField octalInput;
    private JTextField programFileField;

    private JTextArea cacheDisplay;
    private JTextArea printerOutput;
    private JTextField consoleInputField;

    public ComputerSimulatorGUI() {
        computer = new Computer();
        setupUI();
        programFileField.setText("data/load.txt");
        updateDisplay();
    }

    private void setupUI() {
        setTitle("CSCI 6461 Machine Simulator");
        setSize(1200, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(173, 216, 230));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = createRightPanel();
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private JPanel createCenterPanel() {
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

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(400, 0));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel cacheLabel = new JLabel("Cache Content");
        cacheLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cacheLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rightPanel.add(cacheLabel);
        rightPanel.add(Box.createVerticalStrut(5));

        cacheDisplay = new JTextArea(12, 35);
        cacheDisplay.setEditable(false);
        cacheDisplay.setBackground(Color.WHITE);
        cacheDisplay.setFont(new Font("Monospaced", Font.PLAIN, 10));
        JScrollPane cacheScroll = new JScrollPane(cacheDisplay);
        cacheScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        cacheScroll.setPreferredSize(new Dimension(380, 200));
        cacheScroll.setMaximumSize(new Dimension(380, 200));
        rightPanel.add(cacheScroll);

        rightPanel.add(Box.createVerticalStrut(15));

        JLabel printerLabel = new JLabel("Printer");
        printerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        printerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rightPanel.add(printerLabel);
        rightPanel.add(Box.createVerticalStrut(5));

        printerOutput = new JTextArea(10, 35);
        printerOutput.setEditable(false);
        printerOutput.setBackground(Color.WHITE);
        printerOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane printerScroll = new JScrollPane(printerOutput);
        printerScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        printerScroll.setPreferredSize(new Dimension(380, 180));
        printerScroll.setMaximumSize(new Dimension(380, 180));
        rightPanel.add(printerScroll);

        rightPanel.add(Box.createVerticalStrut(15));

        JLabel consoleLabel = new JLabel("Console Input");
        consoleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        consoleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        rightPanel.add(consoleLabel);
        rightPanel.add(Box.createVerticalStrut(5));

        consoleInputField = new JTextField(35);
        consoleInputField.setAlignmentX(Component.LEFT_ALIGNMENT);
        consoleInputField.setMaximumSize(new Dimension(380, 30));
        consoleInputField.setPreferredSize(new Dimension(380, 30));
        rightPanel.add(consoleInputField);

        rightPanel.add(Box.createVerticalGlue());

        return rightPanel;
    }

    private JPanel createRegistersPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 40;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("GPR"), gbc);

        for (int i = 0; i < 4; i++) {
            gbc.gridy = i + 1;
            gbc.gridx = 0;
            panel.add(new JLabel(String.valueOf(i)), gbc);

            gbc.gridx = 1;
            gprFields[i] = createWideRegisterField();
            panel.add(gprFields[i], gbc);

            gbc.gridx = 2;
            int regIndex = i;
            JButton btn = createSmallButton();
            btn.addActionListener(e -> loadToGPR(regIndex));
            panel.add(btn, gbc);
        }

        gbc.gridx = 3; gbc.gridy = 0;
        panel.add(new JLabel("IXR"), gbc);

        for (int i = 1; i <= 3; i++) {
            gbc.gridy = i;
            gbc.gridx = 3;
            panel.add(new JLabel(String.valueOf(i)), gbc);

            gbc.gridx = 4;
            ixrFields[i] = createWideRegisterField();
            panel.add(ixrFields[i], gbc);

            gbc.gridx = 5;
            int ixIndex = i;
            JButton btn = createSmallButton();
            btn.addActionListener(e -> loadToIXR(ixIndex));
            panel.add(btn, gbc);
        }

        gbc.gridx = 6; gbc.gridy = 0;
        panel.add(new JLabel("PC"), gbc);
        gbc.gridy = 1;
        pcField = createWideRegisterField();
        panel.add(pcField, gbc);
        gbc.gridy = 2;
        JButton pcBtn = createSmallButton();
        pcBtn.addActionListener(e -> loadToPC());
        panel.add(pcBtn, gbc);

        gbc.gridy = 0;
        gbc.gridx = 8;
        panel.add(new JLabel("MAR"), gbc);
        gbc.gridy = 1;
        marField = createWideRegisterField();
        panel.add(marField, gbc);
        gbc.gridy = 2;
        JButton marBtn = createSmallButton();
        marBtn.addActionListener(e -> loadToMAR());
        panel.add(marBtn, gbc);

        gbc.gridy = 0;
        gbc.gridx = 10;
        panel.add(new JLabel("MBR"), gbc);
        gbc.gridy = 1;
        mbrField = createWideRegisterField();
        panel.add(mbrField, gbc);
        gbc.gridy = 2;
        JButton mbrBtn = createSmallButton();
        mbrBtn.addActionListener(e -> loadToMBR());
        panel.add(mbrBtn, gbc);

        gbc.gridy = 0;
        gbc.gridx = 12;
        panel.add(new JLabel("IR"), gbc);
        gbc.gridy = 1;
        irField = createWideRegisterField();
        panel.add(irField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 8;
        panel.add(new JLabel("CC"), gbc);
        gbc.gridy = 4;
        ccField = new JTextField("OUDE", 18);
        ccField.setEditable(false);
        panel.add(ccField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 10;
        panel.add(new JLabel("MFR"), gbc);
        gbc.gridy = 4;
        mfrField = new JTextField("MOTR", 18);
        mfrField.setEditable(false);
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
        binaryDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
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

    private JTextField createRegisterField() {
        JTextField field = new JTextField(12);
        field.setEditable(false);
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setText("0");
        field.setPreferredSize(new Dimension(120, 25));
        return field;
    }

    private JTextField createWideRegisterField() {
        JTextField field = new JTextField(18);
        field.setEditable(false);
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setText("0");
        field.setPreferredSize(new Dimension(120, 25));
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

    private void loadToGPR(int index) {
        try {
            String octal = octalInput.getText().trim();
            int value = Integer.parseInt(octal, 8);
            computer.getCPU().R[index] = (short) value;
            updateDisplay();
            JOptionPane.showMessageDialog(this,
                    "Loaded " + octal + " (octal) = " + value + " (decimal) into R" + index);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid octal number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadToIXR(int index) {
        try {
            String octal = octalInput.getText().trim();
            int value = Integer.parseInt(octal, 8);
            computer.getCPU().IX[index] = (short) value;
            updateDisplay();
            JOptionPane.showMessageDialog(this,
                    "Loaded " + octal + " (octal) = " + value + " (decimal) into X" + index);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid octal number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadToPC() {
        try {
            String octal = octalInput.getText().trim();
            int value = Integer.parseInt(octal, 8);
            computer.getCPU().PC = (short) value;
            updateDisplay();
            JOptionPane.showMessageDialog(this,
                    "Loaded " + octal + " (octal) = " + value + " (decimal) into PC");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid octal number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadToMAR() {
        try {
            String octal = octalInput.getText().trim();
            int value = Integer.parseInt(octal, 8);
            computer.getCPU().MAR = (short) value;
            updateDisplay();
            JOptionPane.showMessageDialog(this,
                    "Loaded " + octal + " (octal) = " + value + " (decimal) into MAR");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid octal number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadToMBR() {
        try {
            String octal = octalInput.getText().trim();
            int value = Integer.parseInt(octal, 8);
            computer.getCPU().MBR = (short) value;
            updateDisplay();
            JOptionPane.showMessageDialog(this,
                    "Loaded " + octal + " (octal) = " + value + " (decimal) into MBR");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid octal number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ipl() {
        String programFile = programFileField.getText();
        computer = new Computer();
        computer.IPL(programFile, 14);
        updateDisplay();
        JOptionPane.showMessageDialog(this, "Program loaded successfully!");
    }

    private void step() {
        computer.singleStep();
        updateDisplay();
    }

    private void run() {
        computer.run();
        updateDisplay();
        JOptionPane.showMessageDialog(this, "Program execution completed!");
    }

    private void halt() {
        updateDisplay();
        JOptionPane.showMessageDialog(this, "Halted!");
    }

    private void updateDisplay() {
        for (int i = 0; i < 4; i++) {
            gprFields[i].setText(String.format("%06o", computer.getCPU().R[i] & 0xFFFF));
        }

        for (int i = 1; i <= 3; i++) {
            ixrFields[i].setText(String.format("%06o", computer.getCPU().IX[i] & 0xFFFF));
        }

        pcField.setText(String.format("%06o", computer.getCPU().PC & 0xFFFF));
        marField.setText(String.format("%06o", computer.getCPU().MAR & 0xFFFF));
        mbrField.setText(String.format("%06o", computer.getCPU().MBR & 0xFFFF));
        irField.setText(String.format("%06o", computer.getCPU().IR & 0xFFFF));

        updateBinaryDisplay();
        updateCacheDisplay();
    }

    private void updateBinaryDisplay() {
        int ir = computer.getCPU().IR & 0xFFFF;
        String binary = String.format("%16s", Integer.toBinaryString(ir)).replace(' ', '0');

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < binary.length(); i++) {
            if (i > 0 && i % 4 == 0) formatted.append(" ");
            formatted.append(binary.charAt(i));
        }

        binaryDisplay.setText(formatted.toString());
    }

    private void updateCacheDisplay() {
        StringBuilder cache = new StringBuilder();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int address = row * 8 + col;
                short value = computer.getMemory().read(address);
                cache.append(String.format("%03o %06o ", address, value & 0xFFFF));
            }
            cache.append("\n");
        }

        cacheDisplay.setText(cache.toString());
    }

    public void printToOutput(String text) {
        printerOutput.append(text + "\n");
        printerOutput.setCaretPosition(printerOutput.getDocument().getLength());
    }

    public String getConsoleInput() {
        return consoleInputField.getText();
    }

    public void clearConsoleInput() {
        consoleInputField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ComputerSimulatorGUI gui = new ComputerSimulatorGUI();
            gui.setVisible(true);
        });
    }
}