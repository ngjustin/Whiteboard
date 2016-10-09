
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class WhiteBoard extends JFrame {

	public static final int WIDTH = 1000;
	public static final int HEIGHT = 400;
	public static final String SERVER_MODE = "Server";
	public static final String CLIENT_MODE = "Client";
	public static final String NORMAL_MODE = "Normal";
	public static final String DEFUALT_PORT = "39587";
	public static final int ADD_CODE = 1;
	public static final int REMOVE_CODE = 2;
	public static final int UPDATE_CODE = 3;
	public static final int FRONT_CODE = 4;
	public static final int BACK_CODE = 5;
	
	public ArrayList<DShapeModel> listOfModel = new ArrayList<DShapeModel>();
	public ArrayList<JComponent> listOfComponents = new ArrayList<JComponent>();
	public static final String[] TABLE_HEADERS = new String[] {"X", "Y", "Width", "Height"};
	private DShapeTableModel dShapeTableModel;


	JTextField textEntry;
	JComboBox<String> fontSetter;
	JButton bStartServer;
	JButton bStartClient;
	JLabel modeLabel;
	Box mainPanel;
	Canvas canvas;
	DShapeModel selectedShape;
	int selectedKnob;

	private Server server;
	private Client client;
	String mode;

	boolean mousePressed;
	boolean enabled;
	private int idCounter;

	public WhiteBoard() {
		idCounter = 0;
		enabled = true;
		initialize();
	}

	private void initialize() {
		setTitle("Whiteboard");
		setSize(new Dimension(WhiteBoard.WIDTH, WhiteBoard.HEIGHT));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setMode(NORMAL_MODE);

		canvas = new Canvas(WhiteBoard.WIDTH - Canvas.WIDTH, 0);
		canvas.setPreferredSize(new Dimension(Canvas.WIDTH, Canvas.HEIGHT));
		canvas.setBounds(WhiteBoard.WIDTH - Canvas.WIDTH, 0, WhiteBoard.WIDTH, Canvas.HEIGHT);


		canvas.addMouseListener(new MouseListener() {
			final int WIDTH = 1;
			final int HEIGHT = 1;
			@Override
			public void mouseClicked(MouseEvent e) {

			}
			@Override
			public void mouseEntered(MouseEvent e) {

			}
			@Override
			public void mouseExited(MouseEvent e) {

			}
			@Override
			public void mousePressed(MouseEvent e) {
				if (enabled) {
					mousePressed = true;

					int x = e.getX();
					int y = e.getY();

					if (selectedShape != null) {
						if (!(selectedShape instanceof DTextModel)) {
							textEntry.setEnabled(false);
							fontSetter.setEnabled(false);
						}
						else {
							textEntry.setEnabled(true);
							fontSetter.setEnabled(true);
						}
						List<Point> points = canvas.getSelectedShape().getKnobs();
						int knobSize = canvas.getSelectedShape().KNOB_SIZE;
						int thirdKnobSize = knobSize/3;

						int counter = 0;
						for (Point p : points) {
							int pointX = p.x - thirdKnobSize;
							int pointY = p.y - thirdKnobSize;

							if( targetInBound(x, WIDTH, y, HEIGHT, pointX, knobSize, pointY, knobSize)) {
								selectedKnob = counter;
								return;
							}
							counter++;
						}
					}

					for (DShapeModel d : listOfModel) {
						Rectangle bounds = d.getBounds();

						int shapeX = bounds.x;
						int shapeY = bounds.y;
						int shapeWidth = bounds.width;
						int shapeHeight = bounds.height;

						if (targetInBound(x, WIDTH, y, HEIGHT, shapeX, shapeWidth, shapeY, shapeHeight))
						{
							selectedShape = d;
							canvas.setSelectedShape(d);

							return;
						}
					}
					selectedShape = null;
					canvas.setSelectedShape(selectedShape);
					selectedKnob = -1;
					repaint();
				}
				else {
					return;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (enabled) {
					if (!(selectedShape instanceof DTextModel)) {
						textEntry.setEnabled(false);
						fontSetter.setEnabled(false);
					}
					else {
						textEntry.setEnabled(true);
						fontSetter.setEnabled(true);
					}
					mousePressed = false;
					if(selectedKnob >= 0) {
						selectedShape.resizeAdjustment();
						selectedKnob = -1;
						updateModel();
					}
				}
				else {
					return;
				}
			}

		});

		canvas.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (enabled) {
					if (selectedShape == null)
						return;

					int x = e.getX();
					int y = e.getY();

					if (selectedKnob >= 0 && mousePressed) {
						Point p = canvas.getSelectedShape().getKnobs().get(selectedKnob);

						selectedShape.resize(e.getPoint(), p);
						updateModel();
						return;
					}

					if (mousePressed) {

						selectedShape.moveShape(new Point(x, y));
						updateModel();
						return;
					}
				}
				else {
					return;
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {

			}
		});


		mainPanel = Box.createVerticalBox();
		mainPanel.setSize(WhiteBoard.WIDTH - Canvas.WIDTH, WhiteBoard.HEIGHT/2);
		mainPanel.setMinimumSize(new Dimension(WhiteBoard.WIDTH - Canvas.WIDTH, WhiteBoard.HEIGHT/2));
		mainPanel.setBounds(0, 0, WhiteBoard.WIDTH - Canvas.WIDTH, WhiteBoard.HEIGHT/2);
		setLayout(new BorderLayout());


		Box buttonPanel = Box.createHorizontalBox();

		JButton rect = new JButton("Rect");
		rect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DRectModel rectModel = new DRectModel(10, 10, 20, 20, Color.gray);
				addModel(rectModel);
			}
		});
		listOfComponents.add(rect);
		JButton oval = new JButton("Oval");
		oval.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DOvalModel ovalModel = new DOvalModel(10, 10, 20, 20, Color.gray);
				addModel(ovalModel);
			}
		});
		listOfComponents.add(oval);
		JButton line = new JButton("Line");
		line.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DLineModel lineModel = new DLineModel(10, 10, 20, 20, Color.gray);
				addModel(lineModel);
			}
		});
		listOfComponents.add(line);
		JButton text = new JButton("Text");
		text.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DTextModel textModel = new DTextModel(10, 10, 27, 20, Color.gray);
				textModel.setText("Hello");
				textModel.setFont("Dialog");
				addModel(textModel);
			}
		});
		listOfComponents.add(text);
		modeLabel = new JLabel("Current Mode: " + NORMAL_MODE);

		mainPanel.add(Box.createVerticalStrut(10));
		buttonPanel.add(Box.createRigidArea(new Dimension(3, 0)));
		buttonPanel.add(new JLabel("Add: "));
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		buttonPanel.add(rect);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(oval);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(line);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(text);
		buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		buttonPanel.add(modeLabel);
		mainPanel.add(buttonPanel, BorderLayout.WEST);


		Box colorPanel = Box.createHorizontalBox();
		JButton setColor = new JButton("Set Color");
		setColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedShape != null) {
					Color c = JColorChooser.showDialog(WhiteBoard.this, "Please select a color.", 
							selectedShape.getColor());
					if (c != null) {
						selectedShape.setColor(c);
						updateModel();
					}
				}
			}
		});
		listOfComponents.add(setColor);

		mainPanel.add(Box.createVerticalStrut(10));
		colorPanel.add(Box.createRigidArea(new Dimension(3, 0)));
		colorPanel.add(setColor);	
		mainPanel.add(colorPanel, BorderLayout.WEST);


		Box fontPanel = Box.createHorizontalBox();

		textEntry = new JTextField("");
		textEntry.setMaximumSize(new Dimension(125, 20));
		textEntry.setPreferredSize(new Dimension(125, 20));
		textEntry.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent ke) {

			}
			@Override
			public void keyReleased(KeyEvent ke) {
				if (canvas.getSelectedShape() instanceof DText) {
					DTextModel dtm = (DTextModel)(canvas.getSelectedShape().getdShapeModel());
					dtm.setText(textEntry.getText());
				}
			}
			@Override
			public void keyTyped(KeyEvent ke) {

			}
		});
		listOfComponents.add(textEntry);

		fontSetter = new JComboBox<String>();
		fontSetter.setMaximumSize(new Dimension(150, 20));
		fontSetter.setPreferredSize(new Dimension(150, 20));
		for (String font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
			fontSetter.addItem(font);
		}
		fontSetter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (canvas.getSelectedShape() instanceof DText) {
					DTextModel dtm = (DTextModel)(canvas.getSelectedShape().getdShapeModel());
					dtm.setFont(fontSetter.getSelectedItem().toString());
				}
			}
		});
		listOfComponents.add(fontSetter);

		mainPanel.add(Box.createVerticalStrut(10));
		fontPanel.add(Box.createRigidArea(new Dimension(3, 0)));
		fontPanel.add(textEntry);
		fontPanel.add(Box.createRigidArea(new Dimension(3, 0)));
		fontPanel.add(fontSetter);
		mainPanel.add(fontPanel, BorderLayout.WEST);


		Box modShapePanel = Box.createHorizontalBox();

		JButton moveToFront = new JButton("Move to Front");
		moveToFront.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveFront(selectedShape);
			}
		});
		listOfComponents.add(moveToFront);

		JButton moveToBack = new JButton("Move to Back");
		moveToBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveBack(selectedShape);
			}
		});
		listOfComponents.add(moveToBack);

		JButton remove = new JButton("Remove Shape");
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedShape != null) {
					removeModel();
				}
			}
		});
		listOfComponents.add(remove);

		bStartServer = new JButton("Start Server");
		bStartServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String result = JOptionPane.showInputDialog("Run server on port", DEFUALT_PORT);
				if (result != null) {
					try {
						int port = Integer.parseInt(result);
						server = new Server(port);

						server.start();
						setMode(SERVER_MODE);
						disableButtons(SERVER_MODE);
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		listOfComponents.add(bStartServer);

		bStartClient = new JButton("Start Client");
		bStartClient.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String result = JOptionPane.showInputDialog("Connect to host:port", "127.0.0.1:" + DEFUALT_PORT);
				if (result != null) {
					String[] parts = result.split(":");
					try {
						client = new Client(parts[0].trim(), Integer.parseInt(parts[1].trim()));
						client.start();

						setMode(CLIENT_MODE);
						disableButtons(CLIENT_MODE);


					}
					catch (Exception ex) {
						ex.printStackTrace(); 
					}
				}
			}
		});
		listOfComponents.add(bStartClient);

		mainPanel.add(Box.createVerticalStrut(10));
		modShapePanel.add(Box.createRigidArea(new Dimension(3, 0)));
		modShapePanel.add(moveToFront);
		modShapePanel.add(Box.createRigidArea(new Dimension(10, 0)));
		modShapePanel.add(moveToBack);
		modShapePanel.add(Box.createRigidArea(new Dimension(10, 0)));
		modShapePanel.add(remove);
		mainPanel.add(modShapePanel, BorderLayout.WEST);



		Box ioPanel = Box.createHorizontalBox();

		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String result = JOptionPane.showInputDialog("File Name", null);
				if (result != null) {
					FileHandler<DShapeModel> fh = new FileHandler();

					DShapeModel[] data = new DShapeModel[listOfModel.size()];
					for (int i = 0; i < data.length; i++) {
						data[i] = listOfModel.get(i);
					}
					fh.save(result + ".xml", data);
				}
			}
		});
		listOfComponents.add(save);

		JButton open = new JButton("Open");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String result = JOptionPane.showInputDialog("File Name", null);
				if (result != null) {
					FileHandler<DShapeModel> fh = new FileHandler<DShapeModel>();

					DShapeModel[] data = fh.open(result + ".xml");
					DShapeModel[] models = new DShapeModel[listOfModel.size()];
					models = listOfModel.toArray(models);
					for (DShapeModel m: models) {
						removeModel(m);
					}

					for (DShapeModel model : data) {
						if (data != null) {
							addModel(model);
						}

					}
				}
			}
		});
		listOfComponents.add(open);

		JButton saveImage = new JButton("Save Image");
		saveImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String result = JOptionPane.showInputDialog("File Name", null);
				if (result != null) {
					File f = new File(result + ".png");

					DShape selected = canvas.getSelectedShape();
					canvas.setSelectedShape(null);
					BufferedImage image = (BufferedImage) createImage(canvas.getWidth(), canvas.getHeight());
					Graphics g = image.getGraphics();
					canvas.paintAll(g);
					g.dispose();

					canvas.setSelectedShape(selected.getdShapeModel());
					try {
						javax.imageio.ImageIO.write(image, "PNG", f);
					}
					catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		listOfComponents.add(saveImage);

		mainPanel.add(Box.createVerticalStrut(10));
		ioPanel.add(Box.createRigidArea(new Dimension(3, 0)));
		ioPanel.add(open);
		ioPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		ioPanel.add(save);
		ioPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		ioPanel.add(saveImage);
		ioPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		ioPanel.add(bStartServer);
		ioPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		ioPanel.add(bStartClient);
		mainPanel.add(ioPanel, BorderLayout.WEST);


		for (Component comp : mainPanel.getComponents())
			((JComponent) comp).setAlignmentX(Box.LEFT_ALIGNMENT);


		dShapeTableModel = new DShapeTableModel();
		JTable table = new JTable(dShapeTableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setBackground(Color.WHITE);
		for (int i = 0; i < TABLE_HEADERS.length; i++) {
			String columnHeader = TABLE_HEADERS[i];
			table.getColumnModel().getColumn(i).setHeaderValue(columnHeader);
		}
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(WhiteBoard.WIDTH - Canvas.WIDTH, WhiteBoard.HEIGHT/2));
		scrollPane.setBounds(0, WhiteBoard.HEIGHT/2, WhiteBoard.WIDTH - Canvas.WIDTH, WhiteBoard.HEIGHT);
		scrollPane.setSize(table.getWidth(), table.getHeight());
		scrollPane.setBackground(Color.WHITE);

		JSplitPane controlSplit = new JSplitPane();
		controlSplit.setSize(new Dimension(WhiteBoard.WIDTH - Canvas.WIDTH, WhiteBoard.HEIGHT));
		controlSplit.setDividerSize(0);
		controlSplit.setDividerLocation(WhiteBoard.HEIGHT/2);
		controlSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		controlSplit.setTopComponent(mainPanel);
		controlSplit.setBottomComponent(scrollPane);

		JSplitPane sp = new JSplitPane();
		sp.setSize(new Dimension(WhiteBoard.WIDTH, WhiteBoard.HEIGHT));
		sp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		sp.setDividerSize(0);
		sp.setDividerLocation(WhiteBoard.WIDTH * 55 / 100);
		sp.setLeftComponent(controlSplit);
		sp.setRightComponent(canvas);


		add(sp);
		pack();
		setVisible(true);
	}

	/**
	 * Adds a model
	 * @param model model to be added
	 */
	private void addModel(DShapeModel model) {
		if (model == null)
			return;

		if (!mode.equals(CLIENT_MODE))
			model.setID(idCounter++);

		listOfModel.add(0, model);
		canvas.addShape(model);
		dShapeTableModel.addShape(model);

		if (mode.equals(SERVER_MODE))
			server.sendData(ADD_CODE, model);
	}

	/***
	 * Removes the selected shape
	 */
	private void removeModel() {
		if (selectedShape == null)
			return;

		canvas.removeSelectedShape(canvas.getSelectedShape());
		listOfModel.remove(selectedShape);
		dShapeTableModel.removeShape(selectedShape);

		if (mode == SERVER_MODE)
			server.sendData(REMOVE_CODE, selectedShape);

		selectedShape = null;
	}

	/**
	 * Removes a specified model
	 * @param model The model to be removed
	 */
	public DShapeModel removeModel(DShapeModel model) {
		if (model == null)
			return null;

		int size = listOfModel.size();
		for (int i = 0; i < size; i++) {
			DShapeModel m = listOfModel.get(i);

			if (m.getID() == model.getID()) {
				listOfModel.remove(m);
				canvas.remove(m);
				dShapeTableModel.removeShape(m);
				i = size;
				canvas.repaint();

				if (mode == SERVER_MODE)
					server.sendData(REMOVE_CODE, model);

				return m;
			}
		}
		return null;
	}

	/**
	 * Updates data of the selected model
	 */
	private void updateModel() {
		if (mode == SERVER_MODE)
			server.sendData(UPDATE_CODE, selectedShape);
	}

	/**
	 * Updates a specified model
	 * @param model Model to be updated
	 */
	public void updateModel(DShapeModel model) {
		int size = listOfModel.size();
		for (int i = 0; i < size; i++) {
			DShapeModel m = listOfModel.get(i);

			if (m.getID() == model.getID()) {
				m.mimic(model);
				i = size;

				if (mode == SERVER_MODE)
					server.sendData(UPDATE_CODE, model);
			}
		}
	}

	public void moveFront(DShapeModel model) {
		if (model == null)
			return;

		for (DShapeModel m : listOfModel) {
			if (m.getID() == model.getID()) {
				listOfModel.remove(m);
				listOfModel.add(0, m);
				canvas.frontSelectedShape(m);
				dShapeTableModel.moveToFront(m);

				if (mode == SERVER_MODE)
					server.sendData(FRONT_CODE, model);

				return;
			}
		}
	}

	public void moveBack(DShapeModel model) {
		if (model == null)
			return;

		for (DShapeModel m : listOfModel) {
			if (m.getID() == model.getID()) {
				listOfModel.remove(m);
				listOfModel.add(m);
				canvas.backSelectedShape(m);
				dShapeTableModel.moveToBack(m);

				if (mode == SERVER_MODE)
					server.sendData(BACK_CODE, model);

				return;
			}
		}
	}

	/**
	 * Sets the mode of the Whiteboard
	 * @param mode Mode Whiteboard to be set to
	 */
	private void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * Disables buttons depending on the mode that 
	 * Whiteboard is currently in
	 * @param mode Mode that Whiteboard is currently in
	 */
	private void disableButtons(String mode) {
		bStartServer.setEnabled(false);
		bStartClient.setEnabled(false);
		modeLabel.setText("Current Mode: " + SERVER_MODE);
		if (mode == CLIENT_MODE) {
			for (JComponent comp : listOfComponents) {
				comp.setEnabled(false);
			}
			enabled = false;
			modeLabel.setText("Current Mode: " + CLIENT_MODE);
		}
	}

	/***
	 * Checks if an object is within the boundaries of another object
	 ***/
	boolean targetInBound(int x1, int sizeX1, int y1, int sizeY1, int x2, int sizeX2, int y2, int sizeY2) {
		if (y1 >= y2 &&  (y1 + sizeY1) <= (y2 + sizeY2)) {
			if (x1 >= x2 && x1 <= (x2 + sizeX2) && (x1 + sizeX1) >= x2 && (x1 + sizeX1) <= (x2 + sizeX2)) {
				return true;
			}
			else if (x1 >= x2 && x1 <= (x2 + sizeX2)) {
				return true;
			}
			else if ((x1 + sizeX1) >= x2 && (x1 + sizeX1) <= (x2 + sizeX2)) {
				return true;
			}
		}
		if (y1 >= y2 && y1 <= (y2 + sizeY2)) {
			if (x1 >= x2 && x1 <= (x2+sizeX2)) {
				return true;
			}
			else if ((x1 + sizeX1) >= x2 && (x1 + sizeX1) <= (x2 + sizeX2)) {
				return true;
			}

		}
		if ((y1 + sizeY1) >= y2 && (y1 + sizeY1) <= (y2 + sizeY2)) {
			if (x1 >= x2 && x1 <= (x2 + sizeX2)) {
				return true;
			}
			else if ((x1 + sizeX1) >= x2 && (x1 + sizeX1) <= (x2 + sizeX2)) {
				return true;
			}
		}
		if (x1 >= x2 && x1 <= (x2 + sizeX2)) {
			if (y1 >= y2 && y1 <= (y2 + sizeY2)) {
				return true;
			}
			else if ((y1 + sizeY1) >= y2 && (y1 + sizeY1) <= (y2 + sizeY2)) {
				return true;
			}
		}
		if ((x1 + sizeX1) >= x2 && (x1 + sizeX1) <= (x2 + sizeX2)) {
			if (y1 >= y2 && y1 <= (y2 + sizeY2)) {
				return true;
			}
			else if((y1 + sizeY1) >= y2 && (y1 + sizeY1) <= (y2 + sizeY2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Processes a particular model according to the processId
	 * @param processId The processes that will handle the model
	 * @param model	model to be processed
	 */
	private void processObject(int processId, DShapeModel model) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {

				if (processId == ADD_CODE)
					addModel(model);
				else if (processId == REMOVE_CODE)
					removeModel(model);
				else if (processId == UPDATE_CODE)
					updateModel(model);
				else if (processId == FRONT_CODE)
					moveFront(model);
				else if (processId == BACK_CODE)
					moveBack(model);
			}
		});
	}

	public DShapeTableModel getDShapeTableModel() {
		return dShapeTableModel;
	}

	public static void main(String args[]) {
		WhiteBoard wb = new WhiteBoard();
	}
	public class Server extends Thread {

		private List<ObjectOutputStream> out = new ArrayList<ObjectOutputStream>();
		private int iPort;

		public Server(int iPort) {       
			setPort (iPort);
		}

		public void run() {
			try {
				ServerSocket serverSocket = new ServerSocket(iPort);

				while (true) {
					Socket toClient = null;

					toClient = serverSocket.accept();

					ObjectOutputStream oos = new ObjectOutputStream(toClient.getOutputStream());
					addOutput(oos);
					new Thread(new Runnable() {

						@Override
						public void run() {

							int size = listOfModel.size();
							for(int i = size - 1; i >= 0; i--)
							{
								DShapeModel m = listOfModel.get(i);
								sendData(ADD_CODE, m, oos);
							}

						}

					}).start();

				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		public synchronized void addOutput(ObjectOutputStream out) {
			this.out.add(out);
		}

		public synchronized ObjectOutputStream getOutput(int index) {
			return out.get(index);
		}

		public synchronized int getOutputListSize() {
			return out.size();
		}

		public synchronized void sendData(int actionId, Object data) {   
			Iterator<ObjectOutputStream> iterator = out.iterator();
			while (iterator.hasNext()) 
			{
				try {
					FileHandler<Object> fh = new FileHandler<Object>();
					ObjectOutputStream o = iterator.next();
					o.writeObject(fh.getXMLString(actionId));
					o.flush();
					o.writeObject(fh.getXMLString(data));
					o.flush();
				}
				catch (Exception e) {
					e.printStackTrace();
					iterator.remove();
				}
			}
		}

		private synchronized void sendData(int actionId, Object data, ObjectOutputStream o) {   


			try {
				FileHandler<Object> fh = new FileHandler<Object>();
				o.writeObject(fh.getXMLString(actionId));
				o.flush();
				o.writeObject(fh.getXMLString(data));
				o.flush();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		private void setPort(int iPort) {
			this.iPort = iPort;
		}

		public void postMessage(String message) {
			final String TALKING = "[Server/]: ";
			System.out.println(TALKING + message);
		}

		public void postError(String message) {
			final String TALKING = "[Server/]: ";
			System.err.println(TALKING + message);
		}

	}


	public class Client extends Thread {
		private String name;
		private int port;

		Client(String name, int port) {
			this.name = name;
			this.port = port;
		}

		@Override
		public void run() {
			try {
				Socket toServer = new Socket(name, port);
				ObjectInputStream in = new ObjectInputStream(toServer.getInputStream());

				while (true) {
					String xmlString = (String) in.readObject();

					XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlString.getBytes()));
					int processId = (int) decoder.readObject();

					xmlString = (String) in.readObject();
					decoder = new XMLDecoder(new ByteArrayInputStream(xmlString.getBytes()));
					DShapeModel data = (DShapeModel) decoder.readObject();

					if (data != null)
						processObject(processId, data);
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
