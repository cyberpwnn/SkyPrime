package io.shadowrealm.skyprime.permissions;

import mortar.bukkit.command.MortarPermission;
import mortar.bukkit.command.Permission;

public class PermissionSky extends MortarPermission
{
	@Permission
	public PermissionAdmin admin;

	@Override
	public String getNode()
	{
		return "sky";
	}

	@Override
	public String getDescription()
	{
		return "Gives access to all of skyprime";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
