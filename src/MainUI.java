import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.Arrays;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseEvent;
import java.util.BitSet;
import java.io.File;
import java.util.Objects;


//Layout and functionality for UI
public class MainUI extends JPanel {


    JTextField[] GPRval = new JTextField[4];
    JTextField[] IXRval = new JTextField[4];
    JTextField[] AdditionalReg = new JTextField[6];
    JTextField BinaryInput = new JTextField(16);
    JButton[] CommandButton = new JButton[7];
    Color stopRed = new Color(255, 46, 12);
    Color backgroundColor = new Color(0, 51, 0);
    Color fontColor = new Color(0, 255, 0);
    Font titleFont;
    Font labelFont;
    Font textFieldFont;

    private void setLabelFont(JLabel label) {
        label.setForeground(fontColor);
        label.setFont(labelFont);
    }


    private void formatButton (JButton button, String type, int RegNumber, JTextField textField){


        String[] updatedButtons = {"Run", "Load", "Store", "Halt", "Step", "Load+", "Store+"};

        if (Arrays.asList(updatedButtons).contains(type)) {
            button.setText(type);
            button.setMargin(new Insets(2, 4, 2, 4));
            button.setPreferredSize(new Dimension(100, 25));
        } else if (!(type.equals("IPL"))) {
            button.setPreferredSize(new Dimension(20, 20));
        }


        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);



        if(type.equals("IRtoAssembly")) {
            button.setPreferredSize(null);
            button.setMargin(new Insets(0, 1, 0, 1));


        }
        if(type.equals("OctConvert")||type.equals("MARcontents")||type.equals("ClearAll")) {
            button.setPreferredSize(null);
            button.setMargin(new Insets(0, 5, 0, 1));

        }

        // Need to use a final JButton and JTextField in the Mouse Listener
        final JButton currentButton = button;
        final JTextField currentField;
        currentField = textField;

        currentButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                currentButton.setBackground(Color.BLUE);// Change to blue when pressed


                if (type.equals("IPL")) {
                    String inputFilePath = currentField.getText();

                    if (inputFilePath == null || inputFilePath.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null,
                                "Program file path invalid. \nProgram  not loaded.",
                                "Error!",
                                JOptionPane.ERROR_MESSAGE);
                        currentButton.setBackground(new Color(0, 102, 0));
                    }
                    else {
                        ControlUI.whenIPLClick(inputFilePath);
                        updateAllRegisterFields();
                    }
                }

                else {
                    String bits = "";
                    if (BinaryInput != null) bits = BinaryInput.getText();

                    //Check to see if the value in BinaryInput is the right size and format for the register
                    //if it is, convert it to a BitSet, save it to the register, and update the field in the display.
                    if ((type + RegNumber).equals("GPR0") && isRightSize(bits, DataType.GPR0.getSize())) {
                        ControlUI.loadGPR0Click(convertStringToBitSet(bits, DataType.GPR0.getSize(), currentField));
                    } else if ((type + RegNumber).equals("GPR1") && isRightSize(bits, DataType.GPR1.getSize())) {
                        ControlUI.loadGPR1Click(convertStringToBitSet(bits, DataType.GPR1.getSize(), currentField));
                    } else if ((type + RegNumber).equals("GPR2") && isRightSize(bits, DataType.GPR2.getSize())) {
                        ControlUI.loadGPR2Clicked(convertStringToBitSet(bits, DataType.GPR2.getSize(),currentField));
                    } else if ((type + RegNumber).equals("GPR3") && isRightSize(bits, DataType.GPR3.getSize())) {
                        ControlUI.loadGPR3Clicked(convertStringToBitSet(bits, DataType.GPR3.getSize(), currentField));
                    } else if ((type + RegNumber).equals("IXR1") && isRightSize(bits, DataType.IXR1.getSize())) {
                        ControlUI.whenIXR1LoadClicked(convertStringToBitSet(bits, DataType.IXR1.getSize(), currentField));
                    } else if ((type + RegNumber).equals("IXR2") && isRightSize(bits, DataType.IXR2.getSize())) {
                        ControlUI.whenIXR2LoadClicked(convertStringToBitSet(bits, DataType.IXR2.getSize(), currentField));
                    } else if ((type + RegNumber).equals("IXR3") && isRightSize(bits, DataType.IXR3.getSize())) {
                        ControlUI.whenIXR3LoadClicked(convertStringToBitSet(bits, DataType.IXR3.getSize(), currentField));
                    } else if (type.equals("PC") && isRightSize(bits, DataType.PC.getSize())) {
                        ControlUI.whenPCLoadClicked(convertStringToBitSet(bits, DataType.PC.getSize(), currentField));
                        //Update IR
                        AdditionalReg[3].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getIR(), DataType.IR.getSize()));
                    } else if (type.equals("MAR") && isRightSize(bits, DataType.MAR.getSize())) {
                        ControlUI.whenMARLoadClicked(convertStringToBitSet(bits, DataType.MAR.getSize(), currentField));
                    } else if (type.equals("MBR") && isRightSize(bits, DataType.MBR.getSize())) {
                        ControlUI.whenMBRLoadClicked(convertStringToBitSet(bits, DataType.MBR.getSize(), currentField));
                    }

                    //Store, Store+, Load, Load+ logic
                    else if (type.equals("Store") || type.equals("Store+")){

                        // The panel is going to give the user an alert if the MAR is not between 6 and 2047.
                        // This will tell the user that the value is not going to get stored.
                        //We are still going to let the backend process the command and set the MFR appropriately, though.

                        int MARval = Integer.parseInt(ControlUI.convertBitSetToBinaryString(ControlUI.getMAR(), DataType.MAR.getSize()),2);
                        if(MARval < 6 || MARval > 2047) {
                            JOptionPane.showMessageDialog(null,
                                    "MBR not stored.",
                                    "Error!",
                                    JOptionPane.ERROR_MESSAGE);
                            currentButton.setBackground(new Color(0, 102, 0));
                        }


                        if(type.equals("Store")) {
                            ControlUI.whenStoreClicked();
                        } else {
                            ControlUI.whenStorePlusClicked();
                            //update MAR
                            AdditionalReg[1].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getMAR(), DataType.MAR.getSize()));
                        }

                        //update MFR in case there is a fault
                        AdditionalReg[5].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getMFR(), DataType.MFR.getSize()));



                    }
                    else if (type.equals("Load") || type.equals("Load+")){

                        if(type.equals("Load")) ControlUI.LoadIsClicked();
                        else {
                            ControlUI.LoadPlusIsClicked();
                            //update MAR
                            AdditionalReg[1].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getMAR(), DataType.MAR.getSize()));
                        }
                        //update MBR
                        AdditionalReg[2].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getMBR(), DataType.MBR.getSize()));
                    }
                    else if (type.equals("Step")){
                        if(ControlUI.cpu.checkHLT(ControlUI.getHLT())) {
                            JOptionPane.showMessageDialog(null,
                                    "Program Halted\n\n Click the Halt to resume\n\nPC Should be valid",
                                    "Machine Halted",
                                    JOptionPane.INFORMATION_MESSAGE);
                            currentButton.setBackground(new Color(0, 102, 0));
                        }
                        else {
                            ControlUI.whenStepClicked();
                            updateAllRegisterFields();
                        }

                    }
                    else if (type.equals("Run")){
                        if(ControlUI.cpu.checkHLT(ControlUI.getHLT())) {
                            JOptionPane.showMessageDialog(null,
                                    "Program Halted\n\n Click the Halt to resume\n\nPC Should be valid",
                                    "Machine Halted",
                                    JOptionPane.INFORMATION_MESSAGE);
                            currentButton.setBackground(new Color(0, 102, 0));
                        }
                        else {
                            ControlUI.whenRunClicked();
                            updateAllRegisterFields();
                        }
                    }
                    else if (type.equals("IRtoAssembly")) {
                        String IRbinary = AdditionalReg[3].getText();
                        int intVersion = Integer.parseInt(IRbinary,2);
                        String octalVersion = Integer.toOctalString(intVersion);

                        CPUExecute thisPackage = ControlUI.cpu.computeinstructionExecutionPackage(convertStringToBitSet(IRbinary, DataType.IXR2.getSize(), AdditionalReg[3]));

                        String message;
                        if(Objects.equals(thisPackage.getInstructionString(), "Data")){
                            message = "This contains data.\nDec: "+intVersion+"\nOct: "+octalVersion;
                        } else {
                            message = "Instruction: " + thisPackage.getInstructionString() + "\nR:" + thisPackage.getR() + " IX:" + thisPackage.getIX() +
                                    " I: " + thisPackage.getI() + "\nAddress: " + thisPackage.getAddress() + " EA: " + thisPackage.getEffectiveAddress();
                        }

                        JOptionPane.showMessageDialog(null,
                                message,
                                "Assembly only",
                                JOptionPane.INFORMATION_MESSAGE);
                        currentButton.setBackground(new Color(0, 102, 0));
                    }else if (type.equals("MARcontents")) {

                        BitSet MarValueBitset = ControlUI.cpu.getMemoryValue(ControlUI.getCurrentMARValue());
                        String BinaryString = ControlUI.convertBitSetToBinaryString(MarValueBitset, DataType.MAR.getSize());
                        int intVersion = Integer.parseInt(ControlUI.convertBitSetToBinaryString(MarValueBitset, DataType.MAR.getSize()), 2);
                        String octalVersion = Integer.toOctalString(intVersion);
                        JOptionPane.showMessageDialog(null,
                                "Value in MAR is:\n Bin: "+BinaryString+"\n=Oct: "+octalVersion+"\n=Dec: "+intVersion,
                                "c(MAR)",
                                JOptionPane.INFORMATION_MESSAGE);

                        currentButton.setBackground(new Color(0, 102, 0));
                    }else if (type.equals("ClearAll")) {
                        ControlUI.whenClearClicked();
                        updateAllRegisterFields();
                        JOptionPane.showMessageDialog(null,
                                "You deleted everything",
                                "CLEAR",
                                JOptionPane.INFORMATION_MESSAGE);
                        currentButton.setBackground(stopRed);
                    }else if (type.equals("Halt")){


                        ControlUI.whenHaltClicked();

                        if(ControlUI.cpu.checkHLT(ControlUI.getHLT()))
                            currentButton.setBackground(stopRed);
                        else currentButton.setBackground(new Color(0, 102, 0));
                    }

                    else currentButton.setBackground(new Color(0, 102, 0));


                }

                //Prints the contents of all registers to System.out for debugging purposes
                ControlUI.printAllRegisters();

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(!type.equals("Halt")) currentButton.setBackground(new Color(0, 102, 0));  // Revert back to orange after release
            }
        });
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 204, 0), 2));
        button.setBackground(new Color(0, 102, 0));
        button.setForeground(new Color(0, 255, 0));
    }


    public MainUI() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints position = new GridBagConstraints();
        position.anchor = GridBagConstraints.NORTH;
        position.fill = GridBagConstraints.NONE;
        this.setBackground(backgroundColor);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        String fontName = "Courier";

        if (isFontAvailable(availableFonts, "Courier New")) {
            fontName = "Courier New";
        } else if (isFontAvailable(availableFonts, "VT323")) {
            fontName = "VT323";
        }


        // Use the determined font
        titleFont = new Font(fontName, Font.BOLD, 20);
        labelFont = new Font(fontName, Font.PLAIN, 16);
        textFieldFont = new Font(fontName, Font.PLAIN, 16);

        JLabel title = new JLabel("CSCI 6461 Machine Simulator - Team 10");
        title.setFont(titleFont);
        title.setForeground(fontColor);
        position.gridy = 0;
        position.gridx = 1;
        position.gridwidth = 12;
        position.insets = new Insets(10, 10, 10, 10);
        this.add(title, position);
        //Create GPR Boxes
        position.gridy = 3;
        position.gridx = 9;
        position.gridwidth = 1;
        JButton[] GPRsubmit = new JButton[4];
        for(int i=0; i<4; i++) {
            JLabel number = new JLabel(String.valueOf("GPR"+i));
            setLabelFont(number);
            position.gridy = i+3;
            position.gridx = 8;
            position.gridwidth = 1;
            position.anchor = GridBagConstraints.NORTHEAST;
            position.insets = new Insets(5, 10, 5, 10);
            this.add(number, position);
            number.setBackground(new Color(0, 255, 0));
            GPRval[i] = new JTextField(16);
            GPRval[i].setFont(textFieldFont);
            GPRval[i].setBorder(new EmptyBorder(2, 5, 2, 5));
            GPRval[i].setEditable(false);
            GPRval[i].setText("0000000000000000");

            position.gridy = i+3;
            position.gridx = 9;
            position.gridwidth = 2;
            position.anchor = GridBagConstraints.NORTHWEST;
            position.insets = new Insets(5, 10, 5, 10);
            this.add(GPRval[i], position);

            GPRsubmit[i] = new JButton();
            formatButton(GPRsubmit[i],"GPR",i,GPRval[i]);

            position.gridx = 11;
            position.gridwidth = 1;
            this.add(GPRsubmit[i], position);

        }

        position.gridy = 8;
        position.gridx = 9;
        position.gridwidth = 2;

        JButton[] IXRsubmit = new JButton[4];
        for(int i=1; i<4; i++) {

            JLabel number = new JLabel(String.valueOf("IXR" + i));
            setLabelFont(number);
            position.gridy = i+7;
            position.gridx = 8;
            position.gridwidth = 1;
            position.insets = new Insets(5, 10, 5, 10);
            position.anchor = GridBagConstraints.NORTHEAST;
            this.add(number, position);
            number.setBackground(new Color(0, 255, 0));
            IXRval[i] = new JTextField(16);
            IXRval[i].setFont(textFieldFont);
            IXRval[i].setBorder(new EmptyBorder(2, 5, 2, 5));
            IXRval[i].setEditable(false);
            IXRval[i].setText("0000000000000000");

            position.gridy = i+7;
            position.gridx = 9;
            position.gridwidth = 2;
            position.anchor = GridBagConstraints.NORTHWEST;

            position.insets = new Insets(5, 10, 5, 10);
            this.add(IXRval[i], position);

            IXRsubmit[i] = new JButton();
            formatButton(IXRsubmit[i],"IXR",i,IXRval[i]);

            position.gridx = 11;
            position.gridwidth = 1;
            this.add(IXRsubmit[i], position);

        }

        JButton[] RegSubmit = new JButton[6];

        for(int i=0; i<6; i++) {

            String RegName = switch (i) {
                case 0 -> "PC";
                case 1 -> "MAR";
                case 2 -> "MBR";
                case 3 -> "IR";
                case 4 -> "CC";
                default -> "MFR";
            };

            JLabel RegLabel = new JLabel (RegName);
            RegLabel.setBackground(new Color(0, 255, 0));
            setLabelFont(RegLabel);
            position.gridy = i+12;
            position.gridx = 8;
            position.gridwidth = 1;
            position.insets = new Insets(5, 10, 5, 10);
            this.add(RegLabel, position);

            int length =switch (i) {
                case 0, 1 -> 12;
                case 2, 3 -> 16;
                default -> 4;
            };

            AdditionalReg[i] = new JTextField(length);
            AdditionalReg[i].setFont(textFieldFont);
            AdditionalReg[i].setBorder(new EmptyBorder(2, 5, 2, 5));
            AdditionalReg[i].setEditable(false);
            String string = String.format("%0" + length + "d", 0);
            AdditionalReg[i].setText(string);

            position.anchor = GridBagConstraints.NORTHWEST;
            position.gridy = i+12;
            position.gridx = 9;
            position.gridwidth = 2;
            if (i==4 || i==5)  position.gridwidth = 1;
            position.insets = new Insets(5, 10, 5, 10);
            this.add(AdditionalReg[i], position);

            if (i==4) {
                JLabel OUDE = new JLabel("(OUDE)");
                setLabelFont(OUDE);
                position.anchor = GridBagConstraints.NORTHWEST;
                position.gridy = 16;
                position.gridx = 10;
                position.gridwidth = 1;
                this.add(OUDE, position);
            }

            if(i==5) {
                JLabel MOTR = new JLabel("(MOTR)");
                setLabelFont(MOTR);
                position.anchor = GridBagConstraints.NORTHWEST;
                position.gridy = 17;
                position.gridx = 10;
                position.gridwidth = 1;
                this.add(MOTR, position);
            }
            if(i<3) {
                RegSubmit[i] = new JButton();
                formatButton(RegSubmit[i],RegName, 0,AdditionalReg[i]);
                position.gridy = 12+i;
                position.gridx = 11;
                position.gridwidth = 1;
                this.add(RegSubmit[i], position);
            }

            JButton IRtoAssembly = new JButton("->A");
            position.gridx = 11;
            position.gridy = 15;
            position.gridwidth = 1;
            position.anchor = GridBagConstraints.NORTHWEST;
            IRtoAssembly.setFont(labelFont);
            formatButton(IRtoAssembly,"IRtoAssembly",0,AdditionalReg[3]);
            this.add(IRtoAssembly, position);

            JButton MARcontents = new JButton("c(MAR)");
            position.gridx = 0;
            position.gridy = 10;
            position.gridwidth = 1;
            position.anchor = GridBagConstraints.NORTHWEST;
            MARcontents.setFont(labelFont);
            formatButton(MARcontents,"MARcontents",0,AdditionalReg[3]);
            this.add(MARcontents, position);

            JButton ClearAll = new JButton("CLEAR");
            position.gridx = 0;
            position.gridy = 11;
            position.gridwidth = 1;
            position.anchor = GridBagConstraints.NORTHWEST;
            ClearAll.setFont(labelFont);
            formatButton(ClearAll,"ClearAll",0,AdditionalReg[3]);
            this.add(ClearAll, position);

        }

        for(int i=0; i<7; i++) {

            String buttonName = switch (i) {
                case 0 -> "Run";
                case 1 -> "Load";
                case 2 -> "Store";
                case 3 -> "Halt";
                case 4 -> "Step";
                case 5 -> "Load+";
                default -> "Store+";
            };
            position.gridy = i+3;
            position.gridwidth = 1;
            position.insets = new Insets(5, 5, 5, 5);
            CommandButton[i] = new JButton();
            formatButton(CommandButton[i],buttonName,0,null);
            position.gridx = 0;
            position.anchor = GridBagConstraints.NORTHWEST;
            position.gridwidth = 1;
            this.add(CommandButton[i], position);
        }

        JLabel ProgramFileLabel = new JLabel("Program File");
        ProgramFileLabel.setBackground(Color.BLACK);
        ProgramFileLabel.setForeground(new Color(0, 255, 0));
        setLabelFont(ProgramFileLabel);
        ProgramFileLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 204, 0), 1));
        position.gridy = 1;
//        position.anchor = GridBagConstraints.NORTHEAST;
        position.gridx = 0;
        this.add(ProgramFileLabel, position);

        JTextField ProgramFileInput = new JTextField(27);
        ProgramFileInput.setFont(textFieldFont);
        position.gridy = 1;
//        position.anchor = GridBagConstraints.NORTHWEST;
        position.gridx = 2;
        position.gridwidth = 4;
        this.add(ProgramFileInput, position);

        //IPL Button


        JButton IPL = new JButton("IPL");
        position.gridy= 2;
        position.gridx= 3;

        position.anchor = GridBagConstraints.NORTHWEST;
        IPL.setFont(labelFont);
        formatButton(IPL,"IPL",0,ProgramFileInput);
        this.add(IPL, position);

        // Add a button to open file chooser
        JButton selectButton = new JButton("Select File");
        selectButton.setFont(labelFont);
        position.gridy=2;
        position.gridx =0 ;
        position.gridwidth = 2;
        position.weighty = 1;
        this.add(selectButton, position);

        selectButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                ProgramFileInput.setText(selectedFile.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(null, "No file selected", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });





    }
    private boolean isRightSize(String bits, int size){

        if (!bits.matches("[01]+")) {
            JOptionPane.showMessageDialog(null,
                    "Value not saved. \nFMust be binary",
                    "Error!",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            int value = Integer.parseInt(bits, 2); // Parse binary to int

            int maxValue = (1 << size) - 1;
            if (value > maxValue) {
                JOptionPane.showMessageDialog(null,
                        "Value not saved. \nThe binary number exceeds the " + size + "-bit limit.",
                        "Error!",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Value not saved. \nMust be binary number",
                    "Error!",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;

    }

    private BitSet convertStringToBitSet(String bits, int size, JTextField currentfield) {
        if (bits.length() > size) {
            bits = bits.substring(bits.length() - size);
        }
        String paddedBits = String.format("%" + size + "s", bits).replace(' ', '0');
        int value = Integer.parseInt(bits, 2);
        BitSet binaryArray = new BitSet(size);
        binaryArray.or(BitSet.valueOf(new long[]{value}));
        currentfield.setText(paddedBits);
        return binaryArray;
    }

    private void updateAllRegisterFields() {
        // Update GPR fields
        GPRval[0].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getGPR0(), DataType.GPR0.getSize()));
        GPRval[1].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getGPR1(), DataType.GPR1.getSize()));
        GPRval[2].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getGPR2(), DataType.GPR2.getSize()));
        GPRval[3].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getGPR3(), DataType.GPR3.getSize()));

        // Update IXR fields
        IXRval[1].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getIXR1(), DataType.IXR1.getSize()));
        IXRval[2].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getIXR2(), DataType.IXR2.getSize()));
        IXRval[3].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getIXR3(), DataType.IXR3.getSize()));

        // Update Additional Registers
        AdditionalReg[0].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getPC(), DataType.PC.getSize()));
        AdditionalReg[1].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getMAR(), DataType.MAR.getSize()));
        AdditionalReg[2].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getMBR(), DataType.MBR.getSize()));
        AdditionalReg[3].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getIR(), DataType.IR.getSize()));
        AdditionalReg[4].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getCC(), DataType.CC.getSize()));
        AdditionalReg[5].setText(ControlUI.convertBitSetToBinaryString(ControlUI.getMFR(), DataType.MFR.getSize()));

        //Update Halt
        if(ControlUI.cpu.checkHLT(ControlUI.getHLT())) CommandButton[3].setBackground(stopRed);
        else CommandButton[3].setBackground(new Color(0, 102, 0));
    }

    private boolean isFontAvailable(String[] availableFonts, String fontName) {
        for (String font : availableFonts) {
            if (font.equalsIgnoreCase(fontName)) {
                return true;
            }
        }
        return false;
    }

}
