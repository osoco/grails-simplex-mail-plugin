package es.osoco.simplexmail

import java.util.Map;

class SimplexMailService {

	def transactional = false
	def asynchronousMailService
	
	private sendEmail(toEmail, fromEmail, ccEmails, bccEmails, replyToEmail, emailSubject, 
		emailBody, attachments) {
		asynchronousMailService.sendAsynchronousMail {
			to toEmail
			from fromEmail
			bcc bccEmails
			replyTo replyToEmail
			subject emailSubject
			html emailBody
			attachments?.each {
				attachment ->
				attachBytes attachment.name, attachment.mimeType, attachment.content
			}
		}
	}
}
