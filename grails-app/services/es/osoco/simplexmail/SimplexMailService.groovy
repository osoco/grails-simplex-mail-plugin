package es.osoco.simplexmail
import org.codehaus.groovy.control.CompilerConfiguration
import static es.osoco.simplexmail.MailPropertyType.*

class SimplexMailService {

	def transactional = false
	def asynchronousMailService 
	def grailsApplication
	
	public loadMailConfig() {
		println grailsApplication
		println getResourceByPath(grailsApplication.config.simplex.mail.config.files.paths)
		
		[grailsApplication.config.simplex.mail.config.files.paths].flatten().each { configFilePath -> 
			injectMailSendingMethods(
				postProcessMailConfig(
					buildGroovyShell().evaluate(getResourceByPath(configFilePath)?.text)
				)
			)
		}
	}
	
	public sendEmail(emailProperties) {
		println "INVOKED $emailProperties"
		/*
		asynchronousMailService.sendAsynchronousMail {
			emailProperties.each { type, values -> with(types.configure(values) }
		}*//*
		asynchronousMailService.sendAsynchronousMail {
			to toEmail
			from fromEmail
			cc ccEmails
			bcc bccEmails
			replyTo replyToEmail
			subject emailSubject
			html emailBody
			attachments?.each {
				attachment ->
				attachBytes attachment.name, attachment.mimeType, attachment.content
			}
		}*/
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
			this.metaClass."send${mail.capitalize()}" = { Map overwrittenProps = [:] -> 
				def effectiveProps = props + overwrittenProps
				println "Sending email $mail with props $effectiveProps"
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
			mailProps[ATTACHMENTS] = mailProps[ATTACHMENTS]?.collect { getResourceByPath(it) }
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
