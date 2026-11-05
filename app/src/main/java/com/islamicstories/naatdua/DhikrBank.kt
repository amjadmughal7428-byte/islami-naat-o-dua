package com.islamicstories.naatdua

data class Dhikr(val latin: String, val color: Int)

object DhikrBank {
    val list = listOf(
        Dhikr("SubhanAllah", 0xFF0B8A6E.toInt()),
        Dhikr("Alhamdulillah", 0xFF2E9E44.toInt()),
        Dhikr("Allahu Akbar", 0xFFE8930C.toInt()),
        Dhikr("Astaghfirullah", 0xFF7C5CFC.toInt()),
        Dhikr("La ilaha illallah", 0xFF2D9CDB.toInt()),
        Dhikr("SubhanAllahi wa bihamdihi", 0xFFE05A7A.toInt())
    )
}
