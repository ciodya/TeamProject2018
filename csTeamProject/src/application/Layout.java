/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

//import packages
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import controller.Area;
import controller.Controller;
import controller.Node;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
public class Layout extends BorderPane {

	@FXML BorderPane border_pane;
	@FXML SplitPane base_pane;
	@FXML AnchorPane right_pane;
	@FXML VBox left_pane;
	@FXML Button generateAlgebraicExpression, clearAlgebraicExpression, exportBigFile, newNetworkButton;
	@FXML TextArea algebraicExpressionDisplay;
	@FXML CheckBox Sensor_windspeed, Sensor_temperature, Sensor_humidity, Sensor_virbration, Sensor_pressure;
	@FXML Label IPV6ID, MACID, NodeID;
	@FXML Label AppList;
	@FXML Label NodeIDinvisible;
	@FXML Button createAppButton, AddAppBtn, DelNode;
	@FXML VBox vBoxForAreaNames;
	@FXML MenuItem fileExit, helpAbout;

	double xMin,xMax,yMin,yMax,xm,ym;//location of the node,location of the mouse
	double xNode, yNode, xAreaMin, xAreaMax, yAreaMin, yAreaMax, xNodeMax, yNodeMax, 
	areaMin_x,areaMin_y, areaMax_x,areaMax_y; //location of node in an area
	String tempIDstore;
	private DragableNode mDragableNodeOver = null;
	private DraggableArea mDragableAreaOver = null;
	private EventHandler<DragEvent> mNodeDragOverRoot = null;
	private EventHandler<DragEvent> mAreaDragOverRoot = null;
	private EventHandler<DragEvent> mNodeDragDropped = null;
	private EventHandler<DragEvent> mAreaDragDropped = null;
	private EventHandler<DragEvent> mNodeDragOverRightPane = null;
	private EventHandler<DragEvent> mAreaDragOverRightPane = null;
	private static ArrayList<DraggableArea> AllAreasCreated; // all areas created on the right_pane
	private ArrayList<DraggableArea> TopAreasCreated; // all top-level areas created on the right_pane
	private ArrayList<DraggableArea> InnerAreasCreated; // all inner areas created on the right_pane
	public static ArrayList<DragableNode> AllNodesCreated; // all nodes created on the right_pane
	public static HashMap<String, Line> allGraphicalLinksCreated = new HashMap<>();
	Boolean wind_speed_value;
	Boolean temperature_value;
	Boolean humidity_value;
	Boolean vibration_value;
	Boolean pressure_value;
	static String TempStoreNodeClickedID;
	// Create new Scene, this will be the new Controller
	Controller newScene = new Controller("Scene");

	public Layout() throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader();
		URL location = getClass().getResource("/fxml/Layout.fxml");
		fxmlLoader.setRoot(this);
		fxmlLoader.setLocation(location);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@FXML
	private void initialize() {
		//add all lines created in a list
		Layout.AllAreasCreated = new ArrayList<DraggableArea>();
		this.TopAreasCreated = new ArrayList<DraggableArea>();
		this.InnerAreasCreated = new ArrayList<DraggableArea>();
		Layout.AllNodesCreated = new ArrayList<DragableNode>();
		// set the action on clicking of clear button
		clearAlgebraicExpression.setOnAction(e -> {
			// clear the existing text
			algebraicExpressionDisplay.setText(null);
			// clear the algebraic expression from the text field and clear the existing
			// text
			algebraicExpressionDisplay.setPromptText("The Algebraic Expression generated will be displayed here.");
		});
		// set the action on clicking of generate button
		generateAlgebraicExpression.setOnAction(e -> {
			// get the bigraph expression
			String expression = newScene.exportBigraph();
			// print the bigraph on the display text area
			algebraicExpressionDisplay.setText(expression);
		});
		// set the action on clicking of export button
		exportBigFile.setOnAction(e -> {
			// export the big file
			newScene.exportBIG();
		});
	     //add app to node in cfg box
		AddAppBtn.setOnAction(event -> {
			try {
					//new cfgAddAppLoader(AppList);
					Stage stageC = new Stage();
					BorderPane Pane = new BorderPane();
					Scene sceneC = new Scene(Pane, 420, 230);
					// block using of any other window of current application
					stageC.initModality(Modality.APPLICATION_MODAL);
					// disable resizing of the area setting dialog box
					stageC.resizableProperty().setValue(Boolean.FALSE);
					stageC.setTitle("Add Application for Node");
					stageC.setScene(sceneC);
					stageC.show();
					// set action on close request of node setting window
					stageC.setOnCloseRequest(closeEventOnNodeWindow -> {
					// take away java's control of close event and don't exit							
						closeEventOnNodeWindow.consume();
					});
					//cfgAddAppLoader CFGaddApp = new cfgAddAppLoader(AppList);
					nodeConfigBox CFGaddApp = new nodeConfigBox(AppList);
					Pane.setCenter(CFGaddApp);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		});
		// set the short hand event handler for create application button
		createAppButton.setOnAction(event -> {
			// exception handling
			try {
				// open create a new application dialog
				new AddApplicationLoader(vBoxForAreaNames);
			} catch (Exception error) {
				// print the stack trace in case of error
				error.printStackTrace();
			}
		});
		// event handler for the new network button - create a new FULL SCREEN WINDOW
//		newNetworkButton.setOnAction(newProjectEvent -> {
//			// open new project
//			openNewProject();
//		});
		newNetworkButton.setVisible(false);
		
		DelNode.setOnAction(event ->{
	        if (NodeIDinvisible.getText()!= "") {
	            try {
	                tempIDstore = NodeIDinvisible.getText();
	                for(DragableNode each : AllNodesCreated) {
	                    if(each.getID()== tempIDstore) {
	                    	deleteLinks(each.getID());
	                        right_pane.getChildren().remove(each);
	                        Controller.removeNode(tempIDstore);
	                        //clear the node's cfg box after the node deleted and update Application List
	                		Sensor_windspeed.setSelected(false);
	                		Sensor_windspeed.setSelected(false);
	                		Sensor_temperature.setSelected(false);
	                		Sensor_humidity.setSelected(false);
	                		Sensor_virbration.setSelected(false);
	                		Sensor_pressure.setSelected(false);
	                		IPV6ID.setText(""); 
	                		MACID.setText(""); 
	                		NodeID.setText(""); 
	                		NodeIDinvisible.setText(""); 
	                		AppList.setText("");
	                		AddApplicationLoader.updateApplicationList();
	                    }
	                }
	            }catch (Exception error) {
	                // print the stack trace in case of error
	                error.printStackTrace();
	            }
	        }
	    });
		
		// Create operator
		AnimatedZoomOperator zoomOperator = new AnimatedZoomOperator();
		// Listen to scroll events
		right_pane.setOnScroll(new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {
				double zoomFactor = 1.5;
				if (event.getDeltaY() <= 0) {
					// zoom out
					zoomFactor = 1 / zoomFactor;
				}
				zoomOperator.zoom(right_pane, zoomFactor, event.getSceneX(), event.getSceneY());
			}
		});
		mouseClickDetectAddCfgbBx(right_pane);
		TempStoreNodeClickedID = "";
		mDragableNodeOver = new DragableNode();
		mDragableNodeOver.id = "mDragableNodeOver";
		mDragableNodeOver.setVisible(false);
		mDragableNodeOver.setOpacity(0.65);
		getChildren().add(mDragableNodeOver);
		mDragableAreaOver = new DraggableArea();
		mDragableAreaOver.setVisible(false);
		mDragableAreaOver.setOpacity(0.65);
		getChildren().add(mDragableAreaOver);
		Label areaLabel = new Label("Area");
		Label nodeLabel = new Label("Node");
		left_pane.getChildren().add(areaLabel);
		DraggableArea area = new DraggableArea();
		addDragDetection(area);
		left_pane.getChildren().add(area);
		left_pane.getChildren().add(nodeLabel);
		DragableNode node = new DragableNode();
		node.id = "icon";
		addDragDetection(node);
		left_pane.getChildren().add(node);
		buildDragHandlers();
		buildDragHandlers2();
		NodeIDinvisible.setVisible(false);  
	}
	private void buildDragHandlers() {
		mNodeDragOverRoot = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				Point2D p = right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());
				if (!right_pane.boundsInLocalProperty().get().contains(p)) {
					event.acceptTransferModes(TransferMode.ANY);
					mDragableNodeOver.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
					return;
				}
				event.consume();
			}
		};
		mNodeDragOverRightPane = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				event.acceptTransferModes(TransferMode.ANY);
				mDragableNodeOver.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
				event.consume();
			}
		};
		mNodeDragDropped = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				DragableContainer container = (DragableContainer) event.getDragboard()
						.getContent(DragableContainer.AddNode);
				container.addData("scene_coordinates", new Point2D(event.getSceneX(), event.getSceneY()));
				ClipboardContent content = new ClipboardContent();
				content.put(DragableContainer.AddNode, container);
				event.getDragboard().setContent(content);
				event.setDropCompleted(true);
			}
		};
		this.setOnDragDone(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				right_pane.removeEventHandler(DragEvent.DRAG_OVER, mNodeDragOverRightPane);
				right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mNodeDragDropped);
				base_pane.removeEventHandler(DragEvent.DRAG_OVER, mNodeDragOverRoot);
				mDragableNodeOver.setVisible(false);
				DragableContainer container = (DragableContainer) event.getDragboard()
						.getContent(DragableContainer.AddNode);
				if (container != null) {
					if (container.getValue("scene_coordinates") != null) {
						DragableNode nodeDropped = new DragableNode();
						right_pane.getChildren().add(nodeDropped);
						Point2D cursorPoint = container.getValue("scene_coordinates");
						nodeDropped.relocateToPoint(new Point2D(cursorPoint.getX(), cursorPoint.getY()));
						boolean nodeAccept = false;
						for (int i = 0; i < AllAreasCreated.size(); i++) {
							if (nodeDropped.getLayoutX() > AllAreasCreated.get(i).getLayoutX()
									&& nodeDropped.getLayoutX() + nodeDropped.circle.getRadius()
											* 2 < AllAreasCreated.get(i).getLayoutX()
													+ AllAreasCreated.get(i).areaWidth()
									&& nodeDropped.getLayoutY() > AllAreasCreated.get(i).getLayoutY()
									&& nodeDropped.getLayoutY() + nodeDropped.circle.getRadius()
											* 2 < AllAreasCreated.get(i).getLayoutY()
													+ AllAreasCreated.get(i).areaHeight()) {
								nodeAccept = true;
								break;
							}
						}
						if (nodeAccept) {
							try {
								Stage nodeWindow = new Stage();
								BorderPane Pane = new BorderPane();
								Scene nodeScene = new Scene(Pane, 400, 250);
								// block using of any other window of current application
								nodeWindow.initModality(Modality.APPLICATION_MODAL);
								// disable resizing of the area setting dialog box
								nodeWindow.resizableProperty().setValue(Boolean.FALSE);
								nodeWindow.setTitle("node Settings");
								nodeWindow.setScene(nodeScene);
								nodeWindow.show();
								// set action on close request of node setting window
								nodeWindow.setOnCloseRequest(closeEventOnNodeWindow -> {
									// take away java's control of close event and don't exit
									closeEventOnNodeWindow.consume();
								});
								nodeProperty node_setting = new nodeProperty();
								Pane.setCenter(node_setting);
								node_setting.saveButton.setOnAction(e -> {
									if (node_setting.windSpeed.isSelected()) {
										wind_speed_value = true;
									} else
										wind_speed_value = false;
									if (node_setting.temperature.isSelected()) {
										temperature_value = true;
									} else
										temperature_value = false;
									if (node_setting.humidity.isSelected()) {
										humidity_value = true;
									} else
										humidity_value = false;
									if (node_setting.vibration.isSelected()) {
										vibration_value = true;
									} else
										vibration_value = false;
									if (node_setting.pressure.isSelected()) {
										pressure_value = true;
									} else
										pressure_value = false;
									AllNodesCreated.add(nodeDropped);
									boolean findParent = false;
									for (int i = 0; i < InnerAreasCreated.size(); i++) {
										if (nodeDropped.getLayoutX() > InnerAreasCreated.get(i).getLayoutX()
												&& nodeDropped.getLayoutX() + nodeDropped.circle.getRadius()
														* 2 < InnerAreasCreated.get(i).getLayoutX()
																+ InnerAreasCreated.get(i).areaWidth()
												&& nodeDropped.getLayoutY() > InnerAreasCreated.get(i)
														.getLayoutY()
												&& nodeDropped.getLayoutY() + nodeDropped.circle.getRadius()
														* 2 < InnerAreasCreated.get(i).getLayoutY()
																+ InnerAreasCreated.get(i).areaHeight()) {
											nodeDropped.id = newScene.addNodeToArea(InnerAreasCreated.get(i).name,
													temperature_value, wind_speed_value, humidity_value,
													vibration_value, pressure_value);
											//set the X coordinate of dropped node
											nodeDropped.xCoord = nodeDropped.getLayoutX();
											//set the X coordinate of dropped node
											nodeDropped.yCoord = nodeDropped.getLayoutY();
											findParent = true;
											break;
										}
									}
									if (!findParent) {
										for (int i = 0; i < TopAreasCreated.size(); i++) {
											if (nodeDropped.getLayoutX() > TopAreasCreated.get(i).getLayoutX()
													&& nodeDropped.getLayoutX() + nodeDropped.circle.getRadius()
															* 2 < TopAreasCreated.get(i).getLayoutX()
																	+ TopAreasCreated.get(i).areaWidth()
													&& nodeDropped.getLayoutY() > TopAreasCreated.get(i)
															.getLayoutY()
													&& nodeDropped.getLayoutY() + nodeDropped.circle.getRadius()
															* 2 < TopAreasCreated.get(i).getLayoutY()
																	+ TopAreasCreated.get(i).areaHeight()) {
												nodeDropped.id = newScene.addNodeToArea(TopAreasCreated.get(i).name,
														temperature_value, wind_speed_value, humidity_value,
														vibration_value, pressure_value);
												//set the X coordinate of dropped node
												nodeDropped.xCoord = nodeDropped.getLayoutX();
												//set the X coordinate of dropped node
												nodeDropped.yCoord = nodeDropped.getLayoutY();
												findParent = true;
												break;
											}
										}
									}
									nodeDropped.label.setText(nodeDropped.id.substring(1, nodeDropped.id.length() - 1));
									nodeWindow.close();
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Stage nodePositionErrorBox = new Stage();
							BorderPane errorPane = new BorderPane();
							Scene nodePositionErrorScene = new Scene(errorPane, 300, 100);
							nodePositionErrorBox.setTitle("Node Position Error ");
							nodePositionErrorBox.setScene(nodePositionErrorScene);
							nodePositionErrorBox.show();
							nodePositionError node_position_error = new nodePositionError();
							errorPane.setCenter(node_position_error);
							right_pane.getChildren().remove(nodeDropped);
						}
					}
				}
				right_pane.removeEventHandler(DragEvent.DRAG_OVER, mAreaDragOverRightPane);
				right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mAreaDragDropped);
				base_pane.removeEventHandler(DragEvent.DRAG_OVER, mAreaDragOverRoot);
				mDragableAreaOver.setVisible(false);
				DragableContainer2 container2 = (DragableContainer2) event.getDragboard()
						.getContent(DragableContainer2.AddArea);
				if (container2 != null) {
					if (container2.getValue("scene_coordinates") != null) {
						DraggableArea areaDropped = new DraggableArea();
						right_pane.getChildren().add(areaDropped);
						Point2D cursorPoint = container2.getValue("scene_coordinates");
						areaDropped.relocateToPoint(new Point2D(cursorPoint.getX(), cursorPoint.getY()));
						try {
							Stage areaWindow = new Stage();
							BorderPane Pane = new BorderPane();
							Scene areaScene = new Scene(Pane, 250, 100);
							// block using of any other window of current application
							areaWindow.initModality(Modality.APPLICATION_MODAL);
							// disable resizing of the area setting dialog box
							areaWindow.resizableProperty().setValue(Boolean.FALSE);
							areaWindow.setTitle("Area Settings");
							areaWindow.setScene(areaScene);
							areaWindow.show();
							// set action on close request of area setting window
							areaWindow.setOnCloseRequest(closeEventOnAreaWindow -> {
								// take away java's control of close event and don't exit
								closeEventOnAreaWindow.consume();
							});
							areaSetting area_setting = new areaSetting();
							Pane.setCenter(area_setting);
							DragResizeMod.makeResizable(areaDropped.rectangle, null);
							area_setting.saveButton.setOnAction(e -> {
								areaDropped.name = area_setting.areaName.getText();
								AllAreasCreated.add(areaDropped);
								boolean isInnerarea = false;
								boolean findParent = false;
								for (int i = 0; i < InnerAreasCreated.size(); i++) {
									if (areaDropped.getLayoutX() > InnerAreasCreated.get(i).getLayoutX()
											&& areaDropped.getLayoutX() + areaDropped
													.areaWidth() < InnerAreasCreated.get(i).getLayoutX()
															+ InnerAreasCreated.get(i).areaWidth()
											&& areaDropped.getLayoutY() > InnerAreasCreated.get(i)
													.getLayoutY()
											&& areaDropped.getLayoutY() + areaDropped
													.areaHeight() < InnerAreasCreated.get(i).getLayoutY()
															+ InnerAreasCreated.get(i).areaHeight()) {
										InnerAreasCreated.add(areaDropped);
										newScene.addInnerArea(InnerAreasCreated.get(i).name, areaDropped.name);
										areaDropped.label.setText( areaDropped.name);
										isInnerarea = true;
										findParent = true;
										break;
									}
								}
								if (!findParent) {
									for (int i = 0; i < TopAreasCreated.size(); i++) {
										if (areaDropped.getLayoutX() > TopAreasCreated.get(i).getLayoutX()
												&& areaDropped.getLayoutX() + areaDropped
														.areaWidth() < TopAreasCreated.get(i).getLayoutX()
																+ TopAreasCreated.get(i).areaWidth()
												&& areaDropped.getLayoutY() > TopAreasCreated.get(i)
														.getLayoutY()
												&& areaDropped.getLayoutY() + areaDropped
														.areaHeight() < TopAreasCreated.get(i).getLayoutY()
																+ TopAreasCreated.get(i).areaHeight()) {
											InnerAreasCreated.add(areaDropped);
											newScene.addInnerArea(AllAreasCreated.get(i).name, areaDropped.name);
											areaDropped.label.setText( areaDropped.name);
											isInnerarea = true;
											findParent = true;
											break;
										}
									}
								}
								if (!isInnerarea) {
									TopAreasCreated.add(areaDropped);
									newScene.addTopArea(areaDropped.name);
									areaDropped.label.setText( areaDropped.name);
								}
								areaWindow.close();
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	private void buildDragHandlers2() {
		mAreaDragOverRoot = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				Point2D p = right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());
				if (!right_pane.boundsInLocalProperty().get().contains(p)) {
					event.acceptTransferModes(TransferMode.ANY);
					mDragableAreaOver.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
					return;
				}
				event.consume();
			}
		};
		mAreaDragOverRightPane = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				event.acceptTransferModes(TransferMode.ANY);
				mDragableAreaOver.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
				event.consume();
			}
		};
		mAreaDragDropped = new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				DragableContainer2 container2 = (DragableContainer2) event.getDragboard()
						.getContent(DragableContainer2.AddArea);
				container2.addData("scene_coordinates", new Point2D(event.getSceneX(), event.getSceneY()));
				ClipboardContent content2 = new ClipboardContent();
				content2.put(DragableContainer2.AddArea, container2);
				event.getDragboard().setContent(content2);
				event.setDropCompleted(true);
			}
		};
	}

	private void addDragDetection(DragableNode dragableNode) {
		dragableNode.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				base_pane.setOnDragOver(mNodeDragOverRoot);
				right_pane.setOnDragOver(mNodeDragOverRightPane);
				right_pane.setOnDragDropped(mNodeDragDropped);
				@SuppressWarnings("unused")
				DragableNode node = (DragableNode) event.getSource();
				mDragableNodeOver.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
				ClipboardContent content = new ClipboardContent();
				DragableContainer container = new DragableContainer();
				content.put(DragableContainer.AddNode, container);
				mDragableNodeOver.startDragAndDrop(TransferMode.ANY).setContent(content);
				mDragableNodeOver.setVisible(true);
				mDragableNodeOver.setMouseTransparent(true);
				event.consume();
			}
		});
	}

	private void addDragDetection(DraggableArea draggableArea) {
		draggableArea.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				base_pane.setOnDragOver(mAreaDragOverRoot);
				right_pane.setOnDragOver(mAreaDragOverRightPane);
				right_pane.setOnDragDropped(mAreaDragDropped);
				@SuppressWarnings("unused")
				DraggableArea area = (DraggableArea) event.getSource();
				mDragableAreaOver.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
				ClipboardContent content2 = new ClipboardContent();
				DragableContainer2 container2 = new DragableContainer2();
				content2.put(DragableContainer2.AddArea, container2);
				mDragableAreaOver.startDragAndDrop(TransferMode.ANY).setContent(content2);
				mDragableAreaOver.setVisible(true);
				mDragableAreaOver.setMouseTransparent(true);
				event.consume();
			}
		});
	}

	/**
	 * Function to link two connecting nodes
	 * 
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void addLinkNodeLine(double startX, double startY, double endX, double endY, String linkname) {
		// create a line
		Line linkNodeLine = new Line(startX + 11, startY + 10, endX + 11, endY + 10);
		//add the links to a static list
		allGraphicalLinksCreated.put(linkname, linkNodeLine);
		// set line width
		linkNodeLine.setStrokeWidth(2);
		// add the line to connect 2 nodes
		right_pane.getChildren().add(linkNodeLine);
	}

	/**
	 * Function to get random colors every time for a new link line created
	 */
	public Paint generateRandomColor() {
		// generate a random number
		Random randomNum = new Random();
		// get 3 random numbers - for R, G, and B
		int r = randomNum.nextInt(255);
		int g = randomNum.nextInt(255);
		int b = randomNum.nextInt(255);
		// return new color
		return Color.rgb(r, g, b);
	}
	

	/**
	 * Function to get the configuration information showed on left pane when click the node
	 */
	public void mouseClickDetectAddCfgbBx(AnchorPane pane) {
		 //Creating the mouse event handler 
	      EventHandler<MouseEvent> event = new EventHandler<MouseEvent>() { 
	         @Override 
	         public void handle(MouseEvent event) { 
		        xm = event.getSceneX();
		        ym = event.getSceneY();
		        /**
		    	 * Function to delete an area, as well as the nodes/areas within it
		    	 * however, because of time limit, we can only delete the area now, without deleting the content in it
		    	 * so we stash this part of code for now
		    	 */		        
		        for (DraggableArea eachArea : AllAreasCreated) {		        	
		        	xAreaMin =  eachArea.AreaXmin();		        			        	
		        	 xAreaMax =  eachArea.AreaXmax();
		        	 yAreaMin =  eachArea.AreaYmin();
		        	 yAreaMax =  eachArea.AreaYmax();
		        	 xMin = xAreaMin;
		        	 xMax = xMin + eachArea.label.getWidth();
		        	 yMin = yAreaMin;
		        	 yMax = yMin + eachArea.label.getHeight();
		        	 		    		        	 
		        	 if (xMin < xm && xm < xMax && yMin < ym && ym < yMax) {	
		        		 	Stage areaWindow = new Stage();
							BorderPane Pane = new BorderPane();
							Scene areaScene = new Scene(Pane, 350, 100);
							// block using of any other window of current application
							areaWindow.initModality(Modality.APPLICATION_MODAL);
							// disable resizing of the area setting dialog box
							areaWindow.resizableProperty().setValue(Boolean.FALSE);
							areaWindow.setTitle("Delete Area");
							areaWindow.setScene(areaScene);
							areaWindow.show();
							// set action on close request of area setting window						
							areaDelete area_delete = new areaDelete();
							Pane.setCenter(area_delete);
							area_delete.yesButton.setOnAction(e -> {
								
								Area temp = Controller.findArea(eachArea.name);
			    				deleteAreaFunc(temp);
				    			Controller.removeArea(eachArea.name);
				    			//clear the node cfg box and update the ApplicationList
				    			Sensor_windspeed.setSelected(false);
		                		Sensor_windspeed.setSelected(false);
		                		Sensor_temperature.setSelected(false);
		                		Sensor_humidity.setSelected(false);
		                		Sensor_virbration.setSelected(false);
		                		Sensor_pressure.setSelected(false);
		                		IPV6ID.setText(""); 
		                		MACID.setText(""); 
		                		NodeID.setText(""); 
		                		NodeIDinvisible.setText(""); 
		                		AppList.setText("");
				    			AddApplicationLoader.updateApplicationList();
				    			areaWindow.close();
							});
		        	 }
		        } 
		        //get eachnode position 
		        for (DragableNode eachNode : AllNodesCreated) {
		        	 xMin = eachNode.NodeAreaXmin();
		        	 xMax = eachNode.NodeAreaXmax();
		        	 yMin = eachNode.NodeAreaYmin();
		        	 yMax = eachNode.NodeAreaYmax();
		        	 if (xMin < xm && xm < xMax && yMin < ym && ym < yMax) {
		        		 // show the node's cfg in cfg box
		        		 for (Node nodeEach : Controller.getNodes()) {
		        			 if(nodeEach.getId() == eachNode.id) {
		        				 NodeIDinvisible.setText(nodeEach.getId());  
				        		 TempStoreNodeClickedID = nodeEach.getId(); 
				        		 NodeID.setText(nodeEach.getId().substring(1, nodeEach.getId().length() - 1)); 
				        		 MACID.setText(nodeEach.getMac());
				        		 IPV6ID.setText(nodeEach.getIP());
				        		 if(nodeEach.getWindspeed()) {Sensor_windspeed.setSelected(true);}else {Sensor_windspeed.setSelected(false);}
				        		 if(nodeEach.getTemperature()) {Sensor_temperature.setSelected(true);}else {Sensor_temperature.setSelected(false);}
				        		 if(nodeEach.getHumidity()) {Sensor_humidity.setSelected(true);}else {Sensor_humidity.setSelected(false);}
				        		 if(nodeEach.getVibration()) {Sensor_virbration.setSelected(true);}else {Sensor_virbration.setSelected(false);}
				        		 if(nodeEach.getPressure()) {Sensor_pressure.setSelected(true);}else {Sensor_pressure.setSelected(false);}
				        		 AppList.setText(nodeEach.getApplicationsID());
		        			 }
		        		 }//for Node nodeEach
		        	 }
		        } //for (DragableNode eachNode
	         } 
	      };  // EventHandler<MouseEvent> event
	     pane.addEventHandler(MouseEvent.MOUSE_CLICKED, event);
	}
	
	public void deleteAreaFunc(Area areaToDelete) {
		
		// check if has area
		if(areaToDelete.hasArea()) {
			// get list of areas
			ArrayList<Area> arealist = areaToDelete.getAreas();
			for(int i = 0; i < arealist.size(); i++) {
				deleteAreaFunc(arealist.get(i));				
			}
		}
		
		// delete nodes
		ArrayList<Node> tempNode = areaToDelete.getNodes();
		if(!tempNode.isEmpty()) {
			for(int i = 0; i < tempNode.size(); i++){
				deleteNodesGUI(tempNode.get(i).getId());
			}
		}
		
		for(DraggableArea area: AllAreasCreated) {
			if(area.name.equals(areaToDelete.getId())){
				right_pane.getChildren().remove(area);				
			}
		}
		
	}
	
	public void deleteNodesGUI(String nodeId) {
		
		for(DragableNode each : AllNodesCreated) {
            if(each.getID()== nodeId) {
                // remove link GUI
            	deleteLinks(nodeId);
                right_pane.getChildren().remove(each);
            }
        }
		
	}
	
	
	public void deleteLinks(String nodeId) {
		Node tempNode = Controller.findNode(nodeId);
		ArrayList<String> tempLinks = tempNode.getLinks();
		if(!tempLinks.isEmpty()) {
			for(int i = 0; i < tempLinks.size(); i++) {
				right_pane.getChildren().remove(allGraphicalLinksCreated.get(tempLinks.get(i)));
			}
		}
	}

	// if user changes sensor choice , then record again--
	public void SensorCheckEvent(ActionEvent event) {
		if (Sensor_windspeed.isSelected()) {
			wind_speed_value = true;
		} else {
			wind_speed_value = false;
		}
		if (Sensor_temperature.isSelected()) {
			temperature_value = true;
		} else {
			temperature_value = false;
		}
		if (Sensor_humidity.isSelected()) {
			humidity_value = true;
		} else {
			humidity_value = false;
		}
		if (Sensor_virbration.isSelected()) {
			vibration_value = true;
		} else {
			vibration_value = false;
		}
		if (Sensor_pressure.isSelected()) {
			pressure_value = true;
		} else {
			pressure_value = false;
		}
		for (Node nodeEach : Controller.getNodes()) {
			if (nodeEach.getId() == NodeIDinvisible.getText()) {
				nodeEach.setAllConf(temperature_value, wind_speed_value, humidity_value, vibration_value,
						pressure_value);
			}
		}
	}

	
	/**
	 * Function to handle click on File menu option of Close
	 */
	public void fileExitClicked() {
		// call the close program function to close the current application
		new javafx().closeProgram();
	}

	
	/**
	 * Function to handle click on Help menu option of Help
	 */
	public void helpAboutClicked() {
		// file handling exceptions handled
		try {
			// open the about section
			new AboutHelpLoader();
		} catch (Exception e) { // catch for handling any exception
			// print the error if any as is
			e.printStackTrace();
		}
	}

	
	/**
	 * Function to handle click on Help menu option of Help
	 */
//	public void fileNewClicked() {
//		// open a new project
//		openNewProject();
//	}

	
	
	/**
	 * Function which when called will open a new project
	 */
//	private void openNewProject() {
//		// clear out the scene
//		right_pane.getChildren().clear();
//		// clear the text area for algebraic expression
//		algebraicExpressionDisplay.setText(null);
//		// clear the applications list
//		vBoxForAreaNames.getChildren().clear();
//		//clear the cfg part 
//		Sensor_windspeed.setSelected(false);
//		Sensor_windspeed.setSelected(false);
//		Sensor_temperature.setSelected(false);
//		Sensor_humidity.setSelected(false);
//		Sensor_virbration.setSelected(false);
//		Sensor_pressure.setSelected(false);
//		IPV6ID.setText(""); 
//		MACID.setText(""); 
//		NodeID.setText(""); 
//		NodeIDinvisible.setText(""); 
//		AppList.setText("");
//		// create a new controller
//		newScene = new Controller("Scene");
//		Controller.linkCounter = 1;
//		Controller.nodeCounter = 1;
//		Controller.appCounter = 1;
//	}
	
	
	
	/**
	 * Function used in cfgAddAppController.java to get the current clicked node ID
	 */
	public static String getCurrentNodeId() {
		return TempStoreNodeClickedID;
	}
	
	public void mouseClickDetectAddCfgbBxDeleteArea(AnchorPane pane) {
		 //Creating the mouse event handler 
	      EventHandler<MouseEvent> event = new EventHandler<MouseEvent>() { 
	         @Override 
	         public void handle(MouseEvent event) { 
		        xm = event.getSceneX();
		        ym = event.getSceneY();
		        //get eacharea position 
		        for (DraggableArea eachArea : AllAreasCreated) {
		        	 xMin = eachArea.label.getLayoutX();
		        	 xMax = eachArea.label.getLayoutX() + eachArea.label.getWidth();
		        	 yMin = eachArea.label.getLayoutY();
		        	 yMax = eachArea.label.getLayoutY() + eachArea.label.getHeight();
		        	 if (xMin < xm && xm < xMax && yMin < ym && ym < yMax) {
		        		 // delete the area and the nodes within it
		        		 for (Area areaEach : newScene.getAreas()) {
		        			 if(areaEach.getId() == eachArea.name) {
		        				 
		        				
		        			 }
		        		 }//for Node nodeEach
		        	 }
		        } //for (DragableNode eachNode
	         } 
	      };  // EventHandler<MouseEvent> event
	     pane.addEventHandler(MouseEvent.MOUSE_CLICKED, event);
	}

//	@FXML
//	private void DeleteNode(ActionEvent event) {
//		
//	
//	 
//	}

	/**
	 * Function to perform action on mouse over
	 */
	public void mouseOverForVBox() {
		// call function to perform operation for mouse over
		AddApplicationLoader.actionOnMouseOver();
	}
}
