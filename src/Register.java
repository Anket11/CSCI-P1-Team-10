import java.util.BitSet;

// The Register class simulates a CPU register with a fixed size.
// It lets you store and retrieve binary values using a BitSet.
// You can set the value, get the current value, and check the register size.

public class Register
{

    private BitSet registerValue;
    private int registerSize;

//    Initializes the register with the required size
    public BitSet getRegisterValue()
{
    return (BitSet) registerValue.clone();
}
    public Register(int size)
    {
        try
        {
            if (size <= 0)
                throw new IllegalArgumentException("Error -- Must be greater than 0");

            this.registerSize = size;
            this.registerValue = new BitSet(this.registerSize);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    //    Returns a copy of the current register value


    // Set register value
    public void setRegisterValue(BitSet newValue)
    {
        try
        {
            if (registerSize == 0)
                throw new IllegalStateException("Error -- Size is zero!");

            if (newValue.length() > registerSize){

                throw new IllegalArgumentException(newValue + " Error -- Value size exceeds");
            }

            this.registerValue = (BitSet) newValue.clone();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

}
