@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package test

import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyBlocking
import kotlinx.coroutines.experimental.*
import org.junit.Test


class CoroutinesTest {

    @Test
    fun stubbingSuspending() {
        /* Given */
        val m = mock<SomeInterface> {
            onBlocking { suspending() } doReturn 42
        }

        /* When */
        val result = runBlocking { m.suspending() }

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingSuspending_usingSuspendingFunction() {
        /* Given */
        val m = mock<SomeInterface> {
            onBlocking { suspending() } doReturn runBlocking { SomeClass().result(42) }
        }

        /* When */
        val result = runBlocking { m.suspending() }

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingSuspending_runBlocking() = runBlocking {
        /* Given */
        val m = mock<SomeInterface> {
            onBlocking { suspending() } doReturn 42
        }

        /* When */
        val result = m.suspending()

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingNonSuspending() {
        /* Given */
        val m = mock<SomeInterface> {
            onBlocking { nonsuspending() } doReturn 42
        }

        /* When */
        val result = m.nonsuspending()

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingNonSuspending_runBlocking() = runBlocking {
        /* Given */
        val m = mock<SomeInterface> {
            onBlocking { nonsuspending() } doReturn 42
        }

        /* When */
        val result = m.nonsuspending()

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun delayingResult() {
        /* Given */
        val m = SomeClass()

        /* When */
        val result = runBlocking { m.delaying() }

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun delayingResult_runBlocking() = runBlocking {
        /* Given */
        val m = SomeClass()

        /* When */
        val result = m.delaying()

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun verifySuspendFunctionCalled() {
        /* Given */
        val m = mock<SomeInterface>()

        /* When */
        runBlocking { m.suspending() }

        /* Then */
        runBlocking { verify(m).suspending() }
    }

    @Test
    fun verifySuspendFunctionCalled_runBlocking() = runBlocking<Unit> {
        val m = mock<SomeInterface>()

        m.suspending()

        verify(m).suspending()
    }

    @Test
    fun verifySuspendFunctionCalled_verifyBlocking() {
        val m = mock<SomeInterface>()

        runBlocking { m.suspending() }

        verifyBlocking(m) { suspending() }
    }

    @Test
    fun mockSuspendingFunction() {
        val suspendFunction : suspend () -> Unit = mock()

        runBlocking {
            suspendFunction()
            verify(suspendFunction).invoke()
        }
    }
}

interface SomeInterface {

    suspend fun suspending(): Int
    fun nonsuspending(): Int
}

class SomeClass {

    suspend fun result(r: Int) = withContext(CommonPool) { r }

    suspend fun delaying() = withContext(CommonPool) {
        delay(100)
        42
    }
}
