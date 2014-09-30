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
 * LogletLabFrame.java
 *
 * Title:			LogletLab
 * Description:		logistic analysis tool and more.
 * @author			J
 * @version			
 */

package org.logletlab.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.*;
import java.awt.datatransfer.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

import org.logletlab.LogletLabDataSet;
import org.logletlab.LogletLabDocument;
import org.logletlab.model.AbstractFitModel;
import org.logletlab.model.LogSubModel;
import org.logletlab.model.LogisticFitModel;
import org.logletlab.util.*;


import java.util.Properties;

import jxl.read.biff.BiffException;
//import java.util.*;

public class LogletLabFrame
	extends javax.swing.JFrame
	implements TableModelListener, DocumentListener, ChangeListener, ActionListener {

	// IMPORTANT: Source code between BEGIN/END comment pair will be regenerated
	// every time the form is saved. All manual changes will be overwritten.
	// BEGIN GENERATED CODE
	// member declarations
	javax.swing.JSplitPane jSplitPane1 = new javax.swing.JSplitPane();
	javax.swing.JTabbedPane dataTabs = new javax.swing.JTabbedPane();
	javax.swing.JPanel displayPanel = new javax.swing.JPanel();
	org.logletlab.ui.LogletLabCanvas logletLabCanvas = new org.logletlab.ui.LogletLabCanvas();
	
	javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
	javax.swing.JMenu jMenuFile = new javax.swing.JMenu();
	javax.swing.JMenuItem jMenuFileNewDocument = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuFileOpenDocument = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuFileSave = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuFileExport = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuFilePrint = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuFileExit = new javax.swing.JMenuItem();
	
	javax.swing.JMenu jMenuEdit = new javax.swing.JMenu();
	javax.swing.JMenuItem jMenuEditCopy = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuEditPaste = new javax.swing.JMenuItem();
	
	javax.swing.JMenuItem jMenuEditAddDataSet = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuEditPlotInfo = new javax.swing.JMenuItem();
	
	javax.swing.JMenu jMenuFit = new javax.swing.JMenu();
	javax.swing.JMenuItem jMenuFitFitLogistic = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuFitFitBiLogistic = new javax.swing.JMenuItem();
	javax.swing.JMenuItem jMenuFitFitLogSub = new javax.swing.JMenuItem();

	javax.swing.JMenu jMenuHelp = new javax.swing.JMenu();
	javax.swing.JMenuItem jMenuHelpAbout = new javax.swing.JMenuItem();

	// END GENERATED CODE
	Properties appProperties;

	// The document: datasets and their fit parameters
	private LogletLabDocument doc = new LogletLabDocument();
	LogletLabTableModel tableModel;

	AbstractFitModel currentFit = null;

	public LogletLabFrame(Properties p) {
		appProperties = p;

	}

	public void initComponents() throws Exception {
		// IMPORTANT: Source code between BEGIN/END comment pair will be regenerated
		// every time the form is saved. All manual changes will be overwritten.
		// BEGIN GENERATED CODE
		// the following code sets the frame's initial state
		jSplitPane1.setDividerLocation(170);
		jSplitPane1.setOneTouchExpandable(true);
		jSplitPane1.setResizeWeight(0.2);
		jSplitPane1.setVisible(true);
		dataTabs.setVisible(true);
		
		displayPanel.setLayout(new java.awt.BorderLayout(0, 0));
		displayPanel.setVisible(true);
		logletLabCanvas.setBackground(java.awt.Color.white);
		logletLabCanvas.setVisible(true);

		menuBar.setVisible(true);
		jMenuFile.setText("File");
		jMenuFile.setVisible(true);
		jMenuFileNewDocument.setText("New...");
		jMenuFileNewDocument.setVisible(true);
		jMenuFileOpenDocument.setText("Open...");
		jMenuFileOpenDocument.setVisible(true);
		jMenuFileOpenDocument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
		jMenuFileSave.setText("Save...");
		jMenuFileSave.setVisible(true);
		jMenuFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
		jMenuFileExport.setText("Export Graph...");
		jMenuFileExport.setVisible(true);
		jMenuFilePrint.setText("Print Graph...");
		jMenuFilePrint.setVisible(true);
		jMenuFileExit.setText("Exit");
		jMenuFileExit.setVisible(true);
		
		jMenuEdit.setText("Edit");
		jMenuEdit.setVisible(true);
		jMenuEditCopy.setText("Copy");
		jMenuEditCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
		jMenuEditCopy.setVisible(true);
		jMenuEditPaste.setText("Paste");
		jMenuEditPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));
		jMenuEditPaste.setVisible(true);
		jMenuEditPlotInfo.setText("Plot Info...");
		jMenuEditPlotInfo.setVisible(true);
		jMenuEditAddDataSet.setText("Add Data Set...");
		jMenuEditAddDataSet.setVisible(true);
		
		jMenuFit.setText("Fit");
		jMenuFit.setVisible(true);
		jMenuFitFitLogistic.setText("Fit Logistic");
		jMenuFitFitLogistic.setVisible(true);
		jMenuFitFitBiLogistic.setText("Fit Bi-Logistic");
		jMenuFitFitBiLogistic.setVisible(true);
		jMenuFitFitLogSub.setText("Logistic Substitution");

		jMenuHelp.setText("Help");
		jMenuHelpAbout.setText("About Loglet Lab");
		
		setJMenuBar(menuBar);
		getContentPane().setLayout(new java.awt.BorderLayout(0, 0));
		//setLocation(new java.awt.Point(0, 0));
		setTitle("LogletLab");

		dataTabs.setMinimumSize(new Dimension(140, 0));
		dataTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		displayPanel.setMinimumSize(new Dimension(340, 0));
		displayPanel.add(logletLabCanvas,BorderLayout.CENTER);

		jSplitPane1.add(dataTabs, javax.swing.JSplitPane.LEFT);
		jSplitPane1.add(displayPanel, javax.swing.JSplitPane.RIGHT);
		getContentPane().add(jSplitPane1, null);


		menuBar.add(jMenuFile);
		menuBar.add(jMenuEdit);
		menuBar.add(jMenuFit);
		menuBar.add(jMenuHelp);
		jMenuFile.add(jMenuFileNewDocument);
		jMenuFile.add(jMenuFileOpenDocument);
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileSave);
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileExport);
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFilePrint);
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileExit);
		
		jMenuEdit.add(jMenuEditCopy);
		jMenuEdit.add(jMenuEditPaste);
		jMenuEdit.add(new JSeparator());
		jMenuEdit.add(jMenuEditPlotInfo);
		jMenuEdit.add(new JSeparator());
		jMenuEdit.add(jMenuEditAddDataSet);
		
		jMenuFit.add(jMenuFitFitLogistic);
		jMenuFit.add(jMenuFitFitBiLogistic);
		jMenuFit.add(new JSeparator());
		jMenuFit.add(jMenuFitFitLogSub);
		
		jMenuHelp.add(jMenuHelpAbout);

		setSize(new java.awt.Dimension(640, 480));

		// event handling
		jMenuFileNewDocument.addActionListener(this);
		jMenuFileOpenDocument.addActionListener(this);
		jMenuFileSave.addActionListener(this);
		jMenuFileExport.addActionListener(this);
		jMenuFilePrint.addActionListener(this);
		jMenuFileExit.addActionListener(this);

		jMenuEditCopy.addActionListener(this);
		jMenuEditPaste.addActionListener(this);
		jMenuEditAddDataSet.addActionListener(this);
		jMenuEditPlotInfo.addActionListener(this);
		
		jMenuFitFitLogistic.addActionListener(this);
		jMenuFitFitBiLogistic.addActionListener(this);
		jMenuFitFitLogSub.addActionListener(this);
		
		jMenuHelpAbout.addActionListener(this);

		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				thisWindowClosing(e);
			}
		});

		// END GENERATED CODE
		// add back pointer to me.
		logletLabCanvas.setParent(this);

		ImageIcon icon;
		icon = new ImageIcon("ll_icon16.png", "");
		this.setIconImage(icon.getImage());
	}

	private boolean mShown = false;

	// EVENT HANDLERS
	public void tableChanged(javax.swing.event.TableModelEvent e) {
		/*
		int row = e.getFirstRow();
		int col = e.getColumn();
		//String columnName = model.getColumnName(column);
		//Object data = model.getValueAt(row, column);
		*/
		refreshGraph();
	}

	protected void refreshGraph() {
		//logletLabCanvas.setDims(logletLabCanvas.getSize());
		logletLabCanvas.repaint();
	}

	public void addNotify() {
		super.addNotify();

		if (mShown)
			return;

		// resize frame to account for menubar
		JMenuBar jMenuBar = getJMenuBar();
		if (jMenuBar != null) {
			int jMenuBarHeight = jMenuBar.getPreferredSize().height;
			Dimension dimension = getSize();
			dimension.height += jMenuBarHeight;
			setSize(dimension);

			// move down components in layered pane
			Component[] components =
				getLayeredPane().getComponentsInLayer(
					JLayeredPane.DEFAULT_LAYER.intValue());
			for (int i = 0; i < components.length; i++) {
				Point location = components[i].getLocation();
				location.move(location.x, location.y + jMenuBarHeight);
				components[i].setLocation(location);
			}
		}

		mShown = true;
	}

	// Close the window when the close box is clicked
	void thisWindowClosing(java.awt.event.WindowEvent e) {
		this.exit();
	}
	public void menuExit() {
		this.exit();
	}

	public void clearDocument() {
		// RESET COMPONENTS
		// Reset fit panels (get rid of them)
		for (int i=0; i<displayPanel.getComponentCount(); i++) {
			// System.err.println(i+":"+displayPanel.getComponent(i).getClass().getName());
			if (displayPanel.getComponent(i) instanceof AbstractFitModelPanel) {
				displayPanel.remove(i);
			}
		}
		displayPanel.validate();
		// Reset tabs.
		dataTabs.removeAll();
		currentFit = null;
		setDoc(new LogletLabDocument());
	}

	public void newDocument(boolean firstTime) {
		if (saveBeforeCloseDocument() != JOptionPane.CANCEL_OPTION) {
			clearDocument();
			getDoc().addDataSet(new LogletLabDataSet());
			initDocument();
			editPlotInfo(true, firstTime);
		}
	}

	public void openDocument() {
		if (saveBeforeCloseDocument() == JOptionPane.CANCEL_OPTION)
			return;

		// open file
		String lastOpenedFileDir =
			appProperties.getProperty("LogletLab.lastOpenedFileDir");
		JFileChooser jfc = new JFileChooser(lastOpenedFileDir);
		jfc.addChoosableFileFilter(new LogletLabOpenFileFilter());
		jfc.setFileFilter(new XMLFileFilter());
		int choice = jfc.showOpenDialog(this);
		if (choice != JFileChooser.CANCEL_OPTION) {
			clearDocument();

			try {
				if (jfc.getSelectedFile() != null) {
					getDoc().openDocument(jfc.getSelectedFile());
					initDocument();
				/*
				dataTabs.repaint();
				// really nutty low-level call to make sure table redraws itself!
				dataTable.update(dataTable.getGraphics());
				dataTable.repaint();
				this.repaint();
				*/

					saveLastOpenedFile(jfc.getSelectedFile());
				}
			} catch (BiffException e1) {
				JOptionPane.showMessageDialog(this,
				  "Something went wrong when opening the Excel file.",
				  "File error",
				  JOptionPane.WARNING_MESSAGE);
			} catch (IOException e) {
				// Shouldn't happen; we just selected the file.
				JOptionPane.showMessageDialog(this,
					"Couldn't open: "+ jfc.getSelectedFile().getAbsolutePath()+"\nBad format or file not found.",
					"File error",
					JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public void initDocument() {
		// for each dataset, add a Tab
		for (int i=0; i<getDoc().getNSets(); i++) {
			JTable table = new JTable();
			table.setCellSelectionEnabled(true);
			//table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			// Set table model for the table.
			// This tells the table how to render the data.
			table.setModel(new LogletLabTableModel(getDoc().getDataSet(i)));
			table.getModel().addTableModelListener(this);
		
			JScrollPane scrollpane = new JScrollPane(table);
			dataTabs.addTab(getDoc().getDataSet(i).getDataSetName(),scrollpane);
		}
		dataTabs.setSelectedIndex(0);
		
		// by default, set the current fit to the last fit.
		if (getDoc().getNFits() > 0) {
			currentFit = getDoc().getFit(getDoc().getNFits() - 1);
			// Add parameter panels
			if (currentFit != null) {
				if (currentFit instanceof LogisticFitModel) {
					LogisticFitModel lfm = (LogisticFitModel) currentFit;
					createLogisticFitPanel(lfm.nLogistics);
				} else if (currentFit instanceof LogSubModel) {
					LogSubModel lsm = (LogSubModel) currentFit;
					createLogSubPanel();
				} 
			}
		}
		// Enable/disable options based on doc properties
		jMenuFitFitLogSub.setEnabled(getDoc().getNSets() > 1);

		// refresh the components
		displayPanel.invalidate();
		dataTabs.revalidate();
		refreshGraph();
	}

	public void saveDocument() {
		String lastOpenedFileDir =
			appProperties.getProperty("LogletLab.lastOpenedFileDir");
		JFileChooser jfc = new JFileChooser(lastOpenedFileDir);
		jfc.setFileFilter(new XMLFileFilter());
		if (getDoc().getFile() != null) {
			jfc.setSelectedFile(getDoc().getFile());
		}
		int choice = jfc.showSaveDialog(this);
		if (choice != JFileChooser.CANCEL_OPTION) {
			if (!jfc.getSelectedFile().getName().endsWith(".xml")) {
				// tack on an ".xml"
				jfc.setSelectedFile(new File(jfc.getSelectedFile().getAbsolutePath().concat(".xml")));
			}
			getDoc().saveDocument(jfc.getSelectedFile());
			saveLastOpenedFile(jfc.getSelectedFile());
		}
		
	}

	private void saveLastOpenedFile(File f) {
		// UPDATE PREFS
		String propertiesFileName = "LogletLab.Properties";
		if (System.getProperty("os.name").equals("Mac OS X")) {
			propertiesFileName =
				System.getProperty("user.home")
					+ "Library/Preferences/"
					+ propertiesFileName;
		}
		appProperties.setProperty("LogletLab.lastOpenedFile", f.toString());
		appProperties.setProperty("LogletLab.lastOpenedFileDir", f.getParentFile().toString());
		try {
			java.io.FileOutputStream out =
				new java.io.FileOutputStream(propertiesFileName);
			appProperties.store(out, "---Loglet Lab 0.5---");
			out.close();
		} catch (FileNotFoundException e) {
			// Can't be written? Whatever.
		} catch (IOException e) {
			// Can't be written? Whatever.
		}
	}

	private int saveBeforeCloseDocument() {
		int confirm = JOptionPane.NO_OPTION;
		if (getDoc().getModified()) {
			confirm =
				JOptionPane.showConfirmDialog(
					this,
					"Save changes first?",
					"Confirm close: document modified",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (confirm == JOptionPane.YES_OPTION) {
				saveDocument();
			}
		}
		// System.err.println(confirm+"/"+JOptionPane.CANCEL_OPTION);
		return confirm;
	}

	public void exportDocument() {
		String lastOpenedFileDir =
			appProperties.getProperty("LogletLab.lastOpenedFileDir");
		JFileChooser jfc = new JFileChooser(lastOpenedFileDir);
		jfc.setFileFilter(new PNGFileFilter());
		int choice = jfc.showSaveDialog(this);
		if (choice == JOptionPane.YES_OPTION) {
			// GET A SNAPSHOT OF THE GRAPH
			long secs = System.currentTimeMillis();
			BufferedImage export = new BufferedImage(800,600,BufferedImage.TYPE_INT_RGB);
			logletLabCanvas.createExportImage(export);
			//System.err.println("Created image in "+((double)(System.currentTimeMillis()-secs)/1000)+" s");
			secs = System.currentTimeMillis();

			// CONVERT export TO PNG
			//byte[] pngbytes;
			//PngEncoder png = new PngEncoder(export, PngEncoder.NO_ALPHA, PngEncoder.FILTER_NONE, 9);
			//System.err.println("Created Encoder in "+((double)(System.currentTimeMillis()-secs)/1000)+" s");
			
			try {
				ImageIO.write(export, "png", jfc.getSelectedFile());

				/*
				secs = System.currentTimeMillis();
				pngbytes = png.pngEncode();
				System.err.println("Encoded in "+((double)(System.currentTimeMillis()-secs)/1000)+" s");
				
				if (pngbytes == null) {
					JOptionPane.showMessageDialog(this,
					  "Sorry, the PNG encoder didn't work.",
					  "PNG Encoder problem",
					  JOptionPane.WARNING_MESSAGE);
				} else {
					FileOutputStream outfile =
						new FileOutputStream(
							jfc.getSelectedFile().getAbsolutePath());
					outfile.write(pngbytes);
					outfile.flush();
					outfile.close();
				}
				*/
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this,
				  "Sorry, I can't save the image.",
				  "Write error",
				  JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public void printDocument() {
		PrinterJob printJob = PrinterJob.getPrinterJob();
		PageFormat pf = printJob.defaultPage();
		pf = printJob.pageDialog(pf);
		printJob.setPrintable(logletLabCanvas,pf);
		if (printJob.printDialog()) {
			try {
				printJob.print();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void copyData() {
		JScrollPane sp = (JScrollPane)dataTabs.getSelectedComponent();
		JTable table = (JTable)sp.getViewport().getComponent(0);
		String copytext ="";
		if (table.getSelectedRowCount() > 0 && table.getSelectedColumnCount() > 0) {
			for (int i=0; i<table.getSelectedRowCount(); i++) {
				for (int j=0; j<table.getSelectedColumnCount(); j++) {
					copytext += table.getValueAt(table.getSelectedRow()+i, table.getSelectedColumn()+j);
					copytext += ",";
				}
				copytext += "\n";
			}
		}
		Clipboard clipboard = this.getToolkit().getSystemClipboard();
		StringSelection copytextSel = new StringSelection(copytext);
		clipboard.setContents(copytextSel, copytextSel);
	}
	public void pasteData() {
		Clipboard clipboard = this.getToolkit().getSystemClipboard();
		try {
			Reader reader = DataFlavor.stringFlavor.getReaderForText(clipboard.getContents(this));
			BufferedReader in = new BufferedReader(reader);
			getDoc().parseText(in);
			in.close();
			getDoc().setModified(true);

			dataTabs.getSelectedComponent().repaint();
			((JTable)((JScrollPane)dataTabs.getSelectedComponent()).getViewport().getComponent(0)).revalidate();
			dataTabs.repaint();

			refreshGraph();
			
			repaint();
		} catch (UnsupportedFlavorException e) {
			JOptionPane.showMessageDialog(this,
			  "Sorry, I can't paste whatever is in the clipboard.",
			  "Bad format",
			  JOptionPane.WARNING_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
			  "Something went wrong when opening the Excel file.",
			  "File error",
			  JOptionPane.WARNING_MESSAGE);
		}
	}

	public void addDataSet() {
		int fieldLength = 30;
		JTextField newTitle = new JTextField("Data Set #"+(getDoc().getNSets()+1), fieldLength);
		// create array: labels + text fields
		Object[] components = { "Data Set title", newTitle };
		// create dialog, with  array
		final JDialog dialog = new JDialog(this, "Data set name", true);
		final JOptionPane optionPane =
			new JOptionPane(
				components,
				JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		
		optionPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
			public void propertyChange(java.beans.PropertyChangeEvent e) {
				String prop = e.getPropertyName();
		
				if (dialog.isVisible()
					&& (e.getSource() == optionPane)
					&& (prop.equals(JOptionPane.VALUE_PROPERTY))) {
					//If you were going to check something
					//before closing the window, you'd do
					//it here.
					dialog.setVisible(false);
				}
			}
		});
		
		// Make this dialog display the stuff.
		dialog.setContentPane(optionPane);
		dialog.pack();
		dialog.setVisible(true);
		
		// if "OK", set values.
		int value = ((Integer) optionPane.getValue()).intValue();
		if (value == JOptionPane.OK_OPTION) {
			getDoc().addDataSet( new LogletLabDataSet(newTitle.getText()) );
			getDoc().setCurrentDataSet(getDoc().getNSets()-1);
			JScrollPane myScroll = new JScrollPane();
			JTable myTable = new JTable();
			
			myTable.setModel(new LogletLabTableModel(getDoc().getCurrentDataSet()));
			myTable.getModel().addTableModelListener(this);
			myScroll.getViewport().add(myTable);
	
			dataTabs.addTab(newTitle.getText(),myScroll);
			dataTabs.setSelectedIndex(dataTabs.getTabCount()-1);
		}
		
		jMenuFitFitLogSub.setEnabled(getDoc().getNSets() > 1);
	}
	
	public void editPlotInfo(boolean isNew, boolean firstTime) {
		// create dialog, with  array
		final JDialog dialog =
			new JDialog(this,(isNew ? "New Document" : "Edit Plot Info"),true);
		final JOptionPane optionPane =
			new JOptionPane(
				null,
				JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);

		optionPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
			public void propertyChange(java.beans.PropertyChangeEvent e) {
				String prop = e.getPropertyName();

				if (dialog.isVisible()
					&& (e.getSource() == optionPane)
					&& (prop.equals(JOptionPane.VALUE_PROPERTY))) {
					//If you were going to check something
					//before closing the window, you'd do
					//it here.
					dialog.setVisible(false);
				}
			}
		});
		// The components
		LogletLabPlotInfoPanel subPanel = new LogletLabPlotInfoPanel(getDoc());
		// put components + option buttons into dialog, and show it
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add("Center", subPanel);
		panel.add("South",optionPane);
		if (firstTime) {
			String firstTimeMsg = "It appears that this is your first time running Loglet Lab, " +
				"because I can't find any previously open documents. " +
				"Please enter information for a new document.";
			JTextPane msgPane = new JTextPane();
			msgPane.setEditable(false);
			msgPane.setOpaque(false);
			msgPane.setPreferredSize(new Dimension(320,60));
			msgPane.setText(firstTimeMsg);
			panel.add("North",msgPane);
		}
		dialog.setContentPane(panel);
		dialog.pack();
		dialog.setVisible(true);

		// if "OK", set values.
		if (optionPane.getValue() != null && optionPane.getValue() != JOptionPane.UNINITIALIZED_VALUE) {
			int value = ((Integer) optionPane.getValue()).intValue();
			if (value == JOptionPane.OK_OPTION) {
				getDoc().setTitle(subPanel.newTitle.getText());
				getDoc().setDomainLabel(subPanel.newXAxis.getText());
				getDoc().setRangeLabel(subPanel.newYAxis.getText());
				getDoc().setAutoXAxisOn(!subPanel.useXAxis.isSelected());
				getDoc().setTMin(subPanel.newXAxisMin.getValue());
				getDoc().setTMax(subPanel.newXAxisMax.getValue());
				getDoc().setAutoYAxisOn(!subPanel.useYAxis.isSelected());
				getDoc().setYMin(subPanel.newYAxisMin.getValue());
				getDoc().setYMax(subPanel.newYAxisMax.getValue());
				getDoc().setAxisType(subPanel.isSemilog.isSelected()?PlotRenderer.AXIS_SEMILOG:PlotRenderer.AXIS_LINEAR);
				//change labels
				for (int i=0; i<getDoc().getNSets(); i++) {
					getDoc().getDataSet(i).setDataSetName(subPanel.newDataSetNames[i].getText());
					if (!isNew) {
						dataTabs.setTitleAt(i, getDoc().getDataSet(i).getDataSetName());
					}
				}
				refreshGraph();
			}
		}
	}

	public void exit() {
		if (saveBeforeCloseDocument() == JOptionPane.CANCEL_OPTION) {
			return;
		} else {
			setVisible(false);
			dispose();
			System.exit(0);
		}
	}

	public void addLogisticFitPanel() {
		/* TODO: Allow for multiple parameter sets of Logisitc Fits.
		* Add an entry to Fit Menu for each instance of a Fit Model (???)
		* --not sure if these would be too clumsy.
		*/
		createLogisticFitPanel(1);
	}

	public void addBiLogisticFitPanel() {
		createLogisticFitPanel(2);
	}

	public void createLogisticFitPanel(int n) {
		// Insert LogisticFitModel to the fit model list, if it isn't there.
		currentFit = null;
		for (int i = 0; i < getDoc().getNFits(); i++) {
			if (getDoc().getFit(i) instanceof LogisticFitModel) {
				if (((LogisticFitModel) getDoc().getFit(i)).nLogistics == n) {
					// System.err.println("Eureka!");
					currentFit = getDoc().getFit(i);
					break;
				}
			}
		}
		if (currentFit == null) {
			// System.err.println("Creating!");
			currentFit = getDoc().addFit(new LogisticFitModel(n));
		}

		// REMOVE EXISTING PARAMETER PANELS
		for (int i=0; i<displayPanel.getComponentCount(); i++) {
			// System.err.println(i+":"+displayPanel.getComponent(i).getClass().getName());
			if (displayPanel.getComponent(i) instanceof AbstractFitModelPanel) {
				displayPanel.remove(i);
			}
		}

		// CREATE A NEW PANEL
		LogisticFitModelPanel panel = new LogisticFitModelPanel(this, (LogisticFitModel) currentFit);


		// Set the mode flag to Fitted.
		currentFit.setMode(LogisticFitModel.MODE_NORMAL);

		// add the Log Fit panel to the display.
		displayPanel.add(panel, BorderLayout.NORTH);
		displayPanel.validate();
		refreshGraph();
		this.validate();
		//this.pack();
	}
	
	public void createLogSubPanel() {
		// Insert LogSubModel to the fit model list, if it isn't there.
		currentFit = null;
		for (int i = 0; i < getDoc().getNFits(); i++) {
			if (getDoc().getFit(i) instanceof LogSubModel) {
				currentFit = getDoc().getFit(i);
				break;
			}
		}
		if (currentFit == null) {
			// System.err.println("Creating!");
			currentFit = getDoc().addFit(new LogSubModel(getDoc()));
		}

		// REMOVE EXISTING PARAMETER PANELS
		for (int i=0; i<displayPanel.getComponentCount(); i++) {
			// System.err.println(i+":"+displayPanel.getComponent(i).getClass().getName());
			if (displayPanel.getComponent(i) instanceof AbstractFitModelPanel) {
				displayPanel.remove(i);
			}
		}

		// CREATE A NEW PANEL
		LogSubModelPanel lsmp = new LogSubModelPanel(this, (LogSubModel) currentFit);

		// Set the mode flag to Fitted.
		currentFit.setMode(AbstractFitModel.MODE_DEFAULT);

		// add the Log Fit panel to the display.
		displayPanel.add(lsmp, BorderLayout.NORTH);
		displayPanel.validate();
		refreshGraph();
		this.validate();
		//this.pack();
	}

	// Document changed event handlers
	public void changedUpdate(DocumentEvent e) {
		System.err.println("change");
	}
	public void insertUpdate(DocumentEvent e) {
		refreshGraph();
		getDoc().setModified(true);
	}
	public void removeUpdate(DocumentEvent e) {
		refreshGraph();
		getDoc().setModified(true);
	}

	public void stateChanged(ChangeEvent e) {
		refreshGraph();
		getDoc().setModified(true);
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		//System.err.println("actionPerformed(): "+e.getActionCommand());
		
		if (e.getSource() == jMenuFileNewDocument) {
			newDocument(false);
		} else if (e.getSource() == jMenuFileOpenDocument) {
			openDocument();
		} else if (e.getSource() == jMenuFileSave) {
			saveDocument();
		} else if (e.getSource() == jMenuFileExport) {
			exportDocument();
		} else if (e.getSource() == jMenuFilePrint) {
			printDocument();
		} else if (e.getSource() == jMenuFileExit) {
			menuExit();
//		} else if (e.getSource() == jMenuEdit) {
//			jMenuEditPaste.setEnabled(this.getToolkit().getSystemClipboard().getContents(this) != null);
		} else if (e.getSource() == jMenuEditCopy) {
			copyData();
		} else if (e.getSource() == jMenuEditPaste) {
			pasteData();
		} else if (e.getSource() == jMenuEditPlotInfo) {
			editPlotInfo(false, false);
		} else if (e.getSource() == jMenuEditAddDataSet) {
			addDataSet();
		} else if (e.getSource() == jMenuFitFitLogistic) {
			addLogisticFitPanel();
		} else if (e.getSource() == jMenuFitFitBiLogistic) {
			addBiLogisticFitPanel();
		} else if (e.getSource() == jMenuFitFitLogSub) {
			createLogSubPanel();
		} else if (e.getSource() == jMenuHelpAbout) {
			about();
		}
	}

	public void about() {
		String msg = "Loglet Lab 2.0\n\n" +
		"http://phe.rockefeller.edu/LogletLab/\n\n" +
		"Copyright (c) 2003, Program for the Human Environment," +
		"The Rockefeller University." +
		"Contains code (C) Copr. 1986-92 Numerical Recipes Software." +		"All rights reserved.\n\n" +		"See http://phe.rockefeller.edu/LogletLab/2.0/License.txt for details.";
		JScrollPane scrollpane = new JScrollPane();
		JTextPane msgPane = new JTextPane();
		msgPane.setEditable(false);
		msgPane.setOpaque(false);
		msgPane.setPreferredSize(new Dimension(320,120));
		msgPane.setText(msg);
		JOptionPane.showMessageDialog(this,new JScrollPane(msgPane),"About Loglet Lab",JOptionPane.PLAIN_MESSAGE);
	}

	public LogletLabDocument getDoc() {
		return doc;
	}

	public void setDoc(LogletLabDocument doc) {
		this.doc = doc;
	}
}
