
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.LineMetrics;


public class DText extends DShape {

	public DText(DShapeModel d) {
		super(d);
	}

	public void draw(Graphics g) {
		Shape clip = g.getClip();
		g.setClip(clip.getBounds().createIntersection(getBounds()));
		
		DTextModel dtm = (DTextModel) dShapeModel;
		Font f = computeFont(g);
		int offset = (int) g.getFontMetrics(f).getLineMetrics(dtm.getText(), g).getDescent();
		g.setColor(getColor());
		g.setFont(f);
		g.drawString(getText(), getBounds().x, getBounds().height + getBounds().y - offset);
		
		g.setClip(clip);
	}
	
	/*
	 * Computes the font based on the Text Model
	 */
	public Font computeFont(Graphics g) {
		DTextModel dtm = (DTextModel) dShapeModel;
		Font f = Font.decode(dtm.getFont());
		double initialSize = 1.0;
		f = f.deriveFont((int)(initialSize));
		for (double size = 1; true; size = (size * 1.1) + 1) {
			Font tempFont = f.deriveFont((float)size);
			FontMetrics fm = g.getFontMetrics(f);
			Rectangle tempBounds = fm.getStringBounds(getText(), g).getBounds();
			if (tempBounds.width > dtm.getBounds().width ||
					tempBounds.height > dtm.getBounds().height) {
				break;
			}
			f = tempFont;
		}
		return f;
	}

	public String getText() {
		DTextModel dtm = (DTextModel) dShapeModel;
		return dtm.getText();
	}
	
	public void setText(String text) {
		DTextModel dtm = (DTextModel) dShapeModel;
		dtm.setText(text);
	}

	public String getFont() {
		DTextModel dtm = (DTextModel) dShapeModel;
		return dtm.getFont();
	}
	
	public void setFont(String font) {
		DTextModel dtm = (DTextModel) dShapeModel;
		if (dtm.getFont().equals(font)) {
			dtm.setFont("Dialog");
		}
		else {
			dtm.setFont(font);
		}
	}
	
}
