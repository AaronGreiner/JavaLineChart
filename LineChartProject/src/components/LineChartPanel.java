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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.CubicCurve2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JPanel;

public class LineChartPanel extends JPanel {
    
    //Einstellungen für Nutzer: (Später mit Setter bearbeitbar)
    private LineType line_type = LineType.CURVED;
    private ToolTipPosition tip_pos = ToolTipPosition.CURSOR;
    
    private boolean debug_mode = false;
    private boolean paint_x_achse = true;
    private boolean paint_y_achse = true;
    private boolean paint_line_marks = false;
    private boolean paint_dot = true;
    private boolean show_tooltip = true;
    
    private int offset_border = 10;
    private int offset_tip_x = 10;
    private int offset_tip_y = 0;
    private int size_line_marks = 2;
    
    private float size_curve_muliplicator = 1.0f;
    private float size_stroke = 1.0f;
    
    private Color color_primary = Color.GRAY;
    private Color color_secondary = Color.LIGHT_GRAY;
    private Color color_highlight = Color.LIGHT_GRAY;
    
    //Nicht durch Nutzer bearbeiten:
    private boolean check_highlight_close_point = false;
    
    private Point pos_x = new Point();
    private Point pos_y = new Point();
    private Point pos_zero = new Point();
    private Point pos_mouse = new Point();
    private Point pos_tip = new Point();
    
    private int x_numElements = 1000; //Eigentlich überflüssig
    private int y_numElements = 10; //Eigentlich überflüssig
    private int size_curve = 1;
    private int current_index = 0;
    private int length_x_achse = 0;
    private int length_y_achse = 0;
    
    private float mouse_dist;
    
    private double dist_x_element = 0;
    private double dist_y_element = 0;
    
    private LineChartToolTip tip = new LineChartToolTip();
    private LineChartValueManager manager = new LineChartValueManager();
    
    public LineChartPanel() {
        
        loadDebugData();
        manager.setVisibleRange(60);
        manager.setVisibleStartIndex(0);
        
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
            @Override
            public void mouseClicked(MouseEvent evt) {
                chartMouseClicked();
            }
        });
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                chartMouseWheelMoved(e);
            }
        });
    }
    
    protected void paintComponent(Graphics g) {
        
        //Zurücksetzen vom vorherigen Paint:
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //Antialiasing
        g2.setColor(color_secondary);
        
        //Positionen berechnen:
        Dimension d = this.getSize();
        
        //Wenn keine Elemente gefüllt, dann nichts malen:
        if (manager.size() <= 0) {
            return;
        }
        
        //Wenn zu klein, dann nichts malen:
        if ( d.height - offset_border * 2 < 20 ) {
            return;
        }
        
        int max = manager.getMaxVisible();
        int min = manager.getMinVisible();
        
        int position_x_on_y;
        
        if (max <= 0) {
            
            //X-Achse ganz oben
            position_x_on_y = offset_border;
            
        } else if (min >= 0) {
            
            //X-Achse ganz unten
            position_x_on_y = d.height - offset_border;
            
        } else {
            
            position_x_on_y = (int) ((d.height - (offset_border)) * (Double.valueOf(max) / Double.valueOf(Math.abs(max) + Math.abs(min))));
        }
        
        pos_x.x = d.width - offset_border;
        pos_x.y = position_x_on_y;
        
        pos_y.x = offset_border;
        pos_y.y = offset_border;
        
        pos_zero.x = offset_border;
        pos_zero.y = position_x_on_y;
        
        length_x_achse = pos_x.x - pos_zero.x;
        dist_x_element = (double)length_x_achse / manager.getVisibleRange();
        
        length_y_achse = pos_zero.y - pos_y.y;
        dist_y_element = (double)length_y_achse / y_numElements;
        
        size_curve = (int)(dist_x_element / 2 * size_curve_muliplicator);
        
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
            g2.drawLine(pos_zero.x, (d.height - offset_border), pos_y.x, pos_y.y); //Y Achse
        }
        
        //Daten anzeigen:
        g2.setColor(color_primary);
        g2.setStroke(new BasicStroke(size_stroke));
        
        Point pointTemp = new Point();
        Point pointTemp2 = new Point();
        double temp = Double.valueOf(length_y_achse) / Double.valueOf(max);
        
        System.out.println(manager.getVisibleStartIndex() + "  " + manager.getVisibleEndIndex());
        
        System.out.println("-");
        for (int i = manager.getVisibleStartIndex(); i < manager.getVisibleEndIndex(); i++) {
            
            pointTemp.y = (int)(pos_zero.y - manager.getValue(i) * temp);
            pointTemp.x = (int)(pos_zero.x + Math.round(dist_x_element * (i - manager.getVisibleStartIndex())));
            
//            System.out.println(pointTemp.y + "  " + pointTemp.x);
            
            manager.get(i).setLocation(new Point(pointTemp));
            
            g2.setColor(color_primary);
            
            if (i != manager.getVisibleStartIndex()) {
                switch (line_type) {
                    case STRAIGHT:
                        g2.drawLine(pointTemp2.x, pointTemp2.y, pointTemp.x, pointTemp.y);
                        break;
                    case CURVED:
                        CubicCurve2D c = new CubicCurve2D.Double();
                        //c.setCurve(pointTemp2.x, pointTemp2.y, pointTemp2.x+size_curve, pointTemp2.y, pointTemp.x-size_curve, pointTemp.y, pointTemp.x, pointTemp.y);
                        c.setCurve(pointTemp2.x+3, pointTemp2.y, pointTemp2.x+size_curve, pointTemp2.y, pointTemp.x-size_curve, pointTemp.y, pointTemp.x-3, pointTemp.y);
                        g2.draw(c);
                        break;
                }
            }
            
            pointTemp2.x = pointTemp.x;
            pointTemp2.y = pointTemp.y;
            
        }
        
        //Punkte anzeigen:
        for (int i = manager.getVisibleStartIndex(); i < manager.getVisibleEndIndex(); i++) {
            
            g2.setColor(manager.get(i).getColor());
            
            if (paint_dot) {
                //g2.fillOval(pointTemp.x - 2, pointTemp.y - 2, 5, 5);
                g2.drawOval(manager.get(i).getX()-3, manager.get(i).getY()-3, 6, 6);
            }
        }
        
        //Für Mausbewegung
        if ( check_highlight_close_point && pos_mouse != null ) {
            
            Point point_temp = new Point(getClosestPoint());
            
            if (mouse_dist < 50) {
                
                g2.setColor(color_highlight);
                g2.drawOval(point_temp.x - 5, point_temp.y - 5, 10, 10);
            }
        }
    }
    
    private void loadDebugData() {
        
        manager.clear();
        
        int current_value = 0;
        boolean up = true;
        
        for (int i = 0; i < x_numElements; i++) {
            
            if(current_value < -100) {
                up = true;
            } else if (current_value > 2000) {
                up = false;
            }
            
            if (up) {
                current_value += (int)(Math.random()*3000);
            } else {
                current_value -= (int)(Math.random()*200);
            }
            
            manager.add(current_value, LocalDate.now().plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
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
        
        for (int i = manager.getVisibleStartIndex(); i < manager.getVisibleEndIndex(); i++) {
            
            x2 = manager.get(i).getX();
            y2 = manager.get(i).getY();
            
            dist = (float)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            
            if (dist < dist_temp) {
                ret = new Point(manager.get(i).getLocation());
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
                    
                    pos_tip.x = manager.get(current_index).getX();
                    pos_tip.y = manager.get(current_index).getX();
                    
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
            
            tip.setText(manager.getText(current_index), String.valueOf(manager.getValue(current_index)));
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
    
    private void chartMouseClicked() {
        loadDebugData();
        this.repaint();
        System.out.println("RELOAD DEBUG DATA");
    }
    
    private void chartMouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            updateVisibleStartIndex(-3);
        } else {
            updateVisibleStartIndex(3);
        }
    }
    
    public void updateVisibleStartIndex(int x) {
        
        int update = manager.getVisibleStartIndex() + x;
        
        if (update < 0) {
            update = 0;
        } else if (update > manager.size() - manager.getVisibleRange()) {
            update = manager.size() - manager.getVisibleRange();
        }
        
        manager.setVisibleStartIndex(update);
        this.repaint();
    }
}
