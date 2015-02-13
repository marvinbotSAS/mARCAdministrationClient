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
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import mARC.Connector.Connector;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
/**
 *
 * @author patrice
 */
public class Settings {
    


    
    public static String fileName = "mARCAdministrationClientSettings.xml";
    public static String BasefileName = "mARCAdministrationClientSettings";
    private static ArrayList<Server> servers = new ArrayList<>();
    
     public static String SettingsPath =  System.getProperty("user.dir");
         
    public static File settingsFolder = new File(SettingsPath);
    
    static public void WriteSettings(MainJFrame frame) throws TransformerConfigurationException, TransformerException
    {
       File xmlFile = new File(settingsFolder+File.separator+fileName);  
       if ( xmlFile.exists() )
       {
           Date d = new Date();
           Timestamp ts = new Timestamp(d.getTime());
           File timeStampedFile = new File(settingsFolder+File.separator+BasefileName+"_"+String.valueOf(ts.getTime())+".xml");
           xmlFile.renameTo(timeStampedFile);
       }
       
      try
      {
       DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();  
       DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();  
       Document document = documentBuilder.newDocument();  

       Element rootElement = document.createElement("Servers");  
       document.appendChild(rootElement);  

        for (Server server : frame.servers)
       // define root elements  
       {
       // define serverNode elements  
       Element serverNode = document.createElement("Server");  
       rootElement.appendChild(serverNode);  

       Element e  = document.createElement("isConnected");  
       e.appendChild(document.createTextNode(String.valueOf(server.connector.getIsConnected())) );  
       serverNode.appendChild(e); 
       
       e  = document.createElement("isCurrentServer"); 
       e.appendChild(document.createTextNode(String.valueOf(server == frame.CurrentServer)) );  
       serverNode.appendChild(e);       
       // name elements  
       e = document.createElement("Name");  
       e.appendChild(document.createTextNode(server.name));
       serverNode.appendChild(e);  
       
       e = document.createElement("IP");  
       e.appendChild(document.createTextNode(server.ip)); 
       serverNode.appendChild(e);  

       
       e = document.createElement("Port");  
       e.appendChild(document.createTextNode(server.port));  
       serverNode.appendChild(e);  

       e = document.createElement("WindowSplitDividerLocation");  
       e.appendChild(document.createTextNode(String.valueOf(server.WindowSplitDividerLocation) ) ); 
       
       e = document.createElement("WindowSplitDividerLocation");  
       e.appendChild(document.createTextNode(String.valueOf(server.WindowSplitDividerLocation) ) ); 
       serverNode.appendChild(e); 
       e = document.createElement("SessionsTasksSplitDividerlocation");  
       e.appendChild(document.createTextNode(String.valueOf(server.SessionsTasksSplitDividerlocation))  ); 
       serverNode.appendChild(e);  
       
       e = document.createElement("ServerInternalSplitDividerLocation");  
       e.appendChild(document.createTextNode(String.valueOf(server.ServerInternalSplitDividerLocation) ) ); 
       serverNode.appendChild(e); 
       
       e = document.createElement("SelectedTableIndex");  
       e.appendChild(document.createTextNode(String.valueOf(server.SelectedTableIndex))  ); 
       serverNode.appendChild(e);       

       e = document.createElement("CurrentSessionIndex");  
       int idx = server.FindSessionIndex(server.CurrentSession);
       e.appendChild(document.createTextNode(String.valueOf(idx) ) ); 
       serverNode.appendChild(e);

       if ( server.CurrentTable == null)
       {
           e.appendChild(document.createTextNode("null" ) ); 
       }
       else
       {
         e = document.createElement("CurrentTableName");
         e.appendChild(document.createTextNode(String.valueOf(server.CurrentTable.name))  );    
         serverNode.appendChild(e);
         if ( server.selectedFields != null && server.selectedFields.length != 0 )
         {
            e = document.createElement("selectedFields");
            e.setAttribute("length",String.valueOf(server.selectedFields.length)   ); 
            serverNode.appendChild(e);
            for (int i = 0; i < server.selectedFields.length; i++)
            {
               e = document.createElement("selectedFieldsN"+String.valueOf(i));
               e.appendChild(document.createTextNode( server.selectedFields[i] ) ); 
               serverNode.appendChild(e);               
            }
         }
       }
       
         e = document.createElement("TableContentDividerLocation");
         e.appendChild(document.createTextNode(String.valueOf(server.TableContentDividerLocation) ));  
         serverNode.appendChild(e);
         
         e = document.createElement("TableContentTextAreaDividerLocation");
         e.appendChild(document.createTextNode(String.valueOf(server.TableContentTextAreaDividerLocation) ));  
         serverNode.appendChild(e);         
         
         e = document.createElement("TableContentShownRowId");
         e.appendChild(document.createTextNode(String.valueOf(server.TableContentShownRowId)   ) ); 
         serverNode.appendChild(e);
         
         e = document.createElement("TableContentSize");
         e.appendChild(document.createTextNode(String.valueOf(server.TableContentSize)  )  ); 
         serverNode.appendChild(e);
         
         e = document.createElement("TableContentSliderValue");
         e.appendChild(document.createTextNode(String.valueOf(server.TableContentSliderValue)   ) ); 
         serverNode.appendChild(e);
         
         e = document.createElement("TableContentStart");
         e.appendChild(document.createTextNode(String.valueOf(server.TableContentStart)   ) ); 
         serverNode.appendChild(e);        
 
         e = document.createElement("firstTableContentVisibleRow");
         e.appendChild(document.createTextNode(String.valueOf(server.firstTableContentVisibleRow)   ) ); 
         serverNode.appendChild(e);           

         e = document.createElement("lastTableContentVisibleRow");
         e.appendChild(document.createTextNode(String.valueOf(server.lastTableContentVisibleRow)   ) ); 
         serverNode.appendChild(e);  
         
       e = document.createElement("tablesDividerLocation");
       e.appendChild(document.createTextNode(String.valueOf(server.tablesDividerLocation) ));  
       serverNode.appendChild(e);  
       
        e = document.createElement("FieldsTableColumnsWidths");
        e.setAttribute("length",String.valueOf(server.FieldsTableColumnsWidths.length)   ); 
        serverNode.appendChild(e);
        
        for (int i = 0; i < server.FieldsTableColumnsWidths.length; i++)
        {
            e = document.createElement("FieldsTableColumnsWidthsN"+i);
            e.appendChild(document.createTextNode( String.valueOf(server.FieldsTableColumnsWidths[i] ))  ); 
            serverNode.appendChild(e); 
        }  

        
        e = document.createElement("sessionsTablecolumnsWidths");
        e.setAttribute("length",String.valueOf(server.sessionsTablecolumnsWidths.length)   ); 
        serverNode.appendChild(e); 
        for (int i = 0; i <  server.sessionsTablecolumnsWidths.length;i++ )
        {
            e = document.createElement("sessionsTablecolumnsWidthsN"+i);
            e.appendChild(document.createTextNode( String.valueOf(server.sessionsTablecolumnsWidths[i] ))  ); 
            serverNode.appendChild(e); 
        }

        e = document.createElement("tasksTablecolumnsWidths");
        e.setAttribute("length",String.valueOf(server.tasksTablecolumnsWidths.length)  ); 
        serverNode.appendChild(e); 
        for (int i = 0; i <  server.tasksTablecolumnsWidths.length;i++ )
        {
            e = document.createElement("tasksTablecolumnsWidthsN"+i);
            e.appendChild(document.createTextNode( String.valueOf(server.tasksTablecolumnsWidths[i] ))  ); 
            serverNode.appendChild(e); 
        }        
        
        if ( server.ContentTablecolumnsWidths != null && server.ContentTablecolumnsWidths.length != 0)
        { 
            e = document.createElement("ContentTablecolumnsWidths");
            e.setAttribute("length",String.valueOf(server.ContentTablecolumnsWidths.length)   ); 
            serverNode.appendChild(e);  
            for (int i = 0; i < server.ContentTablecolumnsWidths.length; i++)
            {
                e = document.createElement("ContentTablecolumnsWidthsN"+i);
                e.appendChild(document.createTextNode( String.valueOf(server.ContentTablecolumnsWidths[i] ))  ); 
                serverNode.appendChild(e); 
            }
        }
       e = document.createElement("BTreesColumnsWidths");
       e.setAttribute("length",String.valueOf(server.BTreesColumnsWidths.length)   ); 
       serverNode.appendChild(e);  
       for (int i = 0; i < server.BTreesColumnsWidths.length; i++)
       {
           e = document.createElement("BTreesColumnsWidthsN"+i);
           e.appendChild(document.createTextNode( String.valueOf(server.BTreesColumnsWidths[i] ))  ); 
           serverNode.appendChild(e);  
       }    

       
       e = document.createElement("KTreesColumnsWidths");
       e.setAttribute("length",String.valueOf(server.KTreesColumnsWidths.length)   ); 
       serverNode.appendChild(e);
       for (int i = 0; i < server.KTreesColumnsWidths.length; i++)
       {
           e = document.createElement("KTreesColumnsWidthsN"+i);
           e.appendChild(document.createTextNode( String.valueOf(server.KTreesColumnsWidths[i] ))  ); 
           serverNode.appendChild(e);
       }
       
  
       
       e = document.createElement("LDBTableColumnsWidths");
       e.setAttribute("length",String.valueOf(server.LDBTableColumnsWidths.length)   ); 
       serverNode.appendChild(e);
       for (int i = 0; i < server.LDBTableColumnsWidths.length; i++)
       {
           e = document.createElement("LDBTableColumnsWidthsN"+i);
           e.appendChild(document.createTextNode( String.valueOf(server.LDBTableColumnsWidths[i] ))  ); 
           serverNode.appendChild(e);
       }
       
       
       e = document.createElement("LDBTreesDividerLocation");
       e.appendChild(document.createTextNode(String.valueOf(server.LDBTreesDividerLocation)  )  ); 
       serverNode.appendChild(e);

       // current ResultSet format
       e = document.createElement("RSformat");  
       if ( server.currentRSFormat != null && !server.currentRSFormat.isEmpty())
       {
           e.appendChild(document.createTextNode(server.currentRSFormat));
       }  
       else
       {
           e.appendChild(document.createTextNode("RowId"));
       }
       serverNode.appendChild(e);  

       // on sauve les donnees des windows internes
       for ( String s : server.frames.keySet() )
       {
           Element frameElement = document.createElement("InternalFrame");
           serverNode.appendChild(frameElement);
           e = document.createElement("Name");
           e.appendChild(document.createTextNode(s));
           frameElement.appendChild(e);  
           FrameSpecs fs = server.frames.get(s);
           e = document.createElement("isIcon");
           e.appendChild(document.createTextNode(String.valueOf(fs.isIcon)) );
           frameElement.appendChild(e);
           e = document.createElement("isMaximized");
           e.appendChild(document.createTextNode(String.valueOf(fs.isMaximized)) );
           frameElement.appendChild(e);
           e = document.createElement("isSelected");
           e.appendChild(document.createTextNode(String.valueOf(fs.isSelected)) );
           frameElement.appendChild(e);
           e = document.createElement("locationX");
           e.appendChild(document.createTextNode(String.valueOf(fs.location.x)) );
           frameElement.appendChild(e);
           e = document.createElement("locationY");
           e.appendChild(document.createTextNode(String.valueOf(fs.location.y)) );
           frameElement.appendChild(e);
           e = document.createElement("widht");
           e.appendChild(document.createTextNode(String.valueOf(fs.size.width)) );
           frameElement.appendChild(e);
           e = document.createElement("height");
           e.appendChild(document.createTextNode(String.valueOf(fs.size.height)) );
           frameElement.appendChild(e);           
       }
       
       for (Session session : server.sessions) 
       {
            Element sessionNode = document.createElement("Session");
            serverNode.appendChild(sessionNode);

            if ( session.propertiesChanged && session.values != null )
            {
                int f = 0;
                for (String property : session.values)
                {
                    if ( Session.prop_access[f].toLowerCase().equals("rw"))
                    {
                        e = document.createElement("Property");
                        e.appendChild(document.createTextNode(Session.properties[f] ) );
                        e.setAttribute("value", property);
                        sessionNode.appendChild(e);
                    }
                    f++;
                }
            }
                        
            if ( session.spectrumChanged && session.spectrum_values != null )
            {
                int f = 0;
                for (String value : session.spectrum_values)
                {
                    e = document.createElement("Spectrum");
                    e.appendChild(document.createTextNode(Session.spectrum_names[f++]) );
                    e.setAttribute("value", value);
                    sessionNode.appendChild(e);
                }
            }

            
            e = document.createElement("RSstackDividerLocation");  
            e.appendChild(document.createTextNode(String.valueOf(session.RSstackDividerLocation))  ); 
            sessionNode.appendChild(e);
            
            if (session.ResulSetContentColumnsWidths != null && session.ResulSetContentColumnsWidths.length != 0 )
            {
                e = document.createElement("ResulSetContentColumnsWidths");
                e.setAttribute("length",String.valueOf(session.ResulSetContentColumnsWidths.length)   ); 
                sessionNode.appendChild(e);
                for (int i = 0; i < session.ResulSetContentColumnsWidths.length; i++)
                {
                    e = document.createElement("ResulSetContentColumnsWidthsN"+i);
                    e.appendChild(document.createTextNode( String.valueOf(session.ResulSetContentColumnsWidths[i] ))  );
                    sessionNode.appendChild(e);
                }
            }
            
            if ( session.ResulSetPropertiesColumnsWidths != null && session.ResulSetPropertiesColumnsWidths.length != 0)
            {
                e = document.createElement("ResulSetPropertiesColumnsWidths");
            
                e.setAttribute("length",String.valueOf(session.ResulSetPropertiesColumnsWidths.length)   ); 
                sessionNode.appendChild(e);
                for (int i = 0; i < session.ResulSetPropertiesColumnsWidths.length; i++)
                {
                    e = document.createElement("ResulSetPropertiesColumnsWidthsN"+i);
                    e.appendChild(document.createTextNode( String.valueOf(session.ResulSetPropertiesColumnsWidths[i] ))  );
                    sessionNode.appendChild(e);
                }

            }
            if ( session.contextContentColumnsWidhts!= null && session.contextContentColumnsWidhts.length != 0)
            {
                e = document.createElement("contextContentColumnsWidhts");
                e.setAttribute("length",String.valueOf(session.contextContentColumnsWidhts.length)   ); 
                sessionNode.appendChild(e); 
                for (int i = 0; i < session.contextContentColumnsWidhts.length; i++)
                {
                    e = document.createElement("contextContentColumnsWidhtsN"+i);
                    e.appendChild(document.createTextNode( String.valueOf(session.contextContentColumnsWidhts[i] ))  );
                    sessionNode.appendChild(e); 
                }
            }
            
            e = document.createElement("contextPropertiesColumnsWidths");
            e.setAttribute("length",String.valueOf(session.contextPropertiesColumnsWidths.length)  ); 
            sessionNode.appendChild(e);
            for (int i = 0; i < session.contextPropertiesColumnsWidths.length; i++)
            {
                e = document.createElement("contextPropertiesColumnsWidthsN"+i);
                e.appendChild(document.createTextNode( String.valueOf(session.contextPropertiesColumnsWidths[i] ))  );
                sessionNode.appendChild(e);
            }
             
            if ( session.contextsStackColumnsWidths != null && session.contextsStackColumnsWidths.length != 0 )
            {
                e = document.createElement("contextsStackColumnsWidths");
                e.setAttribute("length",String.valueOf(session.contextsStackColumnsWidths.length)   ); 
                sessionNode.appendChild(e);
                for (int i = 0; i < session.contextsStackColumnsWidths.length; i++)
                {
                    e = document.createElement("contextsStackColumnsWidthsN"+i);
                    e.appendChild(document.createTextNode( String.valueOf(session.contextsStackColumnsWidths[i] ))  );
                    sessionNode.appendChild(e);
                }
            }
            
            if ( session.sessionPropertiesTableColumnsWidhts != null && session.sessionPropertiesTableColumnsWidhts.length != 0)
            {
                e = document.createElement("sessionPropertiesTableColumnsWidhts");
                e.setAttribute("length",String.valueOf(session.sessionPropertiesTableColumnsWidhts.length)   ); 
                sessionNode.appendChild(e);
                for (int i = 0; i < session.sessionPropertiesTableColumnsWidhts.length; i++)
                {
                    e = document.createElement("sessionPropertiesTableColumnsWidhtsN"+i);
                    e.appendChild(document.createTextNode( String.valueOf(session.sessionPropertiesTableColumnsWidhts[i] ))  );
                    sessionNode.appendChild(e);
                }
            }
            if ( session.sessionSpectrumTableColumnsWidhts != null && session.sessionSpectrumTableColumnsWidhts.length != 0 )
            {
                e = document.createElement("sessionSpectrumTableColumnsWidhts");
                e.setAttribute("length",String.valueOf(session.sessionSpectrumTableColumnsWidhts.length)   ); 
                sessionNode.appendChild(e); 
                for (int i = 0; i < session.sessionSpectrumTableColumnsWidhts.length; i++)
                {
                    e = document.createElement("sessionSpectrumTableColumnsWidhtsN"+i);
                    e.appendChild(document.createTextNode( String.valueOf(session.sessionSpectrumTableColumnsWidhts[i] ))  );
                    sessionNode.appendChild(e);
                }
            }
            
            e = document.createElement("ResultsAmplifyB");
            e.appendChild(document.createTextNode(String.valueOf(session.ResultsAmplifyB)   ) ); 
            sessionNode.appendChild(e);
            
            e = document.createElement("ResultsAmplifySlope");
            e.appendChild(document.createTextNode(String.valueOf(session.ResultsAmplifySlope)   ) ); 
            sessionNode.appendChild(e);            

            e = document.createElement("ResultsNormalizeItemIndex");
            e.appendChild(document.createTextNode(String.valueOf(session.ResultsNormalizeItemIndex)   ) ); 
            sessionNode.appendChild(e); 
            
            e = document.createElement("ResultsSortByFieldItemIndex");
            e.appendChild(document.createTextNode(String.valueOf(session.ResultsSortByFieldItemIndex)   ) ); 
            sessionNode.appendChild(e); 

            e = document.createElement("ResultsSortByOrderItemIndex");
            e.appendChild(document.createTextNode(String.valueOf(session.ResultsSortByOrderItemIndex)   ) ); 
            sessionNode.appendChild(e); 
            
            e = document.createElement("ResultsUniqueByItemIndex");
            e.appendChild(document.createTextNode(String.valueOf(session.ResultsUniqueByItemIndex)  )  ); 
            sessionNode.appendChild(e);  
            
            e = document.createElement("SelectFromTableFieldItemIndex");
            e.appendChild(document.createTextNode(String.valueOf(session.SelectFromTableFieldItemIndex)  )  ); 
            sessionNode.appendChild(e);  
            
            e = document.createElement("SelectFromTableModeItemIndex");
            e.appendChild(document.createTextNode(String.valueOf(session.SelectFromTableModeItemIndex)   ) ); 
            sessionNode.appendChild(e);   
 
            e = document.createElement("selectToTableDestinationTableNameItem");
            e.appendChild(document.createTextNode(session.selectToTableDestinationTableNameItem ) ); 
            sessionNode.appendChild(e);
            
            e = document.createElement("SelectToTableFieldItemIndex");
            e.appendChild(document.createTextNode(String.valueOf(session.SelectToTableFieldItemIndex)   ) ); 
            sessionNode.appendChild(e);

            
            e = document.createElement("SelectToTableUnique");
            e.appendChild(document.createTextNode(String.valueOf(session.selectToTableUnique)   ) ); 
            sessionNode.appendChild(e);
            
            e = document.createElement("contextAmplifyB");
            e.appendChild(document.createTextNode(String.valueOf(session.contextAmplifyB)  )  ); 
            sessionNode.appendChild(e); 
            
            e = document.createElement("contextAmplifySlope");
            e.appendChild(document.createTextNode(String.valueOf(session.contextAmplifySlope)   ) ); 
            sessionNode.appendChild(e);             

            e = document.createElement("contextIntersectionItemIndex");
            e.appendChild(document.createTextNode(String.valueOf(session.contextIntersectionItemIndex)   ) ); 
            sessionNode.appendChild(e);
            
            e = document.createElement("contextNormalizeItemIndex");
            e.appendChild(document.createTextNode(String.valueOf(session.contextNormalizeItemIndex)  )  ); 
            sessionNode.appendChild(e);  
            
            e = document.createElement("contextSortByFieldItemIndex");
            e.appendChild(document.createTextNode(String.valueOf(session.contextSortByFieldItemIndex)  )  ); 
            sessionNode.appendChild(e);    
            
            e = document.createElement("contextSortByOrderItemIndex");
            e.appendChild(document.createTextNode(String.valueOf(session.contextSortByOrderItemIndex)  )  ); 
            sessionNode.appendChild(e);
            
            e = document.createElement("contextUnionItemIndex");
            e.appendChild(document.createTextNode(String.valueOf(session.contextUnionItemIndex)  )  ); 
            sessionNode.appendChild(e);     
            
            e = document.createElement("ctxStackAndContentDividerlocation");
            e.appendChild(document.createTextNode(String.valueOf(session.ctxStackAndContentDividerlocation)   ) ); 
            sessionNode.appendChild(e);  
            
            e = document.createElement("ctxStaskDividerlocation");
            e.appendChild(document.createTextNode(String.valueOf(session.ctxStaskDividerlocation)   ) ); 
            sessionNode.appendChild(e); 
            
       }
       // creating and writing to xml file  
       TransformerFactory transformerFactory = TransformerFactory  
         .newInstance();  
       Transformer transformer = transformerFactory.newTransformer();  
       DOMSource domSource = new DOMSource(document);  
       StreamResult streamResult = new StreamResult(new File(  
         settingsFolder+File.separator+fileName));  
       transformer.setOutputProperty(OutputKeys.INDENT, "yes");
       transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
       transformer.transform(domSource, streamResult);  
       }
       System.out.println("File saved to specified path!");  

      } 
      catch (ParserConfigurationException | TransformerException pce) { 
          
          System.out.println(pce.getMessage());
      }  
    }
    
public static void LoadSettings(MainJFrame frame)
{
    if ( settingsFolder.isDirectory() )
    {

      try 
      {  

        File xmlFile = new File(settingsFolder+File.separator+fileName);  
        
        if ( !xmlFile.exists() )
        {
            frame.Updatelog("No Settings file found .Default is '"+xmlFile.getAbsoluteFile()+"' \n");
            frame.loadingSettings = false;
            return;
        }
        frame.loadingSettings = true;
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory  
          .newInstance();  
        DocumentBuilder documentBuilder = documentFactory  
          .newDocumentBuilder();  
        Document doc = documentBuilder.parse(xmlFile);  

        doc.getDocumentElement().normalize();  
        NodeList serversList = doc.getElementsByTagName("Server");  
        for (int temp = 0; temp < serversList.getLength(); temp++) 
        {  
         Node serverNode = serversList.item(temp);  

        if (serverNode.getNodeType() == Node.ELEMENT_NODE) 
        {  

           Element e = (Element) serverNode;  

           String isConnectedS = e.getElementsByTagName("isConnected").item(0).getTextContent();
           boolean isConnected = Boolean.parseBoolean(isConnectedS);
           
           boolean isCurrentServer =  Boolean.parseBoolean(e.getElementsByTagName("isCurrentServer").item(0).getTextContent());
           
           String ip = e.getElementsByTagName("IP").item(0).getTextContent();  
           String port = e.getElementsByTagName("Port").item(0).getTextContent();  
           
           Server server = new Server(ip,port);
           server.name =  e.getElementsByTagName("Name").item(0).getTextContent();
           server._frame = frame;

           if ( !server.connector.connect() )
           {
                   frame.Updatelog("Loading Settings : Unable to connect to server '"+server.name+" "+server.ip+":"+server.port+"'  skipping... \n");
                   continue;
           }
        server.Session_Id = server.connector.getKmScriptSession();
        server.ServerSession.id = server.Session_Id;
        server.ServerSession.connector = server.connector;           
        frame.AddAnewServer(server);

        server.currentRSFormat = e.getElementsByTagName("RSformat").item(0).getTextContent();  
        try
        {
            server.currentTableName = e.getElementsByTagName("CurrentTableName").item(0).getTextContent();
        } 
        catch(Exception eee)
        {
            server.currentTableName = "none";
        }
           server.WindowSplitDividerLocation = Integer.parseInt(e.getElementsByTagName("WindowSplitDividerLocation").item(0).getTextContent());  
            server.SessionsTasksSplitDividerlocation = Integer.parseInt(e.getElementsByTagName("SessionsTasksSplitDividerlocation").item(0).getTextContent());  
           server.ServerInternalSplitDividerLocation = Integer.parseInt(e.getElementsByTagName("ServerInternalSplitDividerLocation").item(0).getTextContent());  
           server.SelectedTableIndex =  Integer.parseInt(e.getElementsByTagName("SelectedTableIndex").item(0).getTextContent());
           int CurrentSessionIndex = Integer.parseInt(e.getElementsByTagName("CurrentSessionIndex").item(0).getTextContent());

           if (server.currentTableName.equals("null") )
           {
            server.CurrentTable = null;
            
           }
           else
           {
            String selectedFields = null;
            try
            {
                selectedFields = e.getElementsByTagName("selectedFields").item(0).getAttributes().getNamedItem("length").getNodeValue();
            }
            catch(Exception ee)
            {
                selectedFields = null;
            }
            if ( selectedFields != null )
            {
                int l = Integer.parseInt(selectedFields);
                server.selectedFields = new String[l];
                for (int i = 0; i < l; i++ )
                {
                    server.selectedFields[i] = e.getElementsByTagName("selectedFieldsN"+i).item(0).getTextContent();
                }
            }
           }
         server.TableContentDividerLocation = Integer.parseInt(e.getElementsByTagName("TableContentDividerLocation").item(0).getTextContent() ); 
         server.TableContentTextAreaDividerLocation = Integer.parseInt(e.getElementsByTagName("TableContentTextAreaDividerLocation").item(0).getTextContent() );
         server.TableContentShownRowId = e.getElementsByTagName("TableContentShownRowId").item(0).getTextContent(); 
         server.TableContentSize       = e.getElementsByTagName("TableContentSize").item(0).getTextContent(); 
         server.TableContentSliderValue = Integer.parseInt(e.getElementsByTagName("TableContentSliderValue").item(0).getTextContent()); 
         server.TableContentStart      = e.getElementsByTagName("TableContentStart").item(0).getTextContent(); 
         try
         {    
            server.firstTableContentVisibleRow = Integer.parseInt(e.getElementsByTagName("firstTableContentVisibleRow").item(0).getTextContent() ); 
            server.lastTableContentVisibleRow = Integer.parseInt(e.getElementsByTagName("lastTableContentVisibleRow").item(0).getTextContent() );          
         }
         catch(Exception er)
         {
             server.firstTableContentVisibleRow = -1;
             server.lastTableContentVisibleRow = -1;
         }
         server.tablesDividerLocation  = Integer.parseInt(e.getElementsByTagName("tablesDividerLocation").item(0).getTextContent()); 
       
        int l = Integer.parseInt(e.getElementsByTagName("FieldsTableColumnsWidths").item(0).getAttributes().getNamedItem("length").getNodeValue());

        server.FieldsTableColumnsWidths = new int[l];
        for (int i = 0; i < l; i++ )
        {
            server.FieldsTableColumnsWidths[i] = Integer.parseInt( e.getElementsByTagName("FieldsTableColumnsWidthsN"+i).item(0).getTextContent() );
        }  
        
        try
        {
            l = Integer.parseInt(e.getElementsByTagName("sessionsTablecolumnsWidths").item(0).getAttributes().getNamedItem("length").getNodeValue());
            for (int i = 0; i < l;i++)
            {
                server.sessionsTablecolumnsWidths[i] = Integer.parseInt( e.getElementsByTagName("sessionsTablecolumnsWidthsN"+i).item(0).getTextContent() );
            }
        }
        catch(Exception e43)
        {
            
        }

        try
        {
            l = Integer.parseInt(e.getElementsByTagName("tasksTablecolumnsWidths").item(0).getAttributes().getNamedItem("length").getNodeValue());
            for (int i = 0; i < l;i++)
            {
                server.tasksTablecolumnsWidths[i] = Integer.parseInt( e.getElementsByTagName("tasksTablecolumnsWidthsN"+i).item(0).getTextContent() );
            }
        }
        catch(Exception e21)
        {
            
        }


        String ContentTablecolumnsWidths;
        try
        {
           ContentTablecolumnsWidths  =  e.getElementsByTagName("ContentTablecolumnsWidths").item(0).getAttributes().getNamedItem("length").getNodeValue();
        }
        catch(Exception ee)
        {
            ContentTablecolumnsWidths = null;
        }
        if ( ContentTablecolumnsWidths != null )
        {
            l = Integer.parseInt(ContentTablecolumnsWidths);
            server.ContentTablecolumnsWidths = new int[l];
            for (int i = 0; i < l; i++)
            {
                server.ContentTablecolumnsWidths[i] = Integer.parseInt(e.getElementsByTagName("ContentTablecolumnsWidthsN"+i).item(0).getTextContent());
            }
        }
       l = Integer.parseInt(e.getElementsByTagName("BTreesColumnsWidths").item(0).getAttributes().getNamedItem("length").getNodeValue());
       server.BTreesColumnsWidths = new int[l];
       for (int i = 0; i < l; i++)
       {
            server.BTreesColumnsWidths[i] = Integer.parseInt(e.getElementsByTagName("BTreesColumnsWidthsN"+i).item(0).getTextContent());
       }    
        
       l = Integer.parseInt(e.getElementsByTagName("KTreesColumnsWidths").item(0).getAttributes().getNamedItem("length").getNodeValue());
       server.KTreesColumnsWidths = new int[l];
       for (int i = 0; i < l; i++)
       {
            server.KTreesColumnsWidths[i] = Integer.parseInt(e.getElementsByTagName("KTreesColumnsWidthsN"+i).item(0).getTextContent());
       }       
       
       l = Integer.parseInt(e.getElementsByTagName("LDBTableColumnsWidths").item(0).getAttributes().getNamedItem("length").getNodeValue());
       server.LDBTableColumnsWidths = new int[l];
       for (int i = 0; i < l; i++)
       {
            server.LDBTableColumnsWidths[i] = Integer.parseInt(e.getElementsByTagName("LDBTableColumnsWidthsN"+i).item(0).getTextContent());
       }
       
       server.LDBTreesDividerLocation  = Integer.parseInt(e.getElementsByTagName("LDBTreesDividerLocation").item(0).getTextContent()); 

       server.currentRSFormat = e.getElementsByTagName("RSformat").item(0).getTextContent(); 


              // on sauve les donnees des windows internes
        NodeList framesList = ((Element) serverNode).getElementsByTagName("InternalFrame");
        JInternalFrame[] frames = frame.jDesktopPane.getAllFrames();
        JInternalFrame frameInternal = null;
        for (int k = 0; k < framesList.getLength(); k++)
        {
            Node frameNode = framesList.item(k);
            Element frameElement = (Element) frameNode;
            String name = frameElement.getElementsByTagName("Name").item(0).getTextContent(); 
            int j = 0;
            for (; j < frames.length;j++)
            {
                if ( frames[j].getTitle().equals(name))
                {
                    frameInternal = frames[j];
                    break;
                }
            }
            if ( j == frames.length )
            {
                continue;
            }
           FrameSpecs fs = new FrameSpecs();
           server.frames.put(name, fs);
           
           fs.isIcon = Boolean.parseBoolean(frameElement.getElementsByTagName("isIcon").item(0).getTextContent() ); 
           fs.isMaximized = Boolean.parseBoolean(frameElement.getElementsByTagName("isMaximized").item(0).getTextContent() ); 
           fs.isSelected = Boolean.parseBoolean(frameElement.getElementsByTagName("isSelected").item(0).getTextContent() ); 
           int x = Integer.parseInt(frameElement.getElementsByTagName("locationX").item(0).getTextContent());
           int y = Integer.parseInt(frameElement.getElementsByTagName("locationY").item(0).getTextContent());
           fs.location = new Point(x,y);
           int w = Integer.parseInt(frameElement.getElementsByTagName("widht").item(0).getTextContent());
           int h = Integer.parseInt(frameElement.getElementsByTagName("height").item(0).getTextContent());
           fs.size = new Dimension(w,h);
               try 
               {
                   frameInternal.setIcon(fs.isIcon);
                   frameInternal.setMaximum(fs.isMaximized);
                   frameInternal.setSelected(fs.isSelected);
                   frameInternal.setLocation(fs.location);
                   frameInternal.setSize(w,h);
               } catch (PropertyVetoException ex) {
                   Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
               }

           
        }
       
       NodeList sessionsList = ((Element) serverNode).getElementsByTagName("Session");
   
       int size = sessionsList.getLength();
       
       for (int iii = 0; iii < size; iii++)
       {
           Node sessionNode = sessionsList.item(iii);
           Element sessionElement = (Element) sessionNode;
           Connector connector = new Connector();
           connector.setIp(server.ip);
           connector.setPort(server.port);
           connector.connect();
           NodeList propertiesNodes = sessionElement.getElementsByTagName("Property");
           String[] accessors = new String[propertiesNodes.getLength()];
           for (int g = 0; g < propertiesNodes.getLength();g++)
           {
               Node propertyNode = propertiesNodes.item(g);
               Element propertyElement = (Element) propertyNode;
               accessors[g]= propertyElement.getTextContent()+" = "+propertyElement.getAttributes().getNamedItem("value").getNodeValue();
           }
           if ( propertiesNodes.getLength() != 0)
           {
               connector.directExecute = true;
               connector.openScript(null);
               connector.SESSION_SetProperties(accessors);
           }
           NodeList spectrumNodes = sessionElement.getElementsByTagName("Spectrum");
           String accessor = "";
           for (int g = 0; g < spectrumNodes.getLength();g++)
           {
               Node spectrumNode = spectrumNodes.item(g);
               Element spectrumElement = (Element) spectrumNode;
               accessor += spectrumElement.getTextContent()+" = "+spectrumElement.getAttributes().getNamedItem("value").getNodeValue()+"; ";
           }
           if ( spectrumNodes.getLength() != 0)
           {
               connector.directExecute = true;
               connector.openScript(null);
               connector.SESSION_SetSpectrum(accessor);
           }
           // les properties et spectrum
           connector.directExecute = false;
           connector.openScript(null);
           connector.SESSION_GetProperties(null);
           connector.SESSION_GetSpectrum();
           connector.executeScript();
           String[] properties_values = null;
           String[] spectrum_values = null;
           if ( connector.result.mError )
           {
               frame.Updatelog(" ERROR occured command was '"+connector.getToSend()+"'  server answer is '"+connector.result.mErrorMessage+"'");
           }
           else
           {
               properties_values = connector.getDataByName("prop_value", 0);
               spectrum_values = connector.getDataByName("value", 1);
               if ( Session.firstInit )
                {
                    Session.properties = connector.getDataByName("prop_name", 0);
                    Session.types = connector.getDataByName("prop_type", 0);
                    Session.prop_access = connector.getDataByName("prop_access", 0);
                    Session.spectrum_names = connector.getDataByName("name", 1);
                    Session.spectrum_types = connector.getDataByName("type", 1);
                    Session.firstInit = false;
                }
           }
           server.AddASessionFromValues(properties_values, spectrum_values, connector);

           // on recupere la session
           Session session = server.FindSessionFromId(connector.SessionId);
           
           if ( iii == CurrentSessionIndex )
           {
               server.CurrentSession = session;
           }
           session.RSstackDividerLocation = Integer.parseInt(sessionElement.getElementsByTagName("RSstackDividerLocation").item(0).getTextContent()); 

           String ResulSetContentColumnsWidths = null;
           try
           { 
               ResulSetContentColumnsWidths = sessionElement.getElementsByTagName("ResulSetContentColumnsWidths").item(0).getAttributes().getNamedItem("length").getNodeValue();
           }
           catch(Exception eeee)
           {
               ResulSetContentColumnsWidths = null;
           }
           if ( ResulSetContentColumnsWidths != null )
           {    
               l =  Integer.parseInt( ResulSetContentColumnsWidths );
               session.ResulSetContentColumnsWidths = new int[l];
               for (int i = 0; i < l; i++)
               {
                    session.ResulSetContentColumnsWidths[i] = Integer.parseInt(sessionElement.getElementsByTagName("LDBTableColumnsWidthsN"+i).item(0).getTextContent());
               }
           }
           String ResulSetPropertiesColumnsWidths = null;
           try
           {
               ResulSetPropertiesColumnsWidths = sessionElement.getElementsByTagName("ResulSetPropertiesColumnsWidths").item(0).getAttributes().getNamedItem("length").getNodeValue();
           }
           catch(Exception e5 )
           {
               ResulSetPropertiesColumnsWidths = null;
           }
           if ( ResulSetPropertiesColumnsWidths != null )
           {
               l =  Integer.parseInt( ResulSetPropertiesColumnsWidths );
               session.ResulSetPropertiesColumnsWidths = new int[l];
               for (int i = 0; i < l; i++)
               {
                    session.ResulSetPropertiesColumnsWidths[i] = Integer.parseInt(sessionElement.getElementsByTagName("ResulSetPropertiesColumnsWidthsN"+i).item(0).getTextContent());
               }
           }
           String contextContentColumnsWidhts = null;
           try
           {
              contextContentColumnsWidhts = sessionElement.getElementsByTagName("contextContentColumnsWidhts").item(0).getAttributes().getNamedItem("length").getNodeValue();
           }
           catch(Exception y)
           {
               contextContentColumnsWidhts = null;
           }
            
           if ( contextContentColumnsWidhts != null )
           {
               l =  Integer.parseInt( contextContentColumnsWidhts );
               session.contextContentColumnsWidhts = new int[l];
               for (int i = 0; i < l; i++)
               {
                    session.contextContentColumnsWidhts[i] = Integer.parseInt(sessionElement.getElementsByTagName("contextContentColumnsWidhtsN"+i).item(0).getTextContent());
               }
           }
 
           String contextPropertiesColumnsWidths = null;
           try
           {
             contextPropertiesColumnsWidths =  sessionElement.getElementsByTagName("contextPropertiesColumnsWidths").item(0).getAttributes().getNamedItem("length").getNodeValue() ;
           }
           catch(Exception e6)
           {
               contextPropertiesColumnsWidths = null;
           }
           if ( contextPropertiesColumnsWidths != null)
           {
               l =  Integer.parseInt(contextPropertiesColumnsWidths);
               session.contextPropertiesColumnsWidths = new int[l];
               for (int i = 0; i < l; i++)
               {
                    session.contextPropertiesColumnsWidths[i] =  Integer.parseInt(sessionElement.getElementsByTagName("contextPropertiesColumnsWidthsN"+i).item(0).getTextContent());
               }
           }
           String contextsStackColumnsWidths = null;
           try
           {
             contextsStackColumnsWidths = sessionElement.getElementsByTagName("contextsStackColumnsWidths").item(0).getAttributes().getNamedItem("length").getNodeValue();
           }
           catch(Exception e7)
           {
               contextsStackColumnsWidths = null;
           }
           if ( contextsStackColumnsWidths != null )
           {
               l =  Integer.parseInt( contextsStackColumnsWidths );
           
               session.contextsStackColumnsWidths = new int[l];
               for (int i = 0; i < l; i++)
               {
                    session.contextsStackColumnsWidths[i] = Integer.parseInt(sessionElement.getElementsByTagName("contextsStackColumnsWidthsN"+i).item(0).getTextContent());
               }            
           }
           String sessionPropertiesTableColumnsWidhts = null;
           try
           {
             sessionPropertiesTableColumnsWidhts = sessionElement.getElementsByTagName("sessionPropertiesTableColumnsWidhts").item(0).getAttributes().getNamedItem("length").getNodeValue(); 
           }
           catch(Exception e8)
           {
               sessionPropertiesTableColumnsWidhts = null;
           }
           if ( sessionPropertiesTableColumnsWidhts != null )
           {
               l =  Integer.parseInt( sessionPropertiesTableColumnsWidhts );
               session.sessionPropertiesTableColumnsWidhts = new int[l];
               for (int i = 0; i < l; i++)
               {
                    session.sessionPropertiesTableColumnsWidhts[i] = Integer.parseInt(sessionElement.getElementsByTagName("sessionPropertiesTableColumnsWidhtsN"+i).item(0).getTextContent());
               } 
           }
           String sessionSpectrumTableColumnsWidhts = null;
           try
           {
               sessionSpectrumTableColumnsWidhts =  sessionElement.getElementsByTagName("sessionSpectrumTableColumnsWidhts").item(0).getAttributes().getNamedItem("length").getNodeValue();
           }
           catch(Exception e70)
           {
             sessionSpectrumTableColumnsWidhts = null;
           }
           if ( sessionSpectrumTableColumnsWidhts != null )
           {
               l =  Integer.parseInt( sessionSpectrumTableColumnsWidhts );
               session.sessionSpectrumTableColumnsWidhts = new int[l];
               for (int i = 0; i < l; i++)
               {
                    session.sessionSpectrumTableColumnsWidhts[i] = Integer.parseInt(sessionElement.getElementsByTagName("sessionSpectrumTableColumnsWidhtsN"+i).item(0).getTextContent());
               }            
           }
           session.ResultsAmplifyB = sessionElement.getElementsByTagName("ResultsAmplifyB").item(0).getTextContent();
           
           session.ResultsAmplifySlope = sessionElement.getElementsByTagName("ResultsAmplifySlope").item(0).getTextContent();
                       
           session.ResultsNormalizeItemIndex = Integer.parseInt(sessionElement.getElementsByTagName("ResultsNormalizeItemIndex").item(0).getTextContent());

           session.ResultsSortByFieldItemIndex = Integer.parseInt(sessionElement.getElementsByTagName("ResultsSortByFieldItemIndex").item(0).getTextContent());
 
           session.ResultsSortByOrderItemIndex = Integer.parseInt(sessionElement.getElementsByTagName("ResultsSortByOrderItemIndex").item(0).getTextContent());
            
           session.ResultsUniqueByItemIndex = Integer.parseInt(sessionElement.getElementsByTagName("ResultsUniqueByItemIndex").item(0).getTextContent());
 
           session.SelectFromTableFieldItemIndex = Integer.parseInt(sessionElement.getElementsByTagName("SelectFromTableFieldItemIndex").item(0).getTextContent());
 
           session.SelectFromTableModeItemIndex = Integer.parseInt(sessionElement.getElementsByTagName("SelectFromTableModeItemIndex").item(0).getTextContent());            
            
           
           session.selectToTableDestinationTableNameItem = sessionElement.getElementsByTagName("selectToTableDestinationTableNameItem").item(0).getTextContent(); 
           session.SelectToTableFieldItemIndex = Integer.parseInt(sessionElement.getElementsByTagName("SelectToTableFieldItemIndex").item(0).getTextContent()); 
           session.selectToTableUnique = Boolean.parseBoolean(sessionElement.getElementsByTagName("SelectToTableUnique").item(0).getTextContent());
           session.contextAmplifyB  = sessionElement.getElementsByTagName("contextAmplifyB").item(0).getTextContent(); 
 
           session.contextAmplifySlope   = sessionElement.getElementsByTagName("contextAmplifySlope").item(0).getTextContent(); 
           
           session.contextIntersectionItemIndex =  Integer.parseInt(sessionElement.getElementsByTagName("contextIntersectionItemIndex").item(0).getTextContent()); 

           session.contextNormalizeItemIndex  =  Integer.parseInt(sessionElement.getElementsByTagName("contextNormalizeItemIndex").item(0).getTextContent()); 

           session.contextSortByFieldItemIndex =  Integer.parseInt(sessionElement.getElementsByTagName("contextSortByFieldItemIndex").item(0).getTextContent());   
           
           session.contextSortByOrderItemIndex =  Integer.parseInt(sessionElement.getElementsByTagName("contextSortByOrderItemIndex").item(0).getTextContent());   
           
           session.contextUnionItemIndex =  Integer.parseInt(sessionElement.getElementsByTagName("contextUnionItemIndex").item(0).getTextContent());   
            
           session.ctxStackAndContentDividerlocation = Integer.parseInt(sessionElement.getElementsByTagName("ctxStackAndContentDividerlocation").item(0).getTextContent());   

           session.ctxStaskDividerlocation = Integer.parseInt(sessionElement.getElementsByTagName("ctxStaskDividerlocation").item(0).getTextContent());   

           session.pushed = true;
            
       }
        server.update();
        frame.updateServerStats(server);
        Table t = server.FindTableFromName(server.currentTableName);
        if ( t != null )
        {
            server.CurrentTable = t;
        }
        else
        {
            server.CurrentTable = null;
            server.currentTableName = null;
            frame.Updatelog("Loading Settings : WARNING unable to find Current Table for server '"+server.name+"' "+server.ip+":"+server.port+" name was '"+server.currentTableName+"' \n");
        }
        frame.CurrentTable = server.CurrentTable;
        if ( !isConnected )
        {
            server.connector.disConnect();
        }
        if ( isCurrentServer )
        {
            frame.CurrentSession = server.CurrentSession;
            frame.SwitchCurrentServer(server, false);
            frame.SwitchCurrentSession(server.CurrentSession, false);
            frame.updateStatus();
        }
        server.pushed = true;
       }               
    }  
            }  
          catch (   ParserConfigurationException | SAXException | IOException | DOMException e) 
          {  
            frame.Updatelog(e.getMessage());
          }  
        }
        
    frame.loadingSettings = false;
    
    }
    
    public static void UpdateServerSettings(Server server)
    {
        
    }
}
