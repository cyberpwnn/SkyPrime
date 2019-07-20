package io.shadowrealm.skyprime.permissions;

import mortar.bukkit.command.MortarPermission;

public class PermissionMembersAdd extends MortarPermission
{
	@Override
	public String getNode()
	{
		return "add";
	}

	@Override
	public String getDescription()
	{
		return "Forcefully adds players to an island";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
