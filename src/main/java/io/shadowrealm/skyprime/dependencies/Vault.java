package io.shadowrealm.skyprime.dependencies;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;

public class Vault
{

	public static Permission getPermission()
	{
		return Bukkit.getServer().getServicesManager().getRegistration(Permission.class).getProvider();
	}

	public static Economy getEconomy()
	{
		return Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
	}

}
