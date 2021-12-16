package com.example.demo.service;

import com.example.demo.empty.User;

public interface UserService {
	
	public User getUserByUserId(Integer userId);
	
	public User getUserByUserName(String userName);
	
	public void addUser(User user);
	
	public void deleteUser(Integer userId);
	
	public void updateUser(User user);

}
