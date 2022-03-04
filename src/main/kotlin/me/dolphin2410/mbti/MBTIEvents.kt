package me.dolphin2410.mbti

import com.destroystokyo.paper.entity.villager.Reputation
import com.destroystokyo.paper.entity.villager.ReputationType
import net.kyori.adventure.text.Component.text
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.entity.EntityBreedEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.TradeSelectEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.MerchantInventory
import org.bukkit.inventory.meta.ItemMeta
import java.util.Random

class MBTIEvents: Listener {
    val randomTool = arrayListOf<(ItemMeta, Random) -> Unit>(
        { it, random -> it.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, random.nextInt(3) + 1, false) },
        { it, random -> it.addEnchant(Enchantment.DIG_SPEED, random.nextInt(5) + 1, false) },
        { it, random -> it.addEnchant(Enchantment.DURABILITY, random.nextInt(3) + 1, false) },
        { it, random -> it.addEnchant(Enchantment.SILK_TOUCH, random.nextInt(3) + 1, false) },
        { it, _ -> it.addEnchant(Enchantment.MENDING, 1, false) }
    )

    val randomMelee = arrayListOf<(ItemMeta, Random) -> Unit>(
        { it, random -> it.addEnchant(Enchantment.LOOT_BONUS_MOBS, random.nextInt(3) + 1, false) },
        { it, random -> it.addEnchant(Enchantment.DAMAGE_ALL, random.nextInt(5) + 1, false) },
        { it, random -> it.addEnchant(Enchantment.FIRE_ASPECT, random.nextInt(2) + 1, false) },
        { it, random -> it.addEnchant(Enchantment.DURABILITY, random.nextInt(3) + 1, false) },
        { it, _ -> it.addEnchant(Enchantment.MENDING, 1, false) }
    )

    val randomBow = arrayListOf<(ItemMeta, Random) -> Unit>(
        { it, random -> it.addEnchant(Enchantment.ARROW_DAMAGE, random.nextInt(5) + 1, false) },
        { it, random -> it.addEnchant(Enchantment.ARROW_KNOCKBACK, random.nextInt(2) + 1, false) },
        { it, _ -> it.addEnchant(Enchantment.ARROW_FIRE, 1, false) },
        { it, random -> it.addEnchant(Enchantment.DURABILITY, random.nextInt(3) + 1, false) },
        { it, _ -> it.addEnchant(Enchantment.MENDING, 1, false) },
        { it, _ -> it.addEnchant(Enchantment.ARROW_INFINITE, 1, false) },
    )

    val randomCrossbow = arrayListOf<(ItemMeta, Random) -> Unit>(
        { it, random -> it.addEnchant(Enchantment.PIERCING, random.nextInt(3) + 1, false) },
        { it, _ -> it.addEnchant(Enchantment.MULTISHOT, 1, false) },
        { it, random -> it.addEnchant(Enchantment.DURABILITY, random.nextInt(3) + 1, false) },
        { it, _ -> it.addEnchant(Enchantment.MENDING, 1, false) },
    )

    val randomFishing = arrayListOf<(ItemMeta, Random) -> Unit>(
        { it, random -> it.addEnchant(Enchantment.LUCK, random.nextInt(3) + 1, false) },
        { it, random -> it.addEnchant(Enchantment.LURE, random.nextInt(3) + 1, false) },
        { it, random -> it.addEnchant(Enchantment.DURABILITY, random.nextInt(3) + 1, false) },
        { it, _ -> it.addEnchant(Enchantment.MENDING, 1, false) },
    )

    val randomTrident = arrayListOf<(ItemMeta, Random) -> Unit>(
        { it, random -> it.addEnchant(Enchantment.IMPALING, random.nextInt(5) + 1, false) },
        { it, random -> it.addEnchant(Enchantment.RIPTIDE, random.nextInt(3) + 1, false) },
        { it, _ -> it.addEnchant(Enchantment.CHANNELING, 1, false) },
        { it, random -> it.addEnchant(Enchantment.DURABILITY, random.nextInt(3) + 1, false) },
        { it, _ -> it.addEnchant(Enchantment.MENDING, 1, false) },
    )

    @EventHandler
    fun onEntityUse(e: PlayerInteractAtEntityEvent) {
        val entity = e.rightClicked
        if (entity is Villager) {
            if (MBTIPlugin.mbtiList.containsKey(e.player.uniqueId)) {
                if (MBTIPlugin.mbtiList[e.player.uniqueId]?.isT() == false) {
                    entity.setReputation(e.player.uniqueId, Reputation(mapOf(ReputationType.MAJOR_POSITIVE to 1)))
                }
            }
        }
    }

    @EventHandler
    fun onBreed(e: EntityBreedEvent) {
        MBTIPlugin.challenges[e.breeder?.uniqueId]?.let { challenge ->
            if (challenge is JChallenge) {
                challenge.feedAnimals()
            }
        }
    }

    @EventHandler
    fun onKill(e: EntityDeathEvent) {
        MBTIPlugin.challenges[e.entity.killer?.uniqueId]?.let { challenge ->
            if (challenge is JChallenge) {
                challenge.kill()
            }
        }
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent) {
        MBTIPlugin.collectives[e.player.uniqueId]?.let { list ->
            if (!list.contains(e.block.type)) {
                list.add(e.block.type)
                if (list.size % 10 == 0) {
                    e.player.sendMessage(text("You have collected ${list.size} Blocks!"))
                    e.player.giveExp(50)
                }
            }
        }
        when (e.block.type) {
            Material.COPPER_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.COAL_ORE, Material.DIAMOND_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE,
            Material.DEEPSLATE_COPPER_ORE, Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.ANCIENT_DEBRIS, Material.NETHER_GOLD_ORE, Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE, Material.NETHER_QUARTZ_ORE -> {
                MBTIPlugin.challenges[e.player.uniqueId]?.let { challenge ->
                    if (challenge is JChallenge) {
                        challenge.mine()
                    }
                }
            }
            Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.NETHER_WART -> {
                if ((e.block.blockData as Ageable).run { age == maximumAge }) {
                    MBTIPlugin.challenges[e.player.uniqueId]?.let { challenge ->
                        if (challenge is JChallenge) {
                            challenge.harvest()
                        }
                    }
                }
            }
            else -> {}
        }
    }

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        MBTIPlugin.challenges[e.player.uniqueId]?.let { challenge ->
            if (challenge is PChallenge) {
                challenge.walk(e.to.subtract(e.from).length())
            }
        }
    }

    @EventHandler
    fun villagerUpgrade(e: InventoryClickEvent) {
        if (((e.inventory as MerchantInventory).merchant as Villager).villagerLevel != 1) {
            MBTIPlugin.mbtiList[e.whoClicked.uniqueId]?.let { mbti ->
                if (!mbti.isN()) {
                    e.whoClicked.sendMessage(text("You upgraded the villager! "))
                    (e.whoClicked as Player).giveExp(50)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerEnchant(e: EnchantItemEvent) {
        val random = Random()
        if (MBTIPlugin.mbtiList.containsKey(e.enchanter.uniqueId)) {
            if (MBTIPlugin.mbtiList[e.enchanter.uniqueId]?.isT() == true) {
                if (random.nextInt(100) < 8) {
                    when (e.item.type) {
                        Material.FISHING_ROD -> {
                            e.item.editMeta {
                                randomFishing[random.nextInt(randomFishing.size)](it, random)
                            }
                        }
                        Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_SHOVEL, Material.WOODEN_PICKAXE,
                        Material.STONE_AXE, Material.STONE_HOE, Material.STONE_SHOVEL, Material.STONE_PICKAXE,
                        Material.IRON_AXE, Material.IRON_HOE, Material.IRON_SHOVEL, Material.IRON_PICKAXE,
                        Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_SHOVEL, Material.GOLDEN_PICKAXE,
                        Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_SHOVEL, Material.DIAMOND_PICKAXE,
                        Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_SHOVEL, Material.NETHERITE_PICKAXE -> {
                            e.item.editMeta {
                                randomTool[random.nextInt(randomTool.size)](it, random)
                            }
                        }
                        Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD -> {
                            e.item.editMeta {
                                randomMelee[random.nextInt(randomMelee.size)](it, random)
                            }
                        }
                        Material.BOW -> {
                            e.item.editMeta {
                                randomBow[random.nextInt(randomBow.size)](it, random)
                            }
                        }
                        Material.CROSSBOW -> {
                            e.item.editMeta {
                                randomCrossbow[random.nextInt(randomCrossbow.size)](it, random)
                            }
                        }
                        Material.TRIDENT -> {
                            e.item.editMeta {
                                randomTrident[random.nextInt(randomTrident.size)](it, random)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}