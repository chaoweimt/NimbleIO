package com.gifisan.nio.server.configuration;

import java.util.ArrayList;
import java.util.List;

import com.gifisan.nio.component.Configuration;

public class PermissionConfiguration {

	private List<Configuration>	roles		= new ArrayList<Configuration>();
	private List<Configuration>	permissions	= new ArrayList<Configuration>();

	public List<Configuration> getRoles() {
		return roles;
	}

	protected void addRole(Configuration role) {
		this.roles.add(role);
	}

	public List<Configuration> getPermissions() {
		return permissions;
	}

	protected void addPermission(Configuration permission) {
		this.permissions.add(permission);
	}

}
