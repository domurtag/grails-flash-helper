package grails.plugin.flashhelper

import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Supports retrieving messages from the flash scope, particularly those placed there using the <code>FlashHelper</code>
 */
class FlashHelperTagLib {
    static namespace = 'flashMsg'

    static final DEFAULT_SEPARATOR = '<br/>'

    GrailsApplication grailsApplication

    /**
     * Retrieves message(s) from the flash scope.
     * @param attrs.key The key under which the message(s) is stored in the flash. This attribute must be provided.
     * @params attrs.sep If the value of the flash key is a <code>Collection</code>, this will be used to join
     * the elements before they are combined as a single string. If this optional attibute is not provided the value
     * <code>flashHelper.separator</code> from <code>Config.groovy</code> will be used. If neither an attribute nor
     * a configuration parameter exists <br/> will be used
     * @param body Ignored - this tag should be invoked as a simple tag (without a body)
     */
    def msg = { attrs, body ->

        def msg = getFlashMsg(attrs)

        if (msg) {
            def separator = getEffectiveParamValue(DEFAULT_SEPARATOR, attrs, 'sep', 'separator')

            def result = msg instanceof Collection ? msg.join(separator) : msg
            log.debug "Returning $result from ${namespace}.msg tag"
            out << result
        }
    }

    /**
     * Retrieve message(s) from the flash scope
     * @param attrs.key The key under which the message(s) is stored in the flash. This attribute must be provided.
     * @param body GSP text into which the flash message will be merged. For each message stored under the provided
     * key, the template will be rendered with <tt>${it}</tt> replaced by the key
     */
    def msgBody = { attrs, body ->

        def msg = getFlashMsg(attrs)

        if (msg) {
            if (msg instanceof Collection) {
                msg.each { message ->
                    out << body(message)
                }
            } else {
                out << body(msg)
            }
        }
    }

    private getFlashMsg(attrs) {
        def key = attrs.key

        if (!key) {
            throwTagError "Tag 'msg' requires attribute 'key'"
        }

        boolean removeEntry = getEffectiveParamValue(false, attrs, 'remove').asBoolean()
        def msg = removeEntry ? flash.remove(key) : flash[key]

        if (!msg) {
            String missingKeyAction = getEffectiveParamValue('ignore', attrs, 'keyNotFound').toLowerCase()

            if ('warn' == missingKeyAction) {
                log.warn "No entry found in flash with key '$key'"

            } else if ('error' == missingKeyAction) {
                throw new FlashKeyException("No message found in flash scope with key $key")
            }
        }
        return msg
    }

    /**
     * Returns the effective value of a parameter that can be provided in the tag attributes or flash configuration
     * @param attrs tag attributes
     * @param defaultValue to be used if parameter is not found in the attributes or config
     * @param attrsKey key to access the parameter value from the attributes list
     * @param paramKey key used to access the parameter from the flash helper config. If not provided, defaults to <tt>attrsKey</tt>
     * @return in order of priority: value from attributes, value from flash helper configuration, default value
     */
    private getEffectiveParamValue(defaultValue, attrs, String attrsKey, String configKey = attrsKey) {

        def configValue = grailsApplication.config.flashHelper?."$configKey"
        attrs[attrsKey] ?: configValue ?: defaultValue
    }
}
