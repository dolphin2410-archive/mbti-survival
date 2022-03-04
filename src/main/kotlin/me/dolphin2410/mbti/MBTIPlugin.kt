package me.dolphin2410.mbti

import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.EnumSet
import java.util.UUID

class MBTIPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: MBTIPlugin
            private set

        val mbtiList = HashMap<UUID, MBTI>()

        val challenges = HashMap<UUID, MBTIChallenge>()

        val collectives = HashMap<UUID, ArrayList<Material>>()
    }

    override fun onEnable() {
        instance = this
        Bukkit.getServer().pluginManager.registerEvents(MBTIEvents(), this)
        kommand {
            register("mbti") {
                requires { isPlayer && isOp }
                then("select") {
                    then("mbti" to dynamicByEnum(EnumSet.allOf(MBTI::class.java))) {
                        executes {
                            val mbti: MBTI by it
                            mbtiList[player.uniqueId] = mbti
                        }
                    }
                }

                then("stat") {
                    executes {
                        val challenge = challenges[player.uniqueId]
                        if (challenge != null) {
                            challenge.printOverall()
                        } else {
                            player.sendMessage(text("Please try refreshing"))
                        }
                    }
                }

                then("refresh") {
                    executes {
                        challenges.clear()
                        mbtiList.forEach { entry ->
                            val currentPlayer = Bukkit.getPlayer(entry.key) ?: return@forEach
                            currentPlayer.removePotionEffect(PotionEffectType.SPEED)
                            currentPlayer.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
                            if (entry.value.isI()) {
                                currentPlayer.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Int.MAX_VALUE, 1, false, false))
                            } else {
                                currentPlayer.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 1, false, false))
                            }

                            if (entry.value.isJ()) {
                                challenges[currentPlayer.uniqueId] = JChallenge(player, { name ->
                                    when (name) {
                                        "animals" -> {
                                            player.giveExp(50)
                                            player.sendMessage(text("You successfully bred ${JChallenge.MAX_ANIMALS} animals"))
                                        }
                                        "harvest" -> {
                                            player.giveExp(50)
                                            player.sendMessage(text("You successfully harvested ${JChallenge.MAX_HARVEST} crops"))
                                        }
                                        "mine" -> {
                                            player.giveExp(50)
                                            player.sendMessage(text("You successfully mined ${JChallenge.MAX_MINE} ores"))
                                        }
                                        "kill" -> {
                                            player.giveExp(50)
                                            player.sendMessage(text("You successfully killed ${JChallenge.MAX_KILL} mobs"))
                                        }
                                    }
                                }) {
                                    player.giveExp(200)
                                }
                            } else {
                                challenges[currentPlayer.uniqueId] = PChallenge(player, { name ->
                                    when (name) {
                                        "level1" -> {
                                            player.giveExp(75)
                                            player.sendMessage(text("You successfully walked ${PChallenge.MAX_WALK} blocks"))
                                        }
                                        "level2" -> {
                                            player.giveExp(125)
                                            player.sendMessage(text("You successfully walked ${PChallenge.MAX_WALK_LEVEL_2} blocks"))
                                        }
                                    }
                                }) {
                                    player.giveExp(200)
                                }
                            }

                            if (entry.value.isN()) {
                                collectives[player.uniqueId] = ArrayList()
                            }
                        }
                    }
                }
            }
        }
    }
}