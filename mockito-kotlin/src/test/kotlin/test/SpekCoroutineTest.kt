package test

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyBlocking
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withContext
import org.mockito.ArgumentMatchers.anyInt
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object SpekTestCoroutines : Spek({

    val mockDependency by memoized { mock<Foo>() }
    val subject by memoized { Bar(mockDependency) }

    describe("doing something") {
        runBlocking {
            subject.result(42)

            it("uses the dependency in a certain way") {
                verifyBlocking(mockDependency) { suspending() }
            }
        }
    }
})

interface Foo {
    suspend fun suspending(): Int
    fun nonsuspending(): Int
}

class Bar(val foo: Foo) {
    suspend fun result(r: Int) = withContext(CommonPool) { foo.suspending() }
}

