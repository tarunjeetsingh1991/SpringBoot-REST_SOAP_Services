package com.testSpring.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.testSpring.exception.TreeAlreadyExistsException;
import com.testSpring.exception.TreeNotFoundException;
import com.testSpring.model.TreeModel;
import com.testSpring.repository.TreeRepository;

@Service
@Transactional
public class TreeService 
{
	@Autowired
	TreeRepository repo;
	
	
	public List<TreeModel> getAll()
	{
		List<TreeModel> trees = repo.findAll();
		
		// Initialize lazy branches while Hibernate session is open
		trees.forEach(tree -> tree.getBranches().size());
		
		return trees;
	}
	
	
	public Optional<TreeModel> getOne(int id)
	{
		TreeModel tree = repo.findById(id)
				.orElseThrow(() -> new TreeNotFoundException(id));
		
		// Initialize lazy branches while Hibernate session is open
		tree.getBranches().size();
		
		return Optional.of(tree);
	}
	
	
	public TreeModel addTree(TreeModel tree)
	{
		if (repo.existsById(tree.getId()))
		{
			throw new TreeAlreadyExistsException(tree.getId());
		}
		
		return repo.save(tree);
	}
	
	
	public List<TreeModel> addMultipleTrees(List<TreeModel> trees)
	{
		for (TreeModel tree : trees)
		{
			if (repo.existsById(tree.getId()))
			{
				throw new TreeAlreadyExistsException(tree.getId());
			}
		}
		
		return repo.saveAll(trees);
	}
	
	
	// using streams
	public TreeModel update(int id, TreeModel tree)
	{
		return repo.findById(id)
				.map(tr -> {
					tr.setName(tree.getName());
					tr.setCategory(tree.getCategory());
					tr.setBranches(tree.getBranches());
					return repo.save(tr);
				})
				.orElseThrow(() -> new TreeNotFoundException(id));
	}
	
	
	// conventional way without streams
	/*  
	public TreeModel updateTree(int id, TreeModel updatedTree) 
	{
	    TreeModel tree = repo.findById(id)
	    		.orElseThrow(() -> new TreeNotFoundException(id));
	    
	    tree.setName(updatedTree.getName());
	    tree.setCategory(updatedTree.getCategory());
	    tree.setBranches(updatedTree.getBranches());
	    
	    return repo.save(tree);
	}
	*/
	
	
	// using optional
	/*
	public TreeModel updateTree(int id, TreeModel updatedTree) 
	{
	    Optional<TreeModel> optionalTree = repo.findById(id);
	    
	    if (optionalTree.isPresent()) 
	    {
	        TreeModel tree = optionalTree.get();
	        
	        tree.setName(updatedTree.getName());
	        tree.setCategory(updatedTree.getCategory());
	        tree.setBranches(updatedTree.getBranches());
	        
	        return repo.save(tree);
	    } 
	    else 
	    {
	        throw new TreeNotFoundException(id);
	    }
	}
	*/
	
	
	public boolean deleteTree(int id) 
	{
        if (repo.existsById(id)) 
        {
            repo.deleteById(id);
            return true;
        }
        
        throw new TreeNotFoundException(id);
    }
	
	
	public TreeModel patchTree(int id, TreeModel partialUpdate) 
	{
	    Optional<TreeModel> optionalTree = repo.findById(id);

	    if (optionalTree.isPresent()) 
	    {
	        TreeModel tree = optionalTree.get();

	        if (partialUpdate.getName() != null) 
	        {
	            tree.setName(partialUpdate.getName());
	        }

	        if (partialUpdate.getCategory() != null) 
	        {
	            tree.setCategory(partialUpdate.getCategory());
	        }

	        if (partialUpdate.getBranches() != null
	                && !partialUpdate.getBranches().isEmpty()) 
	        {
	            tree.setBranches(partialUpdate.getBranches());
	        }

	        return repo.save(tree);
	    } 
	    else 
	    {
	        throw new TreeNotFoundException(id);
	    }
	}
	
	
	// without using optional
	/*
	public TreeModel patchTree(int id, TreeModel partialUpdate) 
	{
	    TreeModel tree = repo.findById(id)
	    		.orElseThrow(() -> new TreeNotFoundException(id));

	    if (partialUpdate.getName() != null) 
	    {
	        tree.setName(partialUpdate.getName());
	    }
	    
	    if (partialUpdate.getCategory() != null) 
	    {
	        tree.setCategory(partialUpdate.getCategory());
	    }
	    
	    if (partialUpdate.getBranches() != null) 
	    { 
	        // skip null/default
	        tree.setBranches(partialUpdate.getBranches());
	    }

	    return repo.save(tree);
	}
	*/
}