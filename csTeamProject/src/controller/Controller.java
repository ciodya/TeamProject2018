package controller;
/** 
 * University of Glasgow
 * MSc CS+ Team Project, fall 2018
 * 
 * Controller for the mathematical backend of a bigraph application
 * Works as an overlay to the Apps, Area, and Node class to makes sure each interaction is correctly done.
 * 
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
public class Controller {

	// name of scene, the controller is the base scene where everything happens
	private String sceneName;
	// list of links
	private static ArrayList<String> listOfLinks = new ArrayList<>();
	// list of application
	private static ArrayList<Apps> listOfApps = new ArrayList<Apps>();
	// list of Areas
	public static ArrayList<Area> listOfAreas; // all areas
	private static ArrayList<Area> topLevelAreas; // areas on the scene
	private static ArrayList<Area> innerAreas; // areas within another area
	public static int linkCounter = 1, nodeCounter = 1, appCounter = 1;
	// list of Nodes
	private static ArrayList<Node> listOfNodes;

	// constructor
	public Controller() {
		this.sceneName = "DefaultName";
		Controller.listOfLinks = new ArrayList<String>();
		Controller.listOfApps = new ArrayList<Apps>();
		Controller.listOfAreas = new ArrayList<Area>();
		Controller.topLevelAreas = new ArrayList<Area>();
		Controller.innerAreas = new ArrayList<Area>();
		Controller.listOfNodes = new ArrayList<Node>();
	}

	// named constructor (preferred)
	public Controller(String name) {
		this.sceneName = name;
		Controller.listOfLinks = new ArrayList<String>();
		Controller.listOfApps = new ArrayList<Apps>();
		Controller.listOfAreas = new ArrayList<Area>();
		Controller.topLevelAreas = new ArrayList<Area>();
		Controller.innerAreas = new ArrayList<Area>();
		Controller.listOfNodes = new ArrayList<Node>();
	}

	// get the name of the scene
	public String getSceneName() {
		return this.sceneName;
	}

	// rename the scene
	public void setSceneName(String newName) {
		this.sceneName = newName;
	}

	// get list of Apps
	public static ArrayList<Apps> getApps() {
		return Controller.listOfApps;
	}

	// get list of Links
	public static ArrayList<String> getLinks() {
		return Controller.listOfLinks;
	}

	// get list of Nodes
	public static ArrayList<Node> getNodes() {
		return Controller.listOfNodes;
	}

	// get list of Areas
	public  ArrayList<Area> getAreas() {
		return Controller.listOfAreas;
	}

	// get list of topAreas
	public ArrayList<Area> getTopLvlAreas() {
		return Controller.topLevelAreas;
	}

	// get list of innerAreas
	public ArrayList<Area> getInnerAreas() {
		return Controller.innerAreas;
	}

	// ---------------- Area ----------------------------
	/**
	 * Add top level Area takes the name of the new area, creates an area and then
	 * add it to the topLevelAras list and the listOfAreas.
	 * 
	 * @param name
	 * @return newArea.getId()
	 */
	public String addTopArea(String name) {
		Area newArea = new Area(name);
		Controller.listOfAreas.add(newArea);
		Controller.topLevelAreas.add(newArea);
		return newArea.getId();
	}

	/**
	 * Add inner area takes the name of the newArea and the area it is places inside
	 * finds the parent area in the list of parents, and then creates a newArea then
	 * add the newArea to the parent.
	 * 
	 * @param parent
	 * @param newId
	 * @return newAra.getId()
	 */
	public String addInnerArea(String parent, String newId) {
		// find the parent Area
		Area parentArea = findArea(parent);
		// add new area to parent area
		Area newArea = new Area(newId);
		parentArea.addArea(newArea);
		Controller.listOfAreas.add(newArea);
		Controller.innerAreas.add(newArea);
		return newArea.getId();
	}
	// ---------------- Node ----------------------------

	// Add new node
	/**
	 * Takes the name of the area the node is placed in to find the area in the
	 * list. takes the paramaters of each configuration a node can have.
	 * 
	 * @param areaName
	 * @param temperature
	 * @param windspeed
	 * @param humidity
	 * @param vibration
	 * @param pressure
	 * @param appName
	 * @return the id of the node
	 */
	public String addNodeToArea(String areaName, boolean temperature, boolean windspeed, boolean humidity,
			boolean vibration, boolean pressure) {
		// find the area
		Area areaToAddTo = findArea(areaName);
		// Create nodeId
		String nodeId = "{c" + (Controller.nodeCounter++) + "}";
		// create new node
		Node newNode = new Node(nodeId);
		// set nodes configurations
		newNode.setAllConf(temperature, windspeed, humidity, vibration, pressure);
		Controller.listOfNodes.add(newNode);
		// add node to area
		areaToAddTo.addNode(newNode);
		// return node name
		return nodeId;
	}

	// change config of node
	/**
	 * takes in the name of a node and finds it in the list of nodes. then we cange
	 * the configurations based on the given paramaters.
	 * 
	 * @param nodeName
	 * @param temperature
	 * @param windspeed
	 * @param humidity
	 * @param vibration
	 * @param pressure
	 */
	public void addConfigToNode(String nodeName, boolean temperature, boolean windspeed, boolean humidity,
			boolean vibration, boolean pressure) {
		// find node
		Node newNode = findNode(nodeName);
		// set nodes configurations
		newNode.setAllConf(temperature, windspeed, humidity, vibration, pressure);
	}

	/**
	 * Takes node id and appName and finds both of them in their lists, before
	 * adding the app to the node
	 * 
	 * @param appName
	 * @param nodeId
	 */
	public static void addAppToNode(String appName, String nodeId) {
		Node findNode = findNode(nodeId);
		Apps findApp = findApp(appName);
		findNode.addApp(findApp);
	}

	// ---------------- Apps ----------------------------
	/**
	 * takes the name of the application you want to create, generate and automatic
	 * id and creates the new application.
	 * 
	 * @param appName
	 * @return generated appID
	 */
	public static String newApp(String appName) {
		String appID = "A(" + (Controller.appCounter++) + ")";
		Apps newApp = new Apps(appName, appID);
		Controller.listOfApps.add(newApp);
		return appID;
	}

	// ---------------- Links ----------------------------
	// add new link
	/**
	 * takes the names of the nodes to be connected and find them in the list of
	 * nodes adds the link to the two nodes generate the linkId
	 * 
	 * @param firstNodeName
	 * @param secondNodeName
	 * @return linkId
	 */
	public static String addNewLink(String firstNodeName, String secondNodeName) {
		// create new link
		String linkName = "l" + (Controller.linkCounter++);
		Controller.listOfLinks.add(linkName);
		// find the nodes
		Node firstNode = findNode(firstNodeName);
		Node secondNode = findNode(secondNodeName);
		firstNode.addLink(linkName);
		firstNode.setNodesLinkedTo(secondNodeName);
		secondNode.addLink(linkName);
		secondNode.setNodesLinkedTo(firstNodeName);
		return linkName;
	}

	// ---------------- Removal section -----------------------
    /**
     * Remove area
     * @param area
     */
    public static void removeArea(String area){
        // get the area
        Area areaToRemove = findArea(area);

        //remove area from lists
        removeAreaFromLists(areaToRemove);

        //check if this area contains another area
        if(areaToRemove.hasArea()){
            ArrayList<Area> removeAllAreas = areaToRemove.getAreas();

            for(int i = 0; i < removeAllAreas.size(); i++){
                removeArea(removeAllAreas.get(i).getId());
            }
        }
        
        removeFromParentArea(areaToRemove);
        
        // get all nodes that must be removed
        ArrayList<Node> nodesToRemove = areaToRemove.getNodes();
        for(int i = 0; i < nodesToRemove.size(); i++){
            removeNode(nodesToRemove.get(i).getId());
        }

    }

    /**
     * Helper to remove area
     * @param areaRem
     */
    private static void removeAreaFromLists(Area areaRem){
        // remove the area from its lists
        int indexTop = Controller.topLevelAreas.indexOf(areaRem);
        if(indexTop != -1){
        	Controller.topLevelAreas.remove(indexTop);
        }
        int indexInner = Controller.innerAreas.indexOf(areaRem);
        if(indexInner != -1){
        	Controller.innerAreas.remove(indexInner);
        }
        int index = Controller.listOfAreas.indexOf(areaRem);
        if(index != -1){
        	Controller.listOfAreas.remove(index);
        }
    }
    
    private static void removeFromParentArea(Area areaToRem) {
    	for (int i = 0; i < Controller.listOfAreas.size(); i++) {
    		if(Controller.listOfAreas.get(i).hasArea()) {
    			ArrayList<Area> temp = Controller.listOfAreas.get(i).getAreas();
    			for(int j = 0; j < temp.size(); j++) {
    			
    				if(temp.get(j).getId().equals(areaToRem.getId())) {
    					Controller.listOfAreas.get(i).removeArea(areaToRem);
    				}
    			}
    		}
    	}
    }

    public static void removeNode(String nodeId){
        //get the node
        Node nodeToRemove = findNode(nodeId);
        
        // remove node from list of nodes
        int index = Controller.listOfNodes.indexOf(nodeToRemove);
        if(index != -1){
            Controller.listOfNodes.remove(index);
        }

        // remove links related to this node
        ArrayList<String> nodesLinks = nodeToRemove.getLinks();
        if(!nodesLinks.isEmpty()){
            for(int i = 0; i < nodesLinks.size(); i++){
                String temp = nodesLinks.get(i);
                Controller.removeLink(temp);
            }
        }

        // remove node from the area its in
        for(int j = 0; j < Controller.listOfAreas.size(); j++){
        	Controller.listOfAreas.get(j).removeNode(nodeToRemove);
        }

    }

    // remove links
    public static void removeLink(String linkId){
        if(Controller.listOfLinks.contains(linkId)){
            for(int i  = 0; i < Controller.listOfNodes.size(); i++){
                Controller.listOfNodes.get(i).removeLink(linkId);
            }
            Controller.listOfLinks.remove(linkId);
        }
    }

    // remove applications
    public void removeApps(String appId){
        // get app
        Apps appToRemove = findApp(appId);
        if(Controller.listOfApps.contains(appToRemove)){
            for(int i = 0; i < Controller.listOfNodes.size(); i++){
                Controller.listOfNodes.get(i).removeApp(appToRemove);
            }
            Controller.listOfApps.remove(appToRemove);
        }
        
    }


	// ---------------- Print out ----------------------------
	/**
	 * Put together a string that represents the bigrap algebraic expression
	 * 
	 * @return bigraph expression
	 */
	public String exportBigraph() {
		// set up the function
		String finalFunc = "";
		// add links in the beginning
		finalFunc = finalFunc + printLinks();
		finalFunc = finalFunc + "(";
		// print the areas and the nodes within them
		finalFunc = finalFunc + printAreas();
		// prepare to show configs
		finalFunc = finalFunc + "||";
		// add configuration of nodes
		if (!Controller.listOfNodes.isEmpty()) {
			finalFunc = finalFunc + printNodesConf();
		}
		// add ending bracket
		finalFunc = finalFunc + ")";
		return finalFunc;
	}

	// return a string with all Node fonfigurations
	/**
	 * loopes through the list of nodes and print the configurations of each node
	 * 
	 * @return string with nodeconfigurations
	 */
	public String printNodesConf() {
		String toRet = "(";
		for (int i = 0; i < Controller.listOfNodes.size(); i++) {
			if (i == Controller.listOfNodes.size() - 1) {
				toRet = toRet + Controller.listOfNodes.get(i).printNodeConf();
			} else {
				toRet = toRet + Controller.listOfNodes.get(i).printNodeConf() + "\n\t\t\t\t| ";
			}
		}
		toRet = toRet + ")";
		return toRet;
	}

	// print out link expression
	/**
	 * loops through the list of links and print out each one
	 * 
	 * @return /l1/ln... and so fort
	 */
	private String printLinks() {
		String toReturn = "/";
		for (int i = 0; i < Controller.listOfLinks.size(); i++) {
			toReturn = toReturn + Controller.listOfLinks.get(i) + "/";
		}
		if (toReturn.substring(toReturn.length() - 1).equals("/")) {
			toReturn = toReturn.substring(0, toReturn.length() - 1);
		}
		return toReturn;
	}

	// print out Areas
	/**
	 * loops through the list of top level areas and calls the printArea Function
	 * from each top area this nestes through all areas in the scene. By the
	 * recursive printArea function in the area class.
	 * 
	 * @return string with all area and node information
	 */
	private String printAreas() {
		String toRet = "";
		for (int i = 0; i < Controller.topLevelAreas.size(); i++) {
			if (i == Controller.topLevelAreas.size() - 1) {
				toRet = toRet + " " + Controller.topLevelAreas.get(i).printArea();
			} else if (i == 0 && !(i == Controller.topLevelAreas.size() - 1)) {
				toRet = toRet + Controller.topLevelAreas.get(i).printArea() + " |";
			} else {
				toRet = toRet + " " + Controller.topLevelAreas.get(i).printArea() + " |";
			}
		}
		// if(toRet.substring(toRet.length() - 1).equals("|")){
		// toRet = toRet.substring(0,toRet.length()-1);
		// }
		return toRet;
	}

	/**
	 * loopes through all applications and print them
	 * 
	 * @return string of all aps
	 */
	private String printApps() {
		String toRet = "(";
		for (int i = 0; i < Controller.listOfApps.size(); i++) {
			if (i == Controller.listOfApps.size() - 1) {
				toRet = toRet + "App(" + (i + 1) + ")." + "(" + Controller.listOfApps.get(i).getId() + ")";
			} else {
				toRet = toRet + "App(" + (i + 1) + ")." + "(" + Controller.listOfApps.get(i).getId() + ")"
						+ "\n\t\t\t\t| ";
			}
		}
		toRet = toRet + ")";
		return toRet;
	}

	/**
	 * print each id of every node in this scene
	 * 
	 * @return /c1/c2... etc
	 */
	private String printNodes() {
		String toRet = "/";
		for (int i = 0; i < Controller.listOfNodes.size(); i++) {
			toRet = toRet + Controller.listOfNodes.get(i).getId().substring(1,
					Controller.listOfNodes.get(i).getId().length() - 1) + "/";
		}
		if (toRet.substring(toRet.length() - 1).equals("|")) {
			toRet = toRet.substring(0, toRet.length() - 1);
		}
		return toRet;
	}

	// ---------------- Export BIG file ----------------------------
	/**
	 * This method writs out a model.big file. When called it builds a string by
	 * calling the print functions above. Then writes the string to the model.bif
	 * file.
	 */
	public void exportBIG() {
		try {
			// creates the writer
			BufferedWriter writer = new BufferedWriter(new FileWriter("model.big"));
			// Everything that is unchangable in the bigfile
			String toWrite = "# Signature\n\n# Node types\n";
			toWrite = toWrite
					+ "ctrl N = 1;                 # Idle node\nctrl N_U = 1;               # Node in use\natomic ctrl N_F = 1;        # Node with failure\nctrl L = 0;                 # Links\natomic ctrl L_E = 1;        # Link end\n\n";
			toWrite = toWrite
					+ "fun ctrl App(x) = 0;        # Application\natomic fun ctrl A(x) = 0;   # Application token\n\n";
			toWrite = toWrite + "# Node configuration\nctrl Conf = 1;\n\n";
			toWrite = toWrite
					+ "# Node configuration values\natomic fun ctrl MAC(x) = 0;\natomic fun ctrl IPv6(x) = 0;\natomic ctrl T = 0;\natomic ctrl H = 0;\natomic ctrl V = 0;\natomic ctrl P = 0;\natomic ctrl W = 0;\n\n";
			// All the areas in this scene
			toWrite = toWrite + "# Topology\n";
			for (int i = 0; i < Controller.listOfAreas.size(); i++) {
				toWrite = toWrite + "ctrl " + Controller.listOfAreas.get(i).getId() + " = 0;\n";
			}
			// Adds the different perspectives (unchangable)
			toWrite = toWrite
					+ "\n# Perspectives\nctrl PHY = 0;\nctrl DATA = 0;\nctrl CONF = 0;\nctrl SERVICE = 0;\n\n";
			// write PHYSICAL State
			toWrite = toWrite + "# Current state\nbig s0_P = \n  " + printLinks() + "\n\t";
			toWrite = toWrite + "PHY.(" + printAreas() + ");\n\n";
			// write DATA state
			toWrite = toWrite + "big s0_D = ";
			toWrite = toWrite + "DATA." + printNodesConf() + ";\n\n";
			// write SERVICE stat
			toWrite = toWrite + "big s0_S = ";
			toWrite = toWrite + "SERVICE." + printApps() + ";\n\n";
			// write NODES stat
			toWrite = toWrite + "big s0 = \n  ";
			toWrite = toWrite + printNodes() + "\n\t(s0_P || s0_D || s0_S);";
			// Write out the file
			writer.write(toWrite);
			// flush and close the writer
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// catch possibe IOException
			e.printStackTrace();
		}
	}
	// ---------------- Private helpers ----------------------------
	// These helpers are for internal use to find the Node, Area, and App based on a
	// given name.

	// find node in list of nodes
	public static Node findNode(String nodeName) {
		Node toReturn = new Node();
		// find Node
		for (int i = 0; i < Controller.listOfNodes.size(); i++) {
			if (listOfNodes.get(i).getId().equals(nodeName)) {
				toReturn = Controller.listOfNodes.get(i);
			}
		}
		return toReturn;
	}

	// find area in list of areas
	public static Area findArea(String areaName) {
		Area toReturn = new Area();
		// find Area
		for (int i = 0; i < Controller.listOfAreas.size(); i++) {
			if (listOfAreas.get(i).getId().equals(areaName)) {
				toReturn = Controller.listOfAreas.get(i);
			}
		}
		return toReturn;
	}

	// find app in list of apps
	public static Apps findApp(String appName) {
		Apps toReturn = new Apps();
		// find App
		for (int i = 0; i < Controller.listOfApps.size(); i++) {
			if (listOfApps.get(i).getId().equals(appName)) {
				toReturn = Controller.listOfApps.get(i);
			}
		}
		return toReturn;
	}
}