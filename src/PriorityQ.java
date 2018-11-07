import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;

/**
 * @author abhanshu 
 * This class is a template implementation of priority queue
 *         provided for implementing A* algorithm
 */
public class PriorityQ<T> {

	/**
	 * class variables queue: to store the data as a queue lastMatch: helps to
	 * modify a queue entry faster
	 */
	private List<SimpleEntry<T, Integer>> queue;
	private SimpleEntry<T, Integer> lastMatch;
	/**
	 * constructor to create a priority queue
	 */
	public PriorityQ() {
		lastMatch = null;
		queue = new LinkedList<SimpleEntry<T, Integer>>();
	}

	/**
	 * adds the element into the priority queue if the score is >= 0 and entry
	 * object is non null.
	 */
	public void add(T entry, int score) {
		if (entry != null && score >= 0) {
			int i = 0;
			for (SimpleEntry<T, Integer> element: queue) {
				if (element.getValue() > score) {
					break;
				}
				i++;
			}
			queue.add(i, new SimpleEntry<T, Integer>(entry, score));
		}
	}
	
	/**
	 * removes the element from the priority queue if it exists.
	 */
	public void remove(T entry) {
		if(entry != null){
			int i = 0;
			for (SimpleEntry<T, Integer> element: queue) {
				if(element.getKey().equals(entry)){
					break;
				}
				i++;
			}
			queue.remove(i);
		}
	}

	/**
	 * removes and returns the first element and its priority score from the queue. 
	 * Returns null if the queue is empty
	 */
	public SimpleEntry<T,Integer> poll() {
		if (!queue.isEmpty()) {
			return queue.remove(0);
		}
		return null;
	}

	/**
	 * returns the first element from the queue without removing it from the
	 * queue. Returns null if the queue is empty
	 */
	public SimpleEntry<T,Integer> peek() {
		if (!queue.isEmpty()) {
			return queue.get(0);
		}
		return null;
	}

	/**
	 * this method returns true if the queue is empty else will return false
	 */
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	/**
	 * this method returns the number of elements in the queue. It will return 0
	 * if the queue is empty
	 */
	public int size() {
		return queue.size();
	}

	/**
	 * this method returns true if the passed element exists in the queue else
	 * will return false
	 */
	public boolean exists(T element) {
		if (element != null) {
			for (SimpleEntry<T, Integer> eachEntry : queue) {
				if (eachEntry.getKey().equals(element)) {
					lastMatch = eachEntry;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * this method returns the priority score associated with the passed
	 * element. A score of -1 will be returned if the element doesnot exists in
	 * the list.
	 */
	public int getPriorityScore(T element) {
		int score = -1;
		if (element != null) {
			if (lastMatch != null && element.equals(lastMatch.getKey())) {
				return lastMatch.getValue();
			} else {
				for (SimpleEntry<T, Integer> eachEntry : queue) {
					if (eachEntry.getKey().equals(element)) {
						lastMatch = eachEntry;
						return eachEntry.getValue();
					}
				}
			}
		}
		return score;
	}

	/**
	 * this method replaces the existing element in the queue with passed entry based
	 *  on the new priority. If the passed element does not exists in the queue it will
	 *  simply add it based on priority.
	 */
	public void modifyEntry(T entry, int score){
		remove(entry);
		add(entry,score);
	}
}
