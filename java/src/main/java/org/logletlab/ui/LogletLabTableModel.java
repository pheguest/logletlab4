/*
 * Loglet Lab 2.0
 * 
 * Copyright (c) 2003, Program for the Human Environment, The Rockefeller University,
 * except where noted. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of The Program for the Human Environment nor The Rockefeller
 *    University nor the names of its contributors may be used to endorse or 
 *    promote products derived from this software without specific prior
 *    written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 * 
 */

package org.logletlab.ui;

import javax.swing.table.AbstractTableModel;

import org.logletlab.LogletLabDataSet;
import org.logletlab.util.DPoint;


public class LogletLabTableModel extends AbstractTableModel
{
	String[] columnNames = {"x","y","Exclude?"};
	LogletLabDataSet ds;
	
	public String getColumnName(int col) { 
		return columnNames[col].toString(); 
	}
	public int getRowCount() {   	
		return ds.getNPoints()+1;
	}
	public int getColumnCount() { return columnNames.length; }
    
	public Object getValueAt(int row, int col) {
		DPoint pt;
		
		if (row >= ds.getNPoints())
			return null;
	    	
		pt = ds.getPoint(row);
		switch (col) {
		case 0:
			if (pt.isXSet)
				return new Double (pt.x);
			else
				return null;
		case 1:
			if (pt.isYSet)
				return new Double (pt.y);
			else
				return null;
		case 2:
			return new Boolean(pt.isExcluded);
		default:
			return null;
		}
	}

	public Class getColumnClass(int c) {
		Double d = new Double(1);
		Boolean b = new Boolean(true);
		if (c == 0 || c == 1) 
			return d.getClass(); //Class.forName("Double");
		else
			return b.getClass();
	}
    
	public boolean isCellEditable(int row, int col) { return true; }
	public void setValueAt(Object value, int row, int col) {
		// System.err.println("setValueAt: "+row);
		DPoint pt;
		if (value != null) {
			if (row >= ds.getNPoints()) {
				pt = new DPoint();
			} else {
				pt = ds.getPoint(row);
			}
			switch (col) {
			case 0:
				try {
					pt.x = Double.parseDouble(value.toString());
					pt.isXSet = true;
				} catch (NumberFormatException nfe) {
					pt.isXSet = false;
				}
				break;
			case 1:
				try {
					pt.y = Double.parseDouble(value.toString());
					pt.isYSet = true;
				} catch (NumberFormatException nfe) {
					pt.isYSet = false;
				}
				break;
			case 2:
				//System.err.println(value);
				pt.isExcluded = value.toString().equals("true");
			}
			if (row >= ds.getNPoints()) {
				ds.addPoint(pt);
			} else {
				ds.replacePoint(row,pt);
			}
		} else {
			pt = ds.getPoint(row);
			switch (col) {
			case 0:
				pt.x = 0;
				pt.isXSet = false;
				break;
			case 1:
				pt.y = 0;
				pt.isYSet = false;
				break;
			}
		}
		ds.getDoc().setModified(true);
		fireTableCellUpdated(row, col);
	}
    
	
	public LogletLabTableModel(LogletLabDataSet set) {
		ds = set;
	}
	
}