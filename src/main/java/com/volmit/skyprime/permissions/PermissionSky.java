package com.volmit.skyprime.permissions;

import com.volmit.phantom.api.command.PhantomPermission;
import com.volmit.phantom.api.module.Permission;

public class PermissionSky extends PhantomPermission
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
