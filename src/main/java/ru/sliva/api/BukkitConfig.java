package ru.sliva.api;

import com.google.common.base.Charsets;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Deprecated
public class BukkitConfig extends YamlConfiguration {

    private final File file;
    private final Plugin plugin;

    public BukkitConfig(@NotNull Plugin plugin, @NotNull String filename) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), filename);
        reloadConfig();
    }

    @SuppressWarnings("all")
    public void reloadConfig() {
        try {
            if(!file.exists()) {
                if(!saveDefaultConfig()) {
                    file.createNewFile();
                }
            }

            load(file);

            final InputStream defConfigStream = plugin.getResource(file.getName());

            if (defConfigStream != null) {
                setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean saveDefaultConfig() {
        if(plugin.getResource(file.getName()) != null) {
            plugin.saveResource(file.getName(), true);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull String getString(@NotNull String path, @Nullable String def) {
        String result = super.getString(path, def);
        if(result == null) {
            result = path;
        }
        return result.replace("/n", "\n");
    }

    @Override
    public @NotNull String getString(@NotNull String path) {
        String result = super.getString(path);
        if(result == null) {
            result = path;
        }
        return result.replace("/n", "\n");
    }

    public @NotNull String getColorizedString(@NotNull String path) {
        return ChatColor.translateAlternateColorCodes('&', getString(path));
    }

    public @NotNull List<String> getColorizedList(@NotNull String path) {
        List<String> list = getStringList(path);
        for(int i = 0; i < list.size(); i++) {
            list.set(i, ChatColor.translateAlternateColorCodes('&', list.get(i)));
        }
        return list;
    }
}