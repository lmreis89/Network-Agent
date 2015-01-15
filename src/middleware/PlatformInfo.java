package middleware;

import java.io.Serializable;

public interface PlatformInfo extends Serializable {
	public String getName();
	public String getHost();
}
