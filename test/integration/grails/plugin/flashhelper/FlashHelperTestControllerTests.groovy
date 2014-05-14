package grails.plugin.flashhelper

import grails.test.ControllerUnitTestCase
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.NoSuchMessageException
import org.springframework.context.support.DefaultMessageSourceResolvable


/**
 * Tests for <code>FlashHelper</code>. I'm using <code>ControllerUnitTestCase</code> only as a means of getting
 * a reference to a controller, which I'll need to instantiate the <code>FlashHelper</code>
 */
public class FlashHelperTestControllerTests extends ControllerUnitTestCase {

    GrailsApplication grailsApplication

    /**
     * Creates a single message or a list of messages
     */
    private getMessages(Range range = null) {
        range ? range.collect { "message number $it" } : "message number 1"
    }

    private FlashHelper getFlashHelper() {
        controller.flashHelper
    }

    /**
     * flashHelper.info "message number 1"
     * flashHelper.info ["message number 2", "message number 3"]
     */
    void testLiteralMessages() {

        def fh = getFlashHelper()

        // Add a single message
        fh.info "foo"
        assertEquals(["foo"], controller.flash.info)

        // Add another messages
        fh.info "bar"

        // Flash should now hold 2 messages in a List
        assertEquals(["foo", "bar"], controller.flash.info)
    }

    /**
     * flashHelper.info "key1"
     * flashHelper.info ["key2", "key3"]
     */
    void testMsgKeysWithoutArgs() {

        def fh = getFlashHelper()
        fh.info 'key1'
        assertEquals getMessages(), controller.flash.info[0]

        fh.info 'key2'
        assertEquals getMessages(1..2), controller.flash.info
    }

    /**
     * Test resolving an instance of MessageSourceResolvable
     */
    void testMessageSourceResolvableResolution() {

        def fh = getFlashHelper()

        // test a MessageSourceResolvable without args
        fh.info new DefaultMessageSourceResolvable('key1')
        assertEquals 'message number 1', controller.flash.info[0]
        fh.clear()

        // test a MessageSourceResolvable with args
        String[] msgCodes = ['1ArgMsg']
        fh.info new DefaultMessageSourceResolvable(msgCodes, ['arg'].toArray())
        assertEquals 'message number arg', controller.flash.info[0]
    }

    /**
     * The arguments may themselves be resolved from the resource bundle before being substituted into the message
     * This only happens when the last parameter is Boolean.TRUE
     */
    void testArgumentResolution() {

        def fh = getFlashHelper()
        def resolvableKey = 'key1'
        def unresolvableKey = 'noSuchKey'
        def resolvedKey = 'message number 1'

        // This should substitute 'key1' into the message, i.e. 'key1' should not be resolved from the resource bundle
        fh.info '1ArgMsg': resolvableKey
        assertEquals 'message number key1', controller.flash.info[0]
        fh.clear()

        // Same here because argument resolution is turned off by default
        fh.info '1ArgMsg': resolvableKey, false
        assertEquals 'message number key1', controller.flash.info[0]
        fh.clear()

        // argument should be resolved
        fh.info '1ArgMsg': resolvableKey, true
        assertEquals "message number $resolvedKey", controller.flash.info[0]
        fh.clear()

        // If an attempt to resolve an argument fails, the literal argument should be used
        fh.info '1ArgMsg': unresolvableKey, true
        assertEquals "message number $unresolvableKey", controller.flash.info[0]
        fh.clear()

        // A mixture of arguments that can and cannot be resolved
        fh.info '2ArgMsg': [unresolvableKey, resolvableKey], true
        assertEquals "message $unresolvableKey $resolvedKey", controller.flash.info[0]
        fh.clear()
        
        // As above, but using the named arg API
        fh.info(msgs: ['2ArgMsg': [unresolvableKey, resolvableKey]], resolveArgs: true)
        assertEquals "message $unresolvableKey $resolvedKey", controller.flash.info[0]
    }

    /**
     * Same as <code>testMsgKeysWithoutArgs</code>, but with various combinations of
     * default messages and locales
     */
    void testMsgKeysWithoutArgsWithLocaleAndOrDefaultMsg() {

        def frenchMessages = ["le premier message", "le deusieme message"]
        def frenchLocale = Locale.FRENCH
        def defaultMsgs = ['default1', 'default2']

        // Test Locale
        def fh = getFlashHelper()
        fh.info 'key1', frenchLocale
        assertEquals frenchMessages[0], controller.flash.info[0]

        fh.info 'key2', frenchLocale
        assertEquals frenchMessages[0..1], controller.flash.info
        fh.clear()

        // Test default message is ignored when key is found in resource bundles
        fh.info 'key1', frenchLocale, 'default'
        assertEquals frenchMessages[0], controller.flash.info[0]

        fh.info('key2', frenchLocale, 'default2')
        assertEquals frenchMessages[0..1], controller.flash.info
        fh.clear()

        // Test default message is used when key is not found in resource bundles
        fh.info 'badKey', frenchLocale, defaultMsgs[0]
        assertEquals defaultMsgs[0], controller.flash.info[0]

        fh.info 'badKey2', frenchLocale, defaultMsgs[1]
        assertEquals(defaultMsgs[0..1], controller.flash.info)
    }

    /**
     * flashHelper.info '1ArgMsg': 1
     * flashHelper.info '2ArgMsg': ['number', '2'], 'other2ArgMsg': ['number', '3']
     */
    void testMsgKeysWithArgs() {
        def fh = getFlashHelper()

        // add a message that takes one arg
        fh.info '1ArgMsg': 1
        assertEquals getMessages(), controller.flash.info[0]

        // add a messages that takes two args
        fh.info '2ArgMsg': ['number', '2']
        assertEquals getMessages(1..2), controller.flash.info
    }

    /**
     * Same as <code>testMsgKeysWithArgs</code>, but with various combinations of
     * default messages and locales
     */
    void testMsgKeysWithArgsAndLocaleAndOrDefaultMsg() {

        def frenchMessages = ["message numero 1", "message numero 2", "message numero 3"]
        def frenchLocale = Locale.FRENCH
        def defaultMsgs = ['default1', 'default2', 'default3']

        // Test Locale
        def fh = getFlashHelper()
        fh.info '1ArgMsg': 1, frenchLocale
        assertEquals frenchMessages[0], controller.flash.info[0]

        fh.info '2ArgMsg': ['numero', 2], frenchLocale
        assertEquals frenchMessages[0..1], controller.flash.info
        fh.clear()

        // Test default message is ignored when key is found in resource bundles
        fh.info '1ArgMsg': 1, frenchLocale, defaultMsgs[0]
        assertEquals frenchMessages[0], controller.flash.info[0]

        fh.info '2ArgMsg': ['numero', 2], frenchLocale, defaultMsgs[1]
        assertEquals frenchMessages[0..1], controller.flash.info
        fh.clear()

        // Test default message is used when key is not found in resource bundles
        fh.info 'badKey': 1, frenchLocale, defaultMsgs[0]
        assertEquals defaultMsgs[0], controller.flash.info[0]

        fh.info 'badKey2': ['numero', 2], frenchLocale, defaultMsgs[1]
        assertEquals(defaultMsgs[0..1], controller.flash.info)
        fh.clear()

        // Default messages without a Locale
        fh.info 'badKey2': ['numero', 2], 'default msg'
        assertEquals(['default msg'], controller.flash.info)
    }

    /**
     * Invokes the flash helper using named arguments, i.e. with a single <tt>Map</tt> argument
     */
    void testNamedArgsApi() {
        
        def fh = getFlashHelper()
        
        // a literal message
        fh.info(msgs: getMessages())
        assertEquals getMessages(), controller.flash.info[0]
        fh.clear()
        
        // A message key with an arg and a Locale
        fh.info(msgs: ['1ArgMsg': 1], locale: Locale.FRENCH)
        assertEquals "message numero 1", controller.flash.info[0]
        fh.clear()
        
        // Use default msg because key doesn't exist
        fh.info(msgs: ['badKey': 1], default: "default message")
        assertEquals "default message", controller.flash.info[0]
        fh.clear()
        
        // Multiple messages using method chainging. Each message has args
        fh.info(msgs: ['2ArgMsg': ['arg', 2]]).info(msgs: ['other2ArgMsg': ['arg', 3]], locale: Locale.FRENCH)
        assertEquals(["message arg 2", "message arg 3"], controller.flash.info)
    }
    
    /**
     * If an invalid key is provided with args and without a default message, an exception should be thrown
     */
    void testMissingMessageException() {

        def fh = getFlashHelper()

        shouldFail(NoSuchMessageException) {
            fh.info 'noSuchKey': 1
        }

        // Also test with a locale
        shouldFail(NoSuchMessageException) {
            fh.info 'noSuchKey': 1, Locale.FRENCH
        }
    }

    /**
     * Test the clear() method 
     */
    void testClear() {
        def fh = getFlashHelper()
        fh.info(["msg1", "msg2"])
        fh.clear()
        assertEquals 0, controller.flash.size()
    }

    void testMethodChaining() {
        def fh = getFlashHelper().info("infoMsg").warn("warnMsg")

        assertEquals(['infoMsg'], controller.flash.info)
        assertEquals(['warnMsg'], controller.flash.warn)

        // remove all messages, then add one
        fh.clear().error "errorMsg"
        assertEquals 1, controller.flash.size()
    }

    /**
     * If the keys that may be used are restricted and an attempt is made to place a message in flash scope with an
     * prohibited key, an exception should be thrown
     */
    void testSingleFlashKeyPermitted() {

        grailsApplication.config.flashHelper.keys = "info"

        def fh = getFlashHelper()
        fh.info 'foo'

        shouldFail(FlashKeyException) {
            fh.badKey "foo"
        }
    }

    void testMultipleFlashKeysPermitted() {

        grailsApplication.config.flashHelper.keys = ["info", "warn"]

        def fh = getFlashHelper()
        fh.info 'foo'
        fh.warn 'foo'

        shouldFail(FlashKeyException) {
            fh.badKey "foo"
        }
    }
}