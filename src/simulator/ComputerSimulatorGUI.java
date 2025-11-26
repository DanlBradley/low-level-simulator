package src.simulator;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ComputerSimulatorGUI extends JFrame {
    private Computer computer;

    private final JTextField[] gprFields = new JTextField[4];
    private final JTextField[] ixrFields = new JTextField[4];
    private JTextField pcField, marField, mbrField, irField;
    private String inputBuffer = "";
    private int inputBufferPosition = 0;

    private JTextArea binaryDisplay;

    private JTextField octalInput;

    private JTextArea cacheDisplay;
    private JTextArea printerOutput;
    private JTextField consoleInputField;
    private JTextField loadFileField;
    private JTextField cardReaderFileField;

    public ComputerSimulatorGUI() {
        computer = new Computer();
        computer.setGUI(this);
        setupUI();
        loadFileField.setText("data/load.txt");
        cardReaderFileField.setText("data/card.txt");
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
        consoleInputField.addActionListener(e -> {
            System.out.println("Console input: " + consoleInputField.getText());
            System.out.println("isWaitingForInput: " + computer.isWaitingForInput());
            if (computer.isWaitingForInput()) {
                computer.continueFromInput();
            }
        });
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
        printerOutput.setLineWrap(true);
        printerOutput.setWrapStyleWord(true);
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
            btn.addActionListener(_ -> loadToGPR(regIndex));
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
            btn.addActionListener(_ -> loadToIXR(ixIndex));
            panel.add(btn, gbc);
        }

        gbc.gridx = 6; gbc.gridy = 0;
        panel.add(new JLabel("PC"), gbc);
        gbc.gridy = 1;
        pcField = createWideRegisterField();
        panel.add(pcField, gbc);
        gbc.gridy = 2;
        JButton pcBtn = createSmallButton();
        pcBtn.addActionListener(_ -> loadToPC());
        panel.add(pcBtn, gbc);

        gbc.gridy = 0;
        gbc.gridx = 8;
        panel.add(new JLabel("MAR"), gbc);
        gbc.gridy = 1;
        marField = createWideRegisterField();
        panel.add(marField, gbc);
        gbc.gridy = 2;
        JButton marBtn = createSmallButton();
        marBtn.addActionListener(_ -> loadToMAR());
        panel.add(marBtn, gbc);

        gbc.gridy = 0;
        gbc.gridx = 10;
        panel.add(new JLabel("MBR"), gbc);
        gbc.gridy = 1;
        mbrField = createWideRegisterField();
        panel.add(mbrField, gbc);
        gbc.gridy = 2;
        JButton mbrBtn = createSmallButton();
        mbrBtn.addActionListener(_ -> loadToMBR());
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
        JTextField ccField = new JTextField("OUDE", 18);
        ccField.setEditable(false);
        panel.add(ccField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 10;
        panel.add(new JLabel("MFR"), gbc);
        gbc.gridy = 4;
        JTextField mfrField = new JTextField("MOTR", 18);
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
        octalInput = new JTextField("002000", 10); //start for program_one.txt
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

        iplBtn.addActionListener(_ -> ipl());
        stepBtn.addActionListener(_ -> step());
        runBtn.addActionListener(_ -> run());
        haltBtn.addActionListener(_ -> halt());
        loadBtn.addActionListener(_ -> load());
        storeBtn.addActionListener(_ -> store());
        storePlusBtn.addActionListener(_ -> storePlus());
        loadPlusBtn.addActionListener(_ -> loadPlus());

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
        loadFileField = new JTextField(15);
        panel.add(loadFileField);

        panel.add(Box.createHorizontalStrut(10));
        panel.add(new JLabel("Card Reader File"));
        cardReaderFileField = new JTextField(15);
        panel.add(cardReaderFileField);
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

    /**
     * Validate and parse octal input, ensuring it fits in 16 bits
     * @return the parsed value, or -1 if invalid
     */
    private int validateAndParseOctal() {
        try {
            String octal = octalInput.getText().trim();
            int value = Integer.parseInt(octal, 8);

            if (value < 0 || value > 0xFFFF) {
                JOptionPane.showMessageDialog(this,
                        "Value must be between 0 and 177777 (octal)",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return -1;
            }

            return value;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid octal number!", "Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private void loadToGPR(int index) {
        int value = validateAndParseOctal();
        if (value == -1) return;

        computer.cpu.R[index] = (short) value;
        updateDisplay();
        JOptionPane.showMessageDialog(this,
                "Loaded " + String.format("%06o", value) + " (octal) = " + value + " (decimal) into R" + index);
    }

    private void loadToIXR(int index) {
        int value = validateAndParseOctal();
        if (value == -1) return;

        computer.cpu.IX[index] = (short) value;
        updateDisplay();
        JOptionPane.showMessageDialog(this,
                "Loaded " + String.format("%06o", value) + " (octal) = " + value + " (decimal) into X" + index);
    }

    private void loadToPC() {
        int value = validateAndParseOctal();
        if (value == -1) return;

        computer.cpu.PC = (short) value;
        updateDisplay();
        JOptionPane.showMessageDialog(this,
                "Loaded " + String.format("%06o", value) + " (octal) = " + value + " (decimal) into PC");
    }

    private void loadToMAR() {
        int value = validateAndParseOctal();
        if (value == -1) return;

        computer.cpu.MAR = (short) value;
        updateDisplay();
        JOptionPane.showMessageDialog(this,
                "Loaded " + String.format("%06o", value) + " (octal) = " + value + " (decimal) into MAR");
    }

    private void loadToMBR() {
        int value = validateAndParseOctal();
        if (value == -1) return;

        computer.cpu.MBR = (short) value;
        updateDisplay();
        JOptionPane.showMessageDialog(this,
                "Loaded " + String.format("%06o", value) + " (octal) = " + value + " (decimal) into MBR");
    }

    private void load() {
        int value = validateAndParseOctal();
        if (value == -1) return;

        int address = computer.cpu.MAR & 0xFFFF;

        computer.cache.write(address, (short) value);
        computer.cpu.MBR = (short) value;

        updateDisplay();
        JOptionPane.showMessageDialog(this,
                "Loaded " + String.format("%06o", value) + " (octal) into Memory[" + address + "]");
    }

    private void ipl() {
        String loadFile = loadFileField.getText();
        String cardReaderFile = cardReaderFileField.getText();
        computer = new Computer();
        computer.setGUI(this);
        if (cardReaderFile != null && !cardReaderFile.trim().isEmpty()) {
            computer.setCardReaderFile(cardReaderFile.trim());
        }
        computer.IPL(loadFile, Integer.parseInt(pcField.getText().trim(), 8));
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

        if (!computer.isWaitingForInput()) {
            JOptionPane.showMessageDialog(this, "Program execution completed!");
        }
    }

    private void halt() {
        updateDisplay();
        JOptionPane.showMessageDialog(this, "Halted!");
    }

    private void store() {
        int address = computer.cpu.MAR & 0xFFFF;
        short value = computer.cache.read(address);
        computer.cpu.MBR = value;

        updateDisplay();
        JOptionPane.showMessageDialog(this,
                "Stored Memory[" + address + "] = " + String.format("%06o", value & 0xFFFF) + " (octal) into MBR");
    }

    private void loadPlus() {
        load();
        computer.cpu.MAR++;
        updateDisplay();
    }

    private void storePlus() {
        store();
        computer.cpu.MAR++;
        updateDisplay();
    }

    /** Refresh all register fields and the IR binary view (octal formatting). */
    public void updateDisplay() {
        for (int i = 0; i < 4; i++) {
            gprFields[i].setText(String.format("%06o", computer.cpu.R[i] & 0xFFFF));
        }

        for (int i = 1; i <= 3; i++) {
            ixrFields[i].setText(String.format("%06o", computer.cpu.IX[i] & 0xFFFF));
        }

        pcField.setText(String.format("%06o", computer.cpu.PC & 0xFFFF));
        marField.setText(String.format("%06o", computer.cpu.MAR & 0xFFFF));
        mbrField.setText(String.format("%06o", computer.cpu.MBR & 0xFFFF));
        irField.setText(String.format("%06o", computer.cpu.IR & 0xFFFF));

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
        StringBuilder cache = new StringBuilder();

        var cacheMap = computer.cache.getCacheMap();
        int iter = 0;
        for (Map.Entry<Short,Short> cacheLoc : cacheMap.entrySet()) {
            iter++;
            int address = cacheLoc.getKey() & 0xFFFF;
            short value = cacheLoc.getValue();
            cache.append(String.format("%03o %06o ", address, value & 0xFFFF));
            if (iter % 5 == 0) cache.append("\n");
        }

        cacheDisplay.setText(cache.toString());
    }

    public void printChar(char ch) {
        printerOutput.append(String.valueOf(ch));
        printerOutput.setCaretPosition(printerOutput.getDocument().getLength());
    }

    public void printToOutput(String text) {
        printerOutput.append(text + "\n");
        printerOutput.setCaretPosition(printerOutput.getDocument().getLength());
    }

    /**
     * updated to go thru string input buffer
     * @return
     */
    public boolean hasConsoleInput() {
        String fieldText = consoleInputField.getText();
        if (!fieldText.isEmpty() && inputBuffer.isEmpty()) {
            inputBuffer = fieldText + "\n";
            inputBufferPosition = 0;
        }
        return inputBufferPosition < inputBuffer.length();
    }

    public String getConsoleInput() {
        if (inputBufferPosition < inputBuffer.length()) {
            String result = String.valueOf(inputBuffer.charAt(inputBufferPosition));
            inputBufferPosition++;
            return result;
        }
        return "";
    }

    public void clearConsoleInput() {
        if (inputBufferPosition >= inputBuffer.length()) {
            consoleInputField.setText("");
            inputBuffer = "";
            inputBufferPosition = 0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ComputerSimulatorGUI gui = new ComputerSimulatorGUI();
            gui.setVisible(true);
        });
    }
}