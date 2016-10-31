
class AVLTree 
{
	private static AVLTree tree;
	AVLNode root = new AVLNode();
	private AVLTree()
	{
		root = null;
	}
	
	public static AVLTree getTree()
	{
		if(tree == null)
			return tree = new AVLTree();
		return tree;
	}

	public synchronized AVLNode insert(byte[] sha, byte[] val)
	{
		root = insert(new AVLNode(sha, val), 31, root);
		switch (balanceNumber(root)) {
		case 1:
			root = rotateLeft(root);
			break;
		case -1:
			root = rotateRight(root);
			break;
		default:
			break;
		}
		return root;
	}

	private synchronized AVLNode insert(AVLNode newNode, int position, AVLNode current)
	{
		if(current == null) //handles adding the node to the list when it reaches an empty position
		{
			current = newNode;
		}
		
		if(32-position < 0) //handles if we reach the end of the line
		{
			if(current.right == null)
				current.left = insert(newNode, position, null);
			else
				current.left = insert(newNode, position, current.right);	
		}
		
		else if(newNode.shaValue[position] > current.shaValue[position]) // > handles inserting to the right of a value
		{
			if(current.right == null)
				current.right = insert(newNode, position-1, null);
			else
				current.right = insert(newNode, position-1, current.right);
		}
		else if(newNode.shaValue[position] > current.shaValue[position]) //handles inserting to the left of a value
		{
			if(current.left == null)
				current.left = insert(newNode, position-1, null);
			else
				current.left = insert(newNode, position-1, current.left);
		}
		current.height++;
		switch (balanceNumber(current)) 
		{
		case 1:
			current = rotateLeft(current);
			break;
		case -1:
			current = rotateRight(current);
			break;
		default:
			return current;
		}
		return current;
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
			if (local.shaValue[31-matchCount]>data.shaValue[31-matchCount])
			{
				if(local.left == null)
					local = null;
				else
					local = local.left;
			}
			else
			{
				if(local.right == null)
					local = null;
				else
					local = local.right;
			}
		}
		
		Object[] obj = new Object[3];
		obj[0] = matchCount;
		obj[1] = match;
		obj[2] = data;
		
		return obj;
	}
	
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

	private int balanceNumber(AVLNode node) 
	{
		int L = height(node.left);
		int R = height(node.right);
		else if (L - R >= 2)
			return -1;
		else if (L - R <= -2)
			return 1;
		return 0;
	}

	private int height(AVLNode a)
	{
		if(a == null)
			return 0;
		else
			return a.height;
	}

	private int maxHeight(int h1, int h2) //finds the largest height of a subtrees children
	{
		if(h1 > h2)
			return h1;
		else if(h2 >= h1)
			return h2;
		else
			return 0;
	}

	private AVLNode rotateLeft(AVLNode c) //This performs the single rotation with left child
	{
		AVLNode c1 = null;
		if(c.left != null)
		{
			c1 = c.left;
			if(c1.right !=null)
				c.left = c1.right; //max left
		c1.right = c;
		}
		c.height = maxHeight(height(c.left), height(c.right)) +1;
		c1.height = maxHeight(height(c1.left), height(c1.right)) +1;
		return c1;
	}

	private AVLNode rotateRight(AVLNode c) //This performs the single rotation with right child
	{
		AVLNode c1 = null;
		if(c.right != null)
		{
			c1 = c.right;
			if(c1.left != null)
				c.right = c1.left; //min right
			c1.left = c;
		}
		c.height = maxHeight(height(c.left), height(c.right)) + 1;
		c1.height = maxHeight(height(c1.left), height(c1.right)) + 1;
		return c1;
	}
}
