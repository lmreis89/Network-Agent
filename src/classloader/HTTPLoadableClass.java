package classloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HTTPLoadableClass implements DynamicClass
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Class<?> theClass;
	
	public HTTPLoadableClass(String url, String classname) throws MalformedURLException, IOException
	{
		loadHttp(url, classname);
	}
	
	private void loadHttp(String url, String classname) throws MalformedURLException, IOException 
	{
		URLConnection conn = new URL(url+"/"+classname+".class").openConnection() ;
		
		InputStream is = conn.getInputStream();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    	byte[] bytebuf = new byte[65536];
        int data = is.read(bytebuf);

        while(data != -1){
            buffer.write(bytebuf,0,data);
            data = is.read(bytebuf);
        }

        byte[] classData = buffer.toByteArray();

       	PlatformClassLoader cl  = new PlatformClassLoader(PlatformClassLoader.class.getClassLoader());
       	this.theClass = cl.defineClass(classname, classData);
		
	}
	
	@Override
	public Class<?> getLoadedClass() 
	{
		return this.theClass;
	}
	
}
