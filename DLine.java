
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


public class DLine extends DShape {
	public DLine(DShapeModel d) {
        super(d);
    }
	
	@Override
	public void draw(Graphics g) {
		DLineModel model = (DLineModel) dShapeModel;
        g.setColor(model.getColor());
        g.drawLine(model.getP1().x, model.getP1().y, model.getP2().x, model.getP2().y);
	}

	@Override
	public List<Point> getKnobs() {
		List<Point> points = new ArrayList<Point>();
		
		DLineModel model = (DLineModel) dShapeModel;
		points.add(model.getP1());
		points.add(model.getP2());
		
		return points;
	}

}
