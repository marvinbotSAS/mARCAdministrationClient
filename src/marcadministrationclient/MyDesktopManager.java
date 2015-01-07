/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;
import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 *
 * @author patrice
 */
public class MyDesktopManager extends DefaultDesktopManager {
    
    
    public JDesktopPane _owner;
    public HashMap<JInternalFrame,Dimension> _myframes;

    public MyDesktopManager() {
        this._myframes = new HashMap<>();
    }
   
    public MyDesktopManager(JDesktopPane d) {
        this._myframes = new HashMap<>();
        if ( d != null )
        {
            JInternalFrame[] frames = d.getAllFrames();
            for (JInternalFrame frame : frames)
            {
                _myframes.put(frame, frame.getSize());
            }
        }
    }
    
    @Override
    public void iconifyFrame(JInternalFrame f) {
        super.iconifyFrame(f);

        if ( _owner != null )
        {
            if (!_myframes.containsKey(f))
            {
                _myframes.put(f,f.getSize());
            }
        }
        JInternalFrame.JDesktopIcon icon = f.getDesktopIcon();
        Dimension prefSize = icon.getPreferredSize();
        icon.setBounds(f.getX(), f.getY(), prefSize.width, prefSize.height);
    }

    public void SaveFramesTo(HashMap<String,FrameSpecs> frames)
    {
        
    }
    
    @Override
    public void maximizeFrame(JInternalFrame f)
    {
        super.maximizeFrame(f);
        
        Rectangle oldSize = this.getPreviousBounds(f);
       super.resizeFrame(f, f.getX(), f.getY(), oldSize.width, oldSize.height);
            
    }
};



