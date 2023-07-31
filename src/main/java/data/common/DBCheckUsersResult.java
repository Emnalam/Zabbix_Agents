package data.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="servicedbusercheckresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class DBCheckUsersResult extends Result {

	public DBCheckUsersResult() throws Exception {
		super();
	}
	
	@XmlElement(required=true) 
	protected int warnings = 0;
	
	@XmlElement(required=true) 
    protected int errors = 0;
	
	@XmlElement(required=true) 
	protected DBUsersStatus users = new DBUsersStatus();
	
    
    /** 
     * @param user
     * @return int
     */
    public int  getWarnings()             { return warnings; }
    
    /** 
     * @param user
     */
    public void setWarnings(int warnings) { this.warnings = warnings; }

    
    /** 
     * @param user
     * @return int
     */
    public int  getErrors()           { return errors; }
    
    /** 
     * @param user
     */
    public void setErrors(int errors) { this.errors = errors; }
    
	
  /** 
   * @param user
   * @return DBUsersStatus
   */
  public DBUsersStatus getUsers()                    { return users; }
	
  /** 
   * @param user
   */
  public void          setUsers(DBUsersStatus users) { this.users = users; }
	
	
  /** 
   * @param user
   */
  public void addUser(DBUserStatus user) { 
		this.users.addUser(user); 
	}

}
