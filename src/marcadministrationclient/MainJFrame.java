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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.*;
import javax.xml.transform.TransformerException;
import mARC.Connector.*;

/**
 *
 * @author patrice
 */
public class MainJFrame extends javax.swing.JFrame {

    boolean firstTime = true; // permet de ne pas déclencher le timer de maj du panneau "General"
    boolean firstTimeToDisplaySessionjPanel = true;
    
    public boolean loadingSettings;
    
    ServersTreePopUpMenu serversTreePopUpMenu;
    TablesTreePopUpMenu tablesPopUpMenu;
    TableKillTreePopUpMenu tableKillPopUpMenu;
    TablesBIndexesTreePopUpMenu tablesBIndexesTreePopUpMenu;
    TablesKIndexesTreePopUpMenu tablesKIndexesTreePopUpMenu;
    SessionsAddPopUpMenu sessionsAddPopUpMenu;
    SessionRemovePopUpMenu sessionRemovePopUpMenu;
    TableContentPopUpMenu tableContentPopUpMenu;
    ContextsStackContentPopUpMenu contextsStackContentPopUpMenu;
    ResultsStackContentPopUpMenu resultsStackContentPopUpMenu;
    ResultSetContentPopUpMenu resultSetContentPopUpMenu;
    mARCTreePopUpMenu marcPopUpMenu;
    
    JDesktopPane activeDesktop;

    final static List<Server> servers = Collections.synchronizedList(new ArrayList<Server>());
    DefaultTreeModel _listModel;
    DefaultMutableTreeNode rootNode;
    MyTreeCellRenderer renderer = new MyTreeCellRenderer();
    private TreePath currentServersTreeSelectedPath;

    mARCListener serverUpdaterActionListener;

    UpdateKnowledgeGraphActionListener updateKnowledgeGraphActionListener;

    javax.swing.Timer timerToUpdateServerStats;

    javax.swing.Timer timerToUpdateKnowledgeGraph;

    Server CurrentServer;
    Table CurrentTable;
    Session CurrentSession;
    Context CurrentContext;
    ResultSet CurrentResultSet;
    int CurrentRSIndex;
    
    String shownTableContentRowId;
    
    UpdateServerWorker updateServerWorker;

    int CurrentSessionPropertiesjTable_EditingRow;
    int CurrentSessionPropertiesjTable_EditingColumn;
    public int firstTableContentVisibleRow ;
    public int lastTableContentVisibleRow ;
    //pour sauver l'etat du serveur
    int shownRSIndex;
    int ShownContextIndex;
    
    KnowLedgeGraph theGraph;

    /**
     * Creates new form MainJFrame
     */
    public MainJFrame() {
        this.loadingSettings = false;
        this.shownTableContentRowId = "-1";
        this.resultSetContentPopUpMenu = new ResultSetContentPopUpMenu();
        this.CurrentRSIndex = -1;
        this.resultsStackContentPopUpMenu = new ResultsStackContentPopUpMenu();
        this.marcPopUpMenu = new mARCTreePopUpMenu();
        this.ShownContextIndex = -1;
        this.contextsStackContentPopUpMenu = new ContextsStackContentPopUpMenu();
        this.tableContentPopUpMenu = new TableContentPopUpMenu();
        this.shownRSIndex = -1;
        this.sessionRemovePopUpMenu = new SessionRemovePopUpMenu();
        this.sessionsAddPopUpMenu = new SessionsAddPopUpMenu();
        this.activeDesktop = null;
        this.tableKillPopUpMenu = new TableKillTreePopUpMenu();
        this.tablesKIndexesTreePopUpMenu = new TablesKIndexesTreePopUpMenu();
        this.tablesBIndexesTreePopUpMenu = new TablesBIndexesTreePopUpMenu();
        this.tablesPopUpMenu = new TablesTreePopUpMenu();
        this.serversTreePopUpMenu = new ServersTreePopUpMenu();

        this.CurrentResultSet = null;
        this.CurrentContext = null;
        this.CurrentSessionPropertiesjTable_EditingRow = -1;
        this.CurrentSessionPropertiesjTable_EditingColumn = -1;
        this.CurrentSession = null;

        this.CurrentTable = null;
        this.CurrentServer = null;

        mARCAdministrationClient.splashProgress(0);
        initComponents();

        MyPropertyChangeListener lst = new MyPropertyChangeListener();
        lst.frame = this;
        this.MainWindowjSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, 
              (PropertyChangeListener)  lst);
        //pour ecouter le scrolling de la tableContent et sauver le visiblerect
        MyAdjustmentListener listener = new MyAdjustmentListener();
        listener.owner = this.ContentjTable;
        listener.frame = this;
       // this.TableContentjScrollPane.getVerticalScrollBar().addAdjustmentListener(listener);
        this.TableContentjScrollPane.getVerticalScrollBar().addMouseListener((new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TableContentjScrollPaneMouseReleased(evt);
            }
        }));
        
        
        this.ServerSessionsjTable.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
            
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, 
                    int row, int column) 
            {
            JLabel lbl = new JLabel();
            lbl.setOpaque(true);
            lbl.setBackground(Color.BLACK);
            lbl.setForeground(new Color(51,255,0));
            lbl.setText((String) value);
            lbl.setBorder(BorderFactory.createCompoundBorder(lbl.getBorder(), 
                      BorderFactory.createEmptyBorder(0, 5, 0, 0)));
                return lbl;
            }
        });
        this.tasksjTable.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
            
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, 
                    int row, int column) 
            {
            JLabel lbl = new JLabel();
            lbl.setOpaque(true);
            lbl.setBackground(Color.BLACK);
            lbl.setForeground(new Color(51,255,0));
            lbl.setText((String) value);
                return lbl;
            }
        });
        
        

this.ServerSessionsjTable.setShowHorizontalLines(true);
this.ServerSessionsjTable.setShowVerticalLines(true);

this.tasksjTable.setShowHorizontalLines(true);
this.tasksjTable.setShowVerticalLines(true);

        //this.SessionPropertiesjPanel.setVisible(false);
        this.theGraph = new KnowLedgeGraph();
        //this.KnowLedgeGraphjPanel.add(this.theGraph);
        this.theGraph.setLocation(50, 150);
        this.theGraph.setSize(1000, 1000);
        this.KnowledgeGraphjPanel.add(this.theGraph);
        this.validate();

        this.theGraph.setVisible(true);
        this.pack();
        this.setVisible(true);

        System.out.println(this.theGraph.getLocation());

        mARCAdministrationClient.splashProgress(50);

        this.IndexationCacheProgressBar.setStringPainted(true);

        TablesjTable.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 9));

        LastDBInfojTable.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 9));
        KTreesjTable.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 9));
        FieldsTable.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 9));
        BTreejTable.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 9));
        tasksjTable.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 9));
        //FieldsjTable.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 9));
        //ContentjTable.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 9));

        ServersTree.setModel(null);
        rootNode = new DefaultMutableTreeNode("Servers");
        _listModel = new DefaultTreeModel(rootNode, false);
        ServersTree.setModel(_listModel);
        renderer.setEnabled(true);
        serversTreePopUpMenu.setVisible(false);
        ServersTree.setCellRenderer(renderer);

        this.serverUpdaterActionListener = new mARCListener();
        this.serverUpdaterActionListener.frame = this;

        this.updateServerWorker = null;
        this.updateKnowledgeGraphActionListener = new UpdateKnowledgeGraphActionListener();
        this.updateKnowledgeGraphActionListener.frame = this;

        this.timerToUpdateServerStats = new javax.swing.Timer(1000, serverUpdaterActionListener);

        this.timerToUpdateKnowledgeGraph = new javax.swing.Timer(100, this.updateKnowledgeGraphActionListener);
       
        mARCAdministrationClient.splashProgress(100);
    }

    public mARCAdministrationClient client;

    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        ServerCurrentSessionjInternalFrame = new javax.swing.JInternalFrame();
        jScrollPane9 = new javax.swing.JScrollPane();
        jPanel8 = new javax.swing.JPanel();
        SessionPropertiesSpectrumjSplitPane = new javax.swing.JSplitPane();
        SessionPropertiesjPanel = new javax.swing.JPanel();
        SessionPropertiesjTable = new javax.swing.JTable();
        jPanel30 = new javax.swing.JPanel();
        SessionSpectrumjTable = new javax.swing.JTable();
        ClearSessionjButton = new javax.swing.JButton();
        TablesjInternalFrame = new javax.swing.JInternalFrame();
        jScrollPane13 = new javax.swing.JScrollPane();
        TablesjSplitPane = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        TablesPanel = new javax.swing.JPanel();
        jSplitPane5 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TablesjTable = new javax.swing.JTable();
        TableStructurePanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        FieldsTable = new javax.swing.JTable();
        LDBTreesjSplitPane = new javax.swing.JSplitPane();
        jSplitPane18 = new javax.swing.JSplitPane();
        BTreesPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        BTreejTable = new javax.swing.JTable();
        KTreePanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        KTreesjTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        LastDBInfojTable = new javax.swing.JTable();
        TableVisualjInternalFrame = new javax.swing.JInternalFrame();
        jScrollPane14 = new javax.swing.JScrollPane();
        jPanel29 = new javax.swing.JPanel();
        TableContentjSplitPane = new javax.swing.JSplitPane();
        jPanel44 = new javax.swing.JPanel();
        jPanel33 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        SizejTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        StartjTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        TableLinesjSlider = new javax.swing.JSlider();
        TableContentTextAreajSplitPane = new javax.swing.JSplitPane();
        ContentTablejTextArea = new javax.swing.JTextArea();
        TableContentjScrollPane = new javax.swing.JScrollPane();
        ContentjTable = new javax.swing.JTable();
        jPanelMainWindow = new javax.swing.JPanel();
        jScrollPane15 = new javax.swing.JScrollPane();
        jPanel31 = new javax.swing.JPanel();
        MainWindowjSplitPane = new javax.swing.JSplitPane();
        CurrentServerjPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        CurrentServerjTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        CurrentTablejTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        CurrentSessionjTextField = new javax.swing.JTextField();
        ServerInternaljSplitPane = new javax.swing.JSplitPane();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane17 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        TimePanel = new javax.swing.JPanel();
        UpTimeLabel = new javax.swing.JLabel();
        UpTimeTextField = new javax.swing.JTextField();
        GMTimeLabel = new javax.swing.JLabel();
        GMTimeTextField = new javax.swing.JTextField();
        IdleTimeLabel = new javax.swing.JLabel();
        IdleTimeTextField = new javax.swing.JTextField();
        MTPanel = new javax.swing.JPanel();
        CTLabel = new javax.swing.JLabel();
        CommandThreadsTextField = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        CacheSizeTextField = new javax.swing.JTextField();
        ExecTimeoutDefaultTextField = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        SessionTimeoutDefaultTextField = new javax.swing.JTextField();
        ServerIdPanel = new javax.swing.JPanel();
        ServerNameLabel = new javax.swing.JLabel();
        ServerNameTextField = new javax.swing.JTextField();
        ModelLabel = new javax.swing.JLabel();
        ModelTextField = new javax.swing.JTextField();
        VersionLabel = new javax.swing.JLabel();
        VersionTextField = new javax.swing.JTextField();
        BuildLabel = new javax.swing.JLabel();
        BuildTextField = new javax.swing.JTextField();
        PortLabel = new javax.swing.JLabel();
        PortTextField = new javax.swing.JTextField();
        IPLabel = new javax.swing.JLabel();
        IPTextField = new javax.swing.JTextField();
        ConnectCountLabel = new javax.swing.JLabel();
        ConnectCountjTextField = new javax.swing.JTextField();
        mARCPanel = new javax.swing.JPanel();
        mARCRebuildFromjTextField = new javax.swing.JTextField();
        mARCRebuildTojTextField = new javax.swing.JTextField();
        RebuildRefjCheckBox = new javax.swing.JCheckBox();
        particlesjLabel = new javax.swing.JLabel();
        particlesjTextField = new javax.swing.JTextField();
        shapesjLabel = new javax.swing.JLabel();
        shapesjTextField = new javax.swing.JTextField();
        relationsjLabel = new javax.swing.JLabel();
        relationsjTextField = new javax.swing.JTextField();
        referencesjLabel = new javax.swing.JLabel();
        referencesjTextField = new javax.swing.JTextField();
        mARCRebuildFieldsjTextField = new javax.swing.JTextField();
        restartjCheckBox = new javax.swing.JCheckBox();
        IndexationPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        IndexationCacheSizeTextField = new javax.swing.JTextField();
        CacheUsedLabel = new javax.swing.JLabel();
        IndexationCacheUsedTextField = new javax.swing.JTextField();
        IndexationCacheProgressBar = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        CacheHitsTextField = new javax.swing.JTextField();
        PercentCacheHitsLabel = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        IndexationTimeoutTextField = new javax.swing.JTextField();
        SessionsTasksjSplitPane = new javax.swing.JSplitPane();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane16 = new javax.swing.JScrollPane();
        ServerSessionsjPanel = new javax.swing.JPanel();
        jScrollPane23 = new javax.swing.JScrollPane();
        ServerSessionsjTable = new javax.swing.JTable(){

        };
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        TasksjPanel = new javax.swing.JPanel();
        jScrollPane33 = new javax.swing.JScrollPane();
        tasksjTable = new javax.swing.JTable();
        jPanel46 = new javax.swing.JPanel();
        jScrollPane22 = new javax.swing.JScrollPane();
        jDesktopPane = new javax.swing.JDesktopPane(){

            BufferedImage img = null;

            @Override
            protected void paintComponent(Graphics grphcs)
            {
                super.paintComponent(grphcs);

                if ( img == null)
                {   try
                    {
                        img = ImageIO.read(this.getClass().getResourceAsStream("voyager.jpg"));
                    }
                    catch(IOException e)
                    {
                        System.out.println(e.getMessage() );
                    }
                }
                grphcs.drawImage(img, 0, 0, null);
            }
        };
        ResultsjInternalFrame = new javax.swing.JInternalFrame();
        jScrollPane30 = new javax.swing.JScrollPane();
        RSstackjSplitPane = new javax.swing.JSplitPane();
        jScrollPane27 = new javax.swing.JScrollPane();
        jPanel11 = new javax.swing.JPanel();
        jPanel39 = new javax.swing.JPanel();
        ResultsAmplifySlopejTextField = new javax.swing.JTextField();
        ResultsAmplifyBjTextField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel43 = new javax.swing.JPanel();
        ResultsNormalizejComboBox = new javax.swing.JComboBox();
        ResultsDeleteByjPanel = new javax.swing.JPanel();
        ResultsDeleteByOrderjComboBox = new javax.swing.JComboBox();
        ResultsDeleteByFieldjComboBox = new javax.swing.JComboBox();
        Operand2ResultsDeleteByjTextField = new javax.swing.JTextField();
        operand1ResultsDeleteByjTextField = new javax.swing.JTextField();
        ResultsUniqueByjPanel = new javax.swing.JPanel();
        ResultsUniqueByFieldjComboBox = new javax.swing.JComboBox();
        SelectToTablejPanel = new javax.swing.JPanel();
        SelectToTableFieldjComboBox = new javax.swing.JComboBox();
        selectToTableDestinationTableNamejComboBox = new javax.swing.JComboBox();
        SelectToTableUniquejCheckBox = new javax.swing.JCheckBox();
        ResultsSelectByjPanel = new javax.swing.JPanel();
        ResultsSelectByOperatorjComboBox = new javax.swing.JComboBox();
        ResultsSelectByFieldjComboBox = new javax.swing.JComboBox();
        operand1ResultsSelectByjTextField = new javax.swing.JTextField();
        Operand2ResultsSelectByjTextField = new javax.swing.JTextField();
        ResultsSortByjPanel = new javax.swing.JPanel();
        ResultsSortByFieldComboBox = new javax.swing.JComboBox();
        ResultsSortByOrderjComboBox = new javax.swing.JComboBox();
        SelectFromTablejPanel = new javax.swing.JPanel();
        SelectFromTableModejComboBox = new javax.swing.JComboBox();
        SelectFromTableFieldjComboBox = new javax.swing.JComboBox();
        OperatorSelectFromTablejComboBox1 = new javax.swing.JComboBox();
        operand1SelectFromTablejTextField1 = new javax.swing.JTextField();
        Operand2SelectFromTablejTextField1 = new javax.swing.JTextField();
        jScrollPane28 = new javax.swing.JScrollPane();
        ResultsStackContentjTable = new javax.swing.JTable();
        ContextsjInternalFrame = new javax.swing.JInternalFrame();
        ContextsjPanel = new javax.swing.JPanel();
        ContextsStackjSplitPane = new javax.swing.JSplitPane();
        jPanel12 = new javax.swing.JPanel();
        CtxStackAndContentjSplitPane = new javax.swing.JSplitPane();
        jScrollPane20 = new javax.swing.JScrollPane();
        ContextsStackContentjTable = new javax.swing.JTable();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane19 = new javax.swing.JScrollPane();
        ContextContentjTable = new javax.swing.JTable();
        stringToContextjSplitPane = new javax.swing.JSplitPane();
        jScrollPane25 = new javax.swing.JScrollPane();
        ContextCommandjPanel = new javax.swing.JPanel();
        ContextSortByjPanel = new javax.swing.JPanel();
        ContextSortByFieldComboBox = new javax.swing.JComboBox();
        ContextSortByOrderjComboBox = new javax.swing.JComboBox();
        ContextUnionjPanel = new javax.swing.JPanel();
        ContextUnionjComboBox = new javax.swing.JComboBox();
        ContextNormalizejPanel = new javax.swing.JPanel();
        ContextNormalizejComboBox = new javax.swing.JComboBox();
        ContextAmplifyjPanel = new javax.swing.JPanel();
        ContextAmplifySlopejTextField = new javax.swing.JTextField();
        ContextAmplifyBjTextField = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        ContextIntersectionjPanel = new javax.swing.JPanel();
        ContextIntersectionjComboBox = new javax.swing.JComboBox();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane24 = new javax.swing.JScrollPane();
        stringToContextjTextArea = new javax.swing.JTextArea();
        stringToContextLearnjCheckBox = new javax.swing.JCheckBox();
        stringToContextjButton = new javax.swing.JButton();
        KnowledgeGraphjInternalFrame = new javax.swing.JInternalFrame();
        KnowledgeGraphjPanel = new javax.swing.JPanel();
        jPanel32 = new javax.swing.JPanel();
        jTextField8 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        masDepthjLabel = new javax.swing.JLabel();
        maxDepthjSlider = new javax.swing.JSlider();
        maxDepthjTextField = new javax.swing.JTextField();
        maxSizejLabel = new javax.swing.JLabel();
        maxSizejTextField = new javax.swing.JTextField();
        maxSizejSlider = new javax.swing.JSlider();
        ResultSetContentjInternalFrame = new javax.swing.JInternalFrame();
        jScrollPane6 = new javax.swing.JScrollPane();
        jPanel35 = new javax.swing.JPanel();
        jScrollPane26 = new javax.swing.JScrollPane();
        ResultSetContentjTable = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        ContextPropertiesjInternalFrame = new javax.swing.JInternalFrame();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane21 = new javax.swing.JScrollPane();
        ContextPropertiesjTable = new javax.swing.JTable();
        ResultSetPropertiesjInternalFrame = new javax.swing.JInternalFrame();
        jPanel49 = new javax.swing.JPanel();
        jScrollPane29 = new javax.swing.JScrollPane();
        ResultsPropertiesjTable = new javax.swing.JTable();
        LogjInternalFrame = new javax.swing.JInternalFrame();
        jScrollPane31 = new javax.swing.JScrollPane();
        logjTextArea = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane17 = new javax.swing.JSplitPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        ServersTree = new javax.swing.JTree();
        jScrollPane10 = new javax.swing.JScrollPane();
        DesktopsjPanel = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane18 = new javax.swing.JScrollPane();
        LogjTextArea = new javax.swing.JTextArea();

        ServerCurrentSessionjInternalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        ServerCurrentSessionjInternalFrame.setIconifiable(true);
        ServerCurrentSessionjInternalFrame.setMaximizable(true);
        ServerCurrentSessionjInternalFrame.setResizable(true);
        ServerCurrentSessionjInternalFrame.setTitle("Session");
        ServerCurrentSessionjInternalFrame.setMaximumSize(new java.awt.Dimension(1250, 1250));
        ServerCurrentSessionjInternalFrame.setMinimumSize(new java.awt.Dimension(10, 10));
        ServerCurrentSessionjInternalFrame.setNormalBounds(new java.awt.Rectangle(0, 0, 102, 33));
        ServerCurrentSessionjInternalFrame.setPreferredSize(new java.awt.Dimension(526, 382));
        ServerCurrentSessionjInternalFrame.setVisible(true);
        ServerCurrentSessionjInternalFrame.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                ServerCurrentSessionjInternalFrameInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
                ServerCurrentSessionjInternalFrameInternalFrameDeactivated(evt);
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jScrollPane9.setMaximumSize(new java.awt.Dimension(526, 382));

        SessionPropertiesjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Session Properties", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        SessionPropertiesjTable.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        SessionPropertiesjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "property", "value", "type", "access"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        SessionPropertiesjTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                SessionPropertiesjTableMouseReleased(evt);
            }
        });
        SessionPropertiesjTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                SessionPropertiesjTableMouseMoved(evt);
            }
        });
        SessionPropertiesjTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                SessionPropertiesjTableKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout SessionPropertiesjPanelLayout = new javax.swing.GroupLayout(SessionPropertiesjPanel);
        SessionPropertiesjPanel.setLayout(SessionPropertiesjPanelLayout);
        SessionPropertiesjPanelLayout.setHorizontalGroup(
            SessionPropertiesjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SessionPropertiesjTable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );
        SessionPropertiesjPanelLayout.setVerticalGroup(
            SessionPropertiesjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SessionPropertiesjPanelLayout.createSequentialGroup()
                .addComponent(SessionPropertiesjTable, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 19, Short.MAX_VALUE))
        );

        SessionPropertiesSpectrumjSplitPane.setLeftComponent(SessionPropertiesjPanel);

        jPanel30.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Session Spectrum", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N
        jPanel30.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        SessionSpectrumjTable.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        SessionSpectrumjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Value", "Type"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        SessionSpectrumjTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        SessionSpectrumjTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                SessionSpectrumjTableMouseReleased(evt);
            }
        });
        SessionSpectrumjTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                SessionSpectrumjTableMouseMoved(evt);
            }
        });
        SessionSpectrumjTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                SessionSpectrumjTableKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
            .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(SessionSpectrumjTable, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 324, Short.MAX_VALUE)
            .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel30Layout.createSequentialGroup()
                    .addComponent(SessionSpectrumjTable, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 19, Short.MAX_VALUE)))
        );

        SessionPropertiesSpectrumjSplitPane.setRightComponent(jPanel30);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SessionPropertiesSpectrumjSplitPane)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(SessionPropertiesSpectrumjSplitPane)
                .addGap(16, 16, 16))
        );

        jScrollPane9.setViewportView(jPanel8);

        ClearSessionjButton.setText("Clear Session");
        ClearSessionjButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ClearSessionjButtonMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout ServerCurrentSessionjInternalFrameLayout = new javax.swing.GroupLayout(ServerCurrentSessionjInternalFrame.getContentPane());
        ServerCurrentSessionjInternalFrame.getContentPane().setLayout(ServerCurrentSessionjInternalFrameLayout);
        ServerCurrentSessionjInternalFrameLayout.setHorizontalGroup(
            ServerCurrentSessionjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(ServerCurrentSessionjInternalFrameLayout.createSequentialGroup()
                .addComponent(ClearSessionjButton)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        ServerCurrentSessionjInternalFrameLayout.setVerticalGroup(
            ServerCurrentSessionjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ServerCurrentSessionjInternalFrameLayout.createSequentialGroup()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ClearSessionjButton))
        );

        TablesjInternalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        TablesjInternalFrame.setIconifiable(true);
        TablesjInternalFrame.setMaximizable(true);
        TablesjInternalFrame.setResizable(true);
        TablesjInternalFrame.setTitle("Tables");
        TablesjInternalFrame.setMaximumSize(new java.awt.Dimension(1250, 1250));
        TablesjInternalFrame.setNormalBounds(new java.awt.Rectangle(0, 0, 22, 33));
        TablesjInternalFrame.setPreferredSize(new java.awt.Dimension(500, 500));
        TablesjInternalFrame.setVisible(true);
        TablesjInternalFrame.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                TablesjInternalFrameInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
                TablesjInternalFrameInternalFrameDeactivated(evt);
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        TablesjInternalFrame.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                TablesjInternalFrameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                TablesjInternalFrameFocusLost(evt);
            }
        });

        TablesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tables", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 9))); // NOI18N
        TablesPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                TablesPanelComponentResized(evt);
            }
        });

        jSplitPane5.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        TablesjTable.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        TablesjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Lines"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TablesjTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        TablesjTable.setMaximumSize(new java.awt.Dimension(500, 500));
        TablesjTable.setPreferredSize(new java.awt.Dimension(100, 100));
        TablesjTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        TablesjTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TablesjTableMouseReleased(evt);
            }
        });
        TablesjTable.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                TablesjTableComponentResized(evt);
            }
        });
        jScrollPane2.setViewportView(TablesjTable);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jSplitPane5.setTopComponent(jPanel2);

        TableStructurePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fields", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 9))); // NOI18N

        FieldsTable.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        FieldsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "name", "type", "size"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(FieldsTable);

        javax.swing.GroupLayout TableStructurePanelLayout = new javax.swing.GroupLayout(TableStructurePanel);
        TableStructurePanel.setLayout(TableStructurePanelLayout);
        TableStructurePanelLayout.setHorizontalGroup(
            TableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
        );
        TableStructurePanelLayout.setVerticalGroup(
            TableStructurePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
        );

        jSplitPane5.setRightComponent(TableStructurePanel);

        javax.swing.GroupLayout TablesPanelLayout = new javax.swing.GroupLayout(TablesPanel);
        TablesPanel.setLayout(TablesPanelLayout);
        TablesPanelLayout.setHorizontalGroup(
            TablesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane5)
        );
        TablesPanelLayout.setVerticalGroup(
            TablesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane5)
        );

        jScrollPane11.setViewportView(TablesPanel);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane11)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane11)
        );

        TablesjSplitPane.setLeftComponent(jPanel6);

        LDBTreesjSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane18.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        BTreesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "B-Trees", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 9))); // NOI18N

        BTreejTable.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        BTreejTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "name", "status", "progress", "unique"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(BTreejTable);

        javax.swing.GroupLayout BTreesPanelLayout = new javax.swing.GroupLayout(BTreesPanel);
        BTreesPanel.setLayout(BTreesPanelLayout);
        BTreesPanelLayout.setHorizontalGroup(
            BTreesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 844, Short.MAX_VALUE)
        );
        BTreesPanelLayout.setVerticalGroup(
            BTreesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
        );

        jSplitPane18.setTopComponent(BTreesPanel);

        KTreePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "K-Trees", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 9))); // NOI18N

        KTreesjTable.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        KTreesjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "index name", "status", "progress"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(KTreesjTable);

        javax.swing.GroupLayout KTreePanelLayout = new javax.swing.GroupLayout(KTreePanel);
        KTreePanel.setLayout(KTreePanelLayout);
        KTreePanelLayout.setHorizontalGroup(
            KTreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 844, Short.MAX_VALUE)
        );
        KTreePanelLayout.setVerticalGroup(
            KTreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
        );

        jSplitPane18.setRightComponent(KTreePanel);

        LDBTreesjSplitPane.setRightComponent(jSplitPane18);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Last DataBase Operation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 9))); // NOI18N

        LastDBInfojTable.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        LastDBInfojTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "table", "operation", "row id", "status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane8.setViewportView(LastDBInfojTable);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
        );

        LDBTreesjSplitPane.setLeftComponent(jPanel4);

        TablesjSplitPane.setRightComponent(LDBTreesjSplitPane);

        jScrollPane13.setViewportView(TablesjSplitPane);

        javax.swing.GroupLayout TablesjInternalFrameLayout = new javax.swing.GroupLayout(TablesjInternalFrame.getContentPane());
        TablesjInternalFrame.getContentPane().setLayout(TablesjInternalFrameLayout);
        TablesjInternalFrameLayout.setHorizontalGroup(
            TablesjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
        );
        TablesjInternalFrameLayout.setVerticalGroup(
            TablesjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane13)
        );

        TableVisualjInternalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        TableVisualjInternalFrame.setIconifiable(true);
        TableVisualjInternalFrame.setMaximizable(true);
        TableVisualjInternalFrame.setResizable(true);
        TableVisualjInternalFrame.setTitle("Table Vizualisation");
        TableVisualjInternalFrame.setMaximumSize(new java.awt.Dimension(1250, 1250));
        TableVisualjInternalFrame.setVisible(true);
        TableVisualjInternalFrame.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                TableVisualjInternalFrameInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
                TableVisualjInternalFrameInternalFrameDeactivated(evt);
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        TableVisualjInternalFrame.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                TableVisualjInternalFrameFocusGained(evt);
            }
        });

        jPanel29.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Table Content", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 9))); // NOI18N
        jPanel29.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel29ComponentShown(evt);
            }
        });

        TableContentjSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        TableContentjSplitPane.setPreferredSize(new java.awt.Dimension(500, 500));

        jButton2.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jButton2.setText("forward");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton2MouseReleased(evt);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jButton1.setText("backward");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton1MouseReleased(evt);
            }
        });

        SizejTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        SizejTextField.setText("20");
        SizejTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SizejTextFieldActionPerformed(evt);
            }
        });
        SizejTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                SizejTextFieldKeyReleased(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel5.setText("size");

        StartjTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        StartjTextField.setText("1");
        StartjTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                StartjTextFieldMouseReleased(evt);
            }
        });
        StartjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                StartjTextFieldKeyReleased(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel4.setText("start");

        TableLinesjSlider.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        TableLinesjSlider.setValue(1);
        TableLinesjSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Start", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 0, 9))); // NOI18N
        TableLinesjSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TableLinesjSliderMouseReleased(evt);
            }
        });
        TableLinesjSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TableLinesjSliderStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2))
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(StartjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SizejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TableLinesjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(StartjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(SizejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(TableLinesjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel44Layout = new javax.swing.GroupLayout(jPanel44);
        jPanel44.setLayout(jPanel44Layout);
        jPanel44Layout.setHorizontalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel44Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(570, Short.MAX_VALUE))
        );
        jPanel44Layout.setVerticalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel44Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TableContentjSplitPane.setTopComponent(jPanel44);

        TableContentTextAreajSplitPane.setDividerLocation(150);
        TableContentTextAreajSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        ContentTablejTextArea.setEditable(false);
        ContentTablejTextArea.setColumns(20);
        ContentTablejTextArea.setFont(new java.awt.Font("Calibri", 0, 9)); // NOI18N
        ContentTablejTextArea.setLineWrap(true);
        ContentTablejTextArea.setRows(5);
        ContentTablejTextArea.setMaximumSize(new java.awt.Dimension(10000, 10000));
        ContentTablejTextArea.setMinimumSize(new java.awt.Dimension(10, 10));
        TableContentTextAreajSplitPane.setRightComponent(ContentTablejTextArea);

        TableContentjScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TableContentjScrollPaneMouseReleased(evt);
            }
        });

        ContentjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        ContentjTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        ContentjTable.setGridColor(new java.awt.Color(204, 204, 204));
        ContentjTable.setMaximumSize(new java.awt.Dimension(65635, 65635));
        ContentjTable.setMinimumSize(new java.awt.Dimension(200, 100));
        ContentjTable.setPreferredSize(new java.awt.Dimension(200, 200));
        ContentjTable.setSelectionBackground(new java.awt.Color(0, 102, 153));
        ContentjTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ContentjTableMouseReleased(evt);
            }
        });
        ContentjTable.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                ContentjTableComponentShown(evt);
            }
        });
        ContentjTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                ContentjTableMouseMoved(evt);
            }
        });
        TableContentjScrollPane.setViewportView(ContentjTable);

        TableContentTextAreajSplitPane.setTopComponent(TableContentjScrollPane);

        TableContentjSplitPane.setRightComponent(TableContentTextAreajSplitPane);

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TableContentjSplitPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 921, Short.MAX_VALUE)
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TableContentjSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
        );

        jScrollPane14.setViewportView(jPanel29);

        javax.swing.GroupLayout TableVisualjInternalFrameLayout = new javax.swing.GroupLayout(TableVisualjInternalFrame.getContentPane());
        TableVisualjInternalFrame.getContentPane().setLayout(TableVisualjInternalFrameLayout);
        TableVisualjInternalFrameLayout.setHorizontalGroup(
            TableVisualjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane14)
        );
        TableVisualjInternalFrameLayout.setVerticalGroup(
            TableVisualjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane14)
        );

        jPanelMainWindow.setBackground(new java.awt.Color(0, 0, 0));
        jPanelMainWindow.setMaximumSize(new java.awt.Dimension(1600, 1200));
        jPanelMainWindow.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanelMainWindow.setPreferredSize(new java.awt.Dimension(1600, 1200));

        jScrollPane15.setBackground(new java.awt.Color(0, 0, 0));
        jScrollPane15.setMaximumSize(new java.awt.Dimension(1600, 1200));
        jScrollPane15.setPreferredSize(new java.awt.Dimension(1600, 1200));

        jPanel31.setBackground(new java.awt.Color(0, 0, 0));
        jPanel31.setMaximumSize(new java.awt.Dimension(1600, 1200));
        jPanel31.setPreferredSize(new java.awt.Dimension(1600, 1200));

        MainWindowjSplitPane.setBackground(new java.awt.Color(0, 0, 0));
        MainWindowjSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        MainWindowjSplitPane.setMaximumSize(new java.awt.Dimension(1600, 1200));
        MainWindowjSplitPane.setMinimumSize(new java.awt.Dimension(0, 0));
        MainWindowjSplitPane.setPreferredSize(new java.awt.Dimension(1600, 1200));

        CurrentServerjPanel.setBackground(new java.awt.Color(0, 0, 0));
        CurrentServerjPanel.setForeground(new java.awt.Color(51, 255, 0));
        CurrentServerjPanel.setMaximumSize(new java.awt.Dimension(1600, 1200));
        CurrentServerjPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        CurrentServerjPanel.setOpaque(false);
        CurrentServerjPanel.setPreferredSize(new java.awt.Dimension(1600, 1200));
        CurrentServerjPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                CurrentServerjPanelComponentHidden(evt);
            }
        });

        jLabel3.setForeground(new java.awt.Color(51, 255, 0));
        jLabel3.setText("Current Server");

        CurrentServerjTextField.setEditable(false);
        CurrentServerjTextField.setBackground(new java.awt.Color(0, 0, 0));
        CurrentServerjTextField.setForeground(new java.awt.Color(51, 255, 0));
        CurrentServerjTextField.setBorder(null);

        jLabel6.setBackground(new java.awt.Color(0, 0, 0));
        jLabel6.setForeground(new java.awt.Color(51, 255, 0));
        jLabel6.setText("Current Session");

        CurrentTablejTextField.setEditable(false);
        CurrentTablejTextField.setBackground(new java.awt.Color(0, 0, 0));
        CurrentTablejTextField.setForeground(new java.awt.Color(51, 255, 0));
        CurrentTablejTextField.setBorder(null);

        jLabel7.setBackground(new java.awt.Color(0, 0, 0));
        jLabel7.setForeground(new java.awt.Color(51, 255, 0));
        jLabel7.setText("Current Table");

        CurrentSessionjTextField.setEditable(false);
        CurrentSessionjTextField.setBackground(new java.awt.Color(0, 0, 0));
        CurrentSessionjTextField.setForeground(new java.awt.Color(51, 255, 0));
        CurrentSessionjTextField.setBorder(null);

        ServerInternaljSplitPane.setBackground(new java.awt.Color(0, 0, 0));
        ServerInternaljSplitPane.setOpaque(false);

        jPanel17.setBackground(new java.awt.Color(0, 0, 0));

        jPanel5.setBackground(new java.awt.Color(0, 0, 0));

        TimePanel.setBackground(new java.awt.Color(0, 0, 0));
        TimePanel.setForeground(new java.awt.Color(51, 255, 0));

        UpTimeLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        UpTimeLabel.setForeground(new java.awt.Color(51, 255, 0));
        UpTimeLabel.setText("Up Time");

        UpTimeTextField.setBackground(new java.awt.Color(0, 0, 0));
        UpTimeTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        UpTimeTextField.setForeground(new java.awt.Color(51, 255, 0));
        UpTimeTextField.setBorder(null);

        GMTimeLabel.setBackground(new java.awt.Color(0, 0, 0));
        GMTimeLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        GMTimeLabel.setForeground(new java.awt.Color(51, 255, 0));
        GMTimeLabel.setText("GM Time");

        GMTimeTextField.setBackground(new java.awt.Color(0, 0, 0));
        GMTimeTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        GMTimeTextField.setForeground(new java.awt.Color(51, 255, 0));
        GMTimeTextField.setBorder(null);

        IdleTimeLabel.setBackground(new java.awt.Color(0, 0, 0));
        IdleTimeLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        IdleTimeLabel.setForeground(new java.awt.Color(51, 255, 0));
        IdleTimeLabel.setText("Idle Time");

        IdleTimeTextField.setBackground(new java.awt.Color(0, 0, 0));
        IdleTimeTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        IdleTimeTextField.setForeground(new java.awt.Color(51, 255, 0));
        IdleTimeTextField.setBorder(null);

        javax.swing.GroupLayout TimePanelLayout = new javax.swing.GroupLayout(TimePanel);
        TimePanel.setLayout(TimePanelLayout);
        TimePanelLayout.setHorizontalGroup(
            TimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TimePanelLayout.createSequentialGroup()
                .addGroup(TimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(GMTimeLabel)
                    .addComponent(IdleTimeLabel)
                    .addComponent(UpTimeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(GMTimeTextField)
                    .addGroup(TimePanelLayout.createSequentialGroup()
                        .addComponent(UpTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(IdleTimeTextField)))
        );
        TimePanelLayout.setVerticalGroup(
            TimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TimePanelLayout.createSequentialGroup()
                .addGroup(TimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(UpTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UpTimeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(GMTimeLabel)
                    .addComponent(GMTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TimePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IdleTimeTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
                    .addComponent(IdleTimeLabel))
                .addContainerGap())
        );

        MTPanel.setBackground(new java.awt.Color(0, 0, 0));
        MTPanel.setForeground(new java.awt.Color(51, 255, 0));

        CTLabel.setBackground(new java.awt.Color(0, 0, 0));
        CTLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        CTLabel.setForeground(new java.awt.Color(51, 255, 0));
        CTLabel.setText("command Threads");

        CommandThreadsTextField.setBackground(new java.awt.Color(0, 0, 0));
        CommandThreadsTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        CommandThreadsTextField.setForeground(new java.awt.Color(51, 255, 0));
        CommandThreadsTextField.setBorder(null);
        CommandThreadsTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                CommandThreadsTextFieldKeyReleased(evt);
            }
        });

        jLabel25.setBackground(new java.awt.Color(0, 0, 0));
        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(51, 255, 0));
        jLabel25.setText("Cache Size");

        CacheSizeTextField.setBackground(new java.awt.Color(0, 0, 0));
        CacheSizeTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        CacheSizeTextField.setForeground(new java.awt.Color(51, 255, 0));
        CacheSizeTextField.setBorder(null);
        CacheSizeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CacheSizeTextFieldActionPerformed(evt);
            }
        });
        CacheSizeTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                CacheSizeTextFieldKeyReleased(evt);
            }
        });

        ExecTimeoutDefaultTextField.setBackground(new java.awt.Color(0, 0, 0));
        ExecTimeoutDefaultTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ExecTimeoutDefaultTextField.setForeground(new java.awt.Color(51, 255, 0));
        ExecTimeoutDefaultTextField.setBorder(null);
        ExecTimeoutDefaultTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ExecTimeoutDefaultTextFieldKeyReleased(evt);
            }
        });

        jLabel26.setBackground(new java.awt.Color(0, 0, 0));
        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(51, 255, 0));
        jLabel26.setText("exec time out default");

        jLabel27.setBackground(new java.awt.Color(0, 0, 0));
        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(51, 255, 0));
        jLabel27.setText("session time out default");

        SessionTimeoutDefaultTextField.setBackground(new java.awt.Color(0, 0, 0));
        SessionTimeoutDefaultTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        SessionTimeoutDefaultTextField.setForeground(new java.awt.Color(51, 255, 0));
        SessionTimeoutDefaultTextField.setBorder(null);
        SessionTimeoutDefaultTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                SessionTimeoutDefaultTextFieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout MTPanelLayout = new javax.swing.GroupLayout(MTPanel);
        MTPanel.setLayout(MTPanelLayout);
        MTPanelLayout.setHorizontalGroup(
            MTPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MTPanelLayout.createSequentialGroup()
                .addGroup(MTPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MTPanelLayout.createSequentialGroup()
                        .addGroup(MTPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(MTPanelLayout.createSequentialGroup()
                                .addGroup(MTPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(CTLabel)
                                    .addComponent(jLabel25))
                                .addGap(25, 25, 25))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MTPanelLayout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addGap(18, 18, 18)))
                        .addGroup(MTPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ExecTimeoutDefaultTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                            .addComponent(CacheSizeTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CommandThreadsTextField))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(MTPanelLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SessionTimeoutDefaultTextField)))
                .addContainerGap())
        );
        MTPanelLayout.setVerticalGroup(
            MTPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MTPanelLayout.createSequentialGroup()
                .addGroup(MTPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CTLabel)
                    .addComponent(CommandThreadsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MTPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(CacheSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MTPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ExecTimeoutDefaultTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MTPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 16, Short.MAX_VALUE)
                    .addComponent(SessionTimeoutDefaultTextField))
                .addGap(0, 17, Short.MAX_VALUE))
        );

        ServerIdPanel.setBackground(new java.awt.Color(0, 0, 0));
        ServerIdPanel.setForeground(new java.awt.Color(51, 255, 0));

        ServerNameLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ServerNameLabel.setForeground(new java.awt.Color(51, 255, 0));
        ServerNameLabel.setText("Name");

        ServerNameTextField.setBackground(new java.awt.Color(0, 0, 0));
        ServerNameTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ServerNameTextField.setForeground(new java.awt.Color(51, 255, 0));
        ServerNameTextField.setBorder(null);
        ServerNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ServerNameTextFieldKeyReleased(evt);
            }
        });

        ModelLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ModelLabel.setForeground(new java.awt.Color(51, 255, 0));
        ModelLabel.setText("Model");

        ModelTextField.setBackground(new java.awt.Color(0, 0, 0));
        ModelTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ModelTextField.setForeground(new java.awt.Color(51, 255, 0));
        ModelTextField.setBorder(null);

        VersionLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        VersionLabel.setForeground(new java.awt.Color(51, 255, 0));
        VersionLabel.setText("Version");

        VersionTextField.setBackground(new java.awt.Color(0, 0, 0));
        VersionTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        VersionTextField.setForeground(new java.awt.Color(51, 255, 0));
        VersionTextField.setBorder(null);
        VersionTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VersionTextFieldActionPerformed(evt);
            }
        });

        BuildLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        BuildLabel.setForeground(new java.awt.Color(51, 255, 0));
        BuildLabel.setText("Build");

        BuildTextField.setBackground(new java.awt.Color(0, 0, 0));
        BuildTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        BuildTextField.setForeground(new java.awt.Color(51, 255, 0));
        BuildTextField.setBorder(null);

        PortLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        PortLabel.setForeground(new java.awt.Color(51, 255, 0));
        PortLabel.setText("Port");

        PortTextField.setBackground(new java.awt.Color(0, 0, 0));
        PortTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        PortTextField.setForeground(new java.awt.Color(51, 255, 0));
        PortTextField.setBorder(null);

        IPLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        IPLabel.setForeground(new java.awt.Color(51, 255, 0));
        IPLabel.setText("IP");

        IPTextField.setBackground(new java.awt.Color(0, 0, 0));
        IPTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        IPTextField.setForeground(new java.awt.Color(51, 255, 0));
        IPTextField.setBorder(null);

        ConnectCountLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ConnectCountLabel.setForeground(new java.awt.Color(51, 255, 0));
        ConnectCountLabel.setText("Connections count");

        ConnectCountjTextField.setBackground(new java.awt.Color(0, 0, 0));
        ConnectCountjTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ConnectCountjTextField.setForeground(new java.awt.Color(51, 255, 0));
        ConnectCountjTextField.setBorder(null);

        javax.swing.GroupLayout ServerIdPanelLayout = new javax.swing.GroupLayout(ServerIdPanel);
        ServerIdPanel.setLayout(ServerIdPanelLayout);
        ServerIdPanelLayout.setHorizontalGroup(
            ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ServerIdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ServerIdPanelLayout.createSequentialGroup()
                        .addGroup(ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ModelLabel)
                            .addComponent(VersionLabel)
                            .addComponent(ServerNameLabel))
                        .addGap(18, 18, 18)
                        .addGroup(ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ModelTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                            .addComponent(ServerNameTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(VersionTextField)))
                    .addGroup(ServerIdPanelLayout.createSequentialGroup()
                        .addComponent(ConnectCountLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ConnectCountjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ServerIdPanelLayout.createSequentialGroup()
                        .addGroup(ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BuildLabel)
                            .addComponent(PortLabel)
                            .addComponent(IPLabel))
                        .addGroup(ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ServerIdPanelLayout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(PortTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                    .addComponent(IPTextField)))
                            .addGroup(ServerIdPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(BuildTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ServerIdPanelLayout.setVerticalGroup(
            ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ServerIdPanelLayout.createSequentialGroup()
                .addGroup(ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ServerNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ServerNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ModelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ModelLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(VersionLabel)
                    .addComponent(VersionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BuildLabel)
                    .addComponent(BuildTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PortLabel)
                    .addComponent(PortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IPLabel)
                    .addComponent(IPTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServerIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ConnectCountLabel)
                    .addComponent(ConnectCountjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        mARCPanel.setBackground(new java.awt.Color(0, 0, 0));

        mARCRebuildFromjTextField.setBackground(new java.awt.Color(0, 0, 0));
        mARCRebuildFromjTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        mARCRebuildFromjTextField.setForeground(new java.awt.Color(51, 255, 0));
        mARCRebuildFromjTextField.setText("from");
        mARCRebuildFromjTextField.setToolTipText("");
        mARCRebuildFromjTextField.setBorder(null);
        mARCRebuildFromjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mARCRebuildFromjTextFieldKeyReleased(evt);
            }
        });

        mARCRebuildTojTextField.setBackground(new java.awt.Color(0, 0, 0));
        mARCRebuildTojTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        mARCRebuildTojTextField.setForeground(new java.awt.Color(51, 255, 0));
        mARCRebuildTojTextField.setText("to");
        mARCRebuildTojTextField.setBorder(null);
        mARCRebuildTojTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mARCRebuildTojTextFieldKeyReleased(evt);
            }
        });

        RebuildRefjCheckBox.setBackground(new java.awt.Color(0, 0, 0));
        RebuildRefjCheckBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        RebuildRefjCheckBox.setForeground(new java.awt.Color(51, 255, 0));
        RebuildRefjCheckBox.setText("ref");
        RebuildRefjCheckBox.setBorder(null);

        particlesjLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        particlesjLabel.setForeground(new java.awt.Color(51, 255, 0));
        particlesjLabel.setText("particles");

        particlesjTextField.setBackground(new java.awt.Color(0, 0, 0));
        particlesjTextField.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        particlesjTextField.setForeground(new java.awt.Color(51, 255, 0));
        particlesjTextField.setBorder(null);

        shapesjLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        shapesjLabel.setForeground(new java.awt.Color(51, 255, 0));
        shapesjLabel.setText("shapes");

        shapesjTextField.setBackground(new java.awt.Color(0, 0, 0));
        shapesjTextField.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        shapesjTextField.setForeground(new java.awt.Color(51, 255, 0));
        shapesjTextField.setBorder(null);

        relationsjLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        relationsjLabel.setForeground(new java.awt.Color(51, 255, 0));
        relationsjLabel.setText("relations");

        relationsjTextField.setBackground(new java.awt.Color(0, 0, 0));
        relationsjTextField.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        relationsjTextField.setForeground(new java.awt.Color(51, 255, 0));
        relationsjTextField.setBorder(null);

        referencesjLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        referencesjLabel.setForeground(new java.awt.Color(51, 255, 0));
        referencesjLabel.setText("references");

        referencesjTextField.setBackground(new java.awt.Color(0, 0, 0));
        referencesjTextField.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        referencesjTextField.setForeground(new java.awt.Color(51, 255, 0));
        referencesjTextField.setBorder(null);

        mARCRebuildFieldsjTextField.setBackground(new java.awt.Color(0, 0, 0));
        mARCRebuildFieldsjTextField.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        mARCRebuildFieldsjTextField.setForeground(new java.awt.Color(51, 255, 0));
        mARCRebuildFieldsjTextField.setText("fields separated by space");
        mARCRebuildFieldsjTextField.setBorder(null);

        restartjCheckBox.setBackground(new java.awt.Color(0, 0, 0));
        restartjCheckBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        restartjCheckBox.setForeground(new java.awt.Color(51, 255, 0));
        restartjCheckBox.setText("restart");

        javax.swing.GroupLayout mARCPanelLayout = new javax.swing.GroupLayout(mARCPanel);
        mARCPanel.setLayout(mARCPanelLayout);
        mARCPanelLayout.setHorizontalGroup(
            mARCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mARCPanelLayout.createSequentialGroup()
                .addGroup(mARCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mARCPanelLayout.createSequentialGroup()
                        .addGroup(mARCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(mARCPanelLayout.createSequentialGroup()
                                .addComponent(referencesjLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(referencesjTextField))
                            .addGroup(mARCPanelLayout.createSequentialGroup()
                                .addComponent(particlesjLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(particlesjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mARCPanelLayout.createSequentialGroup()
                                .addComponent(shapesjLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(shapesjTextField))
                            .addGroup(mARCPanelLayout.createSequentialGroup()
                                .addComponent(relationsjLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(relationsjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mARCPanelLayout.createSequentialGroup()
                                .addComponent(mARCRebuildFromjTextField)
                                .addGap(18, 18, 18)
                                .addComponent(RebuildRefjCheckBox)
                                .addGap(29, 29, 29)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(restartjCheckBox))
                    .addGroup(mARCPanelLayout.createSequentialGroup()
                        .addComponent(mARCRebuildTojTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(mARCRebuildFieldsjTextField))
                .addContainerGap())
        );
        mARCPanelLayout.setVerticalGroup(
            mARCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mARCPanelLayout.createSequentialGroup()
                .addGroup(mARCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(particlesjLabel)
                    .addComponent(particlesjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(mARCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(shapesjLabel)
                    .addComponent(shapesjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(mARCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(relationsjLabel)
                    .addComponent(relationsjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mARCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(referencesjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(referencesjLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mARCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mARCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(RebuildRefjCheckBox)
                        .addComponent(restartjCheckBox))
                    .addGroup(mARCPanelLayout.createSequentialGroup()
                        .addComponent(mARCRebuildFromjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)))
                .addComponent(mARCRebuildTojTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mARCRebuildFieldsjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        IndexationPanel.setBackground(new java.awt.Color(0, 0, 0));
        IndexationPanel.setForeground(new java.awt.Color(51, 255, 0));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 255, 0));
        jLabel1.setText("Cache Size");

        IndexationCacheSizeTextField.setBackground(new java.awt.Color(0, 0, 0));
        IndexationCacheSizeTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        IndexationCacheSizeTextField.setForeground(new java.awt.Color(51, 255, 0));
        IndexationCacheSizeTextField.setBorder(null);
        IndexationCacheSizeTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                IndexationCacheSizeTextFieldKeyReleased(evt);
            }
        });

        CacheUsedLabel.setBackground(new java.awt.Color(0, 0, 0));
        CacheUsedLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        CacheUsedLabel.setForeground(new java.awt.Color(51, 255, 0));
        CacheUsedLabel.setText("Cache Used");

        IndexationCacheUsedTextField.setBackground(new java.awt.Color(0, 0, 0));
        IndexationCacheUsedTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        IndexationCacheUsedTextField.setForeground(new java.awt.Color(51, 255, 0));
        IndexationCacheUsedTextField.setBorder(null);
        IndexationCacheUsedTextField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                IndexationCacheUsedTextFieldPropertyChange(evt);
            }
        });

        IndexationCacheProgressBar.setBackground(new java.awt.Color(0, 0, 0));
        IndexationCacheProgressBar.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        IndexationCacheProgressBar.setForeground(new java.awt.Color(0, 0, 0));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 255, 0));
        jLabel2.setText("Cache Hits");

        CacheHitsTextField.setBackground(new java.awt.Color(0, 0, 0));
        CacheHitsTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        CacheHitsTextField.setForeground(new java.awt.Color(51, 255, 0));
        CacheHitsTextField.setBorder(null);

        PercentCacheHitsLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        PercentCacheHitsLabel.setForeground(new java.awt.Color(51, 255, 0));
        PercentCacheHitsLabel.setText("%");

        jLabel20.setBackground(new java.awt.Color(0, 0, 0));
        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(51, 255, 0));
        jLabel20.setText("time out");

        IndexationTimeoutTextField.setBackground(new java.awt.Color(0, 0, 0));
        IndexationTimeoutTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        IndexationTimeoutTextField.setForeground(new java.awt.Color(51, 255, 0));
        IndexationTimeoutTextField.setBorder(null);
        IndexationTimeoutTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IndexationTimeoutTextFieldActionPerformed(evt);
            }
        });
        IndexationTimeoutTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                IndexationTimeoutTextFieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout IndexationPanelLayout = new javax.swing.GroupLayout(IndexationPanel);
        IndexationPanel.setLayout(IndexationPanelLayout);
        IndexationPanelLayout.setHorizontalGroup(
            IndexationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IndexationPanelLayout.createSequentialGroup()
                .addGroup(IndexationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(IndexationPanelLayout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(20, 20, 20)
                        .addComponent(IndexationTimeoutTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CacheHitsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PercentCacheHitsLabel))
                    .addGroup(IndexationPanelLayout.createSequentialGroup()
                        .addGroup(IndexationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(IndexationCacheUsedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CacheUsedLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(IndexationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(IndexationPanelLayout.createSequentialGroup()
                                .addComponent(IndexationCacheSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(IndexationCacheProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        IndexationPanelLayout.setVerticalGroup(
            IndexationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IndexationPanelLayout.createSequentialGroup()
                .addGroup(IndexationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(CacheHitsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PercentCacheHitsLabel)
                    .addComponent(jLabel20)
                    .addComponent(IndexationTimeoutTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(IndexationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(IndexationPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(IndexationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CacheUsedLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(IndexationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(IndexationCacheUsedTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
                            .addComponent(IndexationCacheSizeTextField)))
                    .addGroup(IndexationPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(IndexationCacheProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(IndexationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                            .addComponent(TimePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(MTPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                            .addComponent(ServerIdPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(mARCPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(104, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(ServerIdPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(TimePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(mARCPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(MTPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(IndexationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        jScrollPane17.setViewportView(jPanel5);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane17)
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane17)
        );

        ServerInternaljSplitPane.setLeftComponent(jPanel17);

        SessionsTasksjSplitPane.setBackground(new java.awt.Color(0, 0, 0));
        SessionsTasksjSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        SessionsTasksjSplitPane.setOpaque(false);

        jPanel7.setBackground(new java.awt.Color(0, 0, 0));

        jScrollPane16.setBackground(new java.awt.Color(0, 0, 0));
        jScrollPane16.setBorder(null);

        ServerSessionsjPanel.setBackground(new java.awt.Color(0, 0, 0));
        ServerSessionsjPanel.setForeground(new java.awt.Color(51, 255, 0));

        jScrollPane23.getViewport().setBackground(Color.BLACK);
        jScrollPane23.setBackground(new java.awt.Color(0, 0, 0));
        jScrollPane23.setForeground(new java.awt.Color(51, 255, 0));

        ServerSessionsjTable.getTableHeader().setForeground(Color.GREEN);
        ServerSessionsjTable.setBackground(new java.awt.Color(0, 0, 0));
        ServerSessionsjTable.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        ServerSessionsjTable.setForeground(new java.awt.Color(51, 255, 0));
        ServerSessionsjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "name", "persistant", "owner ip", "owner port", "priority", "exec timeout", "session timeout", "debug"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ServerSessionsjTable.setGridColor(new java.awt.Color(204, 204, 204));
        ServerSessionsjTable.setSelectionBackground(new java.awt.Color(51, 255, 0));
        ServerSessionsjTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ServerSessionsjTableMouseReleased(evt);
            }
        });
        jScrollPane23.setViewportView(ServerSessionsjTable);

        jButton29.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jButton29.setText("Add A Session");
        jButton29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton29MouseReleased(evt);
            }
        });
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jButton30.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jButton30.setText("Delete Selected Session");
        jButton30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton30MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout ServerSessionsjPanelLayout = new javax.swing.GroupLayout(ServerSessionsjPanel);
        ServerSessionsjPanel.setLayout(ServerSessionsjPanelLayout);
        ServerSessionsjPanelLayout.setHorizontalGroup(
            ServerSessionsjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ServerSessionsjPanelLayout.createSequentialGroup()
                .addComponent(jButton29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton30))
            .addComponent(jScrollPane23, javax.swing.GroupLayout.DEFAULT_SIZE, 1215, Short.MAX_VALUE)
        );
        ServerSessionsjPanelLayout.setVerticalGroup(
            ServerSessionsjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ServerSessionsjPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane23, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ServerSessionsjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton29)
                    .addComponent(jButton30)))
        );

        jScrollPane16.setViewportView(ServerSessionsjPanel);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane16)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane16, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
        );

        SessionsTasksjSplitPane.setTopComponent(jPanel7);

        jPanel10.setBackground(new java.awt.Color(0, 0, 0));

        jScrollPane12.setBackground(new java.awt.Color(0, 0, 0));

        TasksjPanel.setBackground(new java.awt.Color(0, 0, 0));
        TasksjPanel.setForeground(new java.awt.Color(51, 255, 0));

        jScrollPane33.getViewport().setBackground(Color.BLACK);

        tasksjTable.getTableHeader().setOpaque(false);
        tasksjTable.getTableHeader().setBackground(Color.BLACK);
        tasksjTable.setBackground(new java.awt.Color(0, 0, 0));
        tasksjTable.setForeground(new java.awt.Color(51, 255, 0));
        tasksjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "task", "completion", "current", "from", "to", "elapsed"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tasksjTable.setGridColor(new java.awt.Color(204, 204, 204));
        tasksjTable.setMaximumSize(new java.awt.Dimension(570, 150));
        tasksjTable.setMinimumSize(new java.awt.Dimension(0, 0));
        tasksjTable.setPreferredSize(new java.awt.Dimension(570, 150));
        jScrollPane33.setViewportView(tasksjTable);

        javax.swing.GroupLayout TasksjPanelLayout = new javax.swing.GroupLayout(TasksjPanel);
        TasksjPanel.setLayout(TasksjPanelLayout);
        TasksjPanelLayout.setHorizontalGroup(
            TasksjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane33, javax.swing.GroupLayout.DEFAULT_SIZE, 1233, Short.MAX_VALUE)
        );
        TasksjPanelLayout.setVerticalGroup(
            TasksjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane33, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jScrollPane12.setViewportView(TasksjPanel);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane12)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane12)
        );

        SessionsTasksjSplitPane.setRightComponent(jPanel10);

        ServerInternaljSplitPane.setRightComponent(SessionsTasksjSplitPane);

        javax.swing.GroupLayout CurrentServerjPanelLayout = new javax.swing.GroupLayout(CurrentServerjPanel);
        CurrentServerjPanel.setLayout(CurrentServerjPanelLayout);
        CurrentServerjPanelLayout.setHorizontalGroup(
            CurrentServerjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CurrentServerjPanelLayout.createSequentialGroup()
                .addGroup(CurrentServerjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CurrentServerjPanelLayout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(CurrentServerjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(CurrentServerjPanelLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(CurrentTablejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(CurrentServerjPanelLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(CurrentSessionjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(CurrentServerjPanelLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(14, 14, 14)
                                .addComponent(CurrentServerjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(CurrentServerjPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(ServerInternaljSplitPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(781, Short.MAX_VALUE))
        );
        CurrentServerjPanelLayout.setVerticalGroup(
            CurrentServerjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CurrentServerjPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(CurrentServerjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(CurrentServerjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CurrentServerjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(CurrentSessionjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CurrentServerjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(CurrentTablejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ServerInternaljSplitPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        MainWindowjSplitPane.setTopComponent(CurrentServerjPanel);

        jPanel46.setMaximumSize(new java.awt.Dimension(1600, 1200));
        jPanel46.setPreferredSize(new java.awt.Dimension(1600, 1200));

        jScrollPane22.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane22.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane22.setMaximumSize(new java.awt.Dimension(1600, 1200));
        jScrollPane22.setPreferredSize(new java.awt.Dimension(1600, 1200));

        jDesktopPane.setMaximumSize(new java.awt.Dimension(1920, 1200));
        jDesktopPane.setPreferredSize(new java.awt.Dimension(1920, 1200));

        javax.swing.GroupLayout jDesktopPaneLayout = new javax.swing.GroupLayout(jDesktopPane);
        jDesktopPane.setLayout(jDesktopPaneLayout);
        jDesktopPaneLayout.setHorizontalGroup(
            jDesktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2599, Short.MAX_VALUE)
        );
        jDesktopPaneLayout.setVerticalGroup(
            jDesktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1200, Short.MAX_VALUE)
        );

        jScrollPane22.setViewportView(jDesktopPane);

        javax.swing.GroupLayout jPanel46Layout = new javax.swing.GroupLayout(jPanel46);
        jPanel46.setLayout(jPanel46Layout);
        jPanel46Layout.setHorizontalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel46Layout.createSequentialGroup()
                .addComponent(jScrollPane22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel46Layout.setVerticalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane22, javax.swing.GroupLayout.DEFAULT_SIZE, 1047, Short.MAX_VALUE)
        );

        MainWindowjSplitPane.setBottomComponent(jPanel46);

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainWindowjSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addComponent(MainWindowjSplitPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1569, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 134, Short.MAX_VALUE))
        );

        jScrollPane15.setViewportView(jPanel31);

        javax.swing.GroupLayout jPanelMainWindowLayout = new javax.swing.GroupLayout(jPanelMainWindow);
        jPanelMainWindow.setLayout(jPanelMainWindowLayout);
        jPanelMainWindowLayout.setHorizontalGroup(
            jPanelMainWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 2560, Short.MAX_VALUE)
        );
        jPanelMainWindowLayout.setVerticalGroup(
            jPanelMainWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 1600, Short.MAX_VALUE)
        );

        ResultsjInternalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        ResultsjInternalFrame.setIconifiable(true);
        ResultsjInternalFrame.setMaximizable(true);
        ResultsjInternalFrame.setResizable(true);
        ResultsjInternalFrame.setTitle("Results");
        ResultsjInternalFrame.setMaximumSize(new java.awt.Dimension(1250, 1250));
        ResultsjInternalFrame.setNormalBounds(new java.awt.Rectangle(0, 0, 103, 33));
        ResultsjInternalFrame.setVisible(true);
        ResultsjInternalFrame.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                ResultsjInternalFrameInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
                ResultsjInternalFrameInternalFrameDeactivated(evt);
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        RSstackjSplitPane.setDividerLocation(250);

        jPanel39.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Amplify", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N
        jPanel39.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        ResultsAmplifySlopejTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ResultsAmplifySlopejTextField.setText("1");

        ResultsAmplifyBjTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ResultsAmplifyBjTextField.setText("0");
        ResultsAmplifyBjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ResultsAmplifyBjTextFieldKeyReleased(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel23.setText("a");

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel24.setText("b");

        javax.swing.GroupLayout jPanel39Layout = new javax.swing.GroupLayout(jPanel39);
        jPanel39.setLayout(jPanel39Layout);
        jPanel39Layout.setHorizontalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel39Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel39Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ResultsAmplifyBjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel39Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ResultsAmplifySlopejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel39Layout.setVerticalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel39Layout.createSequentialGroup()
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ResultsAmplifySlopejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(ResultsAmplifyBjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel43.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Normalize", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        ResultsNormalizejComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ResultsNormalizejComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "absolute", "relative", "constant", "order" }));

        javax.swing.GroupLayout jPanel43Layout = new javax.swing.GroupLayout(jPanel43);
        jPanel43.setLayout(jPanel43Layout);
        jPanel43Layout.setHorizontalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ResultsNormalizejComboBox, 0, 80, Short.MAX_VALUE)
        );
        jPanel43Layout.setVerticalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ResultsNormalizejComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        ResultsDeleteByjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DeleteBy", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N
        ResultsDeleteByjPanel.setLayout(new java.awt.GridBagLayout());

        ResultsDeleteByOrderjComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ResultsDeleteByOrderjComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ">", "<", ">=", "<=", "Between", "=", "!=", "&", "|", "BeginWith", "EndWith", "Contains" }));
        ResultsDeleteByOrderjComboBox.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 4);
        ResultsDeleteByjPanel.add(ResultsDeleteByOrderjComboBox, gridBagConstraints);

        ResultsDeleteByFieldjComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 38;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 0, 4);
        ResultsDeleteByjPanel.add(ResultsDeleteByFieldjComboBox, gridBagConstraints);

        Operand2ResultsDeleteByjTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 57;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 6, 7, 4);
        ResultsDeleteByjPanel.add(Operand2ResultsDeleteByjTextField, gridBagConstraints);

        operand1ResultsDeleteByjTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        operand1ResultsDeleteByjTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operand1ResultsDeleteByjTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 57;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 4);
        ResultsDeleteByjPanel.add(operand1ResultsDeleteByjTextField, gridBagConstraints);

        ResultsUniqueByjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "UniqueBy", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        ResultsUniqueByFieldjComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        javax.swing.GroupLayout ResultsUniqueByjPanelLayout = new javax.swing.GroupLayout(ResultsUniqueByjPanel);
        ResultsUniqueByjPanel.setLayout(ResultsUniqueByjPanelLayout);
        ResultsUniqueByjPanelLayout.setHorizontalGroup(
            ResultsUniqueByjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ResultsUniqueByFieldjComboBox, 0, 61, Short.MAX_VALUE)
        );
        ResultsUniqueByjPanelLayout.setVerticalGroup(
            ResultsUniqueByjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ResultsUniqueByFieldjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        SelectToTablejPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select To Table", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N
        SelectToTablejPanel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        SelectToTableFieldjComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        SelectToTableFieldjComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectToTableFieldjComboBoxActionPerformed(evt);
            }
        });

        selectToTableDestinationTableNamejComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        selectToTableDestinationTableNamejComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                selectToTableDestinationTableNamejComboBoxMouseReleased(evt);
            }
        });
        selectToTableDestinationTableNamejComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selectToTableDestinationTableNamejComboBoxItemStateChanged(evt);
            }
        });

        SelectToTableUniquejCheckBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        SelectToTableUniquejCheckBox.setText("unique");

        javax.swing.GroupLayout SelectToTablejPanelLayout = new javax.swing.GroupLayout(SelectToTablejPanel);
        SelectToTablejPanel.setLayout(SelectToTablejPanelLayout);
        SelectToTablejPanelLayout.setHorizontalGroup(
            SelectToTablejPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SelectToTablejPanelLayout.createSequentialGroup()
                .addComponent(SelectToTableUniquejCheckBox)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(SelectToTablejPanelLayout.createSequentialGroup()
                .addGroup(SelectToTablejPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(SelectToTableFieldjComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 91, Short.MAX_VALUE)
                    .addComponent(selectToTableDestinationTableNamejComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        SelectToTablejPanelLayout.setVerticalGroup(
            SelectToTablejPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SelectToTablejPanelLayout.createSequentialGroup()
                .addComponent(selectToTableDestinationTableNamejComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SelectToTableFieldjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(SelectToTableUniquejCheckBox)
                .addGap(26, 26, 26))
        );

        ResultsSelectByjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "SelectBy", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        ResultsSelectByOperatorjComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ResultsSelectByOperatorjComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ">", "<", ">=", "<=", "Between", "=", "!=", "&", "|", "BeginWith", "EndWith", "Contains" }));
        ResultsSelectByOperatorjComboBox.setToolTipText("");

        ResultsSelectByFieldjComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        operand1ResultsSelectByjTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        operand1ResultsSelectByjTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operand1ResultsSelectByjTextFieldActionPerformed(evt);
            }
        });

        Operand2ResultsSelectByjTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        javax.swing.GroupLayout ResultsSelectByjPanelLayout = new javax.swing.GroupLayout(ResultsSelectByjPanel);
        ResultsSelectByjPanel.setLayout(ResultsSelectByjPanelLayout);
        ResultsSelectByjPanelLayout.setHorizontalGroup(
            ResultsSelectByjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultsSelectByjPanelLayout.createSequentialGroup()
                .addGroup(ResultsSelectByjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ResultsSelectByOperatorjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Operand2ResultsSelectByjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(ResultsSelectByjPanelLayout.createSequentialGroup()
                .addGroup(ResultsSelectByjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ResultsSelectByFieldjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(operand1ResultsSelectByjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        ResultsSelectByjPanelLayout.setVerticalGroup(
            ResultsSelectByjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultsSelectByjPanelLayout.createSequentialGroup()
                .addComponent(ResultsSelectByFieldjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ResultsSelectByOperatorjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(operand1ResultsSelectByjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(Operand2ResultsSelectByjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        ResultsSortByjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "SortBy", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        ResultsSortByFieldComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        ResultsSortByOrderjComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ResultsSortByOrderjComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "descending", "ascending" }));

        javax.swing.GroupLayout ResultsSortByjPanelLayout = new javax.swing.GroupLayout(ResultsSortByjPanel);
        ResultsSortByjPanel.setLayout(ResultsSortByjPanelLayout);
        ResultsSortByjPanelLayout.setHorizontalGroup(
            ResultsSortByjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultsSortByjPanelLayout.createSequentialGroup()
                .addGroup(ResultsSortByjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(ResultsSortByFieldComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ResultsSortByOrderjComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 77, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ResultsSortByjPanelLayout.setVerticalGroup(
            ResultsSortByjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultsSortByjPanelLayout.createSequentialGroup()
                .addComponent(ResultsSortByFieldComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ResultsSortByOrderjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        SelectFromTablejPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select From Table", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N
        SelectFromTablejPanel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        SelectFromTablejPanel.setLayout(new java.awt.GridBagLayout());

        SelectFromTableModejComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        SelectFromTableModejComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "new", "add", "clear" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 6, 0, 0);
        SelectFromTablejPanel.add(SelectFromTableModejComboBox, gridBagConstraints);

        SelectFromTableFieldjComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        SelectFromTableFieldjComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectFromTableFieldjComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
        SelectFromTablejPanel.add(SelectFromTableFieldjComboBox, gridBagConstraints);

        OperatorSelectFromTablejComboBox1.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        OperatorSelectFromTablejComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ">", "<", ">=", "<=", "=", "Between" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        SelectFromTablejPanel.add(OperatorSelectFromTablejComboBox1, gridBagConstraints);

        operand1SelectFromTablejTextField1.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        operand1SelectFromTablejTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operand1SelectFromTablejTextField1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 53;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        SelectFromTablejPanel.add(operand1SelectFromTablejTextField1, gridBagConstraints);

        Operand2SelectFromTablejTextField1.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        Operand2SelectFromTablejTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Operand2SelectFromTablejTextField1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 67;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 7, 0);
        SelectFromTablejPanel.add(Operand2SelectFromTablejTextField1, gridBagConstraints);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ResultsSortByjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                    .addComponent(ResultsUniqueByjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ResultsDeleteByjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SelectToTablejPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SelectFromTablejPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ResultsSelectByjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ResultsSortByjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ResultsSelectByjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SelectToTablejPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SelectFromTablejPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jPanel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ResultsUniqueByjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ResultsDeleteByjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(36, 36, 36))
        );

        jScrollPane27.setViewportView(jPanel11);

        RSstackjSplitPane.setLeftComponent(jScrollPane27);

        ResultsStackContentjTable.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ResultsStackContentjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ResultsStackContentjTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ResultsStackContentjTableMouseReleased(evt);
            }
        });
        jScrollPane28.setViewportView(ResultsStackContentjTable);

        RSstackjSplitPane.setRightComponent(jScrollPane28);

        jScrollPane30.setViewportView(RSstackjSplitPane);

        javax.swing.GroupLayout ResultsjInternalFrameLayout = new javax.swing.GroupLayout(ResultsjInternalFrame.getContentPane());
        ResultsjInternalFrame.getContentPane().setLayout(ResultsjInternalFrameLayout);
        ResultsjInternalFrameLayout.setHorizontalGroup(
            ResultsjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane30, javax.swing.GroupLayout.DEFAULT_SIZE, 786, Short.MAX_VALUE)
        );
        ResultsjInternalFrameLayout.setVerticalGroup(
            ResultsjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultsjInternalFrameLayout.createSequentialGroup()
                .addComponent(jScrollPane30)
                .addContainerGap())
        );

        ContextsjInternalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        ContextsjInternalFrame.setIconifiable(true);
        ContextsjInternalFrame.setMaximizable(true);
        ContextsjInternalFrame.setResizable(true);
        ContextsjInternalFrame.setTitle("Contexts");
        ContextsjInternalFrame.setMaximumSize(new java.awt.Dimension(1250, 1250));
        ContextsjInternalFrame.setNormalBounds(new java.awt.Rectangle(0, 0, 107, 33));
        ContextsjInternalFrame.setPreferredSize(new java.awt.Dimension(477, 254));
        ContextsjInternalFrame.setVisible(true);
        ContextsjInternalFrame.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                ContextsjInternalFrameInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
                ContextsjInternalFrameInternalFrameDeactivated(evt);
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        ContextsjPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                ContextsjPanelComponentShown(evt);
            }
        });

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Contexts Stack", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        CtxStackAndContentjSplitPane.setDividerLocation(150);

        ContextsStackContentjTable.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ContextsStackContentjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ContextsStackContentjTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ContextsStackContentjTableMouseReleased(evt);
            }
        });
        jScrollPane20.setViewportView(ContextsStackContentjTable);

        CtxStackAndContentjSplitPane.setLeftComponent(jScrollPane20);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Context Content", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        ContextContentjTable.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ContextContentjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "shape", "activity", "generality class", "generality", "id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ContextContentjTable.setRowSelectionAllowed(false);
        ContextContentjTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ContextContentjTableMouseReleased(evt);
            }
        });
        ContextContentjTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                ContextContentjTableMouseMoved(evt);
            }
        });
        jScrollPane19.setViewportView(ContextContentjTable);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane19, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane19, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
        );

        CtxStackAndContentjSplitPane.setRightComponent(jPanel13);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(CtxStackAndContentjSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(CtxStackAndContentjSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
        );

        ContextsStackjSplitPane.setRightComponent(jPanel12);

        stringToContextjSplitPane.setDividerLocation(150);
        stringToContextjSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        stringToContextjSplitPane.setMaximumSize(new java.awt.Dimension(342, 371));
        stringToContextjSplitPane.setMinimumSize(new java.awt.Dimension(342, 371));
        stringToContextjSplitPane.setPreferredSize(new java.awt.Dimension(342, 371));

        ContextSortByjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "SortBy", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        ContextSortByFieldComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ContextSortByFieldComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "generality", "activity" }));

        ContextSortByOrderjComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ContextSortByOrderjComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "descending", "ascending" }));

        javax.swing.GroupLayout ContextSortByjPanelLayout = new javax.swing.GroupLayout(ContextSortByjPanel);
        ContextSortByjPanel.setLayout(ContextSortByjPanelLayout);
        ContextSortByjPanelLayout.setHorizontalGroup(
            ContextSortByjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ContextSortByFieldComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(ContextSortByOrderjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        ContextSortByjPanelLayout.setVerticalGroup(
            ContextSortByjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContextSortByjPanelLayout.createSequentialGroup()
                .addComponent(ContextSortByFieldComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(ContextSortByOrderjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        ContextUnionjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Union"));

        ContextUnionjComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "simple", "min", "max", "mean", "maxinc" }));

        javax.swing.GroupLayout ContextUnionjPanelLayout = new javax.swing.GroupLayout(ContextUnionjPanel);
        ContextUnionjPanel.setLayout(ContextUnionjPanelLayout);
        ContextUnionjPanelLayout.setHorizontalGroup(
            ContextUnionjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ContextUnionjComboBox, 0, 111, Short.MAX_VALUE)
        );
        ContextUnionjPanelLayout.setVerticalGroup(
            ContextUnionjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ContextUnionjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        ContextNormalizejPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Normalize", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        ContextNormalizejComboBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ContextNormalizejComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "absolute", "relative", "constant", "order" }));

        javax.swing.GroupLayout ContextNormalizejPanelLayout = new javax.swing.GroupLayout(ContextNormalizejPanel);
        ContextNormalizejPanel.setLayout(ContextNormalizejPanelLayout);
        ContextNormalizejPanelLayout.setHorizontalGroup(
            ContextNormalizejPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ContextNormalizejComboBox, 0, 74, Short.MAX_VALUE)
        );
        ContextNormalizejPanelLayout.setVerticalGroup(
            ContextNormalizejPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContextNormalizejPanelLayout.createSequentialGroup()
                .addComponent(ContextNormalizejComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ContextAmplifyjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Amplify", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N
        ContextAmplifyjPanel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N

        ContextAmplifySlopejTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ContextAmplifySlopejTextField.setText("1");

        ContextAmplifyBjTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ContextAmplifyBjTextField.setText("0");
        ContextAmplifyBjTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ContextAmplifyBjTextFieldKeyReleased(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel21.setText("a");

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel22.setText("b");

        javax.swing.GroupLayout ContextAmplifyjPanelLayout = new javax.swing.GroupLayout(ContextAmplifyjPanel);
        ContextAmplifyjPanel.setLayout(ContextAmplifyjPanelLayout);
        ContextAmplifyjPanelLayout.setHorizontalGroup(
            ContextAmplifyjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContextAmplifyjPanelLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(ContextAmplifyjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ContextAmplifyjPanelLayout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ContextAmplifySlopejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ContextAmplifyjPanelLayout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ContextAmplifyBjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ContextAmplifyjPanelLayout.setVerticalGroup(
            ContextAmplifyjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContextAmplifyjPanelLayout.createSequentialGroup()
                .addGroup(ContextAmplifyjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ContextAmplifySlopejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(ContextAmplifyjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ContextAmplifyBjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)))
        );

        ContextIntersectionjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Intersection"));

        ContextIntersectionjComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "simple", "min", "max", "mean", "maxinc" }));

        javax.swing.GroupLayout ContextIntersectionjPanelLayout = new javax.swing.GroupLayout(ContextIntersectionjPanel);
        ContextIntersectionjPanel.setLayout(ContextIntersectionjPanelLayout);
        ContextIntersectionjPanelLayout.setHorizontalGroup(
            ContextIntersectionjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ContextIntersectionjComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ContextIntersectionjPanelLayout.setVerticalGroup(
            ContextIntersectionjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ContextIntersectionjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout ContextCommandjPanelLayout = new javax.swing.GroupLayout(ContextCommandjPanel);
        ContextCommandjPanel.setLayout(ContextCommandjPanelLayout);
        ContextCommandjPanelLayout.setHorizontalGroup(
            ContextCommandjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContextCommandjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ContextCommandjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(ContextCommandjPanelLayout.createSequentialGroup()
                        .addComponent(ContextSortByjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ContextNormalizejPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ContextAmplifyjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ContextCommandjPanelLayout.createSequentialGroup()
                        .addComponent(ContextUnionjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ContextIntersectionjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(107, Short.MAX_VALUE))
        );
        ContextCommandjPanelLayout.setVerticalGroup(
            ContextCommandjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContextCommandjPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(ContextCommandjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ContextIntersectionjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ContextUnionjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ContextCommandjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ContextNormalizejPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ContextSortByjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ContextAmplifyjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jScrollPane25.setViewportView(ContextCommandjPanel);

        stringToContextjSplitPane.setTopComponent(jScrollPane25);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "stringToContext", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        stringToContextjTextArea.setColumns(20);
        stringToContextjTextArea.setFont(new java.awt.Font("Monospaced", 0, 9)); // NOI18N
        stringToContextjTextArea.setRows(5);
        stringToContextjTextArea.setText("paste text");
        jScrollPane24.setViewportView(stringToContextjTextArea);

        stringToContextLearnjCheckBox.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        stringToContextLearnjCheckBox.setText("learn");

        stringToContextjButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        stringToContextjButton.setText("stringToContext");
        stringToContextjButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                stringToContextjButtonMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane24)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(stringToContextLearnjCheckBox)
                .addGap(42, 42, 42)
                .addComponent(stringToContextjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(119, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stringToContextLearnjCheckBox)
                    .addComponent(stringToContextjButton, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane24, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))
        );

        stringToContextjSplitPane.setRightComponent(jPanel9);

        ContextsStackjSplitPane.setLeftComponent(stringToContextjSplitPane);

        javax.swing.GroupLayout ContextsjPanelLayout = new javax.swing.GroupLayout(ContextsjPanel);
        ContextsjPanel.setLayout(ContextsjPanelLayout);
        ContextsjPanelLayout.setHorizontalGroup(
            ContextsjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContextsjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ContextsStackjSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 675, Short.MAX_VALUE))
        );
        ContextsjPanelLayout.setVerticalGroup(
            ContextsjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContextsjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ContextsStackjSplitPane))
        );

        javax.swing.GroupLayout ContextsjInternalFrameLayout = new javax.swing.GroupLayout(ContextsjInternalFrame.getContentPane());
        ContextsjInternalFrame.getContentPane().setLayout(ContextsjInternalFrameLayout);
        ContextsjInternalFrameLayout.setHorizontalGroup(
            ContextsjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ContextsjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ContextsjInternalFrameLayout.setVerticalGroup(
            ContextsjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ContextsjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        KnowledgeGraphjInternalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        KnowledgeGraphjInternalFrame.setIconifiable(true);
        KnowledgeGraphjInternalFrame.setMaximizable(true);
        KnowledgeGraphjInternalFrame.setResizable(true);
        KnowledgeGraphjInternalFrame.setTitle("Knowledge Graph");
        KnowledgeGraphjInternalFrame.setMaximumSize(new java.awt.Dimension(1250, 1250));
        KnowledgeGraphjInternalFrame.setNormalBounds(new java.awt.Rectangle(0, 0, 108, 33));
        KnowledgeGraphjInternalFrame.setVisible(true);

        KnowledgeGraphjPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                KnowledgeGraphjPanelComponentShown(evt);
            }
        });

        jPanel32.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "query", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        jTextField8.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jTextField8.setText("paste your query and press Enter");
        jTextField8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField8KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jCheckBox1.setText("Auto refresh");
        jCheckBox1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox1MouseReleased(evt);
            }
        });

        masDepthjLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        masDepthjLabel.setText("max depth");

        maxDepthjSlider.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        maxDepthjSlider.setMajorTickSpacing(1);
        maxDepthjSlider.setMaximum(5);
        maxDepthjSlider.setMinimum(1);
        maxDepthjSlider.setMinorTickSpacing(1);
        maxDepthjSlider.setPaintTicks(true);
        maxDepthjSlider.setSnapToTicks(true);
        maxDepthjSlider.setValue(1);
        maxDepthjSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "max depth", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N
        maxDepthjSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                maxDepthjSliderMouseReleased(evt);
            }
        });

        maxDepthjTextField.setEditable(false);
        maxDepthjTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        maxDepthjTextField.setText("1");

        maxSizejLabel.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        maxSizejLabel.setText("max size");

        maxSizejTextField.setEditable(false);
        maxSizejTextField.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        maxSizejTextField.setText("1");

        maxSizejSlider.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        maxSizejSlider.setMajorTickSpacing(10);
        maxSizejSlider.setMaximum(50);
        maxSizejSlider.setMinimum(1);
        maxSizejSlider.setMinorTickSpacing(5);
        maxSizejSlider.setPaintTicks(true);
        maxSizejSlider.setSnapToTicks(true);
        maxSizejSlider.setValue(1);
        maxSizejSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "max results per depth", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N
        maxSizejSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                maxSizejSliderMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout KnowledgeGraphjPanelLayout = new javax.swing.GroupLayout(KnowledgeGraphjPanel);
        KnowledgeGraphjPanel.setLayout(KnowledgeGraphjPanelLayout);
        KnowledgeGraphjPanelLayout.setHorizontalGroup(
            KnowledgeGraphjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(KnowledgeGraphjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(KnowledgeGraphjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(masDepthjLabel)
                    .addComponent(maxSizejLabel))
                .addGroup(KnowledgeGraphjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(KnowledgeGraphjPanelLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(maxDepthjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(KnowledgeGraphjPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(maxSizejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(KnowledgeGraphjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(KnowledgeGraphjPanelLayout.createSequentialGroup()
                        .addComponent(maxDepthjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox1))
                    .addComponent(maxSizejSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(135, Short.MAX_VALUE))
        );
        KnowledgeGraphjPanelLayout.setVerticalGroup(
            KnowledgeGraphjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(KnowledgeGraphjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(KnowledgeGraphjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(KnowledgeGraphjPanelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(KnowledgeGraphjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxDepthjSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox1)
                            .addGroup(KnowledgeGraphjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(masDepthjLabel)
                                .addComponent(maxDepthjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(KnowledgeGraphjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(KnowledgeGraphjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(maxSizejLabel)
                        .addComponent(maxSizejTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(maxSizejSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(673, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout KnowledgeGraphjInternalFrameLayout = new javax.swing.GroupLayout(KnowledgeGraphjInternalFrame.getContentPane());
        KnowledgeGraphjInternalFrame.getContentPane().setLayout(KnowledgeGraphjInternalFrameLayout);
        KnowledgeGraphjInternalFrameLayout.setHorizontalGroup(
            KnowledgeGraphjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1024, Short.MAX_VALUE)
            .addGroup(KnowledgeGraphjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, KnowledgeGraphjInternalFrameLayout.createSequentialGroup()
                    .addContainerGap(58, Short.MAX_VALUE)
                    .addComponent(KnowledgeGraphjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(59, Short.MAX_VALUE)))
        );
        KnowledgeGraphjInternalFrameLayout.setVerticalGroup(
            KnowledgeGraphjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 827, Short.MAX_VALUE)
            .addGroup(KnowledgeGraphjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, KnowledgeGraphjInternalFrameLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(KnowledgeGraphjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        ResultSetContentjInternalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        ResultSetContentjInternalFrame.setIconifiable(true);
        ResultSetContentjInternalFrame.setMaximizable(true);
        ResultSetContentjInternalFrame.setResizable(true);
        ResultSetContentjInternalFrame.setTitle("ResultSet Content");
        ResultSetContentjInternalFrame.setMaximumSize(new java.awt.Dimension(1250, 1250));
        ResultSetContentjInternalFrame.setNormalBounds(new java.awt.Rectangle(0, 0, 103, 33));
        ResultSetContentjInternalFrame.setVisible(true);
        ResultSetContentjInternalFrame.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                ResultSetContentjInternalFrameInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
                ResultSetContentjInternalFrameInternalFrameDeactivated(evt);
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        ResultSetContentjInternalFrame.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ResultSetContentjInternalFrameMouseReleased(evt);
            }
        });

        jPanel35.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ResultSet Content", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        ResultSetContentjTable.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ResultSetContentjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        ResultSetContentjTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ResultSetContentjTableMouseReleased(evt);
            }
        });
        ResultSetContentjTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                ResultSetContentjTableMouseMoved(evt);
            }
        });
        jScrollPane26.setViewportView(ResultSetContentjTable);

        jButton7.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jButton7.setText("Fetch next");
        jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton7MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addComponent(jButton7)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jScrollPane26, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addComponent(jScrollPane26, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7))
        );

        jScrollPane6.setViewportView(jPanel35);

        javax.swing.GroupLayout ResultSetContentjInternalFrameLayout = new javax.swing.GroupLayout(ResultSetContentjInternalFrame.getContentPane());
        ResultSetContentjInternalFrame.getContentPane().setLayout(ResultSetContentjInternalFrameLayout);
        ResultSetContentjInternalFrameLayout.setHorizontalGroup(
            ResultSetContentjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6)
        );
        ResultSetContentjInternalFrameLayout.setVerticalGroup(
            ResultSetContentjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
        );

        ContextPropertiesjInternalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        ContextPropertiesjInternalFrame.setIconifiable(true);
        ContextPropertiesjInternalFrame.setMaximizable(true);
        ContextPropertiesjInternalFrame.setResizable(true);
        ContextPropertiesjInternalFrame.setTitle("Context Properties");
        ContextPropertiesjInternalFrame.setMaximumSize(new java.awt.Dimension(1250, 1250));
        ContextPropertiesjInternalFrame.setNormalBounds(new java.awt.Rectangle(0, 0, 107, 33));
        ContextPropertiesjInternalFrame.setPreferredSize(new java.awt.Dimension(424, 208));
        ContextPropertiesjInternalFrame.setVisible(true);
        ContextPropertiesjInternalFrame.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                ContextPropertiesjInternalFrameInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
                ContextPropertiesjInternalFrameInternalFrameDeactivated(evt);
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Contexts properties ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        ContextPropertiesjTable.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ContextPropertiesjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Property", "Value", "type", "access"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ContextPropertiesjTable.setCellSelectionEnabled(true);
        ContextPropertiesjTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ContextPropertiesjTableMouseReleased(evt);
            }
        });
        ContextPropertiesjTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                ContextPropertiesjTableMouseMoved(evt);
            }
        });
        ContextPropertiesjTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ContextPropertiesjTableKeyReleased(evt);
            }
        });
        jScrollPane21.setViewportView(ContextPropertiesjTable);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane21, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane21, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout ContextPropertiesjInternalFrameLayout = new javax.swing.GroupLayout(ContextPropertiesjInternalFrame.getContentPane());
        ContextPropertiesjInternalFrame.getContentPane().setLayout(ContextPropertiesjInternalFrameLayout);
        ContextPropertiesjInternalFrameLayout.setHorizontalGroup(
            ContextPropertiesjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ContextPropertiesjInternalFrameLayout.setVerticalGroup(
            ContextPropertiesjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContextPropertiesjInternalFrameLayout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(22, 22, 22))
        );

        ResultSetPropertiesjInternalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        ResultSetPropertiesjInternalFrame.setIconifiable(true);
        ResultSetPropertiesjInternalFrame.setMaximizable(true);
        ResultSetPropertiesjInternalFrame.setTitle("ResultSet Properties");
        ResultSetPropertiesjInternalFrame.setMaximumSize(new java.awt.Dimension(1250, 1250));
        ResultSetPropertiesjInternalFrame.setNormalBounds(new java.awt.Rectangle(0, 0, 103, 33));
        ResultSetPropertiesjInternalFrame.setVisible(true);
        ResultSetPropertiesjInternalFrame.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                ResultSetPropertiesjInternalFrameInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
                ResultSetPropertiesjInternalFrameInternalFrameDeactivated(evt);
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanel49.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Results properties ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N

        ResultsPropertiesjTable.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ResultsPropertiesjTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Property", "Value", "type", "access"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ResultsPropertiesjTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ResultsPropertiesjTableMouseReleased(evt);
            }
        });
        ResultsPropertiesjTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                ResultsPropertiesjTableMouseMoved(evt);
            }
        });
        ResultsPropertiesjTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ResultsPropertiesjTableKeyReleased(evt);
            }
        });
        jScrollPane29.setViewportView(ResultsPropertiesjTable);

        javax.swing.GroupLayout jPanel49Layout = new javax.swing.GroupLayout(jPanel49);
        jPanel49.setLayout(jPanel49Layout);
        jPanel49Layout.setHorizontalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel49Layout.createSequentialGroup()
                .addComponent(jScrollPane29, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel49Layout.setVerticalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel49Layout.createSequentialGroup()
                .addComponent(jScrollPane29, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout ResultSetPropertiesjInternalFrameLayout = new javax.swing.GroupLayout(ResultSetPropertiesjInternalFrame.getContentPane());
        ResultSetPropertiesjInternalFrame.getContentPane().setLayout(ResultSetPropertiesjInternalFrameLayout);
        ResultSetPropertiesjInternalFrameLayout.setHorizontalGroup(
            ResultSetPropertiesjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ResultSetPropertiesjInternalFrameLayout.setVerticalGroup(
            ResultSetPropertiesjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ResultSetPropertiesjInternalFrameLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        LogjInternalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        LogjInternalFrame.setIconifiable(true);
        LogjInternalFrame.setMaximizable(true);
        LogjInternalFrame.setResizable(true);
        LogjInternalFrame.setTitle("Scripts");
        LogjInternalFrame.setMaximumSize(new java.awt.Dimension(1250, 1250));
        LogjInternalFrame.setNormalBounds(new java.awt.Rectangle(0, 0, 96, 33));
        LogjInternalFrame.setPreferredSize(new java.awt.Dimension(371, 314));
        LogjInternalFrame.setVisible(true);

        logjTextArea.setColumns(20);
        logjTextArea.setRows(5);
        logjTextArea.setWrapStyleWord(true);
        jScrollPane31.setViewportView(logjTextArea);

        javax.swing.GroupLayout LogjInternalFrameLayout = new javax.swing.GroupLayout(LogjInternalFrame.getContentPane());
        LogjInternalFrame.getContentPane().setLayout(LogjInternalFrameLayout);
        LogjInternalFrameLayout.setHorizontalGroup(
            LogjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LogjInternalFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane31, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
                .addContainerGap())
        );
        LogjInternalFrameLayout.setVerticalGroup(
            LogjInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LogjInternalFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane31, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("mARC Administration Client");
        setMaximumSize(new java.awt.Dimension(1200, 1000));
        setName("MainFrame"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jScrollPane7.setMaximumSize(new java.awt.Dimension(1200, 826));
        jScrollPane7.setPreferredSize(new java.awt.Dimension(1200, 826));

        jPanel1.setMaximumSize(new java.awt.Dimension(1202, 824));
        jPanel1.setPreferredSize(new java.awt.Dimension(1202, 825));

        jSplitPane17.setDividerLocation(600);
        jSplitPane17.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane17.setPreferredSize(new java.awt.Dimension(1202, 815));

        ServersTree.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        ServersTree.setAutoscrolls(true);
        ServersTree.setMaximumSize(new java.awt.Dimension(59, 54));
        ServersTree.setPreferredSize(new java.awt.Dimension(59, 54));
        ServersTree.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                ServersTreeMouseMoved(evt);
            }
        });
        ServersTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ServersTreeMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(ServersTree);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jScrollPane10.setBackground(new java.awt.Color(0, 0, 0));

        DesktopsjPanel.setBackground(new java.awt.Color(0, 0, 0));
        DesktopsjPanel.setMaximumSize(new java.awt.Dimension(1600, 1200));
        DesktopsjPanel.setPreferredSize(new java.awt.Dimension(1600, 1200));

        javax.swing.GroupLayout DesktopsjPanelLayout = new javax.swing.GroupLayout(DesktopsjPanel);
        DesktopsjPanel.setLayout(DesktopsjPanelLayout);
        DesktopsjPanelLayout.setHorizontalGroup(
            DesktopsjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1600, Short.MAX_VALUE)
        );
        DesktopsjPanelLayout.setVerticalGroup(
            DesktopsjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1200, Short.MAX_VALUE)
        );

        jScrollPane10.setViewportView(DesktopsjPanel);

        jSplitPane1.setRightComponent(jScrollPane10);

        jSplitPane17.setTopComponent(jSplitPane1);

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Log", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 9))); // NOI18N
        jPanel16.setMaximumSize(new java.awt.Dimension(1200, 212));
        jPanel16.setPreferredSize(new java.awt.Dimension(1200, 212));

        LogjTextArea.setColumns(20);
        LogjTextArea.setRows(5);
        jScrollPane18.setViewportView(LogjTextArea);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 1188, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        );

        jSplitPane17.setRightComponent(jPanel16);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jScrollPane7.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 842, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void IndexationCacheSizeTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_IndexationCacheSizeTextFieldKeyReleased

        if (CurrentServer == null) {
            this.Updatelog("No server selected. Aborting \n");
            return;
        }
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            int cacheSize = -1;
            try {
                cacheSize = Integer.parseInt(IndexationCacheSizeTextField.getText());
            } catch (Exception e) {
                return;
            }
            mARCWorker w = new mARCWorker();
            w.ip = CurrentServer.ip;
            w.port = CurrentServer.port;
            w._frame = this;
            w.Action = "SetProperties";
            w.accessors = new String[]{"indexation_cache_size=" + IndexationCacheSizeTextField.getText()};
            w.execute();
        }
    }//GEN-LAST:event_IndexationCacheSizeTextFieldKeyReleased

    private void IndexationCacheUsedTextFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_IndexationCacheUsedTextFieldPropertyChange

    }//GEN-LAST:event_IndexationCacheUsedTextFieldPropertyChange

    private void IndexationTimeoutTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IndexationTimeoutTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IndexationTimeoutTextFieldActionPerformed

    private void IndexationTimeoutTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_IndexationTimeoutTextFieldKeyReleased

        if (CurrentServer == null) {
            this.Updatelog("No Server slected. Aborting \n");
            return;
        }
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            int i = -1;
            try {
                i = Integer.parseInt(this.IndexationTimeoutTextField.getText());
            } catch (Exception e) {
                return;
            }
            mARCWorker w = new mARCWorker();
            w.ip = CurrentServer.ip;
            w.port = CurrentServer.port;
            w._frame = this;
            w.Action = "SetProperties";
            w.accessors = new String[]{"indexation_timeout=" + IndexationTimeoutTextField.getText()};
            w.execute();
        }
    }//GEN-LAST:event_IndexationTimeoutTextFieldKeyReleased

    private void CommandThreadsTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CommandThreadsTextFieldKeyReleased

        if (CurrentServer == null) {
            this.Updatelog("No Server slected. Aborting \n");
            return;
        }
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            int i = -1;
            try {
                i = Integer.parseInt(CommandThreadsTextField.getText());
            } catch (Exception e) {
                return;
            }
            mARCWorker w = new mARCWorker();
            w.ip = CurrentServer.ip;
            w.port = CurrentServer.port;

            w._frame = this;
            w.Action = "SetProperties";
            w.accessors = new String[]{"command_threads=" + CommandThreadsTextField.getText()};
            w.execute();

        }
    }//GEN-LAST:event_CommandThreadsTextFieldKeyReleased

    private void CacheSizeTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CacheSizeTextFieldKeyReleased
        if (CurrentServer == null) {
            this.Updatelog("No Server slected. Aborting \n");
            return;
        }
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            int cacheSize = -1;
            try {
                cacheSize = Integer.parseInt(CacheSizeTextField.getText());
            } catch (Exception e) {
                return;
            }
            mARCWorker w = new mARCWorker();
            w.ip = CurrentServer.ip;
            w.port = CurrentServer.port;
            w._frame = this;
            w.Action = "SetProperties";
            w.accessors = new String[]{"cache_size=" + this.CacheSizeTextField.getText()};
            w.execute();
        }
    }//GEN-LAST:event_CacheSizeTextFieldKeyReleased

    private void ExecTimeoutDefaultTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ExecTimeoutDefaultTextFieldKeyReleased

        if (CurrentServer == null) {
            this.Updatelog("No Server slected. Aborting \n");
            return;
        }
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            int cacheSize = -1;
            try {
                cacheSize = Integer.parseInt(ExecTimeoutDefaultTextField.getText());
            } catch (Exception e) {
                return;
            }
            mARCWorker w = new mARCWorker();
            w.ip = CurrentServer.ip;
            w.port = CurrentServer.port;
            w._frame = this;
            w.Action = "SetProperties";
            w.accessors = new String[]{"exec_timeout_default=" + this.ExecTimeoutDefaultTextField.getText()};
            w.execute();
        }
    }//GEN-LAST:event_ExecTimeoutDefaultTextFieldKeyReleased

    private void SessionTimeoutDefaultTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SessionTimeoutDefaultTextFieldKeyReleased

        if (CurrentServer == null) {
            this.Updatelog("No Server slected. Aborting \n");
            return;
        }
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            int cacheSize = -1;
            try {
                cacheSize = Integer.parseInt(this.SessionTimeoutDefaultTextField.getText());
            } catch (Exception e) {
                return;
            }
            mARCWorker w = new mARCWorker();
            w.ip = CurrentServer.ip;
            w.port = CurrentServer.port;
            w._frame = this;
            w.Action = "SetProperties";
            w.accessors = new String[]{"session_timeout_default=" + SessionTimeoutDefaultTextField.getText()};
            w.execute();
        }
    }//GEN-LAST:event_SessionTimeoutDefaultTextFieldKeyReleased

    private void ServerNameTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ServerNameTextFieldKeyReleased
        if (CurrentServer == null) {
            this.Updatelog("No Server slected. Aborting \n");
            return;
        }
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            mARCWorker w = new mARCWorker();
            w.ip = CurrentServer.ip;
            w.port = CurrentServer.port;
            w._frame = this;
            w.Action = "SetProperties";
            w.accessors = new String[]{"name=" + ServerNameTextField.getText()};
            w.execute();

        }
    }//GEN-LAST:event_ServerNameTextFieldKeyReleased

    private void VersionTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VersionTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_VersionTextFieldActionPerformed

    public void mARCSave()
    {
      if (CurrentServer == null) 
      {
        this.Updatelog("No server selected. Aborting. \n");
        return;

        }
        mARCPanel.setEnabled(false);
        mARCWorker w = new mARCWorker();
        w.ip = this.CurrentServer.ip;
        w.port = this.CurrentServer.port;
        w.Action = "Save";
        w._frame = this;
        w.execute();
        mARCPanel.setEnabled(true);
    }
    public void mARCPublish()
    {
        if (CurrentServer == null) {
            this.Updatelog("No server selected. Aborting. \n");
            return;

        }
        mARCPanel.setEnabled(false);
        mARCWorker w = new mARCWorker();
        w.ip = this.CurrentServer.ip;
        w.port = this.CurrentServer.port;
        w._frame = this;
        w.Action = "Publish";
        w.execute();
        mARCPanel.setEnabled(true);
    }
    
    public void mARCShutDown()
    {
                if (CurrentServer == null) {
            this.Updatelog("No server selected. Aborting. \n");
            return;

        }
        mARCPanel.setEnabled(false);
        mARCWorker w = new mARCWorker();
        w.ip = this.CurrentServer.ip;
        w._frame = this;
        w.port = this.CurrentServer.port;
        w.Action = "ShutDown";
        w.restart = "";
        if (this.restartjCheckBox.isSelected()) {
            w.restart = "restart";
        }
        w.execute();
        mARCPanel.setEnabled(true);
    }
    public void mARCReload()
    {
               if (CurrentServer == null) {
            this.Updatelog("No server selected. Aborting. \n");
            return;

        }
        mARCPanel.setEnabled(false);
        mARCWorker w = new mARCWorker();
        w.ip = this.CurrentServer.ip;
        w.port = this.CurrentServer.port;
        w.Action = "Reload";
        w._frame = this;
        w.execute();
        mARCPanel.setEnabled(true);
    }
    public void mARCRebuild()
    {
                if (CurrentServer == null) {
            this.Updatelog("No server selected. Aborting. \n");
            return;

        }
        mARCPanel.setEnabled(false);
        mARCWorker w = new mARCWorker();
        w._frame = this;
        w.ip = this.CurrentServer.ip;
        w.port = this.CurrentServer.port;
        w.Action = "Rebuild";
        w.start = this.mARCRebuildFromjTextField.getText();
        w.end = this.mARCRebuildTojTextField.getText();
        w.columns = this.mARCRebuildFieldsjTextField.getText();
        w.RebuildRef = "none";
        if (this.RebuildRefjCheckBox.isSelected()) {
            w.RebuildRef = "ref";
        }
        w.execute();
        mARCPanel.setEnabled(true);
    }
    private void mARCRebuildFromjTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mARCRebuildFromjTextFieldKeyReleased
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            int i = -1;
            try {
                i = Integer.parseInt(this.mARCRebuildFromjTextField.getText());
            } catch (Exception e) {
                return;
            }
            this.mARCRebuildFromjTextField.setText(String.valueOf(i));
        }
    }//GEN-LAST:event_mARCRebuildFromjTextFieldKeyReleased

    private void mARCRebuildTojTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mARCRebuildTojTextFieldKeyReleased

        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            int i = -1;
            try {
                i = Integer.parseInt(this.mARCRebuildTojTextField.getText());
            } catch (Exception e) {
                return;
            }
            this.mARCRebuildTojTextField.setText(String.valueOf(i));
        }
    }//GEN-LAST:event_mARCRebuildTojTextFieldKeyReleased

    private void SessionPropertiesjTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SessionPropertiesjTableMouseReleased

        if (CurrentSession == null) {
            return;
        }
        int row = SessionPropertiesjTable.getSelectedRow();
        int col = SessionPropertiesjTable.getSelectedColumn();
        if (row == -1 || col == -1) {
            return;
        }
        if (Session.prop_access[row].equals("r")) {
            this.Updatelog("property SESSION.'" + Session.properties[row] + "' is Read Only. Aborting edition. \n");
            return;
        }
        boolean success = SessionPropertiesjTable.editCellAt(row, col);

        if (success) {
            SessionPropertiesjTable.changeSelection(row, col, false, false);
        }
    }//GEN-LAST:event_SessionPropertiesjTableMouseReleased

    private void SessionPropertiesjTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SessionPropertiesjTableMouseMoved
        
        if ( CurrentSession == null )
        {
            return;
        }
        if ( SessionPropertiesjTable.getRowCount() == 0 )
        {
            return;
        }
        java.awt.Point p = evt.getPoint();
        int rowIndex = SessionPropertiesjTable.rowAtPoint(p);
        int colIndex = SessionPropertiesjTable.columnAtPoint(p);
        if (colIndex == -1 && rowIndex == -1) {
            return;
        }
        int realColumnIndex = SessionPropertiesjTable.convertColumnIndexToModel(colIndex);

        DefaultTableModel m = (DefaultTableModel) SessionPropertiesjTable.getModel();
        SessionPropertiesjTable.setToolTipText((String) m.getValueAt(rowIndex, realColumnIndex));
    }//GEN-LAST:event_SessionPropertiesjTableMouseMoved

    private void SessionPropertiesjTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SessionPropertiesjTableKeyReleased

        int row = SessionPropertiesjTable.getSelectedRow();
        int col = SessionPropertiesjTable.getSelectedColumn();
        if ((row == -1 || col == -1) || CurrentSession == null) {
            return;
        }
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            SessionPropertiesjTable.setEnabled(false);
            DefaultTableModel m = (DefaultTableModel) SessionPropertiesjTable.getModel();
            String value = "";
            try {
                value = (String) m.getValueAt(row, col);
            } catch (Exception e) {
                e.printStackTrace();
                this.Updatelog(e.getMessage());
                SessionPropertiesjTable.setEnabled(true);
            }
            String property = (String) m.getValueAt(row, 0);
            if (property.equals("name")) {
                if (CurrentSession.name.startsWith("Session#")) {
                    Server.ReleaseASessionName(CurrentSession.name);
                }
            }
            SessionSetWorker w = new SessionSetWorker();
            w._frame = this;
            w.Action = "SetProperties";
            w.connector = CurrentSession.connector;
            w.accessors = new String[]{property + "=" + value};
            w.execute();
            SessionPropertiesjTable.setEnabled(true);
            
        }
    }//GEN-LAST:event_SessionPropertiesjTableKeyReleased

    private void ServerSessionsjTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ServerSessionsjTableMouseReleased

        if (CurrentServer == null) {
            return;
        }

        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) // bouton gauche
        {
            if (ServerSessionsjTable.getSelectedRow() == -1) 
            {
                return;
            }

            DefaultTableModel m = (DefaultTableModel) ServerSessionsjTable.getModel();
            String id = (String) m.getValueAt(ServerSessionsjTable.getSelectedRows()[0], 0);
            Session newSession = CurrentServer.FindSessionFromId(id);
            if ( newSession == null )
            {
                this.Updatelog("Session '"+id+"' not found. Aborting \n");
                return;
            }
            this.SwitchCurrentSession(newSession, true);
            // on se connecte pour verifier qu'elle est tjrs accessible
            SessionConnectWorker w = new SessionConnectWorker();
            w.id = id;
            w.connector = CurrentSession.connector;
            w._frame = this;
            w.execute();
        }
    }//GEN-LAST:event_ServerSessionsjTableMouseReleased

    private void jButton29MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton29MouseReleased

        addASession(-666,-666);
    }//GEN-LAST:event_jButton29MouseReleased

    private void jButton30MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton30MouseReleased

    }//GEN-LAST:event_jButton30MouseReleased

    private void SessionSpectrumjTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SessionSpectrumjTableMouseReleased

        int row = SessionSpectrumjTable.getSelectedRow();
        int col = SessionSpectrumjTable.getSelectedColumn();
        //on edit une ligne
        if (row != -1 && col != -1) {
            boolean success = SessionSpectrumjTable.editCellAt(row, col);
            if (success) {
                SessionSpectrumjTable.changeSelection(row, col, false, false);
            }
        }
    }//GEN-LAST:event_SessionSpectrumjTableMouseReleased

    private void SessionSpectrumjTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SessionSpectrumjTableMouseMoved
        
        if ( SessionSpectrumjTable.getRowCount() == 0 )
        {
            return;
        }
        java.awt.Point p = evt.getPoint();
        int rowIndex = SessionSpectrumjTable.rowAtPoint(p);
        int colIndex = SessionSpectrumjTable.columnAtPoint(p);
        if (colIndex == -1 || rowIndex == -1) 
        {
            return;
        }
        int realColumnIndex = SessionSpectrumjTable.convertColumnIndexToModel(colIndex);

        DefaultTableModel m = (DefaultTableModel) SessionSpectrumjTable.getModel();
        SessionSpectrumjTable.setToolTipText((String) m.getValueAt(rowIndex, realColumnIndex));
    }//GEN-LAST:event_SessionSpectrumjTableMouseMoved

    private void SessionSpectrumjTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SessionSpectrumjTableKeyReleased

        int row = SessionSpectrumjTable.getSelectedRow();
        int col = SessionSpectrumjTable.getSelectedColumn();
        if ((row == -1 && col == -1) || CurrentSession == null) {
            return;
        }
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            this.SessionSpectrumjTable.setEnabled(false);
            DefaultTableModel m = (DefaultTableModel) this.SessionSpectrumjTable.getModel();
            String value = (String) m.getValueAt(row, col);
            String property = (String) m.getValueAt(row, 0);
            ProcessContextActionWorker w = new ProcessContextActionWorker();
            w._frame = this;
            w.accessors = property + "=" + value;
            w.setSpectrum = true;
            w.getSpectrum = true;
            w.Action = "none";
            w.session = CurrentSession;
            w.execute();
            this.SessionSpectrumjTable.setEnabled(true);

        }
    }//GEN-LAST:event_SessionSpectrumjTableKeyReleased

    public void ShowContextContent(int contextIndex )
    {
        if ( CurrentSession == null )
        {
            return;
        }
        if ( contextIndex < 0 || contextIndex >= this.CurrentSession.contextsStack.stack.size() )
        {
            return;
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        this.ShownContextIndex = contextIndex;
        w.Action = "ShowOneContextProperties";
        w.session = CurrentSession;
        w._frame = this;
        w.contextIndex = contextIndex;
        CurrentContext = CurrentSession.contextsStack.stack.get(w.contextIndex);
        w.fetchAll = false;
        w.fetchContent = true;
        w.execute();
    }
    private void ContextsjPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_ContextsjPanelComponentShown




    }//GEN-LAST:event_ContextsjPanelComponentShown

    private void jTextField8KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyReleased

        if (CurrentSession == null) {
            this.Updatelog("No session selected. Aborting \n");
            return;
        }

        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            if (this.jTextField8.getText().isEmpty()) {
                this.Updatelog("No query. Aborting \n");
                return;
            }
            ProcessContextActionWorker w = new ProcessContextActionWorker();
            w._frame = this;
            w.range = this.maxDepthjTextField.getText();
            w.consolidation = this.maxSizejTextField.getText();
            w.Action = "ShowKnowLedgeGraph";
            w.accessors = this.jTextField8.getText();
            w.session = CurrentSession;
            w.execute();
        }
    }//GEN-LAST:event_jTextField8KeyReleased

    private void maxDepthjSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_maxDepthjSliderMouseReleased

        this.maxDepthjTextField.setText(String.valueOf(this.maxDepthjSlider.getValue()));
    }//GEN-LAST:event_maxDepthjSliderMouseReleased

    private void maxSizejSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_maxSizejSliderMouseReleased

        this.maxSizejTextField.setText(String.valueOf(this.maxSizejSlider.getValue()));
    }//GEN-LAST:event_maxSizejSliderMouseReleased

    private void KnowledgeGraphjPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_KnowledgeGraphjPanelComponentShown

    }//GEN-LAST:event_KnowledgeGraphjPanelComponentShown

    public void ShowResulSetStackContent(int RSindex)
    {
        if ( CurrentSession == null )
        {
            return;
        }
        if ( RSindex < 0 || RSindex >= this.CurrentSession.RSStack.stack.size())
        {
               return; 
        }
        
            ProcessResultsActionWorker w = new ProcessResultsActionWorker();
            w.Action = "ShowOneResultSetProperties";
            w.session = CurrentSession;
            w.ResultSetIndex = ResultsStackContentjTable.getSelectedRows()[0];
            w._frame = this;
            w.session = CurrentSession;
            w.fetchContent = true;
            w.execute();
    }
    private void ResultSetContentjTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultSetContentjTableMouseMoved

        java.awt.Point p = evt.getPoint();
        int rowIndex = ResultSetContentjTable.rowAtPoint(p);
        int colIndex = ResultSetContentjTable.columnAtPoint(p);
        if (colIndex == -1 && rowIndex == -1) {
            return;
        }
        int realColumnIndex = ResultSetContentjTable.convertColumnIndexToModel(colIndex);

        DefaultTableModel m = (DefaultTableModel) ResultSetContentjTable.getModel();
        ResultSetContentjTable.setToolTipText((String) m.getValueAt(rowIndex, realColumnIndex));
    }//GEN-LAST:event_ResultSetContentjTableMouseMoved

    private void ResultsPropertiesjTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultsPropertiesjTableMouseReleased

        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
            int row = ResultsPropertiesjTable.getSelectedRow();
            int col = ResultsPropertiesjTable.getSelectedColumn();

            if (row != -1 && col != -1) {
                if (ResultSet.access[row].equals("r")) {
                    this.Updatelog("property RESULTS." + ResultSet.properties[row] + "' is Read Only. Aborting edition. \n");
                    return;
                }
                boolean success = ResultsPropertiesjTable.editCellAt(row, col);
                if (success) {
                    ResultsPropertiesjTable.changeSelection(row, col, false, false);
                }
            }

        }
    }//GEN-LAST:event_ResultsPropertiesjTableMouseReleased

    private void ResultsPropertiesjTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultsPropertiesjTableMouseMoved

        java.awt.Point p = evt.getPoint();
        int rowIndex = ResultsPropertiesjTable.rowAtPoint(p);
        int colIndex = ResultsPropertiesjTable.columnAtPoint(p);
        if (colIndex == -1 && rowIndex == -1) {
            return;
        }
        int realColumnIndex = ResultsPropertiesjTable.convertColumnIndexToModel(colIndex);

        DefaultTableModel m = (DefaultTableModel) ResultsPropertiesjTable.getModel();
        ResultsPropertiesjTable.setToolTipText((String) m.getValueAt(rowIndex, realColumnIndex));
    }//GEN-LAST:event_ResultsPropertiesjTableMouseMoved

    private void ResultsPropertiesjTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ResultsPropertiesjTableKeyReleased

        if (CurrentSession == null) {
            this.Updatelog("No Session selected. Aborting \n");
            return;
        }

        int rowe = this.ResultsPropertiesjTable.getEditingRow();
        int cole = this.ResultsPropertiesjTable.getEditingColumn();
        int row = this.ResultsPropertiesjTable.getSelectedRow();
        int col = this.ResultsPropertiesjTable.getSelectedColumn();
        if ((row == -1 && col == -1) || CurrentSession == null) {
            return;
        }
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            this.ResultsPropertiesjTable.setEnabled(false);
            DefaultTableModel m = (DefaultTableModel) this.ResultsPropertiesjTable.getModel();
            String value = (String) m.getValueAt(row, 1);
            if (ResultSet.access[row].equals("r")) {
                this.Updatelog("property '" + ResultSet.properties[row] + "' is Read Only. Aborting \n");
                return;
            }
            String property = (String) m.getValueAt(row, 0);
            if (property.equals("name")) {
                if (this.CurrentResultSet == null) {
                    return;
                }
                if (CurrentResultSet.name.startsWith("ResultSet#")) {
                    ProcessResultsActionWorker.ReleaseARSName(CurrentResultSet.name);
                }
                //CurrentResultSet.name = value;
            } else if (property.equals("fetch_start")) {
                int start = -1;
                try {
                    start = Integer.parseInt(value);
                } catch (Exception e) {
                    this.Updatelog("Bad value for property 'fetch_start'. \n");
                    m.setValueAt("1", row, 1);
                }
                if (value.equals("0")) {
                    m.setValueAt("1", row, 1);
                }
                value = "1";
            } else if (property.equals("format")) 
            {
                if ( !value.toLowerCase().contains("rowid"))
                {
                    value = "RowId "+value;
                }
                CurrentServer.currentRSFormat = value;
            }
            ProcessResultsActionWorker w = new ProcessResultsActionWorker();
            w.ResultSetIndex = CurrentSession.getIndexOfResultSet(CurrentResultSet);
            w.ResultSetIndex = this.CurrentRSIndex;
            if (w.ResultSetIndex == -1) {
                this.Updatelog("No ResultSet selected. Select one before setting property '" + property + "' \n");
                return;
            }
            w._frame = this;
            w.Action = "ResultsSetProperties";
            w.accessors = new String[]{property + "=" + value};
            w.session = CurrentSession;
            w.fetchContent = true;
            w.execute();
            this.ResultsPropertiesjTable.setEnabled(true);

        }
    }//GEN-LAST:event_ResultsPropertiesjTableKeyReleased

    private void jButton7MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton7MouseReleased

        if (CurrentSession == null) {
            this.Updatelog("No session selected. Aborting \n");
            return;
        }
        /*
        int row = ResultsStackContentjTable.getSelectedRow();
        if (row == -1) {
            CurrentResultSet = null;
            return;
        }

        try {
            CurrentResultSet = CurrentSession.RSStack.stack.get(row);
        } catch (Exception e) {
            this.Updatelog("selected ResultSet not existing. Aborting \n");
            CurrentResultSet = null;
            return;
        }
        */
        if ( this.CurrentResultSet == null )
        {
            this.Updatelog("No Result Set selected. Aborting \n");
            return;
        }
        ResultSet rs = null;
        try 
        {
            rs = CurrentSession.RSStack.stack.get(this.CurrentRSIndex);
        } 
        catch (Exception e) 
        {
            this.Updatelog("selected ResultSet not existing. Aborting \n");
            CurrentResultSet = null;
            return;
        }   
        if ( rs != this.CurrentResultSet)
        {
            int idx = this.CurrentSession.getIndexOfResultSet(rs);
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.Action = "ShowOneResultSetProperties";
        w.session = CurrentSession;
        w.fetchNext = true;
        w.ResultSetIndex = this.CurrentRSIndex;
        w._frame = this;
        w.session = CurrentSession;
        w.fetchContent = true;
        w.execute();
    }//GEN-LAST:event_jButton7MouseReleased

    public void updateStatus()
    {
                        if ( CurrentServer != null )
                {
                    this.CurrentServerjTextField.setText(CurrentServer.name);
                }
                else
                {
                    this.CurrentServerjTextField.setText("");
                }
                if ( CurrentSession != null )
                {
                    this.CurrentSessionjTextField.setText(CurrentSession.id);
                }
                else
                {
                    this.CurrentSessionjTextField.setText("");
                }
                if ( CurrentTable == null)
                {
                    this.CurrentTablejTextField.setText("");
                }
                else
                {
                    this.CurrentTablejTextField.setText(CurrentTable.name);
                }
    }
    private void ServersTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ServersTreeMouseReleased

        TreePath theSelectedPath = ServersTree.getPathForLocation(evt.getX(), evt.getY());

        if (evt.getButton() == MouseEvent.BUTTON1) 
        {
            serversTreePopUpMenu._frame = this;
            serversTreePopUpMenu.setVisible(false);

            if (theSelectedPath == null) 
            {
                return;
            }

            /*
             if ( theSelectedPath != null )
             {
             ServersTree.expandPath(theSelectedPath);
             }
             if ( currentServersTreeSelectedPath != null )
             {
             ServersTree.collapsePath(currentServersTreeSelectedPath);
             }
             */
            currentServersTreeSelectedPath = theSelectedPath;

            int count = theSelectedPath.getPathCount();

            if (theSelectedPath.getPathCount() == 2) 
            {
                // on a clique sur un server
               
                if (timerToUpdateServerStats.isRunning()) 
                {
                    timerToUpdateServerStats.stop();
                }
                DefaultMutableTreeNode serverNode = (DefaultMutableTreeNode) theSelectedPath.getLastPathComponent();
                Server server = (Server) serverNode.getUserObject();

                if (!server.connector.getIsConnected()) 
                {
                    server.connector.connect();
                    if (!server.connector.getIsConnected()) {
                        this.Updatelog("Could not connect to server. Socket is not connected. check ip and port. \n");
                        return;
                    }
                    server.Session_Id = server.connector.getKmScriptSession();
                    //
                    server.ServerSession.owner = server;
                    server.ServerSession.id = server.Session_Id;
                    server.ServerSession.connector = server.connector;
                }

                this.SwitchCurrentServer(server, true);
                
            }
            else if ( theSelectedPath.getPathCount() == 4)
            {
                DefaultMutableTreeNode theparentNode = (DefaultMutableTreeNode) theSelectedPath.getPathComponent(2);
                String p = (String) theparentNode.getUserObject() ;
                switch (p) {
                    case "Tables":
                        {
                            DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) theSelectedPath.getLastPathComponent();
                            String name = (String) theNode.getUserObject();
                            DefaultMutableTreeNode serverNode = (DefaultMutableTreeNode) theSelectedPath.getPathComponent(1);
                            Server server = (Server) serverNode.getUserObject();
                            this.SwitchCurrentServer(server, true);
                            Table t = CurrentServer.FindTableFromName(name);
                            if ( t != null )
                            {
                                CurrentTable = t;
                            }       
                            this.updateStatus();
                            if (this.TablesjTable.getRowCount() != 0)
                            {
                                DefaultTableModel m = (DefaultTableModel) this.TablesjTable.getModel();
                                int i = 0;
                                for (; i < m.getRowCount();i++)
                                {
                                    String tname = (String) m.getValueAt(i, 0);
                                    if ( tname.equals(name))
                                    {
                                        break;
                                    }
                                }
                                if ( i == m.getRowCount() )
                                {
                                    this.Updatelog("ERROR Unable to find table '"+name+"' \n");
                                    return;
                                }
                                if ( !this.TablesjTable.isRowSelected(i))
                                {
                                    this.TablesjTable.changeSelection(i, 0, true, false);
                                }
                            }
                            this.UpdateTablesFrame();
                            this.jDesktopPane.getDesktopManager().activateFrame(this.TablesjInternalFrame);
                            try
                            {
                                this.TablesjInternalFrame.setSelected(true);
                            }
                            catch(Exception e)
                            {
                                
                            }       
                    JInternalFrame[] f = this.jDesktopPane.getAllFrames();

                    this.TableVisualjInternalFrame.moveToFront();
                    this.TablesjInternalFrame.moveToFront();
                    this.TablesjInternalFrame.grabFocus();
                    this.TablesjInternalFrame.requestFocus();
                    break;
                 }
                    case "Sessions":
                    {
                        // si la session cliquee n'est pas dans le serveur en cours
                        // on doit sauver le serveur et la session en cours si ils existent
                        //
                        DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) theSelectedPath.getLastPathComponent();
                        String name = (String) theNode.getUserObject();
                        DefaultMutableTreeNode serverNode = (DefaultMutableTreeNode) theSelectedPath.getPathComponent(1);
                        
                     if ( CurrentSession != null && CurrentSession.name.equals(name))
                     {
                         return;
                     }

                     // on switch le server si necessaire
                     Server server = (Server) serverNode.getUserObject();
                     Session newCurrentSession =  server.FindSessionFromId(name);
                     if ( server != this.CurrentServer )
                     {
                         this.SwitchCurrentServer(server, true);
                     }     
                     this.SwitchCurrentSession(newCurrentSession,true);
                    break;
                  }
                }
                
            }
        } else if (evt.getButton() == MouseEvent.BUTTON3) {
            if (theSelectedPath != null && theSelectedPath.getPathCount() == 3) {
                // une session,  une table un BTree ou un KTree
                DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) theSelectedPath.getLastPathComponent();
                DefaultMutableTreeNode theparentNode = (DefaultMutableTreeNode) theNode.getParent();
                if (theNode.getUserObject() instanceof String) {
                    String nodeName = (String) theNode.getUserObject();
                    switch (nodeName) {
                        case "mARC":
                            this.marcPopUpMenu._frame = this;
                            this.marcPopUpMenu.show(evt.getComponent(), evt.getX()+10, evt.getY());
                            return;
                        case "Tables":
                            if ( CurrentServer == null )
                            {
                                DefaultMutableTreeNode serverNode = (DefaultMutableTreeNode) theSelectedPath.getPathComponent(1);
                                CurrentServer = (Server) serverNode.getUserObject();
                                this.updateStatus();
                            }
                            this.tablesPopUpMenu._frame = this;
                            this.tablesPopUpMenu.mouseX = evt.getX();
                            this.tablesPopUpMenu.mouseY = evt.getY();
                            this.tablesPopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                            return;
                        case "Sessions":
                            if ( CurrentServer == null )
                            {
                                DefaultMutableTreeNode serverNode = (DefaultMutableTreeNode) theSelectedPath.getPathComponent(1);
                                Server server = (Server) serverNode.getUserObject(); 
                                this.SwitchCurrentServer(server, true);
                                
                            }
                            this.sessionsAddPopUpMenu._frame = this;
                            this.sessionsAddPopUpMenu.mouseX = evt.getX();
                            this.sessionsAddPopUpMenu.mouseY = evt.getY();
                            this.sessionsAddPopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                            return;
                    }

                }
            }
            if (theSelectedPath != null && theSelectedPath.getPathCount() == 4) {
                DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) theSelectedPath.getLastPathComponent();
                DefaultMutableTreeNode theparentNode = (DefaultMutableTreeNode) theNode.getParent();
                if (theparentNode.getUserObject() instanceof String) {
                    String nodeName = (String) theparentNode.getUserObject();
                    switch (nodeName) {
                        case "K-Trees":
                            this.tablesKIndexesTreePopUpMenu._frame = this;
                            this.tablesKIndexesTreePopUpMenu.x = evt.getX();
                            this.tablesKIndexesTreePopUpMenu.y = evt.getY();
                            this.tablesKIndexesTreePopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                            return;
                        case "B-Trees":
                            this.tablesBIndexesTreePopUpMenu._frame = this;
                            this.tablesBIndexesTreePopUpMenu.x = evt.getX();
                            this.tablesBIndexesTreePopUpMenu.y = evt.getY();
                            this.tablesBIndexesTreePopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                            return;
                        case "Tables":
                            this.tableKillPopUpMenu._frame = this;
                            this.tableKillPopUpMenu.mouseX = evt.getX();
                            this.tableKillPopUpMenu.mouseY = evt.getY();
                            this.tableKillPopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                        return;
                        case "Sessions":
                            this.sessionRemovePopUpMenu._frame = this;
                            this.sessionRemovePopUpMenu.mouseX = evt.getX();
                            this.sessionRemovePopUpMenu.mouseY = evt.getY();
                            this.sessionRemovePopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                        return;
                    }
                }

            }
            serversTreePopUpMenu._frame = this;
            serversTreePopUpMenu.show(evt.getComponent(), evt.getX() + 5, evt.getY());
            serversTreePopUpMenu.mouseX = evt.getX();
            serversTreePopUpMenu.mouseY = evt.getY();

        }
    }//GEN-LAST:event_ServersTreeMouseReleased

    public void SwitchCurrentServer(Server server, boolean push)
    {
     if ( server == null )
     {
         return;
     }
     
     if ( push )
     {
         server.PushUISettings();
     }
     
     if( server == this.CurrentServer )
     {
         this.timerToUpdateServerStats.start();
         return;
     }
     
     if ( !server.connector.getIsConnected())
     {
        if ( !server.connector.connect())
        {
            this.Updatelog("ERROR. Unable to connect to server '"+server.ip+":"+server.port+". Aborting \n");
            return;
        }
     }
    this.timerToUpdateServerStats.stop();
    this.jDesktopPane.setEnabled(false);
    this.jDesktopPane.setVisible(false);

    if ( CurrentServer != null )
     {
         CurrentServer.PushUISettings();
     }

     this.CurrentServer = server;
     this.CurrentTable = server.CurrentTable;
     
     CurrentServer.PopUISettings();
     

     this.InitializeTableContentFrame();
     this.UpdateTablesFrame();
     this.UpdateTableContent(true);
     this.serverUpdaterActionListener.server = server;
     this.updateStatus();
     updateServerWorker = new UpdateServerWorker(this.CurrentServer);
     updateServerWorker.frame = this;
     updateServerWorker.execute();
     timerToUpdateServerStats.start();
     
     this.jDesktopPane.setVisible(true);
     this.jDesktopPane.setEnabled(true);
    }
     
    public void SwitchCurrentSession(Session session, boolean push)
    {
        if ( session == null )
        {
            return;
        }
        
        if ( CurrentSession != null && push)
        {
            CurrentSession.PushUISettings();
        }
        if( session == this.CurrentSession && !this.loadingSettings )
        {
            return;
        }
        this.CurrentSession = session;
        
        CurrentSession.PopUISettings();
        this.updateStatus();
    }
    public void ReInitializeFrames()
    {
        if ( CurrentServer == null )
        {
            return;
        }
        
        //CurrentServer.update();
        this.CurrentSession = CurrentServer.CurrentSession;
        this.CurrentContext = CurrentServer.CurrentContext;
        this.updateStatus();
        this.jDesktopPane.setEnabled(false);
        this.jDesktopPane.setVisible(false);
       this.SizejTextField.setText(CurrentServer.TableContentSize);
       this.StartjTextField.setText(CurrentServer.TableContentStart);
       this.TableLinesjSlider.setValue(CurrentServer.TableContentSliderValue);
       this.ContentTablejTextArea.setText(CurrentServer.TableContentTextAreaText );
       if ( CurrentServer.showTableContent)
       {
           this.UpdateTableContent(false);
       }
       
       if ( CurrentSession != null )
       {
           if ( CurrentSession.shownContextIndex != -1)
            {
                this.ShowContextContent(CurrentSession.shownContextIndex);
            }
            if ( CurrentSession.showResulSetStack)
            {
                this.ShowResulSetStackContent(CurrentSession.shownRSContentIndex);
            }
       }
       
       if (CurrentServer.showFieldsTable)
       {
           DefaultTableModel m = (DefaultTableModel) this.FieldsTable.getModel();
           CurrentServer.CurrentTable.FieldsToTable(m);
       }
        this.jDesktopPane.setVisible(true);
        this.jDesktopPane.setEnabled(true);
        FrameSpecs specs = null;
        for (JInternalFrame f : this.jDesktopPane.getAllFrames())
        {
            
           if ( CurrentServer.findFrameFromTitle(f.getTitle(),specs) )
           {
               try
               {
               f.setSelected(specs.isSelected);
               f.setIcon(specs.isIcon);
               f.setMaximum(specs.isMaximized);
               f.setLocation(specs.location);
               f.setSize(specs.size);
               }
               catch(Exception e)
               {
                   this.Updatelog(e.getMessage());
                   break;
               }
           }
        }
    }
    
    private void ServersTreeMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ServersTreeMouseMoved

        /*
        TreePath thePath = ServersTree.getPathForLocation(evt.getX(), evt.getY());
        if (thePath == null) {
            return;
        }
        // on recupere la session
        if (thePath.getPathCount() == 2) {
            // c'est un server

        } else if (thePath.getPathCount() == 4) {
            //on recupere son parent pour identification
            DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) thePath.getLastPathComponent();
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) theNode.getParent();
            DefaultMutableTreeNode serverNode = (DefaultMutableTreeNode) parentNode.getParent();

            String parentName = (String) parentNode.getUserObject();
            Server server = (Server) serverNode.getUserObject();

            if (parentName.equals("Sessions")) 
            {
                
                DefaultTableModel m = (DefaultTableModel) this.SessionPropertiesjTable.getModel();
                m.setRowCount(0);
                String SessionId = (String) theNode.getUserObject();
                Session session = server.FindSessionFromId(SessionId);
                if (session == null) {
                    this.Updatelog("ERROR session not found \n");
                    return;
                }
                int i = 0;
                for (String value : session.values) {
                    m.addRow(new String[]{Session.properties[i], value, Session.types[i], Session.prop_access[i++]});
                }
            //this.SessionPropertiesjPanel.setLocation( evt.getX()+ 5 ,  evt.getY());
                //this.SessionPropertiesjPanel.setSize(347, 353);
                //this.ServersTree.add(this.SessionPropertiesjPanel);
                //this.SessionPropertiesjPanel.revalidate();
                // this.SessionPropertiesjPanel.repaint();
            }
        }
        */
    }//GEN-LAST:event_ServersTreeMouseMoved

    private void CacheSizeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CacheSizeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CacheSizeTextFieldActionPerformed

    private void TablesPanelComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_TablesPanelComponentResized

    }//GEN-LAST:event_TablesPanelComponentResized

    private void TablesjTableComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_TablesjTableComponentResized

    }//GEN-LAST:event_TablesjTableComponentResized

    public void InitializeTableContentFrame()
    {
        DefaultTableModel model = (DefaultTableModel) this.ContentjTable.getModel();
        model.setRowCount(0);
        model.setColumnCount(0);
        this.ContentTablejTextArea.setText("");
    }
    public void UpdateTablesFrame()
    {
        if ( CurrentServer == null) {
            this.Updatelog("No server selected. Aborting \n");
            return;
        }
        if ( CurrentTable == null) 
        {    
            return;
            /*
            DefaultTableModel model = (DefaultTableModel) FieldsTable.getModel();
            model.setRowCount(0);
            model = (DefaultTableModel) this.TablesjTable.getModel();
            model.setRowCount(0);   
            */
        }
        else
        {
            DefaultTableModel model = (DefaultTableModel) FieldsTable.getModel();

            if (CurrentTable.fields.size() > 0) 
            {
                CurrentTable.FieldsToTable(model);
            }
            if (CurrentTable.kIndexes.size() > 0) {
                model = (DefaultTableModel) KTreesjTable.getModel();
                CurrentTable.kIndexesToTable(model);

            }
            if (CurrentTable.bIndexes.size() > 0) {
                model = (DefaultTableModel) BTreejTable.getModel();
                CurrentTable.bIndexesToTable(model);
            }

            model = (DefaultTableModel) this.TablesjTable.getModel();
            this.TablesjTable.clearSelection();
            for (int i = 0; i < model.getRowCount(); i++)
            {
                String n = (String) model.getValueAt(i, 0);
                if ( n.equals(this.CurrentTable.name) )
                {
                    CurrentServer.SelectedTableIndex = i;
                    this.TablesjTable.addRowSelectionInterval(i,i);
                    break;
                }
            }
        }
        CurrentServer.PopTablesFrameSettings();

        this.TableLinesjSlider.setMinimum(1);
        if ( CurrentTable != null )
        {
            this.TableLinesjSlider.setMaximum(Integer.parseInt(CurrentTable.lines));
        }
    }
    private void TablesjTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablesjTableMouseReleased

        if ( CurrentServer == null) {
            this.Updatelog("No server selected. Aborting \n");
            return;
        }
        CurrentServer.SelectedTableIndex = TablesjTable.getSelectedRow();
        if ( CurrentServer.SelectedTableIndex == -1)
        {
            this.Updatelog("No table selected. Aborting \n");
            return;
            
        }
        String tblName = (String) TablesjTable.getValueAt(CurrentServer.SelectedTableIndex, 0);

        Table thetbl = CurrentServer.FindTableFromName(tblName);
        if (thetbl == null)
        {
            CurrentServer.SelectedTableIndex = -1;
            return;
        }

        CurrentTable = thetbl;
        CurrentServer.currentTableName = thetbl.name;
        this.updateStatus();
        this.UpdateTablesFrame();
    }//GEN-LAST:event_TablesjTableMouseReleased

    private void jButton2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseReleased

        if ( this.CurrentServer == null )
        {
            return;
        }
        int start = Integer.parseInt(StartjTextField.getText()) + Integer.parseInt(SizejTextField.getText());
        int end = start + Integer.parseInt(SizejTextField.getText());

        if (end >= Integer.parseInt(CurrentTable.lines)) {
            end = Integer.parseInt(CurrentTable.lines) - 1;
            start = end - Integer.parseInt(SizejTextField.getText());
        }

        StartjTextField.setText(String.valueOf(start));
        this.UpdateTableContent(false);
    }//GEN-LAST:event_jButton2MouseReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseReleased
       
        if ( this.CurrentServer == null )
        {
            return;
        }
        int start = Integer.parseInt(StartjTextField.getText()) - Integer.parseInt(SizejTextField.getText());

        if (start < 0) {
            StartjTextField.setText("0");

        } else {
            StartjTextField.setText(String.valueOf(start));
        }

        this.UpdateTableContent(false);
        
    }//GEN-LAST:event_jButton1MouseReleased

    private void SizejTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SizejTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SizejTextFieldActionPerformed

    private void SizejTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SizejTextFieldKeyReleased

        if ( this.CurrentServer == null )
        {
            return;
        }
        int value = 0;
        try {
            value = Integer.parseInt(SizejTextField.getText());
        } catch (Exception e) {
            return;
        }
        if (value > Integer.parseInt(CurrentTable.lines)) {
            value = Integer.parseInt(CurrentTable.lines) - 1;
        }
        SizejTextField.setText(String.valueOf(value));

        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) 
        {
            this.CurrentServer.PushTableContentFrameSettings();   
            this.UpdateTableContent(false);
        }
    }//GEN-LAST:event_SizejTextFieldKeyReleased

    private void StartjTextFieldMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_StartjTextFieldMouseReleased

    }//GEN-LAST:event_StartjTextFieldMouseReleased

    private void StartjTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_StartjTextFieldKeyReleased

        if ( this.CurrentServer == null )
        {
            return;
        }
        int value = 0;
        try {
            value = Integer.parseInt(StartjTextField.getText());
        } catch (Exception e) {
            return;
        }
        if (value < 0) {
            value = 0;
        }
        TableLinesjSlider.setValue(value);
        StartjTextField.setText(String.valueOf(value));

        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
        {
            this.CurrentServer.PushTableContentFrameSettings();
            this.UpdateTableContent(false);

        }
    }//GEN-LAST:event_StartjTextFieldKeyReleased

    public void UpdateTableContent(boolean restore)
    {
        if ( this.CurrentTable == null || this.CurrentServer == null )
        {
            if ( this.CurrentServer == null )
            {
                this.Updatelog("No server selected. \n");
            }
            if ( this.CurrentTable == null )
            {
                this.Updatelog("No table selected. \n");
            }            
            return;
        }
        this.CurrentServer.PopTableContentFrameSettings();
        this.ContentjTable.setEnabled(false);
        String[] fields = new String[FieldsTable.getSelectedRows().length + 1];

        DefaultTableModel model = (DefaultTableModel) FieldsTable.getModel();

        int k = 0;
        fields[k++] = "RowId";
        String v = "";
        boolean showAbstract = false;
        int idx = -1;
        for (int i : FieldsTable.getSelectedRows()) 
        {
            v = (String) model.getValueAt(i, 0);
            if (v.contains("ABSTRACT")) {
                showAbstract = true;
                idx = k;
            }
            fields[k++] = v;
        }
        UpdateTableContentWorker TableContentWorker = new UpdateTableContentWorker();
        TableContentWorker.ip = CurrentServer.ip;
        TableContentWorker.port = CurrentServer.port;
        TableContentWorker.showABSTRACT = showAbstract;
        TableContentWorker.restore = restore;
        TableContentWorker.abstractFielIndex = idx;
        TableContentWorker.table = CurrentTable;
        TableContentWorker.start = Integer.parseInt(StartjTextField.getText());
        TableContentWorker.size = Integer.parseInt(SizejTextField.getText());
        TableContentWorker.fields = fields;
        TableContentWorker._frame = this;
        TableContentWorker.execute();
    }
    
    private void TableLinesjSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableLinesjSliderMouseReleased

        if ( this.CurrentServer == null )
        {
            return;
        }
        StartjTextField.setText(String.valueOf(TableLinesjSlider.getValue()));

        this.UpdateTableContent(false);
    }//GEN-LAST:event_TableLinesjSliderMouseReleased

        public void ShowTableContentRowId()
    {
        if ( this.ContentjTable.getRowCount() == 0 )
        {
            return;
        }
         DefaultTableModel m = (DefaultTableModel) ContentjTable.getModel();
         if ( this.shownTableContentRowId.equals("-1") )
         {
             return;
         }
         //on transcrit en row de la table
         int row = -1;
         for (int ii = 0; ii<m.getRowCount();ii++)
         {
             String rs = (String) m.getValueAt(ii, 0);
             if ( rs.equals(this.shownTableContentRowId))
             {
                 row = ii;
                 break;
             }
         }
         if ( row == -1)
         {
             return;
         }
         
         String tablename = CurrentTable.name;
         int colid = ContentjTable.getSelectedColumn();
         String field = ContentjTable.getColumnName(colid);
         TableField tf = CurrentTable.FindTableFieldFromName(field);

         if (!tf.type.equals("STRING") && !tf.type.contains("ABSTRACT")) 
         {
             ContentTablejTextArea.setText((String) m.getValueAt(row, colid));

         }
         if (tf.type.contains("ABSTRACT")) 
         {
         ContentTablejTextArea.setText((String) m.getValueAt(row, colid));
         }
         if (tf.type.equals("STRING")) 
         {
             ContentTableReadBlockWorker w = new ContentTableReadBlockWorker();
             w.server = CurrentServer;
             w.field = field;
             w.rowid = shownTableContentRowId;
             w._frame = this;
             w.tablename = tablename;
             w.execute();
         }
    }
//    public void ShowTableContentRowId()
//    {
//        if ( this.ContentjTable.getRowCount() == 0 )
//        {
//            return;
//        }
//         DefaultTableModel m = (DefaultTableModel) ContentjTable.getModel();
//         int row = ContentjTable.getSelectedRow();
//         if ( row == -1)
//         {
//             return;
//         }
//         //on transcrit en RowId de la table
//         this.shownTableContentRowId = (String) m.getValueAt(row, 0);
//         String tablename = CurrentTable.name;
//         int colid = ContentjTable.getSelectedColumn();
//         String field = ContentjTable.getColumnName(colid);
//         TableField tf = CurrentTable.FindTableFieldFromName(field);
//
//         if (!tf.type.equals("STRING") && !tf.type.contains("ABSTRACT")) 
//         {
//             ContentTablejTextArea.setText((String) m.getValueAt(row, colid));
//
//         }
//         if (tf.type.contains("ABSTRACT")) 
//         {
//         ContentTablejTextArea.setText((String) m.getValueAt(row, colid));
//         }
//         if (tf.type.equals("STRING")) 
//         {
//             ContentTableReadBlockWorker w = new ContentTableReadBlockWorker();
//             w.server = CurrentServer;
//             w.field = field;
//             w.rowid = shownTableContentRowId;
//             w._frame = this;
//             w.tablename = tablename;
//             w.execute();
//         }
//    }
    private void ContentjTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContentjTableMouseReleased

        if ( CurrentTable == null )
        {
            return;
        }
        
        if ( evt.getButton() == java.awt.event.MouseEvent.BUTTON1)
        {
            if (ContentjTable.getSelectedRows() == null  ) 
            {
                return;
            }
            
            if ( ContentjTable.getSelectedRows().length == 1 )
            {
                DefaultTableModel m = (DefaultTableModel) ContentjTable.getModel();
                String rowid = (String) m.getValueAt(this.ContentjTable.getSelectedRow(), 0);
                this.shownTableContentRowId = rowid;
                this.ShowTableContentRowId();
            }
        }
        else if ( evt.getButton() == java.awt.event.MouseEvent.BUTTON3)
        {
            tableContentPopUpMenu._frame = this;
            int rowid = this.ContentjTable.rowAtPoint(evt.getPoint());
            int colid = this.ContentjTable.columnAtPoint(evt.getPoint());
            
            this.tableContentPopUpMenu.row = rowid;
            this.tableContentPopUpMenu.column = colid;
            
            String field = ContentjTable.getColumnName(colid);

            TableField tf = CurrentTable.FindTableFieldFromName(field);
            DefaultTableModel m = (DefaultTableModel) ContentjTable.getModel();
                        
            if (!tf.type.equals("STRING") && !tf.type.contains("ABSTRACT")) 
            {
                ContentTablejTextArea.setText((String) m.getValueAt(rowid, colid));

            }
            if (tf.type.contains("ABSTRACT")) 
            {
            ContentTablejTextArea.setText((String) m.getValueAt(rowid, colid));
            }
            //
            // on recupere le vrai rowid de la table serveur
            rowid = Integer.parseInt( (String) m.getValueAt(rowid, 0) );
            if (tf.type.equals("STRING")) 
            {
                ContentTableReadBlockWorker w = new ContentTableReadBlockWorker();
                w.server = CurrentServer;
                w.field = field;
                w.rowid = String.valueOf(rowid);
                w._frame = this;
                w.tablename = CurrentTable.name;
                w.execute();
            }
            
            this.tableContentPopUpMenu.mouseX = evt.getX();
            this.tableContentPopUpMenu.mouseY = evt.getY();
            this.tableContentPopUpMenu.show(evt.getComponent(), evt.getX()+10, evt.getY());
            
        }
    }//GEN-LAST:event_ContentjTableMouseReleased

    private void ContentjTableComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_ContentjTableComponentShown

    }//GEN-LAST:event_ContentjTableComponentShown

    private void ContentjTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContentjTableMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_ContentjTableMouseMoved

    private void jPanel29ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel29ComponentShown

    }//GEN-LAST:event_jPanel29ComponentShown

    private void TablesjInternalFrameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TablesjInternalFrameFocusLost


    }//GEN-LAST:event_TablesjInternalFrameFocusLost

    private void TableVisualjInternalFrameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TableVisualjInternalFrameFocusGained


    }//GEN-LAST:event_TableVisualjInternalFrameFocusGained

    private void TablesjInternalFrameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TablesjInternalFrameFocusGained

   /*     if ( this.CurrentServer == null )
        {
            return;
        }
        if (CurrentTable == null) {
            this.Updatelog("No Table selected. \n");
            return;
        }

      //  this.UpdateTableContent(false);
        */
    }//GEN-LAST:event_TablesjInternalFrameFocusGained

    private void TableVisualjInternalFrameInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_TableVisualjInternalFrameInternalFrameActivated

        if ( CurrentServer == null )
        {
            return;
        }
        if (CurrentTable == null) {
            this.Updatelog("No Table selected. \n");
            return;
        }
        
        if ( !this.CurrentServer.TableContentSize.isEmpty())
        {
            this.SizejTextField.setText(this.CurrentServer.TableContentSize);
        }
        if ( !this.CurrentServer.TableContentStart.isEmpty())
        {
            this.StartjTextField.setText(this.CurrentServer.TableContentStart);
        }
        if ( this.CurrentServer.TableContentSliderValue != -1)
        {
            this.TableLinesjSlider.setValue(this.CurrentServer.TableContentSliderValue);
        }
        this.UpdateTableContent(false);


    }//GEN-LAST:event_TableVisualjInternalFrameInternalFrameActivated

    private void TablesjInternalFrameInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_TablesjInternalFrameInternalFrameActivated

        this.UpdateTablesFrame();
    }//GEN-LAST:event_TablesjInternalFrameInternalFrameActivated

    private void TableLinesjSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TableLinesjSliderStateChanged

        this.StartjTextField.setText( String.valueOf(this.TableLinesjSlider.getValue()) );
        if ( this.CurrentServer != null && !this.loadingSettings )
        {
            this.CurrentServer.PushTableContentFrameSettings();
        }
    }//GEN-LAST:event_TableLinesjSliderStateChanged

    private void ContextsjInternalFrameInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_ContextsjInternalFrameInternalFrameActivated

        
       if (CurrentSession == null || CurrentServer == null) 
       {
            return;
       }
        // on recupere la pile de contexts
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w.session = CurrentSession;
        w.Action = "UpdatingStack";
        w._frame = this;
        w.contextIndex = this.CurrentSession.shownContextIndex;
        w.fetchAll = true;
        if ( w.contextIndex != -1)
        {
            w.fetchContent = true;
        }
        w.Action = "FetchAll";
        w.execute();
        

        CurrentSession.PopContextsFrameSettings();
        
    }//GEN-LAST:event_ContextsjInternalFrameInternalFrameActivated

    private void ContextPropertiesjTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContextPropertiesjTableMouseReleased

        if (CurrentContext == null) {
            this.Updatelog("No Context selected. Aborting edition of Context properties. \n");
            return;
        }
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) {
            int row = ContextPropertiesjTable.getSelectedRow();
            int col = ContextPropertiesjTable.getSelectedColumn();

            if (row != -1 && col != -1) {
                if (Context.prop_access[row].equals("r")) {
                    this.Updatelog("property CONTEXTS.'" + Context.properties[row] + "' is Read Only. Aborting edition. \n");
                    return;
                }
                boolean success = ContextPropertiesjTable.editCellAt(row, col);
                if (success) {
                    ContextPropertiesjTable.changeSelection(row, col, false, false);
                }
            }

        }
    }//GEN-LAST:event_ContextPropertiesjTableMouseReleased

    private void ContextPropertiesjTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContextPropertiesjTableMouseMoved

        if ( this.ContextPropertiesjTable.getRowCount() == 0)
        {
            return;
        }
        java.awt.Point p = evt.getPoint();
        int rowIndex = ContextPropertiesjTable.rowAtPoint(p);
        int colIndex = ContextPropertiesjTable.columnAtPoint(p);
        if (colIndex == -1 && rowIndex == -1) {
            return;
        }
        int realColumnIndex = ContextPropertiesjTable.convertColumnIndexToModel(colIndex);

        DefaultTableModel m = (DefaultTableModel) ContextPropertiesjTable.getModel();
        ContextPropertiesjTable.setToolTipText((String) m.getValueAt(rowIndex, realColumnIndex));
    }//GEN-LAST:event_ContextPropertiesjTableMouseMoved

    private void ContextPropertiesjTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ContextPropertiesjTableKeyReleased

        if (CurrentSession == null) {
            this.Updatelog("No Session selected. Aborting \n");
            return;
        }
        if (CurrentContext == null) {
            this.Updatelog("No Context selected. Aborting \n");
            return;
        }

        int rowe = this.ContextPropertiesjTable.getEditingRow();
        int cole = this.ContextPropertiesjTable.getEditingColumn();
        int row = this.ContextPropertiesjTable.getSelectedRow();
        int col = this.ContextPropertiesjTable.getSelectedColumn();
        if ((row == -1 && col == -1) || CurrentSession == null) {
            return;
        }
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            this.ContextPropertiesjTable.setEnabled(false);
            DefaultTableModel m = (DefaultTableModel) this.ContextPropertiesjTable.getModel();
            String value = (String) m.getValueAt(row, col);
            if (Context.prop_access[row].equals("r")) {
                this.Updatelog("property '" + Context.properties[row] + "' is Read Only. Aborting \n");
                return;
            }
            String property = (String) m.getValueAt(row, 0);
            if (property.equals("name")) {
                if (CurrentContext == null) {
                    return;
                }

                if (CurrentContext.name.startsWith("context#")) {
                    ProcessContextActionWorker.ReleaseAContextName(CurrentContext.name);
                }
                //CurrentContext.name = value;
            }
            ProcessContextActionWorker w = new ProcessContextActionWorker();
            w.contextIndex = CurrentSession.getIndexOfContext(CurrentContext);
            w._frame = this;
            w.Action = "none";
            if (property.equals("context_string")) {
                w.Action = "ShowOneContextProperties";
                w.fetchContent = true;
            }
            w.getproperties = true;
            w.setproperties = true;
            w.accessors = property + "=" + value;
            w.session = CurrentSession;
            w.execute();
            this.ContextPropertiesjTable.setEnabled(true);

        }
    }//GEN-LAST:event_ContextPropertiesjTableKeyReleased

    private void ContextsStackContentjTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContextsStackContentjTableMouseReleased

        if (CurrentSession == null )
        {
            return;
        }

        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1) 
        {
            if (this.ContextsStackContentjTable.getSelectedRows().length == 1)
            {
                this.ShownContextIndex = this.ContextsStackContentjTable.getSelectedRow();
                this.ShowContextContent(this.ShownContextIndex);
                this.CurrentSession.shownContextIndex = this.ContextsStackContentjTable.getSelectedRow();
            }
            
        }
        else if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3)
        {
            contextsStackContentPopUpMenu._frame = this;
            contextsStackContentPopUpMenu.mouseX = evt.getX();
            contextsStackContentPopUpMenu.mouseY = evt.getY();
            contextsStackContentPopUpMenu.show( evt.getComponent(), evt.getX(), evt.getY() );
        }
    }//GEN-LAST:event_ContextsStackContentjTableMouseReleased

    private void ContextContentjTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContextContentjTableMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_ContextContentjTableMouseReleased

    private void ContextContentjTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContextContentjTableMouseMoved

        if ( this.ContextContentjTable.getRowCount() == 0)
        {
            return;
        }
        java.awt.Point p = evt.getPoint();
        int rowIndex = ContextContentjTable.rowAtPoint(p);
        int colIndex = ContextContentjTable.columnAtPoint(p);
        if (colIndex == -1 && rowIndex == -1) {
            return;
        }
        int realColumnIndex = ContextContentjTable.convertColumnIndexToModel(colIndex);

        DefaultTableModel m = (DefaultTableModel) ContextContentjTable.getModel();
        ContextContentjTable.setToolTipText((String) m.getValueAt(rowIndex, realColumnIndex));
    }//GEN-LAST:event_ContextContentjTableMouseMoved

    private void ContextAmplifyBjTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ContextAmplifyBjTextFieldKeyReleased
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            int value = -1;
            try {
                value = Integer.parseInt(ContextAmplifyBjTextField.getText());
            } catch (Exception e) {
                ContextAmplifyBjTextField.setText("1");
            }
        }
    }//GEN-LAST:event_ContextAmplifyBjTextFieldKeyReleased

    private void stringToContextjButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stringToContextjButtonMouseReleased
        if (CurrentSession == null) {
            this.Updatelog("No Session selected. 'stringToContext' aborted. \n");
            return;
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w.componentToEnable = this.stringToContextjButton;

        w.Action = "stringToContext";
        w.session = CurrentSession;
        w.range = this.stringToContextjTextArea.getText();
        w.consolidation = String.valueOf(this.stringToContextLearnjCheckBox.isSelected());
        w._frame = this;
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.componentToEnable.setEnabled(false);
        w.execute();
    }//GEN-LAST:event_stringToContextjButtonMouseReleased

    private void ResultsAmplifyBjTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ResultsAmplifyBjTextFieldKeyReleased
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            int value = -1;
            try {
                value = Integer.parseInt(ResultsAmplifyBjTextField.getText());
            } catch (Exception e) {
                ResultsAmplifyBjTextField.setText("1");
            }
        }
    }//GEN-LAST:event_ResultsAmplifyBjTextFieldKeyReleased

    private void operand1ResultsDeleteByjTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operand1ResultsDeleteByjTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_operand1ResultsDeleteByjTextFieldActionPerformed

    private void SelectToTableFieldjComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectToTableFieldjComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SelectToTableFieldjComboBoxActionPerformed

    private void selectToTableDestinationTableNamejComboBoxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_selectToTableDestinationTableNamejComboBoxMouseReleased
        if (CurrentServer == null) {
            this.Updatelog("No server selected. Aborting \n");
            return;
        }

        String tablename = (String) this.selectToTableDestinationTableNamejComboBox.getSelectedItem();
        Table tbl = CurrentServer.FindTableFromName(tablename);
        if (tbl == null) {

            return;
        }
        this.selectToTableDestinationTableNamejComboBox.removeAllItems();
        for (TableField s : tbl.fields) {
            this.selectToTableDestinationTableNamejComboBox.addItem(s.name);
        }
    }//GEN-LAST:event_selectToTableDestinationTableNamejComboBoxMouseReleased

    private void selectToTableDestinationTableNamejComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_selectToTableDestinationTableNamejComboBoxItemStateChanged
        if (CurrentServer == null ) // || this.loadingSettings) 
        {
            return;
        }
        if ( evt.getStateChange() == ItemEvent.DESELECTED )
        {
            return;
        }
        
        String tablename = (String) evt.getItem();
        Table tbl = CurrentServer.FindTableFromName(tablename);
        if (tbl == null) 
        {
            return;
        }
        this.SelectToTableFieldjComboBox.removeAllItems();
        for (TableField s : tbl.fields) {
            this.SelectToTableFieldjComboBox.addItem(s.name);
        }
    }//GEN-LAST:event_selectToTableDestinationTableNamejComboBoxItemStateChanged

    private void operand1ResultsSelectByjTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operand1ResultsSelectByjTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_operand1ResultsSelectByjTextFieldActionPerformed

    private void SelectFromTableFieldjComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectFromTableFieldjComboBoxActionPerformed

    }//GEN-LAST:event_SelectFromTableFieldjComboBoxActionPerformed

    private void operand1SelectFromTablejTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operand1SelectFromTablejTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_operand1SelectFromTablejTextField1ActionPerformed

    private void ResultsStackContentjTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultsStackContentjTableMouseReleased

        if (CurrentSession == null) {
            this.Updatelog("No session selected. Aborting \n");
            return;
        }
        int rows = ResultsStackContentjTable.getSelectedRows().length;
        if (rows == 0)
        {
            return;
        }

        if ( evt.getButton() == MouseEvent.BUTTON1)
        { 
            try 
            {
                if ( rows == 1)
                {
                    CurrentResultSet = CurrentSession.RSStack.stack.get( ResultsStackContentjTable.getSelectedRow() );
                    CurrentSession.shownRSContentIndex = ResultsStackContentjTable.getSelectedRow();
                }
            } catch (Exception e) {
                this.Updatelog("selected ResultSet not existing. Aborting \n");
                CurrentResultSet = null;
                return;
            }
            if ( rows == 1)
            {
                this.shownRSIndex = ResultsStackContentjTable.getSelectedRow();
                this.ShowResulSetStackContent(ResultsStackContentjTable.getSelectedRow() );
            }
        }
        
        if ( evt.getButton() == MouseEvent.BUTTON3)
        {
            this.resultsStackContentPopUpMenu._frame = this;
            this.resultsStackContentPopUpMenu.mouseX = evt.getX();
            this.resultsStackContentPopUpMenu.mouseY = evt.getY();
            this.resultsStackContentPopUpMenu.show( evt.getComponent(), evt.getX(), evt.getY() );
        }
        
        
    }//GEN-LAST:event_ResultsStackContentjTableMouseReleased

    private void ResultsjInternalFrameInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_ResultsjInternalFrameInternalFrameActivated


        ShowResulSetStack();
    }//GEN-LAST:event_ResultsjInternalFrameInternalFrameActivated

    public void ShowResulSetStack()
    {
        if ( CurrentSession == null )
        {
            return;
        }

        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.execute();
        this.UpdateTablesForResultsInternalFrame();
        this.CurrentSession.PopResultsCommandsParameters();
    }
    private void TablesjInternalFrameInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_TablesjInternalFrameInternalFrameDeactivated

        if ( CurrentServer == null )
        {
            return;
        }
        if ( CurrentTable == null )
        {
            return;
        
        }

        CurrentServer.PushTablesFrameSettings();

                
        CurrentServer.CurrentTable = this.CurrentTable;
        
        this.UpdateTableContent(false);

    }//GEN-LAST:event_TablesjInternalFrameInternalFrameDeactivated

    private void TableVisualjInternalFrameInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_TableVisualjInternalFrameInternalFrameDeactivated

        if ( this.CurrentServer == null )
        {
            return;
        }



        this.CurrentServer.PushTableContentFrameSettings();
        
    }//GEN-LAST:event_TableVisualjInternalFrameInternalFrameDeactivated

    private void ContextsjInternalFrameInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_ContextsjInternalFrameInternalFrameDeactivated

        if ( CurrentSession == null )
        {
            return;
        }
        CurrentSession.PushContextsFrameSettings();

    }//GEN-LAST:event_ContextsjInternalFrameInternalFrameDeactivated

    public void SetShownContextIndex(int index)
    {
        this.ShownContextIndex = index;
        
    }
        public void SetShownRSIndex(int index)
    {
        this.shownRSIndex = index;
        
    }
    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton29ActionPerformed

    private void Operand2SelectFromTablejTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Operand2SelectFromTablejTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Operand2SelectFromTablejTextField1ActionPerformed

    private void ResultsjInternalFrameInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_ResultsjInternalFrameInternalFrameDeactivated
      
        if ( CurrentSession == null )
        {
            return;
        }
        // on sauve les selected rows du results stack
        
        CurrentSession.PushSelectedRSStackRows();
        CurrentSession.PushResultsCommandsParameters();
    }//GEN-LAST:event_ResultsjInternalFrameInternalFrameDeactivated

    private void ResultSetContentjInternalFrameInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_ResultSetContentjInternalFrameInternalFrameDeactivated
     
        if ( CurrentSession == null )
        {
            return;
        }
        CurrentSession.PushRSContentColumnsWidths();
    }//GEN-LAST:event_ResultSetContentjInternalFrameInternalFrameDeactivated

    private void ContextPropertiesjInternalFrameInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_ContextPropertiesjInternalFrameInternalFrameActivated

        if ( CurrentSession == null )
        {
            return;
        }
        
        CurrentSession.PopSessionPropertiesTableColumnsWidths();
        
    }//GEN-LAST:event_ContextPropertiesjInternalFrameInternalFrameActivated

    private void ContextPropertiesjInternalFrameInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_ContextPropertiesjInternalFrameInternalFrameDeactivated

        if ( CurrentSession == null )
        {
            return;
        }
        
        CurrentSession.PushSessionPropertiesTableColumnsWidths();
    }//GEN-LAST:event_ContextPropertiesjInternalFrameInternalFrameDeactivated

    private void ResultSetContentjInternalFrameInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_ResultSetContentjInternalFrameInternalFrameActivated

        if ( CurrentSession == null )
        {
            return;
        }

        this.ShowResultSetContent();
    }//GEN-LAST:event_ResultSetContentjInternalFrameInternalFrameActivated

    public void ShowResultSetContent()
    {
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w._frame = this;
        w.session = this.CurrentSession;
        w.Action = "none";
        w.fetchContent = true;
        w.ResultSetIndex = this.CurrentSession.shownRSContentIndex;
        w.execute();
    }
    
    private void ServerCurrentSessionjInternalFrameInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_ServerCurrentSessionjInternalFrameInternalFrameDeactivated

        if (CurrentSession == null )
        {
            return;
        }
        
        CurrentSession.PushSessionPropertiesTableColumnsWidths();
        
    }//GEN-LAST:event_ServerCurrentSessionjInternalFrameInternalFrameDeactivated

    private void ServerCurrentSessionjInternalFrameInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_ServerCurrentSessionjInternalFrameInternalFrameActivated

        if (CurrentSession == null )
        {
            return;
        }
        
        CurrentSession.PopSessionPropertiesTableColumnsWidths();   
        
    }//GEN-LAST:event_ServerCurrentSessionjInternalFrameInternalFrameActivated

    private void ResultSetContentjInternalFrameMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultSetContentjInternalFrameMouseReleased


    }//GEN-LAST:event_ResultSetContentjInternalFrameMouseReleased

    private void CurrentServerjPanelComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_CurrentServerjPanelComponentHidden

        
    }//GEN-LAST:event_CurrentServerjPanelComponentHidden

    private void ResultSetContentjTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultSetContentjTableMouseReleased

        if ( CurrentTable == null )
        {
            this.Updatelog("No table selected. Aborting \n");
            return;
            
        }
        if ( evt.getButton() == java.awt.event.MouseEvent.BUTTON3)
        {
            resultSetContentPopUpMenu.mouseX = evt.getX();
            resultSetContentPopUpMenu.mouseY = evt.getY();
            resultSetContentPopUpMenu._frame = this;
            resultSetContentPopUpMenu.show(evt.getComponent(),evt.getX()+10, evt.getY());
        }
        
        
    }//GEN-LAST:event_ResultSetContentjTableMouseReleased

    private void ResultSetPropertiesjInternalFrameInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_ResultSetPropertiesjInternalFrameInternalFrameActivated
      
        if ( this.CurrentSession == null )
        {
            return;
        }
        this.CurrentSession.PushResultSetPropertiesTableColumnsWidths();
    }//GEN-LAST:event_ResultSetPropertiesjInternalFrameInternalFrameActivated

    private void ResultSetPropertiesjInternalFrameInternalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_ResultSetPropertiesjInternalFrameInternalFrameDeactivated
      
        if ( this.CurrentSession == null)
        {
            return;
        }
        this.CurrentSession.PushResultSetPropertiesTableColumnsWidths();
    }//GEN-LAST:event_ResultSetPropertiesjInternalFrameInternalFrameDeactivated

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        try 
        {
            if ( this.CurrentServer != null )
            {
                this.CurrentServer.PushUISettings();
            }
            if ( this.CurrentSession != null )
            {
                this.CurrentSession.PushUISettings();
            }
            Settings.WriteSettings(this);
            System.exit(0);
        } 
        catch (TransformerException ex) 
        {
            Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_formWindowClosing

    public void updateScriptLog(String script)
    {
        this.logjTextArea.append(script);
    }
    
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

        Settings.LoadSettings(this);
    }//GEN-LAST:event_formWindowOpened

    private void jCheckBox1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox1MouseReleased

        if (this.jCheckBox1.isSelected())
        {
            this.timerToUpdateKnowledgeGraph.start();
        }
        else
        {
            this.timerToUpdateKnowledgeGraph.stop();
        }
        
    }//GEN-LAST:event_jCheckBox1MouseReleased

    private void TableContentjScrollPaneMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TableContentjScrollPaneMouseReleased

        Rectangle rect  = this.ContentjTable.getVisibleRect();
     this.firstTableContentVisibleRow = this.ContentjTable.rowAtPoint(new Point(0,rect.y));
     this.lastTableContentVisibleRow = this.ContentjTable.rowAtPoint(new Point(0, rect.y + rect.height - 1));
     
     if ( CurrentServer != null )
     {
         this.CurrentServer.firstTableContentVisibleRow = this.firstTableContentVisibleRow;
         this.CurrentServer.lastTableContentVisibleRow = this.lastTableContentVisibleRow;
     }
    }//GEN-LAST:event_TableContentjScrollPaneMouseReleased

    private void ClearSessionjButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ClearSessionjButtonMouseReleased

        
        this.ClearSession();
        
    }//GEN-LAST:event_ClearSessionjButtonMouseReleased

    public void Updatelog(String msg) {
        if (msg == null || msg.isEmpty()) {
            return;
        }
        String c = this.LogjTextArea.getText();
        c = msg + c;
        this.LogjTextArea.setText(c);
        this.LogjTextArea.setCaretPosition(0);

    }

    public void removeASession() {
        // on l'ajoute au servers tree
        TreePath theSelectedPath = ServersTree.getSelectionPath();

        if (theSelectedPath != null && theSelectedPath.getPathCount() == 4) {
            DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) theSelectedPath.getLastPathComponent();
            String theNodeName = (String) theNode.getUserObject();
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) theNode.getParent();
            String parentName = "";
            if (!((parentName = (String) parent.getUserObject()) instanceof String)) {
                this.Updatelog("ERROR occured parent node is not suitable. \n");
                return;
            }

            if (!parentName.equals("Session")) {
                this.Updatelog("ERROR occured. Selected path is Not Session. \n");
                return;
            }
            //on cherche le child node "Sessions"
            DefaultMutableTreeNode child = null;
            int i = 0;
            String childName = "";
            for (; i < parent.getChildCount(); i++) {
                childName = (String) ((DefaultMutableTreeNode) parent.getChildAt(i)).getUserObject();
                if (childName.equals(theNodeName)) {
                    this._listModel.removeNodeFromParent(parent);
                    break;
                }
            }
            if (i == theNode.getChildCount()) {
                this.Updatelog("ERROR Occured session node NOT found \n");
                return;
            }
            ServersTree.scrollPathToVisible(new TreePath(parent.getPath()));
            _listModel.nodeChanged(parent);

        }

    }

    public void addASession(int x, int y) {
        if (CurrentServer == null) {
            this.Updatelog("No server selected. Aborting. \n");
            return;
        }
        this.timerToUpdateServerStats.stop();
        SessionConnectWorker w = new SessionConnectWorker();
        w.id = "-1";
        Connector connector = new Connector();
        w.connector = connector;
        w.mouseX = x;
        w.mouseY = y;
        connector.setIp(CurrentServer.ip);
        connector.setPort(CurrentServer.port);
        w._frame = this;
        // w.timer = this.timerToUpdateServerStats;
        w.execute();
    }

    public void AddASession(String[] properties_instances, String[] properties_values, String[] spectrum_values, Connector connector, int x ,int y) {
 
        if (CurrentServer == null )
        {
            this.Updatelog("No server selected. Aborting \n");
        }
        if (this.CurrentServer.Session_Id.equals(properties_values[4])) 
        {
            return;
        }
        if (CurrentServer.AddASessionFromGetInstances(properties_instances, connector)) 
        {
            // on l'ajoute au servers tree

            TreePath theSelectedPath = ServersTree.getPathForLocation(x, y);
            if ( (theSelectedPath != null && theSelectedPath.getPathCount() == 3)   ) 
            {

                //on cherche le child node "Sessions"
                DefaultMutableTreeNode sessionsNode = (DefaultMutableTreeNode) theSelectedPath.getLastPathComponent();
                _listModel.insertNodeInto(new DefaultMutableTreeNode(properties_values[4],false), sessionsNode, sessionsNode.getChildCount());
                _listModel.nodeChanged(sessionsNode);
            }
            else if (x==-666 && y==-666)
            {
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) ServersTree.getModel().getRoot();
                for (int i = 0; i < rootNode.getChildCount();i++)
                {
                    DefaultMutableTreeNode serverNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
                    Server server = (Server) serverNode.getUserObject();
                    if (server != CurrentServer)
                    {
                        continue;
                    }
                    DefaultMutableTreeNode sessionsNode = (DefaultMutableTreeNode) serverNode.getChildAt(2);
                    _listModel.insertNodeInto(new DefaultMutableTreeNode(properties_values[4]), sessionsNode, sessionsNode.getChildCount());
                    _listModel.nodeChanged(sessionsNode);
            }
        }
        CurrentServer.AddASessionFromValues(properties_values, spectrum_values, connector);
    }
       
        this.timerToUpdateServerStats.start();
    }
    public void KillATable(TreePath thePath) 
    {
        if (CurrentServer == null) 
        {
            this.Updatelog("No Server selected. Aborting \n");
            return;
        }
        if (thePath == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) thePath.getLastPathComponent();

        if (!(node.getUserObject() instanceof String)) {
            return;
        }
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();

        if (!(parentNode.getUserObject() instanceof String)) {
            return;
        }
        String s = (String) parentNode.getUserObject();
        if (!s.equals("Tables")) {
            return;
        }
        TableWorker w = new TableWorker();
        w._frame = this;
        w.Action = "Kill";
        w.tablename = (String) node.getUserObject();
        w.server = CurrentServer;
        w.execute();

    }

    public void KillABTree(TreePath thePath) {
        if (CurrentServer == null) {
            this.Updatelog("No Server selected. Aborting \n");
            return;
        }
        if (thePath == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) thePath.getLastPathComponent();

        if (!(node.getUserObject() instanceof String)) {
            return;
        }
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();

        if (!(parentNode.getUserObject() instanceof String)) {
            return;
        }
        String s = (String) parentNode.getUserObject();
        if (!s.equals("B-Trees")) {
            return;
        }
        s = (String) node.getUserObject();

        TableWorker w = new TableWorker();
        w._frame = this;
        w.Action = "BTreeKill";
        w.tablename = s.substring(s.indexOf("_") + 1, s.lastIndexOf("_") - 1);
        w.field = s.substring(s.lastIndexOf("_") + 1);
        w.server = CurrentServer;
        w.execute();
    }

    public void KillAKTree(TreePath thePath) {
        if (CurrentServer == null) {
            this.Updatelog("No Server selected. Aborting \n");
            return;
        }

        if (thePath == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) thePath.getLastPathComponent();
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();

        if (!(parentNode.getUserObject() instanceof String)) {
            return;
        }

        if (!(node.getUserObject() instanceof String)) {
            return;
        }

        String s = (String) parentNode.getUserObject();
        if (!s.equals("B-Trees")) {
            return;
        }
        s = (String) node.getUserObject();

        TableWorker w = new TableWorker();
        w._frame = this;
        w.Action = "KTreeKill";
        w.tablename = s.substring(s.indexOf("_") + 1, s.lastIndexOf("_") - 1);
        w.field = s.substring(s.lastIndexOf("_") + 1);
        w.server = CurrentServer;
        w.execute();
    }

    public void CreateABTree(TreePath thePath, int x, int y) {
        if (CurrentServer == null) {
            this.Updatelog("No server selected. Aborting. \n");
            return;
        }

        // on recupere le nom de la table du ServersTree
        if (thePath == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) thePath.getLastPathComponent();
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();

        if (!(parentNode.getUserObject() instanceof String)) {
            return;
        }

        if (!(node.getUserObject() instanceof String)) {
            return;
        }

        String s = (String) parentNode.getUserObject();
        if (!s.equals("Tables")) {
            return;
        }
        // le nom de la table
        s = (String) node.getUserObject();
        Table t = CurrentServer.FindTableFromName(s);
        if (t == null) {
            this.Updatelog("Table not found. Inconsitance detected \n");
        }
        String[] fields = new String[t.fields.size()];
        int i = 0;
        for (TableField tf : t.fields) {
            fields[i++] = tf.name;
        }
        NewBTreeJDialog d = new NewBTreeJDialog();
        d.SetTableName(s);
        d.SetFields(fields);
        d.setModal(true);
        d.setLocation(x + 5, y + 5);
        d.setVisible(true);
        if (d.state.equals("Ok")) {
            TableWorker w = new TableWorker();
            w.Action = "BTreeCreate";
            w.field = d.fields;
            w._frame = this;
            w.boolUnique = d.boolUnique;
            w.tablename = s;
            w.server = CurrentServer;
            w.execute();
        }

    }

    public void CreateAKTree(TreePath thePath, int x, int y) {
        if (CurrentServer == null) {
            this.Updatelog("No server selected. Aborting. \n");
            return;
        }

        // on recupere le nom de la table du ServersTree
        if (thePath == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) thePath.getLastPathComponent();
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();

        if (!(node.getUserObject() instanceof String)) {
            return;
        }

        String s = (String) parentNode.getUserObject();
        if (!s.equals("Tables")) {
            return;
        }
        // le nom de la table
        s = (String) node.getUserObject();
        Table t = CurrentServer.FindTableFromName(s);
        if (t == null) {
            this.Updatelog("Table not found. Inconsitance detected \n");
        }
        String[] fields = new String[t.fields.size()];
        int i = 0;
        for (TableField tf : t.fields) {
            fields[i++] = tf.name;
        }
        NewBTreeJDialog d = new NewBTreeJDialog(false, "Create a K-Tree");
        d.SetTableName(s);
        d.SetFields(fields);
        d.setModal(true);
        d.setLocation(x + 5, y + 5);
        d.setVisible(true);
        if (d.state.equals("Ok")) {
            TableWorker w = new TableWorker();
            w.Action = "KTreeCreate";
            w.field = d.fields;
            w._frame = this;
            w.tablename = s;
            w.server = CurrentServer;
            w.execute();
        }

    }

    public void RebuildBTree(TreePath thePath, int x, int y) {
        if (thePath == null) {
            this.Updatelog("No B-Tree Aborting \n");
            return;
        }
        // on recupere le serveur concerne
        DefaultMutableTreeNode theserverNode = (DefaultMutableTreeNode) thePath.getPathComponent(1);
        Server server = (Server) theserverNode.getUserObject();
        if (!server.connector.getIsConnected()) {
            server.connector.connect();
        }

        DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) thePath.getLastPathComponent();
        String name = (String) theNode.getUserObject();
        TableWorker w = new TableWorker();
        w.Action = "BTreeRebuild";
        w._frame = this;
        w.server = server;
        w.tablename = name.substring(name.indexOf("_") + 1, name.lastIndexOf("_") - 1);
        w.field = name.substring(name.lastIndexOf("_") + 1);
        w.execute();
    }

    public void RebuildKTree(TreePath thePath, int x, int y) {
        if (thePath == null) {
            this.Updatelog("No K-Tree Aborting \n");
            return;
        }
        // on recupere le serveur concerne
        DefaultMutableTreeNode theserverNode = (DefaultMutableTreeNode) thePath.getPathComponent(1);
        Server server = (Server) theserverNode.getUserObject();
        if (!server.connector.getIsConnected()) {
            server.connector.connect();
        }

        DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) thePath.getLastPathComponent();
        String name = (String) theNode.getUserObject();
        TableWorker w = new TableWorker();
        w.Action = "KTreeRebuild";
        w._frame = this;
        w.server = server;
        w.tablename = name.substring(name.indexOf("_") + 1, name.lastIndexOf("_") - 1);
        w.field = name.substring(name.lastIndexOf("_") + 1);
        w.execute();
    }

    public void CreateATable(int x, int y) {
        if (CurrentServer == null) {
            this.Updatelog("No server has been selected. Aborting. \n");
            return;
        }

        CreateTableJDialog d = new CreateTableJDialog();
        d.setModal(true);
        d.setLocation(x + 10, y + 10);
        d.setVisible(true);
        if (d.state.equals("Ok")) {
            TableWorker w = new TableWorker();
            w.Action = "Create";
            w.field = d.fields;
            w._frame = this;
            w.tablename = d.name;
            w.server = CurrentServer;
            w.execute();
        }

    }
    // adding a server

    public void AddAnewServer(Server server) {

        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) this.ServersTree.getModel().getRoot();

        DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(server, true);

        this._listModel.insertNodeInto(serverNode, rootNode, rootNode.getChildCount());

            // on rajoute ses enfants
        DefaultMutableTreeNode SessionsNode = new DefaultMutableTreeNode("Sessions", true);
        // on rajoute son enfant tables
        DefaultMutableTreeNode tablesNode = new DefaultMutableTreeNode("Tables", true);

        this._listModel.insertNodeInto(new DefaultMutableTreeNode("mARC", false), serverNode, serverNode.getChildCount());
        this._listModel.insertNodeInto(tablesNode, serverNode, serverNode.getChildCount());
        this._listModel.insertNodeInto(SessionsNode, serverNode, serverNode.getChildCount());

        this._listModel.insertNodeInto(new DefaultMutableTreeNode("K-Trees", true), serverNode, serverNode.getChildCount());
        this._listModel.insertNodeInto(new DefaultMutableTreeNode("B-Trees", true), serverNode, serverNode.getChildCount());

        _listModel.nodeChanged(rootNode);

        synchronized(servers)
        {
            if (this.servers.isEmpty()) 
            {
            InitializeDesktop();
            }
            this.servers.add(server);
        }
    }

    public void AddAnewServer(int x, int y) {
        NewServerJDialog serverDialog = new NewServerJDialog();
        serverDialog.setModal(true);

        serverDialog.setVisible(true);
        if (serverDialog.state == "Ok") {
            // on check que le serveur n'est pas deja la
            
            synchronized(servers)
            {
                for (int i = 0; i < servers.size(); i++) 
                {
                    Server s = servers.get(i);
                    if (s.ip.equals(serverDialog.ip) && s.port.equals(serverDialog.port)) {
                        this.Updatelog("Server already existing. Aborting \n");
                        return;
                    }
                }
                Server server = new Server(serverDialog.ip, serverDialog.port);
                server._frame = this;

                if (this.servers.isEmpty()) {
                    InitializeDesktop();
                }
                this.servers.add(server);

                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) this.ServersTree.getModel().getRoot();

                DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(server, true);
            
                this._listModel.insertNodeInto(serverNode, rootNode, rootNode.getChildCount());

                // on rajoute ses enfants
                this._listModel.insertNodeInto(new DefaultMutableTreeNode("mARC", false), serverNode, serverNode.getChildCount());

                DefaultMutableTreeNode SessionsNode = new DefaultMutableTreeNode("Sessions", true);
                // on rajoute son enfant tables
                DefaultMutableTreeNode tablesNode = new DefaultMutableTreeNode("Tables", true);

                this._listModel.insertNodeInto(tablesNode, serverNode, serverNode.getChildCount());
                this._listModel.insertNodeInto(SessionsNode, serverNode, serverNode.getChildCount());

                this._listModel.insertNodeInto(new DefaultMutableTreeNode("K-Trees", true), serverNode, serverNode.getChildCount());
                this._listModel.insertNodeInto(new DefaultMutableTreeNode("B-Trees", true), serverNode, serverNode.getChildCount());

                _listModel.nodeChanged(rootNode);
            }
        }
    }

    public void InitializeDesktop() {

        this.DesktopsjPanel.add(this.jPanelMainWindow);
        this.jPanelMainWindow.setMaximumSize(new Dimension(2536, 1600));
        this.jPanelMainWindow.setSize(new Dimension(2536, 1600));
        this.jPanelMainWindow.setLocation(0, 0);
        this.jPanelMainWindow.setVisible(true);
        MyDesktopManager m = new MyDesktopManager();
        this.jDesktopPane.setDesktopManager(m);
        m._owner = this.jDesktopPane;

        this.jDesktopPane.add(this.TablesjInternalFrame);
        this.TablesjInternalFrame.setSize(300, 300);
        this.TablesjInternalFrame.setLocation(20, 20);
        this.TablesjInternalFrame.show();

        this.jDesktopPane.add(this.ServerCurrentSessionjInternalFrame);
        this.ServerCurrentSessionjInternalFrame.setSize(300, 300);
        this.ServerCurrentSessionjInternalFrame.setLocation(40, 40);
        this.ServerCurrentSessionjInternalFrame.show();

        this.jDesktopPane.add(this.TableVisualjInternalFrame);
        this.TableVisualjInternalFrame.setSize(300, 300);
        this.TableVisualjInternalFrame.setLocation(50, 50);
        this.TableVisualjInternalFrame.show();
        
        this.jDesktopPane.add(this.ResultsjInternalFrame);
        this.ResultsjInternalFrame.setSize(300, 300);
        this.ResultsjInternalFrame.setLocation(10, 10);
        this.ResultsjInternalFrame.show();
        
        
        this.jDesktopPane.add(this.ContextsjInternalFrame);
        this.ContextsjInternalFrame.setSize(300, 300);
        this.ContextsjInternalFrame.setLocation(60, 60);
        this.ContextsjInternalFrame.show();
        
                
        this.jDesktopPane.add(this.ContextPropertiesjInternalFrame);
        this.ContextPropertiesjInternalFrame.setSize(300, 300);
        this.ContextPropertiesjInternalFrame.setLocation(80, 80);
        this.ContextPropertiesjInternalFrame.show();
 

        this.ResultSetContentjInternalFrame.setSize(300, 300);
        this.ResultSetContentjInternalFrame.setLocation(90, 90);
        this.jDesktopPane.add(this.ResultSetContentjInternalFrame);
        this.ResultSetContentjInternalFrame.show();
        
        this.LogjInternalFrame.setSize(300, 300);
        this.LogjInternalFrame.setLocation(100, 100);
        this.jDesktopPane.add(this.LogjInternalFrame);
        this.LogjInternalFrame.show();
        
        this.ResultSetPropertiesjInternalFrame.setSize(300, 300);
        this.ResultSetPropertiesjInternalFrame.setLocation(120, 120);
        this.jDesktopPane.add(this.ResultSetPropertiesjInternalFrame);
        this.ResultSetPropertiesjInternalFrame.show();
    }

    public void RemoveAServer(int x, int y) {
        if (ServersTree.getSelectionPath() == null) {
            return;
        }
        TreePath theSelectedPath = ServersTree.getPathForLocation(x, y);
        if (theSelectedPath != null && theSelectedPath.getPathCount() == 2) 
        {
            synchronized(servers)
            {
                DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) theSelectedPath.getLastPathComponent();
                Server server = (Server) theNode.getUserObject();
                if (server.connector.getIsConnected()) {
                    server.connector.disConnect();
                    this.Updatelog("successfully disconnected server '" + server.name + "' \n");
                }
                DefaultMutableTreeNode serversNode = (DefaultMutableTreeNode) theNode.getParent();
                _listModel.removeNodeFromParent(theNode);
                _listModel.nodeChanged(serversNode);
                if ( CurrentServer == server )
                {
                    this.timerToUpdateServerStats.stop();
                    CurrentServer = null;
                    CurrentTable = null;
                    CurrentSession = null;
                }
                this.servers.remove(server);
            }
        }


    }

    public void DisconnectAServer(int x, int y) {
        if (ServersTree.getSelectionPath() == null) {
            return;
        }
        TreePath theSelectedPath = ServersTree.getPathForLocation(x, y);
        if (theSelectedPath.getPathCount() == 2) {
            DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) theSelectedPath.getLastPathComponent();
            Server server = (Server) theNode.getUserObject();
            this.timerToUpdateServerStats.stop();
            server.connector.disConnect();
            if (server.connector.getIsConnected()) {
                this.Updatelog("server '" + server.name + "' successfully disconnected. \n ");
            } else {
                this.Updatelog("ERROR : could not disconnect server. \n");
            }
        }

    }

    public void ToggleEnableContainersOnPanel(java.awt.Container panel, boolean enabled) {
        java.awt.Component[] children = panel.getComponents();
        java.awt.Component cur;
        for (int i = 0; i < children.length; i++) {
            cur = children[i];
            cur.setEnabled(enabled);
            if (cur instanceof java.awt.Container) {
                ToggleEnableContainersOnPanel((java.awt.Container) cur, enabled);
            }
        }
    }

    public void updateServerSessionsjTable(List<Session> theSessions) {
        DefaultTableModel m = (DefaultTableModel) ServerSessionsjTable.getModel();
        int selectedRow = this.ServerSessionsjTable.getSelectedRow();
        m.setRowCount(0);
        int i = 0;
        for (Session se : theSessions) {
            if (se.owned) 
            {
                m.addRow(new String[]{se.id, se.name, se.persistant, se.owner_ip, se.owner_port, se.priority, se.exec_timeout, se.session_timeout});
                if ( i == selectedRow )
                {
                    this.ServerSessionsjTable.changeSelection(i, 0, true, false);
                }
            }
        }
    }

    public void updateKnowLedgeGraph(java.util.Map<String, ArrayList<String>> theNodesMap, ArrayList<String> roots) {
        this.theGraph.AddNodes(theNodesMap, roots);

    }

    public void CheckSessionConnection(String[] values, String[] spectrum_values, String id) {

        if (CurrentSession == null) // IMPOSSIBLE
        {
            Updatelog("No Current Session set. \n");
            return;
        }

        Session se = this.CurrentServer.FindSessionFromId(id);
        if (se != null) 
        {
            se.spectrum_values = spectrum_values;
            se.values = values;
            se.name = values[0];
            Updatelog("Connection to session '" + id + "' successfull. \n");

            DefaultTableModel m = (DefaultTableModel) this.SessionPropertiesjTable.getModel();
            m.setRowCount(0);
            int i = 0;
            for (String s : se.values) {
                m.addRow(new String[]{Session.properties[i], s, Session.types[i], Session.prop_access[i++]});
            }
            int index = this.CurrentServer.getindexOfSession(se);
            if (index != -1) {
                this.ServerSessionsjTable.setValueAt(se.name, index, 1);
            }
            this.updateStatus();
        } else {
            Updatelog("Could not connect to the session '" + id + "' \n");
        }

    }

    public void LogSessionNotConnected(String id) {
        this.Updatelog("Session '" + id + "' is Not connected. ERROR \n");
        this.CurrentServer.RemoveASessionFromId(id);
    }

    public void SettimerToUpdateServerStats(boolean Start) {
        if (Start) {
            this.timerToUpdateServerStats.start();
        } else {
            this.timerToUpdateServerStats.stop();
        }
    }

    public void updateCurrentServerTablesStats(Server server) {
        //une fois une table creee on maj les infos des tables du server en parametre
        server.TablesToTree(ServersTree);
        server.BIndexesToTree(ServersTree);
        server.KIndexesToTree(ServersTree);
    }

    public void updateSessionProperties()
    {
        if ( CurrentSession == null )
        {
        this.Updatelog("No Current Session defined. ERROR \n");
        return;
        }     
        
        if ( CurrentSession.values == null )
        {
            return;
        }
        DefaultTableModel m = (DefaultTableModel) this.SessionPropertiesjTable.getModel();
        m.setRowCount(0);
        int i = 0;
        for (String value : CurrentSession.values) 
        {
            m.addRow(new String[]{Session.properties[i], value, Session.types[i], Session.prop_access[i++]});
        }
    }
    public void updateSessionSpectrum()
    {
        if ( CurrentSession == null )
        {
            return;
        }
        
        if ( CurrentSession.spectrum_values == null )
        {
            return;
        } 
        DefaultTableModel m = (DefaultTableModel) this.SessionSpectrumjTable.getModel();
        m.setRowCount(0);
        int i = 0;
        for (String s : CurrentSession.spectrum_values) 
        {
            m.addRow(new String[]{Session.spectrum_names[i], s, Session.spectrum_types[i++]});
        }
        CurrentSession.spectrumChanged = true;
    }
    public void updateSessionSpectrum(String[] values) 
    {
        if (CurrentSession != null) {
            CurrentSession.spectrum_values = values;
        }

    this.updateSessionSpectrum();
    
    }

    public void updateResultsStack() 
    {
        if ( CurrentSession == null )
        {
            return;
        }
        DefaultTableModel m = (DefaultTableModel) this.ResultsStackContentjTable.getModel();
        m.setRowCount(0);
        if (this.CurrentSession.RSStack.stack.isEmpty()) {
            this.Updatelog("No ResultSets in current session ResultSets stack. \n");
        }
        this.CurrentResultSet = null;
        int i = 0;
        for (ResultSet rs : this.CurrentSession.RSStack.stack) 
        {
            if (rs.name.isEmpty() || rs.name == null) 
            {
                rs.name = "#"+i;
                if ( rs.values != null )
                {
                    rs.name += rs.values[2]+" Elements";
                }// ProcessResultsActionWorker.GetARSName();
            }
            m.addRow(new String[]{rs.name});
            i++;
        }
        
        CurrentSession.PopResultsCommandsParameters();
    }

    public void UpdateResultSetContent(ResultSet rs, int RSindex) 
    {
        if(  CurrentSession == null )
        {
            return;
        }
        this.CurrentResultSet = rs;
        this.CurrentRSIndex = RSindex;
        DefaultTableModel m = (DefaultTableModel) this.ResultSetContentjTable.getModel();
        m.setRowCount(0);
        m.setColumnCount(0);
        int i = 0;
        for (String s : rs.format) 
        {
            m.addColumn(s, rs.cols[i++]);
        }

        this.ResultsStackContentjTable.addRowSelectionInterval(RSindex, RSindex);
        CurrentSession.PopRSContentColumnsWidths();
        CurrentSession.PopResultsCommandsParameters();
        this.ShowResultSetProperties(rs);
        this.CurrentSession.PopResultSetPropertiesTableColumnsWidths();
        
    }

    public void ShowResultSetProperties(ResultSet rs)
    {
        DefaultTableModel m = (DefaultTableModel) this.ResultsPropertiesjTable.getModel();
        m.setRowCount(0);
        int i = 0;
        for (String s : rs.values) 
        {
            m.addRow(new String[]{ResultSet.properties[i], s, ResultSet.types[i], ResultSet.access[i++]});
        }
        
    }
            
    public void UpdateSelectedContext(int contextIndex, String[] values, String[] shapes, String[] activities, String[] gen, String[] gen_class, String[] id, String name) 
    {
        if (CurrentSession == null) 
        {
            this.Updatelog("ERROR. Current Session not found. \n");
            return;
        }
        Context ctx = null;
        try 
        {
            ctx = (Context) CurrentSession.contextsStack.stack.get(contextIndex);
        } catch (Exception e) {
            this.Updatelog("Context selected not found. \n");
            return;
        }
        if (ctx == null) {
            this.Updatelog("Context selected not found. \n");
            return;
        }
        DefaultTableModel m = null;
        int i = 0;
        if (ctx.name.isEmpty() && name != null ) 
        {
            ctx.name = "#i";
            if ( ctx.values != null)
            {
                ctx.name += ": "+values[1];
            }
        }
        try 
        {
            this.ContextsStackContentjTable.setValueAt(ctx.name, contextIndex, 0);
        } catch (Exception e) 
        {
            this.Updatelog(e.getMessage());
            return;
        }


        this.ContextsStackContentjTable.addRowSelectionInterval(contextIndex, contextIndex);

        
        this.CurrentServer.ShownContextIndex = contextIndex;
        
        if (shapes != null) {
            ctx.shapes = shapes;
            ctx.activities = activities;
            ctx.gen_class = gen_class;
            ctx.id = id;
            ctx.generality = gen;
            if (values != null) 
            {
                ctx.values = values;
            }
            m = (DefaultTableModel) this.ContextContentjTable.getModel();
            m.setRowCount(0);

            for (String s : shapes) {
                m.addRow(new String[]{s, activities[i], gen_class[i], gen[i], id[i++]});
            }
        }

        if (values != null) 
        {
            m = (DefaultTableModel) this.ContextPropertiesjTable.getModel();
            m.setRowCount(0);
            i = 0;
            for (String s : values) {
                m.addRow(new String[]{Context.properties[i], s, Context.types[i], Context.prop_access[i++]});
            }
        }
        
        
        CurrentSession.PopContextContentColumnsWidths();
        CurrentSession.PopSessionPropertiesTableColumnsWidths();
        
    }

    public void ClearRs()
    {
         if (CurrentSession == null)
        {
            this.Updatelog("No Session selected. 'New' aborted. \n");
            return;
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.Action = "Clear";
        w._frame = this;
        w.session = CurrentSession;
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.execute();       
    }
    public void NewRs()
    {
        if (CurrentSession == null)
        {
            this.Updatelog("No Session selected. 'New' aborted. \n");
            return;
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.Action = "New";
        w._frame = this;
        w.session = CurrentSession;
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.execute();
    }
    
    public void RsOnTop()
    {
        if (CurrentSession == null) 
        {
            this.Updatelog("No Session selected. 'onTop' aborted. \n");
            return;
        }
        int[] rows = this.ResultsStackContentjTable.getSelectedRows();

        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{1};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.Action = "onTop";
        w.session = CurrentSession;
        w.indices = rows;
        w._frame = this;
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.execute();
    }
    public void RsSortBy()
    {
        if (CurrentSession == null) 
        {
            this.Updatelog("No Session selected. 'Normalize' aborted. \n");
            return;
        }

        int[] rows = this.ResultsStackContentjTable.getSelectedRows();

        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{0};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.Action = "SortBy";
        w.session = CurrentSession;
        w.range = (String) this.ResultsSortByFieldComboBox.getSelectedItem();
        w.consolidation = (String) this.ResultsSortByOrderjComboBox.getSelectedItem();
        w.indices = rows;
        w._frame = this;
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.execute();
    }
    
    public void RsSelectBy()
    {
        if (this.CurrentSession == null) 
        {
            this.Updatelog("No Session selected. 'SelectBy' aborted. \n");
            return;
        }
        int[] rows = this.ResultsStackContentjTable.getSelectedRows();
        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{0};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.Action = "SelectBy";
        w.session = CurrentSession;
        w.colname = (String) this.ResultsSelectByFieldjComboBox.getSelectedItem();
        w.operator = (String) this.ResultsSelectByOperatorjComboBox.getSelectedItem();
        w.op1 = this.operand1ResultsSelectByjTextField.getText();
        w.op2 = this.Operand2ResultsSelectByjTextField.getText();
        w.indices = rows;
        w._frame = this;
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.execute();
    }
    public void RsIntersection()
    {
        if (CurrentSession == null) 
        {
            this.Updatelog("No Session selected. 'Intersection' aborted. \n");
            return;
        }
        int[] rows = this.ResultsStackContentjTable.getSelectedRows();
        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{0};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.Action = "Intersection";
        w.session = CurrentSession;
        w._frame = this;
        w.indices = rows;
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.execute();
    }
    public void RsUnion()
    {
        if (CurrentSession == null) 
        {
            this.Updatelog("No Session selected. 'Intersection' aborted. \n");
            return;
        }
        int[] rows = this.ResultsStackContentjTable.getSelectedRows();
        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{0};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.session = CurrentSession;
        w.Action = "Union";
        w._frame = this;
        w.indices = rows;
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.execute();
    }
    public void RsDup()
    {
        if (CurrentSession == null) 
        {
            this.Updatelog("No Session selected. 'Intersection' aborted. \n");
            return;
        }
        int[] rows = this.ResultsStackContentjTable.getSelectedRows();
        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{0};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.session = CurrentSession;
        w.Action = "Dup";
        w._frame = this;
        w.indices = rows;
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.execute();
    }
       public void RsDrop()
    {
        if (CurrentSession == null) 
        {
            this.Updatelog("No Session selected. 'Intersection' aborted. \n");
            return;
        }
        int[] rows = this.ResultsStackContentjTable.getSelectedRows();
        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{0};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.session = CurrentSession;
        w.Action = "Drop";
        w._frame = this;
        w.indices = rows;
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.execute();
    }
    public void RsAmplify()
    {
        if (CurrentSession == null) 
        {
            this.Updatelog("No Session selected. 'Amplify' aborted. \n");
            return;
        }
        int[] rows = this.ResultsStackContentjTable.getSelectedRows();
        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{0};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.Action = "Amplify";
        w.session = CurrentSession;
        w._frame = this;
        w.indices = rows;
        w.range = this.ResultsAmplifySlopejTextField.getText();
        w.consolidation = this.ResultsAmplifyBjTextField.getText();
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.execute();
    }
    public void RsSwap()
    {
        if (CurrentSession == null) {
            this.Updatelog("No Session selected. 'Swap' aborted. \n");
            return;
        }
        int[] rows = this.ResultsStackContentjTable.getSelectedRows();
        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{0};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.session = CurrentSession;
        w.Action = "Swap";
        w.indices = rows;
        w._frame = this;
        w.fetchTopMostContent = true;
        w.fetchAll = true;
        w.execute();
        
    }
 
     public void ClearSession()
    {
         if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "ClearSession";
        w.fetchAll = true;
        w.execute();       
    }
    
    public void ContextsClear()
    {
         if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "Clear";
        w.fetchAll = true;
        w.execute();       
    }
    public void ContextNew()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "New";
        w.fetchAll = true;
        w.execute();
    }
    public void RsNormalize()
    {
        if (CurrentSession == null) 
        {
            this.Updatelog("No Session selected. 'Normalize' aborted. \n");
            return;
        }
        int[] rows = this.ResultsStackContentjTable.getSelectedRows();
        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{0};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.session = CurrentSession;
        w.Action = "Normalize";
        w.range = (String) this.ResultsNormalizejComboBox.getSelectedItem();
        w._frame = this;
        w.fetchTopMostContent = true;
        w.fetchAll = true;
        w.execute();
    }
    public void RsDeleteBy()
    {
        if (CurrentSession == null) {
            this.Updatelog("No Session selected. 'Normalize' aborted. \n");
            return;
        }
        int[] rows = this.ResultsStackContentjTable.getSelectedRows();
        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{0};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.Action = "DeleteBy";
        w.session = CurrentSession;
        w.colname = (String) this.ResultsDeleteByFieldjComboBox.getSelectedItem();
        w.operator = (String) this.ResultsDeleteByOrderjComboBox.getSelectedItem();
        w.op1 = this.operand1ResultsDeleteByjTextField.getText();
        w.op2 = this.Operand2ResultsDeleteByjTextField.getText();
        w._frame = this;
        w.indices = rows;
        w.fetchTopMostContent = true;
        w.fetchAll = true;
        w.execute();
    }
    public void RsUniqueBy()
    {
        if (CurrentSession == null) 
        {
            this.Updatelog("No Session selected. 'UniqueBy' aborted. \n");
            return;
        }
        int[] rows = this.ResultsStackContentjTable.getSelectedRows();
        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{0};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.Action = "UniqueBy";
        w.session = CurrentSession;
        w._frame = this;
        w.colname = (String) this.ResultsUniqueByFieldjComboBox.getSelectedItem();
        w.fetchTopMostContent = true;
        w.fetchAll = true;

        w.execute();
    }
    
    public void RsSelectToTable()
    {
        if (CurrentTable == null) 
        {
            this.Updatelog("No Table selected. 'SelectFromTable' aborted. \n");
            return;
        }
        if (CurrentSession == null) {
            this.Updatelog("No Session selected. 'SelectFromTable' aborted. \n");
            return;
        }
        int[] rows = this.ResultsStackContentjTable.getSelectedRows();
        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{0};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.Action = "SelectToTable";
        w.session = CurrentSession;
        w.range = (String) this.selectToTableDestinationTableNamejComboBox.getSelectedItem();
        w.mode = String.valueOf(this.SelectToTableUniquejCheckBox.isSelected());
        w.colname = (String) this.SelectToTableFieldjComboBox.getSelectedItem();
        w._frame = this;
        w.fetchAll = true;
        // w.fetchTopMostContent = true;
        w.execute();
    }
    public void RsSelectFromTable()
    {
        if (CurrentTable == null) 
        {
            this.Updatelog("No Table selected. 'SelectFromTable' aborted. \n");
            return;
        }
        if (CurrentSession == null) {
            this.Updatelog("No Session selected. 'SelectFromTable' aborted. \n");
            return;
        }
        int[] rows = this.ResultsStackContentjTable.getSelectedRows();
        if (rows == null || rows.length == 0) 
        {
            rows = new int[]{0};
        }
        ProcessResultsActionWorker w = new ProcessResultsActionWorker();
        w.Action = "SelectFromTable";
        w.session = CurrentSession;
        w.tableName = CurrentTable.name;
        w.operator = (String) this.OperatorSelectFromTablejComboBox1.getSelectedItem();
        w.op1 = this.operand1SelectFromTablejTextField1.getText();
        w.op2 = this.Operand2SelectFromTablejTextField1.getText();
        w.mode = (String) this.SelectFromTableModejComboBox.getSelectedItem();
        w.colname = (String) this.SelectFromTableFieldjComboBox.getSelectedItem();
        w._frame = this;
        w.fetchAll = true;
        // w.fetchTopMostContent = true;
        w.execute();
    }
    public void ContextOnTop()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        int rows[] = this.ContextsStackContentjTable.getSelectedRows();
        if ( rows == null || rows.length == 0)
        {
            this.Updatelog("No contexts selected. \"OnTop\" aborted. \n");
            return;
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "OnTop";
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
    
    public void ContextIntersection()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        int rows[] = this.ContextsStackContentjTable.getSelectedRows();
        if ( rows == null || rows.length == 0)
        {
            rows = new int[]{0,1};
        }
        this.ContextsStackContentjTable.clearSelection();
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "Intersection";
        w.consolidation = (String) this.ContextIntersectionjComboBox.getSelectedItem();
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
        
    }
    public void ContextUnion()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        int rows[] = this.ContextsStackContentjTable.getSelectedRows();
        if ( rows == null || rows.length == 0)
        {
            rows = new int[]{0,1};
        }
        this.ContextsStackContentjTable.clearSelection();
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "Union";
        w.consolidation = (String) this.ContextIntersectionjComboBox.getSelectedItem();
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
    
    public void ContextDrop()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        int rows[] = this.ContextsStackContentjTable.getSelectedRows();
        if ( rows == null || rows.length == 0)
        {
            rows = new int[]{0};
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "Drop";
        w.consolidation = (String) this.ContextIntersectionjComboBox.getSelectedItem();
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
    
    public void ContextDup()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        int rows[] = this.ContextsStackContentjTable.getSelectedRows();
        if ( rows == null || rows.length == 0)
        {
            rows = new int[]{0};
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "Dup";
        w.consolidation = (String) this.ContextIntersectionjComboBox.getSelectedItem();
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
    
    public void ContextSwap()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        int rows[] = this.ContextsStackContentjTable.getSelectedRows();
        if ( rows == null || rows.length == 0)
        {
            rows = new int[]{0};
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "Dup";
        w.consolidation = (String) this.ContextIntersectionjComboBox.getSelectedItem();
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
 
    public void ContextAmplify()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        int rows[] = this.ContextsStackContentjTable.getSelectedRows();
        if ( rows == null || rows.length == 0)
        {
            rows = new int[]{0};
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "Amplify";
        w.range = this.ContextAmplifySlopejTextField.getText();
        w.consolidation = this.ContextAmplifyBjTextField.getText();
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
    
    public void ContextSplit()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        
        int[] rows;
        if (this.ContextsStackContentjTable.getSelectedRow() != -1 )
        {
                rows = new int[]{ this.ContextsStackContentjTable.getSelectedRow() };
        }
        else
        {
                rows = new int[]{0};
        }

        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "Split";
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
       public void ContextSortBy()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        int rows[] = this.ContextsStackContentjTable.getSelectedRows();
        if ( rows == null || rows.length == 0)
        {
            rows = new int[]{0};
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "SortBy";
        w.range = (String) this.ContextSortByFieldComboBox.getSelectedItem();
        w.consolidation = (String) this.ContextSortByOrderjComboBox.getSelectedItem();
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
       public void ContextLearn()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        int rows[] = this.ContextsStackContentjTable.getSelectedRows();
        if ( rows == null || rows.length == 0)
        {
            rows = new int[]{0};
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "Learn";
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
    public void ContextNormalize()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        int rows[] = this.ContextsStackContentjTable.getSelectedRows();
        if ( rows == null || rows.length == 0)
        {
            rows = new int[]{0};
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "Normalize";
        w.range = (String) this.ContextNormalizejComboBox.getSelectedItem();
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
    public void ContextToContext()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        
        int[] rows;
        if (this.ContextsStackContentjTable.getSelectedRow() != -1 )
        {
                rows = new int[]{ this.ContextsStackContentjTable.getSelectedRow() };
        }
        else
        {
                rows = new int[]{0};
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "ContextToContext";
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
    public void ContextToInhibitor()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        
        int[] rows;
        if (this.ContextsStackContentjTable.getSelectedRow() != -1 )
        {
                rows = new int[]{ this.ContextsStackContentjTable.getSelectedRow() };
        }
        else
        {
                rows = new int[]{0};
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "ContextToInhibitor";
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
    public void InhibitorToContext()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "InhibitorToContext";
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.execute();
    }
        public void ContextToProfiler()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        
        int[] rows;
        if (this.ContextsStackContentjTable.getSelectedRow() != -1 )
        {
                rows = new int[]{ this.ContextsStackContentjTable.getSelectedRow() };
        }
        else
        {
                rows = new int[]{0};
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "ContextToProfiler";
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
    public void ProfilerToContext()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "ProfilerToContext";
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.execute();
    }
    public void ContextToDoc()
    {
        if (CurrentSession == null )
        {
            this.Updatelog("No Session selected. Aborting \n");
        } 
        int rows[] = this.ContextsStackContentjTable.getSelectedRows();
        if ( rows == null || rows.length == 0)
        {
            rows = new int[]{0};
        }
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w._frame = this;
        w.session = CurrentSession;
        w.Action = "ContextToDoc";
        w.range = (String) this.ContextNormalizejComboBox.getSelectedItem();
        w.fetchAll = true;
        w.fetchTopMostContent = true;
        w.rows = rows;
        w.execute();
    }
    public void updateContextsStack() 
    {
        if ( CurrentSession == null )
        {
            return;
        }
        DefaultTableModel m = (DefaultTableModel) this.ContextsStackContentjTable.getModel();
        m.setRowCount(0);
        if (this.CurrentSession.contextsStack.stack.isEmpty()) 
        {
            this.Updatelog("No contexts in current session contexts stack. \n");
        }

        this.CurrentContext = null;
        int i = 0;
        for (Context ctx : this.CurrentSession.contextsStack.stack) 
        {
            if (ctx.name.isEmpty() || ctx.name == null) 
            {
                ctx.name = "#" + i;
                if ( ctx.values != null )
                {
                   ctx.name += ctx.values[1]+" Elements";
                } // ProcessContextActionWorker.GetAContextName();
            }
            m.addRow(new String[]{ctx.name});
            if ( i == this.CurrentSession.shownContextIndex )
            {
                ContextsStackContentjTable.addRowSelectionInterval(i,i);
            }
            i++;
        }
        
        CurrentSession.PopSelectedContexts();
    }

    public void UpdateCurrentSessionProperties() {
        if (CurrentSession == null) {
            return;
        }
        CurrentSession.name = this.CurrentSession.values[0];
        DefaultTableModel m = (DefaultTableModel) SessionPropertiesjTable.getModel();
        m.setRowCount(0);
        int i = 0;
        if (CurrentSession.properties == null) {
            return;
        }
        for (String s : Session.properties) {
            m.addRow(new String[]{s, this.CurrentSession.values[i], Session.types[i], Session.prop_access[i++]});
        }
        int index = this.CurrentServer.getindexOfSession(CurrentSession);
        if (index != -1) {
            this.ServerSessionsjTable.setValueAt(CurrentSession.name, index, 1);
        }
        CurrentSession.propertiesChanged = true;
    }

    public void ShowInsertedData(String rowid) 
    {
        if ( this.CurrentServer == null )
        {
            return;
        }
        int id = Integer.parseInt(rowid);
        int start = id - Integer.parseInt(SizejTextField.getText()) / 2;
        int end = id + Integer.parseInt(SizejTextField.getText()) / 2;
        int size;
        if (start < 0) {
            start = 0;
        }

        if (end >= Integer.parseInt(CurrentTable.lines)) {
            end = Integer.parseInt(CurrentTable.lines) - 1;
        }

        size = end - start;
        StartjTextField.setText(String.valueOf(start));

        this.UpdateTableContent(false);
    }

    public void UpdateContentTablejTextArea(String content) {
        ContentTablejTextArea.setText(content);

    }

    public void UpdateCurrentTableContent(boolean restore, boolean showAbstract, int abstractField_index, int numberOflines, String[] fields, String[][] lines) {

        if ( CurrentServer == null )
        {
            return;
        }
        String result;
        DefaultTableModel model = (DefaultTableModel) ContentjTable.getModel();
        model.setRowCount(0);
        model.setColumnCount(fields.length);
        model.setColumnIdentifiers(fields);
        String[] row = new String[fields.length];
        int k = 0;

        if (!showAbstract) {
            for (int i = 0; i < numberOflines; i++) {
                k = 0;
                for (String field : fields) {
                    result = lines[k][i];
                    if (result != null) {

                        row[k++] = result;
                    } else {
                        row[k++] = "NULL";
                    }
                }
                model.addRow(row);
            }
        } else {
            // on doit extraire l'abstract

            for (int i = 0; i < numberOflines; i++) {
                k = 0;
                for (String field : fields) {
                    if (k == abstractField_index) {
                        // on passe
                        if (k == fields.length) {
                            break;
                        }
                        k++;
                        continue;
                    }

                    result = lines[k][i];
                    if (result != null || result.isEmpty() ) {

                        row[k++] = result;
                    } else {
                        row[k++] = "NULL";
                    }
                }
                // on extrait l'abstract

                if (lines[abstractField_index][i] == null || lines[abstractField_index][i].isEmpty()) {
                    // l'abstract est VIDE !!!!
                    row[abstractField_index] = "NULL";
                } else {

                    row[abstractField_index] = lines[abstractField_index][i];

                }
                model.addRow(row);

            }
        }


        this.CurrentServer.PopTableContentFrameSettings();
        this.ShowTableContentRowId();
        this.ContentjTable.setEnabled(true);
    }
    
    public void DeleteTableRows()
    {
        if (CurrentTable == null )
        {
            this.Updatelog("No table selected. Aborting \n");
            return;
            
        }
        if (CurrentServer == null )
        {
            this.Updatelog("No server selected. Aborting \n");
            return;
            
        }        
        int[] rows = this.ContentjTable.getSelectedRows();
        
        if (rows == null ||  rows.length == 0)
        {
                
            this.Updatelog("No rows selected. Aborting \n");
            return;    
        }
        // on trasncrit en rowid de la table
        String[] rowids = new String[rows.length];
        DefaultTableModel m = (DefaultTableModel) ContentjTable.getModel();
        int k = 0;
        for (int i = 0; i < rows.length;i++)
        {
            rowids[k++]= (String) m.getValueAt(rows[i], 0);
        }

        TableWorker w = new TableWorker();
        w._frame = this;
        w.Action ="DeleteRows";
        w.rowids = rowids;
        w.server = CurrentServer;
        w.tablename = CurrentTable.name;
        w.execute();
        
    }

    public void DeleteTableRangeRows(int x, int y)
    {
        if (CurrentTable == null )
        {
            this.Updatelog("No table selected. Aborting \n");
            return;
            
        }
        if (CurrentServer == null )
        {
            this.Updatelog("No server selected. Aborting \n");
            return;
            
        }   
        
        DeleteRowsRangeJDialog d = new DeleteRowsRangeJDialog();
        d.setModal(true);
        d.setLocation(x+5, y+5);
        d.setVisible(true);
        
        if(d.state.equals("Ok"))
        {
            TableWorker w = new TableWorker();
            w._frame = this;
            w.Action ="DeleteRows";
            w.rowids = d.rowids;
            w.server = CurrentServer;
            w.tablename = CurrentTable.name;
            w.execute();
        }
        else if ( d.state.equals("InvalidInput"))
        {
            this.Updatelog("Input is Invalid. Aborting \n");
        }
        
    }
    
    public void UpdateDataTable(int x,int y, int row, int col)
    {
        if (CurrentTable == null )
        {
            this.Updatelog("No table selected. Aborting \n");
            return;
            
        }
        if (CurrentServer == null )
        {
            this.Updatelog("No server selected. Aborting \n");
            return;
            
        }   

        if ( row == -1 || col == -1 )
        {
                
            this.Updatelog("Nothing selected. Aborting \n");
            return;    
        }
        // on trasncrit en rowid de la table

        DefaultTableModel m = (DefaultTableModel) this.ContentjTable.getModel();
        
        String   field = (String) m.getColumnName(col);
        
        TableField tf = CurrentTable.FindTableFieldFromName(field);
        
        if ( tf != null &&  !tf.type.equals("STRING") && !tf.type.equals("CHAR")  )
        {
            this.Updatelog("unable to update field '"+field+"' \n");
            return;
        }
        String[][] contents = new String[][]{{this.ContentTablejTextArea.getText()}};

        String[] rowids = new String[]{ (String) m.getValueAt(row, 0)};
        String[] fields = new String[]{field};
          
        UpdateDataTableJDialog d = new UpdateDataTableJDialog(fields);
        d.RowIdjTextField.setText(String.valueOf(row));
        d.FieldjTextField.setText(field);
        d.UpdateDatajTextArea.setText(contents[0][0]);
        d.lines = rowids.length;
        d.texts = contents;
        d.setLocation(x+5, y+5);
        d.setVisible(true);
        if ( d.state.equals("Ok"))
        {
            TableWorker w = new TableWorker();
            w._frame = this;
            w.server = CurrentServer;
            w.Action = "UpdateData";
            w.fields = d.fields;
            w.tablename = CurrentTable.name;
            w.values = new String[][]{{d.UpdateDatajTextArea.getText()}};
            w.rowids = rowids;
            w.execute();
        }

    }
    
    public void AddDataTable(int x,int y, int row, int col)
    {
        if (CurrentTable == null )
        {
            this.Updatelog("No table selected. Aborting \n");
            return;
            
        }
        if (CurrentServer == null )
        {
            this.Updatelog("No server selected. Aborting \n");
            return;
            
        }   

        if ( row == -1 || col == -1)
        {
                
            this.Updatelog("Nothing selected. Aborting \n");
            return;    
        }
        // on trasncrit en rowid de la table

        DefaultTableModel m = (DefaultTableModel) this.ContentjTable.getModel();
        

        

        String   field = (String) m.getColumnName(col);
        
        if ( field.equals("RowId"))
        {
            this.Updatelog("unable to update field RowId");
            return;
        }
        String[] rowids = new String[]{ (String) m.getValueAt(row, 0)};
        String[] fields = new String[]{field};
            
        AddDataTableJDialog d = new AddDataTableJDialog(fields);
        d.lines = rowids.length;
        d.RowIdjTextField.setText((String) m.getValueAt(row, 0));
        d.FieldjTextField.setText(field);
        d.setLocation(x+5, y+5);
        d.setVisible(true);
        if ( d.state.equals("Ok"))
        {
            TableWorker w = new TableWorker();
            w._frame = this;
            w.server = CurrentServer;
            w.Action = "AddData";
            w.fields = d.fields;
            w.tablename = CurrentTable.name;
            w.values = new String[][]{{d.DatajTextArea.getText()}};
            w.rowids = rowids;
            w.execute();
        }

    }
        
    public void InsertTable(int x,int y)
    {
        if (CurrentTable == null )
        {
            this.Updatelog("No table selected. Aborting \n");
            return;
            
        }
        if (CurrentServer == null )
        {
            this.Updatelog("No server selected. Aborting \n");
            return;
            
        }   

        int col = this.ContentjTable.getSelectedColumn();
        if (  col == -1)
        {
                
            this.Updatelog("Nothing selected. Aborting \n");
            return;    
        }


        DefaultTableModel m = (DefaultTableModel) this.ContentjTable.getModel();
        

        

        String   field =  m.getColumnName(col);
        
        if ( field.equals("RowId"))
        {
            this.Updatelog("unable to update field RowId");
            return;
        }

        String[] fields = new String[]{field};
            
        
        InsertTableJDialog d = new InsertTableJDialog(fields);

        d.setLocation(x+5, y+5);
        d.setVisible(true);
        if ( d.state.equals("Ok"))
        {
            TableWorker w = new TableWorker();
            w._frame = this;
            w.Action = "InsertData";
            w.fields = d.fields;
            w.tablename = CurrentTable.name;
            w.values = new String[][]{{d.DatajTextArea.getText()}};
            w.execute();
        }
    }
     
    public void RSContentDocToContext(int row)
    {
        if (CurrentTable == null )
        {
            this.Updatelog("No table selected. Aborting \n");
            return;
            
        }
        if (CurrentSession == null )
        {
            this.Updatelog("No session selected. Aborting \n");
            return;
            
        }   

        // on trasncrit en rowid de la table

        DefaultTableModel m = (DefaultTableModel) this.ResultSetContentjTable.getModel();
        int[] rows = this.ResultSetContentjTable.getSelectedRows();
        if ( rows == null || rows.length == 0)
        {
            if ( row == -1)
            {
                this.Updatelog("not rows supplied. Aborting \n");
                return;
            }
            rows = new int[]{row};
        }
        String[] rowids =  new String[rows.length];
        int k = 0;
        for (int r : rows)
        {
            rowids[k++] = (String) (String) m.getValueAt(r, 0);
        }
        
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w.Action = "DocToContext";
        w._frame = this;
        w.id = rowids;
        w.consolidation = "true";
        w.session = CurrentSession;
        w.fetchTopMostContent = true;
        w.execute();
    }
    public void TableContentDocToContext(int r, int c)
    {
        if (CurrentTable == null )
        {
            this.Updatelog("No table selected. Aborting \n");
            return;
            
        }
        if (CurrentSession == null )
        {
            this.Updatelog("No session selected. Aborting \n");
            return;
            
        }   

        // on trasncrit en rowid de la table

        DefaultTableModel m = (DefaultTableModel) this.ContentjTable.getModel();
        int[] rows = this.ContentjTable.getSelectedRows();
        if ( rows == null || rows.length == 0)
        {
            if ( r == -1)
            {
                this.Updatelog("No selected Table line. Aborting \n");
                return;
            }
            // un clic droit a eu lieu
            rows = new int[]{r};
        }

        String[] rowids =  new String[rows.length];
        int k = 0;
        for (int row : rows)
        {
            rowids[k++] = (String) (String) m.getValueAt(row, 0);
        }
        
        ProcessContextActionWorker w = new ProcessContextActionWorker();
        w.Action = "DocToContext";
        w._frame = this;
        w.id = rowids;
        w.consolidation = "true";
        w.session = CurrentSession;
        w.fetchTopMostContent = true;
        w.execute();
    }
    public void UpdateDataTable_FutureUse(int x,int y)
    {
        if (CurrentTable == null )
        {
            this.Updatelog("No table selected. Aborting \n");
            return;
            
        }
        if (CurrentServer == null )
        {
            this.Updatelog("No server selected. Aborting \n");
            return;
            
        }   
        int[] rows = this.ContentjTable.getSelectedRows();
        
        if (rows == null ||  rows.length == 0)
        {
                
            this.Updatelog("No rows selected. Aborting \n");
            return;    
        }
        // on trasncrit en rowid de la table

        DefaultTableModel m = (DefaultTableModel) this.ContentjTable.getModel();
        
        String[] fields = new String[ m.getColumnCount() - 1];
        
        for ( int j = 1 ; j < m.getColumnCount();j++ )
        {
           fields[j] = (String) m.getColumnName(j);
        }

        String[] rowids = new String[rows.length];
        String[][] contents = new String[rowids.length][fields.length];
        int k = 0;
        for (int i = 0; i < rows.length;i++)
        {
            rowids[k++]= (String) m.getValueAt(rows[i], 0);
            // le contenu
            int l = 0;
            for (int j = 1; j  < m.getColumnCount();j++ )
            {
                contents[i][l++] = (String) m.getValueAt( rows[i], j);
            }
        }
        UpdateDataTableJDialog d = new UpdateDataTableJDialog(fields);
        d.lines = rowids.length;
        d.texts = contents;
        d.setVisible(true);
        if ( d.equals("Ok"))
        {
            TableWorker w = new TableWorker();
            w._frame = this;
            w.Action = "UpdateData";
            w.fields = fields;
            w.tablename = CurrentTable.name;
            w.values = d.texts;
            w.rowids = rowids;
            w.execute();
        }

    }
    public static String SecondsToDate(String ms)
    {
        Long  l = (long) (int) Double.parseDouble(ms);       
        long days = TimeUnit.SECONDS.toDays(l);

        l -= days * 3600*24;
        long hours = TimeUnit.SECONDS.toHours(l);

        l -= hours * 3600;
        long minutes = TimeUnit.SECONDS.toMinutes(l);
        
        l -= minutes * 60;
        
        long seconds = TimeUnit.SECONDS.toSeconds(l);
        
        
        String retour = days+" Day";
        if ( days > 1 )
        {
            retour += "s ";
        }
        else
        {
            retour += " ";
        }            
        retour+= hours+" Hour";
        if ( hours > 1 )
        {
            retour += "s ";
        }
        else
        {
            retour += " ";
        }
        retour+= minutes+" minute";
        if (minutes > 1)
        {
            retour +="s ";
        }
                else
        {
            retour += " ";
        }
        retour += seconds+" Second";
        if ( seconds > 1)
        {
            retour +="s";
        }
        return retour;
    }
    public void updateServerStats(Server server) {

        if (!this.ServerNameTextField.isFocusOwner()) {
            ServerNameTextField.setText(server.name);
        }
        ModelTextField.setText(server.model);
        VersionTextField.setText(server.version);
        BuildTextField.setText(server.build);
        PortTextField.setText(server.port);
        IPTextField.setText(server.ip);
        ConnectCountjTextField.setText(server.connection_count);
        UpTimeTextField.setText(SecondsToDate(server.up_time) );
        GMTimeTextField.setText( server.time_gmt );
        IdleTimeTextField.setText(SecondsToDate(server.idle_time) );
        if (!this.CommandThreadsTextField.isFocusOwner()) {
            CommandThreadsTextField.setText(server.command_threads);
        }
        if (!this.CacheSizeTextField.isFocusOwner()) {
            CacheSizeTextField.setText(server.cache_size);
        }
        IndexationCacheUsedTextField.setText(server.cache_used);
        if (!this.IndexationCacheSizeTextField.isFocusOwner()) {
            IndexationCacheSizeTextField.setText(server.indexation_cache_size);
        }
        IndexationCacheUsedTextField.setText(server.indexation_cache_used);
        CacheHitsTextField.setText(server.cache_hits);
        particlesjTextField.setText(server._marc.particles);
        shapesjTextField.setText(server._marc.shapes);
        relationsjTextField.setText(server._marc.relations);
        referencesjTextField.setText(server._marc.references);
        if (!this.IndexationTimeoutTextField.isFocusOwner()) {
            this.IndexationTimeoutTextField.setText(server.indexation_timeout);
        }
        if (!this.ExecTimeoutDefaultTextField.isFocusOwner()) {
            this.ExecTimeoutDefaultTextField.setText(server.exec_timeout_default);
        }
        if (!this.SessionTimeoutDefaultTextField.isFocusOwner()) {
            this.SessionTimeoutDefaultTextField.setText(server.session_timeout_default);
        }
        float ratio = 100 * (Float.parseFloat(server.indexation_cache_used) / Float.parseFloat(server.indexation_cache_size));
        IndexationCacheProgressBar.setValue((int) ratio);
        IndexationCacheProgressBar.setString(String.valueOf((int) ratio) + "%");
        DefaultTableModel model = (DefaultTableModel) this.ServerSessionsjTable.getModel();
        model.setRowCount(0);
        server.SessionsToTable(model);

        model = (DefaultTableModel) TablesjTable.getModel();

        server.TablesToTable(TablesjTable);

        model = (DefaultTableModel) this.KTreesjTable.getModel();
        server.KIndexesToTable(model);

        model = (DefaultTableModel) this.BTreejTable.getModel();
        server.BIndexesToTable(model);

        server.TablesToTree(this.ServersTree);
        server.BIndexesToTree(ServersTree);
        server.KIndexesToTree(ServersTree);
        server.SessionsToTree(ServersTree);
        model = (DefaultTableModel) LastDBInfojTable.getModel();
        model.setRowCount(0);
        model.addRow(new String[]{server.lastDBInfoTableName, server.lastDBInfoId, server.lastDBInfoOperation, server.lastDBInfoStatus});

        model = (DefaultTableModel) tasksjTable.getModel();
        server.TasksToTable(model);

    }

    public void UpdateTablesForResultsInternalFrame() {


            if (CurrentTable == null) 
            {
                this.ToggleEnableContainersOnPanel(ResultsDeleteByjPanel, false);
                this.ToggleEnableContainersOnPanel(ResultsSelectByjPanel, false);
                this.ToggleEnableContainersOnPanel(ContextSortByjPanel, false);
                this.ToggleEnableContainersOnPanel(ResultsUniqueByjPanel, false);
                this.ToggleEnableContainersOnPanel(SelectFromTablejPanel, false);
                return;
            } else {
                this.ToggleEnableContainersOnPanel(ResultsDeleteByjPanel, true);
                this.ToggleEnableContainersOnPanel(ResultsSelectByjPanel, true);
                this.ToggleEnableContainersOnPanel(ContextSortByjPanel, true);
                this.ToggleEnableContainersOnPanel(ResultsUniqueByjPanel, true);
                this.ToggleEnableContainersOnPanel(SelectFromTablejPanel, true);

            }
            this.ResultsDeleteByFieldjComboBox.removeAllItems();
            this.ResultsSelectByFieldjComboBox.removeAllItems();
            this.ContextSortByFieldComboBox.removeAllItems();
            this.ResultsUniqueByFieldjComboBox.removeAllItems();
            this.ResultsSortByFieldComboBox.removeAllItems();
            for (TableField tf : CurrentTable.fields) 
            {
                if (!tf.name.equals("ABSTRACT") && !tf.name.contains("KNW")) 
                {
                    this.ResultsDeleteByFieldjComboBox.addItem(tf.name);
                    this.ResultsSelectByFieldjComboBox.addItem(tf.name);
                    this.ContextSortByFieldComboBox.addItem(tf.name);
                    this.ResultsSortByFieldComboBox.addItem(tf.name);
                    this.ResultsUniqueByFieldjComboBox.addItem(tf.name);
                }
            }

            this.selectToTableDestinationTableNamejComboBox.removeAllItems();
            if (CurrentServer != null) 
            {
                synchronized( this.CurrentServer.tables )
                {
                    for (Table t : this.CurrentServer.tables) 
                    {
                    this.selectToTableDestinationTableNamejComboBox.addItem(t.name);
                    }
                }
            }
            if ( this.selectToTableDestinationTableNamejComboBox.getItemCount() != 0)
            {
                String tablename = (String) this.selectToTableDestinationTableNamejComboBox.getItemAt(0);
                Table tbl = CurrentServer.FindTableFromName(tablename);
                if (tbl == null) 
                {
                    return;
                }
                this.SelectToTableFieldjComboBox.removeAllItems();
                for (TableField s : tbl.fields) {
                    this.SelectToTableFieldjComboBox.addItem(s.name);
                }
            }

    }

    public void updateKnowLedgeGraph(String[] theNodes) {

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTable BTreejTable;
    private javax.swing.JPanel BTreesPanel;
    private javax.swing.JLabel BuildLabel;
    private javax.swing.JTextField BuildTextField;
    private javax.swing.JLabel CTLabel;
    private javax.swing.JTextField CacheHitsTextField;
    private javax.swing.JTextField CacheSizeTextField;
    private javax.swing.JLabel CacheUsedLabel;
    private javax.swing.JButton ClearSessionjButton;
    private javax.swing.JTextField CommandThreadsTextField;
    private javax.swing.JLabel ConnectCountLabel;
    private javax.swing.JTextField ConnectCountjTextField;
    public javax.swing.JTextArea ContentTablejTextArea;
    public javax.swing.JTable ContentjTable;
    public javax.swing.JTextField ContextAmplifyBjTextField;
    public javax.swing.JTextField ContextAmplifySlopejTextField;
    private javax.swing.JPanel ContextAmplifyjPanel;
    private javax.swing.JPanel ContextCommandjPanel;
    public javax.swing.JTable ContextContentjTable;
    public javax.swing.JComboBox ContextIntersectionjComboBox;
    private javax.swing.JPanel ContextIntersectionjPanel;
    public javax.swing.JComboBox ContextNormalizejComboBox;
    private javax.swing.JPanel ContextNormalizejPanel;
    private javax.swing.JInternalFrame ContextPropertiesjInternalFrame;
    public javax.swing.JTable ContextPropertiesjTable;
    public javax.swing.JComboBox ContextSortByFieldComboBox;
    public javax.swing.JComboBox ContextSortByOrderjComboBox;
    private javax.swing.JPanel ContextSortByjPanel;
    public javax.swing.JComboBox ContextUnionjComboBox;
    private javax.swing.JPanel ContextUnionjPanel;
    public javax.swing.JTable ContextsStackContentjTable;
    public javax.swing.JSplitPane ContextsStackjSplitPane;
    private javax.swing.JInternalFrame ContextsjInternalFrame;
    private javax.swing.JPanel ContextsjPanel;
    public javax.swing.JSplitPane CtxStackAndContentjSplitPane;
    private javax.swing.JPanel CurrentServerjPanel;
    private javax.swing.JTextField CurrentServerjTextField;
    private javax.swing.JTextField CurrentSessionjTextField;
    private javax.swing.JTextField CurrentTablejTextField;
    private javax.swing.JPanel DesktopsjPanel;
    private javax.swing.JTextField ExecTimeoutDefaultTextField;
    public javax.swing.JTable FieldsTable;
    private javax.swing.JLabel GMTimeLabel;
    private javax.swing.JTextField GMTimeTextField;
    private javax.swing.JLabel IPLabel;
    private javax.swing.JTextField IPTextField;
    private javax.swing.JLabel IdleTimeLabel;
    private javax.swing.JTextField IdleTimeTextField;
    private javax.swing.JProgressBar IndexationCacheProgressBar;
    private javax.swing.JTextField IndexationCacheSizeTextField;
    private javax.swing.JTextField IndexationCacheUsedTextField;
    private javax.swing.JPanel IndexationPanel;
    private javax.swing.JTextField IndexationTimeoutTextField;
    private javax.swing.JPanel KTreePanel;
    public javax.swing.JTable KTreesjTable;
    private javax.swing.JInternalFrame KnowledgeGraphjInternalFrame;
    private javax.swing.JPanel KnowledgeGraphjPanel;
    public javax.swing.JSplitPane LDBTreesjSplitPane;
    public javax.swing.JTable LastDBInfojTable;
    private javax.swing.JInternalFrame LogjInternalFrame;
    private javax.swing.JTextArea LogjTextArea;
    private javax.swing.JPanel MTPanel;
    public javax.swing.JSplitPane MainWindowjSplitPane;
    private javax.swing.JLabel ModelLabel;
    private javax.swing.JTextField ModelTextField;
    public javax.swing.JTextField Operand2ResultsDeleteByjTextField;
    public javax.swing.JTextField Operand2ResultsSelectByjTextField;
    public javax.swing.JTextField Operand2SelectFromTablejTextField1;
    public javax.swing.JComboBox OperatorSelectFromTablejComboBox1;
    private javax.swing.JLabel PercentCacheHitsLabel;
    private javax.swing.JLabel PortLabel;
    private javax.swing.JTextField PortTextField;
    public javax.swing.JSplitPane RSstackjSplitPane;
    private javax.swing.JCheckBox RebuildRefjCheckBox;
    private javax.swing.JInternalFrame ResultSetContentjInternalFrame;
    public javax.swing.JTable ResultSetContentjTable;
    private javax.swing.JInternalFrame ResultSetPropertiesjInternalFrame;
    public javax.swing.JTextField ResultsAmplifyBjTextField;
    public javax.swing.JTextField ResultsAmplifySlopejTextField;
    public javax.swing.JComboBox ResultsDeleteByFieldjComboBox;
    public javax.swing.JComboBox ResultsDeleteByOrderjComboBox;
    private javax.swing.JPanel ResultsDeleteByjPanel;
    public javax.swing.JComboBox ResultsNormalizejComboBox;
    public javax.swing.JTable ResultsPropertiesjTable;
    public javax.swing.JComboBox ResultsSelectByFieldjComboBox;
    public javax.swing.JComboBox ResultsSelectByOperatorjComboBox;
    private javax.swing.JPanel ResultsSelectByjPanel;
    public javax.swing.JComboBox ResultsSortByFieldComboBox;
    public javax.swing.JComboBox ResultsSortByOrderjComboBox;
    private javax.swing.JPanel ResultsSortByjPanel;
    public javax.swing.JTable ResultsStackContentjTable;
    public javax.swing.JComboBox ResultsUniqueByFieldjComboBox;
    private javax.swing.JPanel ResultsUniqueByjPanel;
    private javax.swing.JInternalFrame ResultsjInternalFrame;
    public javax.swing.JComboBox SelectFromTableFieldjComboBox;
    public javax.swing.JComboBox SelectFromTableModejComboBox;
    private javax.swing.JPanel SelectFromTablejPanel;
    public javax.swing.JComboBox SelectToTableFieldjComboBox;
    public javax.swing.JCheckBox SelectToTableUniquejCheckBox;
    private javax.swing.JPanel SelectToTablejPanel;
    public javax.swing.JInternalFrame ServerCurrentSessionjInternalFrame;
    private javax.swing.JPanel ServerIdPanel;
    public javax.swing.JSplitPane ServerInternaljSplitPane;
    private javax.swing.JLabel ServerNameLabel;
    public javax.swing.JTextField ServerNameTextField;
    private javax.swing.JPanel ServerSessionsjPanel;
    public javax.swing.JTable ServerSessionsjTable;
    private javax.swing.JTree ServersTree;
    public javax.swing.JSplitPane SessionPropertiesSpectrumjSplitPane;
    private javax.swing.JPanel SessionPropertiesjPanel;
    public javax.swing.JTable SessionPropertiesjTable;
    public javax.swing.JTable SessionSpectrumjTable;
    private javax.swing.JTextField SessionTimeoutDefaultTextField;
    public javax.swing.JSplitPane SessionsTasksjSplitPane;
    public javax.swing.JTextField SizejTextField;
    public javax.swing.JTextField StartjTextField;
    public javax.swing.JSplitPane TableContentTextAreajSplitPane;
    private javax.swing.JScrollPane TableContentjScrollPane;
    public javax.swing.JSplitPane TableContentjSplitPane;
    public javax.swing.JSlider TableLinesjSlider;
    private javax.swing.JPanel TableStructurePanel;
    private javax.swing.JInternalFrame TableVisualjInternalFrame;
    private javax.swing.JPanel TablesPanel;
    private javax.swing.JInternalFrame TablesjInternalFrame;
    public javax.swing.JSplitPane TablesjSplitPane;
    public javax.swing.JTable TablesjTable;
    private javax.swing.JPanel TasksjPanel;
    private javax.swing.JPanel TimePanel;
    private javax.swing.JLabel UpTimeLabel;
    private javax.swing.JTextField UpTimeTextField;
    private javax.swing.JLabel VersionLabel;
    private javax.swing.JTextField VersionTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton7;
    private javax.swing.JCheckBox jCheckBox1;
    public javax.swing.JDesktopPane jDesktopPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelMainWindow;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    public javax.swing.JScrollPane jScrollPane23;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JScrollPane jScrollPane25;
    private javax.swing.JScrollPane jScrollPane26;
    private javax.swing.JScrollPane jScrollPane27;
    private javax.swing.JScrollPane jScrollPane28;
    private javax.swing.JScrollPane jScrollPane29;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane30;
    private javax.swing.JScrollPane jScrollPane31;
    private javax.swing.JScrollPane jScrollPane33;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane17;
    private javax.swing.JSplitPane jSplitPane18;
    private javax.swing.JSplitPane jSplitPane5;
    public javax.swing.JTextField jTextField8;
    private javax.swing.JTextArea logjTextArea;
    private javax.swing.JPanel mARCPanel;
    private javax.swing.JTextField mARCRebuildFieldsjTextField;
    private javax.swing.JTextField mARCRebuildFromjTextField;
    private javax.swing.JTextField mARCRebuildTojTextField;
    private javax.swing.JLabel masDepthjLabel;
    private javax.swing.JSlider maxDepthjSlider;
    public javax.swing.JTextField maxDepthjTextField;
    private javax.swing.JLabel maxSizejLabel;
    private javax.swing.JSlider maxSizejSlider;
    public javax.swing.JTextField maxSizejTextField;
    public javax.swing.JTextField operand1ResultsDeleteByjTextField;
    public javax.swing.JTextField operand1ResultsSelectByjTextField;
    public javax.swing.JTextField operand1SelectFromTablejTextField1;
    private javax.swing.JLabel particlesjLabel;
    private javax.swing.JTextField particlesjTextField;
    private javax.swing.JLabel referencesjLabel;
    private javax.swing.JTextField referencesjTextField;
    private javax.swing.JLabel relationsjLabel;
    private javax.swing.JTextField relationsjTextField;
    private javax.swing.JCheckBox restartjCheckBox;
    public javax.swing.JComboBox selectToTableDestinationTableNamejComboBox;
    private javax.swing.JLabel shapesjLabel;
    private javax.swing.JTextField shapesjTextField;
    public javax.swing.JCheckBox stringToContextLearnjCheckBox;
    private javax.swing.JButton stringToContextjButton;
    public javax.swing.JSplitPane stringToContextjSplitPane;
    public javax.swing.JTextArea stringToContextjTextArea;
    public javax.swing.JTable tasksjTable;
    // End of variables declaration//GEN-END:variables
}
