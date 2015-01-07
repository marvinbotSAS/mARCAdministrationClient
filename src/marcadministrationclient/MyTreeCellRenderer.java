package marcadministrationclient;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.*;
import javax.swing.JLabel;
import javax.swing.JList;
import mARC.Connector.Connector;
/**
 *
 * @author xavier
 */
public class MyTreeCellRenderer extends JLabel implements TreeCellRenderer {
    
   ImageIcon unkwownConnectedImage = new ImageIcon(MainJFrame.class.getResource("black-led.jpeg"));
   ImageIcon ConnectedImage = new ImageIcon(MainJFrame.class.getResource("greenled.jpeg"));
   ImageIcon disConnectedImage = new ImageIcon(MainJFrame.class.getResource("redled.jpeg"));
   
    Icon unknownconnectedIcon;
    Icon connectedIcon;
    Icon disconnectedIcon;
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus)
    {   
        setPreferredSize(new Dimension(150, 20));
        setBorder(null);
        DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) value;         
        
        String name ="";
        
        if (theNode.isRoot()) 
        {
           setText(theNode.getUserObject().toString() ); 
           setOpaque(false);
           setBackground(Color.white);
           setForeground(Color.MAGENTA);
           setFont(new Font("Sans", Font.PLAIN,12));
           setIcon( null );
           return this;
        }  
        else
        {
            
            if ( theNode.getUserObject() instanceof Server )
            {
                Server theServer = (Server) theNode.getUserObject();
                if (theServer.connector.getIsConnected() )
                {
                    setIcon(connectedIcon);
                }
                else
                {
                    setIcon(null);
                    setIcon(unknownconnectedIcon);
                }
                setOpaque(false);
                setBackground(Color.white);
                setForeground(Color.black);
                setText(theServer.ip+":"+theServer.port);
            
            }
            else if ( theNode.getUserObject() instanceof String )
            {
                setIcon( null );
                setText(theNode.toString());


            }
        }
        
                if ( !selected )
                {
                    setOpaque(false);
                    setBackground(Color.white);
                    setForeground(Color.black);
                }
                else
                {
                    setOpaque(true);
                    setBackground(Color.blue);
                    setForeground(Color.white);                    
                }
        /*
        if ( theNode.getUserObject() instanceof TestsGroup )
        {
           
            TestsGroup theSession = (TestsGroup) theNode.getUserObject();
            setOpaque(false);
            setBackground(Color.white);
            setForeground(Color.black);
            if (selected)
            {
            setOpaque(true);
            setBackground(Color.BLUE);
            setForeground(Color.white);
            }
            setIcon(null);
            if( theSession.toExecute)
            {
                setIcon(exeIcon);
            }
            setFont(new Font("Sans", Font.PLAIN,12));
            setText(theSession._name);
            setIconTextGap(4);
            return this;
        }
        if ( theNode.getUserObject() instanceof Test)
        {
            Test script = (Test) theNode.getUserObject();
            Font font;
            if ( script.isModified )
            {
            font = new Font("Sans", Font.BOLD,12);
            }
            else
            {
             font = new Font("Sans", Font.PLAIN,12);
            }
            setIcon(null);
            if( script.toExecute)
            {
                setIcon(exeIcon);
            }
            setFont(font);
            setIconTextGap(4);
            setText(script._name);
            if (selected)
            {
            setOpaque(true);
            setBackground(Color.BLUE);
            setForeground(Color.white);
            
            }
            else
            {
            setOpaque(false);
            setBackground(Color.white);
            setForeground(Color.black);
            }

        }
        */
        return this;
    }
    public MyTreeCellRenderer()
    {         unknownconnectedIcon = new ImageIcon(unkwownConnectedImage.getImage().getScaledInstance(  20, 20 , 0));
       connectedIcon = new ImageIcon(ConnectedImage.getImage().getScaledInstance(  20, 20 , 0));
       disconnectedIcon = new ImageIcon(disConnectedImage.getImage().getScaledInstance(  50, 50 , 0));
    }
    
}
