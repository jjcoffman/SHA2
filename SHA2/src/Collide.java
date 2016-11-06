import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * ShaFinder is a multithreaded application to check for collisions in random byte values
 * when hashed using sha256. The values and their hashes are stored in an AVL tree to reduced the time
 * complexity for searching and inserting. The size of the AVL tree can be adjusted as needed or allowed.
 * Random values are used do to the needed size of 2^129 nodes cannot be fulfilled on the authors machine.
 * Another possible route would be a different data structure who's index is the value hashed, however search 
 * time complexity will likely be linear.
 * 
 * Assignment 4 csc 152 Fall 2016
 * @author Jonathan Coffman - 7965
 * @version 1 = 10/30/2016
 */
public class Collide 
{
	static Thread_Runner[] thread;
	static int THREAD_COUNT = 8;
	static AVLTree tree;
	static int TREE_SIZE = 50000000; // I limited this here due to machine constraints.
	public static void main(String[] args) 
	{
		int i = 0;
		thread = new Thread_Runner[THREAD_COUNT];
		tree = AVLTree.getTree();
		Controller control = Controller.getController();
		for(i = 1; i <= THREAD_COUNT; i++)
		{
			Collide.thread[i-1] = new Collide.Thread_Runner(("Thread-" + i), control, tree);
			thread[i-1].start();
		}
	}

	/**
	 * Here is our thread running class. It will handla value generation, hoshing, searches, inserts (synchronized),
	 * and writing output via controller.
	 * @author Jonathan Coffman
	 *
	 */
	static class Thread_Runner implements Runnable 
	{
		private Thread t;
		private String threadName;
		private Controller control;
		private AVLTree tree;

		Thread_Runner(String name, Controller control, AVLTree t) 
		{
			threadName = name;
			this.control = control;
			tree = t;
		}

		public void run() 
		{
			System.out.println("Running Thread: " + threadName);
			MessageDigest md = null;
			try 
			{
				md = MessageDigest.getInstance("SHA-256");
			} 
			catch (NoSuchAlgorithmException e) 
			{
				e.printStackTrace();
			}
			do
			{
				int size = (new Random().nextInt(100)); //generate random value NOTE: constrained due to HW limits.
				byte[] XBytes = new byte[size];
				new Random().nextBytes(XBytes);
				md.update(XBytes, 0, size); //here we calculate the hash based on sha 256

				byte[] shaX = md.digest();
				md.reset();
				search(XBytes, shaX); //search the tree
				insert(shaX, XBytes); //insert NOTE: only performs up to the size limitations of TREE_LIMIT
				XBytes = null;
				shaX = null;
			}
			while(true);
		}
		
		/**
		 * Search performs a tree search and then calls write out if a match larger than a previously
		 * reported match is found.
		 * @param XBytes byte[]
		 * @param shaX byte[]
		 */
		private synchronized void search(byte[] XBytes, byte[] shaX) 
		{
			Object[] obj = tree.search(new AVLNode(shaX, XBytes));
			int currentCount = (int)obj[0];
			if(currentCount > control.getMax() && currentCount != 32)
			{
				System.out.println(threadName + " Max: " + currentCount);
				try 
				{
					byte[] YBytes = ((AVLNode)obj[1]).binValue;
					byte[] shaY = ((AVLNode)obj[1]).shaValue;
					writeOut(XBytes, YBytes, shaX, shaY, currentCount);
					YBytes = null;
					shaY = null;
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}

		}

		/**
		 * Simply calls the AVL tree's insert method.
		 * @param shaX byte[]
		 * @param xBytes byte[]
		 */
		private synchronized void insert(byte[] shaX, byte[] xBytes) 
		{
			tree.insert(shaX, xBytes);
		}

		/**
		 * This is a synchonized write method using a mutex for each thread.
		 * @param X byte[]
		 * @param Y byte[]
		 * @param ShaX byte[]
		 * @param ShaY byte[]
		 * @param count int
		 * @throws IOException
		 */
		private void writeOut(byte[] X, byte[] Y, byte[] ShaX, byte[] ShaY, int count) throws IOException
		{
			while(control.getRunning() > 0) {};
			control.writeOut(X, Y, ShaX, ShaY, count);
		}

		/**
		 * Thread start method
		 */
		public void start() 
		{
			if (t == null) 
			{
				t = new Thread (this, threadName);
				t.start ();
			}
		}

	}

	/**
	 * AVLTree build a balanced AVL tree using AVLNode objects. AVLTree uses Singleton design patterns
	 * to allow only one instance across all threads.
	 * @author Jonathan Coffman - 7965
	 *
	 */
	static class AVLTree 
	{
		private static AVLTree tree;
		AVLNode root = new AVLNode();
		private int cleaner;
		
		/**
		 * Private constructor for the singleton instance of the tree.
		 */
		private AVLTree()
		{
			root = null;
			cleaner = 0;
		}
		
		/**
		 * Public get method to return the tree object
		 * @return AVLTree tree
		 */
		public static AVLTree getTree()
		{
			if(tree == null)
				return tree = new AVLTree();
			return tree;
		}

		/**
		 * Inserts an item into the tree, this is synchronized for multiple threads.
		 * @param sha byte[]
		 * @param val byte[]
		 * @return node AVLNode
		 */
		public synchronized AVLNode insert(byte[] sha, byte[] val)
		{
			if(cleaner < TREE_SIZE)
			{
				cleaner++;
				root = insert(new AVLNode(sha, val), 31, root);
			}
			if(cleaner == TREE_SIZE)
			{
				cleaner++;
				System.out.println("Full");
			}
			return(checkRotation(root));
		}

		/**
		 * performs actual insertion recursively. called privately via insert(byte[] byte[])
		 * @param newNode AVLNode
		 * @param position int
		 * @param current AVLNode
		 * @return node AVLNode
		 */
		private synchronized AVLNode insert(AVLNode newNode, int position, AVLNode current)
		{
			if(current == null || position == -1) //handles adding the node to the list when it reaches an empty position
			{
				current = newNode;
			}	
			else if(newNode.shaValue[position] > current.shaValue[position]) // > handles inserting to the right of a value
			{
				if(current.right == null)
					current.right = insert(newNode, position, null);
				else
					current.right = insert(newNode,position, current.right);
			}
			else if(newNode.shaValue[position] < current.shaValue[position]) //handles inserting to the left of a value
			{
				if(current.left == null)
					current.left = insert(newNode, position, null);
				else
					current.left = insert(newNode, position, current.left);
			}
			else if(newNode.shaValue[position] == current.shaValue[position])
			{
				current = insert(newNode, position-1, current);
			}
			setHeight(current);
			return(checkRotation(current));
		}

		/**
		 * Searches the tree for a match
		 * @param data
		 * @return OBJ 0=match, 1=treefile, 2=searchedFile
		 */
		public Object[] search(AVLNode data) 
		{
			AVLNode local = root;
			int matchCount = 0;
			AVLNode match = new AVLNode();

			while (local != null)
			{
				matchCount = Compare(data, local);
				if(matchCount > 0)
				{
					match.binValue = local.binValue;
					match.shaValue = local.shaValue;
				}

				local = checkMatch(local, data, 31, matchCount);
			}	
			Object[] obj = new Object[3];
			obj[0] = matchCount;
			obj[1] = match;
			obj[2] = data;

			return obj;
		}

		/**
		 * Due to the advanced nature of a byte array and checking against the tail backwards, this method is used in search
		 * to correctly traverse the tree and find subsequent matches based on correct insertion.
		 * @param local AVLNode
		 * @param data AVLNode
		 * @param position int
		 * @param matchCount int
		 * @return
		 */
		private AVLNode checkMatch(AVLNode local, AVLNode data, int position, int matchCount)
		{
			if (position == 0 || matchCount == 32)
			{
				return null;
			}
			if (local.shaValue[position] > data.shaValue[position])
			{
				if(local.left == null)
					return null;
				else
					return local.left;
			}
			else if(local.shaValue[position] < data.shaValue[position])
			{
				if(local.right == null)
					return null;
				else
					return local.right;
			}
			else
			{
				return (checkMatch(local, data, position-1, matchCount));
			}

		}

		/**
		 * This compares the data node with the local and returns how many tail bytes match
		 * @param data AVLNode
		 * @param local AVLNode
		 * @return
		 */
		private int Compare(AVLNode data, AVLNode local) 
		{
			int matchCount = 0;
			int position = 31;
			while((!(position < 0)) && local.shaValue[position] == (data.shaValue[position]))
			{
				matchCount++;
				position--;
			}
			return matchCount;
		}

		/**
		 * This ensures that the correct height is set as the method recurses up the tree
		 * @param cur AVLNode
		 */
		private void setHeight(AVLNode cur) 
		{
			int lh = getHeight(cur.left);
			int rh = getHeight(cur.right);
			if(lh < rh)
				cur.height = 1 + rh;
			else
				cur.height = 1 + lh;
		}

		/**
		 * Here we return the height of the passed node accounting for its existence or lack thereof.
		 * @param a AVLnode
		 * @return int
		 */
		private int getHeight(AVLNode a)
		{
			if(a == null)
				return 0;
			else
				return a.height;
		}
		
		/**
		 * Validate the need for rotation and perform rotations ans needed.
		 * @param n
		 * @return
		 */
		private AVLNode checkRotation(AVLNode n)
		{
			int heightDiff = heightDiff(n);
			if (heightDiff < -1) {
				if (heightDiff(n.right) > 0) {
					n.right = rotateRight(n.right);
					return rotateLeft(n);
				} else {
					return rotateLeft(n);
				}
			} else if (heightDiff > 1) {
				if (heightDiff(n.left) < 0) {
					n.left = rotateLeft(n.left);
					return rotateRight(n);
				} else {
					return rotateRight(n);
				}
			} else;
			return n;
		}

		/**
		 * finds the difference between the left and right children
		 * @param n AVLNode
		 * @return int
		 */
		private int heightDiff(AVLNode n) 
		{
			if (n == null) {
				return 0;
			}
			return getHeight(n.left) - getHeight(n.right);
		}

		/**
		 * Performs single rotional Left
		 * @param top AVLNode
		 * @return AVLNode
		 */
		private AVLNode rotateLeft(AVLNode top)
		{
			AVLNode r = top.right;
			top.right = r.left;
			r.left = top;
			top.height = Math.max(getHeight(top.left), getHeight(top.right)) + 1;
			r.height = Math.max(getHeight(r.left), getHeight(r.right)) + 1;
			return r;
		}

		/**
		 * performs single rotation right
		 * @param top AVLNode
		 * @return AVLNode
		 */
		private AVLNode rotateRight(AVLNode top) //This performs the single rotation with right child
		{
			AVLNode r = top.left;
			top.left = r.right;
			r.right = top;
			top.height = Math.max(getHeight(top.left), getHeight(top.right)) + 1;
			r.height = Math.max(getHeight(r.left), getHeight(r.right)) + 1;
			return r;

		}
	}


	/**
	 * This defines the AVLNode objects which contain the raw data value and its hash.
	 * NOTE: publicly accessible values as security not a concern for initial implmentation.
	 * @author Jonathan Coffman - 7965
	 *
	 */
	static public class AVLNode 
	{
		byte[] shaValue;
		byte[] binValue;
		AVLNode left;
		AVLNode right;
		int height;

		/**
		 * Generic Constructor
		 */
		public AVLNode()
		{
			shaValue = null;
			binValue = null;
			left = null;
			right = null;
			height = 0;
		}
		/**
		 * Creates an AVLNode
		 * @param sha byte[]
		 * @param bin byte[]
		 */
		public AVLNode(byte[] sha, byte[] bin)
		{
			shaValue = sha;
			binValue = bin;
			height = 0;
			left = null;
			right = null;
		}

		/**
		 * Creates an AVL node with children if needed.
		 * @param sha
		 * @param bin
		 * @param left
		 * @param right
		 */
		public AVLNode(byte[] sha, byte[] bin, AVLNode left, AVLNode right)
		{
			shaValue = sha;
			binValue = bin;
			height = 0;
			this.left = left;
			this.right = right;
		}
	}

	/**
	 * This is the controller class that handles write out and other synchronization elements
	 * @author Jonathan Coffman -7965
	 *
	 */
	static public class Controller 
	{	
		static Controller control;
		int max;
		int runningThread;
		private Controller()
		{
			max = 0;
			runningThread = 0;
		}

		public static Controller getController()
		{
			if(control == null)
				control = new Controller();
			return control;
		}

		/**
		 * This method outputs the file to the users desktop with the raw data files as .bin files. and prints
		 * the SHA256 values to the terminal
		 * @param X byte[]
		 * @param Y byte[]
		 * @param ShaX byte[]
		 * @param ShaY byte[]
		 * @param count int
		 * @throws IOException
		 */
		public synchronized void writeOut(byte[] X, byte[] Y, byte[] ShaX, byte[] ShaY, int count) throws IOException
		{
			runningThread++;
			max = count;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < ShaX.length; i++) {
				sb.append(Integer.toString((ShaX[i] & 0xff) + 0x100, 16).substring(1));
			}
			System.out.println("Hex format for X: " + sb.toString());

			StringBuffer sb2 = new StringBuffer();
			for (int i = 0; i < ShaY.length; i++) {
				sb2.append(Integer.toString((ShaY[i] & 0xff) + 0x100, 16).substring(1));
			}
			System.out.println("Hex format for Y: " + sb2.toString());


			String path = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop" + 
					System.getProperty("file.separator");
			FileOutputStream file1 = new FileOutputStream(path + "X.bin");
			FileOutputStream file2 = new FileOutputStream(path + "Y.bin");
			file1.write(X);
			file2.write(Y);
			file1.close();
			file2.close();
			sb = null;
			sb2 = null;
			file1 = null;
			file2 = null;
			runningThread--;
		}

		public int getMax()
		{
			return max;
		}
		public int getRunning()
		{
			return runningThread;
		}
	}



}
