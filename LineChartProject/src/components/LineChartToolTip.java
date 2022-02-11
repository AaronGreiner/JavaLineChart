package components;

import com.formdev.flatlaf.ui.FlatBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicBorders;

public class LineChartToolTip extends JPanel {
    
    private JLabel label;
    
    public LineChartToolTip(String s) {
        
        this.setLayout(new BorderLayout());
        this.setBorder(new FlatBorder());
        
        label = new JLabel(s);
        label.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        this.add(label, BorderLayout.CENTER);
        
        this.repaint();
    }
    
    public void setText(String s) {
        label.setText(s);
    }
    
}
