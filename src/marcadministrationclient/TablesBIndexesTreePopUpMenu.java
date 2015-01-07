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
public class TablesBIndexesTreePopUpMenu extends JPopupMenu implements ActionListener
{

    int x,y;
   JMenuItem killBTreeItem = new JMenuItem("Kill") ;  
   JMenuItem rebuildItem = new JMenuItem("Rebuild") ;
   public MainJFrame _frame;
   
   DefaultMutableTreeNode cutnode;
   DefaultMutableTreeNode pastenode;
   
   public int mouseX;
   public int mouseY;
   
    public TablesBIndexesTreePopUpMenu() 
    {

        add( killBTreeItem );
        killBTreeItem.addActionListener( this );
        add(rebuildItem);
        rebuildItem.addActionListener( this );
    }
  
public void actionPerformed( ActionEvent e )
{
             // determine which menu item was selected
             JTree tree = (JTree) getInvoker();
             TreePath theSelectedPath = tree.getPathForLocation(x, y);

             if ( e.getActionCommand().equals(this.rebuildItem.getActionCommand() ) )
             {
                 _frame.RebuildBTree(theSelectedPath,x,y);
                 return;
             }
             if ( e.getActionCommand().equals(killBTreeItem.getActionCommand() ) )
             {
                 _frame.KillABTree(theSelectedPath);
                 return;
             }
               
}
              
}
