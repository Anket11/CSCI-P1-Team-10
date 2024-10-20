import javax.swing.JFrame;

public class Main
{
    public static void main(String[] args)
    {
        //Create UI
        JFrame frame = new JFrame("Team 10 Machine Simulator");
        frame.add(new MainUI());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 680);
        frame.setVisible(true);

        System.out.println("Started");
    }
}