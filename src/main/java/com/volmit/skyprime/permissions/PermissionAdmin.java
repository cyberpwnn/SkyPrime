package com.volmit.skyprime.permissions;

import com.volmit.phantom.plugin.PhantomPermission;
import com.volmit.phantom.plugin.Scaffold.Permission;

public class PermissionAdmin extends PhantomPermission
{
	@Permission
	public static PermissionReload reload;

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
