package grails.plugin.flashhelper.args

import static grails.plugin.flashhelper.args.CollectionUtils.*


/**
 * Resolves the arguments when they are provided as a single <tt>Map</tt>.The keys of this map are the argument names, and the map values are
 * the arguments themselves.
 * 
 * @author Donal Murtagh
 */
class NamedArgumentsResolver implements ArgumentsResolver {

    private final Map args 
    private message
    private List messageArgs = []
    private final defaultMessage
    
    NamedArgumentsResolver(Map methodArgs) {
        this.args = methodArgs        
        this.defaultMessage = methodArgs.default
        
        // Store the messages and keys
        def msgsAndArgs = methodArgs.msgs
        
        if (msgsAndArgs instanceof Map) {

            msgsAndArgs.each {msgCode, msgArgs ->
                addMsgAndArgs(msgCode, msgArgs)
            }
        } else {
            addMsgAndArgs(msgsAndArgs)
        }
    }

    private void addMsgAndArgs(msgOrCode, args = Collections.emptyList()) {
        message = msgOrCode
        messageArgs = asCollection(args)
    }
    
    @Override
    Locale getLocale() {
        args.locale
    }

    @Override
    boolean resolveMessageArguments() {
        args.resolveArgs
    }

    @Override
    String getMessage() {
        message
    }
    
    @Override
    List getMessageArguments() {
        messageArgs
    }
    
    @Override
    String getDefaultMessage() {
        defaultMessage
    }

    @Override
    boolean codeMustResolve() {
        args.codeMustResolve
    }
}
