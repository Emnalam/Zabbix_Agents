package data.common;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "users")
@XmlAccessorType (XmlAccessType.FIELD)
public class DBUsersStatus {
	@XmlElement(required=true, name="user") 
	List<DBUserStatus> users = new ArrayList<DBUserStatus>();

	
  /** 
   * @return List<DBUserStatus>
   */
  public List<DBUserStatus> getUsers() {
		return users;
	}

	
  /** 
   * @param users
   */
  public void setUsers(List<DBUserStatus> users) {
		this.users = users;
	}
	
  /** 
   * @param user
   */
  public void addUser(DBUserStatus user)
	{
		if (users == null)
		{
			users = new ArrayList<DBUserStatus>();
		}
		users.add(user);
	}
}
