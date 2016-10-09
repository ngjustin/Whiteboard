
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;


public class DShapeTableModel extends AbstractTableModel implements ModelListener {
	
	private ArrayList<DShapeModel> shapes = new ArrayList<DShapeModel>();
	public static final int COLUMN_COUNT = 4;
	
	public DShapeTableModel() {
		super();
	}
	
	@Override
	public int getRowCount() {
		return shapes.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_COUNT;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		DShapeModel shape = shapes.get(rowIndex);
		if (columnIndex == 0)
			return shape.getBounds().x;
		else if (columnIndex == 1)
			return shape.getBounds().y;
		else if (columnIndex == 2)
			return shape.getBounds().getWidth();
		else if (columnIndex == 3)
			return shape.getBounds().getHeight();
		else
			return null;
	}
	
	public int getRowNum(DShapeModel d) {
		return shapes.indexOf(d);
	}
	
	public void addShape(DShapeModel d) {
		shapes.add(0, d);
		d.addMListener(this);
		fireTableDataChanged();
	}
	
	public void removeShape(DShapeModel d) {
		for (DShapeModel m: shapes) {
			if (m.getID() == d.getID()) {
				shapes.remove(m);
				m.removeMListener(this);
				fireTableDataChanged();
				return;
			}
		}
	}
	
	public void moveToFront(DShapeModel d) {
		for (DShapeModel m: shapes) {
			if (m.getID() == d.getID()) {
				shapes.remove(m);
				shapes.add(0, m);
				fireTableDataChanged();
				return;
			}
		}
	}
	
	public void moveToBack(DShapeModel d) {
		for (DShapeModel m: shapes) {
			if (m.getID() == d.getID()) {
				shapes.remove(m);
				shapes.add(m);
				fireTableDataChanged();
				return;
			}
		}
	}
	
	/**
	 * Prevents Table Model from being edited
	 */
	public boolean isCellEditable(int rowIndex, int colIndex) {
        return false;
    }
	
	public void modelChanged(DShapeModel d) {
		int dNum = shapes.indexOf(d);
		fireTableRowsUpdated(dNum, dNum);
	}
	
	public void clear() {
		shapes.clear();
	}
	
}
