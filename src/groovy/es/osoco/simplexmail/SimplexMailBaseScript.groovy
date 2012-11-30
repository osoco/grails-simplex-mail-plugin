package es.osoco.simplexmail

abstract class SimplexMailBaseScript extends Script {
	
	def mailProperties = [:]
	def currentMail
	
	def methodMissing(String methodName, args) {
		if (args) {
			if (args[0] instanceof Closure) {
				currentMail = methodName
				mailProperties[currentMail] = [:]
				with(args[0])
			}
			else {
				def propertyType = MailPropertyType.findByName(methodName)
				if (propertyType) {
					mailProperties[currentMail][propertyType] = propertyType.parseArgs(args)
				}
				else {
					throw InvalidMailPropertyException(currentMail, methodName)
				}
			}
		}
		return mailProperties
	}
}