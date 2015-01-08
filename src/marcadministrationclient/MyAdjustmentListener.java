/*
 * Copyright (C) 2015 Marvinbot S.A.S
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
