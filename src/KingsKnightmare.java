import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.AbstractMap.SimpleEntry;
import java.io.PrintWriter;

public class KingsKnightmare {
	//represents the map/board
	private static boolean[][] board;
	//represents the goal node
	private static Location king;
	//represents the start node
	private static Location knight;
	//y dimension of board
	private static int n;
	//x dimension of the board
	private static int m;
	
	private static boolean[][] explored; //board for explored nodes
	private static Stack<Location> frontierStack;//stack for holding frontier in DFS
	private static int count; //number of nodes expanded
	private static String filePath; // name of output filepath
	private static Queue<Location> frontierQ;
	private static int COST = 3;
	private static PriorityQ<Location> frontierPQ;

	
	//enum defining different algo types
 	enum SearchAlgo{
		BFS, DFS, ASTAR;
	}

	public static void main(String[] args) {
		if (args != null && args.length > 0) {
			//loads the input file and populates the data variables
			SearchAlgo algo = loadFile(args[0]);
			filePath = "Output_" + args[0];
			if (algo != null) {
				switch (algo) {
					case DFS :
						executeDFS();
						break;
					case BFS :
						executeBFS();
						break;
					case ASTAR :
						executeAStar();
						break;
					default :
						break;
				}
			}
		}
	}

	/**
	 * Implementation of Astar algorithm for the problem
	 */
	private static void executeAStar() {
		frontierPQ = new PriorityQ<Location>();
		frontierPQ.add(knight, 0);
		explored = new boolean[board[0].length][board.length]; //initialize explored array to the same size as board
		count = 0; //count of the number of nodes being expanded
		
		while (!frontierPQ.isEmpty()) {
			Location currNode = frontierPQ.poll().getKey(); //Obtain reference to next node to be expanded	
			if (isKing(currNode)) {
				writeSolution(currNode);
				return; //since solution was found, return
			}
			expandNode(currNode, 3, true); //will have a value if solution had been found
		}
		
		//Write no solution since frontier queue was empty and no solution was found
		writeNoSolution();
		
	}

	/**
	 * Implementation of BFS algorithm
	 */
	private static void executeBFS() {
		frontierQ = new LinkedList<Location>();
		frontierQ.add(knight); //queue of frontier nodes to be added and removed
		explored = new boolean[board[0].length][board.length]; //initialize explored array to the same size as board
		count = 0; //count of the number of nodes being expanded
		
		while (!frontierQ.isEmpty()) {
			Location returnedNode = expandNode(frontierQ.poll(), 2, false);
			if (returnedNode != null) { //since PQ is not used, score for all nodes will be 0
				writeSolution(returnedNode);
				return; //since solution was found, return
			}
		}
		
		//Write no solution since frontier queue was empty and no solution was found
		writeNoSolution();
		
	}
	
	/**
	 * Implemention of DFS algorithm
	 */
	private static void executeDFS() {
		frontierStack = new Stack<Location>(); //List of frontier nodes to be added and removed
		frontierStack.push(knight); //Add the first location node of knight to frontier
		explored = new boolean[board[0].length][board.length]; //initialize explored array to the same size as board
		
		while (!frontierStack.isEmpty()) {
			Location returnedNode = expandNode(frontierStack.pop(), 1, false); 
			if (returnedNode != null) { //since PQ not used, score = 0 for all nodes
				writeSolution(returnedNode);
				return; //since solution was found, return
			}
		}
		
		//Write no solution since frontier queue was empty and no solution was found
		writeNoSolution();
		
	}
	
	/**
	 * 
	 * @param filename
	 * @return Algo type
	 * This method reads the input file and populates all the 
	 * data variables for further processing
	 */
	private static SearchAlgo loadFile(String filename) {
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);
			SearchAlgo algo = SearchAlgo.valueOf(sc.nextLine().trim().toUpperCase());
			n = sc.nextInt();
			m = sc.nextInt();
			sc.nextLine();
			board = new boolean[n][m];
			for (int i = 0; i < n; i++) {
				String line = sc.nextLine();
				for (int j = 0; j < m; j++) {
					if (line.charAt(j) == '1') {
						board[i][j] = true;
					} else if (line.charAt(j) == 'S') {
						knight = new Location(j, i, null);
					} else if (line.charAt(j) == 'G') {
						king = new Location(j, i, null);
					}
				}
			}
			sc.close();
			return algo;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static boolean isStackFrontier(Location node) {
		//Check if node with provided params is already in frontier
		if (frontierStack.search(node) == -1) {
			return false;
		}
		return true;
	}
	
	private static boolean isQFrontier(Location node) {
		//Check if node with provided params is already in frontier
		return frontierQ.contains(node);
	}
	
	private static boolean isPQFrontier(Location node) {
		return frontierPQ.exists(node);
	}
	
	private static boolean isExplored(Location node) {
		//Return the boolean value of explored array with given coordinates
		return explored[node.getX()][node.getY()];
	}
	
	private static int hFunction(Location node) {
		return (Math.abs(node.getX() - king.getX()) + (Math.abs(node.getY() - king.getY())));
	}
	
	private static boolean isKing(Location node) {
		//Check if x & y coordinates provided matches the king node's x and y coordinates
		if ( node.getX() == king.getX() && node.getY() == king.getY()) 
			return true;
		return false;
	}
	
	private static Location expandNode (Location currNode, int mode, boolean checkGoal) {
		count++; //increment count after expanding node successfully
		
		int currNodeX = currNode.getX();
		int currNodeY = currNode.getY();
		explored[currNodeX][currNodeY] = true; //save location of current node to explored array
		Location childNode;
		//Find possible nodes from current node's position
		//1. 2 right 1 down
		//Check if child node is in range of board
		if ((currNodeX + 2 < board[0].length) && (currNodeY + 1 < board.length)) {
			//Check for obstacles
			if (!board[currNodeY + 1][currNodeX + 2]) {
				childNode = new Location(currNodeX + 2, currNodeY + 1, currNode);
				//Check if child node is king
				if (!isKing(childNode) || checkGoal) {
					if (mode == 3) {
						addNode(childNode, mode, calcScore(childNode));
					}
					//addNode method already checks for explored and frontier
					else {
						addNode(childNode, mode, 0);
					}
				}
				else {
					//Solution is found, hence return the solution node
					return childNode;
				}
				
			}
		}
		
		
		
		//2. 1 right 2 down
		//Check if child node is in range of board
		if ((currNodeX + 1 < board[0].length) && (currNodeY + 2 < board.length)) {
			if (!board[currNodeY + 2][currNodeX + 1]) {
				childNode = new Location(currNodeX + 1, currNodeY + 2, currNode);
				
				//Check if child node is king
				if (!isKing(childNode) || checkGoal) {
					if (mode == 3) {
						addNode(childNode, mode, calcScore(childNode));
					}
					//addNode method already checks for explored and frontier
					else {
						addNode(childNode, mode, 0);
					}
				}
				else {
					//Solution is found, hence return the solution node
					return childNode;
				}
				
			}
		}
			
		
		//3. 1 left 2 down
		//Check if child node is in range of board
		if ((currNodeX - 1 >= 0) && (currNodeY + 2 < board.length)) {
			if (!board[currNodeY + 2][currNodeX - 1]) {
				childNode = new Location(currNodeX - 1, currNodeY + 2, currNode);
		
				//Check if child node is king
				if (!isKing(childNode) || checkGoal) {
					if (mode == 3) {
						addNode(childNode, mode, calcScore(childNode));
					}
					//addNode method already checks for explored and frontier
					else {
						addNode(childNode, mode, 0);
					}
				}
				else {
					//Solution is found, hence return the solution node
					return childNode;
				}
			}
		}
		
		//4. 2 left 1 down
		//Check if child node is in range of board
		if ((currNodeX - 2 >= 0) && (currNodeY + 1 < board.length)) {
			if (!board[currNodeY + 1][currNodeX - 2]) {
				childNode = new Location(currNodeX - 2, currNodeY + 1, currNode);
				
				//Check if child node is king
				if (!isKing(childNode) || checkGoal) {
					if (mode == 3) {
						addNode(childNode, mode, calcScore(childNode));
					}
					//addNode method already checks for explored and frontier
					else {
						addNode(childNode, mode, 0);
					}
				}
				else {
					//Solution is found, hence return the solution node
					return childNode;
				}
			}
		}
		
		//5. 2 left 1 up
		//Check if child node is in range of board
		if ((currNodeX - 2 >= 0) && (currNodeY - 1 >= 0)) {
			if (!board[currNodeY - 1][currNodeX - 2]) {
				childNode = new Location(currNodeX - 2, currNodeY - 1, currNode);
				
				//Check if child node is king
				if (!isKing(childNode) || checkGoal) {
					if (mode == 3) {
						addNode(childNode, mode, calcScore(childNode));
					}
					//addNode method already checks for explored and frontier
					else {
						addNode(childNode, mode, 0);
					}
				}
				else {
					//Solution is found, hence return the solution node
					return childNode;
				}
			}
		}
		
		//6. 1 left 2 up
		//Check if child node is in range of board
		if ((currNodeX - 1 >= 0) && (currNodeY - 2 >= 0)) {
			if (!board[currNodeY - 2][currNodeX - 1]) {
				childNode = new Location(currNodeX - 1, currNodeY - 2, currNode);
				
				//Check if child node is king
				if (!isKing(childNode) || checkGoal) {
					if (mode == 3) {
						addNode(childNode, mode, calcScore(childNode));
					}
					//addNode method already checks for explored and frontier
					else {
						addNode(childNode, mode, 0);
					}
				}
				else {
					//Solution is found, hence return the solution node
					return childNode;
				}
			}
		}
		
		//7. 1 right 2 up
		//Check if child node is in range of board
		if ((currNodeX + 1 < board[0].length) && (currNodeY - 2 >= 0)) {
			if (!board[currNodeY - 2][currNodeX + 1]) {
				childNode = new Location(currNodeX + 1, currNodeY - 2, currNode);
				
				//Check if child node is king
				if (!isKing(childNode) || checkGoal) {
					if (mode == 3) {
						addNode(childNode, mode, calcScore(childNode));
					}
					//addNode method already checks for explored and frontier
					else {
						addNode(childNode, mode, 0);
					}
				}
				else {
					//Solution is found, hence return the solution node
					return childNode;
				}
			}
		}
		
		//8. 2 right 1 up
		//Check if child node is in range of board
		if ((currNodeX + 2 < board[0].length) && (currNodeY - 1 >= 0)) {
			if (!board[currNodeY - 1][currNodeX + 2]) {
				childNode = new Location(currNodeX + 2, currNodeY - 1, currNode);
				
				//Check if child node is king
				if (!isKing(childNode) || checkGoal) {
					if (mode == 3) {
						addNode(childNode, mode, calcScore(childNode));
					}
					//addNode method already checks for explored and frontier
					else {
						addNode(childNode, mode, 0);
					}
				}
				else {
					//Solution is found, hence return the solution node
					return childNode;
				}
			}
		}
		return null;
	}
	
	private static void addNode(Location childNode, int mode, int score) {
		//Mode 1 = DFS
		if (mode == 1 && !isStackFrontier(childNode) && !isExplored(childNode)) {
			frontierStack.push(childNode);
		}
		//Mode 2 = BFS
		else if (mode == 2 && !isQFrontier(childNode) && !isExplored(childNode)) {
			frontierQ.add(childNode);
		}
		//Mode 3 = A Star
		else if (mode == 3 && !isExplored(childNode)) {
			if (!isPQFrontier(childNode)) {
				frontierPQ.add(childNode, score);
			}
			//check if childnode has a lesser value than the one in the frontier
			else {
				int frontierNodeScore = frontierPQ.getPriorityScore(childNode);
				if (score < frontierNodeScore) {
					//And replace it with the new one with the lower score
					frontierPQ.modifyEntry(childNode, score);
				}
				
			}
		}
	}
		
	private static void writeSolution(Location childNode) {
		Stack<Location> tempStack = new Stack<Location>();
		while (!childNode.equals(knight)) {
			tempStack.push(childNode);
			childNode = childNode.getParent();
			if (childNode.equals(knight)) {
				tempStack.push(childNode);
			}
		}
		
		//Iterate through stack to print out solution
		Location tempNode;
		while(!tempStack.isEmpty()) {
			tempNode = tempStack.pop();
			System.out.println(tempNode.getX() + " " + tempNode.getY());
		}
		System.out.println("Expanded Nodes: " + count);
		
//		//Writing solution to file
//		PrintWriter writer = null;
//		try {
//			
//			File outFile = new File(filePath);
//			writer = new PrintWriter(outFile);
//			Location tempNode;
//			while(!tempStack.isEmpty()) {
//				tempNode = tempStack.pop();
//				writer.println(tempNode.getX() + " " + tempNode.getY());
//			}
//		}
//		catch (Exception e) {
//			return;
//		}
//		finally {
//			writer.println("Expanded Nodes: " + count); //print expanded number of nodes
//			writer.close();
//		}
	}

	private static void writeNoSolution() {
		//Solution no found
		System.out.println("NOT REACHABLE");
		System.out.println("Expanded Nodes: " + count);
//		PrintWriter writer = null;
//		try {
//			
//			File outFile = new File(filePath);
//			writer = new PrintWriter(outFile);
//			writer.println("NOT REACHABLE");
//		}
//		catch (Exception e) {
//			
//		}
//		finally {
//			writer.println("Expanded Nodes: " + count); // print expanded number of nodes
//			writer.close();
//		}
	}

	private static int calcScore(Location node) {
		int edges = 1; //number of edges
		Location parent = node.getParent();
		while (!parent.equals(knight)) {
			parent = parent.getParent();
			edges++;
		}
		return edges * COST + hFunction(node);
	}
}
