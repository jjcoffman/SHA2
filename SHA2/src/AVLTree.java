
public class AVLTree 
{
	AVLNode root = new AVLNode();
	public AVLTree()
	{
		root = null;
	}
	public void insert(byte[] v, byte[] X)
	{
		root = insert(v, X, root);
	}
	
	public AVLNode insert(byte[] val, byte[] x, AVLNode current)
	{
		if(current == null) //handles adding the node to the list when it reaches an empty position
			current = new AVLNode(val ,x);
		else if(current.shaValue[current.height] < val[current.height]) //handles traversing through the tree to the right
			current.right = insert(val, x, current.right); //recursive method call
		else if(current.shaValue[current.height] > val[current.height]) //handles traversing the tree to the left
			current.left = insert(val, x, current.left); //Recursive method call
		else{} //Duplicate Value
		
		current.height = max(height(current.left), height(current.right)) + 1; //sets the correct height based on its subtrees
		int rotate = rotateCheck(current);
		if(rotate < -1)
		{
			if(rotateCheck(current.right) > 0) //this handles double rotation by calling each rotation separately
			{
				current = rotateWithLeftChild(current.right);
				return rotateWithRightChild(current);
			}
			else
				current = rotateWithRightChild(current);
		}
		
		if(rotate > 1)
		{
			if(rotateCheck(current.left) < 0)
			{
				current = rotateWithRightChild(current.left);
				return rotateWithLeftChild(current);
			}
			else
				current = rotateWithLeftChild(current);
		}	
		return current;
	}
	
	private int height(AVLNode a)
	{
		if(a == null)
			return 0;
		else
			return a.height;
	}
	
	private int rotateCheck(AVLNode c) //this checks to make sure that the subtree should be rotated or not
	{
		if(c == null)
			return 0;
		else
			return height(c.left) - height(c.right);
	}

	private int max(int h1, int h2) //finds the largest height of a subtrees children
	{
		if(h1 > h2)
			return h1;
		else if(h2 >= h1)
			return h2;
		else
			return 0;
	}

	private AVLNode rotateWithLeftChild(AVLNode c) //This performs the single rotation with left child
	{
		AVLNode c1 = c.left;
		c.left = c1.right; //max left
		c1.right = c;
		c.height = max(height(c.left), height(c.right)) +1;
		c1.height = max(height(c1.left), height(c1.right)) +1;
		return c1;
	}

	private AVLNode rotateWithRightChild(AVLNode c) //This performs the single rotation with right child
	{
		AVLNode c1 = c.right;
		c.right = c1.left; //min right
		c1.left = c;
		c.height = max(height(c.left), height(c.right)) + 1;
		c1.height = max(height(c1.left), height(c1.right)) + 1;
		return c1;
	}
}
