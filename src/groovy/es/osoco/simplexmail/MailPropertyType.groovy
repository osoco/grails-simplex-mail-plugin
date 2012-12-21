package es.osoco.simplexmail

enum MailPropertyType {
	TO('to', true, null),
	FROM('from', false, null),
	BCC('bcc', true, null),
	REPLYTO('replyTo', false, null),
	SUBJECT('subject', false,  { aValue -> aValue as Boolean }),
	HTML('html', false, { aValue -> aValue as Boolean }),
	ATTACHMENTS('attachments', true, null),
	INHERITS('inherits', true, null),
    LOCALE ('locale', false, null)
	 
	String name
	Boolean canBeMultiple
    Closure validator
	
	private MailPropertyType(name, canBeMultiple, validator) {
		this.name = name
		this.canBeMultiple = canBeMultiple
        this.validator = validator
	}
	
    static findByName(name) {
		MailPropertyType.find { it.name == name }
	}
	
	def parseArgs(args) {
		canBeMultiple ? args : args[0]
	}
    
    def validate(value) {
        return !validator || validator(value)
    }
	
}