package io.shadowrealm.skyprime.permissions;

import mortar.bukkit.command.MortarPermission;

public class PermissionReload extends MortarPermission
{
	@Override
	public String getNode()
	{
		return "reload";
	}

	@Override
	public String getDescription()
	{
		return "Gives access to reload skyprime.";
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}
}
