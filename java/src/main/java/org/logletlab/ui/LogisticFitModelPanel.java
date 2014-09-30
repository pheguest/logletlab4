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
 * LogisticFitModelPanel.java
 *
 * Title:			LogletLab
 * Description:		logistic analysis tool and more.
 * @author			J
 * @version			
 */

package org.logletlab.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import org.logletlab.LogletLabDataSet;
import org.logletlab.model.AbstractFitModel;
import org.logletlab.model.LogisticFitModel;
import org.logletlab.model.LogisticFitParameters;

public class LogisticFitModelPanel extends AbstractFitModelPanel
{
	// member declarations
	javax.swing.JRadioButton xformNormal = new javax.swing.JRadioButton();
	javax.swing.JRadioButton xformDecomposed = new javax.swing.JRadioButton();
	javax.swing.JRadioButton xformFisherPry = new javax.swing.JRadioButton();
	javax.swing.JRadioButton xformBellCurve = new javax.swing.JRadioButton();
	javax.swing.JRadioButton residualsOff = new javax.swing.JRadioButton();
	javax.swing.JRadioButton residualsAbsolute = new javax.swing.JRadioButton();
	javax.swing.JRadioButton residualsPctage = new javax.swing.JRadioButton();

	javax.swing.JSpinner nLogistics;

	ButtonGroup xformGroup, resGroup;

	
	public LogisticFitModelPanel(LogletLabFrame f, LogisticFitModel m) {
		parent = f;
		model = m;
		try {
			initComponents();
			nLogistics.setModel ( new SpinnerNumberModel(m.nLogistics,1,Integer.MAX_VALUE,1) );
		} catch (Exception e) {
			e.printStackTrace();
		}
		addChangeListeners(parent);
		
		// Go through the data sets, and get the parameters.
		for (int i = 0; i < parent.getDoc().getNSets(); i++) {
			LogletLabDataSet ds = parent.getDoc().getDataSet(i);
			// Create inputs
			this.add(new LogisticFitParameterPanel(this, ds, null, ds.getColor(), i==0));
		}

	}

	public void initComponents() throws Exception {
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));

		nLogistics = new JSpinner();
		nLogistics.setPreferredSize(new java.awt.Dimension(20,5));
		
		Box box = new Box(BoxLayout.Y_AXIS);
		box.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Transforms"));
		xformNormal.setText("Normal");
		xformNormal.setVisible(true);
		xformDecomposed.setText("Decomposed");
		xformDecomposed.setVisible(true);
		xformFisherPry.setText("Fisher-Pry");
		xformFisherPry.setVisible(true);
		xformBellCurve.setText("Bell curves");
		xformBellCurve.setVisible(true);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		panel.add(nLogistics,BorderLayout.CENTER);
		panel.add(new JLabel("# of pulses:"),BorderLayout.WEST);
		
		Box box2 = new Box(BoxLayout.Y_AXIS);
		box2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Residuals"));
		residualsOff.setSize(new java.awt.Dimension(50, 20));
		residualsOff.setText("Off");
		residualsOff.setVisible(true);
		residualsAbsolute.setSize(new java.awt.Dimension(90, 20));
		residualsAbsolute.setText("Absolute");
		residualsAbsolute.setVisible(true);
		residualsPctage.setSize(new java.awt.Dimension(100, 20));
		residualsPctage.setText("Percentage");
		residualsPctage.setVisible(true);


		box.add(xformNormal);
		box.add(xformDecomposed);
		box.add(xformFisherPry);
		box.add(xformBellCurve);
		this.add(box);
		this.add(Box.createHorizontalGlue());
		
		box2.add(residualsOff);
		box2.add(residualsAbsolute);
		box2.add(residualsPctage);
		
		panel.add(box2,BorderLayout.SOUTH);
		this.add(panel);
		
		xformGroup = new ButtonGroup();
		xformGroup.add(xformNormal);
		xformGroup.add(xformDecomposed);
		xformGroup.add(xformFisherPry);
		xformGroup.add(xformBellCurve);

		resGroup = new ButtonGroup();
		resGroup.add(residualsOff);
		resGroup.add(residualsAbsolute);
		resGroup.add(residualsPctage);
		
		// SELECT FROM EACH GROUP
		// TODO: read from document to get the mode
		xformNormal.setSelected(true);
		residualsOff.setSelected(true);

		// ADD EVENT HANDLERS FOR EACH RADIO BUTTON
		xformNormal.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				setModelMode(LogisticFitModel.MODE_NORMAL);
			}
		});

		xformDecomposed.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				setModelMode(LogisticFitModel.MODE_DECOMP);
			}
		});
		
		xformFisherPry.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				setModelMode(LogisticFitModel.MODE_FP);
			}
		});

		xformBellCurve.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				setModelMode(LogisticFitModel.MODE_BELL);
			}
		});
		
		residualsOff.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				setResidualMode(AbstractFitModel.RESIDUALS_OFF);
			}
		});
		residualsAbsolute.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				setResidualMode(AbstractFitModel.RESIDUALS_ABS);
			}
		});
		residualsPctage.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				setResidualMode(AbstractFitModel.RESIDUALS_REL);
			}
		});

		nLogistics.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				setNLogistics(((Integer)nLogistics.getValue()).intValue());
			}
		});
	}
	
	public void addChangeListeners(ChangeListener l) {
		xformNormal.addChangeListener(l);
		xformDecomposed.addChangeListener(l);
		xformFisherPry.addChangeListener(l);
		xformBellCurve.addChangeListener(l);
		residualsOff.addChangeListener(l);
		residualsAbsolute.addChangeListener(l);
		residualsPctage.addChangeListener(l);
		nLogistics.addChangeListener(l);
	}
	
	public void setNLogistics(int n) {
		LogisticFitModel lfm = new LogisticFitModel(n);
		LogisticFitModel oldLFM = (LogisticFitModel)model;
		
		for (int i=0; i<this.getComponentCount(); i++) {
			if (this.getComponent(i) instanceof LogisticFitParameterPanel) {
				this.remove(i);
			}
		}
		// Go through the data sets, and get the parameters.
		for (int i = 0; i < parent.getDoc().getNSets(); i++) {
			LogletLabDataSet ds = parent.getDoc().getDataSet(i);
			// Create inputs
			model = lfm;
			LogisticFitParameters oldParams = (LogisticFitParameters) parent.getDoc().getDataSet(i).getFitParameters(oldLFM);
			this.add(new LogisticFitParameterPanel(this, ds, oldParams, ds.getColor(), i==0));			
		}
		this.validate();
		// SET CURRENT FIT TO THIS ONE.
		parent.currentFit = lfm;
	}
	
}
