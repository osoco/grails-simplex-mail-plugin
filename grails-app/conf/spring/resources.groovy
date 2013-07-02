beans = {
    // Workaround for GRAILS-9106 bug
    customPageRenderer(es.osoco.simplexmail.util.CustomPageRenderer, ref("groovyPagesTemplateEngine")) {
        groovyPageLocator = ref("groovyPageLocator")
    }
}