package com.willfp.ecoskills.effects.effects

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.inventory.EnchantingInventory
import org.bukkit.inventory.ItemStack


class EffectOverCompensation : Effect(
        "over_compensation"
) {
    override fun formatDescription(string: String, level: Int): String {
        return string.replace("%chance%", NumberUtils.format(config.getDouble("chance-per-level") * level))
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: EnchantItemEvent) {
        val player = event.enchanter
        val cost = event.whichButton()+1

        val chance = config.getDouble("chance-per-level") * player.getEffectLevel(this)

        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
            Bukkit.getLogger().info("?")
            Bukkit.getLogger().info(NumberUtils.randFloat(0.0, 100.0).toString())
            Bukkit.getLogger().info(chance.toString())


//            event.isCancelled = true
                this.plugin.scheduler.runLater({
                    event.inventory.addItem(ItemStack(Material.LAPIS_LAZULI, cost))
                }, 2)
            }
    }
}