import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

class Pair{ //created this class so we can return both the stack and the array list from the methods
	/*
	 * the stack contains the history i.e. every state visited, we remove elements when all options 
	 * for that state have been explored.
	 * 
	 * states list contains every unique state
	 */
	
	protected Stack<int[]> history;
	protected ArrayList<int[]> states; 
	
	Pair(Stack<int[]> History, ArrayList<int[]> States){
		this.history = History;
		this.states = States;
	}
}

public class JugProblem {
	
	public static void main(String[] args) {
		//get the user input and initialise variables
		int[] inputs = getInput();
		int capA = inputs[0]; int capB = inputs[1]; int capC = inputs[2];
		int[] startnode = {0,0,0};
		Stack<int[]> history = new Stack<int[]>();
		ArrayList<int[]> states = new ArrayList<int[]>();
		history.push(startnode);
		states.add(startnode);
		Pair pair = new Pair(history, states);
		int[] currentNode = pair.history.peek();
		// call the method that generates the states
		pair = generateStates(capA, capB, capC, pair, currentNode);
		// show the states
		showStates(pair.states);
		System.out.print("There are " + pair.states.size() + " unique states!");
	}
	
	public static int[] getInput() {// ask for the user's input
		int[]input = new int[3];
		Scanner entry = new Scanner(System.in);
		System.out.println("Enter the capacity of jug A:");
		input[0] = entry.nextInt();
		System.out.println("Enter the capacity of jug B:");
		input[1] = entry.nextInt();
		System.out.println("Enter the capacity of jug C:");
		input[2] = entry.nextInt();
		entry.close();
		return input;
	}
	
	public static Pair generateStates(int capA, int capB, int capC, Pair pair, int[] currentNode) {
		//initialise count to check number of states
		int stateCount = pair.states.size();
		// if there are nodes still to be checked (history not empty)
		if(!pair.history.isEmpty()) {
			// call the fill, empty and pour methods
			pair = Fill(capA, capB, capC, pair.history, pair.states, currentNode);
			currentNode = pair.history.peek();
			pair = Empty(pair.history, pair.states, currentNode);
			currentNode = pair.history.peek();
			pair = Pour(capA, capB, capC, pair.history, pair.states, currentNode);
			// states is the same length i.e. no new nodes
			if(stateCount == pair.states.size()) {
				// remove top of stack and generate states again
				int[] nextNode = pair.history.pop();
				generateStates(capA, capB, capC, pair, nextNode);
			}else {
				// if more states have been discovered, we are not done with that element in history, so we peek
				int[] nextNode = pair.history.peek();
				// generate states again.
				generateStates(capA, capB, capC, pair, nextNode);
			}
		}
		return pair;
	}
	
	public static Pair Fill(int capA, int capB, int capC, Stack<int[]> history, ArrayList<int[]> states, int[] node) {
		/* 
		 * check for each jug if it is not at capacity and if filling it makes a new state
		 * then we fill and call fill on the new state
		 */
		if(node[0] != capA && !checkExists(capA, node[1], node[2], states)) {
			int[] newnode = {capA, node[1], node[2]};
			states.add(newnode);
			history.push(newnode);
			Fill(capA, capB, capC, history, states, newnode);
		}else if(node[1] != capB && !checkExists(node[0], capB, node[2], states)) {
			int[] newnode = {node[0], capB, node[2]};
			states.add(newnode);
			history.push(newnode);
			Fill(capA, capB, capC, history, states, newnode);
		}else if(node[2] != capC && !checkExists(node[0], node[1], capC, states)) {
			int[] newnode = {node[0], node[1], capC};
			states.add(newnode);
			history.push(newnode);
			Fill(capA, capB, capC, history, states, newnode);
		}
		Pair pair = new Pair(history, states);
		return pair;
	}
	
	public static Pair Empty(Stack<int[]> history, ArrayList<int[]> states, int[] node) {
		/*
		 * check for each jug if it is not empty, and emptying it creates a new state,
		 * then we empty it and apply empty on that state again
		 */
		if(node[0] != 0 && !checkExists(0, node[1], node[2], states)) {
			int[] newnode = {0, node[1], node[2]};
			states.add(newnode);
			history.push(newnode);
			Empty(history, states, newnode);
		}else if(node[1] != 0 && !checkExists(node[0], 0, node[2], states)) {
			int[] newnode = {node[0], 0, node[2]};
			states.add(newnode);
			history.push(newnode);
			Empty(history, states, newnode);
		}else if(node[2] != 0 && !checkExists(node[0], node[1], 0, states)) {
			int[] newnode = {node[0], node[1], 0};
			states.add(newnode);
			history.push(newnode);
			Empty(history, states, newnode);
		}
		Pair pair = new Pair(history, states);
		return pair;
	}
	// you pour until one jug is either empty or full
	public static Pair Pour(int capA, int capB, int capC, Stack<int[]> history, ArrayList<int[]> states, int[] node) { // pass states, history, capacity.
		// for all 6 combinations of pouring i.e. a-b, a-c etc. check if swapping makes a new state
		// if swapping makes a new state call transfer and apply pour on that new state.
		if(!checkExists(Transfer(0, 1, 2, capB, node), states)) {
			int[] newnode = Transfer(0, 1, 2, capB, node);
			states.add(newnode);
			history.push(newnode);
			Pour(capA, capB, capC, history, states, newnode);
		}else if(!checkExists(Transfer(0, 2, 1, capC, node), states)) {
			int[] newnode = Transfer(0, 2, 1, capC, node);
			states.add(newnode);
			history.push(newnode);
			Pour(capA, capB, capC, history, states, newnode);
		}else if(!checkExists(Transfer(1, 0, 2, capA, node), states)) {
			int[] newnode = Transfer(1, 0, 2, capA, node);
			states.add(newnode);
			history.push(newnode);
			Pour(capA, capB, capC, history, states, newnode);
		}else if(!checkExists(Transfer(1, 2, 0, capC, node), states)) {
			int[] newnode = Transfer(1, 2, 0, capC, node);
			states.add(newnode);
			history.push(newnode);
			Pour(capA, capB, capC, history, states, newnode);
		}else if(!checkExists(Transfer(2, 0, 1, capA, node), states)) {
			int[] newnode = Transfer(2, 0, 1, capA, node);
			states.add(newnode);
			history.push(newnode);
			Pour(capA, capB, capC, history, states, newnode);
		}else if(!checkExists(Transfer(2, 1, 0, capB, node), states)) {
			int[] newnode = Transfer(2, 1, 0, capB, node);
			states.add(newnode);
			history.push(newnode);
			Pour(capA, capB, capC, history, states, newnode);
		}
		Pair pair = new Pair(history, states);
		return pair;
	}
	
	public static int[] Transfer(int jug1, int jug2, int jug3, int cap2, int[] node) {//always pour from 1 to 2.
		
		int jugtoempty = node[jug1];
		int jugtofill = node[jug2];
		int count = jugtoempty;
		// if not full then fill until it reaches capacity for each unit of water in jug to empty
		if(jugtofill < cap2) {
			for(int i=0; i<count; i++) {
				jugtoempty -= 1;
				jugtofill += 1;
				if(jugtofill == cap2) {
					break;
				}
			}
		}
		//return the new state(node)
		int[] newnode = new int[3];
		newnode[jug1] = jugtoempty;
		newnode[jug2] = jugtofill;
		newnode[jug3] = node[jug3];
		return newnode;
	}
	
	public static void showStates(ArrayList<int[]> states) {
		// for each element in the list output it.
		for(int i=0; i<states.size(); i++) {
			System.out.println(Arrays.toString(states.get(i)));
		}
	}
	
	public static boolean checkExists(int a, int b, int c, ArrayList<int[]> states) {
		// check exists by passing each value in array
		int[] Node = {a,b,c};
		boolean valid = false;
		// can't use .contains as each array has unique ID even if same values
		for(int[]state : states) {
			if(Node[0] == state[0] && Node[1] == state[1] && Node[2] == state[2]) {
				valid = true;
			}
		}
		return valid;
	}
	
	public static boolean checkExists(int[] Node, ArrayList<int[]> states) {
		// check exists by passing whole array
		boolean valid = false;
		for(int[]state : states) {
			if(Node[0] == state[0] && Node[1] == state[1] && Node[2] == state[2]) {
				valid = true;
			}
		}
		return valid;
	}
}

