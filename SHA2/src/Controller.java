import java.io.FileOutputStream;
import java.io.IOException;

public class Controller 
{	
	static Controller control;
	int max;
	private Controller()
	{
		max = 0;
	}
	
	public static Controller getController()
	{
		if(control == null)
			control = new Controller();
		return control;
	}
	
	public synchronized void writeOut(byte[] X, byte[] Y, byte[] ShaX, byte[] ShaY, int count) throws IOException
	   {
			max = count;
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

	public int getMax()
	{
		return max;
	}
}
