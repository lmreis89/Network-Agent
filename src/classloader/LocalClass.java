package classloader;

public class LocalClass implements DynamicClass {


	private static final long serialVersionUID = 1L;

	private Class<?> theClass;
	
	public LocalClass(String classname) throws ClassNotFoundException 
	{
		super();
		loadClass(classname);
	}

	private void loadClass(String classname) throws ClassNotFoundException 
	{
		PlatformClassLoader pcl = new PlatformClassLoader(PlatformClassLoader.class.getClassLoader());
		theClass = pcl.loadClass(classname);
	}

	@Override
	public Class<?> getLoadedClass() 
	{
		return this.theClass;
	}

}
