package LexicalSeparator;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class OutputTable {
    JFrame f;
    
    public OutputTable(List<Token> list) {
        f = new JFrame();
        String col[] = {"Type", "Value"};
        DefaultTableModel tableModel = new DefaultTableModel(col, 0);
        
        for (int i=0; i<list.size(); i++) {
            Object[] row = {list.get(i).getType(), list.get(i).getValue()};
            tableModel.addRow(row);
        }
        
        JTable jt = new JTable(tableModel);
        
        jt.setBounds(30,40,200,300);
        JScrollPane sp = new JScrollPane(jt);
        f.add(sp);
        f.setSize(450,600);    
        f.setVisible(true);
        
        f.addWindowListener(new CloseButtonHandler());
    }
    
    // To end the program when the window is closed
    private class CloseButtonHandler extends WindowAdapter {
        public void windowClosing(WindowEvent we) {
            System.exit(0);
        }
    }
}