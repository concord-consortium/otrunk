package org.concord.otrunk;

import java.util.Vector;

import org.concord.framework.otrunk.OTUser;

public class OTUserListService {
	private Vector userList = new Vector();
	
	public OTUserListService() {
		
	}
	
	public OTUserListService(Vector userList) {
		setUserList(userList);
	}
	
	public void setUserList(Vector userList) {
		this.userList = userList;
	}
	
	public Vector getUserList() {
		return userList;
	}
	
	public void addUser(OTUser user) {
		userList.add(user);
	}
}
