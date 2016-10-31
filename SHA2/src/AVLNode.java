

public class AVLNode 
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
		height = 1;
	}
	/**
	 * Creates an AVLNode
	 * @param sha
	 * @param bin
	 */
	public AVLNode(byte[] sha, byte[] bin)
	{
		shaValue = sha;
		binValue = bin;
		height = 0;
		left = new AVLNode();
		right = new AVLNode();
	}
	
	/**
	 * 
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
	
	public boolean exists()
	{
		return(shaValue != null);
			
	}
}
