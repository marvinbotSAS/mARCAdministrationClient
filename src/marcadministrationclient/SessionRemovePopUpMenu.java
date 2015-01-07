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
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author descourt
 */
public class SessionRemovePopUpMenu extends JPopupMenu implements ActionListener
{


   JMenuItem removeSessionItem = new JMenuItem("Remove") ;
   public DefaultTableModel model;
   

   MainJFrame _frame;
   
   public int mouseX;
   public int mouseY;
   
    public SessionRemovePopUpMenu() 
    {

        add( removeSessionItem );
        removeSessionItem.addActionListener( this );

    }
  
public void actionPerformed( ActionEvent e )
{
                       
             if ( e.getActionCommand().equals(removeSessionItem.getActionCommand() ) )
             {
                
                 _frame.removeASession();
                 return;
             } 
              
}


}
