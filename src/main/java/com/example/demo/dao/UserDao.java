package com.example.demo.dao;

import java.util.List;

import com.example.demo.empty.User;

public interface UserDao {
	
	public User load(Integer userId);
	
	public List<User> allSelectLists(User user);
	
	public void insert(User user);
	
	public void update(User user);
	
	public void delete(Integer userId);


}
