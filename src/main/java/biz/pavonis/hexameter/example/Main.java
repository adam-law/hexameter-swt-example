package biz.pavonis.hexameter.example;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import biz.pavonis.hexameter.Hexagon;
import biz.pavonis.hexameter.HexagonGridLayout;
import biz.pavonis.hexameter.HexagonOrientation;
import biz.pavonis.hexameter.HexagonalGrid;
import biz.pavonis.hexameter.HexagonalGridBuilder;
import biz.pavonis.hexameter.Point;
import biz.pavonis.hexameter.exception.HexagonNotFoundException;

public class Main {

	static HexagonalGrid hexagonGrid;
	private static final int DEFAULT_GRID_WIDTH = 7;
	private static final int DEFAULT_GRID_HEIGHT = 7;
	private static final int DEFAULT_RADIUS = 30;
	private static final HexagonOrientation DEFAULT_ORIENTATION = HexagonOrientation.POINTY;

	// since this is just a demo i did not bother to create its own object...
	private static int gridWidth = DEFAULT_GRID_WIDTH;
	private static int gridHeight = DEFAULT_GRID_HEIGHT;
	private static int radius = DEFAULT_RADIUS;
	private static HexagonOrientation orientation = DEFAULT_ORIENTATION;
	private static HexagonGridLayout hexagonGridLayout = HexagonGridLayout.RECTANGULAR;
	private static boolean showNeighbors = false;
	private static boolean showMovementRange = false;
	private static Hexagon prevSelected = null;
	private static Hexagon currSelected = null;
	private static int movementRange;
	private static Font font;
	private static int fontSize;

	/**
	 * Simple sample usage of the hexameter framework.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		final Shell shell = new Shell(display);

		// params for grid
		final int shellWidth = 1280;
		final int shellHeight = 768;
		final int canvasWidth = 1000;

		// containers
		shell.setSize(shellWidth, shellHeight);
		GridLayout gl_shell = new GridLayout(2, false);
		gl_shell.horizontalSpacing = 0;
		gl_shell.verticalSpacing = 0;
		gl_shell.marginWidth = 0;
		gl_shell.marginHeight = 0;
		shell.setLayout(gl_shell);
		final Canvas canvas = new Canvas(shell, SWT.NONE);
		canvas.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
			}
		});
		GridData gd_canvas = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_canvas.minimumWidth = canvasWidth;
		canvas.setLayoutData(gd_canvas);
		canvas.setLayout(new GridLayout(1, false));
		final Group grpControls = new Group(shell, SWT.NONE);
		grpControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_grpControls = new GridLayout(2, false);
		gl_grpControls.marginHeight = 0;
		grpControls.setLayout(gl_grpControls);
		grpControls.setText("Controls:");

		// pointy radio
		final Button radioPointy = new Button(grpControls, SWT.RADIO);
		radioPointy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (radioPointy.getSelection()) {
					orientation = HexagonOrientation.POINTY;
					regenerateHexagonGrid(canvas);
				}
			}
		});
		radioPointy.setSelection(true);
		radioPointy.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		radioPointy.setText("Pointy");

		// flat radio
		final Button radioFlat = new Button(grpControls, SWT.RADIO);
		radioFlat.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		radioFlat.setText("Flat");
		radioFlat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (radioFlat.getSelection()) {
					orientation = HexagonOrientation.FLAT;
					regenerateHexagonGrid(canvas);
				}
			}
		});

		// layout
		Label lblLayout = new Label(grpControls, SWT.NONE);
		lblLayout.setText("Layout");
		final Combo layoutCombo = new Combo(grpControls, SWT.NONE);
		for (HexagonGridLayout layout : HexagonGridLayout.values()) {
			layoutCombo.add(layout.name());
		}
		layoutCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				hexagonGridLayout = HexagonGridLayout.valueOf(layoutCombo.getText());
				regenerateHexagonGrid(canvas);
			}
		});
		layoutCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// grid width
		Label lblNewLabel = new Label(grpControls, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText("Grid width");
		final Spinner gridWidthSpinner = new Spinner(grpControls, SWT.BORDER);
		gridWidthSpinner.setSelection(gridWidth);
		gridWidthSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gridWidth = gridWidthSpinner.getSelection();
				regenerateHexagonGrid(canvas);
			}

		});

		// grid height
		Label lblGridy = new Label(grpControls, SWT.NONE);
		lblGridy.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblGridy.setText("Grid height");
		final Spinner gridHeightSpinner = new Spinner(grpControls, SWT.BORDER);
		gridHeightSpinner.setSelection(gridHeight);
		gridHeightSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gridHeight = gridHeightSpinner.getSelection();
				regenerateHexagonGrid(canvas);
			}

		});

		// radius
		Label lblRadius = new Label(grpControls, SWT.NONE);
		lblRadius.setText("Radius");
		final Spinner radiusSpinner = new Spinner(grpControls, SWT.BORDER);
		radiusSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				radius = radiusSpinner.getSelection();
				regenerateHexagonGrid(canvas);
			}

		});
		radiusSpinner.setSelection(radius);

		Label lblMovementRange = new Label(grpControls, SWT.NONE);
		lblMovementRange.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMovementRange.setText("Movement range");

		final Spinner movementRangeSpinner = new Spinner(grpControls, SWT.BORDER);
		movementRangeSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				movementRange = movementRangeSpinner.getSelection();
				canvas.redraw();
			}

		});

		// toggle neighbors
		final Button toggleNeighborsCheck = new Button(grpControls, SWT.CHECK);
		toggleNeighborsCheck.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				showNeighbors = toggleNeighborsCheck.getSelection();
				canvas.redraw();
			}
		});
		toggleNeighborsCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		toggleNeighborsCheck.setText("Toggle neighbors");

		final Button toggleMovementRangeCheck = new Button(grpControls, SWT.CHECK);
		toggleMovementRangeCheck.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		toggleMovementRangeCheck.setText("Toggle movement range");
		toggleMovementRangeCheck.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				showMovementRange = toggleMovementRangeCheck.getSelection();
				canvas.redraw();
			}
		});

		// reset button
		Button resetButton = new Button(grpControls, SWT.NONE);
		resetButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		resetButton.setText("Reset");

		// position of mouse
		Label lblXPosition = new Label(grpControls, SWT.NONE);
		lblXPosition.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblXPosition.setText("X position:");

		final Text xPositionText = new Text(grpControls, SWT.BORDER);
		xPositionText.setEditable(false);
		xPositionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblYPosition = new Label(grpControls, SWT.NONE);
		lblYPosition.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblYPosition.setText("Y position:");

		final Text yPositionText = new Text(grpControls, SWT.BORDER);
		yPositionText.setEditable(false);
		yPositionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblDistance = new Label(grpControls, SWT.NONE);
		lblDistance.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDistance.setText("Distance");

		final Text distanceText = new Text(grpControls, SWT.BORDER);
		distanceText.setEditable(false);
		distanceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_1 = new Label(grpControls, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblNewLabel_1.setText("(between last 2 selected)");

		resetButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				resetFields();
				resetControls();
				regenerateHexagonGrid(canvas);
			}

			private void resetFields() {
				orientation = DEFAULT_ORIENTATION;
				gridHeight = DEFAULT_GRID_HEIGHT;
				gridWidth = DEFAULT_GRID_WIDTH;
				radius = DEFAULT_RADIUS;
				showNeighbors = false;
				showMovementRange = false;
				hexagonGridLayout = HexagonGridLayout.RECTANGULAR;
				prevSelected = null;
				currSelected = null;
				movementRange = 0;
			}

			private void resetControls() {
				radioPointy.setSelection(true);
				radioFlat.setSelection(false);
				gridHeightSpinner.setSelection(DEFAULT_GRID_HEIGHT);
				gridWidthSpinner.setSelection(DEFAULT_GRID_WIDTH);
				radiusSpinner.setSelection(DEFAULT_RADIUS);
				toggleNeighborsCheck.setSelection(false);
				toggleMovementRangeCheck.setSelection(false);
				distanceText.setText("");
				movementRangeSpinner.setSelection(0);
			}
		});

		canvas.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				xPositionText.setText(e.x + "");
				yPositionText.setText(e.y + "");
			}
		});

		// darawing
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				Hexagon hex = null;
				try {
					hex = hexagonGrid.getByPixelCoordinate(e.x, e.y);
				} catch (HexagonNotFoundException ex) {
					ex.printStackTrace();
				}
				if (hex != null) {
					prevSelected = currSelected;
					currSelected = hex;
					drawDistance();
					SatelliteData data = hex.<SatelliteData> getSatelliteData();
					if (data == null) {
						data = new SatelliteData();
					}
					data.setSelected(!data.isSelected());
					hex.<SatelliteData> setSatelliteData(data);
				}
				canvas.redraw();
			}

			private void drawDistance() {
				if (prevSelected != null) {
					distanceText.setText(hexagonGrid.calculateDistanceBetween(prevSelected, currSelected) + "");
				}
			}
		});
		canvas.addPaintListener(new PaintListener() {
			Color darkBlue = shell.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE);
			Color yellow = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
			Color gray = shell.getDisplay().getSystemColor(SWT.COLOR_GRAY);
			Color darkGray = shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
			Color black = shell.getDisplay().getSystemColor(SWT.COLOR_BLACK);
			Color magenta = shell.getDisplay().getSystemColor(SWT.COLOR_MAGENTA);

			public void paintControl(PaintEvent e) {
				e.gc.setLineWidth(2);
				e.gc.setForeground(darkBlue);
				e.gc.setBackground(yellow);
				e.gc.fillRectangle(new Rectangle(0, 0, shellWidth, shellHeight));

				for (List<Hexagon> hexagons : hexagonGrid.getHexagons()) {
					for (Hexagon hexagon : hexagons) {
						SatelliteData data = hexagon.<SatelliteData> getSatelliteData();
						if (data != null && data.isSelected()) {
							drawFilledHexagon(shell, e, hexagon);
						} else {
							drawEmptyHexagon(shell, e, hexagon);
						}
					}
				}
			}

			private void drawEmptyHexagon(final Shell shell, PaintEvent e, Hexagon hexagon) {
				e.gc.setForeground(darkBlue);
				e.gc.setBackground(yellow);
				e.gc.drawPolygon(convertToPointsArr(hexagon.getPoints()));
				drawCoordinates(e, hexagon);
			}

			private void drawFilledHexagon(final Shell shell, PaintEvent e, Hexagon hexagon) {
				e.gc.setForeground(yellow);
				e.gc.setBackground(darkBlue);
				e.gc.fillPolygon(convertToPointsArr(hexagon.getPoints()));
				drawCoordinates(e, hexagon);
				if (showNeighbors) {
					for (Hexagon hex : hexagonGrid.getNeighborsOf(hexagon)) {
						drawNeighborHexagon(shell, e, hex);
					}
				}
				if (showMovementRange) {
					for (Hexagon hex : hexagonGrid.calculateMovementRangeFrom(hexagon, movementRange)) {
						drawMovementRangeHexagon(shell, e, hex);
					}
				}
			}

			private void drawNeighborHexagon(final Shell shell, PaintEvent e, Hexagon hexagon) {
				e.gc.setForeground(gray);
				e.gc.setBackground(darkGray);
				e.gc.fillPolygon(convertToPointsArr(hexagon.getPoints()));
				drawCoordinates(e, hexagon);
			}

			private void drawMovementRangeHexagon(Shell shell, PaintEvent e, Hexagon hexagon) {
				e.gc.setForeground(black);
				e.gc.setBackground(magenta);
				e.gc.fillPolygon(convertToPointsArr(hexagon.getPoints()));
				drawCoordinates(e, hexagon);
			}

			private void drawCoordinates(PaintEvent e, Hexagon hexagon) {
				e.gc.setFont(font);
				e.gc.drawText("x:" + hexagon.getGridX(), (int) hexagon.getCenterX() - fontSize, (int) (hexagon.getCenterY() - fontSize * 2.5));
				e.gc.drawText("y:" + hexagon.getGridY(), (int) (hexagon.getCenterX() - fontSize * 2.8), (int) hexagon.getCenterY() + fontSize / 3);
				int z = -(hexagon.getGridX() + hexagon.getGridY());
				e.gc.drawText("z:" + z, (int) (hexagon.getCenterX()), (int) hexagon.getCenterY() + fontSize / 3);
			}

			private int[] convertToPointsArr(Point[] points) {
				int[] pointsArr = new int[12];
				int i = 0;
				for (Point point : points) {
					pointsArr[i] = (int) Math.round(point.x);
					pointsArr[i + 1] = (int) Math.round(point.y);
					i += 2;
				}
				return pointsArr;
			}
		});

		// fire it up
		regenerateHexagonGrid(canvas);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private static void regenerateHexagonGrid(Canvas canvas) {
		FontData fd = canvas.getDisplay().getSystemFont().getFontData()[0];
		fontSize = (int) (radius / 3.5);
		font = new Font(canvas.getDisplay(), fd.getName(), fontSize, SWT.NONE);
		hexagonGrid = new HexagonalGridBuilder().setGridWidth(gridWidth).setGridHeight(gridHeight).setRadius(radius).setOrientation(orientation).setGridLayout(hexagonGridLayout)
				.build();
		// System.out.println(hexagonGrid);
		canvas.redraw();
	}
}
