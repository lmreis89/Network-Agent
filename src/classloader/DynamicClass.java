package classloader;

import java.io.Serializable;

public interface DynamicClass extends Serializable 
{
	public Class<?> getLoadedClass();	
}
