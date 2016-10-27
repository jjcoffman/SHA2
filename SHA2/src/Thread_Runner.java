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
	private Thread_Runner[] threads;

	Thread_Runner(String name, int size, Thread_Runner[] thd, int THREAD_COUNT) 
	{
		System.out.println("Creating Thread: " + name);
		threadName = name;
		byteSize = size;
		threads = thd;
		count = THREAD_COUNT;
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
		do
		{
			byte[] XBytes = new byte[byteSize];
			byte[] YBytes = new byte[byteSize];
			new Random().nextBytes(XBytes);
			new Random().nextBytes(YBytes);

			while(XBytes.equals(YBytes))
			{
				new Random().nextBytes(XBytes);
				new Random().nextBytes(YBytes);
			}



			md.update(XBytes, 0, byteSize);
			byte[] shaX = md.digest();
			md.reset();
			md.update(YBytes, 0, byteSize);
			byte[] shaY = md.digest();
			md.reset();
			int currentCount = 0;
			currentCount = checkBytes(shaX, shaY, shaX.length-1, 0);
			if(currentCount > max)
			{
				updateMax(currentCount);
				System.out.println(threadName + " Current Count: " + currentCount + " Max: " + max);
				try 
				{
					writeOut(XBytes, YBytes, shaX, shaY, currentCount);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		while(true);
	}

	private int checkBytes(byte[] X, byte[] Y, int position, int matchCount)
	{
		if(position == 0)
			return matchCount;
		else if(X[position] != Y[position])
			return matchCount;
		else
			return checkBytes(X, Y, position-1, matchCount+1);  
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
			System.out.println("Starting Thread: " + threadName);
			t = new Thread (this, threadName);
			t.start ();
		}
	}
}