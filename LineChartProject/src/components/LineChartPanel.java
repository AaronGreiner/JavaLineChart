package components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.CubicCurve2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JPanel;

public class LineChartPanel extends JPanel {
    
    //Einstellungen für Nutzer: (Später mit Setter bearbeitbar)
    private LineType line_type = LineType.CURVED;
    private ToolTipPosition tip_pos = ToolTipPosition.CURSOR;
    
    private boolean debug_mode = false;
    private boolean paint_x_achse = true;
    private boolean paint_y_achse = true;
    private boolean paint_line_marks = false;
    private boolean paint_dot = false;
    private boolean show_tooltip = true;
    
    private int offset_border = 10;
    private int offset_tip_x = 10;
    private int offset_tip_y = 0;
    private int size_line_marks = 2;
    
    private float size_curve_muliplicator = 1.0f;
    private float size_stroke = 2.0f;
    
    private Color color_primary = Color.white;
    private Color color_secondary = Color.LIGHT_GRAY;
    private Color color_highlight = Color.cyan;
    
    //Nicht durch Nutzer bearbeiten:
    private boolean check_highlight_close_point = false;
    
    private Point pos_x = new Point();
    private Point pos_y = new Point();
    private Point pos_zero = new Point();
    private Point pos_mouse = new Point();
    private Point pos_tip = new Point();
    
    private ArrayList<String> listString = new ArrayList<String>();
    private ArrayList<Integer> listValue = new ArrayList<Integer>();
    private ArrayList<Point> listPoints = new ArrayList<Point>();
    
    private int x_numElements = 100; //Eigentlich überflüssig
    private int y_numElements = 10; //Eigentlich überflüssig
    private int size_curve = 1;
    private int current_index = 0;
    
    private float mouse_dist;
    
    private LineChartToolTip tip = new LineChartToolTip();
    
    public LineChartPanel() {
        
        loadDebugData();
        
        init();
    }
    
    private void init() {
        
        setMaximumSize(new Dimension(200, 100));
        setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 0, 0)));
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent evt) {
                chartMouseMoved(evt);
            }
            @Override
            public void mouseDragged(MouseEvent evt) {
                chartMouseMoved(evt);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                chartMouseEntered(evt);
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                chartMouseExited(evt);
            }
        });
    }
    
    protected void paintComponent(Graphics g) {
        
        //Zurücksetzen vom vorherigen Paint:
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //Antialiasing
        g2.setColor(color_secondary);
        
        //Wenn keine Elemente gefüllt, dann nichts malen:
        if (listValue.size() <= 0) {
            return;
        }
        
        listPoints.clear();
        
        //Positionen berechnen:
        Dimension d = this.getSize();
        
        pos_x.x = d.width - offset_border;
        pos_x.y = d.height - offset_border;
        
        pos_y.x = offset_border;
        pos_y.y = offset_border;
        
        pos_zero.x = offset_border;
        pos_zero.y = d.height - offset_border;
        
        //Punkte Anzeigen:
        if (paint_line_marks) {
            g2.fillOval(pos_zero.x - 3, pos_zero.y - 3, 7, 7);
            g2.fillOval(pos_x.x - 3, pos_x.y - 3, 7, 7);
            g2.fillOval(pos_y.x - 3, pos_y.y - 3, 7, 7);
        }
        
        //Linien anzeigen:
        if (paint_x_achse) {
            g2.drawLine(pos_zero.x, pos_zero.y, pos_x.x, pos_x.y); //X Achse
        }
        if (paint_y_achse) {
            g2.drawLine(pos_zero.x, pos_zero.y, pos_y.x, pos_y.y); //Y Achse
        }
        
        //X-Achse Einteilung:
        int x_length = pos_x.x - pos_zero.x;
        double x_ElementDist = (double)x_length / x_numElements;
        
        if (paint_line_marks) {
            for (int i = 0; i < x_numElements; i++) {
                if (i != 0) {
                    
                    int pos = (int)((pos_zero.x + Math.round(x_ElementDist * i)));
                    int x1 = pos;
                    int y1 = pos_x.y + size_line_marks;
                    int x2 = pos;
                    int y2 = pos_x.y - size_line_marks;
                    
                    g2.drawLine(x1, y1, x2, y2);
                }
            }
        }
        
        //Y-Achse Einteilung:
        int y_length = pos_zero.y - pos_y.y;
        double y_ElementDist = (double)y_length / y_numElements;
        
        if (paint_line_marks) {
            for (int i = 0; i < y_numElements; i++) {
                if (i != 0) {
                    int pos = (int)((pos_zero.y - Math.round(y_ElementDist * i)));
                    int x1 = pos_y.x + size_line_marks;
                    int y1 = pos;
                    int x2 = pos_y.x - size_line_marks;
                    int y2 = pos;
                    
                    g2.drawLine(x1, y1, x2, y2);
                }
            }
        }
        
        //Daten anzeigen:
        g2.setColor(color_primary);
        g2.setStroke(new BasicStroke(size_stroke));
        Point pointTemp = new Point();
        Point pointTemp2 = new Point();
        size_curve = (int) (x_ElementDist / 2 * size_curve_muliplicator);
        int max_value = Collections.max(listValue);
        double temp = Double.valueOf(y_length) / Double.valueOf(max_value);
        
        for (int i = 0; i < x_numElements; i++) {
            
            pointTemp.y = (int)(pos_zero.y - listValue.get(i) * temp);
            pointTemp.x = (int)(pos_zero.x + Math.round(x_ElementDist * i));
            
            listPoints.add(new Point(pointTemp));
            if (paint_dot) {
                g2.fillOval(pointTemp.x - 2, pointTemp.y - 2, 5, 5);
            }
            
            if (i != 0) {
                switch (line_type) {
                    case STRAIGHT:
                        g2.drawLine(pointTemp2.x, pointTemp2.y, pointTemp.x, pointTemp.y);
                        break;
                    case CURVED:
                        CubicCurve2D c = new CubicCurve2D.Double();
                        c.setCurve(pointTemp2.x, pointTemp2.y, pointTemp2.x+size_curve, pointTemp2.y, pointTemp.x-size_curve, pointTemp.y, pointTemp.x, pointTemp.y);
                        g2.draw(c);
                        break;
                }
            }
            
            pointTemp2.x = pointTemp.x;
            pointTemp2.y = pointTemp.y;
            
        }
        
        //Test MausMovement;
        if ( check_highlight_close_point && pos_mouse != null ) {
            
            Point point_temp = new Point(getClosestPoint());
            
            if (mouse_dist < 50) {
                
                g2.setColor(color_highlight);
                g2.fillOval(point_temp.x - 5, point_temp.y - 5, 11, 11);
            }
        }
    }
    
    private void loadDebugData() {
        
        //Daten Zufällig generieren V2:
        int start_value = 1000;
        
        for (int i = 0; i < x_numElements; i++) {
            
            int temp = (int)(Math.random()*4);
            
            switch (temp) {
                case 0:
                    listString.add(LocalDate.now().plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    start_value += 50 + (int)(Math.random()*200);
                    listValue.add(start_value);
                    break;
                case 1:
                    listString.add(LocalDate.now().plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    start_value -= 10 + (int)(Math.random()*50);
                    listValue.add(start_value);
                    break;
                case 2:
                    listString.add(LocalDate.now().plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    start_value -= 20 + (int)(Math.random()*50);
                    listValue.add(start_value);
                    break;
                case 3:
                    listString.add(LocalDate.now().plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    start_value -= 30 + (int)(Math.random()*50);
                    listValue.add(start_value);
                    break;
            }
        }
    }
    
    private Point getClosestPoint(){
        
        Point ret = new Point();
        
        float x1 = pos_mouse.x;
        float y1 = pos_mouse.y;
        float x2;
        float y2;
        float dist;
        float dist_temp = 999999;
        
        for (int i = 0; i < listPoints.size(); i++) {
            
            x2 = listPoints.get(i).x;
            y2 = listPoints.get(i).y;
            
            dist = (float)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            
            if (dist < dist_temp) {
                ret = new Point(listPoints.get(i));
                dist_temp = dist;
                current_index = i;
            }
        }
        
        mouse_dist = dist_temp;
        return ret;
    }
    
    private void showTip(boolean visible) {
        
        if (visible && mouse_dist < 50) {
            
            Dimension dim_tip = tip.getPreferredSize();
            Dimension dim_chart = this.getSize();
            
            switch (tip_pos) {
                case CURSOR:
                    
                    pos_tip.x = pos_mouse.x;
                    pos_tip.y = pos_mouse.y;
                    
                    break;
                case VALUE:
                    
                    pos_tip.x = listPoints.get(current_index).x;
                    pos_tip.y = listPoints.get(current_index).y;
                    
                    break;
            }
            
            if (dim_chart.width >= pos_tip.x + dim_tip.width + offset_tip_x) {
                
                pos_tip.x = pos_tip.x + offset_tip_x;
                
            } else {
                
                pos_tip.x = pos_tip.x - dim_tip.width - offset_tip_x;
            }
            
            if (dim_chart.height >= pos_tip.y + dim_tip.height + offset_tip_y) {
                
                pos_tip.y = pos_tip.y + offset_tip_y;
                
            } else {
                
                pos_tip.y = pos_tip.y - dim_tip.height + offset_tip_y;
            }
            
            tip.setBounds(pos_tip.x, pos_tip.y, dim_tip.width, dim_tip.height);
            
            
            
            tip.setText(listString.get(current_index), String.valueOf(listValue.get(current_index)));
            this.add(tip);
            
            tip.setVisible(true);
            
        } else {
            
            tip.setVisible(false);
        }
    }
    
    public void setDebugMode(boolean debug_mode) {
        this.debug_mode = debug_mode;
    }
    
    private void chartMouseMoved(MouseEvent evt) {
        pos_mouse.x = evt.getX();
        pos_mouse.y = evt.getY();
        
        if (show_tooltip) {
            showTip(true);
        }
        
        this.repaint();
    }
    
    private void chartMouseEntered(MouseEvent evt) {
        
        check_highlight_close_point = true;
        
        this.repaint();
    }
    
    private void chartMouseExited(MouseEvent evt) {
        
        check_highlight_close_point = false;
        
        if (show_tooltip) {
            showTip(false);
        }
        
        this.repaint();
    }
    
}
