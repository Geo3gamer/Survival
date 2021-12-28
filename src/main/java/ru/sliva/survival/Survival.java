package ru.sliva.survival;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.sliva.survival.api.Commands;
import ru.sliva.survival.config.PlayersConfig;
import ru.sliva.survival.config.PluginConfig;

public class Survival extends JavaPlugin {

	private PlayersConfig playersConfig;
	private PluginConfig config;

	public LuckPerms luckPerms;

	public static Survival instance;
	
	@Override
	public void onEnable() {
		instance = this;

		RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if(provider != null) {
			luckPerms = provider.getProvider();
		}
		
		config = new PluginConfig(this);
		playersConfig = new PlayersConfig(this);

		new PlayerListener(this);
		new TabList(this);
		new Sidebar();
	}

	public static Survival getInstance() {
		return instance;
	}

	@Override
	public void onDisable() {
		Commands.unregisterCommands(this);
	}

	@NotNull
	@Override
	public PluginConfig getConfig() {
		return config;
	}

	public PlayersConfig getPlayersConfig() {
		return playersConfig;
	}
}