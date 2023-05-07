package org.egomez.irpgeditor;

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
/**
 * defines the line specs.
 *
 * @author Derek Van Kooten.
 */
public class RPGLineSpec {

    public char specUpper, specLower;
    public LinePosition[] positions;

    public static RPGLineSpec[] SPECS = new RPGLineSpec[]{
        new RPGLineSpec('D', 'd', new LinePosition[]{LinePosition.FORM_TYPE,
            LinePosition.COMMENT, LinePosition.DIRECTIVE, LinePosition.D_NAME,
            LinePosition.D_EXTERNAL, LinePosition.D_DATA_STRUCTURE_TYPE,
            LinePosition.D_DECLARATION_TYPE, LinePosition.D_FROM, LinePosition.D_TO,
            LinePosition.D_INTERNAL_DATA_TYPE, LinePosition.D_DECIMAL_POSITIONS,
            LinePosition.D_KEYWORDS, LinePosition.D_COMMENTS
        }),
        new RPGLineSpec('A', 'a', new LinePosition[]{LinePosition.FORM_TYPE,
            LinePosition.COMMENT, LinePosition.A_INDICATOR1,
            LinePosition.A_INDICATOR2, LinePosition.A_INDICATOR3,
            LinePosition.A_TYPE, LinePosition.A_NAME, LinePosition.A_REF,
            LinePosition.A_LENGTH, LinePosition.A_DATA_TYPE,
            LinePosition.A_DECIMAL_POSITIONS, LinePosition.A_USE,
            LinePosition.A_LINE, LinePosition.A_POSITIONS,
            LinePosition.A_FUNCTIONS
        }),
        new RPGLineSpec('C', 'c', new LinePosition[]{LinePosition.FORM_TYPE,
            LinePosition.COMMENT, LinePosition.C_CONTROL_LEVEL,
            LinePosition.C_INDICATORS, LinePosition.C_FACTOR_1,
            LinePosition.C_OPERATION, LinePosition.C_FACTOR_2,
            LinePosition.C_RESULT, LinePosition.C_FIELD_LENGTH,
            LinePosition.C_DECIMAL_POSITIONS, LinePosition.C_RESULT_INDICATORS
        }),
        new RPGLineSpec('F', 'f', new LinePosition[]{LinePosition.FORM_TYPE,
            LinePosition.F_NAME, LinePosition.F_TYPE, LinePosition.F_DESIGNATION,
            LinePosition.F_EOF, LinePosition.F_ADDITION, LinePosition.F_SEQUENCE,
            LinePosition.F_FORMAT, LinePosition.F_RECORD_LENGTH,
            LinePosition.F_LIMITS_PROCESSING, LinePosition.F_KEY,
            LinePosition.F_ADDRESS_TYPE, LinePosition.F_ORGANIZATION,
            LinePosition.F_DEVICE, LinePosition.F_RESERVED, LinePosition.F_KEYWORDS
        }),
        new RPGLineSpec(' ', ' ', new LinePosition[]{LinePosition.FORM_TYPE,
            LinePosition.COMMENT
        })
    };

    private RPGLineSpec(char specUpper, char specLower, LinePosition[] positions) {
        this.specUpper = specUpper;
        this.specLower = specLower;
        this.positions = positions;
        for (LinePosition position : positions) {
            position.spec = this;
        }
    }

    public static LinePosition getLinePosition(char c, int col) {
        RPGLineSpec spec;
        LinePosition position;

        if (col == 6) {
            return LinePosition.FORM_TYPE;
        }
        for (RPGLineSpec SPECS1 : SPECS) {
            spec = SPECS1;
            if (spec.specLower == c || spec.specUpper == c) {
                for (LinePosition position1 : spec.positions) {
                    position = position1;
                    if (col >= position.start && col <= position.end) {
                        return position;
                    }
                }
                return null;
            }
        }
        return null;
    }

    public static LinePosition getLinePosition(SourceLine line, int position) {
        StringBuilder source = new StringBuilder();
        char c;

        if (line == null) {
            return null;
        }
        source.append(line.parser.getText());
        if (line.start + 6 >= source.length()) {
            return null;
        }
        c = source.charAt(line.start + 6);
        if (c == '*') {
            return null;
        }
        if (c == '/') {
            return null;
        }
        if (c == '+') {
            return null;
        }
        // compile time data.
        if (source.charAt(line.start) == '*'
                && source.charAt(line.start + 1) == '*') {
            return null;
        }

        c = source.charAt(line.start + 5);
        return getLinePosition(c, position);
    }
}
