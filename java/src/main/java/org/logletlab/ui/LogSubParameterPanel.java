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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.logletlab.LogletLabDataSet;
import org.logletlab.model.AbstractFitParameters;
import org.logletlab.model.LogSubParameters;

public class LogSubParameterPanel extends AbstractFitParameterPanel 
	implements ChangeListener, ActionListener
{
	javax.swing.JPanel colorcode;
	javax.swing.JSpinner spinStart, spinStop, spinDT, spinTM;

	// LogSubModel model;
	LogSubParameters params;
	
	public LogSubParameterPanel(LogSubModelPanel f, LogletLabDataSet ds, Color c, boolean showLabels) {
		parent = f;
		//model = m;
		dataset = ds;
		if (parent.model != null) {
			// Look for existing parameter sets
			// if none exists, create a new set
			params = (LogSubParameters)dataset.getFitParameters(parent.model);
			if (params == null)	{
				dataset.addFit(parent.model,null);
				params = (LogSubParameters)dataset.getFitParameters(parent.model);
			}
		}

		try {
			initComponents(showLabels,c);
			colorcode.setBackground(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void initComponents(boolean showLabels, Color c) {
		// INITIALIZE THE LAYOUT MANAGER
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		this.setLayout(gridbag);

		if (showLabels) {
			// just the labels
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.weightx=0;
			JLabel label = new JLabel("start"); 
			gridbag.setConstraints(label,constraints);
			this.add(label);
			constraints.gridy++;
			label = new JLabel("stop");
			gridbag.setConstraints(label,constraints);
			this.add(label);
			/*
			constraints.gridy++;
			label = new JLabel("Midpoint (tm)");
			gridbag.setConstraints(label,constraints);
			this.add(label);
			constraints.gridy++;
			label = new JLabel("Time (dT)");
			gridbag.setConstraints(label,constraints);
			this.add(label);

			constraints.gridy++;
			label = new JLabel("");
			gridbag.setConstraints(label,constraints);
			this.add(label);
			*/
		}



		constraints.gridx = (showLabels?1:0);
		constraints.weightx = 0.25;

		// color tag on top
		constraints.gridy = 0;
		colorcode = new JPanel();
		gridbag.setConstraints(colorcode,constraints);
		this.add(colorcode);

		constraints.gridy++;
		spinStart = new JSpinner();
		spinStart.setBorder(new javax.swing.border.LineBorder(c));
		gridbag.setConstraints(spinStart, constraints);
		this.add(spinStart);
						
		constraints.gridy++;
		spinStop = new JSpinner();
		spinStop.setBorder(new javax.swing.border.LineBorder(c));
		gridbag.setConstraints(spinStop, constraints);
		this.add(spinStop);

		/*
		constraints.gridy++;
		spinTM = new JSpinner();
		spinTM.setBorder(new javax.swing.border.LineBorder(c));
		gridbag.setConstraints(spinTM, constraints);
		this.add(spinTM);
			
		constraints.gridy++;
		spinDT = new JSpinner();
		spinDT.setBorder(new javax.swing.border.LineBorder(c));
		gridbag.setConstraints(spinDT, constraints);
		this.add(spinDT);
		*/
		setParameters(dataset.getFitParameters(parent.model));

		addChangeListeners(this);
		addChangeListeners(parent.parent);
	}
	

	public void addChangeListeners(ChangeListener l) {
		spinStart.addChangeListener(l);
		spinStop.addChangeListener(l);
		/*
		spinDT.addChangeListener(l);
		spinTM.addChangeListener(l);
		*/
	}

	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitParameterPanel#fitDriver()
	 */
	public void fitDriver() {
	}

	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitParameterPanel#getParameters()
	 */
	public AbstractFitParameters getParameters() {
		return params;
		/*
		double start = ((Double)spinStart.getValue()).doubleValue();
		double stop  = ((Double) spinStop.getValue()).doubleValue();
		return new LogSubParameters(start, stop, false);
		*/
	}

	/* (non-Javadoc)
	 * @see LogletLab.AbstractFitParameterPanel#setParameters(LogletLab.AbstractFitParameters)
	 */
	public void setParameters(AbstractFitParameters p) {
		LogSubParameters lsp = (LogSubParameters) p;
		
		spinStart.setModel( new SpinnerNumberModel(lsp.start,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,1) );
		spinStop.setModel ( new SpinnerNumberModel(lsp.stop,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,1) );
		/*
		double tm, t1, dt; 
		tm = -lsp.tm[0] / lsp.dt[0];
		t1 = (1-lsp.dt[0]) / lsp.tm[0];
		dt = 2 * (t1-tm);

		spinDT.setModel   ( new SpinnerNumberModel(dt,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,1) );
		spinTM.setModel   ( new SpinnerNumberModel(tm,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,1) );
		*/
	}


	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		params.start = ((Double)spinStart.getValue()).doubleValue();
		params.stop  = ((Double) spinStop.getValue()).doubleValue();
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "Fit") {
			// refresh values
			this.setParameters(params);
		}
		
	}

}
