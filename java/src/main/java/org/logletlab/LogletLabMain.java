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
 * LogletLabMain.java
 *
 * Title:			LogletLab
 * Description:		logistic analysis tool and more.
 * @author			J
 * @version			
 */

package org.logletlab;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.logletlab.ui.LogletLabFrame;
import org.logletlab.ui.LogletLabSplashPage;

public class LogletLabMain {
	public LogletLabMain() {
		try {
			/*
			// For native Look and Feel, uncomment the following code.
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} 
			catch (Exception e) { 
			}
			*/
			Properties applicationProps = new Properties();

			//			now load properties from last invocation
			try {
				String propertiesFileName = "LogletLab.Properties";
				//System.err.println(System.getProperty("os.name"));
				if (System.getProperty("os.name").equals("Mac OS X")) {
					propertiesFileName = System.getProperty("user.home")+"Library/Preferences/"+propertiesFileName;
				}
				FileInputStream in = new FileInputStream(propertiesFileName);
				applicationProps.load(in);
				in.close();
			} catch (FileNotFoundException e1) {
			}

			if (applicationProps.getProperty("LogletLab.showSplashPage")!="false") {
				// SHOW SPLASH PAGE
				LogletLabSplashPage splash = new LogletLabSplashPage();
				splash.setVisible(true);
				try {
					Thread.sleep(3000);
				} catch(InterruptedException ie){}

				splash.dispose();

			}

			System.setProperty("apple.awt.showGrowBox", "true");
			// CREATE THE FRAME
			LogletLabFrame frame = new LogletLabFrame(applicationProps);
			
			// INITIALIZE COMPONENTS
			frame.initComponents();
			
			// INITIALIZE FILES
			//doc.openDocument("nukes.xml");
			String lastOpenedFile =
				applicationProps.getProperty("LogletLab.lastOpenedFile");
			if (lastOpenedFile == null) {
				frame.newDocument(true);
			} else {
				try {
					frame.getDoc().openDocument(new java.io.File(lastOpenedFile));
					frame.initDocument();
				} catch (java.io.IOException e) {
					// File not found; open new document instead.
					frame.newDocument(true);
				}
			}

			frame.setVisible(true);
			System.setProperty("apple.awt.showGrowBox", "false");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Main entry point
	static public void main(String[] args) {
		new LogletLabMain();
	}

}
