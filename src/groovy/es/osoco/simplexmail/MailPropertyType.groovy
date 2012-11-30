package es.osoco.simplexmail

enum MailPropertyType {
	TO('to', true),
	FROM('from', false),
	BCC('bcc', true),
	REPLYTO('replyTo', false),
	SUBJECT('subject', false),
	HTML('html', false),
	ATTACHMENTS('attachments', true),
	INHERITS('inherits', true)
	
	String name
	Boolean canBeMultiple
	
	private MailPropertyType(name, canBeMultiple) {
		this.name = name
		this.canBeMultiple = canBeMultiple
	}
	
	static findByName(name) {
		MailPropertyType.find { it.name == name }
	}
	
	def parseArgs(args) {
		canBeMultiple ? args : args[0]
	}
	
}