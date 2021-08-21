package me.hsgamer.paperenchantmentcore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
    public static final Component MAGIC_SUFFIX = Component.space().color(TextColor.color(0xfa02ff)).append(net.kyori.adventure.text.Component.space().color(TextColor.color(0x26b8ff)));

    protected CustomEnchantment(Plugin plugin, String key) {
        super(new NamespacedKey(plugin, key));
    }

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
}
