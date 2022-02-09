package linechartproject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

public class LineChartPanel extends javax.swing.JPanel {
    
    private int offset_border = 20;
    
    private Point x_pos = new Point();
    private Point y_pos = new Point();
    private Point zero_pos = new Point();
    
    
    public LineChartPanel() {
        
        initComponents();
    }
    
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        
        //Positionen berechnen:
        Dimension d = this.getSize();
        
        x_pos.x = d.width-offset_border;
        x_pos.y = d.height-offset_border;
        
        y_pos.x = offset_border;
        y_pos.y = offset_border;
        
        zero_pos.x = offset_border;
        zero_pos.y = d.height-offset_border;
        
        //Punkte Anzeigen:
        g.setColor(Color.red);
        g.drawOval(zero_pos.x-3, zero_pos.y-3, 6, 6);
        g.setColor(Color.green);
        g.drawOval(x_pos.x-3, x_pos.y-3, 6, 6);
        g.setColor(Color.blue);
        g.drawOval(y_pos.x-3, y_pos.y-3, 6, 6);
        
        //Linien anzeigen:
        g.setColor(Color.white);
        
        g.drawLine(zero_pos.x, zero_pos.y, x_pos.x, x_pos.y);
        g.drawLine(zero_pos.x, zero_pos.y, y_pos.x, y_pos.y);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
