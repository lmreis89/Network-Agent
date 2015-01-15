package syntax;

import java.io.Serializable;

public interface Node extends Serializable{
	String unparse();
}
