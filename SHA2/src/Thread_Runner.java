import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

class Thread_Runner implements Runnable 
{
   private Thread t;
   private String threadName;
   private int max;
   private int byteSize;
   private int count;
   private int cleaner;
   private Thread_Runner[] threads;
   private AVLTree tree;
   
   Thread_Runner(String name, int size, Thread_Runner[] thd, int THREAD_COUNT, AVLTree t) 
   {
      threadName = name;
      byteSize = size;
      threads = thd;
      count = THREAD_COUNT;
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

    	   if(++cleaner < 10000)
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
	   if(currentCount > max && currentCount != 31)
	   {
		   updateMax(currentCount);
		   System.out.println(threadName + " Current Count: " + currentCount + " Max: " + max);
		   try 
		   {
			   writeOut(XBytes, ((AVLNode)obj[1]).binValue, shaX, ((AVLNode)obj[1]).shaValue, currentCount);
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

private synchronized void writeOut(byte[] X, byte[] Y, byte[] ShaX, byte[] ShaY, int count) throws IOException
   {
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
   }
   
   public void updateMax(int newMax)
   {
	   for(int i = 0; i < count; i++)
	   {
		   if(threads[i].getMax() < newMax)
			   threads[i].setMax(newMax);	   
	   }
   }
   
   private void setMax(int count2) 
   {
	 this.max = count2;
   }

private int getMax() 
{
	return this.max;
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