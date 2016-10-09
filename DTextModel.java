
import java.awt.Color;

public class DTextModel extends DShapeModel {
	
	public static final String DEFAULT_FONT = "Dialog";
	public static final String DEFAULT_TEXT = "Hello";
	
	private String font;
	private String text;
	
	public DTextModel() {
		super();
	}
	
	public DTextModel(int x, int y, int width, int height, Color color) {
		super(x, y, width, height, color);
		font = DEFAULT_FONT;
		text = DEFAULT_TEXT;
	}
	
	
	@Override
	public void mimic(DShapeModel model) {
		DTextModel realModel = (DTextModel) model;
		this.bounds = realModel.getBounds();
		this.color = realModel.getColor();
		this.font = realModel.getFont();
		this.text = realModel.getText();
		notifyMListeners();
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String t) {
		this.text = t;
		notifyMListeners();
	}
	
	public String getFont() {
		return font;
	}
	
	public void setFont(String f) {
		this.font = f;
		notifyMListeners();
	}
}
