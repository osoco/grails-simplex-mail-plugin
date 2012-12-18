package es.osoco.simplexmail.util

import javax.servlet.http.*
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.web.servlet.support.RequestContextUtils
import org.springframework.web.servlet.LocaleResolver

class LocaleUtils {
    static final grailsApplication = ApplicationHolder.application
    static final SESSION_LOCALE = 'org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE'
    static final LOCALE_RESOLVER_KEY = "org.springframework.web.servlet.DispatcherServlet.LOCALE_RESOLVER"
    
    static changeCurrentRequestLocale(newLocale) {
        doWithRequestAndResponse() {
            request, response ->
            RequestContextUtils.getLocaleResolver(request).setLocale(request,response,newLocale)
        }
    }
    
    static changeSessionLocale(newLocale) {
        def grailsWebRequest = WebUtils.retrieveGrailsWebRequest()
        def session = grailsWebRequest.session
        session[SESSION_LOCALE] = newLocale
    }
    
    static changeNonRequestLocale(newLocale) {
        doWithRequestAndResponse() {
            request, response ->
            def localeResolver = RequestContextUtils.getLocaleResolver(request)
            if(localeResolver == null) {
                localeResolver = new CustomLocaleResolver()
                request.setAttribute(LOCALE_RESOLVER_KEY,localeResolver)
            }
            localeResolver.setLocale(request,response,newLocale)
        }
    }
    
    private static doWithRequestAndResponse(delegate) {
        def grailsWebRequest = WebUtils.retrieveGrailsWebRequest()
        def request = grailsWebRequest.request
        def response = grailsWebRequest.response
        
        delegate(request, response)
    }
    
    private static class CustomLocaleResolver extends LocaleResolver{
        Locale locale
        
        Locale resolveLocale(HttpServletRequest request) {
            return locale
        }
        
        void setLocale(HttpServletRequest request, HttpServletResponse httpServletRequest, Locale locale){
            this.locale = locale
        }
    }
}
