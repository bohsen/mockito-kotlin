package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.expect.fail
import com.nhaarman.mockitokotlin2.*
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runners.MethodSorters
import org.junit.runners.MethodSorters.*
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_MOCKS
import org.mockito.exceptions.base.MockitoAssertionError
import org.mockito.exceptions.verification.WantedButNotInvoked
import org.mockito.listeners.InvocationListener
import org.mockito.mock.SerializableMode.BASIC
import org.mockito.stubbing.Answer
import java.io.IOException
import java.io.PrintStream
import java.io.Serializable


/*
 * The MIT License
 *
 * Copyright (c) 2016 Niek Haarman
 * Copyright (c) 2007 Mockito contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

@FixMethodOrder(NAME_ASCENDING)
@Suppress("DEPRECATION")
class MockitoTest : TestBase() {

    @Test
    fun `Test any with string type`() {
        mock<Methods>().apply {
            string("")
            verify(this).string(any())
        }
    }

    @Test
    fun `Test isNull with nullable type`() {
        mock<Methods?>()?.apply {
            nullableString(null)
            verify(this).nullableString(isNull())
        }
    }

    @Test
    fun `Test isNotNull with nullable type`() {
        mock<Methods?>()?.apply {
            nullableString("")
            verify(this).nullableString(isNotNull())
        }
    }

    @Test
    fun `Test any with closed class`() {
        mock<Methods>().apply {
            closed(Closed())
            verify(this).closed(any())
        }
    }

    @Test
    fun `Test any with IntArray`() {
        mock<Methods>().apply {
            intArray(intArrayOf())
            verify(this).intArray(any())
        }
    }

    @Test
    fun `Test any with closed ClassArray`() {
        mock<Methods>().apply {
            closedArray(arrayOf(Closed(), Closed()))
            verify(this).closedArray(anyArray())
        }
    }

    @Test
    fun `Test any with ClassArray that includes a null`() {
        mock<Methods>().apply {
            closedNullableArray(arrayOf(Closed(), null, Closed()))
            verify(this).closedNullableArray(anyArray())
        }
    }

    @Test
    fun `Test any with String vararg`() {
        mock<Methods>().apply {
            stringVararg(String(), String())
            verify(this).stringVararg(anyVararg())
        }
    }

    @Test
    fun `When any used with null value, any should not verify`() {
        mock<Methods>().apply {
            nullableString(null)
            verify(this, never()).nullableString(any())
        }
    }

    @Test
    fun `When anyOrNull is used with null value, anyOrNull should verify`() {
        mock<Methods>().apply {
            nullableString(null)
            verify(this).nullableString(anyOrNull())
        }
    }

    @Test
    fun `Test any with throwable that has single throwable constructor`() {
        mock<Methods>().apply {
            throwableClass(ThrowableClass(IOException()))
            verify(this).throwableClass(any())
        }
    }

    @Test
    fun `When argThat is used on list, it should verify properties of given list`() {
        mock<Methods>().apply {
            closedList(listOf(Closed(), Closed()))
            verify(this).closedList(argThat {
                size == 2
                size != 1
            })
        }
    }

    @Test
    fun `When argThat is used on list with a null element, it should verify properties of given list`() {
        mock<Methods>().apply {
            closedListWithNullValues(listOf(Closed(), null))
            verify(this).closedListWithNullValues(argThat {
                size == 2
                size != 1
            })
        }
    }

    @Test
    fun `When argWhich is used on list, it should verify properties of given list`() {
        mock<Methods>().apply {
            closedList(listOf(Closed(), Closed()))
            verify(this).closedList(argForWhich {
                size == 2
                size != 1
            })
        }
    }

    @Test
    fun `When argForWhich() is used on list with a null element, it should verify properties of given list`() {
        mock<Methods>().apply {
            closedListWithNullValues(listOf(Closed(), null))
            verify(this).closedListWithNullValues(argForWhich {
                size == 2
                size != 1
            })
        }
    }

    @Test
    fun `When argWhere() is used on list, it should verify properties of given list`() {
        mock<Methods>().apply {
            closedList(listOf(Closed(), Closed()))
            verify(this).closedList(argWhere {
                it.size == 2
                it.size != 1
            })
        }
    }

    @Test
    fun `When argWhere() is used on list with a null element, it should verify properties of given list`() {
        mock<Methods>().apply {
            closedListWithNullValues(listOf(Closed(), null))
            verify(this).closedListWithNullValues(argWhere {
                it.size == 2
                it.size != 1
            })
        }
    }

    @Test
    fun `check() on list, should verify size and type of element in list`() {
        mock<Methods>().apply {
            closedList(listOf(Closed(), Closed()))
            verify(this).closedList(check {
                expect(it.size).toBe(2)
                expect(it[0]).toBeInstanceOf<Closed>()
                expect(it[1]).toBeInstanceOf<Closed>()
            })
        }
    }

    @Test
    fun `check() with null as argument should throw error`() {
        mock<Methods>().apply {
            nullableString(null)

            expectErrorWithMessage("null").on {
                verify(this).nullableString(check {})
            }
        }
    }

    @Test
    fun `atLeast() should verify that method was invoked at least x number of times`() {
        mock<Methods>().apply {
            string("")
            string("")

            verify(this, atLeast(2)).string(any())
        }
    }

    @Test
    fun `atLeastOnce() should verify that method was invoked at least once`() {
        mock<Methods>().apply {
            string("")
            string("")

            verify(this, atLeastOnce()).string(any())
        }
    }

    @Test
    fun `atMost() should verify that method was invoked at most x number of times`() {
        mock<Methods>().apply {
            string("")
            string("")

            verify(this, atMost(2)).string(any())
        }
    }

    @Test
    fun `calls() should verify that method was invoked x number of times`() {
        mock<Methods>().apply {
            string("")
            string("")

            inOrder(this).verify(this, calls(2)).string(any())
        }
    }

    @Test
    fun `inOrder() should work with lambda (function as a type)`() {
        /* Given */
        val a = mock<() -> Unit>()
        val b = mock<() -> Unit>()

        /* When */
        b()
        a()

        /* Then */
        inOrder(a, b) {
            verify(b).invoke()
            verify(a).invoke()
        }
    }

    @Test
    fun `clearInvocations() should prevent method from being invoked`() {
        mock<Methods>().apply {
            string("")
            clearInvocations(this)

            verify(this, never()).string(any())
        }

    }

    @Test
    fun `when assertion error is thrown, description() should add a specific description`() {
        try {
            mock<Methods>().apply {
                verify(this, description("Test")).string(any())
            }
            throw AssertionError("Verify should throw Exception.")
        } catch (e: MockitoAssertionError) {
            expect(e.message).toContain("Test")
        }
    }

    @Test
    fun `Test doAnswer() with string value`() {
        val mock = mock<Methods>()

        doAnswer { "Test" }
                .whenever(mock)
                .stringResult()

        expect(mock.stringResult()).toBe("Test")
    }

    @Test
    fun `When doCallRealMethod() is used, it should call reail method and return default string value`() {
        val mock = mock<Open>()

        doReturn("Test").whenever(mock).stringResult()
        doCallRealMethod().whenever(mock).stringResult()

        expect(mock.stringResult()).toBe("Default")
    }

    @Test
    fun `When doNothing() is used, it should block method from being invoked`() {
        val spy = spy(Open())
        val array = intArrayOf(3)

        doNothing().whenever(spy).modifiesContents(array)
        spy.modifiesContents(array)

        expect(array[0]).toBe(3)
    }

    @Test
    fun `When doReturn() is used, it should return string value`() {
        val mock = mock<Methods>()

        doReturn("test").whenever(mock).stringResult()

        expect(mock.stringResult()).toBe("test")
    }

    @Test
    fun `When doReturn() is used with null value, it should return null`() {
        val mock = mock<Methods>()

        doReturn(null).whenever(mock).stringResult()

        expect(mock.stringResult()).toBeNull()
    }

    @Test
    fun `When doReturn() is used with multiple null values, it should return null values multiple times`() {
        val mock = mock<Methods>()

        doReturn(null, null).whenever(mock).stringResult()

        expect(mock.stringResult()).toBeNull()
        expect(mock.stringResult()).toBeNull()
    }

    @Test
    fun `When doReturn() is used with multiple values, it should return values in correct order`() {
        val mock = mock<Methods>()

        doReturn("test", "test2").whenever(mock).stringResult()

        expect(mock.stringResult()).toBe("test")
        expect(mock.stringResult()).toBe("test2")
    }

    @Test
    fun `When doThrow() is used, it should throw exception of specified type`() {
        val mock = mock<Open>()

        doThrow(IllegalStateException::class).whenever(mock).go()

        try {
            mock.go()
            throw AssertionError("Call should have thrown.")
        } catch (e: IllegalStateException) {
            expect(e).toBeInstanceOf<IllegalStateException>()
        }
    }

    @Test
    fun `When doThrow() with custom message is used, it should throw exception of specified type with specified message`() {
        val mock = mock<Open>()

        doThrow(IllegalStateException("test")).whenever(mock).go()

        expectErrorWithMessage("test").on {
            mock.go()
        }
    }

    @Test
    fun `When on() is used with regular function doReturn, method stub should return specified value`() {
        /* Given */
        val mock = mock<Open>()
        mock<Open> {
            on(mock.stringResult()).doReturn("A")
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `When on() is used with infix function doReturn, method stub should return specified value`() {
        /* Given */
        val mock = mock<Open>() {
            on { stringResult() } doReturn "A"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `When stubbing same method twice, the latest stub should be applied to the method call`() {
        /* Given */
        val mock = mock<Open>() {
            on { stringResult() }.doReturn("A")
        }
        whenever(mock.stringResult()).thenReturn("B")

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("B")
    }

    @Test
    fun `When mock is used in builder method, the returned object should be the mock`() {
        /* Given */
        val mock = mock<Methods> { mock ->
            on { builderMethod() } doReturn mock
        }

        /* When */
        val result = mock.builderMethod()

        /* Then */
        expect(result).toBeTheSameAs(mock)
    }

    @Test
    fun `It should be possible to stub method that returns a nullable type`() {
        /* Given */
        val mock = mock<Methods> {
            on { nullableStringResult() } doReturn "Test"
        }

        /* When */
        val result = mock.nullableStringResult()

        /* Then */
        expect(result).toBe("Test")
    }

    @Ignore("This test shows one problem that mockito-kotlin still needs to adress")
    @Test
    fun `It should be possible to return null from a stub method that returns a nullable type`() {

        /* Given */
        val mock = mock<Methods> {
            on { nullableStringResult() } doReturn "null" /** Remove braces on value */
        }

        /* When */
        val result = mock.nullableStringResult()

        /* Then */
        expect(result).toBeNull()
        TODO("Refactor mockito-kotlin to support returning null. The above test should pass.")
    }

    @Test
    fun `When doThrow() as infix function, method stub should throw specified exception`() {
        /* Given */
        val mock = mock<Methods> {
            on { builderMethod() } doThrow IllegalArgumentException()
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (e: IllegalArgumentException) {
            /* Then */
            expect(e).toBeInstanceOf<IllegalArgumentException>()
        }
    }

    @Test
    fun `When doThrow() is used with KClass as argument, method stub should throw specified exception`() {
        /* Given */
        val mock = mock<Methods> {
            on { builderMethod() } doThrow IllegalArgumentException::class
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (e: IllegalArgumentException) {
            /* Then */
            expect(e).toBeInstanceOf<IllegalArgumentException>()
        }
    }

    @Test
    fun `When doThrow() is used with varags as argument, method stub should throw specified exceptions in correct order`() {
        /* Given */
        val mock = mock<Methods> {
            on { builderMethod() }.doThrow(IllegalArgumentException(), UnsupportedOperationException())
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (e: IllegalArgumentException) {
            expect(e).toBeInstanceOf<IllegalArgumentException>()
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (e: UnsupportedOperationException) {
            expect(e).toBeInstanceOf<UnsupportedOperationException>()
        }
    }

    @Test
    fun `When doThrow() is used with KClass as vararg arguments, method stub should throw specified exceptions in correct order`() {
        /* Given */
        val mock = mock<Methods> {
            on { builderMethod() }.doThrow(IllegalArgumentException::class, UnsupportedOperationException::class)
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (e: IllegalArgumentException) {
            expect(e).toBeInstanceOf<IllegalArgumentException>()
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (e: UnsupportedOperationException) {
            expect(e).toBeInstanceOf<UnsupportedOperationException>()
        }
    }

    @Test
    fun `When infix function doAnswer() is used, method stub should return specified value`() {
        /* Given */
        val mock = mock<Methods> {
            on { stringResult() } doAnswer { "result" }
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun `When infix function doAnswer() is used with Answer instance, method stub should return specified value`() {
        /* Given */
        val mock = mock<Methods> {
            on { stringResult() } doAnswer Answer<String> { "result" }
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun `When infix function doAnswer() is used with RETURNS_SELF, method stub should return itself`() {
        /* Given */
        val mock = mock<Methods> {
            on { builderMethod() } doAnswer Mockito.RETURNS_SELF
        }

        /* When */
        val result = mock.builderMethod()

        /* Then */
        expect(result).toBe(mock)
    }

    @Test
    fun `When infix function doAnswer() is used with custom arguments, method stub should return specified value`() {
        /* Given */
        val mock = mock<Methods> {
            on { stringResult(any()) } doAnswer { "${it.arguments[0]}-result" }
        }

        /* When */
        val result = mock.stringResult("argument")

        /* Then */
        expect(result).toBe("argument-result")
    }

    @Test
    fun `When stubbing method after creation of mock, it should return specified value of stub method`() {
        val mock = mock<Methods>()

        //create stub after creation of mock
        mock.stub {
            on { stringResult() } doReturn "result"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun `When overriding stub, it should return specified value of overridden stub method`() {
        /* Given mock with stub */
        val mock = mock<Methods> {
            on { stringResult() } doReturn "result1"
        }

        /* override stub */
        mock.stub {
            on { stringResult() } doReturn "result2"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("result2")
    }

    @Test
    fun `When specifying a custom name for a mock, this name should be used in assertionExceptions`() {
        /* Given */
        val mock = mock<Methods>("myName")

        /* Expect */
        expectErrorWithMessage("myName.stringResult()") on {
            verify(mock).stringResult()
        }
    }

    @Test
    fun `When specifying a default Answer for a mock, it should return this Answer as default`() {
        /* Given */
        val mock = mock<Methods>(Mockito.RETURNS_SELF)

        /* When */
        val result = mock.builderMethod()

        /* Then */
        expect(result).toBe(mock)
    }

    @Test
    fun `When specifying a default Answer with named prams for a mock, it should return this Answer as default`() {
        /* Given */
        val mock = mock<Methods>(defaultAnswer = Mockito.RETURNS_SELF)

        /* When */
        val result = mock.builderMethod()

        /* Then */
        expect(result).toBe(mock)
    }

    @Test
    fun mock_withSettings_extraInterfaces() {
        /* Given */
        val mock = mock<Methods>(
                withSettings().extraInterfaces(ExtraInterface::class.java)
        )

        /* Then */
        expect(mock).toBeInstanceOf<ExtraInterface>()
    }

    @Test
    fun mock_withSettings_name() {
        /* Given */
        val mock = mock<Methods>(
                withSettings().name("myName")
        )

        /* When */
        expectErrorWithMessage("myName.stringResult()") on {
            verify(mock).stringResult()
        }
    }

    @Test
    fun mock_withSettings_defaultAnswer() {
        /* Given */
        val mock = mock<Methods>(
                withSettings().defaultAnswer(RETURNS_MOCKS)
        )

        /* When */
        val result = mock.nonDefaultReturnType()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun mock_withSettings_serializable() {
        /* Given */
        val mock = mock<Methods>(
                withSettings().serializable()
        )

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mock_withSettings_serializableMode() {
        /* Given */
        val mock = mock<Methods>(
                withSettings().serializable(BASIC)
        )

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mock_withSettings_verboseLogging() {
        /* Given */
        val out = mock<PrintStream>()
        System.setOut(out)
        val mock = mock<Methods>(
                withSettings().verboseLogging()
        )

        try {
            /* When */
            verify(mock).stringResult()
            fail("Expected an exception")
        } catch (e: WantedButNotInvoked) {
            /* Then */
            verify(out).println("methods.stringResult();")
        }
    }

    @Test
    fun mock_withSettings_invocationListeners() {
        /* Given */
        var bool = false
        val mock = mock<Methods>(
                withSettings().invocationListeners(InvocationListener { bool = true })
        )

        /* When */
        mock.stringResult()

        /* Then */
        expect(bool).toHold()
    }

    @Test
    fun mock_withSettings_stubOnly() {
        /* Given */
        val mock = mock<Methods>(
                withSettings().stubOnly()
        )

        /* Expect */
        expectErrorWithMessage("is a stubOnly() mock") on {

            /* When */
            verify(mock).stringResult()
        }
    }

    @Test
    fun mock_withSettings_useConstructor() {
        /* Expect */
        expectErrorWithMessage("Unable to create mock instance of type") on {
            mock<ThrowingConstructor>(
                    withSettings().useConstructor()
            )
        }
    }

    @Test
    fun mock_withSettingsAPI_extraInterfaces() {
        /* Given */
        val mock = mock<Methods>(
                extraInterfaces = arrayOf(ExtraInterface::class)
        )

        /* Then */
        expect(mock).toBeInstanceOf<ExtraInterface>()
    }

    @Test
    fun mock_withSettingsAPI_name() {
        /* Given */
        val mock = mock<Methods>(name = "myName")

        /* When */
        expectErrorWithMessage("myName.stringResult()") on {
            verify(mock).stringResult()
        }
    }

    @Test
    fun mock_withSettingsAPI_defaultAnswer() {
        /* Given */
        val mock = mock<Methods>(defaultAnswer = RETURNS_MOCKS)

        /* When */
        val result = mock.nonDefaultReturnType()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun mock_withSettingsAPI_serializable() {
        /* Given */
        val mock = mock<Methods>(serializable = true)

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mock_withSettingsAPI_serializableMode() {
        /* Given */
        val mock = mock<Methods>(serializableMode = BASIC)

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mock_withSettingsAPI_verboseLogging() {
        /* Given */
        val out = mock<PrintStream>()
        System.setOut(out)
        val mock = mock<Methods>(verboseLogging = true)

        try {
            /* When */
            verify(mock).stringResult()
            fail("Expected an exception")
        } catch (e: WantedButNotInvoked) {
            /* Then */
            verify(out).println("methods.stringResult();")
        }
    }

    @Test
    fun mock_withSettingsAPI_invocationListeners() {
        /* Given */
        var bool = false
        val mock = mock<Methods>(invocationListeners = arrayOf(InvocationListener { bool = true }))

        /* When */
        mock.stringResult()

        /* Then */
        expect(bool).toHold()
    }

    @Test
    fun mock_withSettingsAPI_stubOnly() {
        /* Given */
        val mock = mock<Methods>(stubOnly = true)

        /* Expect */
        expectErrorWithMessage("is a stubOnly() mock") on {

            /* When */
            verify(mock).stringResult()
        }
    }

    @Test
    fun mock_withSettingsAPI_useConstructor() {
        /* Given */
        expectErrorWithMessage("Unable to create mock instance of type ") on {
            mock<ThrowingConstructor>(useConstructor = true) {}
        }
    }

    @Test
    fun mockStubbing_withSettingsAPI_extraInterfaces() {
        /* Given */
        val mock = mock<Methods>(extraInterfaces = arrayOf(ExtraInterface::class)) {}

        /* Then */
        expect(mock).toBeInstanceOf<ExtraInterface>()
    }

    @Test
    fun mockStubbing_withSettingsAPI_name() {
        /* Given */
        val mock = mock<Methods>(name = "myName") {}

        /* When */
        expectErrorWithMessage("myName.stringResult()") on {
            verify(mock).stringResult()
        }
    }

    @Test
    fun mockStubbing_withSettingsAPI_defaultAnswer() {
        /* Given */
        val mock = mock<Methods>(defaultAnswer = RETURNS_MOCKS) {}

        /* When */
        val result = mock.nonDefaultReturnType()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun mockStubbing_withSettingsAPI_serializable() {
        /* Given */
        val mock = mock<Methods>(serializable = true) {}

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mockStubbing_withSettingsAPI_serializableMode() {
        /* Given */
        val mock = mock<Methods>(serializableMode = BASIC) {}

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mockStubbing_withSettingsAPI_verboseLogging() {
        /* Given */
        val out = mock<PrintStream>()
        System.setOut(out)
        val mock = mock<Methods>(verboseLogging = true) {}

        try {
            /* When */
            verify(mock).stringResult()
            fail("Expected an exception")
        } catch (e: WantedButNotInvoked) {
            /* Then */
            verify(out).println("methods.stringResult();")
        }
    }

    @Test
    fun mockStubbing_withSettingsAPI_invocationListeners() {
        /* Given */
        var bool = false
        val mock = mock<Methods>(invocationListeners = arrayOf(InvocationListener { bool = true })) {}

        /* When */
        mock.stringResult()

        /* Then */
        expect(bool).toHold()
    }

    @Test
    fun mockStubbing_withSettingsAPI_stubOnly() {
        /* Given */
        val mock = mock<Methods>(stubOnly = true) {}

        /* Expect */
        expectErrorWithMessage("is a stubOnly() mock") on {

            /* When */
            verify(mock).stringResult()
        }
    }

    @Test
    fun mockStubbing_withSettingsAPI_useConstructor() {
        /* Given */
        expectErrorWithMessage("Unable to create mock instance of type ") on {
            mock<ThrowingConstructor>(useConstructor = true) {}
        }
    }

    @Test
    fun stubbingTwiceWithArgumentMatchers() {
        /* When */
        val mock = mock<Methods> {
            on { stringResult(argThat { this == "A" }) } doReturn "A"
            on { stringResult(argThat { this == "B" }) } doReturn "B"
        }

        /* Then */
        expect(mock.stringResult("A")).toBe("A")
        expect(mock.stringResult("B")).toBe("B")
    }

    @Test
    fun stubbingTwiceWithCheckArgumentMatchers_throwsException() {
        /* Expect */
        expectErrorWithMessage("null").on {
            mock<Methods> {
                on { stringResult(check { }) } doReturn "A"
                on { stringResult(check { }) } doReturn "B"
            }
        }
    }

    @Test
    fun doReturn_withSingleItemList() {
        /* Given */
        val mock = mock<Open> {
            on { stringResult() } doReturn listOf("a", "b")
        }

        /* Then */
        expect(mock.stringResult()).toBe("a")
        expect(mock.stringResult()).toBe("b")
    }

    @Test
    fun doReturn_throwsNPE() {
        expectErrorWithMessage("look at the stack trace below") on {

            /* When */
            mock<Open> {
                on { throwsNPE() } doReturn "result"
            }
        }
    }

    @Test
    fun doReturn_withGenericIntReturnType_on() {
        /* Expect */
        expectErrorWithMessage("onGeneric") on {

            /* When */
            mock<GenericMethods<Int>> {
                on { genericMethod() } doReturn 2
            }
        }
    }

    @Test
    fun doReturn_withGenericIntReturnType_onGeneric() {
        /* Given */
        val mock = mock<GenericMethods<Int>> {
            onGeneric { genericMethod() } doReturn 2
        }

        /* Then */
        expect(mock.genericMethod()).toBe(2)
    }

    @Test
    fun doReturn_withGenericNullableReturnType_onGeneric() {
        val m = mock<GenericMethods<String>> {
            onGeneric { nullableReturnType() } doReturn "Test"
        }

        expect(m.nullableReturnType()).toBe("Test")
    }

    @Test
    fun `isA() should verify non nullable type is of correct type`() {
        mock<Methods>().apply {
            anyType(String())
            verify(this).anyType(isA<String>())
        }
    }

    @Ignore("Issue with nullable types")
    @Test
    fun `isA() should verify nullable type is of correct type`() {
        mock<Methods>().apply {
            anyNullableType(String())
            verify(this).anyNullableType(isA<String>())
        }
        TODO("Refactor mockito-kotlin to show correct behavior -> like this: verify(this).anyNullableType(isA<String?>())")
    }

    @Test
    fun `same() should verify that non-nullable value is the same as given non-null value`() {
        mock<Methods>().apply {
            string("")
            verify(this).string(same(""))
        }
    }

    @Test
    fun `same() should verify that nullable value is the same as given non-null value`() {
        mock<Methods>().apply {
            nullableString("")
            verify(this).nullableString(same(""))
        }
    }

    @Test
    fun `same() should verify that null value is the same as null`() {
        mock<Methods>().apply {
            nullableString(null)
            verify(this).nullableString(same(null))
        }
    }

    @Test
    fun `When stubbing() is used on existing mock, it should mock specified methods`() {
        /* Given */
        val mock = mock<Methods>()

        /* When */
        stubbing(mock) {
            on { stringResult() } doReturn "result"
        }

        /* Then */
        expect(mock.stringResult()).toBe("result")
    }
}