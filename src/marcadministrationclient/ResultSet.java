/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;
import mARC.Connector.Connector;
/**
 *
 * @author patrice
 */
public class ResultSet {
    
    static public boolean firstInit = true;
    
    static public String[] properties;
    static public String[] types;
    static public String[] access;
    
    String[] values;
    String name;
    String[] format;
    String[][] cols; // les colonnes de valeurs dépendant de format
    int numRows; // la seconde dimension de cols
    public ResultSet()
    {

    }
}
