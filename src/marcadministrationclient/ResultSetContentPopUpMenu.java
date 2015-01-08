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
