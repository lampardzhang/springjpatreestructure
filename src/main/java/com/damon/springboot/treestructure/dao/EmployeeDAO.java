package com.damon.springboot.treestructure.dao;

import java.util.List;

import com.damon.springboot.treestructure.model.bo.Employee;

public interface EmployeeDAO {
	
	List<Employee> get();
	
	Employee get(int id);
	
	void save(Employee employee);
	
	void delete(int id);
	

}
