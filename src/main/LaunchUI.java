package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Vector;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LaunchUI extends Application{

	ArrayList<Polygon> plys = new ArrayList<Polygon>();

	int currentPoly = 0;//keeps track of polygons being made
	boolean polyClosed=false;//is the current polygon complete yet?

	//how many dimensionless units we want to subdivide our grid into
	int xParts = 100; 
	int yParts = 70;

	//right now, the grid has a defined size. Can be scalable in a later release
	//basically make each rectangle 10 pixels
	int paneWidth = 10*xParts;
	int paneHeight = 10*yParts;

	VBox input = new VBox();
	HBox output = new HBox();
	BorderPane bpMaster = new BorderPane();
	HBox title = new HBox();
	BorderPane grid = new BorderPane();
	Pane selectPane = new Pane();
	GridPane gp = new GridPane();
	HBox xAxis = new HBox();
	VBox yAxisBuffer = new VBox();
	VBox yAxis = new VBox();
	Label outputText = new Label();

	@Override
	public void start(Stage stage) throws Exception {
		//Layout the layouts
		title.setAlignment(Pos.CENTER);
		input.setAlignment(Pos.CENTER);
		output.setAlignment(Pos.CENTER);
		selectPane.getChildren().add(gp);
		grid.setCenter(selectPane);
		grid.setBottom(xAxis);
		grid.setLeft(yAxis);
		xAxis.setAlignment(Pos.BOTTOM_CENTER);
		yAxis.setAlignment(Pos.BASELINE_LEFT);
		BorderPane.setMargin(yAxis, new Insets(0,2,0,20));
		BorderPane.setMargin(input, new Insets(0,0,0,30));
		BorderPane.setMargin(grid, new Insets(0,30,0,0));

		bpMaster.setCenter(grid);
		bpMaster.setLeft(input);
		bpMaster.setBottom(output);
		bpMaster.setTop(title);


		//Initialize the grid ----- this takes a long ass time to make 100 rectangles
		Rectangle[][] cells = new Rectangle[xParts][yParts];
		for (int i = 0; i <= xParts-1; i++) {
			for (int j = 0; j<= yParts-1; j++){
				Rectangle temp = new Rectangle((paneWidth-100)/xParts,(paneHeight-100)/yParts, Color.WHITE);
				temp.setStroke(Color.GRAY);
				temp.setStrokeWidth(0.1);
				cells[i][j] = temp;
				gp.add(cells[i][j], i, j);
			}
		}

		//Display title
		Text header = new Text("Level Builder");
		header.setStyle("-fx-font-size: 36");
		header.setTextAlignment(TextAlignment.CENTER);
		title.getChildren().add(header);

		//Display axes
		yAxis.setSpacing(8.6*(paneHeight-100)/yParts);//title height plus 10 rectangles
		for (int i = 0; i <= yParts; i=i+10){
			int tempInt = yParts-i;
			Text temp = new Text(""+tempInt);
			temp.setTranslateY(-6);
			yAxis.getChildren().add(temp);
		}

		xAxis.setSpacing(9.2*(paneWidth-100)/xParts);//random ratio that seemed to work OK
		for (int i = 0; i <= xParts; i=i+10){
			Text temp = new Text("" + i);
			temp.setTranslateX(15);
			xAxis.getChildren().add(temp);
		}

		//Display output
		outputText.setTextAlignment(TextAlignment.CENTER);
		outputText.setMinHeight(50);
		output.getChildren().add(outputText);

		//Collect Input
		Text polyIndexBox = new Text("Enter starting index for\npolygon numbering. (default 0)");
		polyIndexBox.setTextAlignment(TextAlignment.CENTER);
		TextField polyIndexField = new TextField();
		Label filler0 = new Label("\n\n\n\n\n\n");
		Text xTextBox = new Text("Enter x coordinate \nof point");
		xTextBox.setTextAlignment(TextAlignment.CENTER);
		TextField xTextField = new TextField();
		Label filler1 = new Label("\n");
		Text yTextBox = new Text("Enter y coordinate \nof point");
		yTextBox.setTextAlignment(TextAlignment.CENTER);
		TextField yTextField = new TextField();
		Label filler2 = new Label("\n");
		Text hTextBox = new Text("Enter height (going down)\n of the rectangle");
		hTextBox.setTextAlignment(TextAlignment.CENTER);
		TextField hTextField = new TextField();
		Label filler3 = new Label("\n");
		Text wTextBox = new Text("Enter width (going right)\n of the rectangle");
		wTextBox.setTextAlignment(TextAlignment.CENTER);
		TextField wTextField = new TextField();
		Label filler4 = new Label("\n");

		Button addPt = new Button("Insert Point");
		Label filler5 = new Label("\n\n\n");
		Button export = new Button("Export Geometry");
		Label filler6 = new Label("\n\n\n\n\n\n\n\n\n\n\n");
		Button reset = new Button("Reset");
		Label filler7 = new Label("\n");
		Button undo = new Button("Undo");

		input.getChildren().addAll(polyIndexBox, polyIndexField, filler0, xTextBox, xTextField, filler1, yTextBox, yTextField,
				filler2, addPt, filler5, export, filler7, undo, filler6, reset);

		//Button Event Handlers
		Vector<Rectangle> recVec = new Vector<Rectangle>();

		addPt.setOnAction(e1 -> {
			int xPos = Integer.parseInt(xTextField.getText());
			int yPos = Integer.parseInt(yTextField.getText());
			
			xPos= xPos*xParts/paneWidth;
			yPos =(paneHeight - yPos)*yParts/paneHeight;

			//recVec.add(new Rectangle((double) xPos, (double) yPos,
			//		Double.parseDouble(wTextField.getText()),
			//		Double.parseDouble(hTextField.getText())));

			addPolyPoint(xPos, yPos);

			xTextField.clear();
			yTextField.clear();
		});

		export.setOnAction(e2 -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Choose a Java file to write coordinates to.");
			fc.setInitialDirectory(new File("/home/kyle/git/newtonian/core/src/com/jello/newtonian"));
			File dest = fc.showOpenDialog(stage);

			//try to get the desired poly start index from the box.
			//If theres nothing there or it's unreadable, just go with zero.
			int pnum = 0;
			if(!polyIndexField.getText().equals("")){
				try{
					pnum=Integer.parseInt(polyIndexField.getText());
					if(pnum<0) pnum=0;
				}catch(NumberFormatException e){
					pnum = 0;
				}
			}
			writeToTags(dest.getAbsolutePath(), pnum);

		});

		reset.setOnAction(e3 -> {
			xTextField.clear();
			yTextField.clear();
			hTextField.clear();
			wTextField.clear();

			/*for (int i = 0; i <= 19; i++) {
				for (int j = 0; j<= 19; j++){
					cells[i][j].setFill(Color.WHITE);
					//cells[i][j].setStroke(Color.BLACK);
				}
			}
			 */

			selectPane.getChildren().clear();
			selectPane.getChildren().add(gp);
			plys.clear();
			currentPoly=0;
			plys.add(new Polygon());
			outputText.setText("");
			recVec.clear();
		});

		undo.setOnAction(e4 -> {
			//only undo if there is something to undo
			if(!plys.isEmpty()){

				//if there happens to be an empty one on the end of the list, take it out too
				if(plys.get(plys.size()-1).getPoints().isEmpty()){
					plys.remove(plys.size()-1);
					currentPoly-=1;
				}

				//take out partial polygon, make sure taking out a blank one didn't empty it
				if(!plys.isEmpty()){
					plys.remove(plys.size()-1);
					plys.add(new Polygon());
					outputText.setText("");
				}
				//clear screen
				selectPane.getChildren().clear();
				selectPane.getChildren().add(gp);

				boolean set = false;//flag to take the numbers in pairs

				//loop through and re-add everything that didn't get deleted
				for(Polygon p : plys){

					//this holds the previous number extracted
					int prevD = -1;

					for(double d : p.getPoints()){

						if(set){
							//draw a dot wherever we create a new point
							Circle dot = new Circle(prevD, (int)d, 3);
							dot.setStroke(Color.CADETBLUE);
							dot.setFill(Color.CADETBLUE);
							selectPane.getChildren().add(dot);

							set=false;
						}else{
							set=true;
						}
						prevD=(int)d;
					}
					//paint the inside of the polygon
					p.setStroke(Color.BLACK);
					p.setFill(Color.BLANCHEDALMOND);
					selectPane.getChildren().add(p);
				}
			}
		});

		//mouse listener
		selectPane.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent me) {
				if(me.getSource().equals(selectPane)){
					double roundX = me.getX();
					double roundY = me.getY();
					addPolyPoint(roundX, roundY);
				}
			}
		});

		//Create Scene and stage and display and stuff
		Scene scene = new Scene(bpMaster);
		stage.setTitle("Newtonian Level Builder");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {launch(args);}

	public double InertiaCalc(Rectangle[] rectangleArray){
		int length = rectangleArray.length;
		double[] Yarray = new double [length];
		double Ytop = 0;
		double[] widtharray = new double [length];
		double[] heightarray = new double [length];
		double[] areaarray = new double [length];
		double NAnum = 0;
		double NAdenom = 0;
		double Inertia = 0;

		for(int i = 0; i <= (length-1); i++){
			Yarray[i] = rectangleArray[i].getY();

			if( Ytop < Yarray[i]){
				Ytop = Yarray[i];
			}

			heightarray[i] = rectangleArray[i].getHeight();
			widtharray[i] = rectangleArray[i].getWidth();
			areaarray[i] = (heightarray[i]*widtharray[i]);
		}

		for(int i = 0; i<= (length-1); i++){
			NAnum = NAnum + areaarray[i]*(Ytop-Yarray[i]+heightarray[i]/2);
			NAdenom = NAdenom +areaarray[i];
		}

		double YNeutralAxis = Ytop-NAnum/NAdenom;

		for(int i = 0; i<= (length-1); i++){
			double deltaY = YNeutralAxis - (Yarray[i]-heightarray[i]/2);
			Inertia = Inertia + (0.0833333333333333333333333333333)*
					areaarray[i]*(heightarray[i])*(heightarray[i]) + areaarray[i]*deltaY*deltaY;
		}
		return Inertia;
	}

	private void addPolyPoint(double roundX, double roundY){
		//round the input to the nearest grid unit. 
		//x
		
		if(roundX%(gp.getWidth()/xParts) <= (gp.getWidth()/xParts)/2){
			roundX -= roundX%(gp.getWidth()/xParts);
		}else{
			roundX += gp.getWidth()/xParts-roundX%(gp.getWidth()/xParts);
		}
		//y
		if(roundY%(gp.getHeight()/yParts) <= (gp.getHeight()/yParts)/2){
			roundY -= roundY%(gp.getWidth()/yParts);
		}else{
			roundY += gp.getHeight()/yParts - roundY%(gp.getHeight()/yParts);
		}
		
		//if we don't have any polygons made yet, make a new one
		if(plys.isEmpty()) plys.add(new Polygon());

		//check to see if the most recent polygon was closed off. If so, start a new one.
		if(polyClosed){
			plys.add(new Polygon());
			currentPoly++;
			polyClosed = false;
		}


		//add the coords we just received to current polygon (points stored as x1,y1,x2,y2,x3,y3...)
		plys.get(currentPoly).getPoints().add(roundX);
		plys.get(currentPoly).getPoints().add(roundY);

		//draw a dot where our new point is
		Circle dot = new Circle(roundX, roundY, 3);
		dot.setStroke(Color.CADETBLUE);
		dot.setFill(Color.CADETBLUE);
		selectPane.getChildren().add(dot);

		//reset the status text on the bottom of the screen
		outputText.setText("");	



		//if this is at least the second point, draw lines and check for the end. (size() = 2 = 1x+1y)
		if (plys.get(currentPoly).getPoints().size()>2){

			//check if this point is very close to the first point.
			if((Math.abs(roundX-plys.get(currentPoly).getPoints().get(0)) < 5 &&
					Math.abs(roundY-plys.get(currentPoly).getPoints().get(1)) < 5)) {

				//if so, remove it and just close off the polygon instead

				//get current poly-------extract verts-------remove the two vertices on the end----------
				plys.get(currentPoly).getPoints().remove(plys.get(currentPoly).getPoints().size()-2);
				plys.get(currentPoly).getPoints().remove(plys.get(currentPoly).getPoints().size()-1);

				//since we closed a new polygon, tell the user
				outputText.setText("new polygon created");	

				//paint the line between the first point and the previous
				double plX_old=plys.get(currentPoly).getPoints().get(plys.get(currentPoly).getPoints().size()-2);
				double plY_old=plys.get(currentPoly).getPoints().get(plys.get(currentPoly).getPoints().size()-1);
				Line l1 = new Line(plX_old,plY_old,
						plys.get(currentPoly).getPoints().get(0),
						plys.get(currentPoly).getPoints().get(1));
				selectPane.getChildren().add(l1);

				//paint the inside of the polygon
				plys.get(currentPoly).setStroke(Color.BLACK);
				plys.get(currentPoly).setFill(Color.BLANCHEDALMOND);
				selectPane.getChildren().add(plys.get(currentPoly));

				//indicate that we've closed off a new polygon
				polyClosed = true;
			}else{
				//for the case where this was not the last point, just paint the line
				double plX_old=plys.get(currentPoly).getPoints().get(plys.get(currentPoly).getPoints().size()-4);
				double plY_old=plys.get(currentPoly).getPoints().get(plys.get(currentPoly).getPoints().size()-3);
				Line l1 = new Line(plX_old,plY_old,roundX,roundY);
				selectPane.getChildren().add(l1);
			}
		}
		//if the user has made 8 vertices and has not closed it off, close it forcefully
		//8 vertices is the max for a Box2D polygon.
		if(plys.get(currentPoly).getPoints().size()>=16 && !polyClosed){
			//paint the line between the first point and the previous
			double plX_old=plys.get(currentPoly).getPoints().get(plys.get(currentPoly).getPoints().size()-2);
			double plY_old=plys.get(currentPoly).getPoints().get(plys.get(currentPoly).getPoints().size()-1);
			Line l1 = new Line(plX_old,plY_old,
					plys.get(currentPoly).getPoints().get(0),
					plys.get(currentPoly).getPoints().get(1));
			selectPane.getChildren().add(l1);

			//paint the inside of the polygon
			plys.get(currentPoly).setStroke(Color.BLACK);
			plys.get(currentPoly).setFill(Color.BLANCHEDALMOND);
			selectPane.getChildren().add(plys.get(currentPoly));

			//indicate that we've closed off a new polygon
			polyClosed = true;

			outputText.setText("max vertices reached (8). Auto-closed polygon");
		}

	}
	/**
	 * Exports the drawn polygons into a selected java file. The file MUST contain the three tags:
	 * 
	 * #body-defs                     -place directly after class declaration (these are class variables)
	 * #element-spawn-methods         -put this in the "create" method. These call the third tag methods
	 * #method-declarations           -writes the actual method that draw things. Put this wherever you could normally declare methods
	 * 
	 * in that order. This will write the polygons into the java file so that the polygons become static Box2D physics objects
	 * .
	 * @param original The file that contains the tagged java code
	 * @param pnum The desired starting index for the polygon numbering in the file.
	 */
	public void writeToTags(String original, int pnum){

		try {
			//open a reader on the original file
			FileReader fr = new FileReader(original);
			BufferedReader br = new BufferedReader(fr);
			
			//open a writer on the temporary construction file in the same directory
			File tempDest = new File(original.substring(0, original.lastIndexOf("/")+1)+"print_tester.txt");
			System.out.println(tempDest.getAbsolutePath());
			FileWriter fw = new FileWriter(tempDest);
			BufferedWriter bw = new BufferedWriter(fw);
			
			//some re-usable variables
			String tempLine;
			boolean notFound;
			int pnumI;
			
			//copy directly until the tag - #body-defs
			pnumI = pnum;
			notFound = true;
			while(notFound && (tempLine = br.readLine()) != null){
				bw.write(tempLine);
				bw.newLine();
				System.out.println(tempLine);
				tempLine = tempLine.trim();//trim away tabs
				//when we find the tag, print the method calls to the temp file

				if(tempLine.equals("//#body-defs")){
					
					for(Polygon p : plys){
						bw.write("\n\tBody poly"+pnumI+";");
						pnumI++;
					}
					notFound = false;
				}
			}

			//copy directly until the tag - #element-spawn-methods
			pnumI = pnum;
			notFound = true;
			while(notFound && (tempLine = br.readLine()) != null){
				bw.write(tempLine);
				bw.newLine();
				System.out.println(tempLine);
				tempLine = tempLine.trim();//trim away tabs
				//when we find the tag, print the method calls to the temp file

				if(tempLine.equals("//#element-spawn-methods")){
					for(Polygon p : plys){
						bw.write("\n\tspawnStaticPoly"+pnumI+"();");
						pnumI++;
					}
					notFound = false;
				}
			}
			
			//copy directly until the tag - #method-declarations
			pnumI = pnum;
			notFound = true;
			while(notFound && (tempLine = br.readLine()) != null){
				bw.write(tempLine);
				bw.newLine();
				System.out.println(tempLine);
				tempLine = tempLine.trim();//trim away tabs
				//when we find the tag, print the method declarations to the temp file
				if(tempLine.equals("//#method-declarations")){
					for(Polygon p : plys){
						String vertices="";
						boolean isX=true;
						
						//transform (pixel-> units) & (coordsA->coordsB)
						for(double d : p.getPoints()){
							if(isX) d= d*xParts/paneWidth;
							else d=(paneHeight - d)*yParts/paneHeight;
							vertices += d + "f,";
							isX=!isX;
						}
						//remove the trailing comma
						if(vertices.length()>1){
							vertices = vertices.substring(0, vertices.length()-1);
						}

						//write the source code to the temp file
						bw.write("\nprivate void spawnStaticPoly"+pnumI+"(){\n"
								+ "\tBodyDef poly"+pnumI+"Def = new BodyDef();\n"
								+ "\tpoly"+pnumI+" = level1.createBody(poly"+pnumI+"Def);//needs to be a class var\n"
								+ "\tPolygonShape p"+pnumI+" = new PolygonShape();\n"
								+ "\tfloat[] vertices = {"+vertices+"};\n"
								+ "\tp"+pnumI+".set(vertices);\n"
								+ "\tpoly"+pnumI+".createFixture(p"+pnumI+", 0.0f);\n"
								+ "\tp"+pnumI+".dispose();\n}\n\n");
						pnumI++;
					}
					notFound=false;
				}
			}
			
			//finish off the file
			while((tempLine = br.readLine()) != null){
				bw.write(tempLine);
				bw.newLine();
			}
			
			//close our reader and writer
			//!!!!The buffered writer only actually writes to the file when you call bw.close() and it flushes the buffer!!!!
			br.close();
			bw.close();
			
			//we've been writing to a temp file. Rename it and save the old one as FILENAME_old.java
			//ermahgerd this is soooo inefficient. I'm creating temp File objects out the wazoo. If someone reads this, can you fix it?
			File oldFile = new File(original.substring(0, original.lastIndexOf(".java"))+"_old.java");
			boolean rnmStatus1 = new File(original).renameTo(oldFile);//file --> file_old
			boolean rnmStatus2 = tempDest.renameTo(new File(original));//temp --> file
			
			//if something goes wrong, trigger exception
			if (!rnmStatus1 || !rnmStatus2) throw new java.io.IOException();
			
			//notify the user that everything went okay
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Successful");
			alert.setHeaderText(null);
			alert.setContentText("Geometry was successfully transferred to the chosen "
					+ "file as Box2D static objects.");
			alert.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
			
			//notify the user that there was an error
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Ouch");
			alert.setHeaderText(null);
			alert.setContentText("Oops, looks like something went wrong with the export. "
					+ "Are any of the files read or write locked?");
			alert.showAndWait();
		}
		
	}
}

