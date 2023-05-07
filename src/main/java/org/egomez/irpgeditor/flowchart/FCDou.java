package org.egomez.irpgeditor.flowchart;

import java.awt.*;

/**
 * @author Derek Van Kooten
 */
public class FCDou extends FCShape {

    boolean finished = false;
    FCShape shapeLoop = null;

    public FCDou(FCOp op, FCShape previous, FCShape container) {
        super(op, previous, container);
    }

    @Override
    public FCShape appendOp(FCOp op) {
        if (finished) {
            if (op.getOp().startsWith("WHEN")
                    || op.getOp().startsWith("ELSE")
                    || op.getOp().startsWith("OTHER")
                    || op.getOp().startsWith("END")) {
                // must notify the container that an when, else or end was received.
                // if no container is available then this is an extra END statement with 
                // no start statement, must throw an error.
                return container.containerProcess(op);
            }
            next = FCShape.construct(op, this, container);
            return next;
        }
        shapeLoop = FCShape.construct(op, null, this);
        return shapeLoop;
    }

    @Override
    protected FCShape containerProcess(FCOp op) {
        if (op.getOp().equals("END")
                || op.getOp().equals("ENDDO")) {
            finished = true;
            return this;
        }
        throw new RuntimeException("Invalid source structure: " + op.getOp() + ", " + op.getStart());
    }

    @Override
    protected void calculateWidth(Graphics graphics) {
        if (shapeLoop == null) {
            width = DEFAULT_WIDTH;
            center = width / 2;
        } else {
            width = shapeLoop.getSeriesWidth(graphics);
            center = shapeLoop.getSeriesCenter(graphics);
        }
        width += DEFAULT_HORIZONTAL_SPACING;
    }

    @Override
    public void calculateHeight(Graphics graphics) {
        FontMetrics fm;

        fm = graphics.getFontMetrics();
        // this is where the line comes back in.
        height = DEFAULT_VERTICAL_SPACING;
        if (shapeLoop != null) {
            height += shapeLoop.getTotalHeight(graphics);
        }
        height += DEFAULT_VERTICAL_SPACING;
        height += (fm.getHeight() * 4) + (2 * DEFAULT_TEXT_VERTICAL_SPACING);

        if (next != null) {
            height += DEFAULT_VERTICAL_SPACING;
        }
    }

    public void draw(Graphics graphics, int center, int y) {
        int bottom;

        bottom = y + getHeight(graphics);

        // draw connection line.
        graphics.setColor(COLOR_BORDER_LIGHT);
        graphics.fillRect(center - 2, y, 2, DEFAULT_VERTICAL_SPACING);
        graphics.setColor(COLOR_BORDER);
        graphics.fillRect(center, y, 2, DEFAULT_VERTICAL_SPACING);
        graphics.setColor(COLOR_BORDER_DARK);
        graphics.fillRect(center + 2, y, 2, DEFAULT_VERTICAL_SPACING);

        y += DEFAULT_VERTICAL_SPACING;

        if (shapeLoop != null) {
            shapeLoop.draw(graphics, center, y);
        }

        if (next != null) {
            next.draw(graphics, center, bottom);
        }
    }
}
