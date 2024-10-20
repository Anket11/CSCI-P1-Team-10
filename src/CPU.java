import java.math.BigInteger;
import java.util.BitSet;

// The CPU class simulates the behavior of a CPU with registers and memory.
// This class serves as the backend of this project.
public class CPU {

    // Define all registers
    Register MAR = new Register(DataType.MAR.getSize());
    Register MBR = new Register(DataType.MBR.getSize());
    Register PC = new Register(DataType.PC.getSize());
    Register IXR1 = new Register(DataType.IXR1.getSize());
    Register IXR2 = new Register(DataType.IXR2.getSize());
    Register IXR3 = new Register(DataType.IXR3.getSize());
    Register GPR0 = new Register(DataType.GPR0.getSize());
    Register GPR1 = new Register(DataType.GPR1.getSize());
    Register GPR2 = new Register(DataType.GPR2.getSize());
    Register GPR3 = new Register(DataType.GPR3.getSize());
    Register CC = new Register(DataType.CC.getSize());
    Register IR = new Register(DataType.IR.getSize());
    Register MFR = new Register(DataType.MFR.getSize());
    Register HLT = new Register(DataType.HLT.getSize()); // We may need this.

    Memory mainMemory = new Memory(); // Define the main memory.

    // Set the value of a specific register based on the provided DataType.
    public void setRegisterValue(DataType DataType, BitSet value) {
        switch (DataType.getType()) {
            case "MAR" -> MAR.setRegisterValue(value);
            case "MBR" -> MBR.setRegisterValue(value);
            case "PC" -> PC.setRegisterValue(value);
            case "CC" -> CC.setRegisterValue(value);
            case "IR" -> IR.setRegisterValue(value);
            case "IXR1" -> IXR1.setRegisterValue(value);
            case "IXR2" -> IXR2.setRegisterValue(value);
            case "IXR3" -> IXR3.setRegisterValue(value);
            case "GPR0" -> GPR0.setRegisterValue(value);
            case "GPR1" -> GPR1.setRegisterValue(value);
            case "GPR2" -> GPR2.setRegisterValue(value);
            case "GPR3" -> GPR3.setRegisterValue(value);
        }
    }

    // Get the value of a specific register based on the provided DataType.
    public BitSet getRegisterValue(DataType DataType) {
        return switch (DataType.getType()) {
            case "PC" -> PC.getRegisterValue();
            case "GPR0" -> GPR0.getRegisterValue();
            case "GPR1" -> GPR1.getRegisterValue();
            case "GPR2" -> GPR2.getRegisterValue();
            case "GPR3" -> GPR3.getRegisterValue();
            case "CC" -> CC.getRegisterValue();
            case "IR" -> IR.getRegisterValue();
            case "MAR" -> MAR.getRegisterValue();
            case "IXR1" -> IXR1.getRegisterValue();
            case "IXR2" -> IXR2.getRegisterValue();
            case "IXR3" -> IXR3.getRegisterValue();
            case "MBR" -> MBR.getRegisterValue();
            case "MFR" -> MFR.getRegisterValue();
            case "HLT" -> HLT.getRegisterValue();
            default -> throw new IllegalStateException("Unexpected value: " + DataType.getType());
        };
    }

    // Set the Memory Fault Register (MFR) value based on a binary string.
    public void setRegisterMFRValue(String binaryString) {
        int value = Integer.parseInt(binaryString, 2);
        BitSet faultCode = new BitSet(DataType.MFR.getSize());
        faultCode.or(BitSet.valueOf(new long[]{value}));
        MFR.setRegisterValue(faultCode);
    }

    // Set the Halt Instruction (HLT) register value based on a binary string.
    public void setRegisterHLTValue(String binaryString) {
        int value = Integer.parseInt(binaryString, 2);
        BitSet faultCode = new BitSet(DataType.HLT.getSize());
        faultCode.or(BitSet.valueOf(new long[]{value}));
        HLT.setRegisterValue(faultCode);
    }

    // Reset all registers.
    public void resetAllRegisters() {
        BitSet clean16Bits = new BitSet(DataType.MBR.getSize());
        BitSet clean12Bits = new BitSet(DataType.MAR.getSize());
        BitSet clean4Bits = new BitSet(DataType.MFR.getSize());

        setRegisterValue(DataType.GPR0, clean16Bits);
        setRegisterValue(DataType.GPR1, clean16Bits);
        setRegisterValue(DataType.GPR2, clean16Bits);
        setRegisterValue(DataType.GPR3, clean16Bits);
        setRegisterValue(DataType.IXR1, clean16Bits);
        setRegisterValue(DataType.IXR2, clean16Bits);
        setRegisterValue(DataType.IXR3, clean16Bits);
        setRegisterValue(DataType.IR, clean16Bits);
        setRegisterValue(DataType.MBR, clean16Bits);
        setRegisterValue(DataType.MAR, clean12Bits);
        setRegisterValue(DataType.PC, clean12Bits);
        setRegisterValue(DataType.CC, clean4Bits);
        setRegisterMFRValue("0000");
        setRegisterHLTValue("0");
    }

    // Reset memory.
    public void resetMemory() {
        mainMemory.resetMemory();
    }

    // Check if the HLT register is set.
    public boolean checkHLT(BitSet currentHLT) {
        BitSet faultCode = new BitSet(DataType.HLT.getSize());
        faultCode.set(0);
        return currentHLT.equals(faultCode);
    }

    // Convert a BitSet representing a binary number to an integer.
    public int convertBinaryArrayToInt(BitSet binary) {
        int intValue = 0;
        for (int i = 0; i < binary.length(); i++) {
            if (binary.get(i))
                intValue += (1 << i);
        }
        return intValue;
    }


    // Helper to convert octal string to BitSet of specified size.
    public BitSet octalToBinaryArray(String octal, int bitSize) {
        String binaryString = new BigInteger(octal, 8).toString(2);
        int value = Integer.parseInt(binaryString, 2);
        BitSet binaryArray = new BitSet(bitSize);
        binaryArray.or(BitSet.valueOf(new long[]{value}));
        return binaryArray;
    }

    // Get the memory value at the specified row.
    public BitSet getMemoryValue(int row) {
        if (row < 6)
            return new BitSet(mainMemory.getWordSize()); // Returns a default 16-bit zeroed BitSet.

        if (row > mainMemory.getMemoryMaximumSize()) {
            setRegisterMFRValue("1000"); // Memory fault
            return new BitSet(mainMemory.getWordSize());
        }
        return mainMemory.getMemoryValue(row);
    }

    // Set the memory value at the specified row.
    public void setMemoryValue(int row, BitSet value) {
        if (row < 6) {
            setRegisterMFRValue("0001"); // Memory fault
            setRegisterHLTValue("1"); // Halt execution
            return;
        }
        mainMemory.setMemoryValue(row, value);
    }

    // Increment the Program Counter (PC).
    public void PCincrease() {
        BitSet pcBinaryArray = getRegisterValue(DataType.PC);
        int newPC = convertBinaryArrayToInt(pcBinaryArray) + 1;
        BitSet newPCBinaryArray = binaryArrayForProgramCounterAndMAR(Integer.toBinaryString(newPC));
        setRegisterValue(DataType.PC, newPCBinaryArray);
    }

    // Execute the current instruction.
    public void execute() {
        BitSet pcBinaryArray = getRegisterValue(DataType.PC);
        int pc = convertBinaryArrayToInt(pcBinaryArray);
        setRegisterValue(DataType.IR, getMemoryValue(pc));

        BitSet instructionBinaryArray = getMemoryValue(pc);
        CPUExecute instructionExecutionPackage = computeinstructionExecutionPackage(instructionBinaryArray);
        executeInstruction(instructionExecutionPackage);

        if (!checkHLT(getRegisterValue(DataType.HLT)))
            PCincrease();
    }


    // Helper method to convert a binary string to a BitSet for PC and MAR (12-bit).
    public BitSet binaryArrayForProgramCounterAndMAR(String binaryString) {
        return intToBinaryArray(binaryString, DataType.PC.getSize());
    }

    // Helper method to convert a binary string to a BitSet for other registers (16-bit).
    public BitSet intToBinaryArrayForOther(String binaryString) {
        return intToBinaryArray(binaryString, DataType.MBR.getSize());
    }

    // Helper to convert a binary string to BitSet of specified size.
    public BitSet intToBinaryArray(String binaryString, int bitSize) {
        int value = Integer.parseInt(binaryString, 2);
        BitSet binaryArray = new BitSet(bitSize);
        binaryArray.or(BitSet.valueOf(new long[]{value}));
        return binaryArray;
    }

    // Helper to convert octal string to BitSet for PC and MAR (12-bit).
    public BitSet octalToBinaryArrayForPCMAR(String octal) {
        return octalToBinaryArray(octal, DataType.PC.getSize());
    }
    // Converts an octal string to its integer equivalent.
    public int octalToInt(String octal) {
        return Integer.parseInt(octal, 8);
    }

    // Helper to convert octal string to BitSet for other registers (16-bit).
    public BitSet octalToBinaryArrayForOther(String octal) {
        return octalToBinaryArray(octal, DataType.MBR.getSize());
    }
    // Compute the effective address and other components of an instruction.
    public CPUExecute computeinstructionExecutionPackage(BitSet instructionBinaryArray) {
        BitSet opcodeBinary = bitsetGetRange(instructionBinaryArray, 15, 10);
        String instructionString = decodeOPCode(opcodeBinary);

        int R = (instructionBinaryArray.get(9) ? 2 : 0) + (instructionBinaryArray.get(8) ? 1 : 0);
        int IX = (instructionBinaryArray.get(7) ? 2 : 0) + (instructionBinaryArray.get(6) ? 1 : 0);
        int I = instructionBinaryArray.get(5) ? 1 : 0;
        BitSet addressFieldBinaryArray = bitsetGetRange(instructionBinaryArray, 4, 0);
        int intAddress = convertBinaryArrayToInt(addressFieldBinaryArray);

        int EA = computeEffectiveAddress(I, IX, intAddress);

        instructionString = isHLTInstruction(EA, I, R, IX, intAddress, instructionString) ? "HLT" : instructionString;
        instructionString = isDataInstruction(EA, I, R, IX, intAddress, instructionString) ? "Data" : instructionString;

        return new CPUExecute(EA, I, R, IX, intAddress, instructionString);
    }

    // Compute the effective address based on addressing modes.
    private int computeEffectiveAddress(int I, int IX, int intAddress) {
        int EA;
        if (I == 0) {
            if (IX == 0) {
                EA = intAddress;
            } else {
                int IXValue = switch (IX) {
                    case 1 -> convertBinaryArrayToInt(getRegisterValue(DataType.IXR1));
                    case 2 -> convertBinaryArrayToInt(getRegisterValue(DataType.IXR2));
                    default -> convertBinaryArrayToInt(getRegisterValue(DataType.IXR3));
                };
                EA = intAddress + IXValue;
            }
        } else {
            if (IX == 0) {
                int row = intAddress;
                EA = convertBinaryArrayToInt(getMemoryValue(convertBinaryArrayToInt(getMemoryValue(row))));
            } else {
                int IXValue = switch (IX) {
                    case 1 -> convertBinaryArrayToInt(getRegisterValue(DataType.IXR1));
                    case 2 -> convertBinaryArrayToInt(getRegisterValue(DataType.IXR2));
                    default -> convertBinaryArrayToInt(getRegisterValue(DataType.IXR3));
                };
                EA = convertBinaryArrayToInt(getMemoryValue(intAddress + IXValue));
            }
        }
        return EA;
    }

    // Execute the specified instruction.
    private void executeInstruction(CPUExecute instructionExecutionPackage) {
        String instruction = instructionExecutionPackage.getInstructionString();
        switch (instruction) {
            case "LDR" -> executeLDR(instructionExecutionPackage);
            case "STR" -> executeSTR(instructionExecutionPackage);
            case "LDA" -> executeLDA(instructionExecutionPackage);
            case "LDX" -> executeLDX(instructionExecutionPackage);
            case "STX" -> executeSTX(instructionExecutionPackage);
            case "HLT" -> executeHLT();
        }
    }

    // LDR instruction implementation.
    private void executeLDR(CPUExecute instructionExecutionPackage) {
        int effectiveAddress = instructionExecutionPackage.getEffectiveAddress();
        int R = instructionExecutionPackage.getR();

        String effectiveAddressBinaryArray = instructionExecutionPackage.getEffectiveAddressBinaryArray();
        setRegisterValue(DataType.MAR, binaryArrayForProgramCounterAndMAR(effectiveAddressBinaryArray));

        setRegisterValue(DataType.MBR, getMemoryValue(effectiveAddress));

        BitSet registerValue = getRegisterValue(DataType.MBR);
        switch (R) {
            case 0 -> setRegisterValue(DataType.GPR0, registerValue);
            case 1 -> setRegisterValue(DataType.GPR1, registerValue);
            case 2 -> setRegisterValue(DataType.GPR2, registerValue);
            default -> setRegisterValue(DataType.GPR3, registerValue);
        }
    }

    // STR instruction implementation.
    private void executeSTR(CPUExecute instructionExecutionPackage) {
        int effectiveAddress = instructionExecutionPackage.getEffectiveAddress();
        int R = instructionExecutionPackage.getR();

        String effectiveAddressBinaryArray = instructionExecutionPackage.getEffectiveAddressBinaryArray();
        setRegisterValue(DataType.MAR, binaryArrayForProgramCounterAndMAR(effectiveAddressBinaryArray));

        BitSet registerValue = switch (R) {
            case 0 -> getRegisterValue(DataType.GPR0);
            case 1 -> getRegisterValue(DataType.GPR1);
            case 2 -> getRegisterValue(DataType.GPR2);
            default -> getRegisterValue(DataType.GPR3);
        };
        setRegisterValue(DataType.MBR, registerValue);
        setMemoryValue(effectiveAddress, getRegisterValue(DataType.MBR));
    }

    // LDA instruction implementation.
    private void executeLDA(CPUExecute instructionExecutionPackage) {
        int effectiveAddress = instructionExecutionPackage.getEffectiveAddress();
        int R = instructionExecutionPackage.getR();

        String effectiveAddressBinaryArray = instructionExecutionPackage.getEffectiveAddressBinaryArray();
        setRegisterValue(DataType.MAR, binaryArrayForProgramCounterAndMAR(effectiveAddressBinaryArray));

        BitSet registerValue = intToBinaryArrayForOther(effectiveAddressBinaryArray);
        switch (R) {
            case 0 -> setRegisterValue(DataType.GPR0, registerValue);
            case 1 -> setRegisterValue(DataType.GPR1, registerValue);
            case 2 -> setRegisterValue(DataType.GPR2, registerValue);
            default -> setRegisterValue(DataType.GPR3, registerValue);
        }
    }

    // LDX instruction implementation.
    private void executeLDX(CPUExecute instructionExecutionPackage) {
        int effectiveAddress = instructionExecutionPackage.getEffectiveAddress();
        int IX = instructionExecutionPackage.getIX();

        String effectiveAddressBinaryArray = instructionExecutionPackage.getEffectiveAddressBinaryArray();
        setRegisterValue(DataType.MAR, binaryArrayForProgramCounterAndMAR(effectiveAddressBinaryArray));

        setRegisterValue(DataType.MBR, getMemoryValue(effectiveAddress));

        BitSet registerValue = getRegisterValue(DataType.MBR);
        switch (IX) {
            case 1 -> setRegisterValue(DataType.IXR1, registerValue);
            case 2 -> setRegisterValue(DataType.IXR2, registerValue);
            case 3 -> setRegisterValue(DataType.IXR3, registerValue);
        }
    }

    // STX instruction implementation.
    private void executeSTX(CPUExecute instructionExecutionPackage) {
        int effectiveAddress = instructionExecutionPackage.getEffectiveAddress();
        int IX = instructionExecutionPackage.getIX();

        String effectiveAddressBinaryArray = instructionExecutionPackage.getEffectiveAddressBinaryArray();
        setRegisterValue(DataType.MAR, binaryArrayForProgramCounterAndMAR(effectiveAddressBinaryArray));

        BitSet registerValue = switch (IX) {
            case 1 -> getRegisterValue(DataType.IXR1);
            case 2 -> getRegisterValue(DataType.IXR2);
            case 3 -> getRegisterValue(DataType.IXR3);
            default -> new BitSet(16);
        };
        setRegisterValue(DataType.MBR, registerValue);
        setMemoryValue(effectiveAddress, getRegisterValue(DataType.MBR));
    }

    // Decodes the opcode from a BitSet into its corresponding instruction string.
    private String decodeOPCode(BitSet opcodeBinary) {
        StringBuilder binaryString = new StringBuilder();
        for (int i = 5; i >= 0; i--) {
            binaryString.append(opcodeBinary.get(i) ? '1' : '0');
        }
        return findInstructionByOpcode(binaryString.toString());
    }
    public String findInstructionByOpcode(String opcodeBinary) {
        String opcodeOctal = !opcodeBinary.isEmpty() ? bin2Octal(opcodeBinary) : "";
        opcodeOctal = opcodeOctal.length() == 1 ? "0" + opcodeOctal : opcodeOctal;

        return switch (opcodeOctal) {
            case "01" -> "LDR";
            case "02" -> "STR";
            case "03" -> "LDA";
            case "41" -> "LDX";
            case "42" -> "STX";
            case "10" -> "JZ";
            case "11" -> "JNE";
            case "12" -> "JCC";
            case "13" -> "JMA";
            case "14" -> "JSR";
            case "15" -> "RFS";
            case "16" -> "SOB";
            case "17" -> "JGE";
            case "04" -> "AMR";
            case "05" -> "SMR";
            case "06" -> "AIR";
            case "07" -> "SIR";
            case "70" -> "MLT";
            case "71" -> "DVD";
            case "72" -> "TRR";
            case "73" -> "AND";
            case "74" -> "ORR";
            case "75" -> "NOT";
            case "31" -> "SRC";
            case "32" -> "RRC";
            case "61" -> "IN";
            case "62" -> "OUT";
            case "63" -> "CHK";
            case "30" -> "TRAP";
            case "00" -> "Data";
            default -> "";
        };
    }
    private String bin2Octal(String binary) {
        return new BigInteger(binary, 2).toString(8);
    }

    // Check if the instruction is a halt instruction.
    private boolean isHLTInstruction(int EA, int I, int R, int IX, int intAddress, String instructionString) {
        return (EA == 0 && I == 0 && R == 0 && IX == 0 && intAddress == 0 && instructionString.equals("Data"));
    }

    // Check if the instruction is a data instruction.
    private boolean isDataInstruction(int EA, int I, int R, int IX, int intAddress, String instructionString) {
        return (EA == 0 && I == 0 && R == 0 && IX == 0 && intAddress == 0 && instructionString.equals("LDR"));
    }

    // Get a range of bits from a BitSet.
    private BitSet bitsetGetRange(BitSet inputBitSet, int from, int to) {
        int end = from - to;
        BitSet bitset = new BitSet(end + 1);
        for (int i = 0; i <= end; i++) {
            if (inputBitSet.get(from - i)) {
                bitset.set(end - i);  // Set the bit in the result at position i
            }
        }
        return bitset;
    }
    // HLT instruction implementation.
    private void executeHLT() {
        setRegisterHLTValue("1");
    }
}
