package com.xonmin.spring.jpa.valuetype

import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ValueMain

fun main(args: Array<String>) {
    val a = 10
    val b = 10

    println("a == b " + (a == b))

    val address = Address("city", "street", "a101")
    val address2 = Address("city", "street", "a101")
    // java와 달리 코틀린에서는 동일성 비교에 대해서 === 을 사용한다  (동일성 vs 동등성)
    println("result2 =" + (address === address2))
    println("equals() result : " + (address == address2)) // kotlin equals = (==)
}
