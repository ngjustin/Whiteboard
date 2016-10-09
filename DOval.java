
import java.awt.Graphics;

public class DOval extends DShape {
	
	public DOval(DShapeModel d) {
		super(d);
	}
	
	@Override
	public void draw(Graphics g) {
		g.setColor(dShapeModel.getColor());
		g.fillOval(dShapeModel.getBounds().x, dShapeModel.getBounds().y, 
				dShapeModel.getBounds().width, dShapeModel.getBounds().height);
	}

}
