package io.shadowrealm.skyprime.permissions;

import mortar.bukkit.command.MortarPermission;
import mortar.bukkit.command.Permission;

public class PermissionAdmin extends MortarPermission
{
	@Permission
	public PermissionReload reload;

	@Permission
	public PermissionBypass bypass;

	@Override
	public String getNode()
	{
		return "admin";
	}

	@Override
	public String getDescription()
	{
		return "Gives access admin commands of skyprime.";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
