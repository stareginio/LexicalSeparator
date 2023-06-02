/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LexicalSeparator;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, String> symbols;

    public SymbolTable() {
        symbols = new HashMap<>();
    }

    public void addSymbol(String name, String info) {
        symbols.put(name, info);
    }

//    public SymbolInfo getSymbol(String name) {
//        return symbols.get(name);
//    }

    public boolean containsSymbol(String name) {
        return symbols.containsValue(name);
    }
    
    public Map<String, String> getSymbols() {
        return symbols;
    }
    
    public void updateValue(String key, String val) {
        symbols.replace(key, val);
    }
    
    public void removeSymbol(String symbolName) {
        symbols.remove(symbolName);
    }

}


