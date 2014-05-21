package grails.plugin.flashhelper

import grails.plugin.flashhelper.args.*

import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import org.springframework.context.NoSuchMessageException
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Supports adding messages to the flash scope.
 *
 * @author Donal Murtagh
 */
class FlashHelper {

    private controller
    private MessageSource messageSource
    private validKeys

    public FlashHelper(controller, MessageSource messageSource, flashHelperConfig) {
        this.controller = controller
        this.messageSource = messageSource
        this.validKeys = flashHelperConfig?.keys
    }

    /**
     * Will intercept all calls to non-existent methods in this class, i.e. every method
     * that is called to add a message to the flash scope.
     */
    def methodMissing(String methodName, args) {
        validateKey(methodName)
        addMsg(methodName, args)
        return this
    }

    /**
     * If a list of valid keys have been specified, check that the method called corresponds
     * to one of these
     */
    private validateKey(key) {

        if (validKeys && !(key in validKeys)) {
            throw new FlashKeyException("Flash key '$key' is not allowed by the configuration parameter 'flashHelper.keys'")
        }
    }

    /**
     * Looks up a message in the resource bundle
     * @param argsResolver Provides access to the flash helper arguments
     * @throws NoSuchMessageException If no message matching the supplied key exists and a default message was not provided
     */
    private String lookupMsg(ArgumentsResolver argsResolver) throws NoSuchMessageException {

        def args = argsResolver.messageArguments
        Locale locale = argsResolver.locale ?: LocaleContextHolder.locale

        if (argsResolver.resolveMessageArguments()) {
            args = args.collect {arg ->
                // Placeholders are not supported when resolving the arguments
                messageSource.getMessage(arg, null, arg, locale)
            }
        }

        args = args.toArray()

        String key = argsResolver.message
        String defaultMsg = argsResolver.defaultMessage

        try {
            defaultMsg ? messageSource.getMessage(key, args, defaultMsg, locale) : messageSource.getMessage(key, args, locale)

        } catch (NoSuchMessageException ex) {

            if (argsResolver.codeMustResolve()) {
                throw ex
            } else {
                return key
            }
        }
    }

    /**
     * Add message(s) to the flash scope
     * @param flashKey The key under which the message(s) will be added
     * @param args The message(s) or message key(s) and any message arguments
     */
    private void addMsg(String flashKey, args) {

        if (!controller.flash[flashKey]) {
            controller.flash[flashKey] = []
        }

        String resolvedMessage

        if (args[0] instanceof MessageSourceResolvable) {
            resolvedMessage = messageSource.getMessage(args[0], LocaleContextHolder.locale)

        } else {
            ArgumentsResolver argsResolver = ArgumentsResolverFactory.getInstance(args)
            resolvedMessage = lookupMsg(argsResolver)
        }

        controller.flash[flashKey] << resolvedMessage
    }

    /**
     * Empties the flash
     */
    def clear() {
        controller.flash.clear()
        return this
    }
}
