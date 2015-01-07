/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;

import java.awt.Adjustable;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JTable;

/**
 *
 * @author patrice
 */
public class MyAdjustmentListener implements AdjustmentListener 
{
    
    public JTable owner;
    public MainJFrame frame;
    
    public MyAdjustmentListener() {
        this.owner = null;
    }
    
  public void adjustmentValueChanged(AdjustmentEvent evt) {
    Adjustable source = evt.getAdjustable();
    if (evt.getValueIsAdjusting()) {
      return;
    }
    int orient = source.getOrientation();
    if (orient == Adjustable.HORIZONTAL) {
      System.out.println("from horizontal scrollbar"); 
    } else {
      System.out.println("from vertical scrollbar");
    }
    int type = evt.getAdjustmentType();
    switch (type) {
    case AdjustmentEvent.UNIT_INCREMENT:
      System.out.println("Scrollbar was increased by one unit");
      break;
    case AdjustmentEvent.UNIT_DECREMENT:
      System.out.println("Scrollbar was decreased by one unit");
      break;
    case AdjustmentEvent.BLOCK_INCREMENT:
      System.out.println("Scrollbar was increased by one block");
      break;
    case AdjustmentEvent.BLOCK_DECREMENT:
      System.out.println("Scrollbar was decreased by one block");
      break;
    case AdjustmentEvent.TRACK:
      System.out.println("The knob on the scrollbar was dragged");
      break;
    }
    int value = evt.getValue();
    
            if ( this.owner.getRowCount() == 0 )
        {
            return;
        }
      Rectangle rect =  this.owner.getVisibleRect();
      
      frame.firstTableContentVisibleRow = this.owner.rowAtPoint(new Point(0,rect.y));
      frame.lastTableContentVisibleRow = this.owner.rowAtPoint(new Point(0, rect.y + rect.height - 1));
      
        
    
  }
}
