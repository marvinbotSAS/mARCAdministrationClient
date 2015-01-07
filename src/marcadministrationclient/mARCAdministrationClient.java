/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import java.awt.SplashScreen;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import javax.swing.UIManager.*;

/**
 *
 * @author patrice
 */
public class mARCAdministrationClient {

    /**
     * @param args the command line arguments
     */
   private static SplashScreen mySplash; 
   private static Graphics2D splashGraphics;
   private static Rectangle2D splashTextArea;
   private static Rectangle2D splashProgressArea;
   private static Font font;
   
/**
     * Display text in status area of Splash.  Note: no validation it will fit.
     * @param str - text to be displayed
     */
    public static void splashText(String str)
    {
        if (mySplash != null && mySplash.isVisible())
        {   // important to check here so no other methods need to know if there
            // really is a Splash being displayed

            // erase the last status text
            splashGraphics.setPaint(Color.LIGHT_GRAY);
            splashGraphics.fill(splashTextArea);

            // draw the text
            splashGraphics.setPaint(Color.WHITE);
            splashGraphics.drawString(str, (int)(splashTextArea.getX() + 10),(int)(splashTextArea.getY() + 15));

            // make sure it's displayed
            mySplash.update();
        }
    }
    /**
     * Prepare the global variables for the other splash functions
     */
    private static void splashInit()
    {
        mySplash = SplashScreen.getSplashScreen();
        if (mySplash != null)
        {   // if there are any problems displaying the splash this will be null
            Dimension ssDim = mySplash.getSize();
            int height = ssDim.height;
            int width = ssDim.width;
            // stake out some area for our status information
            splashTextArea = new Rectangle2D.Double(15., height*0.88, width * .45, 32.);
            splashProgressArea = new Rectangle2D.Double(width * .55, height*.92, width*.4, 12 );

            // create the Graphics environment for drawing status info
            splashGraphics = mySplash.createGraphics();
            font = new Font("Dialog", Font.PLAIN, 10);
            splashGraphics.setFont(font);
            
            // initialize the status info
            splashText("Starting mARC Administration Client.");
            splashProgress(0);
        }
    }
    /**
     * Display a (very) basic progress bar
     * @param pct how much of the progress bar to display 0-100
     */
    public static void splashProgress(int pct)
    {
        if (mySplash != null && mySplash.isVisible())
        {

            // Note: 3 colors are used here to demonstrate steps
            // erase the old one
            splashGraphics.setPaint(Color.LIGHT_GRAY);
            splashGraphics.fill(splashProgressArea);

            // draw an outline
            splashGraphics.setPaint(Color.BLUE);
            splashGraphics.draw(splashProgressArea);

            // Calculate the width corresponding to the correct percentage
            int x = (int) splashProgressArea.getMinX();
            int y = (int) splashProgressArea.getMinY();
            int wid = (int) splashProgressArea.getWidth();
            int hgt = (int) splashProgressArea.getHeight();

            int doneWidth = Math.round(pct*wid/100.f);
            doneWidth = Math.max(0, Math.min(doneWidth, wid-1));  // limit 0-width

            // fill the done part one pixel smaller than the outline
            splashGraphics.setPaint(Color.GREEN);
            splashGraphics.fillRect(x, y+1, doneWidth, hgt-1);

            // make sure it's displayed
            mySplash.update();
        }
    }
    public static void main(String[] args) 
    {

            SwingUtilities.invokeLater(new Runnable() 
            {
                MainJFrame frame;
            public void run() {
                
                splashInit();
                

                try
                {
                    
                UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
                    for (LookAndFeelInfo info : infos ) 
                {
                if ("Nimbus".equals(info.getName()))
                {
                    {
                        UIManager.setLookAndFeel(info.getClassName());
                    }
                     break;
                }
                
                }
                }
                catch (UnsupportedLookAndFeelException e) {
       // handle exception
    }
                
    catch (ClassNotFoundException e) {
       // handle exception
    }
    catch (InstantiationException e) {
       // handle exception
    }
    catch (IllegalAccessException e) {
       // handle exception
    }
                        
                frame = new MainJFrame();
                
                if (mySplash != null)   // check if we really had a spash screen
                    mySplash.close();   // if so we're now done with it
                frame.setVisible(true);

            }
            
            });

      
    }
    
}
