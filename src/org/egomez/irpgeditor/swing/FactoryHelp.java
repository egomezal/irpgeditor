package org.egomez.irpgeditor.swing;

import org.egomez.irpgeditor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * gets called when someone wants to see help
 * 
 * @author not attributable
 */
public class FactoryHelp implements FactoryPanelTool {
	PanelHelp panelHelp = new PanelHelp();
	Logger logger = LoggerFactory.getLogger(FactoryHelp.class);

	public PanelTool construct(Object object) {
		HelpRequest request;

		try {
			request = (HelpRequest) object;
			panelHelp.setHelpRequest(request);
			return panelHelp;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
	}
}
