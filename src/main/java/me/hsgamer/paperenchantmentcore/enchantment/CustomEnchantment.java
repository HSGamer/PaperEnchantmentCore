package me.hsgamer.paperenchantmentcore.enchantment;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A custom enchantment.
 * Should have a Plugin constructor.
 * Example:
 * <pre>
 *     public class TestEnchantment extends CustomEnchantment {
 *         public TestEnchantment(Plugin plugin) {
 *             super(plugin, "test");
 *         }
 *     }
 * </pre>
 */
public abstract class CustomEnchantment extends Enchantment {
    /**
     * Create a new enchantment
     *
     * @param plugin the plugin
     * @param key    the enchantment key
     */
    protected CustomEnchantment(Plugin plugin, String key) {
        super(new NamespacedKey(plugin, key));
    }

    /**
     * Get the display name of the enchantment
     *
     * @return the component of the display name
     */
    public abstract @NotNull Component displayName();

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull Component displayName(int level) {
        Component component = displayName();
        if (level != 1 || this.getMaxLevel() != 1) {
            component = component.append(Component.space()).append(Component.translatable("enchantment.level." + level));
        }
        component = component.color(this.isCursed() ? NamedTextColor.RED : NamedTextColor.GRAY);
        return component;
    }

    /**
     * Get the translation key. Default is <code>enchantment.custom.%_plugin_%.%_key_%</code>
     *
     * @return the translation key
     */
    @Override
    public @NotNull String translationKey() {
        NamespacedKey namespacedKey = this.getKey();
        return "enchantment.custom." + namespacedKey.getNamespace() + "." + namespacedKey.getKey();
    }
}
