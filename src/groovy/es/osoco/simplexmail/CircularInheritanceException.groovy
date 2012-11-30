package es.osoco.simplexmail

import sun.tools.tree.ThisExpression;

class CircularInheritanceException extends Exception {
	
	public CircularInheritanceException(stack)
	{
		super("Found a circular inheritance: " + stack.join(' -> '))
	}
	
}