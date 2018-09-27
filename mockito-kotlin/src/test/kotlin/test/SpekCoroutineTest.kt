package test

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withContext
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object SubjectClassSpec : Spek({

    val mockDependency by memoized { mock<Foo>()}
    val subject by memoized { Bar(mockDependency) }

    describe("doing something") {
        runBlocking {
            subject.result(42)

            it("uses the dependency in a certain way") {
                verify(mockDependency).nonsuspending()
            }
        }
    }
})

interface Foo {
    fun nonsuspending(): Int
}

class Bar(val foo: Foo) {
    suspend fun result(r: Int) = withContext(CommonPool) { foo.nonsuspending() }
}

