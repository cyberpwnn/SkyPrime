package io.shadowrealm.skyprime.permissions;

import mortar.bukkit.command.MortarPermission;
import mortar.bukkit.command.Permission;

public class PermissionBypass extends MortarPermission
{
	@Override
	public String getNode()
	{
		return "bypass";
	}

	@Override
	public String getDescription()
	{
		return "Gives complete island protection bypass.";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
