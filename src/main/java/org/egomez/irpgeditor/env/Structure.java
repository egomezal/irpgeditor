package org.egomez.irpgeditor.env;

import java.util.*;
import javax.swing.tree.*;

import org.egomez.irpgeditor.event.*;

/**
 * @author Derek Van Kooten.
 */
public class Structure {

    OutputStructure output;

    public void setOutput(OutputStructure output) {
        this.output = output;
    }

    @SuppressWarnings("rawtypes")
    public void setStructure(TreeModel model, Enumeration expands, ListenerStructure listener) {
        output.setStructure(model, expands, listener);
    }

    public void removeStructure(TreeModel model) {
        output.removeStructure(model);
    }
}
