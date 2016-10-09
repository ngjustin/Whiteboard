
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;


public class Canvas extends JPanel implements ModelListener {
	
	private ArrayList<DShape> shapes = new ArrayList<DShape>();
	private DShape selectedShape;

	public final int x;
	public final int y;
	public final static int WIDTH = 400;
	public final static int HEIGHT = 400;
	
	public Canvas(int x, int y) {
		this.x = x;
		this.y = y;
		selectedShape = null;
		loadCanvas();
		this.setFocusable(true);
	}
	
	/**
	 * Paints the shapes with their knobs on the canvas
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for (DShape draw : shapes) {
			draw.draw(g);
			
			if (draw == selectedShape) {
				draw.drawKnobs(g);
			}
		}
	}

	private void loadCanvas() {
		setBackground(Color.white);
	}
	
	/**
	 * Adds a shape to the canvas
	 * @param newModel Model associated with the shape to be added
	 */
	public void addShape(DShapeModel newModel) {
		DShape newShape = null;
		if (newModel instanceof DRectModel)
			newShape = new DRect(newModel);
		else if (newModel instanceof DOvalModel)
			newShape = new DOval(newModel);
		else if (newModel instanceof DLineModel)
			newShape = new DLine(newModel);
		else if (newModel instanceof DTextModel)
			newShape = new DText(newModel);
		else
			return;
		
		newModel.addMListener(this);
		shapes.add(newShape);
		repaint();
	}
	
	public void removeSelectedShape(DShape removeShape) {
		removeShape.getdShapeModel().removeMListener(this);
		shapes.remove(removeShape);
		repaint();
	}
	
	public void frontSelectedShape(DShape frontShape) {
		removeSelectedShape(frontShape);
		frontShape.getdShapeModel().addMListener(this);
		shapes.add(frontShape);
	}
	
	public void frontSelectedShape(DShapeModel frontShape) {
		for (DShape d: shapes) {
			DShapeModel m = d.getdShapeModel();
			
			if (m.getID() == frontShape.getID()) {
				removeSelectedShape(d);
				m.addMListener(this);
				shapes.add(d);
				return;
			}
		}
	}
	
	public void backSelectedShape(DShape backShape) {
		removeSelectedShape(backShape);
		backShape.getdShapeModel().addMListener(this);
		shapes.add(0, backShape);
	}
	
	public void backSelectedShape(DShapeModel frontShape) {
		for (DShape d: shapes)
		{
			DShapeModel m = d.getdShapeModel();
			
			if (m.getID() == frontShape.getID()) {
				removeSelectedShape(d);
				m.addMListener(this);
				shapes.add(0, d);
				return;
			}
		}
	}
	
	public boolean hasSelectedShape() {
		if (selectedShape != null)
			return true;
		else
			return false;
	}
	
	public DShape getSelectedShape() {
		return selectedShape;
	}
	
	public void setSelectedShapeColor(Color c) {
		selectedShape.setColor(c);
		repaint();
	}
	
	/**
	 * Sets the currently selected shape
	 * @param selectedShape Shape to be selected
	 */
	public void setSelectedShape(DShapeModel selectedShape) {
		if (selectedShape == null)
			this.selectedShape = null;
		
		for (DShape d : shapes) {
			DShapeModel currShape = d.getdShapeModel();
			
			if (selectedShape == currShape) {
				this.selectedShape = d;
				repaint();
				break;
			}
		}
	}
	
	public boolean remove(DShapeModel model) {
		if (model != null) {
			for (DShape d: shapes) {
				DShapeModel currShape = d.getdShapeModel();
				
				if (model.getID() == currShape.getID()) {
					shapes.remove(d);
					return true;
				}
			}
		}
		return false;
	}
	
	public void clear() {
		shapes.clear();
	}
	
	public ArrayList<DShape> getShapesList() {
		return shapes;
	}

	@Override
	public void modelChanged(DShapeModel model) {
		repaint();
	}
	
}
