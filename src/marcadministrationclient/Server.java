/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;

import mARC.Connector.*;
import java.util.HashSet;
import java.util.ArrayList;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
/**
 *
 * @author patrice
 */

class FrameSpecs
{
    public Dimension size;
    public Point location;
    public boolean isIcon;
    public boolean isSelected;
    public boolean isMaximized;
}
public class Server {
    
    public MainJFrame _frame;
    
    boolean showTableContent;

    boolean showFieldsTable;
    boolean showTables;
    boolean showmARC;

    
    String[] selectedFields;

    
    public boolean pushed;
    
    Session CurrentSession;
    Context CurrentContext;
    Table CurrentTable;
    
    int[] TableContentSelectedRows;
    int[] LDBTableColumnsWidths;
    int TableContentSelectedColumn;
    
    int ShownContextIndex;
    int ShownResultSetIndex;
    String TableContentStart;
    String TableContentSize;
    String selectedFrame;
    int TableContentSliderValue;
    int TableContentDividerLocation;
    int TableContentTextAreaDividerLocation;
    public int firstTableContentVisibleRow ;
    public int lastTableContentVisibleRow ;
    
    String TableContentTextAreaText;
    
    int[] ReSultSetContentSelectedRows;
    
    HashMap<String, FrameSpecs> frames = new HashMap<>();
    
    static String SessionName = "Session";
    static int SessionId = 0;
    static ArrayList<String> sessionsNamesStock = new ArrayList<>();
    
    ArrayList<Component> notToUpdate;
    Connector connector;
    
    String currentRSFormat;
    String currentTableName;
    
    ArrayList<Session> toName;
    

    
    
    final List<Task> tasks;
    ArrayList<Task> tasksToRemove;
    final List<Session> sessions;
    ArrayList<Session> sessionsToRemove;
    String ip;
    String port;
    String name;
    String type;
    String model;
    String version;
    String build;
    String connection_count;
    String command_threads;
    String time_local;
    String time_gmt;
    String up_time;
    String idle_time;
    String cache_size;
    String indexation_cache_size;
    String indexation_timeout;
    String cache_used ;
    String indexation_cache_used ;
    String cache_hits;
    String exec_timeout_default;
    String session_timeout_default;
    ArrayList<Table> toRemove;
    
    String Session_Id;
    Session ServerSession;
    String lastDBInfoTableName;    
    String lastDBInfoOperation;
    String lastDBInfoId;
    String lastDBInfoStatus;
    
    int tablesDividerLocation;
    int LDBTreesDividerLocation;
    int WindowSplitDividerLocation;
    int ServerInternalSplitDividerLocation;
    int SessionsTasksSplitDividerlocation;
    int[] sessionsTablecolumnsWidths;
    int[] tasksTablecolumnsWidths;
    int[] ContentTablecolumnsWidths;
    
    String TableContentShownRowId;
    
    int[] FieldsTableColumnsWidths;
    int[] BTreesColumnsWidths;
    int[] KTreesColumnsWidths;
    

    int SelectedTableIndex;
    
    public mARC _marc;
    final Set<Table> tables;

    public Server(String ip, String port)
    {
        this.TableContentShownRowId = "-1";
        this.SessionsTasksSplitDividerlocation = -1;
        this.ServerInternalSplitDividerLocation = -1;
        this.WindowSplitDividerLocation = -1;
        this.ShownContextIndex = -1;
        this.ShownResultSetIndex = -1;
        this.tasksToRemove = new ArrayList<>();

        this.selectedFrame = "none";
        this.showTableContent = false;

        this.showFieldsTable = false;
        this.TableContentSliderValue = -1;
        this.TableContentSize = "";
        this.TableContentStart = "";
        this.TableContentTextAreaText = "";
        this.currentTableName = "none";
        this.currentRSFormat = "";
        this.notToUpdate = new ArrayList<>();
        this.toName = new ArrayList<>();
        this.tasks = Collections.synchronizedList(new ArrayList<Task>());
        this.sessionsToRemove = new ArrayList<>();
        this.sessions = Collections.synchronizedList(new ArrayList<Session>());
        this.toRemove = new ArrayList<>();
        this.ip = ip;
        this.port = port;
        connector = new Connector();
        connector.setIp(ip);
        connector.setPort(port);
        Session_Id = connector.getKmScriptSession();
        this.ServerSession = new Session();
        this.ServerSession.id = Session_Id;
        this.ServerSession.connector = connector;
       //  sessions.add(ServerSession);
        tables = Collections.synchronizedSet(new HashSet<Table>());
        _marc = new mARC();
        
    }
    
    public void PushFieldsTableColumnsWidths()
    {
        this.showFieldsTable = this._frame.FieldsTable.getRowCount() != 0;
        int  n = this._frame.FieldsTable.getColumnModel().getColumnCount();
        this.FieldsTableColumnsWidths = new int[n];
        for (int k = 0; k < n;k++)
        {
            this.FieldsTableColumnsWidths[k] = this._frame.FieldsTable.getColumnModel().getColumn(k).getWidth();
        }
    }
    
    public void PushTableContentFrameSettings()
    {
        this.TableContentDividerLocation = this._frame.TableContentjSplitPane.getDividerLocation();
        this.TableContentTextAreaDividerLocation = this._frame.TableContentTextAreajSplitPane.getDividerLocation();
        this.PushTableContentColumnsWidths();
        this.TableContentSize = this._frame.SizejTextField.getText();
        this.TableContentStart = this._frame.StartjTextField.getText();
        this.TableContentSliderValue = this._frame.TableLinesjSlider.getValue();
        this.TableContentTextAreaText = this._frame.ContentTablejTextArea.getText();
        this.TableContentShownRowId = this._frame.shownTableContentRowId;
        this.TableContentSelectedRows = this._frame.ContentjTable.getSelectedRows();
        this.firstTableContentVisibleRow = this._frame.firstTableContentVisibleRow;
        this.lastTableContentVisibleRow = this._frame.lastTableContentVisibleRow;
                
        this.TableContentSelectedColumn = this._frame.ContentjTable.getSelectedColumn();
        
    }

    public void PopTableContentFrameSettings()
    {
        this._frame.TableContentjSplitPane.setDividerLocation(this.TableContentDividerLocation);
        this._frame.TableContentTextAreajSplitPane.setDividerLocation(this.TableContentTextAreaDividerLocation);
        this.PopTableContentColumnsWidths();
        this._frame.SizejTextField.setText(this.TableContentSize);
        this._frame.StartjTextField.setText(this.TableContentStart);
        this._frame.TableLinesjSlider.setValue(this.TableContentSliderValue);
        this._frame.shownTableContentRowId = this.TableContentShownRowId;
        if ( this.firstTableContentVisibleRow != -1)
        {
            this._frame.ContentjTable.scrollRectToVisible(this._frame.ContentjTable.getCellRect(this.firstTableContentVisibleRow, 0, true) );
        }
        
    }
        
    public void PushTableContentColumnsWidths()
    {
        this.ContentTablecolumnsWidths = new int[this._frame.ContentjTable.getColumnModel().getColumnCount()];        
        for (int i = 0; i < this._frame.ContentjTable.getColumnModel().getColumnCount();i++)
        {
            this.ContentTablecolumnsWidths[i] = this._frame.ContentjTable.getColumnModel().getColumn(i).getWidth();
        }
        this.TableContentSelectedRows = this._frame.ContentjTable.getSelectedRows();
    }
    
    public void PopTableContentColumnsWidths()
    {
        if ( TableContentSelectedRows != null && TableContentSelectedRows.length <= this._frame.ContentjTable.getRowCount()  )
        {
            this._frame.ContentjTable.clearSelection();
            for (int i : this.TableContentSelectedRows )
            {
              this._frame.ContentjTable.addRowSelectionInterval(i, i);
            }
        }    
            
        if ( this.ContentTablecolumnsWidths == null || this.ContentTablecolumnsWidths.length == 0 || this.ContentTablecolumnsWidths.length != this._frame.ContentjTable.getColumnCount() )
        {
            return;
        }
        for (int i = 0; i < this._frame.ContentjTable.getColumnModel().getColumnCount();i++)
        {
            this._frame.ContentjTable.getColumnModel().getColumn(i).setWidth(this.ContentTablecolumnsWidths[i]);
        }
    }
    
    public void PushtasksTablesColumnsWidths()
    {
        this.tasksTablecolumnsWidths = new int[this._frame.tasksjTable.getColumnCount()];
        for (int i = 0; i < this._frame.tasksjTable.getColumnModel().getColumnCount();i++)
        {
            this.tasksTablecolumnsWidths[i] = this._frame.tasksjTable.getColumnModel().getColumn(i).getWidth();
        }
    }
    
    
    public void PushSessionsTableColumnsWidths()
    {
        this.sessionsTablecolumnsWidths = new int[this._frame.ServerSessionsjTable.getColumnModel().getColumnCount()];
        for (int i = 0; i < this._frame.ServerSessionsjTable.getColumnModel().getColumnCount();i++)
        {
            this.sessionsTablecolumnsWidths[i] = this._frame.ServerSessionsjTable.getColumnModel().getColumn(i).getWidth();
        }
    }

    public void PopSessionsTableColumnsWidths()
    {
        
        if ( this.sessionsTablecolumnsWidths == null || this.sessionsTablecolumnsWidths.length == 0 )
        {
            return;
        }
            
        for (int i = 0; i < this._frame.ServerSessionsjTable.getColumnModel().getColumnCount();i++)
        {
            this._frame.ServerSessionsjTable.getColumnModel().getColumn(i).setWidth(this.sessionsTablecolumnsWidths[i]);
        }
    }
        
    public void PushSelectedFields()
    {
        int[] fields = this._frame.FieldsTable.getSelectedRows();
        if ( fields == null || fields.length == 0  )
        {
            this.selectedFields = null;
        }
        else
        {
            this.selectedFields = new String[fields.length];
            DefaultTableModel m = (DefaultTableModel) this._frame.FieldsTable.getModel();
            int k = 0;
            for (int i : fields)
            {
                this.selectedFields[k++] = (String) m.getValueAt(i, 0);
            }
        }
    }
    

    
    public void PushUISettings()
    {

        
        this.CurrentSession = this._frame.CurrentSession;
        this.CurrentTable = this._frame.CurrentTable;
        // les split dividers locations
        this.WindowSplitDividerLocation = this._frame.MainWindowjSplitPane.getDividerLocation();
        this.ServerInternalSplitDividerLocation = this._frame.ServerInternaljSplitPane.getDividerLocation();
        this.SessionsTasksSplitDividerlocation = this._frame.SessionsTasksjSplitPane.getDividerLocation();

        this.PushTablesFrameSettings();
        this.PushTableContentFrameSettings();
        PushSessionsTableColumnsWidths();
        PushtasksTablesColumnsWidths();
        this.showTableContent =  this._frame.ContentjTable.getRowCount() != 0;
        // tables frame


       
        // on sauve l'etat de chaque fenetre
        
        JInternalFrame[] f =   this._frame.jDesktopPane.getAllFrames();
        for (JInternalFrame i : f)
        {
            if ( i.isSelected())
            {
                this.selectedFrame = i.getTitle();
            }
            if ( !frames.containsKey(i.getTitle()) )
            {
                   frames.put(i.getTitle(), new FrameSpecs() );
            }
            FrameSpecs specs = frames.get(i.getTitle());
            specs.location = i.getLocation();
            specs.size = i.getSize();
            specs.isIcon = i.isIcon();
            specs.isMaximized = i.isMaximum();
            specs.isSelected = i.isSelected();
        }

        pushed = true;
                
    }

    public void PopTasksTableColumnsWidths()
    {
        if ( tasksTablecolumnsWidths == null )
        {
            return;
        }
        for (int i = 0; i < tasksTablecolumnsWidths.length;i++)
        {
            this._frame.tasksjTable.getColumnModel().getColumn(i).setWidth(this.tasksTablecolumnsWidths[i]);
        }
        
    }
    
    
    public void PopUISettings()
    {

        if ( !pushed )
        {
            return;
        }
        //
        if ( !this.connector.getIsConnected() )
        {
            if (!this.connector.connect())
            {
                this._frame.Updatelog("Unable to connect to server '"+this.ip+":"+this.port+"'  \n");
                return;
            }
        }
        this._frame.CurrentTable = this.CurrentTable;
        
       // this.UpdatemARCStats();
       // this.UpdateTablesStats();
       // this.updateSessions();
      //  this.updateTasks();
        
        // les split divider locations
        this._frame.MainWindowjSplitPane.setDividerLocation(this.WindowSplitDividerLocation);
        this._frame.ServerInternaljSplitPane.setDividerLocation(this.ServerInternalSplitDividerLocation );
        this._frame.SessionsTasksjSplitPane.setDividerLocation(this.SessionsTasksSplitDividerlocation );

        // tables frame

        this._frame.UpdateTablesFrame();
        if ( this.CurrentTable != null && this.showFieldsTable )
        {
            this._frame.FieldsTable.clearSelection();
            int k = 0;
            for (String s : this.selectedFields)
            {
                if ( this.CurrentTable.FindTableFieldFromName(s) != null )
                {
                    this._frame.FieldsTable.addRowSelectionInterval(k, k);
                }
                k++;
            }
        }
        // table visualizer frame
        this._frame.UpdateTableContent(true);
        this._frame.ShowTableContentRowId();
        
        this.showTables = this._frame.TablesjTable.getRowCount() != 0;
       
        this.PopSessionsTableColumnsWidths();
        this.PopTasksTableColumnsWidths();
        
        
        // on restaure l'etat de chaque fenetre
        

           JInternalFrame[] fs = this._frame.jDesktopPane.getAllFrames();
           for( JInternalFrame f : fs)
           {
               String title = f.getTitle();
               if ( frames.containsKey(title))
               {
                   try {
                       FrameSpecs s = frames.get(title);
                       f.setLocation(s.location);
                       f.setSize(s.size);
                       f.setIcon(s.isIcon);
                       f.setMaximum(s.isMaximized);
                       f.setSelected(s.isSelected);
                   } catch (PropertyVetoException ex) {
                       Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                   }
               }
            
        }

                
    }
    
    public void PushTablesFrameSettings()
    {
      this.LDBTreesDividerLocation = this._frame.LDBTreesjSplitPane.getDividerLocation();
      this.tablesDividerLocation = this._frame.TablesjSplitPane.getDividerLocation();
      this.PushSelectedFields();
      this.PushFieldsTableColumnsWidths();

      this.PushBTreesTableColumnsWidths();
      this.PushKTreesTableColumnsWidths();
      this.PushLDBTableColumnsWidths();
      this.showTables = this._frame.TablesjTable.getRowCount() != 0;
      
    }
    
    public void PopTablesFrameSettings()
    {
        this._frame.LDBTreesjSplitPane.setDividerLocation(LDBTreesDividerLocation);
        this._frame.TablesjSplitPane.setDividerLocation(tablesDividerLocation);
        if ( this._frame.TablesjTable.getRowCount() != 0 && this.SelectedTableIndex != -1)
        {
            this._frame.TablesjTable.addRowSelectionInterval(this.SelectedTableIndex,this.SelectedTableIndex);
        }
        this.PopFieldsTableSelectedFields();
        this.PopFieldsTableColumnsWidths();
        this.PopBTreesTableColumnsWidths();
        this.PopKTreesTableColumnsWidths();
        this.PopLDBTableColumnsWidths();
    }
    public void PopFieldsTableSelectedFields()
    {
        if ( this.selectedFields != null && this._frame.FieldsTable.getRowCount() != 0)
        {
            DefaultTableModel m = (DefaultTableModel) this._frame.FieldsTable.getModel();
            this._frame.FieldsTable.clearSelection();
            for (int i = 0; i < m.getRowCount();i++ )
            {
                String f = (String) m.getValueAt(i, 0);
                for (String s : this.selectedFields)
                {
                    if ( s.equals(f))
                    {
                        this._frame.FieldsTable.addRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
        }

    }
    public void PopFieldsTableColumnsWidths()
    {
        if ( this.FieldsTableColumnsWidths == null )
        {
            return;
        }

        for (int i = 0; i < this.FieldsTableColumnsWidths.length;i++)
        {
            this._frame.FieldsTable.getColumnModel().getColumn(i).setWidth(this.FieldsTableColumnsWidths[i]);
        }
    }
    
    public void PushLDBTableColumnsWidths()
    {
        this.LDBTableColumnsWidths = new int[ this._frame.LastDBInfojTable.getColumnCount()];
        for (int i = 0; i < this._frame.LastDBInfojTable.getColumnCount();i++)
        {
            this.LDBTableColumnsWidths[i] = this._frame.LastDBInfojTable.getColumnModel().getColumn(i).getWidth();
        }
    }
    public void PopLDBTableColumnsWidths()
    {
        for (int i = 0; i < this._frame.LastDBInfojTable.getColumnCount();i++)
        {
            this._frame.LastDBInfojTable.getColumnModel().getColumn(i).setWidth(this.LDBTableColumnsWidths[i]); 
        }
    }
    public void PushKTreesTableColumnsWidths()
    {
        this.KTreesColumnsWidths = new int[this._frame.KTreesjTable.getColumnCount()];
        for (int i = 0; i < this.KTreesColumnsWidths.length;i++)
        {
            this.KTreesColumnsWidths[i] = this._frame.KTreesjTable.getColumnModel().getColumn(i).getWidth();
        }
    }
    
     public void PopKTreesTableColumnsWidths()
    {
        if ( this.KTreesColumnsWidths == null )
        {
            return;
        }

        for (int i = 0; i < this.KTreesColumnsWidths.length;i++)
        {
            this._frame.KTreesjTable.getColumnModel().getColumn(i).setWidth(this.KTreesColumnsWidths[i]);
        }
    }
public void PushBTreesTableColumnsWidths()
    {
        this.BTreesColumnsWidths = new int[this._frame.BTreejTable.getColumnCount()];
        for (int i = 0; i < this.BTreesColumnsWidths.length;i++)
        {
            this.BTreesColumnsWidths[i] = this._frame.BTreejTable.getColumnModel().getColumn(i).getWidth();
        }
    }
    
     public void PopBTreesTableColumnsWidths()
    {
        if ( this.BTreesColumnsWidths == null )
        {
            return;
        }

        for (int i = 0; i < this.KTreesColumnsWidths.length;i++)
        {
            this._frame.BTreejTable.getColumnModel().getColumn(i).setWidth(this.BTreesColumnsWidths[i]);
        }
    }    
    
       
       
       
    public boolean findFrameFromTitle(String title, FrameSpecs specs)
    {
        if( this.frames.containsKey(title) )
        {
            specs = this.frames.get(title);
            return true;
        }
        
        specs = null;
        return false;
    }

    /**
     * on se branche sur la session avec l'id id
     * @param id : id de session
     */
    public void GetSession(String id)
    {
        if ( !connector.getIsConnected() )
        {
            return;
        }
        connector.SessionId = id; 
        connector.setKmScriptSession(id);
    }
    public void SessionsToTree(JTree tree)
    {
        DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode rootNode =  (DefaultMutableTreeNode) tree.getModel().getRoot();
        DefaultMutableTreeNode serverNode = null, sessionNode = null, childsession = null;
        //find server in root nodes children
        String thisID = this.ip+":"+this.port;
        Server s = null;
        int i = 0;
        for ( i = 0; i< rootNode.getChildCount();i++ )
        {
            serverNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
            if ( (s = (Server) serverNode.getUserObject() ) instanceof Server)
            {
                if (thisID.equals(this.ip+":"+this.port))
                {
                    //find sessions node
                    break;
                }
            }
        }
        if ( i == rootNode.getChildCount() )
        {
            return;
        }
// on a trouve le server Node on cherche le session Node
        
        for ( i = 0; i < serverNode.getChildCount();i++)
        {
            sessionNode = (DefaultMutableTreeNode) serverNode.getChildAt(i);
            if ( sessionNode.getUserObject() instanceof String )
            {
                String sessionName = (String) sessionNode.getUserObject();
                if ( sessionName.equals("Sessions") )
                {
                    break;
                }
            }
        }
        if ( i == serverNode.getChildCount() )
        {
            return;
        }

        Session session = null;
        // on parcourt tous les enfants de "Sessions"
        i = 0;
        for ( ; i < sessionNode.getChildCount();i++)
        {
            childsession = (DefaultMutableTreeNode) sessionNode.getChildAt(i);
            session = this.FindSessionFromId( (String) childsession.getUserObject() );
            if ( session == null )
            {
                m.removeNodeFromParent(childsession);
                i = 0;
            }
        }
        
        synchronized(sessions)
        {
            for ( Session se : sessions)
            {
                i = 0;
                for ( ; i < sessionNode.getChildCount();i++)
                {
                    childsession = (DefaultMutableTreeNode) sessionNode.getChildAt(i);
                    String id = (String) childsession.getUserObject() ;
                    if (  id.equals(se.id) )
                    {
                            break;
                    }
                }
                if ( i == sessionNode.getChildCount())
                {
                    m.insertNodeInto(new DefaultMutableTreeNode(se.id,false), sessionNode, i);
                }
            }
        }
    }
    public void SessionsToTable(DefaultTableModel model)
    {
        synchronized(sessions)
        {
            model.setRowCount(0);
            
            if ( sessions.isEmpty() )
            {
            return ;
            }
            for (Session se : sessions )
            {
                model.addRow(new String[]{se.id,se.name,se.persistant,se.owner_ip,se.owner_port,se.priority,se.exec_timeout,se.session_timeout,se.debug});
            }
        }
    }
    
    public void TasksToTable(DefaultTableModel model)
    {
        model.setRowCount(0);
        synchronized(tasks)
        {
            if ( tasks.isEmpty() )
            {
                return;
            }

            for (Task se : tasks )
            {

                model.addRow(new String[]{se.task,se.completion,se.current,se.from,se.to,se.elapsed});
            }
        }
    }
    
    public void TablesToTable(JTable t )
    {
        DefaultTableModel m = (DefaultTableModel) t.getModel();

        synchronized(tables)
        {
            if ( tables.isEmpty() )
            {
                m.setRowCount(0);
                return;
            }

            // on regarde si le contenu a changé
            int count  = m.getRowCount();
            int cols = m.getColumnCount();
            if ( m.getRowCount() > 0 )
            {
                int found = 0;
                for (int i = 0; i < m.getRowCount();i++)
                {
                    String n = (String) m.getValueAt(i, 0);
                    if ( this.FindTableFromName(n) != null )
                    {
                        found ++;
                    }
                }

                if ( found == tables.size() )
                {
                    // tout est la
                    return;
                }
            }

            m.setRowCount(0);
            int selected = t.getSelectedRow();
            for (Table se : tables )
            {
                m.addRow( new String[]{se.name,se.lines});
            }

            if ( selected != -1)
            {
                t.changeSelection(selected, 0, true, false);
            }
        }
    } 
    
    public void TablesToTree( JTree theTree)
    {
        DefaultTreeModel m = (DefaultTreeModel) theTree.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) theTree.getModel().getRoot();
        // on cherche le server
        DefaultMutableTreeNode serverNode = null;
        for(int i = 0; i < rootNode.getChildCount();i++)
        {
           serverNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
           if ( (Server ) serverNode.getUserObject() == this )
           {
               // on recupere le tables node
               DefaultMutableTreeNode tablesNode = (DefaultMutableTreeNode)serverNode.getChildAt(1);
               String s;
               if ( (s = (String) tablesNode.getUserObject()) instanceof String )
               {
                   if (s.equals("Tables"))
                   {
                       DefaultMutableTreeNode tableNode;
                       
                       if ( tablesNode.getChildCount() == 0 )
                       {
                           int idx = 0;
                           synchronized(tables)
                           {
                               for(Table t  : this.tables)
                               {
                                    m.insertNodeInto(new DefaultMutableTreeNode(t.name,false), tablesNode, idx++);
                               //tablesNode.add( new DefaultMutableTreeNode(t.n,false) );
                               }
                           }
                           m.nodeChanged(tablesNode);
                           return;
                       }
                       for (int k = 0; k < tablesNode.getChildCount();k++)
                       {
                           tableNode = (DefaultMutableTreeNode)tablesNode.getChildAt(k);
                           String tableName = (String) tableNode.getUserObject();
                           Table t = this.FindTableFromName(tableName);
                           if ( t == null  )
                           {
                               m.removeNodeFromParent((DefaultMutableTreeNode) tablesNode.getChildAt(k) );
                               k = 0;
                           }
                       }
                       
                       for (Table t : this.tables)
                       {
                           String n = t.name;
                           int k =0;
                            for (; k < tablesNode.getChildCount();k++)
                            {
                                tableNode = (DefaultMutableTreeNode)tablesNode.getChildAt(k);
                                String tableName = (String) tableNode.getUserObject();
                                if ( tableName.equals(n))
                                {
                                    break;
                                }
                            }
                            if ( k == tablesNode.getChildCount() )
                            {
                                m.insertNodeInto(new DefaultMutableTreeNode(n,false), tablesNode, k);
                            }
                       }
                       m.nodeChanged(tablesNode);
                   }
               }
           }
        }
           
        
    }
    public void BIndexesToTable(DefaultTableModel model)
    {
        model.setRowCount(0);
        
        synchronized(tables)
        {
            if ( tables.isEmpty() )
            {
                return;
            }


            for (Table se : tables )
            {
                for (BIndex b : se.bIndexes)
                {
                    model.addRow( new String[]{b.name,b.status, b.progress});
                }
            }
        }
    } 
    
        public void BIndexesToTree( JTree theTree)
    {
        DefaultTreeModel m = (DefaultTreeModel) theTree.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) theTree.getModel().getRoot();
        // on cherche le server
        DefaultMutableTreeNode serverNode = null;
        for(int i = 0; i < rootNode.getChildCount();i++)
        {
           serverNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
           if ( (Server ) serverNode.getUserObject() == this )
           {
               // on recupere le tables node
               DefaultMutableTreeNode btreesNode = (DefaultMutableTreeNode)serverNode.getChildAt(4);
               String s;
               if ( (s = (String) btreesNode.getUserObject()) instanceof String )
               {
                   if (s.equals("B-Trees"))
                   {    
                       if ( btreesNode.getChildCount() == 0 )
                       {
                           synchronized(tables)
                           {
                               for(Table t  : this.tables)
                                {
                                    for (BIndex b : t.bIndexes)
                                    {
                                         m.insertNodeInto(new DefaultMutableTreeNode(b.name,false), btreesNode, btreesNode.getChildCount());
                                    }
                                }
                           }
                           m.nodeChanged(btreesNode);
                           return;
                       }
                       DefaultMutableTreeNode btreeNode;
                       for (int k = 0; k < btreesNode.getChildCount();k++)
                       {
                           btreeNode = (DefaultMutableTreeNode)btreesNode.getChildAt(k);
                           String btreeS = (String) btreeNode.getUserObject();
                           boolean found = false;
                           for ( Table t: this.tables)
                           {
                               for ( BIndex b : t.bIndexes)
                               {
                                   if ( btreeS.equals(b.name))
                                   {
                                       found = true;
                                       break;
                                   }
                               }
                           if ( found )
                           {
                                break;
                           }
                           }
                           if ( !found)
                           {
                               m.removeNodeFromParent(btreeNode);
                               k = 0;
                           }
                       }
                       
                       for (Table t : this.tables)
                       {
                           for ( BIndex b : t.bIndexes)
                           {
                               String name = b.name;
                               int k =0;
                            for (; k < btreesNode.getChildCount();k++)
                            {
                                btreeNode = (DefaultMutableTreeNode) btreesNode.getChildAt(k);
                                s = (String) btreeNode.getUserObject();
                                if ( s.equals(name))
                                {
                                    break;
                                }
                            }
                            if ( k == btreesNode.getChildCount() )
                            {
                                m.insertNodeInto(new DefaultMutableTreeNode(name,false), btreesNode, k);
                            }
                           }
                       }
                       
                       
                     //  m.nodeStructureChanged(rootNode);
                       m.nodeChanged(btreesNode);
                       
                   }
               }
           }
        }
           
        
    }
 
    public void KIndexesToTable(DefaultTableModel model)
    {
        model.setRowCount(0);
        if ( tables.isEmpty() )
        {
            return;
        }


        for (Table se : tables )
        {
            for (KIndex b : se.kIndexes)
            {
                model.addRow( new String[]{b.name,b.status, b.progress});
            }
        }
    } 
    
    public void KIndexesToTree( JTree theTree)
    {
        DefaultTreeModel m = (DefaultTreeModel) theTree.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) theTree.getModel().getRoot();
        // on cherche le server
        DefaultMutableTreeNode serverNode = null;
        for(int i = 0; i < rootNode.getChildCount();i++)
        {
           serverNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
           if ( (Server ) serverNode.getUserObject() == this )
           {
               // on recupere le tables node
               DefaultMutableTreeNode btreesNode = (DefaultMutableTreeNode)serverNode.getChildAt(3);
               String s;
               if ( (s = (String) btreesNode.getUserObject()) instanceof String )
               {
                   if (s.equals("K-Trees"))
                   {    
                       if ( btreesNode.getChildCount() == 0 )
                       {
                           for(Table t  : this.tables)
                           {
                               for (KIndex b : t.kIndexes)
                               {
                                    m.insertNodeInto(new DefaultMutableTreeNode(b.name,false), btreesNode, btreesNode.getChildCount());
                               }
                           }
                           m.nodeChanged(btreesNode);
                           return;
                       }
                       DefaultMutableTreeNode btreeNode;
                       for (int k = 0; k < btreesNode.getChildCount();k++)
                       {
                           btreeNode = (DefaultMutableTreeNode)btreesNode.getChildAt(k);
                           String btreeS = (String) btreeNode.getUserObject();
                           boolean found = false;
                           for ( Table t: this.tables)
                           {
                               for ( KIndex b : t.kIndexes)
                               {
                                   if ( btreeS.equals(b.name))
                                   {
                                       found = true;
                                       break;
                                   }
                               }
                               if ( found )
                               {
                                    break;
                               }
                           }
                           if ( !found)
                           {
                               m.removeNodeFromParent(btreeNode);
                               k = 0;
                           }
                       }
                       
                       for (Table t : this.tables)
                       {
                           for ( KIndex b : t.kIndexes)
                           {
                               String name = b.name;
                               int k =0;
                            for (; k < btreesNode.getChildCount();k++)
                            {
                                btreeNode = (DefaultMutableTreeNode) btreesNode.getChildAt(k);
                                s = (String) btreeNode.getUserObject();
                                if ( s.equals(name))
                                {
                                    break;
                                }
                            }
                            if ( k == btreesNode.getChildCount() )
                            {
                                m.insertNodeInto(new DefaultMutableTreeNode(name,false), btreesNode, k);
                            }
                           }
                       }
                       
                       
                     //  m.nodeStructureChanged(rootNode);
                       m.nodeChanged(btreesNode);
                       
                   }
               }
           }
        }
           
        
    }
    public int getindexOfSession(Session se)
    {
        int index = 0;
        for (Session s : sessions)
        {
            if ( s == se)
            {
                return index;
            }
            index ++;
        }
        return -1;
    }
    public static String GetASessionName()
    {
        if (sessionsNamesStock.isEmpty() )
        {
            return SessionName+"#"+Integer.toString(SessionId ++);
        }
        
        String theSessionName = sessionsNamesStock.get(sessionsNamesStock.size() - 1);
        
        sessionsNamesStock.remove(theSessionName);
        
        return theSessionName;
        
    }
    
    public static void ReleaseASessionName(String theName)
    {
        if ( sessionsNamesStock.contains(theName) )
        {
            return;
        }
        sessionsNamesStock.add(theName);
        
    }
            
    public void update()
    {
        //on se plug sur la session principale du server
        this.GetSession(Session_Id);
        updateStats();
        UpdatemARCStats();
        UpdateTablesStats();
        updateSessions();
        updateTasks();
    }
    
       public void updateTasks()
    {
        connector.directExecute = true;
        connector.openScript(null);
        connector.SERVER_GetTasks();
        String[] task = connector.getDataByName("task", -1);
        if ( task == null || task.length == 0)
        {
            tasks.clear();
            return;
        }
        
        String[] completions = connector.getDataByName("completion", -1);
        String[] currents = connector.getDataByName("current", -1);
        String[] froms = connector.getDataByName("from", -1);
        String[] tos = connector.getDataByName("to", -1);
        String[] elapseds = connector.getDataByName("elapsed", -1);

        
        for (Task t : tasks)
        {
          t.toUpdate = false;
        }
        
        int i = 0;
        for( String n : task)
        {
            Task t = FindTaskFromName(n);
            if ( t == null )
            {
                t = new Task();
                tasks.add(t);
                t.task = n;
            }
            t.completion = completions[i];
            t.current = currents[i];
            t.from = froms[i];
            t.to = tos[i];
            t.elapsed = elapseds[i++];
            t.toUpdate = true; 
        }
        
         for (Task t : tasks)
        {
          if ( !t.toUpdate)
          {
              tasksToRemove.add(t);
          }
        }
         
         tasks.removeAll(tasksToRemove);
         tasksToRemove.clear();
    }
    
    public void RemoveASessionFromId(String id)
    {
        Session se = FindSessionFromId(id);
        if ( se == null )
        {
            return;
        }
        sessions.remove(se);
    }
    public boolean AddASessionFromGetInstances(String[] properties, Connector connector)
    {
        if ( this.Session_Id.equals(properties[0]) )
        {
            return false;
        }
         if (properties == null || properties.length < 9)
        {
            return false;
        }
        
        Session session = FindSessionFromId(properties[0]);
        if ( session != null )
        {
            return true;
        }
        session = new Session(); 
        session.connector = connector;
        session.owned = true;
        session.owner = this;
        session.id = properties[0];
        session.name = properties[1];
        session.persistant = properties[2];
        session.owner_ip = properties[3];
        session.owner_port = properties[4];
        session.priority =  properties[5];
        session.exec_timeout = properties[6];
        session.session_timeout = properties[7];
        session.debug = properties[8];
        session.PushUISettings();
        synchronized(sessions)
        {
            sessions.add(session);
        }
        return true;
    }
    
               /*  session properties : 
    n
last_time
owner_IP
owner_port
id
priority
session_timeout
exec_timeout
context_count
result_count
spectrum_string
profiler_context_string
inhibitor_context_string
result_max_stack_size
result_embedded
result_line_separator
result_column_separator
result_DBCursor

    */
    public  boolean AddASessionFromValues(String[] properties_values, String[] spectrum_values, Connector connector)
    {
        if ( this.Session_Id.equals(properties_values[4]) )
        {
            return false;
        }
        Session session = FindSessionFromId(properties_values[4]);
        if ( session != null )
        {
            if ( properties_values != null)
            {
                session.values = properties_values;
            }
            if ( spectrum_values != null )
            {
                session.spectrum_values = spectrum_values;
            }
            return false;
        }
        session = new Session(); 
        session.owned = true;
        session.owner = this;
        if ( properties_values != null )
        {
            session.values = properties_values;
        }
        session.id = connector.SessionId;
        session.connector = connector;
        if(spectrum_values != null)  
        {
            session.spectrum_values = spectrum_values;
        }
        if ( !this._frame.loadingSettings )
        {
            session.PushUISettings();
        }
        sessions.add(session);
        return true;
    }        
    
    public void RemoveASession( String id )
    {
       Session session = FindSessionFromId(id);
        if ( session != null )
        {
            sessions.remove(session);
        }
    }
    public void updateSessions()
    {
        connector.directExecute = true;
        connector.openScript(null);
        connector.SESSION_GetInstances("1", "-1");
        String[] ids = connector.getDataByName("id", -1);
        if ( ids == null || ids.length == 0)
        {
            sessions.clear();
            return;
        }
        
        String[] names = connector.getDataByName("name", -1);
        String[] persistant = connector.getDataByName("persistant", -1);
        String[] owner_ip = connector.getDataByName("owner_ip", -1);
        String[] owner_port = connector.getDataByName("owner_port", -1);
        String[] priority = connector.getDataByName("priority", -1);
        String[] exec_timeout = connector.getDataByName("exec_timeout", -1);
        String[] session_timeout = connector.getDataByName("session_timeout", -1);
        String[] debug = connector.getDataByName("debug", -1);
        for (Session se : sessions)
        {
          se.toUpdate = false;
        }

        ServerSession.toUpdate = true;
        toName.clear();
        
        int i = 0;
        for( String n : ids)
        {
            if ( n.equals(this.Session_Id) )
            {
                i++;
                continue;
            }
            Session se = FindSessionFromId(n);
            if ( se == null )
            {
                se = new Session();
                se.connector = this.connector;
                se.owned = false;
                se.owner = this;
                se.connector.setIp(this.connector.getIp());
                se.connector.setPort(this.connector.getPort());
                // on enregistre la session
                this.connector.AddASessionId(n);
                //se.connector.connect();
                se.id = n;
                sessions.add(se);
            }
            se.name = names[i];
            /*
            if ( se.n == null || se.n.isEmpty() )
            {
               se.n = GetASessionName();
               toName.add(se);
            }
             */
            se.owner_ip = owner_ip[i];
            se.owner_port = owner_port[i];
            se.persistant = persistant[i];
            se.exec_timeout = exec_timeout[i];
            se.session_timeout = session_timeout[i];
            se.priority = priority[i];
            se.debug = debug[i++];
            se.toUpdate = true; 
        }
        
        for (Session se : sessions)
        {
          if ( !se.toUpdate)
          {
              // si la session n'est pas attachee au serveur
              // on deconnecte la session du serveur
              if ( !this.connector.FindASessionId(se.id) )
              {
                  se.connector.disConnect();
              }
              sessionsToRemove.add(se);
              if ( se.name.startsWith("Session#"))
              {
                  sessionsNamesStock.add(se.name);
              }
              this.connector.RemoveASessionId(se.id);
          }
        }
         
         sessions.removeAll(sessionsToRemove);
         sessionsToRemove.clear();
         
         if ( !toName.isEmpty() )
         {
             for (Session se : toName )
             {
                 // si la session partage le connecteur du serveur
                 // on se plug sur la session : IMPORTANT
                 if ( this.connector.FindASessionId(se.id) )
                 {
                     GetSession(se.id);
                 }
                 se.connector.directExecute = true;
                 se.connector.SESSION_SetProperties(new String[]{"name = "+se.name});
             }
             
             // on se replugge sur la session principale
             
             GetSession(this.Session_Id);
         }
         
    }
    public void updateStats()
    {
        connector.directExecute = true;
        connector.openScript(null);
        connector.SERVER_GetProperties("name;port;type;model;version;build;connection_count;command_threads;time_local;time_gmt;up_time;idle_time;cache_size;cache_used;indexation_cache_size;indexation_cache_used;cache_hits;exec_timeout_default;session_timeout_default;indexation_timeout");
        String[] props = connector.getDataByName("prop_value", -1);
        name = props[0];
        type = props[2];
        model= props[3];
        version = props[4];
        build= props[5];
        connection_count= props[6];
        command_threads= props[7];
        time_local= props[8];
        time_gmt= props[9];
        up_time= props[10];
        idle_time= props[11];
        cache_size= props[12];
        cache_used = props[13];
        indexation_cache_size= props[14];
        indexation_cache_used = props[15];
        cache_hits= props[16];
        exec_timeout_default= props[17];
        session_timeout_default= props[18];
        this.indexation_timeout = props[19];
    }
    public void UpdatemARCStats()
    {
        connector.directExecute = true;
        connector.openScript(null);
        connector.SERVER_GetProperties("marc_relations;marc_shapes;marc_references;marc_particles;marc_quality");
        String[] props = connector.getDataByName("prop_value", -1);
        _marc.relations = props[0];
        _marc.shapes = props[1];
        _marc.references  =props[2];
        _marc.particles = props[3];
        _marc.quality = props[4];
        
    }
  
     public Task FindTaskFromName(String n)
    {

        for (Task t : tasks)
        {
            if ( t.task.equals(n))
            {
                return t;
            }
        }
        
        return null;
    } 
     
    public int FindSessionIndex(Session session)
    {
        if ( session == null )
        {
            return -1;
        }
        int i = 0;
        for (Session t : sessions)
        {
            if ( t == session)
            {
                return i;
            }
            i++;
        }
        
        return -1;
    } 
       public Session FindSessionFromId(String n)
    {
        for (Session t : sessions)
        {
            if ( t.id.equals(n))
            {
                return t;
            }
        }
        
        return null;
    } 
    
    public Table FindTableFromName(String n)
    {
        for (Table t : tables)
        {
            if ( t.name.equals(n))
            {
                return t;
            }
        }
        
        return null;
    }
    

    public void UpdateTablesStats()
    {
        connector.directExecute = true;
        connector.openScript(null);
        connector.TABLE_GetInstances("1", "-1");
        String[] tbls = connector.getDataByName("tables", -1);
        
        
        connector.SESSION_GetLastDBInfo(); // on recupere les operations en cours sur les tables
        
        String[] LastDBInfoTable = connector.getDataByName("table", 0);
        String[] LastDBInfoOperation = connector.getDataByName("operation", 0);
        String[] LastDBInfoId = connector.getDataByName("id", 0);
        String[] LastDBInfoStatus = connector.getDataByName("status", 0);
        if ( LastDBInfoTable !=null && LastDBInfoTable.length != 0 )
        {    
            lastDBInfoTableName =   LastDBInfoTable[0];    
            lastDBInfoOperation = LastDBInfoOperation[0];
            lastDBInfoId     = LastDBInfoId[0];
            lastDBInfoStatus = LastDBInfoStatus[0];
        }        
        else
        {
            lastDBInfoTableName =   "none";    
            lastDBInfoOperation =  "none"; 
            lastDBInfoId     =  "none"; 
            lastDBInfoStatus =  "none"; 
            
        }
        
        synchronized(tables)
        {
            if ( tbls == null || tbls.length == 0)
            {
                tables.clear();
                return;
            }
            for (Table t : tables)
            {
              t.toUpdate = false;
            }

            Table tb = null;
            for ( String t : tbls )
            {
                tb = FindTableFromName(t);
                if ( tb == null )
                {
                    tb = new Table();
                    tb._server = this;
                    tb.name = t;
                    tables.add(tb);
                }
                tb.toUpdate = true;

            }        

            connector.directExecute = false;
            for ( Table t : tables)
            {
                if ( !t.toUpdate )
                {
                    toRemove.add(t);
                    continue;
                }
                connector.openScript(null);
                connector.TABLE_GetStructure(t.name);
                connector.TABLE_GetLines(t.name);
                connector.TABLE_GetBIndexes(t.name);
                connector.TABLE_GetKIndexes(t.name);
                connector.executeScript();
                String[] names = connector.getDataByName("name", 0);
                String[] types = connector.getDataByName("type", 0);
                String[] sizes = connector.getDataByName("size", 0);


                String[] allNames = new String[names.length+1];
                String[] allTypes = new String[ types.length+1];
                String[] allSizes = new String[sizes.length+1];
                allNames[0]="RowId";
                allTypes[0] = "INT32";
                allSizes[0] = "0";
                for ( int i = 0; i <names.length;i++)
                {
                    allNames[i+1] = names[i];
                    allTypes[i+1] = types[i];
                    allSizes[i+1] = sizes[i];
                }

                t.UpdateStructure(allNames, allTypes, allSizes);

                String[] lines = connector.getDataByName("lines", 1);
                t.lines = lines[0];

                names = connector.getDataByName("index_name", 2);
                String[] status = connector.getDataByName("status", 2);
                String[] progress = connector.getDataByName("progress", 2);
                String[] unique = connector.getDataByName("unique", 2);
                t.UpdateBIndexes(names, status, progress, unique);

                names = connector.getDataByName("index_name", 3);
                status = connector.getDataByName("status", 3);
                progress = connector.getDataByName("progress", 3);
                t.UpdateKIndexes(names, status, progress);


            }

            // on enleve celles qui n'ont pas de toUpdate = true
            tables.removeAll(toRemove);
            toRemove.clear();
        }
    }
}
