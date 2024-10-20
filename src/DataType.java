// The DataType class defines various types of registers used in this project.
// Each instance of DataType represents a specific register with its corresponding
// name (type) and size (in bits). This class centralizes register names and sizes
// to prevent typographical errors and ensure consistency throughout the project.
public class DataType {

    private final String type; // Name of the register
    private final int size;    // Size of the register in bits

    // Private constructor to prevent external instantiation
    private DataType(String type, int size) {
        this.type = type;
        this.size = size;
    }

    // Static instances representing different registers
    public static final DataType GPR0 = new DataType("GPR0", 16);
    public static final DataType GPR1 = new DataType("GPR1", 16);
    public static final DataType GPR2 = new DataType("GPR2", 16);
    public static final DataType GPR3 = new DataType("GPR3", 16);
    public static final DataType IXR1 = new DataType("IXR1", 16);
    public static final DataType IXR2 = new DataType("IXR2", 16);
    public static final DataType IXR3 = new DataType("IXR3", 16);
    public static final DataType IR = new DataType("IR", 16);
    public static final DataType MBR = new DataType("MBR", 16);
    public static final DataType MAR = new DataType("MAR", 12);
    public static final DataType PC = new DataType("PC", 12);
    public static final DataType MFR = new DataType("MFR", 4);
    public static final DataType CC = new DataType("CC", 4);
    public static final DataType HLT = new DataType("HLT", 1);

    // Getter for the register type name
    public String getType() {
        return type;
    }

    // Getter for the register size in bits
    public int getSize() {
        return size;
    }
}
