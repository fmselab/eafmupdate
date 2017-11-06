package eafmupdate.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Roles {
	public List<Role> roles;
	
	public Roles(List<Role> roles) {
		this.roles = roles;
	}
	
	public Roles(Role role) {
		roles = new ArrayList<>();
		roles.add(role);
	}
	
	public Roles() {
		this.roles = any();
	}
	
	/** @return a list with all the roles.
	 * All the roles are admitted */
	private static List<Role> any() {
		ArrayList<Role> roles = new ArrayList<Role>();
		for (Role r : Role.values()) roles.add(r);
		return roles;
	}
	
	/** @return if the list of roles contains a certain role */
	public boolean containsRole(Role role) {
		return roles==null || roles.contains(role);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(roles.toArray());
	}
}
