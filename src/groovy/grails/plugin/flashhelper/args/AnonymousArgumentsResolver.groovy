package grails.plugin.flashhelper.args

import static grails.plugin.flashhelper.args.CollectionUtils.*

/**
 * Resolves the arguments when they are provided as an unnamed collection of values
 * @author Donal Murtagh
 */
class AnonymousArgumentsResolver implements ArgumentsResolver {

    private final args
    private messageOrCode
    private List arguments = []
    private final boolean messageCodeMustResolve = false
    private final defaultMessage

    /**
     * @param methodArgs All arguments passed to <tt>FlashHelper</tt> 
     */
    AnonymousArgumentsResolver(methodArgs) {

        def msgsAndArgs = methodArgs[0]
        
        // Store the messages and keys
        if (msgsAndArgs instanceof Map) {

            // If message arguments are provided we can assume messageSourceResolvables contains message codes
            // rather than literal messages
            this.messageCodeMustResolve = true

            msgsAndArgs.each {msgCode, msgArgs ->
                addMsgAndArgs(msgCode, msgArgs)
            }
        } else {
            addMsgAndArgs(msgsAndArgs)
        }

        this.defaultMessage = findDefaultMessage(methodArgs)
        this.args = methodArgs
    }

    private void addMsgAndArgs(msgOrCode, args = Collections.emptyList()) {
        messageOrCode = msgOrCode
        arguments = asCollection(args)
    }

    @Override
    final boolean codeMustResolve() {
        messageCodeMustResolve
    }

    @Override
    final String getMessage() {
        messageOrCode
    }

    @Override
    final List getMessageArguments() {
        arguments
    }

    @Override
    final String getDefaultMessage() {
        defaultMessage
    }

    @Override
    Locale getLocale() {
        if (args.size() > 1) {
            args[1..-1].find {arg -> arg instanceof Locale }
        }
    }

    @Override
    boolean resolveMessageArguments() {
        args.size() > 1 && Boolean.TRUE == args[-1]
    }

    private static final findDefaultMessage(methodArgs) {

        if (methodArgs.size() > 1) {
            // TODO: test if this works with GStrings
            methodArgs[1..-1].find { it instanceof String }
        }
    }
}
