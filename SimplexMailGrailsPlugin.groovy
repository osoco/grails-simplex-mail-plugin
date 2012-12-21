import org.apache.commons.logging.LogFactory

class SimplexMailGrailsPlugin {
    private static final log = LogFactory.getLog("es.osoco.simplexmail")
    
    // the plugin version
    def version = "0.1-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0 > *"
    // the other plugins this plugin depends on
    //def dependsOn = ['asynchronous-mail' : "0.7>*"]
    //def loadBefore = ['asynchronous-mail']
	//def loadAfter = ['asynchronous-mail']
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]
 
    // TODO Fill in these fields
    def title = "Grails Simplex Mail Plugin Plugin" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
        Simplex mail plugin provides a service for do a very simple mail definition
    '''
    def simplexMailService
    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/grails-simplex-mail-plugin"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        reloadMailConfig(ctx)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
        reloadMailConfig(event.ctx)
    }  

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
        reloadMailConfig(event.ctx)
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
    
    def getWatchedResources() {
        def watchedPaths = [grailsApplication.config.simplex.mail.config.files.paths].flatten()
        def watchedFiles = watchedPaths.collect { "file:./${it}"}
        log.info  "The files for search changes for reload simplex mail service are $watchedFiles"
        return watchedFiles
    }
    
    private reloadMailConfig(ctx) {
        ctx.simplexMailLoaderService.loadMailConfig()
    }
      
    private getGrailsApplication() {
        org.codehaus.groovy.grails.commons.ApplicationHolder.application
    }
    
}
