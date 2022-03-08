package components;

import java.awt.Color;
import java.awt.Point;

public class LineChartValue {
    
    private int value;
    private String text;
    
    private Point location;
    private Color color;
    
    public LineChartValue(int value, String text) {
        this.value = value;
        this.text = text;
        
        if (value >= 0) {
            this.color = Color.green;
        } else {
            this.color = Color.red;
        }
        
        location = new Point(0, 0);
    }
    
    public void setLocation(Point location) {
        this.location = location.getLocation();
    }
    
    public void setLocation(int x, int y) {
        this.location.x = x;
        this.location.y = y;
    }
    
    public Point getLocation() {
        return location;
    }
    
    public int getX() {
        return location.x;
    }
    
    public int getY() {
        return location.y;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getText() {
        return text;
    }
    
    public Color getColor() {
        return color;
    }
}
