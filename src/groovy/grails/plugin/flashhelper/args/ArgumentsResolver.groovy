package grails.plugin.flashhelper.args

/**
 * Responsible for retrieving arguments from the parameters passed to an invocation of <tt>FlashHelper</tt>
 */
interface ArgumentsResolver {

    /**
     * The locale to be used when resolving messages from the resource bundle
     * @return
     */
    Locale getLocale()

    /**
     * Returns either the messages themselves or the codes thereof
     * @return
     */
    String getMessage()

    /**
     * Returns the values that are substituted for message argument placeholders 
     * @return
     */
    List getMessageArguments()

    /**
     * Message(s) to be used if a key is not found in application messages
     * @return
     */
    String getDefaultMessage()

    /**
     * Indicates whether the arguments themselves are keys that should be resolved from the resource bundle
     * @return
     */
    boolean resolveMessageArguments()

    /**
     * If we lookup a message code in the resource bundle and fail to resolve it, we can either:
     * <ol>
     *   <li>Throw an exception<li>
     *   <li>Use the key itself as the message</tt>
     * <ol>
     * 
     * This method decides which of these alternatives to choose 
     * @return
     */
    boolean codeMustResolve()    
    
}
