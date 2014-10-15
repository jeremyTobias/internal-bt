import java.io.*;
import java.util.*;

/**
* Tests the functionality of an internal BTree.
* Insures both add and remove methods work for all cases.
*/
public class BTreeTests {
	public static void main (String[] args) {
		InternalBT<String> bTree;

		/*try {
			bTree = new InternalBT<String>();
			System.out.println("------ Test 1 ------\n");
			System.out.println("Add a bunch of values to the tree and manually split root.\n");

			bTree.add("monkey");
			bTree.add("two");
			bTree.add("paladin");
			bTree.add("tank");
			bTree.add("sword");
			bTree.add("cookie");
			bTree.add("beer");

			bTree.displayTree();

			System.out.println("*** Splitting Root ***\n");
			bTree.root.splitRoot(bTree.root);

			bTree.displayTree();

			System.out.println("------ Test 1 ------\n");
		} catch (Exception e) { e.printStackTrace(); }

		try {
			System.out.println("------ Test 2 ------\n");
			System.out.println("Add numbers to tree to assure proper ordering.\n");

			InternalBT<Integer> intTree = new InternalBT<Integer>();

			intTree.add(1);
			intTree.add(4);
			intTree.add(65);
			intTree.add(2);
			intTree.add(9);
			intTree.add(10);
			intTree.add(54);
			intTree.add(23);
			intTree.add(12);
			intTree.add(3);
			intTree.add(15);
			intTree.add(5);
			intTree.add(75);
			intTree.add(20);

			intTree.displayTree();

			System.out.println("------ Test 2 ------\n");

			System.out.println("------ Test 3 ------\n");
			System.out.println("Delete all numbers from the tree to assure proper deletion.\n");

			intTree.remove(65);
			intTree.displayTree();
			intTree.remove(5);
			intTree.displayTree();
			intTree.remove(12);
			intTree.displayTree();
			intTree.remove(15);
			intTree.displayTree();
			intTree.remove(10);
			intTree.displayTree();
			intTree.remove(1);
			intTree.displayTree();
			intTree.remove(9);
			intTree.displayTree();
			intTree.remove(23);
			intTree.displayTree();
			intTree.remove(4);
			intTree.displayTree();
			intTree.remove(2);
			intTree.displayTree();
			intTree.remove(3);
			intTree.displayTree();
			intTree.remove(54);
			intTree.displayTree();
			intTree.remove(20);
			intTree.displayTree();
			intTree.remove(75);
			
			if (intTree.root == null) {
				System.out.println("Tree is empty.");
			} else {
				intTree.displayTree();
			}

			System.out.println("------ Test 3 ------\n");
		} catch (Exception e) { e.printStackTrace(); }

		try {
			System.out.println("------ Test 4 ------\n");
			System.out.println("Add numbers to tree to assure proper ordering.\n");

			InternalBT<Integer> intTree = new InternalBT<Integer>();

			for (int i = 0; i < 50; i++) {
				int randAdd = (int)(Math.random() * 100);
				System.out.println("Adding " + randAdd + " to tree.");
				intTree.add(randAdd);
			}

			intTree.displayTree();

			System.out.println("------ Test 4 ------\n");

			System.out.println("------ Test 5 ------\n");
			System.out.println("Delete some numbers from the tree to assure proper deletion.\n");

			for (int i = 0; i < 25; i++) {
				int randRem = (int)(Math.random() * 100);
				System.out.println("Deleting " + randRem + " from tree.");
				intTree.remove(randRem);
			}

			intTree.displayTree();

			System.out.println("------ Test 5 ------\n");
		} catch (Exception e) { e.printStackTrace(); }

		try {
			bTree = new InternalBT<String>();
			System.out.println("------ Test 6 ------\n");
			System.out.println("Add a bunch of values to the tree, some of which are repeating.\n" +
								"Repeating values should only be in tree once.\n" +
								"Display tree after each insertion to show tree \"shape\", node and " +
								"root splitting.\n");
			bTree.add("low");
			bTree.displayTree();
			bTree.add("medium");
			bTree.displayTree();
			bTree.add("high");
			bTree.displayTree();
			bTree.add("cookie");
			bTree.displayTree();
			bTree.add("rock");
			bTree.displayTree();
			bTree.add("cookie");
			bTree.displayTree();
			bTree.add("monkey");
			bTree.displayTree();
			bTree.add("super");
			bTree.displayTree();
			bTree.add("half");
			bTree.displayTree();
			bTree.add("zebra");
			bTree.displayTree();
			bTree.add("juice");
			bTree.displayTree();
			bTree.add("baby");
			bTree.displayTree();
			bTree.add("coffee");
			bTree.displayTree();
			bTree.add("apple");
			bTree.displayTree();
			bTree.add("cookie");
			bTree.displayTree();
			
			System.out.println("------ Test 6 ------\n");

		} catch (Exception e) { e.printStackTrace(); }*/

		try {
			bTree = new InternalBT<String>();
			System.out.println("------ Test 1 ------\n");
			System.out.println("*****************************************************************");			
			System.out.println("*                                                               *");
			System.out.println("*   Add all the words from \"words.txt\" and then delete them.    *");
			System.out.println("*                                                               *");
			System.out.println("*****************************************************************");
			System.out.println("\n\n");

			BufferedReader fileInput;
			BufferedReader fileInput2;

			String nodeLoad;
			int limit = 0;
			fileInput = new BufferedReader(new InputStreamReader(new FileInputStream(
				new File("words.txt"))));
			Scanner input = new Scanner(fileInput);

			System.out.println("***** Adding words to tree, please wait... *****\n");
			while (input.hasNext()) {
				nodeLoad = input.next();
				bTree.add(nodeLoad);
				limit++;
			}

			System.out.println(limit + " words added.");
			int j = 0;
			fileInput2 = new BufferedReader(new InputStreamReader(new FileInputStream(
				new File("words.txt"))));
			Scanner input2 = new Scanner(fileInput2);

			System.out.println("***** Attempting to delete all words from tree, please wait... *****\n");
			while (input2.hasNext()) {
				nodeLoad = input2.next();
				bTree.remove(nodeLoad);
				j++;
			}

			System.out.println(j + " words deleted.");
			if (bTree.root == null) {
				System.out.println("Tree is empty.\n");
			} else {
				bTree.displayTree();
			}

			System.out.println("------ Test 1 ------\n");
		} catch (Exception e) { e.printStackTrace(); }
	}
}