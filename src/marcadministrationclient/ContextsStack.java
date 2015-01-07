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
public class ContextsStack {
    
    Session OwnerSession;
    ArrayList<Context> stack;

    public ContextsStack() {
        this.stack = new ArrayList<>();
    }
}
