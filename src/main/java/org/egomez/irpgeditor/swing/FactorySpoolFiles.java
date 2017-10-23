package org.egomez.irpgeditor.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.as400.access.*;

/**
 * @author not attributable
 */
public class FactorySpoolFiles implements FactoryPanelTool {
	Logger logger = LoggerFactory.getLogger(FactorySpoolFiles.class);

	public PanelTool construct(Object object) {
		PanelSpool panelSpool;
		SpooledFile file;

		try {
			file = (SpooledFile) object;
			panelSpool = new PanelSpool();
			panelSpool.setSpooledFile(file);
			return panelSpool;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
	}
}
