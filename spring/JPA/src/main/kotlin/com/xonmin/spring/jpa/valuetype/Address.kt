package com.xonmin.spring.jpa.valuetype

data class Address(
    val city: String,
    val street: String,
    val zipCode: String
) {
    // 추후에 proxy 를 사용하게 된다면, getter가 필요한 경우가 있다.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Address

        if (city != other.city) return false
        if (street != other.street) return false
        if (zipCode != other.zipCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = city.hashCode()
        result = 31 * result + street.hashCode()
        result = 31 * result + zipCode.hashCode()
        return result
    }
}
