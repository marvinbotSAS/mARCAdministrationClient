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
public class FieldsjTablePopUpMenu extends JPopupMenu implements ActionListener
{

 
    
   JMenuItem newFieldItem = new JMenuItem("Add field") ;
   JMenuItem removeFieldItem = new JMenuItem("Remove a field") ;
   public DefaultTableModel model;
   
   DefaultMutableTreeNode cutnode;
   DefaultMutableTreeNode pastenode;
   
   public int mouseX;
   public int mouseY;
   
    public FieldsjTablePopUpMenu() 
    {
        add(newFieldItem);
        newFieldItem.addActionListener( this );
        add( removeFieldItem );
        removeFieldItem.addActionListener( this );

    }
  
public void actionPerformed( ActionEvent e )
{
             
             if ( e.getActionCommand().equals(newFieldItem.getActionCommand() ) )
             {
                NewFieldJDialog d = new NewFieldJDialog();
                d.setModal(true);
                if ( d.state.equals("Ok"))
                {
                    // on rajoute une ligne
                    model.addRow(new String[]{d.NamejTextField.getText(),(String) d.jComboBox1.getSelectedItem()});
                }
                 return;
             }
 
                        if ( e.getActionCommand().equals(removeFieldItem.getActionCommand() ) )
             {
                NewFieldJDialog d = new NewFieldJDialog();
                d.setModal(true);
                if ( d.state.equals("Ok"))
                {
                    // on rajoute une ligne
                    model.addRow(new String[]{d.NamejTextField.getText(),(String) d.jComboBox1.getSelectedItem()});
                }
                 return;
             } 
              
}


}
