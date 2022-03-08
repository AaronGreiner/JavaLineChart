
package components;

import java.util.ArrayList;

public class LineChartValueManager {
    
    private ArrayList<LineChartValue> list = new ArrayList<LineChartValue>();
    
    private int visible_range = 0;
    private int visible_start_index = 0;
    
    public void clear() {
        list.clear();
    }
    
    public void add(int value, String text) {
        list.add(new LineChartValue(value, text));
    }
    
    public int size() {
        return list.size();
    }
    
    public int getMax(){
        
        int ret = 0;
        
        for (LineChartValue lineChartValue : list) {
            if (ret == 0 || lineChartValue.getValue() > ret) {
                ret = lineChartValue.getValue();
            }
        }
        return ret;
    }
    
    public int getMaxVisible(){
        
        int ret = 0;
        
        for (int i = getVisibleStartIndex(); i < getVisibleEndIndex(); i++){
            if (ret == 0 || list.get(i).getValue() > ret) {
                ret = list.get(i).getValue();
            }
        }
        return ret;
    }
    
    public int getMin(){
        
        int ret = 0;
        
        for (LineChartValue lineChartValue : list) {
            if (ret == 0 || lineChartValue.getValue() < ret) {
                ret = lineChartValue.getValue();
            }
        }
        return ret;
    }
    
    public int getMinVisible(){
        
        int ret = 0;
        
        for (int i = getVisibleStartIndex(); i < getVisibleEndIndex(); i++){
            if (ret == 0 || list.get(i).getValue() < ret) {
                ret = list.get(i).getValue();
            }
        }
        return ret;
    }
    
    public int getValue(int index) {
        return list.get(index).getValue();
    }
    
    public String getText(int index) {
        return list.get(index).getText();
    }
    
    public LineChartValue get(int index) {
        return list.get(index);
    }
    
    public void setVisibleRange(int range) {
        this.visible_range = range;
    }
    
    public void setVisibleStartIndex(int index) {
        this.visible_start_index = index;
    }
    
    public int getVisibleRange() {
        return visible_range;
    }
    
    public int getVisibleStartIndex() {
        return visible_start_index;
    }
    
    public int getVisibleEndIndex() {
        return visible_start_index + visible_range;
    }
}
