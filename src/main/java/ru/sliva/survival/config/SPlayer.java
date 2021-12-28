package ru.sliva.survival.config;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class SPlayer implements ConfigurationSerializable {

    private final Map<String, Location> homes = new HashMap<>();

    /**
     * Deserialization constructor
     * @param map to deserialize
     */
    @SuppressWarnings("unchecked")
    public SPlayer(@NotNull Map<String, Object> map) {
        Map<String, Location> homesMap = (Map<String, Location>) map.get("homes");
        for(Map.Entry<String, Location> entry : homesMap.entrySet()) {
            String key = entry.getKey();
            if(key.equals(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) continue;
            homes.put(key, entry.getValue());
        }
    }

    public SPlayer(@NotNull HashMap<String, Location> map) {
        for(Map.Entry<String, Location> entry : map.entrySet()) {
            String key = entry.getKey();
            if(key.equals(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) continue;
            homes.put(key, entry.getValue());
        }
    }

    public SPlayer() {
        this(new HashMap<String, Location>());
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("homes", homes);
        return map;
    }

    public void setHome(@NotNull String name, @NotNull Location location) {
        homes.put(name, location);
    }

    public @Nullable Location getHome(@NotNull String name) {
        return homes.get(name);
    }

    public void delHome(@NotNull String name) {
        homes.remove(name);
    }
}
