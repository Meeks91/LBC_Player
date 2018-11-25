package com.heavymagikhq.lbc_player_ultimate

import org.junit.Test

import org.junit.Assert.*

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
     fun addition_isCorrect(): Unit {
            assertEquals(4, (2 + 2).toLong())

//            val show = LbcShowRetrieverService
//                    .getShowFor(LbcShowPresenter.JAMES_O_BRIEN, Calendar.getInstance().apply { set(2018, 10, 16) }.time)?.show?.fileName
//

    }

    @Test
    @Throws(Exception::class)
    fun testNull(): Unit {
        assertEquals(4, (2 + 2).toLong())

        val t: String? = null
        println("test")
        try {
            val t2: String = t!!
            println("succcess")
        } catch (e: Throwable) {
            println("failure")
            println(e)
        }
    }

}