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
