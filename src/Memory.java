import java.util.BitSet;

// The Memory class simulates memory with BitSet words for data storage and retrieval.

public class Memory {

    // addresses in memory
    private static final int MEMORY_SIZE = 2048;

    // bits per word in memory
    private static final int BITS_PER_WORD = 16;

    private BitSet[] memoryArray;
    // Sets the memory value
    public void setMemoryValue(int address, BitSet value) {
        if (isAddressValid(address)) {
            if (value.length() > BITS_PER_WORD) {
                throw new IllegalArgumentException("BitSet size exceeds the allowed word size of " + BITS_PER_WORD + " bits.");
            }
            initializeRow(address);
            memoryArray[address].clear();
            memoryArray[address].or(value); // Copy new value into memory
        } else {
            throw new IllegalArgumentException("Address out of bounds: " + address);
        }
    }

    // Resets the memory
    public void resetMemory() {
        memoryArray = new BitSet[MEMORY_SIZE];
    }

    // Get the maximum
    public int getMemoryMaximumSize() {
        return MEMORY_SIZE;
    }

    public Memory() {
        memoryArray = new BitSet[MEMORY_SIZE];
    }

    // Get the memory value
    public BitSet getMemoryValue(int address) {
        if (isAddressValid(address)) {
            initializeRow(address);
            return (BitSet) memoryArray[address].clone(); // Return a clone to prevent modification
        } else {
            throw new IllegalArgumentException("Address out of bounds: " + address);
        }
    }

    private boolean isAddressValid(int address) {
        return address >= 0 && address < MEMORY_SIZE;
    }
    // Get the word size
    public int getWordSize() {
        return BITS_PER_WORD;
    }

    private void initializeRow(int address) {
        if (memoryArray[address] == null) {
            memoryArray[address] = new BitSet(BITS_PER_WORD);
        }
    }


}
