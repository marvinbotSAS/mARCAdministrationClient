/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;

/**
 *
 * @author patrice
 */
public class Context {
    
    public static String[] properties =null;
    public static String[] types = null;
    public static String[] prop_access = null;
    
    public String[] values; // contexts properties
    
    static boolean firstInit = true;
    
    String name;
    String[] shapes, activities, gen_class, generality, id;

}
