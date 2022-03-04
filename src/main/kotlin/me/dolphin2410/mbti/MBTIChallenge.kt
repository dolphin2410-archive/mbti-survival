package me.dolphin2410.mbti

import org.bukkit.entity.Player

interface MBTIChallenge {
    var isValid: Boolean
    val player: Player

    fun updateStatus(name: String)

    fun printOverall()
}