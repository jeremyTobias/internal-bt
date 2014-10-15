import java.util.Comparator;
import java.util.ArrayList;

/**
* BTree node class
*/
public class BTNode<E extends Comparable<E>> {
	Comparator<? super E> ordering;

	ArrayList<E> keys;
	ArrayList<BTNode<E>> links;

	int m;

	/**
	* Initialize the node
	* All nodes start with an initial size for both the keys in the node and
	* the links(leaves/children) for that node
	* @param m - the size of the node or order of the node; O(m)
	*/
	public BTNode(int m) {
		ordering = new NaturalComparator<E>();
		keys = new ArrayList<E>(m - 1);
		links = new ArrayList<BTNode<E>>(m);
		this.m = m;
	}

	/********************************************************
	*														*
	*					Node Splitting						*
	*														*
	********************************************************/

	/**
	* General node splitting
	* Split a full node, creating new left and right child of O(m).
	* Add the values from the current node to the appropriate child and acquire any current children of
	* the node being split.
	* @param n - node being split (child of this()), the parent of n is called from the InternalBT class
	*/
	public void splitNode(BTNode<E> n) {
		BTNode<E> l = new BTNode<E>(m); // new left child of n
		BTNode<E> r = new BTNode<E>(m); // new right child of n

		int middle = (m / 2) - 1;
		E temp = n.keys.remove(middle); // store middle value

		// place current node's values in appropriate leaf nodes
		for (int i = 0; i < middle; i++) {
			l.keys.add(n.keys.get(i));
		}

		for (int i = middle; i < n.keys.size(); i++) {
			r.keys.add(n.keys.get(i));
		}

		// if there are children add them to the new children
		if (!n.emptyLeaves()) {
			for (int i = 0; i < middle + 1; i++) {
				l.links.add(n.links.get(i));
			}

			for (int i = middle + 1; i < n.links.size(); i++) {
				r.links.add(n.links.get(i));
			}
		}

		int j = links.indexOf(n);

		keys.add(j, temp); // promotes middle into current node and "push" everything to right
		links.set(j, l); // set n to new leaf l
		links.add(j + 1, r); // add new leaf r
	}

	/**
	* Special split case, where root node is full.
	* Create a "new" root node and children.
	* Should only be called when root is full, due to clearing of array lists.
	* @param root - the root node
	*/
	public void splitRoot(BTNode<E> root) {
		BTNode<E> l = new BTNode<E>(m); // left child of new root
		BTNode<E> r = new BTNode<E>(m); // right child of new root

		int middle = (m / 2) - 1;
		E temp = root.keys.remove(middle); // store the middle value

		// place the current root's values in the new children
		for (int i = 0; i < middle; i++) {
			l.keys.add(root.keys.get(i));
		}

		for (int i = middle; i < root.keys.size(); i++) {
			r.keys.add(root.keys.get(i));
		}

		// if the current root has children, make those children the new root's childrens' children
		if (!root.emptyLeaves()) {
			for (int i = 0; i < middle + 1; i++) {
				l.links.add(root.links.get(i));
			}

			for (int i = middle + 1; i < root.links.size(); i++) {
				r.links.add(root.links.get(i));
			}
		}

		// clean up the old root and make it new and shiny
		root.keys.clear();
		root.links.clear();
		root.keys.add(temp);
		root.links.add(l);
		root.links.add(r);
	}

	/********************************************************
	*														*
	*					Balancing Act						*
	*														*
	********************************************************/

	/**
	* Balance the tree.
	* Checks 3 potential cases to attempt to keep the tree balanced.
	* Case 1 - There is a right sibling available and can spare a value.
	* Case 2 - There is a left sibling available and can spare a value.
	* Case 3 - Neither 1 or 2 can be done, so merge to the left or right.
	* @param n - node where balancing starts
	*/
	public void balanceTree(BTNode<E> n) {
		BTNode<E> l = null; // left sibling
		BTNode<E> r = null; // right sibling

		int i = links.indexOf(n);
		int min = (m / 2) - 1; // minimum number of keys allowed in a node

		if (i > 0) { // set the left sibling
			l = links.get(i - 1);
		}
		if (i < links.size() - 1) { // set the right sibling
			r = links.get(i + 1);
		}

		if (r != null && r.keys.size() > min) { // case 1
			// perform a left rotation
			n.keys.add(keys.get(i));
			keys.set(i, r.keys.remove(0));

			if (!n.emptyLeaves()) { // if there are children, close the gap created
				n.links.add(r.takeMinLink());
			}
		} else if (l != null && l.keys.size() > min) { // case 2
			// perform a right rotation
			n.keys.add(0, keys.get(i - 1));
			keys.set(i - 1, l.keys.remove(l.keys.size() - 1));
			
			if (!n.emptyLeaves()) { // if there are children, close the gap created
				n.links.add(0, l.takeMaxLink());
			}
		} else { // case 3
			BTNode<E> u = new BTNode<E>(m); // all purpose node

			/*
			* If n is the first node, merge to the right
			* else merge to the left
			* Take all of the values from either right or left, including links and place them in the new node u
			*/
			if (i == 0) {
				for (E f : n.keys) { // get all values from n
					u.keys.add(f);
				}

				u.keys.add(keys.remove(i)); // merge parent value

				for (E f : r.keys) { // get all values from right sibling
					u.keys.add(f);
				}

				if (!n.emptyLeaves()) { // if there are children, get them
					for (BTNode<E> q: n.links) { 
							u.links.add(q);
					}
					for (BTNode<E> q: r.links) {
							u.links.add(q);
					}
				}

				// close gap created
				links.set(i, u);
				links.remove(i + 1);
			} else {
				for (E f : l.keys) { // get all values from left sibling
					u.keys.add(f);
				}

				u.keys.add(keys.remove(i - 1)); // merge parent value

				for (E f : n.keys) { // get all values from n
					u.keys.add(f);
				}

				if (!n.emptyLeaves()) { // if there are children, get them
					for (BTNode<E> q: l.links) {
							u.links.add(q);
					}
					for (BTNode<E> q: n.links) {
							u.links.add(q);
					}
				}

				// close gap created
				links.remove(i);
				links.set(i - 1, u);
			}
		}
	}

	/**
	* Steal the predecessor value.
	* @param n - node value is being taken from
	*/
	public E stealPred(BTNode<E> n) {
		if (n.emptyLeaves()) { // if there are no children, take the largest value in node n
			return n.keys.remove(n.keys.size() - 1);
		}
		BTNode<E> u = n.links.get(n.links.size() - 1); // get largest child
		return n.stealPred(u); // recurse, try again
	}

	/**
	* Steal the successor value.
	* @param n - node value is being taken from
	*/
	public E stealSuc(BTNode<E> n) {
		if (n.emptyLeaves()) { // if there are no children, take the smallest value in node n
			return n.keys.remove(0);
		}
		BTNode<E> u = n.links.get(0); // get smallest child
		return n.stealSuc(u); // recurse, try again
	}

    // gets and removes the max link
	private BTNode<E> takeMaxLink() {
		return links.remove(links.size() - 1);
	}

	// gets and removes the min link
	private BTNode<E> takeMinLink() {
		return links.remove(0);
	}

	/********************************************************
	*														*
	*					Node Helpers						*
	*														*
	********************************************************/

	/**
	* Checks to see if the value trying to be added is already in the node.
	* @param e - value being searched for
	*/
	public boolean inNode(E e) {
		return keys.contains(e);
	}

	/**
	* Checks if the leaves are empty.
	*/
	public boolean emptyLeaves() {
		return links.isEmpty();
	}

	/**
	* Checks to see if a node is full
	* @param n - node being checked
	*/
	public boolean isFull(BTNode<E> n) {
		return (n.keys.size() >= (m - 1));
	}

	/**
	* Checks to see if current node has too few values.
	*/
	public boolean underflowed() {
		return (keys.size() < (m / 2) -1);
	}

	/********************************************************
	*														*
	*				Display Operation						*
	*														*
	********************************************************/

	/**
	* Display the node and it's children in a manner that a BTree may be seen.
	* @param spacer - adds additional space to each leaf node in order to differentiate parent and child
	*/
	public void display(int spacer) {
        for (E e : keys) { // display the keys for the current node
            System.out.print(e + ", ");
        }

        System.out.println();
	    
	    // while there are leaves available, get them and display them accordingly    
        for (int i = 0; i < links.size(); i++) {
            for (int j = 0; j < spacer; j++) {
                System.out.print(" "); // make some space
            }

            System.out.print("  " + i + ": ");
            
            if (links.get(i) != null) { // if leaf exists
                links.get(i).display(spacer + 2); // display leaf at i, and space accordingly
            }
        }

        System.out.println();
    }
}