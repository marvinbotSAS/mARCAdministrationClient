package marcadministrationclient;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;

import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.*;

/**
 *
 * @author descourt
 */
public class TablesTreePopUpMenu extends JPopupMenu implements ActionListener
{

   JMenuItem newItem = new JMenuItem("Create a Table") ;

   public MainJFrame _frame;
   
   DefaultMutableTreeNode cutnode;
   DefaultMutableTreeNode pastenode;
   
   public int mouseX;
   public int mouseY;
   
    public TablesTreePopUpMenu() 
    {
        add(newItem);
        newItem.addActionListener( this );

        
    }
  
public void actionPerformed( ActionEvent e )
{
             // determine which menu item was selected
             JTree tree = (JTree) getInvoker();
              TreePath theSelectedPath = tree.getPathForLocation(mouseX, mouseY);
             if ( e.getActionCommand().equals(newItem.getActionCommand() ) )
             {
                 _frame.CreateATable(mouseX,mouseY);
                 return;
             }
               
}
              
}
