package test.createinstance

import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.internal.NullCaster
import org.junit.Test
import test.TestBase


class NullCasterTest : TestBase() {

    private val nullCaster = NullCaster()

    @Test
    fun createInstance() {
        /* When */
        val result = nullCaster.createInstance(String::class)

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun kotlinAcceptsNullValue() {
        /* Given */
        val s: String = nullCaster.createInstance(String::class)

        /* When */
        expect(s).toBeNull()

        /* Then */
        acceptNullWithNonNullableString(s)
    }

    private fun acceptNullWithNonNullableString(@Suppress("UNUSED_PARAMETER") s: String) {
    }
}
