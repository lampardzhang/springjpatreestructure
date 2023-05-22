package com.damon.springboot.treestructure.service;

import java.util.List;

import com.damon.springboot.treestructure.model.bo.Employee;

public interface EmployeeService {
	

	List<Employee> get();
	
	Employee get(int id);
	
	void save(Employee employee);
	
	void delete(int id);
	

}
