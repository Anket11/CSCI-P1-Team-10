// The CPUExecute class combines the data required for execution in the CPU class.
// It provides getter methods to access these fields and allows modification
// of the instruction string when needed (designed for HLT and Data instructions).
public class CPUExecute {
    // Fields to store instruction-related data
    private final int effectiveAddress;
    private final int I;
    private final int R;
    private final int IX;
    private final int address;
    // Stores the current instruction
    private String instructionString;
    // Constructor to initialize the CPUExecute object with relevant fields
    public CPUExecute(int effectiveAddress, int I, int R, int IX, int address, String instructionString) {
        this.effectiveAddress = effectiveAddress;
        this.I = I;
        this.R = R;
        this.IX = IX;
        this.address = address;
        this.instructionString = instructionString;
    }
    // Getter methods to access individual fields
    public int getEffectiveAddress() {
        return effectiveAddress;
    }
    public int getI() {
        return I;
    }

    // Getter and setter for instruction string
    public String getInstructionString() {
        return instructionString;
    }
    public int getR() {
        return R;
    }
    public int getIX() {
        return IX;
    }
    public int getAddress() {
        return address;
    }
    // Converts the effective address to a binary string
    public String getEffectiveAddressBinaryArray() {
        return Integer.toBinaryString(effectiveAddress);
    }
    public void setInstructionString(String instructionString) {
        this.instructionString = instructionString;
    }
}
