package io.shadowrealm.skyprime.permissions;

import io.shadowrealm.skyprime.Config;
import io.shadowrealm.skyprime.dependencies.Vault;
import mortar.bukkit.command.MortarPermission;
import mortar.bukkit.command.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;

public class PermissionMembers extends MortarPermission
{
	@Override
	public String getNode()
	{
		return "members";
	}

	@Override
	public String getDescription()
	{
		return "Gives access to all of skyprime";
	}

	@Permission
	public PermissionMembersAdd add;

	@Override
	public boolean isDefault()
	{
		return false;
	}

	public int getSize(OfflinePlayer player)
	{
		int s = Config.MAX_MEMBERS.getOrDefault("default", 0);
		for (Map.Entry<String, Integer> m : Config.MAX_MEMBERS.entrySet()) {
			if (m.getValue() > s && Vault.getPermission().playerHas(null, player, this.getFullNode() + "." + m.getKey())) {
				s = m.getValue();
			}
		}
		return s;
	}
}
