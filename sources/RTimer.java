import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RTimer implements ActionListener{
    private R73SSA r73;
    
    public RTimer(R73SSA r73s) {
        r73 = r73s;
    }
    public void actionPerformed(ActionEvent e) {
        r73.setThreadProgress();
        r73.rg.resetGraphics();
//        System.out.println("tick-tack");
    }
}