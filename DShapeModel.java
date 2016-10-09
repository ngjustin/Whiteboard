
import java.awt.Rectangle;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Point;

public class DShapeModel {
	
	private int ID;
	protected Color color;
	protected Rectangle bounds;
	protected ArrayList<ModelListener> listenersList;
	
	public DShapeModel() {
		bounds = new Rectangle(0, 0, 0, 0);
		color = Color.GRAY;
	}
	
	public DShapeModel(int x, int y, int width, int height, Color color) {
		bounds = new Rectangle(x, y, width, height);
		this.color = color;
		listenersList = new ArrayList<ModelListener>();
	}
	
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	public void setBounds(int x, int y, int width, int height) {
		bounds = new Rectangle(x, y, width, height);
		notifyMListeners();
	}
	
	public void setBounds(Rectangle rect) {
		bounds = rect;
		notifyMListeners();
	}
	
	/**
	 * Moves the center of the shape to the 
	 * new center point. 
	 * @param newCenter Point of the new center of the shape
	 */
	public void moveShape(Point newCenter)
	{
		setBounds(newCenter.x - bounds.width/2, newCenter.y - bounds.height/2, bounds.width, bounds.height);
		
	}
	
	public Point getCenterPoint()
	{
		int centerX = bounds.x + bounds.width/2;
		int centerY = bounds.y + bounds.height/2;
		
		return new Point(centerX, centerY);
	}
	
	/*
	 *  Resizes a shape at the current point
	 */
	public void resize(Point movingPoint, Point currPoint) {
		Rectangle rect = getBounds();
		int x = rect.x;
		int y = rect.y;
		int width = rect.width;
		int height = rect.height;
		
		int newX = 0;
		int newY = 0;
		int newWidth = 0;
		int newHeight = 0;
		
		if (currPoint.x == x && currPoint.y == y) {
			newX = movingPoint.x;
			newY = movingPoint.y;
			newWidth = width - (movingPoint.x - currPoint.x);
			newHeight = height - (movingPoint.y - currPoint.y);
		}
		else if (currPoint.x == (x + width) && currPoint.y == y) {
			newX = x;
			newY = movingPoint.y;
			newWidth = width - (currPoint.x - movingPoint.x);
			newHeight = height - (movingPoint.y - currPoint.y);
		}
		else if (currPoint.x == x && currPoint.y == (y + height)) {
			newX = movingPoint.x;
			newY = y;
			newWidth = width - (movingPoint.x - currPoint.x);
			newHeight = height - (currPoint.y - movingPoint.y);
		}
		else if (currPoint.x == (x + width) && currPoint.y == (y + height)) {
			newX = x;
			newY = y;
			newWidth = width - (currPoint.x - movingPoint.x);
			newHeight = height - (currPoint.y - movingPoint.y);
		}
		
		setBounds(newX, newY, newWidth, newHeight);
		notifyMListeners();
	}
	
	public void mimic(DShapeModel model) {
		this.color = model.getColor();
		this.bounds = model.getBounds();
		notifyMListeners();
	}
	
	public void resizeAdjustment()
	{
		Rectangle rect = getBounds();
		
		if (rect.width < 0) {
			rect.width *= -1;
			rect.x -= rect.width;
			notifyMListeners();
		}
		
		if (rect.height < 0) {
			rect.height *= -1;
			rect.y -= rect.height;
			notifyMListeners();
		}
	}
	
	public Point getCoordinates() {
		return bounds.getLocation();
	}
	
	public void setCoordinates(int x, int y) {
		bounds.setLocation(x, y);
		notifyMListeners();
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		notifyMListeners();
	}
	
	public void addMListener(ModelListener m) {
		if (listenersList == null)
			 listenersList = new ArrayList<ModelListener>();
		
		listenersList.add(m);
	}
	
	public boolean removeMListener(ModelListener m) {
		return listenersList.remove(m);
	}
	
	public void notifyMListeners() {
		if (listenersList == null)
			return;
		
		for (ModelListener m : listenersList) {
			m.modelChanged(this);
		}
	}
	
	public void setX(int x) {
		bounds.x = x;
		notifyMListeners();
	}
	
	public void setY(int y) {
		bounds.y = y;
		notifyMListeners();
	}
	
	public int getID() {
		return ID;
	}
	
	public void setID(int Id) {
		this.ID = Id;
	}
	
}
