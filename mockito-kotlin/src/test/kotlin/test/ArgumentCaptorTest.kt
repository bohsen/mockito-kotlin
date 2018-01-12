package test

import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import java.util.*

class ArgumentCaptorTest : TestBase() {

    @Test
    fun `test argumentCaptor with single value`() {
        /* Given */
        val date: Date = mock()

        /* When */
        date.time = 5L

        /* Then */
        val captor = argumentCaptor<Long>()
        verify(date).time = captor.capture()
        expect(captor.lastValue).toBe(5L)
    }

    @Test
    fun `test argumentCaptor with null value using non-nullable type`() {
        /* Given */
        val m: Methods = mock()

        /* When */
        m.nullableString(null)

        /* Then */
        val captor = argumentCaptor<String>()
        verify(m).nullableString(captor.capture())
        expect(captor.lastValue).toBeNull()
    }

    @Test
    fun `test argumentCaptor with null value using nullable type`() {
        /* Given */
        val m: Methods = mock()

        /* When */
        m.nullableString(null)

        /* Then */
        val captor = argumentCaptor<String?>()
        verify(m).nullableString(captor.capture())
        expect(captor.lastValue).toBeNull()
    }

    @Test
    fun `test argumentCaptor with multiple non-null values`() {
        /* Given */
        val date: Date = mock()

        /* When */
        date.time = 5L
        date.time = 7L

        /* Then */
        val captor = argumentCaptor<Long>()
        verify(date, times(2)).time = captor.capture()
        expect(captor.allValues).toBe(listOf(5, 7))
    }

    @Test
    fun `test nullableArgumentCaptor with multiple values including null`() {
        /* Given */
        val m: Methods = mock()

        /* When */
        m.nullableString("test")
        m.nullableString(null)

        /* Then */
        val captor = nullableArgumentCaptor<String>()
        verify(m, times(2)).nullableString(captor.capture())
        expect(captor.allValues).toBe(listOf("test", null))
    }

    @Test
    fun `test argumentCaptor with multiple values including null`() {
        /* Given */
        val m: Methods = mock()

        /* When */
        m.nullableString("test")
        m.nullableString(null)

        /* Then */
        val captor = argumentCaptor<String?>()
        verify(m, times(2)).nullableString(captor.capture())
        expect(captor.allValues).toBe(listOf("test", null))
    }

    @Test
    fun `test argumentCaptor call properties`() {
        /* Given */
        val m: Methods = mock()

        /* When */
        m.int(1)
        m.int(2)
        m.int(3)
        m.int(4)
        m.int(5)

        /* Then */
        argumentCaptor<Int>().apply {
            verify(m, times(5)).int(capture())

            expect(firstValue).toBe(1)
            expect(secondValue).toBe(2)
            expect(thirdValue).toBe(3)
            expect(lastValue).toBe(5)
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `test argumentCaptor call property not available`() {
        /* Given */
        val m: Methods = mock()

        /* When */
        m.int(1)

        /* Then */
        argumentCaptor<Int>().apply {
            verify(m).int(capture())
            expect(firstValue).toBe(1)
            expect(secondValue).toBe(2)
        }
    }
}
