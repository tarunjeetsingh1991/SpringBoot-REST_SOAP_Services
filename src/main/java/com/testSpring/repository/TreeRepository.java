package com.testSpring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.testSpring.model.TreeModel;

@Repository
public interface TreeRepository extends JpaRepository<TreeModel, Integer> 
{
	
}