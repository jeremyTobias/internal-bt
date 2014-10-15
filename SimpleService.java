import java.net.*;
import java.io.*;
import java.util.*;

public class SimpleService {
	static final int PORT = 2389; // use 2-digit number selected in call for "47"

	static InternalBT<String> treeChecker = new InternalBT<String>();

	public static void main(String[] args) {
		BufferedReader fileInput;

		String loadDictionary;

		try { // get the dictionary file
			fileInput = new BufferedReader(new InputStreamReader(new FileInputStream(
				new File("words.txt"))));
			Scanner input = new Scanner(fileInput);
			while (input.hasNext()) {
				loadDictionary = input.next();
				treeChecker.add(loadDictionary);
			}
		} catch (IOException e) { }

		try { // set up server daemon
			ServerSocket serverSocket = new ServerSocket(PORT);

			//treeChecker.displayTree();

			for (;;) {
				Socket client = serverSocket.accept();

				PrintWriter out = new PrintWriter(client.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

				String cmd = in.readLine();
				String cmd2 = cmd.substring(5, cmd.length() - 9); // cleans up GET command, only takes word input
				
				char[] ca = cmd2.toCharArray(); // set word to an array, used when searching for deletion initialization

				String response = "";

				/*
				* If there is currently no value, or GET throws a favico, ignore it
				* else perform either a deletion or addition/search on the tree
				*/
				if (cmd2.equals("") || cmd2.equals("favicon.ico")) {
					response = "Waiting for a word...";
				} else {
					if (ca[0] == '-') { // - is used to initiate deletion
						if (!treeChecker.remove(cmd2.substring(1))) {
							response = cmd2.substring(1) + " not in the tree.";
						} else {
							response = cmd2.substring(1) + " deleted from tree.";
						}
					} else {
						if (!treeChecker.add(cmd2)) {
							response = cmd2 + " already in tree.";
						} else {
							response = cmd2 + " added to tree.";
						}
					}
				}

				String reply = "<!DOCTYPE HTML>\n<html>\n" +
					"<head><title>Testing</title>" + 
					"</head>\n" +
					"<body><h1>Welcome</h1>" + 
					"<p>To search for or try to add a word, please type \"/some word\" " + 
					"into the address bar after 2389. Example: /cookie</p>" +
					"<p>If the \"word\" does not already exist, it will be added to the tree.</p>" +
					"<p>To delete a word place a - in front of the \"word\". Example: /-cookie</p>" +
					"Got request:<br />\n" + response + 
					"</body>\n" +
					"</html>";
			
				int len = reply.length();

				out.println("HTTP/1.1 200 OK");
				out.println("Content-Length: " + len);
				out.println("Content-Type: text/html\n");
				out.println(reply);

				out.close();
				in.close();
				client.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}
}	