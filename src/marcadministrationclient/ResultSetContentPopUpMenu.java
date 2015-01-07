package marcadministrationclient;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Point;
import javax.swing.*;

import java.awt.event.*;
import javax.swing.tree.*;

/**
 *
 * @author descourt
 */
public class ResultSetContentPopUpMenu extends JPopupMenu implements ActionListener
{

   JMenuItem docToContextItem = new JMenuItem("DocToContext") ;   
   int row,column; 
   
   public MainJFrame _frame;
   
   DefaultMutableTreeNode cutnode;
   DefaultMutableTreeNode pastenode;
   
   public int mouseX;
   public int mouseY;
   
    public ResultSetContentPopUpMenu() 
    {
        this.column = -1;
        add(docToContextItem);
        docToContextItem.addActionListener( this );        
    }
  
public void actionPerformed( ActionEvent e )
{
             // determine which menu item was selected
             JTable table = (JTable) getInvoker();

             row = table.rowAtPoint(new Point(mouseX,mouseY));
             if ( row == -1 )
             {
                 return;
             }
             if ( e.getActionCommand().equals(this.docToContextItem.getActionCommand() ) )
             {
                 _frame.RSContentDocToContext(row);
             } 
}
              
}
