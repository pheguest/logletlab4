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


/**
 * @author J Yung
 *
 * Axis object for charting plots.
 */
public class Axis {
	private int axisLength;
	private double min, max;
	private java.awt.Rectangle labelBounds;

	/**
	 * @return
	 */
	public int getAxisLength() {
		return axisLength;
	}

	/**
	 * @return
	 */
	public java.awt.Rectangle getLabelBounds() {
		return labelBounds;
	}

	/**
	 * @return
	 */
	public double getMax() {
		return max;
	}

	/**
	 * @return
	 */
	public double getMin() {
		return min;
	}

	/**
	 * @param i
	 */
	public void setAxisLength(int i) {
		axisLength = i;
	}

	/**
	 * @param rectangle
	 */
	public void setLabelBounds(java.awt.Rectangle rectangle) {
		labelBounds = rectangle;
	}

	/**
	 * @param d
	 */
	public void setMax(double d) {
		max = d;
	}

	/**
	 * @param d
	 */
	public void setMin(double d) {
		min = d;
	}

}
