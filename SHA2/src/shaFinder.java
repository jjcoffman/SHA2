
public class shaFinder 
{
	static Thread_Runner[] thread;
	static int THREAD_COUNT = 8;
	static AVLTree tree;
	public static void main(String[] args) 
	{
		int i = 0;
		thread = new Thread_Runner[THREAD_COUNT];
		tree = AVLTree.getTree();
		Controller control = Controller.getController();
		for(i = 1; i <= THREAD_COUNT; i++)
		{
			thread[i-1] = new Thread_Runner(("Thread-" + i), i*4, control, tree);
			thread[i-1].start();
		}
    }
}
