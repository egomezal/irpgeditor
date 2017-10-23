package org.egomez.parm;

import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;

public class ArrayNode extends AbstractMutableTreeTableNode {
	private Object[] x =null;
	public ArrayNode(Object[] data) {
		super(data);
		x= data;
	}

	@Override
	public Object getValueAt(int column) {
		return getUserObject()[column];
	}

	@Override
	public void setValueAt(Object aValue, int column) {
		getUserObject()[column] = aValue;
	}

	@Override
	public int getColumnCount() {
		return x.length;
	}

	@Override
	public Object[] getUserObject() {
		return (Object[]) super.getUserObject();
	}

	@Override
	public boolean isEditable(int column) {
		return true;
	}

}
