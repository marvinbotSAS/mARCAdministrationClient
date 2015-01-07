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
public class mARCTreePopUpMenu extends JPopupMenu implements ActionListener
{

   JMenuItem saveItem = new JMenuItem("Save") ;
   JMenuItem reloadItem = new JMenuItem("Reload") ;
   JMenuItem rebuildItem = new JMenuItem("Rebuild") ;
   JMenuItem shutdownItem = new JMenuItem("ShutDown") ;
   JMenuItem publishItem = new JMenuItem("Publish") ;
   public MainJFrame _frame;
   
   DefaultMutableTreeNode cutnode;
   DefaultMutableTreeNode pastenode;
   
   public int mouseX;
   public int mouseY;
   
    public mARCTreePopUpMenu() 
    {
        add(saveItem);
        saveItem.addActionListener( this );
        add( reloadItem );
        reloadItem.addActionListener( this );
        add( rebuildItem );
        rebuildItem.addActionListener( this );        
        add( shutdownItem );
        shutdownItem.addActionListener( this );        
        add( publishItem );
        publishItem.addActionListener( this );
        
    }
  
public void actionPerformed( ActionEvent e )
{
             // determine which menu item was selected

             if ( e.getActionCommand().equals(this.saveItem.getActionCommand() ) )
             {
                 _frame.mARCSave();

             }
             else if ( e.getActionCommand().equals(this.reloadItem.getActionCommand() ) )
             {
                 _frame.mARCReload();
             }
             if ( e.getActionCommand().equals(this.shutdownItem.getActionCommand() ) )
             {
                 _frame.mARCShutDown();

             }
             else if ( e.getActionCommand().equals(this.rebuildItem.getActionCommand() ) )
             {
                 _frame.mARCRebuild();
             }
             else if ( e.getActionCommand().equals(this.publishItem.getActionCommand() ) )
             {
                 _frame.mARCPublish();
             }
               
}
              
}
