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



