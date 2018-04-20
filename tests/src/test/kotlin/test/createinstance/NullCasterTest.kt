package test.createinstance

import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.internal.createInstance
import org.junit.Test
import test.TestBase


class NullCasterTest : TestBase() {

    @Test
    fun createInstance() {
        /* When */
        val result: String = createInstance(null)

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun kotlinAcceptsNullValue() {
        /* Given */
        val s: String = createInstance(null)

        /* When */
        acceptNonNullableString(s)
    }

    private fun acceptNonNullableString(@Suppress("UNUSED_PARAMETER") s: String) {
    }
}
