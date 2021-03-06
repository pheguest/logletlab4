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
 * LogletLabCanvasBeanInfo.java
 *
 * Description:		BeanInfo for class LogletLabCanvas
 * @author			J
 * @version			
 */

package org.logletlab.ui;

/**
 * LogletLabCanvasBeanInfo just gives the LogletLabCanvas bean its icons.
 *
 * @see LogletLabCanvas
 */
public class LogletLabCanvasBeanInfo extends java.beans.SimpleBeanInfo {

 	/* Small icon is in LogletLabCanvas.gif
 	 * Large icon is in LogletLabCanvasL.gif
 	 * [It is expected that the contents of the icon files will be changed to suit your bean.]
	 */
	 
    public java.awt.Image getIcon(int iconKind) {
		java.awt.Image icon = null;
		switch (iconKind) {
		case ICON_COLOR_16x16:
    		icon = loadImage("LogletLabCanvas.gif");
			break;			
		case ICON_COLOR_32x32:
			icon = loadImage("LogletLabCanvasL.gif");
			break;
		default:
			break;
		}
		return icon;
	}
}

/* @(#)LogletLabCanvasBeanInfo.java */
