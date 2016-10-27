
public class shaFinder 
{
	static Thread_Runner[] thread;
	static int THREAD_COUNT = 8;
	public static void main(String[] args) 
	{
		int i = 0;
		thread = new Thread_Runner[8];
		AVLTree tree = new AVLTree();
		for(i = 1; i <= THREAD_COUNT; i++)
		{
			thread[i-1] = new Thread_Runner(("Thread-" + i), 32, thread, THREAD_COUNT, tree);
			thread[i-1].start();
		}
    }
}
