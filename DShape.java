
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class DShape implements ModelListener {
	
	public final static int KNOB_SIZE = 9;
	
	protected DShapeModel dShapeModel;
	protected boolean resizing;
	protected Canvas canvas;
	
	public DShape(DShapeModel d) {
		dShapeModel = d;
	}
	
	
	public void draw(Graphics g) {
		
	}

	public DShapeModel getdShapeModel() {
		return dShapeModel;
	}
	
	public void setdShapeModel(DShapeModel dShapeModel) {
		this.dShapeModel = dShapeModel;
	}
	
	public Rectangle getBounds() {
		return dShapeModel.getBounds();
	}
	
	public List<Point> getKnobs() {
		List<Point> list = new ArrayList<Point>();
		Rectangle bounds = dShapeModel.getBounds();
		int x = bounds.x;
		int y = bounds.y;
		int width = bounds.width;
		int height = bounds.height;
		
		list.add(new Point(x, y)); 
		list.add(new Point(x, y + height));
		list.add(new Point(x + width, y));
		list.add(new Point(x + width, y + height));
		
		return list;
	}
	
	public void drawKnobs(Graphics g) {
		List<Point> points = getKnobs();
		
		int thirdKnobSize = KNOB_SIZE/3;
		
		g.setColor(Color.black);
		for (Point p : points) {
			int x = p.x;
			int y = p.y;
			
			g.fillRect(x - thirdKnobSize, y - thirdKnobSize, KNOB_SIZE, KNOB_SIZE);
		}
	}
	
	public Color getColor() {
		return dShapeModel.getColor();
	}
	
	public void setColor(Color c) {
		dShapeModel.setColor(c);
		dShapeModel.notifyMListeners();
	}
	
	public void setResizing(boolean resizing) {
		this.resizing = resizing;
	}
	
	public boolean isResizing() {
		return resizing;
	}


	@Override
	public void modelChanged(DShapeModel model) {
		if (dShapeModel == model) {
			canvas.repaint(dShapeModel.getBounds());
		}
	}
	
}
