package com.testSpring.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "trees")
public class TreeModel 
{
	@Id
	int id;
	String name,category;
	
	@ElementCollection
    @CollectionTable(name = "tree_branches", joinColumns = @JoinColumn(name = "tree_id"))
    @Column(name = "branch")
	List<String> branches = new ArrayList<>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<String> getBranches() {
		return branches;
	}

	public void setBranches(List<String> branches) {
		this.branches = branches;
	}

	public TreeModel(int id, String name, String category, List<String> branches) {
		super();
		this.id = id;
		this.name = name;
		this.category = category;
		this.branches = branches;
	}

	public TreeModel() {
		super();
	}

	@Override
	public String toString() {
		return "TreeModel [id=" + id + ", name=" + name + ", category=" + category + ", branches=" + branches + "]";
	}
	
	
}
