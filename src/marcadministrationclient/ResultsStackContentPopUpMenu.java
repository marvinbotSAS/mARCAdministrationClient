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

import java.awt.MouseInfo;
import java.awt.Point;
import javax.swing.*;

import java.awt.event.*;
import javax.swing.tree.*;

/**
 *
 * @author descourt
 */
public class ResultsStackContentPopUpMenu extends JPopupMenu implements ActionListener
{

   JMenuItem newItem = new JMenuItem("New") ;
   JMenuItem ontopItem = new JMenuItem("OnTop") ; 
   JMenuItem intersectItem = new JMenuItem("Intersection") ;
   JMenuItem unionItem = new JMenuItem("Union") ;
   JMenuItem swapItem = new JMenuItem("Swap") ; 
   JMenuItem dropItem = new JMenuItem("Drop") ; 
   JMenuItem dupItem = new JMenuItem("Dup") ; 
   JMenuItem normalizeItem = new JMenuItem("Normalize") ;
   JMenuItem amplifyItem = new JMenuItem("Amplify") ; 
   JMenuItem sortbyItem = new JMenuItem("SortBy") ;  
   JMenuItem selectbyItem = new JMenuItem("SelectBy") ; 
   JMenuItem uniquebyItem = new JMenuItem("UniqueBy") ; 
   JMenuItem deletebyItem = new JMenuItem("DeleteBy") ;
   JMenuItem selecttotableItem = new JMenuItem("Select To Table") ;
   JMenuItem selectfromtableItem = new JMenuItem("Select From Table") ;
 
   
   
   int row,column; 
   
   public MainJFrame _frame;
   
   DefaultMutableTreeNode cutnode;
   DefaultMutableTreeNode pastenode;
   
   public int mouseX;
   public int mouseY;
   
    public ResultsStackContentPopUpMenu() 
    {
        this.column = -1;
        add(newItem);
        newItem.addActionListener( this );
        add(ontopItem);
        ontopItem.addActionListener( this );
        add(intersectItem);
        intersectItem.addActionListener( this );
        add(unionItem);
        unionItem.addActionListener( this );
        add(swapItem);
        swapItem.addActionListener( this );
        add(dropItem);
        dropItem.addActionListener( this );
        add(dupItem);
        dupItem.addActionListener( this );
        add(normalizeItem);
        normalizeItem.addActionListener( this );
        add(amplifyItem);
        amplifyItem.addActionListener( this );  
        add(sortbyItem);
        sortbyItem.addActionListener( this );
        add(selectbyItem);
        selectbyItem.addActionListener( this );
        add(uniquebyItem);
        uniquebyItem.addActionListener( this ); 
        add(deletebyItem);
        deletebyItem.addActionListener( this );
        add(selecttotableItem);
        selecttotableItem.addActionListener( this );
        add(selectfromtableItem);
        selectfromtableItem.addActionListener( this );  
    }
  
public void actionPerformed( ActionEvent e )
{
             // determine which menu item was selected
             JTable table = (JTable) getInvoker();

             if ( e.getActionCommand().equals(newItem.getActionCommand() ) )
             {
                 _frame.NewRs();
                 return;
             }
             else if ( e.getActionCommand().equals(this.ontopItem.getActionCommand() ) )
             {
                 _frame.RsOnTop();
             }
             else if ( e.getActionCommand().equals(this.sortbyItem.getActionCommand() ) )
             {
                 _frame.RsSortBy();
             }
             else if ( e.getActionCommand().equals(this.selectbyItem.getActionCommand() ) )
             {
                 _frame.RsSelectBy();
             }
             else if ( e.getActionCommand().equals(this.uniquebyItem.getActionCommand() ) )
             {
                 _frame.RsUniqueBy();
             }          
             else if ( e.getActionCommand().equals(this.deletebyItem.getActionCommand() ) )
             {
                 _frame.RsDeleteBy();
             } 
             
             else if ( e.getActionCommand().equals(this.selecttotableItem.getActionCommand() ) )
             {
                 _frame.RsSelectToTable();
             }
             else if ( e.getActionCommand().equals(this.selectfromtableItem.getActionCommand() ) )
             {
                 _frame.RsSelectFromTable();
             }          
}
              
}
