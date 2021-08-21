package me.hsgamer.paperenchantmentcore.enchantment;

import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A translatable {@link CustomEnchantment}, which is helpful for resource packs
 */
public abstract class TranslatableCustomEnchantment extends CustomEnchantment {
    /**
     * Create a new enchantment
     *
     * @param plugin the plugin
     * @param key    the enchantment key
     */
    protected TranslatableCustomEnchantment(Plugin plugin, String key) {
        super(plugin, key);
    }

    @Override
    public @NotNull Component displayName() {
        return Component.translatable(translationKey());
    }
}
