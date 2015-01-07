/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marcadministrationclient;
import java.util.ArrayList;

/**
 *
 * @author patrice
 */


public class ResultsStack {
  
        Session OwnerSession;
   public  ArrayList<ResultSet> stack;
    
    public ResultsStack()
    {
        this.stack = new ArrayList<>();
    }
}
