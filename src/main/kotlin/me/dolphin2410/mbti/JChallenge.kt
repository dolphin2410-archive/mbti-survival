package me.dolphin2410.mbti

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class JChallenge(override val player: Player, var finish: (String) -> Unit, var then: JChallenge.() -> Unit): MBTIChallenge {
    companion object {
        const val MAX_ANIMALS = 5
        const val MAX_HARVEST = 20
        const val MAX_MINE = 10
        const val MAX_KILL = 10
    }
    override var isValid = true
    var animals = 0
    var harvest = 0
    var mine = 0
    var kill = 0

    fun feedAnimals() {
        animals++
        updateStatus("animals")
    }

    fun harvest() {
        harvest++
        updateStatus("harvest")
    }

    fun mine() {
        mine++
        updateStatus("mine")
    }

    fun kill() {
        kill++
        updateStatus("kill")
    }

    override fun updateStatus(name: String) {
        if ((kill == MAX_KILL && name == "kill") || (mine == MAX_MINE && name == "mine") || (animals == MAX_ANIMALS && name == "animals") || (harvest == MAX_HARVEST && name == "harvest")) {
            finish(name)
        }

        if (kill >= MAX_KILL && mine >= MAX_MINE && animals >= MAX_ANIMALS && harvest >= MAX_HARVEST && isValid) {
            isValid = false
            then()
        }
    }

    override fun printOverall() {
        player.sendMessage(Component.text().decorate(TextDecoration.BOLD).content("*** Your Challenge ***").build())
        player.sendMessage(Component.text("Animals Bred: [$animals/${MAX_ANIMALS}] ${if (animals >= MAX_ANIMALS) "${ChatColor.YELLOW}DONE" else ""}"))
        player.sendMessage(Component.text("Crops Harvested: [$harvest/${MAX_HARVEST}] ${if (harvest >= MAX_HARVEST) "${ChatColor.YELLOW}DONE" else ""}"))
        player.sendMessage(Component.text("Blocks Mined: [$mine/${MAX_MINE}] ${if (mine >= MAX_MINE) "${ChatColor.YELLOW}DONE" else ""}"))
        player.sendMessage(Component.text("Entities Killed: [$kill/${MAX_KILL}] ${if (kill >= MAX_KILL) "${ChatColor.YELLOW}DONE" else ""}"))
        player.sendMessage(Component.text().decorate(TextDecoration.BOLD).content("**********************").build())
    }
}