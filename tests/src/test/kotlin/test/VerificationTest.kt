package test

import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import org.mockito.exceptions.base.MockitoAssertionError

class VerificationTest : TestBase() {

    @Test
    fun atLeastXInvocations() {
        mock<Methods>().apply {
            string("")
            string("")

            verify(this, atLeast(2)).string(any())
        }
    }

    @Test
    fun testAtLeastOnce() {
        mock<Methods>().apply {
            string("")
            string("")

            verify(this, atLeastOnce()).string(any())
        }
    }

    @Test
    fun atMostXInvocations() {
        mock<Methods>().apply {
            string("")
            string("")

            verify(this, atMost(2)).string(any())
        }
    }

    @Test
    fun testCalls() {
        mock<Methods>().apply {
            string("")
            string("")

            inOrder(this).verify(this, calls(2)).string(any())
        }
    }

    @Test
    fun testInOrderWithLambda() {
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
    fun testInOrderWithReceiver() {
        /* Given */
        val mock = mock<Methods>()

        /* When */
        mock.string("")
        mock.int(0)

        /* Then */
        mock.inOrder {
            verify().string(any())
            verify().int(any())
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun testClearInvocations() {
        val mock = mock<Methods>().apply {
            string("")
        }

        clearInvocations(mock)

        verify(mock, never()).string(any())
    }

    @Test
    fun testDescription() {
        try {
            mock<Methods>().apply {
                verify(this, description("Test")).string(any())
            }
            throw AssertionError("Verify should throw Exception.")
        } catch (e: MockitoAssertionError) {
            expect(e.message).toContain("Test")
        }
    }

    interface UnderTest {
        fun f(s: String, i: Int): String
    }

    @Test
    fun verifications() {

        val underTest = mock<UnderTest>()

        underTest.f("s1", 1)
        underTest.f("s2", 2)

        verify(underTest).f(eq("s1"), eq(1))
        verify(underTest).f(eq("s2"), eq(2))
    }
}