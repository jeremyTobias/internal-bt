import java.util.Comparator;
import java.util.ArrayList;

public class InternalBT<E extends Comparable<E>> {
	private static final int M = 8; // set to 'x' to keep a constant size for tree

	Comparator<? super E> ordering;

	BTNode<E> root;

	int m; // mutable version of M

	/**
	* Initialize BTree with a root node of O(M)
	*/
	public InternalBT() {
		root = new BTNode<E>(M);
		ordering = new NaturalComparator<E>();
		m = M;
	}

	/**
	* Add an item to the tree.
	* First check to see if the root is full.
	* If the root is full, split the root, else insert the item through the node class.
	* Returns true if value is added, false otherwise.
	* @param item - item to be added to tree
	*/
	public boolean add(E item) {
		if (root.isFull(root)) {
			root.splitRoot(root);
		}
		return insert(root, item);
	}

	/**
	* Remove an item from the tree.
	* Start deletion from the root.
	* If, after deletion has completed, the root is completely empty, set root to null.
	* If, after deletion has completed, the root has no values, but has children, make a new root.
	* Returns true if value has been deleted from the tree, false otherwise.
	* @param item - item to be removed from tree.
	*/
	public boolean remove(E item) {
		boolean b = delete(root, item);
		
		if (!b) {
			debugger(item + " not in tree");
		}

		if (root.keys.isEmpty()) {
			if (root.emptyLeaves()) { 
				root = null;
			} else {
				BTNode<E> newRoot = root.links.get(0);
				root = newRoot;
			}
		}
		return b;
	}

	/**
	* Insert a value into the node.
	* First check to see if the value already exists in the tree.
	* If value exits, leave. Else, find where the value should live and place it there.
	* Then check to see if the node is full. If it's full, split it, and reorganize the tree.
	*/
	private boolean insert(BTNode<E> n, E e) {
		if (n.inNode(e)) { // if value exists, leave
			debugger("\n************* " + e + " already in tree. *************\n");
			return false;
		}

		int i = 0; // index tracker
		for (E f : n.keys) {
			// compare incoming value to values already in current node, find where incoming value should live
			if (ordering.compare(e, f) > 0) { i++; }
		}

		if (n.emptyLeaves()) { // check if the leaves are empty
			n.keys.add(i, e); // add if there are no children
			return true;
		}

		/*
		* If there are children, get the one where the value should live.
		* First check if the child is full, if it is, split it, and find which node 
		* incoming value should live in.
		* Recurse, to attempt to add incoming value again. Rinse, repeat.
		* n is the parent node when performing split operation, and u is n's child.
		*/

		BTNode<E> u = n.links.get(i); // child at index i

		if (n.isFull(u)) { // if full, split
			n.splitNode(u);

			// find node where value should live
			if (ordering.compare(e,n.keys.get(i)) < 0) {
				u = n.links.get(i);
			} else {     
				u = n.links.get(i + 1);
			} 
		}

		return insert(u, e); // recurse, attempting to add value to current leaf node
	}	

	/**
	* Delete a value from the tree.
	* Checks to see if the current node has no children, if it does not, then attempt to delete the value.
	* If the node does have children, first check to see if the value is in the node.
	* If the value is in the node perform a series of "steals" and tree balancing (see BTNode) operations,
	* replacing the value in question and keeping the tree balanced.
	* If the value is not in the current node, search for where the node should live, and attempt deletion again.
	* @param n - node value is attempting to be deleted from
	* @param e - value attempting to be deleted from tree
	*/
	private boolean delete(BTNode<E> n, E e) {
		BTNode<E> u; // all purpose node
		int i; // index tracking

		/*
		* If there are no children attempt to remove the value from the node.
		* Since root is always called first, the only time this would execute first is if the root has no children.
		* Since the root is the only node allowed to be < (m / 2) - 1, no need to check for underflowing.
		* Else after the return on a recursive call, the node will be checked for underflowing.
		*/
		if (n.emptyLeaves()) {
			return n.keys.remove(e);
		}

		if (n.inNode(e)) { // if the value is in the current node
			i = n.keys.indexOf(e); // index of value being deleted

			if (i == 0) { // if the value is the first value, steal its successor from its right child (min value of left sibling)
				u = n.links.get(i + 1); // right child of n
				n.keys.set(i, n.stealSuc(u)); // replace n value with successor from u
				balanceRSubTree(u); // fix the right subtree
				if (u.underflowed()) { // if too few values, fix tree
					n.balanceTree(u);
				}
			} else { // all other cases, steal its predecssor from its left child (max value in left child)
				u = n.links.get(i); // left child of n
				n.keys.set(i, n.stealPred(u)); // replace n value with predecessor from u
				balanceLSubTree(u); // fix the left subtree
				if (u.underflowed()) { // if too few values, fix tree
					n.balanceTree(u);
				}
			}

			return true;
		}

		i = 0; // value not in node, set index to zero, try again
		for (E f : n.keys) {
			// compare incoming value to values already in current node, find where incoming value should live
			if (ordering.compare(e, f) > 0) { i++; }
		}

		u = n.links.get(i);

		boolean b = delete(u, e); // recurse, attempting to delete value on new node
		
		if (u.underflowed()) { // if too few values, fix tree
			n.balanceTree(u);
		}

		return b;
	}

	/**
	* Checks to make sure the max node value was stolen from is balanced.
	* If it's the last node, then leave.
	* If it's not, get the next max node and check that node.
	* Recurse, checking if nodes are well balanced.
	*/
	private void balanceLSubTree(BTNode<E> n) {
        if (n.emptyLeaves()) { return; }
        BTNode<E> u = n.links.get(n.links.size()-1);
        balanceLSubTree(u);
        if (u.underflowed()) {
            n.balanceTree(u);
        }
    }

    /**
	* Checks to make sure the node value was stolen from is balanced.
	* If it's the last node, then leave.
	* If it's not, get the next max node and check that node.
	* Recurse, checking if nodes are well balanced.
	*/
    private void balanceRSubTree(BTNode<E> n) {
        if (n.emptyLeaves()) { return; }
        BTNode<E> u = n.links.get(0);
        balanceRSubTree(u);
        if (u.underflowed()) {
            n.balanceTree(u);
        }
    }

	/**
	* Display the tree, starting at the root.
	*/
	public void displayTree() {
		root.display(0);
	}

	// display a debug message
	private void debugger(String s) {
		if (Debugger.isEnabled()) {
			Debugger.log(s);
		}
	}
}