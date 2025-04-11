package com.example.pmt_backend.dto;

import com.example.pmt_backend.models.Role;

public class AddMemberRequest {
	
	private String email;
	
    private Role role;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	} 
    
}