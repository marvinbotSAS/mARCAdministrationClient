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
public class ContextsStackContentPopUpMenu extends JPopupMenu implements ActionListener, MouseMotionListener
{

   JMenuItem newItem = new JMenuItem("New") ;
   JMenuItem ontopItem = new JMenuItem("OnTop") ; 
   JMenuItem intersectItem = new JMenuItem("Intersection") ;
   JMenuItem unionItem = new JMenuItem("Union") ;
   JMenuItem swapItem = new JMenuItem("Swap") ; 
   JMenuItem dropItem = new JMenuItem("Drop") ; 
   JMenuItem dupItem = new JMenuItem("Dup") ; 
      JMenuItem splitItem = new JMenuItem("Split") ; 
   JMenuItem normalizeItem = new JMenuItem("Normalize") ;
   JMenuItem ctxToDocItem = new JMenuItem("ContextToDoc") ;
   JMenuItem ctxToctxItem = new JMenuItem("ContextToContext") ;
   JMenuItem amplifyItem = new JMenuItem("Amplify") ; 
   JMenuItem sortbyItem = new JMenuItem("SortBy") ;  
    JMenuItem learnItem = new JMenuItem("Learn") ;
   
   
   int row,column; 
   
   public MainJFrame _frame;
   
   DefaultMutableTreeNode cutnode;
   DefaultMutableTreeNode pastenode;
   
   public int mouseX;
   public int mouseY;
   
    public ContextsStackContentPopUpMenu() 
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
        add(ctxToDocItem);
        ctxToDocItem.addActionListener( this );
        add(ctxToctxItem);
        ctxToctxItem.addActionListener( this );  
        add(amplifyItem);
        amplifyItem.addActionListener( this );
        add(sortbyItem);
        sortbyItem.addActionListener( this );
        add(splitItem);
        splitItem.addActionListener( this );
        add(learnItem);
        learnItem.addActionListener( this );
    }
  
    @Override
    public void mouseMoved(MouseEvent e)
    {

                
                
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
        
    }
public void actionPerformed( ActionEvent e )
{
             // determine which menu item was selected
             JTable table = (JTable) getInvoker();

             if ( e.getActionCommand().equals(newItem.getActionCommand() ) )
             {
                 _frame.ContextNew();
             }
             else if ( e.getActionCommand().equals(this.ontopItem.getActionCommand() ) )
             {
                 _frame.ContextOnTop();
             }
             else if ( e.getActionCommand().equals(this.intersectItem.getActionCommand() ) )
             {
                 _frame.ContextIntersection();
             }
             else if ( e.getActionCommand().equals(this.unionItem.getActionCommand() ) )
             {
                 _frame.ContextUnion();
             }
             else if ( e.getActionCommand().equals(this.swapItem.getActionCommand() ) )
             {
                 _frame.ContextSwap();
             }          
             else if ( e.getActionCommand().equals(this.dropItem.getActionCommand() ) )
             {
                 _frame.ContextDrop();
             }
             else if ( e.getActionCommand().equals(dupItem.getActionCommand() ) )
             {
                 _frame.ContextDup();
             }
             else if ( e.getActionCommand().equals(this.normalizeItem.getActionCommand() ) )
             {
                 _frame.ContextNormalize();
             }
             else if ( e.getActionCommand().equals(this.ctxToDocItem.getActionCommand() ) )
             {
                 _frame.ContextToDoc();
             }
            else if ( e.getActionCommand().equals(this.ctxToctxItem.getActionCommand() ) )
             {
                 _frame.ContextToContext();
             }
             else if ( e.getActionCommand().equals(this.amplifyItem.getActionCommand() ) )
             {
                 _frame.ContextAmplify();
             }          
             else if ( e.getActionCommand().equals(this.sortbyItem.getActionCommand() ) )
             {
                 _frame.ContextSortBy();

             }
             else if ( e.getActionCommand().equals(this.learnItem.getActionCommand() ) )
             {
                 _frame.ContextLearn();
             }          

}
              
}
