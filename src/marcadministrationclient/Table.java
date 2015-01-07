/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author patrice
 */
public class Table {
    
    Server _server;
    String name;
    String lastDBInfo;
    
    final List<TableField> fields;
    String lines;
    final List<KIndex> kIndexes = Collections.synchronizedList(new ArrayList<KIndex>());
    final List<BIndex> bIndexes = Collections.synchronizedList(new ArrayList<BIndex>());
    
    ArrayList<KIndex> KIndextoRemove = new ArrayList<>();
    ArrayList<BIndex> BIndextoRemove = new ArrayList<>();    
    ArrayList<TableField> FieldstoRemove = new ArrayList<>();
    
    
    boolean toUpdate;
    
    public Table() {
        this.fields = Collections.synchronizedList(new ArrayList<TableField>());
        
    }
    

    
    public TableField FindTableFieldFromName(String name)
    {
        synchronized(fields)
        {
            for ( TableField tf : fields)
            {
                if (tf.name.equals(name) )
                {
                    return tf;
                }
            }
        }
        return null; 
    }      

    public BIndex FindBIndexFromName(String name)
    {
        synchronized(bIndexes)
        {
            for ( BIndex bi : bIndexes )
            {
                if ( bi.name.equals(name) )
                {
                    return bi;
                }
            }
        }
        return null; 
    }
    
    public KIndex FindKIndexFromName(String name)
    {
        synchronized(this.kIndexes)
        { 
            for ( KIndex bi : kIndexes )
            {
                if ( bi.name.equals(name) )
                {
                    return bi;
                }
            }
        }
        return null; 
    }
    
    public void UpdateStructure(String[] names, String[] types, String[] sizes)
    {
        if (names == null || names.length == 0)
        {
            return;
        }
        TableField tf = null;
        
        synchronized(fields)
        {
            for (TableField t : fields)
            {
                t.toUpdate = false;
            }

            for (int i = 0; i < names.length;i++)
            {
                tf = FindTableFieldFromName(names[i]);
                if ( tf == null )
                {
                    tf = new TableField();
                    fields.add(tf);

                }
                tf.name = names[i];
                tf.size = sizes[i];
                tf.type = types[i];
                tf.toUpdate = true;


            }

            for (TableField t : fields)
            {
                if (!t.toUpdate)
                {
                    FieldstoRemove.add(t);
                }
            }  

            fields.removeAll(FieldstoRemove);
            FieldstoRemove.clear();
        }
    }
    
    
    public void UpdateBIndexes(String[] names, String[] status, String[] progress, String[] unique)
    {
        if (names == null || names.length == 0)
        {
            return;
        }
        BIndex bi = null;
        int i = 0;
        
        synchronized(this.bIndexes)
        {
            for ( BIndex b : bIndexes )
            {
                b.toUpdate = false;
            }
            for (String n : names)
            {
                bi = FindBIndexFromName(n);
                if (bi == null )
                {
                    bi = new BIndex();
                    bi.name = n;
                    bIndexes.add(bi);
                }
                bi.status = status[i];
                bi.progress = progress[i];
                bi.boolUnique = unique[i++];
                bi.toUpdate = true;
            }

            for ( BIndex b : bIndexes )
            {
                if ( !b.toUpdate )
                {
                    BIndextoRemove.add(b);
                }
            }

            bIndexes.removeAll(BIndextoRemove);

            BIndextoRemove.clear();
        }
    }
    public void UpdateKIndexes(String[] names, String[] status, String[] progress)
    {
        if (names == null || names.length == 0)
        {
            return;
        }
        KIndex bi = null;
        int i = 0;
        synchronized(this.kIndexes)
        {
            for ( KIndex b : kIndexes )
            {
                b.toUpdate = false;
            }

            for (String n : names)
            {
                bi = FindKIndexFromName(n);
                if (bi == null )
                {
                    bi = new KIndex();
                    bi.name = n;
                    kIndexes.add(bi);
                }
                bi.progress = progress[i];
                bi.status = status[i++];
                bi.toUpdate = true;
            }

            for ( KIndex b : kIndexes )
            {
                if ( !b.toUpdate )
                {
                    KIndextoRemove.add(b);
                }
            }

            kIndexes.removeAll(KIndextoRemove);
            KIndextoRemove.clear();
        }
    }
    
    public void FieldsToTable(DefaultTableModel model)
    {
        model.setRowCount(0);
        
        synchronized(fields)
        {
            if ( fields.isEmpty())
            {
                return;
            }

            for ( TableField ft : fields)
            {
                model.addRow(new String[]{ft.name,ft.type,ft.size});
            }
        }
       _server.PopFieldsTableColumnsWidths();
    }
    
    public void kIndexesToTable(DefaultTableModel model)
    {
        model.setRowCount(0);
        
        
        synchronized(kIndexes)
        {
            if ( kIndexes.size() == 0)
            {
                return;
            }

            for ( KIndex ft : kIndexes)
            {
                model.addRow(new String[]{ft.name,ft.status,ft.progress});
            }
        }
        _server.PopKTreesTableColumnsWidths();
    }
    
    public void bIndexesToTable(DefaultTableModel model)
    {
        model.setRowCount(0);
        
        synchronized(bIndexes)
        {
            if ( bIndexes.size() == 0)
            {
                return;
            }

            for ( BIndex ft : bIndexes)
            {
                model.addRow(new String[]{ft.name,ft.status,ft.progress,ft.boolUnique});
            }
        }
        _server.PopBTreesTableColumnsWidths();
    }
}
