package es.osoco.simplexmail.exceptions

import es.osoco.simplexmail.MailPropertyType;
import sun.tools.tree.ThisExpression;

class InvalidMailPropertyException extends Exception {
	
	private static final validProperties = MailPropertyType.collect { it.name }.join(", ")
	public InvalidMailPropertyException(mailName, property)
	{
		super("Invalid property '$property' when evaluating mail '$mailName'. Property must be one of the following: $validProperties")
	}	
}