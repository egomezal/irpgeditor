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
import org.egomez.irpgeditor.*;

/**
 * Converts comparisons to not use factor 1.
 *
 * @author Derek Van Kooten.
 */
public class RefactorComparisons extends Refactor {


    @Override
    public void process(SourceLine line) {
        String op;
        char spec;

        if (line.getLength() < 28) {
            return;
        }
        if (line.isComment()) {
            return;
        }
        spec = line.getSpec();
        if (spec != 'c' && spec != 'C') {
            return;
        }
        op = line.get(LinePosition.C_OPERATION);
        if (op == null) {
            return;
        }
        if (op.equalsIgnoreCase("IFEQ")) {
            replace(line, "IF", "=");
        } else if (op.equalsIgnoreCase("IFNE")) {
            replace(line, "IF", "<>");
        } else if (op.equalsIgnoreCase("IFGT")) {
            replace(line, "IF", ">");
        } else if (op.equalsIgnoreCase("IFLT")) {
            replace(line, "IF", "<");
        } else if (op.equalsIgnoreCase("IFGE")) {
            replace(line, "IF", ">=");
        } else if (op.equalsIgnoreCase("IFLE")) {
            replace(line, "IF", "<=");
        } else if (op.equalsIgnoreCase("OREQ")) {
            replaceNext(line, "OR", "=");
        } else if (op.equalsIgnoreCase("ORNE")) {
            replaceNext(line, "OR", "<>");
        } else if (op.equalsIgnoreCase("ORGT")) {
            replaceNext(line, "OR", ">");
        } else if (op.equalsIgnoreCase("ORGE")) {
            replaceNext(line, "OR", ">=");
        } else if (op.equalsIgnoreCase("ORLT")) {
            replaceNext(line, "OR", "<");
        } else if (op.equalsIgnoreCase("ORLE")) {
            replaceNext(line, "OR", "<=");
        } else if (op.equalsIgnoreCase("ANDEQ")) {
            replaceNext(line, "AND", "=");
        } else if (op.equalsIgnoreCase("ANDNE")) {
            replaceNext(line, "AND", "<>");
        } else if (op.equalsIgnoreCase("ANDNE")) {
            replaceNext(line, "AND", "<>");
        } else if (op.equalsIgnoreCase("ANDGT")) {
            replaceNext(line, "AND", ">");
        } else if (op.equalsIgnoreCase("ANDGE")) {
            replaceNext(line, "AND", ">=");
        } else if (op.equalsIgnoreCase("ANDLT")) {
            replaceNext(line, "AND", "<");
        } else if (op.equalsIgnoreCase("ANDLE")) {
            replaceNext(line, "AND", "<=");
        } else if (op.equalsIgnoreCase("DOUEQ")) {
            replace(line, "DOU", "=");
        } else if (op.equalsIgnoreCase("DOWEQ")) {
            replace(line, "DOW", "=");
        } else if (op.equalsIgnoreCase("DOWLE")) {
            replace(line, "DOW", "<=");
        } else if (op.equalsIgnoreCase("DOWGE")) {
            replace(line, "DOW", ">=");
        } else if (op.equalsIgnoreCase("DOWNE")) {
            replace(line, "DOW", "<>");
        } else if (op.equalsIgnoreCase("WHENEQ")) {
            replace(line, "WHEN", "=");
        }
    }

    private void replace(SourceLine line, String condition, String comparison) {
        String buffer;
        String ind;

        while (condition.length() < LinePosition.C_OPERATION.length) {
            condition = condition + " ";
        }
        // any indicators on line?
        ind = line.get(LinePosition.C_INDICATORS);
        while (ind.length() < LinePosition.C_INDICATORS.length) {
            ind = " " + ind;
        }
        buffer = "     C  " + ind + "              " + condition + line.get(LinePosition.C_FACTOR_1) + " " + comparison + " " + line.get(LinePosition.C_FACTOR_2) + "\n";
        line.setText(buffer);
    }

    private void replaceNext(SourceLine line, String next, String comparison) {
        SourceLine lineParent;
        String buffer;
        String ind;

        lineParent = line.getParent();
        lineParent.appendText(" " + next);
        // any indicators on line?
        // any indicators on line?
        ind = line.get(LinePosition.C_INDICATORS);
        while (ind.length() < LinePosition.C_INDICATORS.length) {
            ind = " " + ind;
        }
        buffer = "     C  " + ind + "                        " + line.get(LinePosition.C_FACTOR_1) + " " + comparison + " " + line.get(LinePosition.C_FACTOR_2) + "\n";
        line.setText(buffer);
    }
}
