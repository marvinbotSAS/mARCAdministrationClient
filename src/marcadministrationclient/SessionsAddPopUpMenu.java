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
