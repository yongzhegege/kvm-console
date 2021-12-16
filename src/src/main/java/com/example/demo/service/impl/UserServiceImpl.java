package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.empty.User;
import com.example.demo.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDao userDao;

	@Override
	public User getUserByUserId(Integer userId) {
		return userDao.load(userId);
	}

	@Override
	public User getUserByUserName(String userName) {
		User user = new User();
		user.setUserName(userName);
		List<User> allSelectLists = userDao.allSelectLists(user);
		if(allSelectLists.size() > 0) {
			return allSelectLists.get(0);
		}else {
			return null;
		}
	}

	@Override
	public void addUser(User user) {
		userDao.insert(user);
	}

	@Override
	public void deleteUser(Integer userId) {
		userDao.delete(userId);
	}

	@Override
	public void updateUser(User user) {
		userDao.update(user);
	}

}
