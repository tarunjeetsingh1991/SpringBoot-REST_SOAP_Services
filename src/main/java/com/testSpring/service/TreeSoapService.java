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
public class TreeSoapService {

    @Autowired
    private TreeRepository repo;


    // ========================================
    // GET ALL TREES
    // ========================================

    @Transactional(readOnly = true)
    public List<TreeModel> getAllTrees() {

        List<TreeModel> trees = repo.findAll();

        // Initialize lazy branches while Hibernate session is open
        trees.forEach(tree -> tree.getBranches().size());

        return trees;
    }


    // ========================================
    // GET ONE TREE
    // ========================================

    @Transactional(readOnly = true)
    public Optional<TreeModel> getTreeById(int id) {

        TreeModel tree = repo.findById(id)
                .orElseThrow(
                        () -> new TreeNotFoundException(id)
                );

        // Initialize lazy branches while Hibernate session is open
        tree.getBranches().size();

        return Optional.of(tree);
    }


    // ========================================
    // ADD ONE TREE
    // ========================================

    public TreeModel addTree(TreeModel tree) {

        if (repo.existsById(tree.getId())) {

            throw new TreeAlreadyExistsException(
                    tree.getId()
            );
        }

        return repo.save(tree);
    }


    // ========================================
    // ADD MULTIPLE TREES
    // ========================================

    public List<TreeModel> addMultipleTrees(
            List<TreeModel> trees) {

        for (TreeModel tree : trees) {

            if (repo.existsById(tree.getId())) {

                throw new TreeAlreadyExistsException(
                        tree.getId()
                );
            }
        }

        return repo.saveAll(trees);
    }


    // ========================================
    // UPDATE TREE
    // ========================================

    public TreeModel updateTree(
            int id,
            TreeModel updatedTree) {

        Optional<TreeModel> optionalTree =
                repo.findById(id);

        if (optionalTree.isPresent()) {

            TreeModel existingTree =
                    optionalTree.get();

            existingTree.setName(
                    updatedTree.getName()
            );

            existingTree.setCategory(
                    updatedTree.getCategory()
            );

            existingTree.setBranches(
                    updatedTree.getBranches()
            );

            return repo.save(existingTree);
        }

        throw new TreeNotFoundException(id);
    }


    // ========================================
    // DELETE TREE
    // ========================================

    public boolean deleteTree(int id) {

        if (repo.existsById(id)) {

            repo.deleteById(id);

            return true;
        }

        throw new TreeNotFoundException(id);
    }


    // ========================================
    // PATCH TREE
    // ========================================

    public TreeModel patchTree(
            int id,
            TreeModel partialUpdate) {

        Optional<TreeModel> optionalTree =
                repo.findById(id);

        if (optionalTree.isPresent()) {

            TreeModel existingTree =
                    optionalTree.get();

            if (partialUpdate.getName() != null) {

                existingTree.setName(
                        partialUpdate.getName()
                );
            }

            if (partialUpdate.getCategory() != null) {

                existingTree.setCategory(
                        partialUpdate.getCategory()
                );
            }

            if (partialUpdate.getBranches() != null) {

                existingTree.setBranches(
                        partialUpdate.getBranches()
                );
            }

            return repo.save(existingTree);
        }

        throw new TreeNotFoundException(id);
    }
}