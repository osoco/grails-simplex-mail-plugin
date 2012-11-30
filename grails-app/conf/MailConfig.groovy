/*templatesBaseDirName = "/"
environments {
	development {
		templatesBaseDirName = "${(grailsSettings as grails.util.BuildSettings).getSourceDir().canonicalPath}/../generatedFiles/"
	}
	test {
		templatesBaseDirName = "${(grailsSettings as grails.util.BuildSettings).getSourceDir().canonicalPath}/../generatedFiles/"
	}
}
contractDir = 'caca'
*/
contractMail {
	to  "juli@julian.com"
	bcc 'diego@'
}

registrationMail {
	inherits 'contractMail'
	subject 'contracto.muy.rico'
	bcc 'julian@'
}

	/*from 'diego@toharia.com'
	cc 'arturo@herrero.com'
	bcc 'anava@plenummedia.com'
	replyTo 'juli@elloco.es'
	subject 'Email de contracto'
	template "$contractDir/emailTemplate.gsp"
	
}

registrationMail {
	inherits contractMail
	subjectCode 'contracto.muy.rico'
	template ''*/	
