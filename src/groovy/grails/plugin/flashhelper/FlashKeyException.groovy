package grails.plugin.flashhelper

/**
 * Indicates an attempt to add a message to the flash with an invalid key 
 * @author Donal Murtagh
 */
public class FlashKeyException extends GroovyRuntimeException {
    FlashKeyException(String msg) {
        super(msg)
    }
}