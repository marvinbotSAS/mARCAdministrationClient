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

import mARC.Connector.Connector;

/**
 *
 * @author patrice
 */
public class Session 
{

    String RSFormat; 
   boolean pushed; 
   boolean propertiesChanged;
   boolean spectrumChanged;
    boolean showResultsFrameContent;
    boolean showContextsFrameContent;
    boolean showSessionProperties;
    boolean showSessionSpectrum;
    boolean showResulSetStack;
    int[] SelectedResulSets;
    int shownRSContentIndex;
    int[] SelectedContexts ;
    int propertiesSpectrumDividerlocation;
    
    int shownContextIndex;
    int[] sessionPropertiesTableColumnsWidhts;
    int[] sessionSpectrumTableColumnsWidhts;
    int[] contextContentColumnsWidhts;
    int[] ResulSetContentColumnsWidths;
    int[] ResulSetPropertiesColumnsWidths;
    
    Server owner;
    
    String name;
    String id;
    String persistant;
    String owner_ip;
    String owner_port;
    String priority;
    String exec_timeout;
    String session_timeout;
    String debug;
    Connector connector;
    boolean toUpdate;
    
    int ctxStaskDividerlocation;
    
    int ctxStackAndContentDividerlocation;
    
    int RSstackDividerLocation;
    int ResultsSortByFieldItemIndex;
    int   ResultsSortByOrderItemIndex;
    String    operand1ResultsSelectBy;
    String    operand2ResultsSelectBy;
    String    operand1ResultsDeleteBy;
    String    operand2ResultsDeleteBy;
    int    ResultsNormalizeItemIndex;
    int    ResultsUniqueByItemIndex;
    int    SelectFromTableModeItemIndex;
    int    SelectFromTableFieldItemIndex;
    int    SelectToTableFieldItemIndex;
    boolean selectToTableUnique;
    String    selectToTableDestinationTableNameItem;
    String    ResultsAmplifySlope;
    String    ResultsAmplifyB;
        
    int stringToContextDividerLocation;
    
    String stringToContextTextArea;
    
    int[] contextsStackColumnsWidths;
    int[] contextPropertiesColumnsWidths;
    int contextUnionItemIndex ;
    int contextIntersectionItemIndex;
    int contextSortByFieldItemIndex;
    int contextSortByOrderItemIndex;
    int contextNormalizeItemIndex ;
    String contextAmplifySlope ;
    String contextAmplifyB;
    
    public boolean owned;
    public static String[] properties =null;
    public static String[] types = null;
    public static String[] prop_access = null;
    
    public static String[] spectrum_names = null;
    public static String[] spectrum_types = null;
    
    /**
     *
     */
    public static boolean  firstInit = true;
    
    public  String[] spectrum_values = null;
    public String[] values=null;
    ContextsStack contextsStack;
    ResultsStack  RSStack;
    
    public Session() 
    {
        this.spectrumChanged = false;
        this.propertiesChanged = false;
        this.pushed = false;
        this.shownContextIndex = -1;

        
         this.showResultsFrameContent = false;
        this.showContextsFrameContent = false;
        this.shownContextIndex = -1;
        this.shownRSContentIndex = -1;
        this.SelectedContexts = null;

        owner = null;
        this.contextsStack = new ContextsStack();
        this.RSStack = new ResultsStack();
        this.connector = null;
        RSStack.OwnerSession = this;
        owned = false;
    }
    
    public void PushSelectedRSStackRows()
    {
        this.SelectedResulSets = this.owner._frame.ResultsStackContentjTable.getSelectedRows();
    }
    
    public void PushSelectedContexts()
    {
        this.SelectedContexts =  this.owner._frame.ContextsStackContentjTable.getSelectedRows();
    }

    public void PopSelectedContexts()
    {
        if ( this.SelectedContexts == null || this.SelectedContexts.length == 0 )
        {
            return;
        }
        
        this.owner._frame.ContextsStackContentjTable.clearSelection();
        for (int i = 0; i < this.SelectedContexts.length;i++)
        {
            this.owner._frame.ContextsStackContentjTable.addRowSelectionInterval(i, i);
        }
    }
    
    public void PushContextCommandsParameters()
    {
        contextUnionItemIndex = this.owner._frame.ContextUnionjComboBox.getSelectedIndex();
        contextIntersectionItemIndex = this.owner._frame.ContextIntersectionjComboBox.getSelectedIndex();
        contextSortByFieldItemIndex = this.owner._frame.ContextSortByFieldComboBox.getSelectedIndex();
        contextSortByOrderItemIndex = this.owner._frame.ContextSortByOrderjComboBox.getSelectedIndex();
        contextNormalizeItemIndex = this.owner._frame.ContextNormalizejComboBox.getSelectedIndex();
        contextAmplifySlope = this.owner._frame.ContextAmplifySlopejTextField.getText();
        contextAmplifyB = this.owner._frame.ContextAmplifyBjTextField.getText();
    }
    public void PushResultsCommandsParameters()
    {
        this.RSstackDividerLocation = this.owner._frame.RSstackjSplitPane.getDividerLocation();
        ResultsSortByFieldItemIndex = this.owner._frame.ResultsSortByFieldComboBox.getSelectedIndex();
        ResultsSortByOrderItemIndex = this.owner._frame.ResultsSortByOrderjComboBox.getSelectedIndex();
        operand1ResultsSelectBy =     this.owner._frame.operand1ResultsSelectByjTextField.getText();
        operand2ResultsSelectBy = this.owner._frame.Operand2ResultsSelectByjTextField.getText();
        operand1ResultsDeleteBy = this.owner._frame.operand1ResultsDeleteByjTextField.getText();
        operand2ResultsDeleteBy = this.owner._frame.Operand2ResultsDeleteByjTextField.getText();
        ResultsNormalizeItemIndex = this.owner._frame.ResultsNormalizejComboBox.getSelectedIndex();
        ResultsUniqueByItemIndex = this.owner._frame.ResultsUniqueByFieldjComboBox.getSelectedIndex();
        SelectFromTableModeItemIndex = this.owner._frame.SelectFromTableModejComboBox.getSelectedIndex();
        SelectFromTableFieldItemIndex = this.owner._frame.SelectFromTableFieldjComboBox.getSelectedIndex();
        SelectToTableFieldItemIndex = this.owner._frame.SelectToTableFieldjComboBox.getSelectedIndex();
        this.selectToTableUnique = this.owner._frame.SelectToTableUniquejCheckBox.isSelected();
        if ( this.owner._frame.selectToTableDestinationTableNamejComboBox.getItemCount() != 0)
        {
            selectToTableDestinationTableNameItem = (String) this.owner._frame.selectToTableDestinationTableNamejComboBox.getItemAt(0);
        }
        else
        {
            selectToTableDestinationTableNameItem = "none";
        }
        ResultsAmplifySlope = this.owner._frame.ResultsAmplifySlopejTextField.getText();
        ResultsAmplifyB = this.owner._frame.ResultsAmplifyBjTextField.getText();

    }
    
    public void PushRSContentColumnsWidths()
    {
       int cols =  this.owner._frame.ResultSetContentjTable.getColumnCount();
       if (cols == 0 )
       {
           return;
       }
       
       this.ResulSetContentColumnsWidths = new int[cols];
       this.RSFormat = "";
       for (int i = 0; i < cols;i++)
       {
           this.ResulSetContentColumnsWidths[i] = this.owner._frame.ResultSetContentjTable.getColumnModel().getColumn(i).getWidth();
           this.RSFormat += this.owner._frame.ResultSetContentjTable.getColumnName(i)+" ";
       }
       this.RSFormat = this.RSFormat.trim();
    }
    
     public void PopRSContentColumnsWidths()
    {
        if ( this.ResulSetContentColumnsWidths == null )
        {
            return;
        }
       int cols =  this.owner._frame.ResultSetContentjTable.getColumnCount();
       if (cols == 0 )
       {
           return;
       }
       if ( this.ResulSetContentColumnsWidths.length != cols )
       {
           this.PushRSContentColumnsWidths();
           return;
       }
       this.ResulSetContentColumnsWidths = new int[cols];
       for (int i = 0; i < cols;i++)
       {
          this.owner._frame.ResultSetContentjTable.getColumnModel().getColumn(i).setWidth( this.ResulSetContentColumnsWidths[i]);
       }
    }
    
    public void PopResultsCommandsParameters()
    {
        this.owner._frame.RSstackjSplitPane.setDividerLocation(this.RSstackDividerLocation);
        this.owner._frame.ResultsSortByFieldComboBox.setSelectedIndex(ResultsSortByFieldItemIndex);
        this.owner._frame.ResultsSortByOrderjComboBox.setSelectedIndex(ResultsSortByOrderItemIndex);
        this.owner._frame.operand1ResultsSelectByjTextField.setText(operand1ResultsSelectBy);    
        this.owner._frame.Operand2ResultsSelectByjTextField.setText(operand2ResultsSelectBy); 
        this.owner._frame.operand1ResultsDeleteByjTextField.setText(operand1ResultsDeleteBy);
        this.owner._frame.Operand2ResultsDeleteByjTextField.setText(operand2ResultsDeleteBy);
        this.owner._frame.ResultsNormalizejComboBox.setSelectedIndex(ResultsNormalizeItemIndex);
        this.owner._frame.ResultsUniqueByFieldjComboBox.setSelectedIndex(ResultsUniqueByItemIndex);
        this.owner._frame.SelectFromTableModejComboBox.setSelectedIndex(SelectFromTableModeItemIndex);
        this.owner._frame.SelectFromTableFieldjComboBox.setSelectedIndex(SelectFromTableFieldItemIndex);
        this.owner._frame.SelectToTableFieldjComboBox.setSelectedIndex(SelectToTableFieldItemIndex);
        this.owner._frame.selectToTableDestinationTableNamejComboBox.setSelectedItem(selectToTableDestinationTableNameItem); 
        this.owner._frame.SelectToTableUniquejCheckBox.setSelected(selectToTableUnique);
        this.owner._frame.ResultsAmplifySlopejTextField.setText(ResultsAmplifySlope);
        this.owner._frame.ResultsAmplifyBjTextField.setText(ResultsAmplifyB);

    }
    
    public void PopContextCommandsParameters()
    {
        this.owner._frame.ContextUnionjComboBox.setSelectedIndex(contextUnionItemIndex);
        this.owner._frame.ContextIntersectionjComboBox.setSelectedIndex(contextIntersectionItemIndex);
        this.owner._frame.ContextSortByFieldComboBox.setSelectedIndex(contextSortByFieldItemIndex);
        this.owner._frame.ContextSortByOrderjComboBox.setSelectedIndex(contextSortByOrderItemIndex);
        this.owner._frame.ContextNormalizejComboBox.setSelectedIndex(contextNormalizeItemIndex);
        this.owner._frame.ContextAmplifySlopejTextField.setText(contextAmplifySlope);
        this.owner._frame.ContextAmplifyBjTextField.setText(contextAmplifyB);
    }
    public void PushSessionPropertiesTableColumnsWidths()
    {
        this.propertiesSpectrumDividerlocation = this.owner._frame.SessionPropertiesSpectrumjSplitPane.getDividerLocation();
        if ( this.owner._frame.SessionPropertiesjTable.getColumnCount() !=0 )
        {
            this.sessionPropertiesTableColumnsWidhts = new int[this.owner._frame.SessionPropertiesjTable.getColumnModel().getColumnCount()];
            for (int i = 0; i < this.owner._frame.SessionPropertiesjTable.getColumnModel().getColumnCount();i++)
            {
                this.sessionPropertiesTableColumnsWidhts[i] = this.owner._frame.SessionPropertiesjTable.getColumnModel().getColumn(i).getWidth();
            }
        }
        if ( this.owner._frame.SessionSpectrumjTable.getColumnCount() !=0 )
        {        
            this.sessionSpectrumTableColumnsWidhts = new int[this.owner._frame.SessionSpectrumjTable.getColumnModel().getColumnCount()];
            for (int i = 0; i < this.owner._frame.SessionSpectrumjTable.getColumnModel().getColumnCount();i++)
            {
                this.sessionSpectrumTableColumnsWidhts[i] = this.owner._frame.SessionSpectrumjTable.getColumnModel().getColumn(i).getWidth();
            }        
        }
    }
    
    public void PopSessionPropertiesTableColumnsWidths()
    {
        this.owner._frame.SessionPropertiesSpectrumjSplitPane.setDividerLocation(this.propertiesSpectrumDividerlocation);
        if ( this.sessionPropertiesTableColumnsWidhts == null )
        {
            return;
        }
        for (int i = 0; i < this.owner._frame.SessionPropertiesjTable.getColumnModel().getColumnCount();i++)
        {
            this.owner._frame.SessionPropertiesjTable.getColumnModel().getColumn(i).setWidth(this.sessionPropertiesTableColumnsWidhts[i]);
        }
        if (this.sessionSpectrumTableColumnsWidhts == null )
        {
            return;
        }
        for (int i = 0; i < this.owner._frame.SessionSpectrumjTable.getColumnModel().getColumnCount();i++)
        {
            this.owner._frame.SessionSpectrumjTable.getColumnModel().getColumn(i).setWidth(this.sessionSpectrumTableColumnsWidhts[i]);
        }
    }
    
    public void PushContextPropertiesTableColumnsWidths()
    {
        contextPropertiesColumnsWidths = new int[this.owner._frame.ContextPropertiesjTable.getColumnCount()];
        for (int i = 0; i < this.owner._frame.ContextPropertiesjTable.getColumnCount();i++)
        {
            contextPropertiesColumnsWidths[i] = this.owner._frame.ContextPropertiesjTable.getColumnModel().getColumn(i).getWidth();
        }
    }
    
    public void PopContextPropertiesTableColumnsWidths()
    {

        for (int i = 0; i < this.owner._frame.ContextPropertiesjTable.getColumnCount();i++)
        {
            this.owner._frame.ContextPropertiesjTable.getColumnModel().getColumn(i).setWidth(contextPropertiesColumnsWidths[i]);
        }
    }
    
    public void PushContextContentColumnsWidths()
    {
        this.contextContentColumnsWidhts = new int[this.owner._frame.ContextContentjTable.getColumnCount()];
        for (int i = 0; i < this.owner._frame.ContextContentjTable.getColumnCount();i++)
        {
            this.contextContentColumnsWidhts[i] = this.owner._frame.ContextContentjTable.getColumnModel().getColumn(i).getWidth();
        }
    }
    
    public void PopContextContentColumnsWidths()
    {

        if(  this.contextContentColumnsWidhts == null || this.contextContentColumnsWidhts.length == 0)
        {
            return;
        }
        for (int i = 0; i < this.owner._frame.ContextContentjTable.getColumnCount();i++)
        {
           this.owner._frame.ContextContentjTable.getColumnModel().getColumn(i).setWidth( this.contextContentColumnsWidhts[i] );
        }
    }
     
    public void PushContextsFrameSettings()
    {
        this.stringToContextDividerLocation = this.owner._frame.stringToContextjSplitPane.getDividerLocation();
        this.stringToContextTextArea = this.owner._frame.stringToContextjTextArea.getText();
        this.ctxStaskDividerlocation = this.owner._frame.ContextsStackjSplitPane.getDividerLocation();
        this.ctxStackAndContentDividerlocation =  this.owner._frame.CtxStackAndContentjSplitPane.getDividerLocation();

        this.PushSelectedContexts();
        this.PushContextContentColumnsWidths();
        this.PushContextCommandsParameters();
    }
    
    public void PopContextsFrameSettings()
    {
        this.owner._frame.stringToContextjSplitPane.setDividerLocation(this.stringToContextDividerLocation);
        this.owner._frame.stringToContextjTextArea.setText(this.stringToContextTextArea);
        this.owner._frame.ContextsStackjSplitPane.setDividerLocation(this.ctxStaskDividerlocation);
        this.owner._frame.CtxStackAndContentjSplitPane.setDividerLocation(this.ctxStackAndContentDividerlocation);
        this.PopContextContentColumnsWidths();
        this.PopContextCommandsParameters();
    }
    
    public void PushResultsStackContentFrameSettings()
    {
        this.RSstackDividerLocation = this.owner._frame.RSstackjSplitPane.getDividerLocation();
        this.PushSelectedRSStackRows();
        this.PushResultsCommandsParameters();
    }
    
    public void PushResultSetPropertiesTableColumnsWidths()
    {
        this.ResulSetPropertiesColumnsWidths = new int[this.owner._frame.ResultsPropertiesjTable.getColumnCount()];
        for(int i = 0; i < this.owner._frame.ResultsPropertiesjTable.getColumnCount();i++)
        {
            this.ResulSetPropertiesColumnsWidths[i] = this.owner._frame.ResultsPropertiesjTable.getColumnModel().getColumn(i).getWidth();
        }
    }
 
    public void PopResultSetPropertiesTableColumnsWidths()
    {
        for(int i = 0; i < this.owner._frame.ResultsPropertiesjTable.getColumnCount();i++)
        {
             this.owner._frame.ResultsPropertiesjTable.getColumnModel().getColumn(i).setWidth(this.ResulSetPropertiesColumnsWidths[i]);
        }
    }
        
    public void PushUISettings()
    {
        // session properties et spectrum frame

        
        this.PushSessionPropertiesTableColumnsWidths();
        
        this.PushContextsFrameSettings();
        
        // results frame
        this.showResulSetStack        = this.owner._frame.ResultsStackContentjTable.getRowCount() != 0;
        this.SelectedResulSets        = this.owner._frame.ResultsStackContentjTable.getSelectedRows();
        this.shownRSContentIndex      = this.owner._frame.shownRSIndex;
        this.shownContextIndex        = this.owner._frame.ShownContextIndex;
        
        this.showResultsFrameContent = this.owner._frame.ResultSetContentjTable.getRowCount() != 0;
        
        //contexts frame
        this.showContextsFrameContent = this.owner._frame.ContextsStackContentjTable.getRowCount() != 0;
    


        this.PushResultsStackContentFrameSettings();
        this.PushResultSetPropertiesTableColumnsWidths();
        
        // session features frame
        this.showSessionProperties = this.owner._frame.SessionPropertiesjTable.getRowCount() != 0;
        this.showSessionSpectrum   = this.owner._frame.SessionSpectrumjTable.getRowCount() != 0;
        
        
        // contexts stack frame and commands parameters
        


        // context properties frame
        
        this.PushContextPropertiesTableColumnsWidths();
        
        //results stack frame

        this.PushRSContentColumnsWidths();
        
        pushed = true;
    
    }
    
    public void PopUISettings()
    {
        if ( !pushed)
        {
            return;
        }
        // session properties et spectrum frame
        
        this.PopSessionPropertiesTableColumnsWidths();

       
        this.PopRSContentColumnsWidths();
        
        // contexts stack frame and commands parameters
        this.PopContextsFrameSettings();      
        this.owner._frame.ShowContextContent(shownContextIndex);

        
        // context properties frame
        this.PopContextPropertiesTableColumnsWidths();
        // session properties & spectrum
        this.owner._frame.updateSessionProperties();
        this.owner._frame.updateSessionSpectrum();
        //results stack frame

        this.owner._frame.shownRSIndex = this.shownRSContentIndex;


        this.owner._frame.ShowResulSetStack();

        //this.owner._frame.updateResultsStack();
            

        if ( this.shownRSContentIndex != -1)
        {
            ResultSet rs = this.RSStack.stack.get(shownRSContentIndex);
            this.owner._frame.ShowResultSetProperties(rs);
            this.owner._frame.ShowResultSetContent();
        }
        
        
    }
    
    public int getIndexOfContext(Context ctx)
    {
        int i = 0;
        for (Context c :  this.contextsStack.stack )
        {
            if ( c == ctx )
            {
                return i;
            }
            i++;
        }
        return -1;
    }
    public int getIndexOfResultSet(ResultSet rs)
    {
        int i = 0;
        for (ResultSet c :  this.RSStack.stack )
        {
            if ( c == rs )
            {
                return i;
            }
            i++;
        }
        return -1;
    }
}
