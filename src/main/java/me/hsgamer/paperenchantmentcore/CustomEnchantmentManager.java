package me.hsgamer.paperenchantmentcore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomEnchantmentManager {
    private static final Field BY_KEY;
    private static final Field BY_NAME;

    static {
        Field acceptingNew;
        Field keyField;
        Field nameField;

        try {
            acceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
            acceptingNew.setAccessible(true);
            acceptingNew.set(null, true);
            keyField = Enchantment.class.getDeclaredField("byKey");
            keyField.setAccessible(true);
            nameField = Enchantment.class.getDeclaredField("byName");
            nameField.setAccessible(true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        BY_KEY = keyField;
        BY_NAME = nameField;
    }

    private final Plugin plugin;
    private final List<CustomEnchantment> registeredEnchantments = new ArrayList<>();

    public CustomEnchantmentManager(Plugin plugin) {
        this.plugin = plugin;
    }

    private static void removeFromEnchantMap(Enchantment enchantment) {
        try {
            // noinspection unchecked
            Map<NamespacedKey, Enchantment> byKey = (Map<NamespacedKey, Enchantment>) BY_KEY.get(null);
            // noinspection unchecked
            Map<String, Enchantment> byName = (Map<String, Enchantment>) BY_NAME.get(null);

            byKey.entrySet().removeIf(entry -> entry.getValue().equals(enchantment));
            byName.entrySet().removeIf(entry -> entry.getValue().equals(enchantment));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void addToEnchantMap(Enchantment enchantment) {
        Enchantment.registerEnchantment(enchantment);
    }

    public void register(Class<CustomEnchantment> enchantmentClass) {
        CustomEnchantment customEnchantment;
        try {
            Constructor<CustomEnchantment> constructor = enchantmentClass.getDeclaredConstructor(Plugin.class);
            customEnchantment = constructor.newInstance(this.plugin);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("The enchantment class does not have a (Plugin) constructor");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Cannot create instance for " + enchantmentClass.getName(), e);
        }
        this.registeredEnchantments.add(customEnchantment);
        addToEnchantMap(customEnchantment);
    }

    public void unregister(Class<CustomEnchantment> enchantmentClass) {
        List<CustomEnchantment> toUnregister = registeredEnchantments.stream()
                .filter(enchantment -> enchantment.getClass().equals(enchantmentClass))
                .collect(Collectors.toList());
        toUnregister.forEach(CustomEnchantmentManager::removeFromEnchantMap);
        registeredEnchantments.removeIf(toUnregister::contains);
    }

    public void unregisterAll() {
        registeredEnchantments.forEach(CustomEnchantmentManager::removeFromEnchantMap);
        registeredEnchantments.clear();
    }

    public void updateItem(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        Component suffix = CustomEnchantment.MAGIC_SUFFIX;
        List<Component> lore = Optional.ofNullable(itemMeta.lore()).orElseGet(ArrayList::new);
        lore.removeIf(c -> c.contains(suffix, Component.EQUALS));
        if (!itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
            Map<Enchantment, Integer> enchantmentMap;
            if (itemMeta instanceof EnchantmentStorageMeta storageMeta) {
                enchantmentMap = storageMeta.getStoredEnchants();
            } else {
                enchantmentMap = itemMeta.getEnchants();
            }
            enchantmentMap.forEach((enchantment, level) -> {
                if (enchantment instanceof CustomEnchantment customEnchantment) {
                    Component component = customEnchantment.displayName(level).decoration(TextDecoration.ITALIC, false).append(suffix);
                    lore.add(0, component);
                }
            });
        }
        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    public List<CustomEnchantment> getRegisteredEnchantments() {
        return registeredEnchantments;
    }
}
