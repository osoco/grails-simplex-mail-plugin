package es.osoco.simplexmail

import javax.activation.MimetypesFileTypeMap
import org.codehaus.groovy.control.CompilerConfiguration
import es.osoco.simplexmail.exceptions.InvalidAttachmentException
import es.osoco.simplexmail.exceptions.InvalidMailPropertyValueException
import org.apache.commons.logging.LogFactory
import static es.osoco.simplexmail.MailPropertyType.*

class SimplexMailService {
    private static final log = LogFactory.getLog("es.osoco.simplexmail")
    
    def asynchronousMailService 
	def grailsApplication
    def messageSource
    def customPageRenderer
    
	private sendEmail(emailProperties) {
        calculateEmailInternacionalizedProperties(emailProperties)
        checkPropertiesForErrors(emailProperties)
        
        log.info( "Sending email from '${emailProperties[FROM]}'" + 
                "to ${anonymizesEmailTo(emailProperties[TO])}" +                 
                "subject '${emailProperties[SUBJECT]}'" +
                "and bcc '${emailProperties[BCC]}'" + 
                "${emailProperties[ATTACHMENTS] ?: 'not attached data'}" +
                "locale ${emailProperties[LOCALE]}")
    
        resolveaAttachments(emailProperties)
                 
        asynchronousMailService.sendMail {
            multipart true
            emailProperties.each { type, values ->
                if(type instanceof MailPropertyType) {
                    if(type == MailPropertyType.ATTACHMENTS) {
                        values.each {
                            attachment ->
                            owner.delegate.attachBytes attachment.name , attachment.mimeType , attachment.content
                        }
                    } else {
                        delegate."$type.name"(values)
                    }
                }
            }
        }
    }
    
    private def anonymizesEmailTo(def to)
    {
        if (to instanceof String) {
            return to.substring(0,4).concat("****")
        } else {
            return to.collect { it.substring(0,4).concat("****") }
        }
    }
    
    private calculateEmailInternacionalizedProperties(emailProperties) {
        if(!emailProperties[LOCALE]) {
            emailProperties[LOCALE] = new Locale(grailsApplication.config.simplex.mail.language.default.isoCode)
        }
        def mailLocale = emailProperties[LOCALE]
        
        
        try {
            emailProperties[SUBJECT] = messageSource.getMessage(emailProperties[SUBJECT]?:'', null, mailLocale )
        } catch (e) {
            throw new InvalidMailPropertyValueException(SUBJECT, '',
                emailProperties[SimplexMailLoaderService.MAIL_METHOD_NAME])
        }
        
        emailProperties[HTML] = customPageRenderer.render(view: emailProperties[HTML] , 
            model: (emailProperties.model?:[:] << [locale:mailLocale] ) )
    }
    
    private checkPropertiesForErrors(emailProperties) {
        MailPropertyType.findAll().each {
            propertyType ->
            if(!propertyType.validate(emailProperties[propertyType])) {
                throw new InvalidMailPropertyValueException(propertyType,emailProperties[propertyType], 
                    emailProperties[SimplexMailLoaderService.MAIL_METHOD_NAME])
            }
        }
    }
    
    private resolveaAttachments(emailProperties) {
        if(emailProperties[ATTACHMENTS]) {
            emailProperties[ATTACHMENTS] = generateAttachments(emailProperties[ATTACHMENTS],
                emailProperties[SimplexMailLoaderService.MAIL_METHOD_NAME])
        }
    }

    private def generateAttachments(files, methodName)
    {
        files.inject([]) {
            attachments, file ->
            if(file.exists()) 
            {
                def attachment = [
                    name: file.getName(),
                    mimeType: new MimetypesFileTypeMap().getContentType(file),
                    content: file.getBytes()
                ]
                attachments.add(attachment)
            }
            else
            {
                throw new InvalidAttachmentException(file, methodName)
            }
            return attachments
        }
        
    }

}
