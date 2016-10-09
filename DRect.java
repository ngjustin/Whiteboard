
import java.awt.Graphics;

public class DRect extends DShape {
	
	public DRect(DShapeModel d) {
		super(d);
	}
	
	@Override
	public void draw(Graphics g) {
		g.setColor(dShapeModel.getColor());
		g.fillRect(dShapeModel.getBounds().x, dShapeModel.getBounds().y, 
				dShapeModel.getBounds().width, dShapeModel.getBounds().height);
	}

}
