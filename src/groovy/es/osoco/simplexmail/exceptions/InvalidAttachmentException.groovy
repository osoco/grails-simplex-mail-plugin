package es.osoco.simplexmail.exceptions

class InvalidAttachmentException extends Exception{

    public InvalidAttachmentException(file, mailName)
    {
        super("Error when generating attachments for mail $mailName from file $file")
    }
}
