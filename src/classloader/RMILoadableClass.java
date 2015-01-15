package classloader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;


/**
 * 
 * @author Pedro Amaral
 * 
 * This object has the porpouse of tranfering the bytes and loading a new class dynamically
 * when returned by a remote method invocation 
 *
 */
public class RMILoadableClass implements DynamicClass, Serializable {

	protected static final long serialVersionUID = 1L;
	protected static final String END = ".class";
	
	/**
	 * This Object is only non-null if it is on the receivers side
	 */
	protected Class<?> theClass;
	protected String repository,classpath;
	
	public RMILoadableClass(String classpath, String repository) 
	{
		super();
		this.repository = repository;
		this.classpath = classpath;
	}
	
	
	/**
	 * Override's the inherited implementation from object, changing the way this file received 
	 * from an ObjectOutputStream.
	 */
	private void writeObject( ObjectOutputStream oos ) throws IOException 
    {
    	oos.writeUTF(classpath);
    	
    	URL myUrl = new URL("file:"+repository +classpath + END);
    	URLConnection connection = myUrl.openConnection();
    	InputStream fis = connection.getInputStream();
    	
		byte [] buf = new byte[65536];
		int nRead = 0;
		while((nRead = fis.read(buf)) != -1)
		{
			oos.write(buf, 0, nRead);

		}

    }    
    
	/**
	 * Override's the inherited implementation from object, changing the way this file received 
	 * from an ObjectOutputStream.
	 */
    private void readObject( ObjectInputStream ois ) throws IOException, ClassNotFoundException 
    {

    	classpath = ois.readUTF();
    	repository = "";
    	
    	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    	byte[] bytebuf = new byte[65536];
        int data = ois.read(bytebuf);

        while(data != -1){
            buffer.write(bytebuf,0,data);
            data = ois.read(bytebuf);
        }

        byte[] classData = buffer.toByteArray();

       	PlatformClassLoader cl  = new PlatformClassLoader(PlatformClassLoader.class.getClassLoader());
       	theClass = cl.defineClass(classpath, classData);

    }
    
    public Class<?> getLoadedClass()
    {
    	return theClass;
    }
	
}
