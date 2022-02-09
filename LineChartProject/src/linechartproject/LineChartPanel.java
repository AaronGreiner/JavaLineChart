package linechartproject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class LineChartPanel extends javax.swing.JPanel {

    private int offset_border = 20;
    private Font font = new Font("Segoe UI", Font.PLAIN, 12);
    
    private Point x_pos = new Point();
    private Point y_pos = new Point();
    private Point zero_pos = new Point();

    private ArrayList<String> x_listString = new ArrayList<String>();
    private ArrayList<Integer> x_listValue = new ArrayList<Integer>();
    
    private int x_numElements = 1000;
    private int y_numElements = 20;
    
    public LineChartPanel() {
        
        //Daten Zufällig generieren:
        for (int i = 0; i < x_numElements; i++) {
            x_listString.add(LocalDate.now().plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            x_listValue.add((int)(Math.random()*1000));
        }
        
        initComponents();
    }

    protected void paintComponent(Graphics g) {
        
        Graphics2D g2 = (Graphics2D) g; //wird für Font-Antialiasing benötigt
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //Antialiasing
        
        //Positionen berechnen:
        Dimension d = this.getSize();
        
        x_pos.x = d.width - offset_border;
        x_pos.y = d.height - offset_border;
        
        y_pos.x = offset_border;
        y_pos.y = offset_border;
        
        zero_pos.x = offset_border;
        zero_pos.y = d.height - offset_border;
        
        //Punkte Anzeigen:
        g2.setColor(Color.red);
        g2.drawOval(zero_pos.x - 3, zero_pos.y - 3, 6, 6);
        g2.setColor(Color.green);
        g2.drawOval(x_pos.x - 3, x_pos.y - 3, 6, 6);
        g2.setColor(Color.blue);
        g2.drawOval(y_pos.x - 3, y_pos.y - 3, 6, 6);
        
        //Linien anzeigen:
        g2.setColor(Color.white);
        
        g2.drawLine(zero_pos.x, zero_pos.y, x_pos.x, x_pos.y);
        g2.drawLine(zero_pos.x, zero_pos.y, y_pos.x, y_pos.y);
        
        //X-Achse Einteilung:
        int x_length = x_pos.x - zero_pos.x;
        System.out.println("X Length: " + x_length);
        int x_ElementDist = x_length / x_numElements;
        System.out.println("X Element Dist: " + x_ElementDist);
        
        for (int i = 0; i < x_numElements; i++) {
            
            g2.drawOval((zero_pos.x + (x_ElementDist * i) ) - 3, x_pos.y - 3, 6, 6); //Wenn i = 0 kann noch abgefangen werden
            
        }
        
        //Y-Achse Einteilung:
        int y_length = zero_pos.y - y_pos.y;
        System.out.println("Y Length: " + y_length);
        int y_ElementDist = y_length / y_numElements;
        System.out.println("Y Element Dist: " + y_ElementDist);
        
        for (int i = 0; i < y_numElements; i++) {
            
            g2.drawOval(y_pos.x - 3, (zero_pos.y - (y_ElementDist * i) ) - 3, 6, 6); //Wenn i = 0 kann noch abgefangen werden
            
        }
        
        //Daten anzeigen:
        g2.setColor(Color.white);
        Point pointTemp = new Point();
        Point pointTemp2 = new Point();
        int max_value = Collections.max(x_listValue);
        System.out.println("MAX_VALUE: " + max_value);
        double temp = Double.valueOf(y_length) / Double.valueOf(max_value);
        System.out.println("TEMP: " + temp);
        
        for (int i = 0; i < x_numElements; i++) {
            
            pointTemp.y = (int)(zero_pos.y - x_listValue.get(i) * temp);
            pointTemp.x = zero_pos.x + (x_ElementDist * i);
            
            g2.fillOval(pointTemp.x - 3, pointTemp.y - 3, 6, 6);
            
            if (i != 0) {
                g2.drawLine(pointTemp2.x, pointTemp2.y, pointTemp.x, pointTemp.y);
            }
            
            pointTemp2.x = pointTemp.x;
            pointTemp2.y = pointTemp.y;
            
        }
        
        //Test Text:
        g2.setColor(Color.gray);
        g2.setFont(font);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB); //Antialiasing
        g2.drawString("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz 0123456789", 100, 100);
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
