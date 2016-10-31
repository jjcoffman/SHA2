import java.util.Random;

public class shaFinder 
{
	static Thread_Runner[] thread;
	static int THREAD_COUNT = 8;
	static AVLTree tree;
	public static void main(String[] args) 
	{
		int i = 0;
		thread = new Thread_Runner[8];
		tree = AVLTree.getTree();
		for(i = 1; i <= THREAD_COUNT; i++)
		{
			thread[i-1] = new Thread_Runner(("Thread-" + i), i*(new Random().nextInt(100)), thread, THREAD_COUNT, tree);
			thread[i-1].start();
		}
    }
}
