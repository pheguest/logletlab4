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

import javax.swing.BoxLayout;
import javax.swing.JButton;

import org.logletlab.LogletLabDataSet;
import org.logletlab.model.LogSubModel;

/**
 * @author jyung
 *
 * Holds the Fit button.
 */
public class LogSubModelPanel extends AbstractFitModelPanel {
	LogSubParameterPanel parameterPanels[];
	JButton doit;
	
	public LogSubModelPanel(LogletLabFrame f, LogSubModel m) {
		parent = f;
		model = m;
		initComponents();
	}
	
	public void fitDriver() {
		((LogSubModel)model).fitDriver(parent.getDoc());
		for (int i = 0; i < parent.getDoc().getNSets(); i++) {
			parameterPanels[i].setParameters(parameterPanels[i].params);
		}
	}
	
	public void initComponents() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		// Go through the data sets, and get the parameters.
		parameterPanels = new LogSubParameterPanel[parent.getDoc().getNSets()];
		for (int i = 0; i < parent.getDoc().getNSets(); i++) {
			LogletLabDataSet ds = parent.getDoc().getDataSet(i);
			// Create inputs
			parameterPanels[i] = new LogSubParameterPanel(this,ds, ds.getColor(), i==0);
			this.add(parameterPanels[i]);
		}

		doit = new JButton("Fit");
		this.add(doit);
		
		doit.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent e) {
				fitDriver();
				parent.refreshGraph();
			}
		});

	}
}
