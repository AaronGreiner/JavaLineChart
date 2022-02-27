
package components;

import java.util.ArrayList;

public class LineChartValueManager {
    
    private ArrayList<LineChartValue> list = new ArrayList<LineChartValue>();
    
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
    
    public int getMin(){
        
        int ret = 0;
        
        for (LineChartValue lineChartValue : list) {
            if (ret == 0 || lineChartValue.getValue() < ret) {
                ret = lineChartValue.getValue();
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
}
