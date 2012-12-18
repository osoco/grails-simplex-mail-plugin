package es.osoco.simplexmail

class InvalidMailPropertyValueException extends Exception {
    
    private static final validProperties = MailPropertyType.collect { it.name }.join(", ")
    public InvalidMailPropertyValueException(property, value, mailName)
    {
        super("Invalid value '$value' for property '$property' for mail $mailName")
    }
}