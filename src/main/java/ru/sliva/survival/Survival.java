package ru.sliva.survival;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.sliva.api.API;
import ru.sliva.api.command.Commands;
import ru.sliva.survival.command.SlezhkaCommand;
import ru.sliva.survival.command.SudoCommand;
import ru.sliva.survival.command.TellCommand;
import ru.sliva.survival.config.PluginConfig;

public final class Survival extends JavaPlugin {

	private PluginConfig config;

	private static Survival instance;
	
	@Override
	public void onEnable() {
		instance = this;

		API.setup(this);

		getDataFolder().mkdirs();
		
		config = new PluginConfig(this);

		new PlayerListener();
		new TabList();
		new Sidebar();

		Commands.registerCommand(new TellCommand(this));
		Commands.registerCommand(new SlezhkaCommand(this));
		Commands.registerCommand(new SudoCommand(this));
	}

	@Override
	public void onDisable() {
		Commands.unregisterCommands();
	}

	@NotNull
	public PluginConfig getPluginConfig() {
		return config;
	}

	public static Survival getInstance() {
		return instance;
	}
}