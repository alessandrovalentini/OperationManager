package entities;

import java.util.HashSet;
import java.util.Set;

public class User {
    private String username;
    private String password;
    private String persmissions;
    private String email;
    private Boolean delete;
    private Set<Intervention> interventionses = new HashSet<Intervention>(0);
    
    
	public String getUsername() {		
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPersmissions() {
		return persmissions;
	}
	public void setPersmissions(String persmissions) {
		this.persmissions = persmissions;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Boolean getDelete() {
		return delete;
	}
	public void setDelete(Boolean delete) {
		this.delete = delete;
	}
	public Set<Intervention> getInterventionses() {
		return interventionses;
	}
	public void setInterventionses(Set<Intervention> interventionses) {
		this.interventionses = interventionses;
	}
    
    
}
