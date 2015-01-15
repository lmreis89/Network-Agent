package classloader;

public class PlatformClassLoader extends ClassLoader{

	
	PlatformClassLoader(ClassLoader classLoader)
	{
		super(classLoader);
	}
	
	
	public Class<?> defineClass(String name, byte[] classData)
	{
		return super.defineClass(name,  classData, 0, classData.length);	
	}
	
	
	
	public void resolve(Class<?> theClass)
	{
		super.resolveClass(theClass);
	}
}
