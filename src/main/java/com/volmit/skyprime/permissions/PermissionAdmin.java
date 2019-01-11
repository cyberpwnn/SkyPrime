package com.volmit.skyprime.permissions;

import com.volmit.phantom.api.command.PhantomPermission;
import com.volmit.phantom.api.module.Permission;

public class PermissionAdmin extends PhantomPermission
{
	@Permission
	public PermissionReload reload;

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
