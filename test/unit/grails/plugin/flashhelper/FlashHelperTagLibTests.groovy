package grails.plugin.flashhelper

import grails.test.mixin.*
import org.junit.Before

@TestFor(FlashHelperTagLib)
class FlashHelperTagLibTests {

    private flashMap

    @Before
    void setUp() {
        flashMap = [oneMsg: 'msg1', msgList: ['msg2', 'msg3'], oneMsgInList: ['msg1']]

        // Mock the flash scope with a map
        tagLib.metaClass.getFlash = {-> flashMap}
    }

    void testFlashMocking() {
        assertEquals 3, tagLib.flash.size()
    }

    void testSingleMessage() {
        assertEquals 'msg1', tagLib.msg(key: 'oneMsg').toString()
        assertEquals 'msg1', tagLib.msg(key: 'oneMsgInList').toString()
    }

    void testMessageRemoval() {

        // By default, messages are not removed when we retrieve them
        2.times {
            assertEquals 'msg1', tagLib.msg(key: 'oneMsg').toString()
        }

        // If we remove the flash entry on retrieval, then try to retrieve it again with 'keyNotFound' set to 'error'
        // we should get an exception
        tagLib.msg(key: 'oneMsg', remove: true)

        shouldFail(FlashKeyException) {
            tagLib.msg(key: 'oneMsg', keyNotFound: 'error')
        }
    }

    void testMessageList() {

        // test the default separator
        def output = tagLib.msg(key: 'msgList')
        verifySeparatedOutput(FlashHelperTagLib.DEFAULT_SEPARATOR, output)

        // Test a separator provided as an attribute
        def separator = "|"
        output = tagLib.msg(key: 'msgList', sep: separator)
        verifySeparatedOutput(separator, output)

        // Test a separator provided in Config.groovy
        separator = '_'
        grailsApplication.config.flashHelper.separator = separator
        output = tagLib.msg(key: 'msgList', sep: separator)
        verifySeparatedOutput(separator, output)
    }

    private void verifySeparatedOutput(separator, output) {
        assertEquals flashMap.msgList.join(separator), output.toString()
    }

    void testMissingAttribute() {

        shouldFail {
            tagLib.msg(sep: '_')
        }
    }

    void testMsgBodySingleMessage() {

        String expectedResult = "template text ${flashMap.oneMsg}"

        def actualResult = tagLib.msgBody(key: 'oneMsg') {
            "template text ${it}"
        }
        assertEquals expectedResult, actualResult.toString()

        actualResult = tagLib.msgBody(key: 'oneMsgInList') {
            "template text ${it}"
        }
        assertEquals expectedResult, actualResult.toString()
    }

    void testMsgBodyMessageList() {

        def expectedResult = "template text msg2<br/>template text msg3<br/>"

        def result = tagLib.msgBody(key: 'msgList') {
            "template text ${it}<br/>"
        }
        assertEquals expectedResult, result.toString()
    }

    void testFailOnMissingKey() {

        shouldFail(FlashKeyException) {
            tagLib.msg(key: 'badKey', keyNotFound: 'error')
        }

        shouldFail(FlashKeyException) {
            tagLib.msgBody(key: 'badKey', keyNotFound: 'error')
        }

        // Change the default behaviour to error
        // TODO: Reinstate this test
        grailsApplication.config.flashHelper.keyNotFound = "error"

        shouldFail(FlashKeyException) {
            tagLib.msg(key: 'badKey')
        }

        shouldFail(FlashKeyException) {
            tagLib.msgBody(key: 'badKey')
        }
    }

    void testIgnoreMissingKey() {

        // if the config property is not set, then by default a bad key does not cause an error
        grailsApplication.config.flashHelper.keyNotFound = null

        tagLib.msg(key: 'badKey')
        tagLib.msgBody(key: 'badKey')

        // Change the default behaviour to error, but override it in each tag
        grailsApplication.config.flashHelper.keyNotFound = "error"

        tagLib.msg(key: 'badKey', keyNotFound: 'warn')
        tagLib.msgBody(key: 'badKey', keyNotFound: 'ignore')
    }
}