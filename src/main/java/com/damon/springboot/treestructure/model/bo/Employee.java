package com.damon.springboot.treestructure.model.bo;

import com.damon.springboot.treestructure.util.SnowFlakeIdWorker;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "t_emp")
public class Employee {

	@Id
	@Column
	private Long id;

	@Column
	private String name;

	@Column
	private String department;

	@Column
	private LocalDateTime dob;

	@Column
	private String gender;

	@Override
	public String toString() {
		return "Employee [id=	" + id + ", name=" + name + ", department=" + department + ", dob=" + dob + ", gender="
				+ gender + "]";
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public LocalDateTime getDob() {
		return dob;
	}

	public void setDob(LocalDateTime dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@PrePersist
	protected void prePersist() {
		if(this.getId()==null) {

			Long newId = SnowFlakeIdWorker.generateId();
			this.setId(newId);
		}

	}

}
