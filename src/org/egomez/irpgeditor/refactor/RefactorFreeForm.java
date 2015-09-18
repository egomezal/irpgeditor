package org.egomez.irpgeditor.refactor;

/*
 * Copyright:    Copyright (c) 2004
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 */

import java.util.*;

import org.egomez.irpgeditor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts the fixed format code to free form.
 * 
 * @author Derek Van Kooten.
 */
@SuppressWarnings("unchecked")
public class RefactorFreeForm extends Refactor {
	Logger logger = LoggerFactory.getLogger(RefactorFreeForm.class);
	
	@SuppressWarnings("rawtypes")
	ArrayList listIndicators = new ArrayList();
	StringBuffer bufferTab = new StringBuffer("        ");
	String currentIndicator = "";
	@SuppressWarnings("rawtypes")
	static HashSet setIgnore = new HashSet();

	static {
		setIgnore.add("klist");
		setIgnore.add("kfld");
		setIgnore.add("plist");
		setIgnore.add("parm");
		setIgnore.add("call");
		setIgnore.add("cabeq");
		setIgnore.add("cabne");
		setIgnore.add("goto");
		setIgnore.add("tag");
		setIgnore.add("lookup");
	}

	public void start() {
		currentIndicator = "";
		bufferTab.replace(0, 2, "  ");
	}

	/**
	 * convert the line if it needs to be.
	 */
	public void process(SourceLine line) {
		String op, factor1, factor2, result, text, ind, before;
		char spec, c;

		if (line.getLength() < 7) {
			return;
		}
		c = line.charAt(6);
		if (c == '+' || c == '/') {
			return;
		}
		if (c == '*') {
			// change to free form type comment.
			line.setText(bufferTab + "// " + line.getText().substring(7).trim() + "\n");
			return;
		}
		spec = line.getSpec();
		if (spec != 'c' && spec != 'C') {
			return;
		}
		if (line.getLength() < 28) {
			return;
		}
		op = line.get(LinePosition.C_OPERATION);
		if (op == null) {
			return;
		}
		op = op.toLowerCase();
		if (setIgnore.contains(op)) {
			return;
		}
		before = processIndicators(line, op);
		if (op.equalsIgnoreCase("IF") || op.equalsIgnoreCase("DOU") || op.equalsIgnoreCase("DOW")) {
			line.setText(before + bufferTab + op + " " + line.get(LinePosition.C_FACTOR_2_EXTENDED) + ";\n");
			bufferTab.append("  ");
			incrementIndicators();
		} else if (op.equalsIgnoreCase("WHEN") || op.equalsIgnoreCase("OTHER")) {
			backTab(bufferTab);
			line.setText(before + bufferTab + op + " " + line.get(LinePosition.C_FACTOR_2_EXTENDED) + ";\n");
			bufferTab.append("  ");
		} else if (op.equalsIgnoreCase("SELECT")) {
			line.setText(before + bufferTab + op + ";\n");
			bufferTab.append("    ");
			incrementIndicators();
		} else if (op.equalsIgnoreCase("BEGSR")) {
			line.setText(before + bufferTab + op + " " + line.get(LinePosition.C_FACTOR_1) + ";\n");
			bufferTab.append("  ");
		} else if (op.equalsIgnoreCase("ELSE")) {
			backTab(bufferTab);
			line.setText(before + bufferTab + op + ";\n");
			bufferTab.append("  ");
		} else if (op.equalsIgnoreCase("END") || op.equalsIgnoreCase("ENDIF") || op.equalsIgnoreCase("ENDDO")
				|| op.equalsIgnoreCase("ENDSR")) {
			backTab(bufferTab);
			if (decrementIndicators()) {
				text = before + bufferTab + op + ";\n";
				backTab(bufferTab);
				text = text + bufferTab + "endif;\n";
				line.setText(text);
			} else {
				line.setText(before + bufferTab + op + ";\n");
			}
		} else if (op.equalsIgnoreCase("ENDSL")) {
			backTab(bufferTab);
			backTab(bufferTab);
			if (decrementIndicators()) {
				text = before + bufferTab + op + ";\n";
				backTab(bufferTab);
				text = text + bufferTab + "endif;\n";
				line.setText(text);
			} else {
				line.setText(before + bufferTab + op + ";\n");
			}
		} else if (op.equalsIgnoreCase("CLEAR")) {
			line.setText(before + bufferTab + op + " "
					+ (line.get(LinePosition.C_FACTOR_1) + " " + line.get(LinePosition.C_FACTOR_2)).trim()
					+ line.get(LinePosition.C_RESULT) + ";\n");
		} else if (op.equalsIgnoreCase("MOVE") || op.equalsIgnoreCase("Z-ADD") || op.equalsIgnoreCase("MOVEL")) {
			line.setText(before + bufferTab + line.get(LinePosition.C_RESULT) + " = "
					+ line.get(LinePosition.C_FACTOR_2) + ";\n");
		} else if (op.equalsIgnoreCase("CHAIN") || op.equalsIgnoreCase("SETGT") || op.equalsIgnoreCase("CHAIN(N)")) {
			text = convertIO(line, op);
			ind = line.getText(70, 2).trim();
			if (ind.length() > 0) {
				text = text + bufferTab + "*in" + ind + " = not %found;\n";
			}
			line.setText(before + text);
		} else if (op.equalsIgnoreCase("READE") || op.equalsIgnoreCase("READE(N)") || op.equalsIgnoreCase("READ")
				|| op.equalsIgnoreCase("READ(N)") || op.equalsIgnoreCase("READP") || op.equalsIgnoreCase("READP(N)")
				|| op.equalsIgnoreCase("READC") || op.equalsIgnoreCase("WRITE")) {
			text = convertIO(line, op);
			ind = line.getText(74, 2).trim();
			if (ind.length() > 0) {
				text = text + bufferTab + "*in" + ind + " = %eof;\n";
			}
			line.setText(before + text);
		} else if (op.equalsIgnoreCase("SETLL")) {
			text = convertIO(line, op);
			ind = line.getText(70, 2).trim();
			if (ind.length() > 0) {
				text = text + bufferTab + "*in" + ind + " = not %found;\n";
			}
			ind = line.getText(74, 2).trim();
			if (ind.length() > 0) {
				text = text + bufferTab + "*in" + ind + " = %equal;\n";
			}
			line.setText(before + text);
		} else if (op.equalsIgnoreCase("DO")) {
			factor2 = line.get(LinePosition.C_FACTOR_2);
			if (factor2 == null || factor2.trim().length() == 0) {
				factor2 = "1";
			}
			factor1 = line.get(LinePosition.C_FACTOR_1);
			if (factor1 == null || factor1.trim().length() == 0) {
				factor1 = "1";
			}
			result = line.get(LinePosition.C_RESULT);
			if (result == null || result.trim().length() == 0) {
				result = factor1;
			}
			try {
				if (Integer.parseInt(factor1) > Integer.parseInt(factor2)) {
					line.setText(before + bufferTab + "for " + result + " = " + factor1 + " downto " + factor2 + ";\n");
				} else {
					line.setText(before + bufferTab + "for " + result + " = " + factor1 + " to " + factor2 + ";\n");
				}
			} catch (NumberFormatException e) {
				line.setText(before + bufferTab + "for " + result + " = " + factor1 + " to " + factor2 + ";\n");
			} catch (Exception e2) {
				logger.error(e2.getMessage());
				//e2.printStackTrace();
			}
			bufferTab.append("  ");
		} else if (op.equalsIgnoreCase("EVAL")) {
			line.setText(before + bufferTab + line.get(LinePosition.C_FACTOR_2_EXTENDED) + ";\n");
		} else if (op.equalsIgnoreCase("mult")) {
			math(line, "*", before);
		} else if (op.equalsIgnoreCase("div")) {
			math(line, "/", before);
		} else if (op.equalsIgnoreCase("add")) {
			math(line, "+", before);
		} else if (op.equalsIgnoreCase("subt") || op.equalsIgnoreCase("sub")) {
			math(line, "-", before);
		} else if (op.equalsIgnoreCase("seton")) {
			text = line.getText(70, 2);
			if (text.trim().length() > 0) {
				StringBuffer buffer = new StringBuffer(bufferTab.toString());
				buffer.append("*in");
				buffer.append(text);
				buffer.append(" = *on;\n");
				text = line.getText(72, 2);
				if (text.trim().length() > 0) {
					buffer.append(bufferTab.toString());
					buffer.append("*in");
					buffer.append(text);
					buffer.append(" = *on;\n");
					text = line.getText(74, 2);
					if (text.trim().length() > 0) {
						buffer.append(bufferTab.toString());
						buffer.append("*in");
						buffer.append(text);
						buffer.append(" = *on;\n");
					}
				}
				line.setText(before + buffer.toString());
			}
		} else if (op.equalsIgnoreCase("setoff")) {
			text = line.getText(70, 2);
			if (text.trim().length() > 0) {
				StringBuffer buffer = new StringBuffer(bufferTab.toString());
				buffer.append("*in");
				buffer.append(text);
				buffer.append(" = *off;\n");
				text = line.getText(72, 2);
				if (text.trim().length() > 0) {
					buffer.append(bufferTab.toString());
					buffer.append("*in");
					buffer.append(text);
					buffer.append(" = *off;\n");
					text = line.getText(74, 2);
					if (text.trim().length() > 0) {
						buffer.append(bufferTab.toString());
						buffer.append("*in");
						buffer.append(text);
						buffer.append(" = *off;\n");
					}
				}
				line.setText(before + buffer.toString());
			}
		} else {
			// if there is no op, then this line is proably a continuation of
			// the previous if/dou/dow line.
			if (op.trim().length() == 0) {
				factor2 = line.get(LinePosition.C_FACTOR_2_EXTENDED);
			} else {
				factor2 = line.get(LinePosition.C_FACTOR_2);
			}
			if (factor2 == null) {
				line.setText(bufferTab + op + ";\n");
			} else {
				factor2 = factor2.trim();
				if (factor2.length() == 0) {
					line.setText(before + bufferTab + op + ";\n");
				} else {
					line.setText(before + bufferTab + op + " " + factor2 + ";\n");
				}
			}
		}
	}

	private String processIndicators(SourceLine line, String op) {
		String ind, before;

		// any indicators on line?
		ind = line.get(LinePosition.C_INDICATORS).trim();
		if (ind.equalsIgnoreCase(currentIndicator)) {
			return "";
		}
		if (ind.length() == 0) {
			// put in an endif before this line.
			backTab(bufferTab);
			currentIndicator = "";
			return bufferTab + "endif;\n";
		}
		if (currentIndicator.length() > 0) {
			// put in an endif before this different indicator.
			backTab(bufferTab);
			before = bufferTab + "endif;\n";
		} else {
			before = "";
		}
		if (ind.startsWith("N") || ind.startsWith("n")) {
			before = before + bufferTab + "if *in" + ind.substring(1) + " = *off;\n";
		} else {
			before = before + bufferTab + "if *in" + ind + " = *on;\n";
		}
		bufferTab.append("  ");
		// if this indicator is placed onto a start of a block of code, then
		// then endif
		// must come at the end of the block of code.
		if (op.startsWith("if") || op.startsWith("do") || op.startsWith("select")) {
			// this indicator must be ended at the end of the block.
			listIndicators.add(new Integer(0));
		} else {
			currentIndicator = ind;
		}
		return before;
	}

	private void incrementIndicators() {
		int i;

		for (int x = 0; x < listIndicators.size(); x++) {
			i = ((Integer) listIndicators.get(x)).intValue();
			i++;
			listIndicators.set(x, new Integer(i));
		}
	}

	private boolean decrementIndicators() {
		int i;
		boolean after = false;

		for (int x = 0; x < listIndicators.size(); x++) {
			i = ((Integer) listIndicators.get(x)).intValue();
			i--;
			if (i == 0) {
				listIndicators.remove(x);
				after = true;
				x--;
			} else {
				listIndicators.set(x, new Integer(i));
			}
		}
		return after;
	}

	private String convertIO(SourceLine line, String op) {
		String result, text, factor1, factor2;

		factor1 = line.get(LinePosition.C_FACTOR_1).trim();
		factor2 = line.get(LinePosition.C_FACTOR_2).trim();
		result = line.get(LinePosition.C_RESULT).trim();
		text = bufferTab + op;
		if (factor1.length() > 0) {
			text = text + " " + line.get(LinePosition.C_FACTOR_1);
		}
		if (factor2.length() > 0) {
			text = text + " " + line.get(LinePosition.C_FACTOR_2);
		}
		if (result.length() > 0) {
			text = text + " " + result;
		}
		text = text + ";\n";
		return text;
	}

	private void backTab(StringBuffer bufferTab) {
		if (bufferTab.length() < 2) {
			return;
		}
		bufferTab.delete(bufferTab.length() - 2, bufferTab.length());
	}

	private void math(SourceLine line, String op, String before) {
		String factor1, factor2, result;

		factor1 = line.get(LinePosition.C_FACTOR_1);
		factor2 = line.get(LinePosition.C_FACTOR_2);
		result = line.get(LinePosition.C_RESULT);
		if (factor1.trim().length() == 0) {
			factor1 = result;
		}
		line.setText(before + bufferTab + result + " = " + factor1 + " " + op + " " + factor2 + ";\n");
	}
}
