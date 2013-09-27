package grails.plugin.flashhelper.args


/**
 * Factory that creates implementations of <tt>ArgumentsResolver</tt>
 * @author Donal Murtagh
 *
 */
class ArgumentsResolverFactory {

    /**
     * @param methodArgs Arguments passed to the method call on <tt>FlashHelper</tt>
     * @return
     */
    static ArgumentsResolver getInstance(methodArgs) {
        
        if (methodArgs.size() == 1) {
            def flashHelperArg = methodArgs[0]
            
            if (flashHelperArg instanceof Map && flashHelperArg.msgs) {
                return new NamedArgumentsResolver(flashHelperArg)
            }
        }
        
        new AnonymousArgumentsResolver(methodArgs)
    }
}
