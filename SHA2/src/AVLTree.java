
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
		return(checkRotation(root));
	}

	private synchronized AVLNode insert(AVLNode newNode, int position, AVLNode current)
	{
		if(current == null) //handles adding the node to the list when it reaches an empty position
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
         --position;
         if(newNode.shaValue[position] > current.shaValue[position]) // > handles inserting to the right of a value
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
			if (local.shaValue[31] > data.shaValue[31])
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

	private void setHeight(AVLNode cur) 
	{
	    int lh = getHeight(cur.left);
	    int rh = getHeight(cur.right);
	    if(lh < rh)
	         cur.height = 1 + rh;
	    else
	         cur.height = 1 + lh;
	}

	private int getHeight(AVLNode a)
	{
		if(a == null)
			return 0;
		else
			return a.height;
	}
	
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

	private int heightDiff(AVLNode n) 
	{
		if (n == null) {
            return 0;
        }
        return getHeight(n.left) - getHeight(n.right);
	}

	/**
	 * Performs single rotional Left
	 * @param top
	 * @return
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
	 * performs Single rotation right
	 * @param top
	 * @return
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
