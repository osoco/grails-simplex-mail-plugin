package es.osoco.simplexmail

import org.codehaus.groovy.control.CompilerConfiguration
import static es.osoco.simplexmail.MailPropertyType.*

class SimplexMailLoaderService {

    final String MAIL_METHOD_NAME = "methodName"
    static transactional = false
    def grailsApplication
    
    public loadMailConfig() {
        println ('Loading simplex-mail-config from file' + 
                  "${getResourceByPath(grailsApplication.config.simplex.mail.config.files.paths)}")
        
        [grailsApplication.config.simplex.mail.config.files.paths].flatten().each { configFilePath ->
            injectMailSendingMethods(
                postProcessMailConfig(
                    buildGroovyShell().evaluate(getResourceByPath(configFilePath)?.text)
                )
            )
        }
    }
    
    private buildGroovyShell() {
        new GroovyShell(
            this.class.classLoader, new Binding(grailsApplication.config.toProperties()),
            new CompilerConfiguration().with { compilerConfig ->
                scriptBaseClass = SimplexMailBaseScript.class.name
                compilerConfig
            }
        )
    }
    
    private injectMailSendingMethods(mailConfig) {
        mailConfig.each { mail, props ->
            def methodName = "send${mail.capitalize()}".toString()
            println "Injecting mail method $methodName with props $props"
            props[MAIL_METHOD_NAME] = methodName
            
            SimplexMailService.metaClass."$methodName" = { Map overwrittenProps = [:] ->
                def effectiveProps = props + overwrittenProps
                sendEmail(effectiveProps)
            }
        }
    }
    
    private getResourceByPath(path)
    {
        new File(new grails.util.BuildSettings().baseDir.path + "/" + path)
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
                    mailProps << call(mailConfig[inherits], stack << inherits)
                }
            }
            else
            {
                mailProps
            }
        }
        mailConfig.each { mailName, mailProps -> resolve(mailProps, [mailName]) }
        mailConfig
    }

}
