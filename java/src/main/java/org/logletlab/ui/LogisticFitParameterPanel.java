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
/** 
 * LogisticFitParameterPanel.java
 *
 * Title:			LogletLab
 * Description:		Holds the paramters for Logistic fit model..
 * @author			J
 * @version			
 */

package org.logletlab.ui;

import java.awt.*;
import java.awt.event.*;
//import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.logletlab.LogletLabDataSet;
import org.logletlab.model.AbstractFitParameters;
import org.logletlab.model.LogisticFitModel;
import org.logletlab.model.LogisticFitParameters;
//import java.util.Vector;
import java.text.DecimalFormat;

public class LogisticFitParameterPanel extends AbstractFitParameterPanel
	implements ChangeListener, ItemListener
{
	// small color tag to correspond to plot color
	javax.swing.JPanel colorcode;
	// the parameter text fields
	javax.swing.JSpinner k[];
	javax.swing.JSpinner tm[];
	javax.swing.JSpinner dt[];
	// TODO: need to add icon
	javax.swing.JCheckBox cbLockK[];
	javax.swing.JCheckBox cbLockTM[];
	javax.swing.JCheckBox cbLockDT[];
	boolean lockK[];
	boolean lockTM[];
	boolean lockDT[];

	javax.swing.JSpinner displacement;
	// buttons
	javax.swing.JButton doFit, doCMFit;

	//LogisticFitModel model;
	
	int fitType = LogisticFitModel.FITTYPE_NORMAL;
	boolean isFitting;
	
	public LogisticFitParameterPanel(LogisticFitModelPanel f, LogletLabDataSet ds, LogisticFitParameters oldP, Color c, boolean showLabels) {
		parent = f;
		dataset = ds;
		//model = m;
		
		// Look for existing parameter sets
		// if none exists, create a new set
		LogisticFitParameters params;
		if (parent.model != null) {
			params = (LogisticFitParameters)dataset.getFitParameters(parent.model);
			if (params == null)	{
				dataset.addFit(parent.model,new Integer(((LogisticFitModel)parent.model).nLogistics));
				params = (LogisticFitParameters)dataset.getFitParameters(parent.model);
			}

			// Copy parameters
			if (oldP != null) {
				for (int j=0; j<oldP.getnLogistics() && j<((LogisticFitModel)parent.model).nLogistics; j++) {
					params.k[j] = oldP.k[j];
					params.tm[j] = oldP.tm[j];
					params.dt[j] = oldP.dt[j];
				}
				dataset.setFitParameters(parent.model,params);
			}
		}

		
		try {
			initComponents(showLabels,c);
			colorcode.setBackground(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void initComponents(boolean showLabels, Color c) throws Exception {
		//LogisticFitParameters params;
		
		
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
			JLabel label = new JLabel("Saturation (K)"); 
			gridbag.setConstraints(label,constraints);
			this.add(label);
			constraints.gridy++;
			label = new JLabel("Midpoint (tm)");
			gridbag.setConstraints(label,constraints);
			this.add(label);
			constraints.gridy++;
			label = new JLabel("Time (dT)");
			gridbag.setConstraints(label,constraints);
			this.add(label);
			constraints.gridy++;
			label = new JLabel("Displace");
			gridbag.setConstraints(label,constraints);
			this.add(label);
			constraints.gridy++;
			label = new JLabel("");
			gridbag.setConstraints(label,constraints);
			this.add(label);
		}

		int nLogistics = ((LogisticFitModel)parent.model).nLogistics;
		// CREATE THE TEXTFIELD ARRAYS
		k  = new JSpinner[nLogistics];
		tm = new JSpinner[nLogistics];
		dt = new JSpinner[nLogistics];
		
		cbLockK  = new JCheckBox[nLogistics];
		cbLockDT = new JCheckBox[nLogistics];
		cbLockTM = new JCheckBox[nLogistics];
		lockK  = new boolean[nLogistics];
		lockDT = new boolean[nLogistics];
		lockTM = new boolean[nLogistics];

		for (int i=0; i<nLogistics; i++) {
			// Create the text fields for this component logistic
		 	k[i] = new JSpinner();
		 	tm[i] = new JSpinner();
		 	dt[i] = new JSpinner();
			k[i].setBorder(new javax.swing.border.LineBorder(c));
			tm[i].setBorder(new javax.swing.border.LineBorder(c));
			dt[i].setBorder(new javax.swing.border.LineBorder(c));

			constraints.gridx = i*2 + (showLabels?1:0);
//			constraints.gridwidth = GridBagConstraints.RELATIVE;
			constraints.weightx = 0.5;

			// Add the text fields to the grid
			constraints.gridy=1;
			gridbag.setConstraints(k[i], constraints);
			this.add(k[i]);
						
			constraints.gridy++;
			gridbag.setConstraints(tm[i], constraints);
			this.add(tm[i]);
			
			constraints.gridy++;
			gridbag.setConstraints(dt[i], constraints);
			this.add(dt[i]);
			
			// Next column: Locks
			constraints.gridx = i*2 + 1 + (showLabels?1:0);
			constraints.weightx = 0.0;
			
			constraints.gridy=1;
			cbLockK[i] = new JCheckBox();
			cbLockK[i].addItemListener(this);
			gridbag.setConstraints(cbLockK[i], constraints);
			this.add(cbLockK[i]);
						
			constraints.gridy++;
			cbLockTM[i] = new JCheckBox();
			cbLockTM[i].addItemListener(this);
			gridbag.setConstraints(cbLockTM[i], constraints);
			this.add(cbLockTM[i]);
			
			constraints.gridy++;
			cbLockDT[i] = new JCheckBox();
			cbLockDT[i].addItemListener(this);
			gridbag.setConstraints(cbLockDT[i], constraints);
			this.add(cbLockDT[i]);


			if (i==0) { // if first column
				constraints.gridx = (showLabels?1:0);
				// Add displacement
				constraints.gridy++;
				constraints.weightx = 0.5;
				displacement = new JSpinner();
				//displacement.setHorizontalAlignment(JTextField.RIGHT);
				displacement.setBorder(new javax.swing.border.LineBorder(c));
				gridbag.setConstraints(displacement, constraints);
				this.add(displacement);
			}
		}
		// The following objects should span the entire row
				 
		// THE FIT BUTTON(s)
		constraints.gridx = (showLabels?1:0);
		constraints.gridy = 5;
		constraints.gridwidth=GridBagConstraints.RELATIVE;
		doFit = new javax.swing.JButton("Fit");
		doFit.setBorder(new javax.swing.border.LineBorder(c));
		gridbag.setConstraints(doFit, constraints);
		doFit.setVisible(true);
		this.add(doFit);
				
		constraints.gridx = nLogistics*2;
		constraints.gridwidth=GridBagConstraints.REMAINDER;
		constraints.weightx=0.0;
		doCMFit = new javax.swing.JButton("CM");
		doCMFit.setBorder(new javax.swing.border.LineBorder(c));
		gridbag.setConstraints(doCMFit, constraints);
		doCMFit.setVisible(true);
		this.add(doCMFit);


		// color tag on top
		constraints.gridwidth=GridBagConstraints.REMAINDER;
		constraints.gridx = (showLabels?1:0);
		constraints.gridy = 0;
		colorcode = new JPanel();
		gridbag.setConstraints(colorcode,constraints);
		this.add(colorcode);

		setParameters(dataset.getFitParameters(parent.model));

		doFit.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent e) {
				// fitDriver interface requires blank parameters;
				// therefore set parameters before calling it.
				fitType = LogisticFitModel.FITTYPE_NORMAL;
				fitDriver();
			}
		});
		doCMFit.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent e) {
				fitType = LogisticFitModel.FITTYPE_CM;
				fitDriver();
				((LogisticFitModelPanel)parent).xformFisherPry.setSelected(true);
			}
		});

		addChangeListeners(this);
		addChangeListeners(parent.parent);
	}

	
	
	public void setParameters(AbstractFitParameters p) {
		// Set semaphore, so that DocumentEvents aren't handled
		isFitting = true;
		
		LogisticFitParameters lfp = (LogisticFitParameters) p;
		DecimalFormat myFormatter = new DecimalFormat("#.##");
		
		for (int i=0; i<((LogisticFitModel)parent.model).nLogistics; i++) {
			k[i].setModel ( new SpinnerNumberModel(lfp.k [i],0,Double.POSITIVE_INFINITY,1) );
			dt[i].setModel ( new SpinnerNumberModel(lfp.dt[i],Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,1) );
			tm[i].setModel ( new SpinnerNumberModel(lfp.tm[i],0,Double.POSITIVE_INFINITY,1) );
			lockK [i]=lfp.holdK[i] ;
			lockDT[i]=lfp.holdDT[i];
			lockTM[i]=lfp.holdTM[i];
			cbLockK [i].setSelected(lockK[i] );
			cbLockDT[i].setSelected(lockDT[i]);
			cbLockTM[i].setSelected(lockTM[i]);
		}
		displacement.setModel ( new SpinnerNumberModel(lfp.getDisplacement(),0,Double.POSITIVE_INFINITY,1) );
		//displacement.setValue( new Double(lfp.displacement) );
		

		// Unset semaphore
		isFitting = false;
	}
	
	public AbstractFitParameters getParameters() {
		int nLogistics = ((LogisticFitModel)parent.model).nLogistics;
		double p[] = new double[nLogistics*3];
		boolean hold[] = new boolean[nLogistics*3];
		for (int i=0; i<nLogistics; i++) {
			p[i*3  ] = ((Double)(k[i].getValue())).doubleValue();
			p[i*3+1] = ((Double)(dt[i].getValue())).doubleValue();
			p[i*3+2] = ((Double)(tm[i].getValue())).doubleValue();
//			p[i*3  ] = ( k[i].getText().length()>0?Double.parseDouble( k[i].getText()):0);
//			p[i*3+1] = (dt[i].getText().length()>0?Double.parseDouble(dt[i].getText()):0);
//			p[i*3+2] = (tm[i].getText().length()>0?Double.parseDouble(tm[i].getText()):0);
			hold[i*3  ] = lockK[i];
			hold[i*3+1] = lockDT[i];
			hold[i*3+2] = lockTM[i];
		}
		return new LogisticFitParameters(
			nLogistics, p, hold,
			((Double)(displacement.getValue())).doubleValue());
	}	
  
  	private boolean mShown = false;
  	
	public void addNotify() {
		super.addNotify();
		
		if (mShown)
			return;
		mShown = true;
	}

	public void fitDriver() {
		// DO THE FIT
		((LogisticFitModel)parent.model).fitDriver(dataset, fitType);
		
		// READ NEW PARAMETERS
		setParameters((LogisticFitParameters)dataset.getFitParameters(parent.model));
		
		// UPDATE XFORM
		parent.setModelMode(parent.model.getMode());

		// UPDATE GRAPH
		parent.parent.stateChanged(new ChangeEvent(this));
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		if (!isFitting) {
			dataset.setFitParameters(parent.model,this.getParameters());
		}
	}

	public void addChangeListeners(ChangeListener l) {
		int nLogistics = ((LogisticFitModel)parent.model).nLogistics;
		for (int i=0; i< nLogistics; i++) {
			k[i].addChangeListener(l);//getDocument().addDocumentListener(l);
			tm[i].addChangeListener(l);//.addDocumentListener(l);
			dt[i].addChangeListener(l);//.addDocumentListener(l);
		}
		displacement.addChangeListener(l);//getDocument().addDocumentListener(l);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();

		int nLogistics = ((LogisticFitModel)parent.model).nLogistics;
		for (int i=0; i<nLogistics; i++) {
			if (source == cbLockK[i]) {
				lockK[i] = (e.getStateChange() == ItemEvent.SELECTED);
			} else if (source == cbLockTM[i]) {
				lockTM[i] = (e.getStateChange() == ItemEvent.SELECTED);
			} else if (source == cbLockDT[i]) {
				lockDT[i] = (e.getStateChange() == ItemEvent.SELECTED);
			}
		}
		dataset.setFitParameters(parent.model,this.getParameters());
	}
	

}
