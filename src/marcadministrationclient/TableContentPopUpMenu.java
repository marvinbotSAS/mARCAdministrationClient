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
public class TableContentPopUpMenu extends JPopupMenu implements ActionListener
{

   JMenuItem deleteItem = new JMenuItem("Delete Selected Rows") ;
   JMenuItem deleteRangeItem = new JMenuItem("Delete Range") ;
   JMenuItem updateItem = new JMenuItem("Update Data") ;
   JMenuItem addItem = new JMenuItem("Add Data") ;
   JMenuItem insertItem = new JMenuItem("Insert Data") ;
   JMenuItem docToContextItem = new JMenuItem("DocToContext") ;   
   int row,column; 
   
   public MainJFrame _frame;
   
   DefaultMutableTreeNode cutnode;
   DefaultMutableTreeNode pastenode;
   
   public int mouseX;
   public int mouseY;
   
    public TableContentPopUpMenu() 
    {
        this.column = -1;
        add(deleteItem);
        deleteItem.addActionListener( this );
        add(deleteRangeItem);
        deleteRangeItem.addActionListener( this );
        add(updateItem);
        updateItem.addActionListener( this );
        add(addItem);
        addItem.addActionListener( this );
        add(insertItem);
        insertItem.addActionListener( this );
        add(docToContextItem);
        docToContextItem.addActionListener( this );        
    }
  
public void actionPerformed( ActionEvent e )
{
             // determine which menu item was selected
             JTable table = (JTable) getInvoker();

             if ( e.getActionCommand().equals(deleteItem.getActionCommand() ) )
             {
                 _frame.DeleteTableRows();
                 return;
             }
             else if ( e.getActionCommand().equals(this.deleteRangeItem.getActionCommand() ) )
             {
                 _frame.DeleteTableRangeRows(mouseX,mouseY);
                 return;
             }
             else if ( e.getActionCommand().equals(this.updateItem.getActionCommand() ) )
             {
                 _frame.UpdateDataTable(mouseX,mouseY, row, column);
                 return;
             }
             else if ( e.getActionCommand().equals(this.addItem.getActionCommand() ) )
             {
                 _frame.AddDataTable(mouseX,mouseY,row,column);
                 return;
             }
             else if ( e.getActionCommand().equals(this.insertItem.getActionCommand() ) )
             {
                 _frame.InsertTable(mouseX,mouseY);
                 return;
             }          
             else if ( e.getActionCommand().equals(this.docToContextItem.getActionCommand() ) )
             {
                 _frame.TableContentDocToContext(row,column);
                 return;
             } 
}
              
}
