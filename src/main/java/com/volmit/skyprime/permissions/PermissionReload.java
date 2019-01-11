package com.volmit.skyprime.permissions;

import com.volmit.phantom.api.command.PhantomPermission;

public class PermissionReload extends PhantomPermission
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
