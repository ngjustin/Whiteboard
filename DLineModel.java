
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

public class DLineModel extends DShapeModel {
	
	private Point p1, p2;

	public DLineModel() {
		super();
		p1 = new Point(0, 0);
		p2 = new Point(0, 0);
	}

	public DLineModel(int x, int y, int width, int height, Color color) {
		super(x, y, width, height, color);
		p1 = new Point(x, y);
		p2 = new Point(x + width, y + height);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		
		super.setBounds(x, y, width, height);
	}

	
	@Override
	public void moveShape(Point newCenter) {
		
		Point currCenter = getCenterPoint();
		
		int newCenterDiffX = currCenter.x - newCenter.x;
		int newCenterDiffY = currCenter.y - newCenter.y;
		
		p1.x = p1.x - newCenterDiffX;
		p2.x = p2.x - newCenterDiffX;
		p1.y = p1.y - newCenterDiffY;
		p2.y = p2.y - newCenterDiffY;
		
		super.moveShape(newCenter);
	}

	@Override
	public void mimic(DShapeModel model) {
		DLineModel realModel =(DLineModel) model;
		this.bounds = realModel.bounds;
		this.color = realModel.color;
		p1 = realModel.p1;
		p2 = realModel.p2;
		
		notifyMListeners();
	}

	@Override
	public void resize(Point movingPoint, Point currPoint) {
		if (p1 == currPoint) {
			p1.x = movingPoint.x;
			p1.y = movingPoint.y;
			notifyMListeners();
		}
		else if (p2 == currPoint) {
			p2.x = movingPoint.x;
			p2.y = movingPoint.y;
			notifyMListeners();
		}
	}

	@Override
	public void resizeAdjustment() {
		Rectangle rect = getBounds();

		rect.x = p1.x;
		rect.y = p1.y;
		rect.width = p2.x - p1.x;
		rect.height = p2.y - p1.y;

		if (rect.height < 0) {
			rect.height *= -1;
			rect.y -= rect.height;
		}

		if (rect.width < 0) {
			rect.width *= -1;
			rect.x -= rect.width;
		}
		notifyMListeners();
	}

	public Point getP1() {
		return p1;
	}
	
	public void setP1(Point p1) {
		this.p1 = p1;
		notifyMListeners();
	}

	public Point getP2() {
		return p2;
	}
	
	public void setP2(Point p2) {
		this.p2 = p2;
		notifyMListeners();
	}
	
}
