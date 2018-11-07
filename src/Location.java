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

/**
 * @author abhanshu 
 * This class is a template for implementation of 
 * HW1 for CS540 section 2
 */
/**
 * Data structure to store each node.
 */
class Location {
	private int x;
	private int y;
	private Location parent;

	public Location(int x, int y, Location parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Location getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return x + " " + y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Location) {
			Location loc = (Location) obj;
			return loc.x == x && loc.y == y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * (hash + x);
		hash = 31 * (hash + y);
		return hash;
	}
}

