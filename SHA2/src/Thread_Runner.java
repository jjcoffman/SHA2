import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

class Thread_Runner implements Runnable 
{
   private Thread t;
   private String threadName;
   private int byteSize;
   private int cleaner;
   private Controller control;
   private AVLTree tree;
   
   Thread_Runner(String name, int size, Controller control, AVLTree t) 
   {
      threadName = name;
      byteSize = size;
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
       byte[] XBytes = new byte[byteSize];
	   cleaner = 0;
	   do
	   {
		   new Random().nextBytes(XBytes);
		   for(int i = byteSize-1; i >= 0; i--)
    	   {
    		   XBytes[i]++;
    	   }

    	   md.update(XBytes, 0, byteSize);
    	   byte[] shaX = md.digest();
    	   md.reset();

    	   search(XBytes, shaX);

    	   if(++cleaner < 1000)
    	   {
    		   insert(shaX, XBytes);
    	   }
	   }
       while(true);
   }
   
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
		   } 
		   catch (IOException e) 
		   {
			   e.printStackTrace();
		   }
	   }
	
   }

private synchronized void insert(byte[] shaX, byte[] xBytes) 
   {
	   tree.insert(shaX, xBytes);
	
   }

   private void writeOut(byte[] X, byte[] Y, byte[] ShaX, byte[] ShaY, int count) throws IOException
   {
	  control.writeOut(X, Y, ShaX, ShaY, count);
   }
 

public void start() 
   {
      if (t == null) 
      {
         t = new Thread (this, threadName);
         t.start ();
      }
   }

}