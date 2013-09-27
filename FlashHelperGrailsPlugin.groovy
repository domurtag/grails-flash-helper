import grails.plugin.flashhelper.FlashHelper

class FlashHelperGrailsPlugin {

    // the plugin version
    def version = "0.9.6"

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.0 > *"

    // resources that are excluded from plugin packaging
    def pluginExcludes = ["grails-app/conf", "grails-app/controllers", "grails-app/domain", "grails-app/i18n",
            "grails-app/services", "grails-app/utils", "grails-app/views", "lib", "scripts", "web-app"]

    // the other plugins this plugin depends on
    def dependsOn = [controllers: grailsVersion]

    def observe = ['controllers']

    def author = "Donal Murtagh"
    def authorEmail = "domurtag@yahoo.co.uk"
    def title = "Flash-Scoped Messages Helper"
    def description = '''
Simplifies and standardizes the process of adding/reading messages in the flash scope, particularly i18n messages that must be retrieved from the messages*.properties files. It provides the following features:

    * Automatically resolves i18n messages when message keys are stored in flash scope
    * Optionally enforces the use of a limited number of flash keys (e.g. info, error, warning)
    * Supports adding multiple messages to the same flash key
    * Allows a Locale and default message argument to be provided when resolving i18n messages
    * Provides a taglib that can be used to retrieve messages added to the flash
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/flash-helper"

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "GitHub", url: "https://github.com/domurtag/grails-flash-helper/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/domurtag/grails-flash-helper" ]

    def doWithWebDescriptor = {xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = {ctx ->
        configureFlashHelper(application, ctx)
    }

    def doWithApplicationContext = {applicationContext ->
        // Implement post initialization spring config (optional)
    }

    def onChange = {event ->
        // Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
        configureFlashHelper(event.application, event.ctx)
    }

    def onConfigChange = {event ->
        // Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    /**
     * add getFlashHelper() to controllers
     */
    def configureFlashHelper(application, applicationContext) {

        def messageSource = applicationContext.getBean('messageSource')

        application.controllerClasses*.metaClass*.getFlashHelper = {

            def controllerInstance = delegate

            // Avoid creating a new FlashHelper each time the 'flashHelper' property is accessed
            if (!controllerInstance.metaClass.hasProperty('flashHelperInstance')) {
                controllerInstance.metaClass.flashHelperInstance = new FlashHelper(controllerInstance, messageSource)
            }

            // Return the FlashHelper instance. There may be a simpler way, but I tried
            // controllerInstance.metaClass.getMetaProperty('flashHelperInstance')
            // and it didn't work
            return controllerInstance.metaClass.getMetaProperty('flashHelperInstance').getter.invoke(controllerInstance, [] as Object[])
        }
    }
}
