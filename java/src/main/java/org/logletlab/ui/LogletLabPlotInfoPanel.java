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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;

import javax.swing.*;

import org.logletlab.LogletLabDocument;
import org.logletlab.util.DecimalField;



/**
 * @author jyung
 *
 * This panel shows the document/plot metadata and puts it in a panel.
 */
public class LogletLabPlotInfoPanel extends JPanel
	implements ItemListener
{
	// create text fields for plot properties.
	int fieldLength = 30, numFieldLength = 10;
	JTextField newTitle,newXAxis,newYAxis;
	JCheckBox  useXAxis,useYAxis,isSemilog;
	DecimalField newXAxisMin,newXAxisMax,newYAxisMax,newYAxisMin;
	JTextField newDataSetNames[];
	
	LogletLabPlotInfoPanel(LogletLabDocument doc) {
		initComponents(doc);
	}
	
	public void initComponents(LogletLabDocument doc) {
		newTitle = new JTextField(doc.getTitle(), fieldLength);
		newXAxis = new JTextField(doc.getDomainLabel(), fieldLength);
		newYAxis = new JTextField(doc.getRangeLabel(), fieldLength);
		useXAxis = new JCheckBox("",!doc.isAutoXAxisOn());
		useYAxis = new JCheckBox("",!doc.isAutoYAxisOn());
		isSemilog = new JCheckBox("",doc.getAxisType()==PlotRenderer.AXIS_SEMILOG);
		NumberFormat numberformat = NumberFormat.getNumberInstance();
		newXAxisMin = new DecimalField(doc.getTMin(), numFieldLength, numberformat);
		newXAxisMax = new DecimalField(doc.getTMax(), numFieldLength, numberformat);
		newYAxisMax = new DecimalField(doc.getYMax(), numFieldLength, numberformat);
		newYAxisMin = new DecimalField(doc.getYMin(), numFieldLength, numberformat);
		newDataSetNames = new JTextField[doc.getNSets()];
		for (int i=0; i<doc.getNSets(); i++) {
			newDataSetNames[i] = new JTextField(doc.getDataSet(i).getDataSetName());
		}
		
		// Lay out  the dialog
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		JPanel titlePanel, xAxisPanel, yAxisPanel, tabPanel;
		titlePanel=new JPanel();
		titlePanel.setBorder(BorderFactory.createTitledBorder(titlePanel.getBorder(),"Title"));
		titlePanel.setLayout(new BoxLayout(titlePanel,BoxLayout.Y_AXIS));
		titlePanel.add(newTitle);
		this.add(titlePanel);
		
		xAxisPanel=new JPanel();
		xAxisPanel.setBorder(BorderFactory.createTitledBorder(xAxisPanel.getBorder(),"X Axis"));
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		xAxisPanel.setLayout(gridbag);
		// axis title
		constraints.gridy = 0;
		constraints.gridx = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.weightx = 1.0;
		gridbag.setConstraints(newXAxis, constraints);
		xAxisPanel.add(newXAxis);
		// Row 2: min/max
		constraints.gridy++;
		constraints.gridx = GridBagConstraints.RELATIVE;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		gridbag.setConstraints(useXAxis, constraints);
		xAxisPanel.add(useXAxis);
		useXAxis.addItemListener(this);
		// axis min
		JLabel minLabel = new JLabel("min ");
		minLabel.setLabelFor(newXAxisMin);
		gridbag.setConstraints(minLabel, constraints);
		xAxisPanel.add(minLabel);
		newXAxisMin.setEnabled(!doc.isAutoXAxisOn());
		gridbag.setConstraints(newXAxisMin, constraints);
		xAxisPanel.add(newXAxisMin);
		//axis max
		constraints.weightx = 1.0;
		JLabel maxLabel = new JLabel("max ",JLabel.TRAILING);
		maxLabel.setLabelFor(newYAxisMin);
		gridbag.setConstraints(maxLabel, constraints);
		xAxisPanel.add(maxLabel);
		newXAxisMax.setEnabled(!doc.isAutoXAxisOn());
		constraints.weightx = 0.0;
		gridbag.setConstraints(newXAxisMax, constraints);
		xAxisPanel.add(newXAxisMax);
		// add to panel
		this.add(xAxisPanel);

		yAxisPanel=new JPanel();
		yAxisPanel.setBorder(BorderFactory.createTitledBorder(yAxisPanel.getBorder(),"Y Axis"));
		gridbag = new GridBagLayout();
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		yAxisPanel.setLayout(gridbag);
		// axis title
		constraints.gridy = 0;
		constraints.gridx = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.weightx = 1.0;
		gridbag.setConstraints(newYAxis, constraints);
		yAxisPanel.add(newYAxis);
		// Row 2: min/max
		constraints.gridy++;
		constraints.gridx = GridBagConstraints.RELATIVE;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		gridbag.setConstraints(useYAxis, constraints);
		yAxisPanel.add(useYAxis);
		useYAxis.addItemListener(this);
		// axis min
		JLabel yminLabel = new JLabel("min ");
		yminLabel.setLabelFor(newYAxisMin);
		gridbag.setConstraints(yminLabel, constraints);
		yAxisPanel.add(yminLabel);
		newYAxisMin.setEnabled(!doc.isAutoYAxisOn());
		gridbag.setConstraints(newYAxisMin, constraints);
		yAxisPanel.add(newYAxisMin);
		//axis max
		constraints.weightx = 1.0;
		JLabel ymaxLabel = new JLabel("max ",JLabel.TRAILING);
		ymaxLabel.setLabelFor(newYAxisMin);
		gridbag.setConstraints(ymaxLabel, constraints);
		yAxisPanel.add(ymaxLabel);
		newYAxisMax.setEnabled(!doc.isAutoYAxisOn());
		constraints.weightx = 0.0;
		gridbag.setConstraints(newYAxisMax, constraints);
		yAxisPanel.add(newYAxisMax);
		// Row 3: semi-log
		constraints.gridy++;
		constraints.gridx = GridBagConstraints.RELATIVE;
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		gridbag.setConstraints(isSemilog, constraints);
		yAxisPanel.add(isSemilog);
		isSemilog.addItemListener(this);
		JLabel ySemilogLabel = new JLabel("Plot as semilog");
		ySemilogLabel.setLabelFor(newYAxisMin);
		constraints.weightx = 1.0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(ySemilogLabel, constraints);
		yAxisPanel.add(ySemilogLabel);

		// add this panel to the main panel
		this.add(yAxisPanel);

		tabPanel = new JPanel();
		tabPanel.setBorder(BorderFactory.createTitledBorder(tabPanel.getBorder(),"Data sets"));
		gridbag = new GridBagLayout();
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL; 
		tabPanel.setLayout(gridbag);
		for (int i=0; i<doc.getNSets(); i++) {
			constraints.gridy=i;
			constraints.gridx = 0;
			constraints.weightx = 0.0;
			JLabel label = new JLabel("Data set #"+(i+1)+" name "); 
			gridbag.setConstraints(label, constraints);
			tabPanel.add(label);
			constraints.gridx=1;
			constraints.weightx = 1;
			gridbag.setConstraints(newDataSetNames[i], constraints);
			tabPanel.add(newDataSetNames[i]);
		}
		this.add(tabPanel);
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		if (source == useXAxis) {
			newXAxisMin.setEnabled(useXAxis.isSelected());
			newXAxisMax.setEnabled(useXAxis.isSelected());
		} else if (source == useYAxis) {
			newYAxisMin.setEnabled(useYAxis.isSelected());
			newYAxisMax.setEnabled(useYAxis.isSelected());
		}
	}
	

}
