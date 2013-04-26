package es.osoco.simplexmail

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.runtime.InvokerHelper

import es.osoco.simplexmail.exceptions.CircularInheritanceException
import org.apache.commons.logging.LogFactory
import static es.osoco.simplexmail.MailPropertyType.*

class SimplexMailLoaderService {
    private static final log = LogFactory.getLog("es.osoco.simplexmail")
    
    static final String MAIL_METHOD_NAME = "methodName"
    static transactional = false
    def grailsApplication
    
    public loadMailConfig() {
        def mailConfigScriptNames = [grailsApplication.config.simplex.mail.config.script.names]?.flatten()
        
        log.info ("Loading simplex-mail-config from scripts ${mailConfigScriptNames}")
        
        mailConfigScriptNames?.each { scriptName ->
            injectMailSendingMethods(
                postProcessMailConfig(
                    executeScript(getScriptByName(scriptName))
                )
            )
        }
    }
    
    private getResourceByPath(path)
    {
        new File(path.startsWith("/") ? (new grails.util.BuildSettings().baseDir.path + "/" + path) : path)
    }
    
    private injectMailSendingMethods(mailConfig) {
        mailConfig.each { mail, props ->
            def methodName = "send${mail.capitalize()}".toString()
            
            log.info "Injecting mail method $methodName with props $props"
            
            props[MAIL_METHOD_NAME] = methodName
            
            SimplexMailService.metaClass."$methodName" = { Map overwrittenProps = [:] ->
                def effectiveProps = props + overwrittenProps
                sendEmail(effectiveProps)
            }
        }
    }
    
    private getScriptByName(scriptName)
    {
        try {
            Class.forName(scriptName, false, this.class.classLoader).newInstance()
        } catch (ClassNotFoundException exception) {
            log.error("Simplex mail configuration script name ${scriptName} not found",exception )
        }
    }
    
    private postProcessMailConfig(mailConfig)
    {
        resolveAttachments(resolveInherits(mailConfig))
    }
    
    private resolveAttachments(mailConfig)
    {
        mailConfig.each { mailName, mailProps ->
            def attachments = mailProps[ATTACHMENTS]?.collect { getResourceByPath(it) }
            if(attachments) {
                mailProps[ATTACHMENTS] = attachments
            }
        }
        mailConfig
    }

    private resolveInherits(mailConfig)
    {
        def resolve = { mailProps, stack ->
            def inherits = mailProps.remove(INHERITS)?.getAt(0)
            if (inherits)
            {
                if (inherits in stack) {
                    throw new CircularInheritanceException(stack)
                }
                else {
                    call(mailConfig[inherits], stack << inherits).each {
                        property, value ->
                        if (!mailProps.containsKey(property)) {
                            mailProps[property] = value
                        }
                    }
                }
            }
            return mailProps
        }
        mailConfig.each { mailName, mailProps -> resolve(mailProps, [mailName]) }
        mailConfig
    }
    
    private executeScript(Script script) {
        try {    
            SimplexMailBaseScript.enhanceScript(script)
            script.setBinding(new Binding(grailsApplication.config.toProperties()))
            script.run()
        } catch (e) {
            log.error("An error occurs executing script", e)
        }finally {
            if (script != null) {
                InvokerHelper.removeClass(script.getClass())
            }
        }
    }

}
