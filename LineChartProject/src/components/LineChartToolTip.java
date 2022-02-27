package components;

import com.formdev.flatlaf.ui.FlatBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class LineChartToolTip extends JPanel {
    
    private JLabel labelText;
    private JLabel labelValue;
    
    public LineChartToolTip() {
        
        this.setLayout(new BorderLayout());
        this.setBorder(new FlatBorder());
        this.setOpaque(true);
        
        //Transperent:
        this.setBackground(new Color(50, 50, 50, 200));
        
        labelText = new JLabel();
        labelText.setBorder(new EmptyBorder(5, 5, 0, 5));
        labelText.setIcon(new ImageIcon(getClass().getResource("/images/today_18.png")));
        
        labelValue = new JLabel();
        labelValue.setBorder(new EmptyBorder(0, 5, 5, 5));
        labelValue.setIcon(new ImageIcon(getClass().getResource("/images/euro_18.png")));
        
        this.add(labelText, BorderLayout.CENTER);
        this.add(labelValue, BorderLayout.PAGE_END);
    }
    
    public void setText(String sText, String sValue) {
        labelText.setText(sText);
        labelValue.setText(sValue);
    }
    
}
