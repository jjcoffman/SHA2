
public class AVLNode 
{
	//here are the default values used by the avl tree
	byte[] shaValue;
	byte[] x;
	AVLNode left;
	AVLNode right;
	int height;
	
	//the following method is a generic constructor for the node
	public AVLNode()
	{
		shaValue = null;
		height = 1;
		left = null;
		right = null;
	}
	
	//this is the overloaded constructor that passes a value to the node
	public AVLNode(byte[] val, byte[] x)
	{
		shaValue = val;
		height = 0;
		this.x = x;
		left = null;
		right = null;
	}
	
	
}
