/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.awt.Color;
import javax.swing.JList;
/**
 *
 * @author patrice
 */
class MyListCellRenderer extends JLabel implements ListCellRenderer<Object> {
     public MyListCellRenderer() 
     {
         setOpaque(true);
     }
     @Override
     public Component getListCellRendererComponent(JList<?> list,
                                                   Object value,
                                                   int index,
                                                   boolean isSelected,
                                                   boolean cellHasFocus) {

         if ( value instanceof Script )
         {
             Script script = (Script) value;
            setText(script.name);
         }
         Color background;
         Color foreground;

         // check if this cell represents the current DnD drop location
         JList.DropLocation dropLocation = list.getDropLocation();
         if (dropLocation != null
                 && !dropLocation.isInsert()
                 && dropLocation.getIndex() == index) {

             background = Color.BLUE;
             foreground = Color.WHITE;

         // check if this cell is selected
         } else if (isSelected) {
             background = Color.RED;
             foreground = Color.WHITE;

         // unselected, and not the DnD drop location
         } else {
             background = Color.WHITE;
             foreground = Color.BLACK;
         };

         setBackground(background);
         setForeground(foreground);

         return this;
     }
 }
