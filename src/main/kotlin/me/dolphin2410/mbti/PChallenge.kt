package me.dolphin2410.mbti

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import kotlin.math.round

class PChallenge(override var player: Player, var finish: (String) -> Unit, var then: PChallenge.() -> Unit): MBTIChallenge {
    companion object {
        const val MAX_WALK = 1500
        const val MAX_WALK_LEVEL_2 = 4000
    }
    var walk = 0.0

    fun walk(amount: Double) {
        val prev = walk
        walk += amount
        preUpdateStatus("walk", prev)
    }

    override var isValid = true
    override fun updateStatus(name: String) {
        if (walk >= MAX_WALK_LEVEL_2 && isValid) {
            isValid = false
            then()
        }
    }

    fun preUpdateStatus(name: String, prev: Double) {
        if ((prev < MAX_WALK && walk >= MAX_WALK) || (prev < MAX_WALK_LEVEL_2 && walk >= MAX_WALK_LEVEL_2)) {
            finish(if (prev < MAX_WALK) "level1" else "level2")
        }
        updateStatus(name)
    }

    override fun printOverall() {
        player.sendMessage(text().decorate(TextDecoration.BOLD).content("*** Your Challenge ***").build())
        player.sendMessage(text("Walked: [${round(walk * 100) / 100}/$MAX_WALK] ${if (walk >= MAX_WALK) "${ChatColor.YELLOW}DONE" else ""}"))
        player.sendMessage(text("Walked Level 2: [${round(walk * 100) / 100}/$MAX_WALK_LEVEL_2] ${if (walk >= MAX_WALK_LEVEL_2) "${ChatColor.YELLOW}DONE" else ""}"))
        player.sendMessage(text().decorate(TextDecoration.BOLD).content("**********************").build())
    }
}