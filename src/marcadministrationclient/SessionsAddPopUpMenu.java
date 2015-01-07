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
public class SessionsAddPopUpMenu extends JPopupMenu implements ActionListener
{

 
    
   JMenuItem newSessionItem = new JMenuItem("Add a Session") ;

   public DefaultTableModel model;
   

   MainJFrame _frame;
   
   public int mouseX;
   public int mouseY;
   
    public SessionsAddPopUpMenu() 
    {
        add(newSessionItem);
        newSessionItem.addActionListener( this );


    }
  
public void actionPerformed( ActionEvent e )
{
             
             if ( e.getActionCommand().equals(newSessionItem.getActionCommand() ) )
             {
                _frame.addASession(mouseX,mouseY);
             }           
 
              
}


}
