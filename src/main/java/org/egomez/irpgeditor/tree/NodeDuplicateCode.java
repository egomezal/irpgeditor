package org.egomez.irpgeditor.tree;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import org.egomez.irpgeditor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author not attributable
 */
public class NodeDuplicateCode extends NodeDefault implements Runnable, DocumentListener {
	static int minMatches = 5;
	ProjectMember projectMember;
	SourceParser sourceParser;
	TreeModelSourceStructure treeModel;
	Thread threadScan, threadScanWait;
	@SuppressWarnings("rawtypes")
	ArrayList listTemp;
	long lastModified = 0;
	Logger logger = LoggerFactory.getLogger(NodeDuplicateCode.class);
	
	public NodeDuplicateCode(ProjectMember projectMember, SourceParser sourceParser,
			TreeModelSourceStructure treeModel) {
		this.projectMember = projectMember;
		this.sourceParser = sourceParser;
		this.treeModel = treeModel;
	}

	public void clear() {
		if (list.size() > 0) {
			list.clear();
			treeModel.fireStructureChanged(new TreeModelEvent(treeModel, new Object[] { treeModel.getRoot(), this }));
		}
	}

	public void startScan() {
		threadScan = new Thread() {
			public void run() {
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				try {
					scan();
				} catch (Exception e) {
					//e.printStackTrace();
					logger.error(e.getMessage());
				}
				threadScan = null;
			}
		};
		threadScan.start();
	}

	/**
	 * start scanning for duplicate code.
	 */
	@SuppressWarnings("rawtypes")
	protected void scan() {
		SourceLine line;

		listTemp = new ArrayList();
		line = sourceParser.getFirst();
		while (line != null) {
			// look for duplicates
			if (isPartOfDuplicate(line)) {
				line = line.getNext();
			} else {
				line = findDuplicates(line);
			}
		}
		list = listTemp;
		if (list.size() > 0) {
			treeModel.fireStructureChanged(new TreeModelEvent(treeModel, new Object[] { treeModel.getRoot(), this }));
		}
	}

	protected SourceLine findDuplicates(SourceLine line) {
		SourceLine lineNext;
		NodeDuplicates node = null;

		lineNext = line.getNext();
		while (lineNext != null) {
			if (lineNext.isSame(line)) {
				// if node == null, then a match of 2 or more lines hasnt been
				// found yet
				// for this line, but, a single has just been found to match, so
				// see if there are multiple lines that match.
				if (node == null) {
					// how many lines are duplicate
					node = foundDuplicateLines(line, lineNext);
				} else {
					foundDuplicateLines(node, lineNext);
				}
			}
			lineNext = lineNext.getNext();
		}
		if (node == null) {
			return line.getNext();
		}
		return node.getFirst().lineEnd.getNext();
	}

	protected void foundDuplicateLines(NodeDuplicates node, SourceLine lineStart) {
		NodeDuplicate nodeFirst;
		SourceLine line, lineNext;

		// the variable line will loop through the existing duplicate code.
		nodeFirst = node.getFirst();
		line = nodeFirst.lineStart.getNext();
		// the variable lineNext will loop through the newly found duplicate
		// code.
		lineNext = lineStart.getNext();
		while (true) {
			if (line.isSame(lineNext) == false) {
				return;
			}
			if (line == nodeFirst.lineEnd) {
				// if this point is reached, then all the lines are equal, make
				// another duplicate node.
				node.add(new NodeDuplicate(node, lineStart, lineNext));
				return;
			}
			line = line.getNext();
			lineNext = lineNext.getNext();
		}
	}

	protected NodeDuplicates foundDuplicateLines(SourceLine lineAStart, SourceLine lineBStart) {
		SourceLine lineANext, lineBNext, lineAEnd, lineBEnd;
		int count;

		// see if there are at least minMatches or more lines that match.
		lineAEnd = null;
		lineBEnd = null;
		lineANext = lineAStart.getNext();
		lineBNext = lineBStart.getNext();
		count = 1;
		while (true) {
			if (lineANext == null || lineBNext == null || lineANext == lineBStart
					|| lineANext.isSame(lineBNext) == false) {
				if (count >= minMatches) {
					return add(lineAStart, lineAEnd, lineBStart, lineBEnd);
				}
				return null;
			}
			count++;
			lineAEnd = lineANext;
			lineBEnd = lineBNext;
			lineANext = lineANext.getNext();
			lineBNext = lineBNext.getNext();
		}
	}

	@SuppressWarnings("unchecked")
	protected NodeDuplicates add(SourceLine lineAStart, SourceLine lineAEnd, SourceLine lineBStart,
			SourceLine lineBEnd) {
		NodeDuplicates nodeDuplicates = null;
		NodeDuplicate nodeA, nodeB;

		nodeA = new NodeDuplicate(nodeDuplicates, lineAStart, lineAEnd);
		// does this node already exist in the tree?
		if (exists(nodeA)) {
			return null;
		}
		nodeDuplicates = new NodeDuplicates(this);
		nodeB = new NodeDuplicate(nodeDuplicates, lineBStart, lineBEnd);
		nodeA.parent = nodeDuplicates;
		nodeDuplicates.add(nodeA);
		nodeDuplicates.add(nodeB);
		listTemp.add(nodeDuplicates);
		return nodeDuplicates;
	}

	protected boolean exists(NodeDuplicate node) {
		NodeDuplicates nodeDuplicates;

		for (int x = 0; x < listTemp.size(); x++) {
			nodeDuplicates = (NodeDuplicates) listTemp.get(x);
			if (nodeDuplicates.list.contains(node)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isPartOfDuplicate(SourceLine line) {
		NodeDuplicates nodeDuplicates;
		NodeDuplicate node;

		for (int x = 0; x < listTemp.size(); x++) {
			nodeDuplicates = (NodeDuplicates) listTemp.get(x);
			for (int y = 0; y < nodeDuplicates.list.size(); y++) {
				node = (NodeDuplicate) nodeDuplicates.getChild(y);
				if (line.isBetween(node.lineStart, node.lineEnd, true)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getText() {
		return "Duplicate Code";
	}

	public void rightClick(Component invoker, int x, int y) {
	}

	public void insertUpdate(DocumentEvent e) {
		if (threadScan != null) {
			threadScan.interrupt();
		}
		clear();
		lastModified = System.currentTimeMillis();
		if (threadScanWait == null) {
			threadScanWait = new Thread(this);
			threadScanWait.start();
		}
	}

	public void removeUpdate(DocumentEvent e) {
		if (threadScan != null) {
			threadScan.interrupt();
		}
		clear();
		lastModified = System.currentTimeMillis();
		if (threadScanWait == null) {
			threadScanWait = new Thread(this);
			threadScanWait.start();
		}
	}

	public void changedUpdate(DocumentEvent e) {
	}

	@SuppressWarnings("static-access")
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		while (true) {
			try {
				Thread.currentThread().sleep(5000);
			} catch (Exception e) {
			}
			if (System.currentTimeMillis() >= (lastModified + 5000)) {
				threadScanWait = null;
				lastModified = 0;
				startScan();
				return;
			}
		}
	}
}

class NodeDuplicates extends NodeDefault {
	NodeDuplicateCode parent;

	public NodeDuplicates(NodeDuplicateCode parent) {
		this.parent = parent;
	}

	public NodeDuplicate getFirst() {
		return (NodeDuplicate) list.get(0);
	}

	public Node getParent() {
		return parent;
	}

	public String getText() {
		NodeDuplicate node;

		node = getFirst();
		return "Lines: " + node.getCount() + ", " + node.lineStart.getText();
	}

	public void rightClick(Component invoker, int x, int y) {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem menuSub = new JMenuItem();

		menuSub.setText("Make Subroutine");
		popupMenu.add(menuSub);

		menuSub.addActionListener(new ActionSub());

		popupMenu.show(invoker, x, y);
	}

	public void removeChildrenCode(String subName) {
		for (int x = (getChildCount() - 1); x >= 0; x--) {
			((NodeDuplicate) getChild(x)).removeCode(subName);
		}
	}

	/**
	 */
	class ActionSub implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			String subName;

			subName = JOptionPane.showInputDialog(null, "New Subroutine Name?");
			if (subName == null) {
				return;
			}
			((NodeDuplicate) getChild(0)).createSubroutine(subName);
			removeChildrenCode(subName);
		}
	}
}

class NodeDuplicate extends NodeAbstract {
	NodeDuplicates parent;
	SourceLine lineStart, lineEnd;
	Logger logger = LoggerFactory.getLogger(NodeDuplicate.class);
	public NodeDuplicate(NodeDuplicates parent, SourceLine lineStart, SourceLine lineEnd) {
		this.parent = parent;
		this.lineStart = lineStart;
		this.lineEnd = lineEnd;
	}

	public String getAllText() {
		StringBuffer buffer;
		SourceLine line;

		line = lineStart;
		buffer = new StringBuffer();
		while (true) {
			buffer.append(line.getText());
			if (line == lineEnd) {
				return buffer.toString();
			}
			line = line.getNext();
		}
	}

	public int getCount() {
		SourceLine line;
		int count;

		count = 1;
		line = lineStart;
		while (line != lineEnd) {
			count++;
			line = line.getNext();
		}
		return count;
	}

	public String getText() {
		SourceParser parser;

		parser = lineStart.getSourceParser();
		return "Location: " + parser.getPoint(lineStart.getStart()).y + " to " + parser.getPoint(lineEnd.getStart()).y;
	}

	public Node getParent() {
		return parent;
	}

	public void selected() {
		lineStart.requestFocus();
	}

	public void rightClick(Component invoker, int x, int y) {
	}

	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object instanceof NodeDuplicate) {
			NodeDuplicate node = (NodeDuplicate) object;
			if (node.lineStart == lineStart && node.lineEnd == lineEnd) {
				return true;
			}
		}
		return false;
	}

	public void createSubroutine(String subName) {
		SourceLine sourceLine, nextLine;
		StringBuffer buffer;
		Document doc;

		// find the last C line and append text to it.
		sourceLine = lineStart.getSourceParser().getFirst();
		if (sourceLine == null) {
			return;
		}
		nextLine = sourceLine.getNext();
		if (nextLine == null) {
			return;
		}
		while (nextLine != null && nextLine.getSpec() != 'o' && nextLine.getSpec() != 'O') {
			sourceLine = nextLine;
			nextLine = nextLine.getNext();
		}
		buffer = new StringBuffer("     C                   begsr\n");
		SourceLine.formatText(buffer, LinePosition.C_FACTOR_1, subName);
		buffer.insert(0, "     C*******************************************************\n");
		buffer.insert(0, "     C* " + subName + "\n");
		buffer.insert(0, "     C*******************************************************\n");
		// put all the duplicate code into the buffer.
		nextLine = lineStart;
		while (nextLine != null && nextLine != lineEnd.getNext()) {
			buffer.append(nextLine.getText());
			nextLine = nextLine.getNext();
		}
		buffer.append("     C                   endsr\n");
		doc = lineStart.getSourceParser().getDocument();
		try {
			doc.insertString(sourceLine.getStart() + sourceLine.getLength(), buffer.toString(), null);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public void removeCode(String subName) {
		Document doc;

		try {
			doc = lineStart.getSourceParser().getDocument();
			doc.remove(lineStart.getStart(), (lineEnd.getStart() + lineEnd.getLength()) - lineStart.getStart());
			doc.insertString(lineStart.getStart(), "     C                   exsr      " + subName + "\n", null);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public int hashCode() {
		return lineStart.getStart();
	}
}
