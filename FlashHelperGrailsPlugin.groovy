import grails.plugin.flashhelper.FlashHelper
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.MessageSource

class FlashHelperGrailsPlugin {

    // the plugin version
    def version = "0.9.9"

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.0 > *"

    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/conf",
            "grails-app/controllers",
            "grails-app/domain",
            "grails-app/i18n",
            "grails-app/services",
            "grails-app/utils",
            "grails-app/views",
            "lib",
            "scripts",
            "web-app"
    ]

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
    def issueManagement = [system: "GitHub", url: "https://github.com/domurtag/grails-flash-helper/issues"]

    // Online location of the plugin's browseable source code.
    def scm = [url: "https://github.com/domurtag/grails-flash-helper"]

    def doWithWebDescriptor = { xml ->
    }

    def doWithSpring = {
    }

    def doWithDynamicMethods = { ctx ->
        addFlashHelperMethod(application, ctx)
    }

    def doWithApplicationContext = { applicationContext ->
        // Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
        addFlashHelperMethod(event.application, event.ctx)
    }

    def onConfigChange = { event ->
        // Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    /**
     * add getFlashHelper() to controllers
     */
    private addFlashHelperMethod(GrailsApplication grailsApplication, applicationContext) {

        MessageSource messageSource = applicationContext.getBean('messageSource')
        def flashHelperConfig = grailsApplication.config.flashHelper

        grailsApplication.controllerClasses*.metaClass*.getFlashHelper = {

            def controllerInstance = delegate
            MetaClass controllerMetaClass = controllerInstance.metaClass

            // Avoid creating a new FlashHelper each time the 'flashHelper' property is accessed
            if (!controllerMetaClass.hasProperty('flashHelperInstance')) {
                controllerMetaClass.flashHelperInstance = new FlashHelper(controllerInstance, messageSource, flashHelperConfig)
            }

            return controllerInstance.flashHelperInstance
        }
    }
}
