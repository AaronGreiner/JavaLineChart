package components;

import com.formdev.flatlaf.ui.FlatBorder;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class LineChartToolTip extends JPanel {
    
    private JLabel label;
    
    public LineChartToolTip() {
        
        this.setLayout(new BorderLayout());
        this.setBorder(new FlatBorder());
        
        label = new JLabel();
        label.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        this.add(label, BorderLayout.CENTER);
    }
    
    public void setText(String s) {
        label.setText(s);
    }
    
}
