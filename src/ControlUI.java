import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;

public class ControlUI
{
    static CPU cpu = new CPU();

    public static void loadGPR0Click(BitSet value) {
        cpu.setRegisterValue(DataType.GPR0, value);
    }

    public static BitSet getGPR0(){
        return cpu.getRegisterValue(DataType.GPR0);
    }

    public static void loadGPR1Click(BitSet value) {
        cpu.setRegisterValue(DataType.GPR1, value);
    }

    public static BitSet getGPR1(){
        return cpu.getRegisterValue(DataType.GPR1);
    }

    public static void loadGPR2Clicked(BitSet value) {
        cpu.setRegisterValue(DataType.GPR2, value);
    }

    public static BitSet getGPR2(){
        return cpu.getRegisterValue(DataType.GPR2);
    }

    public static void loadGPR3Clicked(BitSet value) {
        cpu.setRegisterValue(DataType.GPR3, value);
    }

    public static BitSet getGPR3(){
        return cpu.getRegisterValue(DataType.GPR3);
    }

    public static void whenIXR1LoadClicked(BitSet value) {
        cpu.setRegisterValue(DataType.IXR1, value);
    }

    public static BitSet getIXR1(){
        return cpu.getRegisterValue(DataType.IXR1);
    }

    public static void whenIXR2LoadClicked(BitSet value) {
        cpu.setRegisterValue(DataType.IXR2, value);
    }

    public static BitSet getIXR2(){
        return cpu.getRegisterValue(DataType.IXR2);
    }

    public static void whenIXR3LoadClicked(BitSet value) {
        cpu.setRegisterValue(DataType.IXR3, value);
    }

    public static BitSet getIXR3(){
        return cpu.getRegisterValue(DataType.IXR3);
    }

    public static void whenPCLoadClicked(BitSet value) {
        cpu.setRegisterValue(DataType.PC, value);
        cpu.setRegisterValue(DataType.IR, cpu.getMemoryValue(cpu.convertBinaryArrayToInt(getPC())));
    }

    public static BitSet getPC(){
        return cpu.getRegisterValue(DataType.PC);
    }

    public static void whenMARLoadClicked(BitSet value) {
        cpu.setRegisterValue(DataType.MAR, value);
    }

    public static BitSet getMAR(){
        return cpu.getRegisterValue(DataType.MAR);
    }

    public static void whenMBRLoadClicked(BitSet value) {
        cpu.setRegisterValue(DataType.MBR, value);
    }

    public static BitSet getMBR(){
        return cpu.getRegisterValue(DataType.MBR);
    }

    public static BitSet getIR(){
        return cpu.getRegisterValue(DataType.IR);
    }

    public static BitSet getCC(){
        return cpu.getRegisterValue(DataType.CC);
    }

    public static BitSet getMFR(){
        return cpu.getRegisterValue(DataType.MFR);
    }

    public static BitSet getHLT(){
        return cpu.getRegisterValue(DataType.HLT);
    }

    public static void printAllRegisters(){

        System.out.println("GPR 0: "+ convertBitSetToBinaryString(getGPR0(), DataType.GPR0.getSize()));
        System.out.println("GPR 1: "+ convertBitSetToBinaryString(getGPR1(), DataType.GPR1.getSize()));
        System.out.println("GPR 2: "+ convertBitSetToBinaryString(getGPR2(), DataType.GPR2.getSize()));
        System.out.println("GPR 3: "+ convertBitSetToBinaryString(getGPR3(), DataType.GPR3.getSize()));

        System.out.println("IXR 1: "+ convertBitSetToBinaryString(getIXR1(), DataType.IXR1.getSize()));
        System.out.println("IXR 2: "+ convertBitSetToBinaryString(getIXR2(), DataType.IXR2.getSize()));
        System.out.println("IXR 3: "+ convertBitSetToBinaryString(getIXR3(), DataType.IXR3.getSize()));

        System.out.println("PC: "+ convertBitSetToBinaryString(getPC(), DataType.PC.getSize()));
        System.out.println("MAR: "+ convertBitSetToBinaryString(getMAR(), DataType.MAR.getSize()));
        System.out.println("MBR: "+ convertBitSetToBinaryString(getMBR(), DataType.MBR.getSize()));

        System.out.println("IR: "+ convertBitSetToBinaryString(getIR(), DataType.IR.getSize()));
        System.out.println("CC: "+ convertBitSetToBinaryString(getCC(), DataType.CC.getSize()));
        System.out.println("MFR: "+ convertBitSetToBinaryString(getMFR(), DataType.MFR.getSize()));
        System.out.println("HLT: "+ convertBitSetToBinaryString(getHLT(), DataType.HLT.getSize()));



    }

    static String convertBitSetToBinaryString(BitSet bitSet, int length) {
        StringBuilder binaryString = new StringBuilder(length);

        length = length -1;

        for (int i = length; i >= 0; i--) {
            if (bitSet.get(i)) {
                binaryString.append('1');
            } else {
                binaryString.append('0');
            }
        }
        return binaryString.toString();
    }


    public static int getCurrentMARValue()
    {
        BitSet currentMAR = cpu.getRegisterValue(DataType.MAR);

        return Integer.parseInt(convertBitSetToBinaryString(currentMAR, DataType.MAR.getSize()),2);
    }

    public static void incrementMAR(int currentMARValue)
    {
        BitSet newMAR = cpu.binaryArrayForProgramCounterAndMAR(Integer.toBinaryString(currentMARValue + 1));
        cpu.setRegisterValue(DataType.MAR, newMAR);
    }

    public static void LoadIsClicked()
    {
        cpu.setRegisterValue(DataType.MBR, cpu.getMemoryValue(getCurrentMARValue()));
    }

    public static void LoadPlusIsClicked()
    {
        int currentMARValue = getCurrentMARValue();
        cpu.setRegisterValue(DataType.MBR, cpu.getMemoryValue(currentMARValue));
        incrementMAR(currentMARValue);
    }

    public static void whenStoreClicked()
    {
        System.out.println("Current MAR Value"+getCurrentMARValue());
        cpu.setMemoryValue(getCurrentMARValue(), cpu.getRegisterValue(DataType.MBR));
    }

    public static void whenStorePlusClicked() {
        int currentMARValue = getCurrentMARValue();
        cpu.setMemoryValue(currentMARValue, cpu.getRegisterValue(DataType.MBR));
        incrementMAR(currentMARValue);
    }

    public static void whenRunClicked()
    {
        int MemorySize = 2048;
        //TODO refer to a program-wide memory size variable

        for(int i=0; i< MemorySize; i++)
            cpu.execute();
    }

    public static void whenStepClicked()
    {
        cpu.execute();

        cpu.setRegisterValue(DataType.IR, cpu.getMemoryValue(cpu.convertBinaryArrayToInt(getPC())));

    }

    public static void whenHaltClicked()
    {
        if(!cpu.checkHLT(cpu.getRegisterValue(DataType.HLT))) {
            System.out.println("program not previously halted");
            cpu.setRegisterHLTValue("1");
        }
        else cpu.setRegisterHLTValue("0");
    }

    public static void whenClearClicked(){
        cpu.resetAllRegisters();
        cpu.resetMemory();
    }

    public static void whenIPLClick(String inputFilePath)
    {
        try
        {

            Path path = Paths.get(inputFilePath);

            StringBuilder fileContentLines = new StringBuilder();
            Files.readAllLines(path).forEach(line -> {
                fileContentLines.append(line).append(System.lineSeparator());
            });

            String[] octalLines = fileContentLines.toString().split(System.lineSeparator());

            int MEMORY_SIZE = 2048;
            if (octalLines.length < MEMORY_SIZE)
            {
                boolean first = true;

                for (String str : octalLines)
                {
                    String[] octals = str.split(" ");

                    BitSet rowBinaryArray = cpu.octalToBinaryArrayForPCMAR(octals[0]);

                    BitSet valueBinaryArray = cpu.octalToBinaryArrayForOther(octals[1]);

                    if(first) {
                        cpu.setRegisterValue(DataType.PC, rowBinaryArray);

                        cpu.setRegisterValue(DataType.IR, valueBinaryArray);

                        first = false;

                    }

                    int row = cpu.octalToInt(octals[0]);
                    cpu.setMemoryValue(row, valueBinaryArray);
                }
            }


        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }

}
