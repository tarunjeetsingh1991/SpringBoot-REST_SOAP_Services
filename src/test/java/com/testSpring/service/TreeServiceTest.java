package com.testSpring.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.testSpring.exception.TreeAlreadyExistsException;
import com.testSpring.exception.TreeNotFoundException;
import com.testSpring.model.TreeModel;
import com.testSpring.repository.TreeRepository;

@ExtendWith(MockitoExtension.class)
class TreeServiceTest {

    @Mock
    private TreeRepository repo;

    @InjectMocks
    private TreeService treeService;

    private TreeModel tree;


    @BeforeEach
    void setUp() {

        tree = new TreeModel();

        tree.setId(101);
        tree.setName("FirstTree");
        tree.setCategory("One");
        tree.setBranches(
                Arrays.asList(
                        "Branch-1",
                        "Branch-2",
                        "Branch-3"
                )
        );
    }


    // ========================================
    // GET ALL
    // ========================================

    @Test
    void getAll_shouldReturnAllTrees() {

        TreeModel tree2 = new TreeModel();

        tree2.setId(201);
        tree2.setName("SecondTree");
        tree2.setCategory("Two");

        when(repo.findAll())
                .thenReturn(
                        Arrays.asList(tree, tree2)
                );

        List<TreeModel> result =
                treeService.getAll();

        assertEquals(2, result.size());

        assertEquals(
                "FirstTree",
                result.get(0).getName()
        );

        verify(repo, times(1))
                .findAll();
    }


    // ========================================
    // GET ONE - SUCCESS
    // ========================================

    @Test
    void getOne_shouldReturnTree_whenTreeExists() {

        when(repo.findById(101))
                .thenReturn(Optional.of(tree));

        Optional<TreeModel> result =
                treeService.getOne(101);

        assertTrue(result.isPresent());

        assertEquals(
                101,
                result.get().getId()
        );

        assertEquals(
                "FirstTree",
                result.get().getName()
        );

        verify(repo).findById(101);
    }


    // ========================================
    // GET ONE - NOT FOUND
    // ========================================

    @Test
    void getOne_shouldThrowException_whenTreeDoesNotExist() {

        when(repo.findById(9999))
                .thenReturn(Optional.empty());

        assertThrows(
                TreeNotFoundException.class,
                () -> treeService.getOne(9999)
        );

        verify(repo).findById(9999);
    }


    // ========================================
    // ADD TREE - SUCCESS
    // ========================================

    @Test
    void addTree_shouldSaveTree_whenIdDoesNotExist() {

        when(repo.existsById(101))
                .thenReturn(false);

        when(repo.save(tree))
                .thenReturn(tree);

        TreeModel result =
                treeService.addTree(tree);

        assertNotNull(result);

        assertEquals(
                "FirstTree",
                result.getName()
        );

        verify(repo).existsById(101);

        verify(repo).save(tree);
    }


    // ========================================
    // ADD TREE - ALREADY EXISTS
    // ========================================

    @Test
    void addTree_shouldThrowException_whenTreeAlreadyExists() {

        when(repo.existsById(101))
                .thenReturn(true);

        assertThrows(
                TreeAlreadyExistsException.class,
                () -> treeService.addTree(tree)
        );

        verify(repo).existsById(101);

        verify(repo, never())
                .save(any(TreeModel.class));
    }


    // ========================================
    // ADD MULTIPLE TREES
    // ========================================

    @Test
    void addMultipleTrees_shouldSaveAllTrees() {

        TreeModel tree2 = new TreeModel();

        tree2.setId(201);
        tree2.setName("SecondTree");
        tree2.setCategory("Two");

        List<TreeModel> trees =
                Arrays.asList(tree, tree2);

        when(repo.existsById(101))
                .thenReturn(false);

        when(repo.existsById(201))
                .thenReturn(false);

        when(repo.saveAll(trees))
                .thenReturn(trees);

        List<TreeModel> result =
                treeService.addMultipleTrees(trees);

        assertEquals(2, result.size());

        verify(repo).existsById(101);
        verify(repo).existsById(201);
        verify(repo).saveAll(trees);
    }


    // ========================================
    // ADD MULTIPLE - DUPLICATE
    // ========================================

    @Test
    void addMultipleTrees_shouldThrowException_whenTreeExists() {

        TreeModel tree2 = new TreeModel();

        tree2.setId(201);

        List<TreeModel> trees =
                Arrays.asList(tree, tree2);

        when(repo.existsById(101))
                .thenReturn(false);

        when(repo.existsById(201))
                .thenReturn(true);

        assertThrows(
                TreeAlreadyExistsException.class,
                () -> treeService.addMultipleTrees(trees)
        );

        verify(repo, never())
                .saveAll(anyList());
    }


    // ========================================
    // UPDATE - SUCCESS
    // ========================================

    @Test
    void update_shouldUpdateTree_whenTreeExists() {

        TreeModel update = new TreeModel();

        update.setName("UpdatedTree");
        update.setCategory("UpdatedCategory");
        update.setBranches(
                Arrays.asList(
                        "New-Branch-1",
                        "New-Branch-2"
                )
        );

        when(repo.findById(101))
                .thenReturn(Optional.of(tree));

        when(repo.save(any(TreeModel.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0)
                );

        TreeModel result =
                treeService.update(
                        101,
                        update
                );

        assertEquals(
                "UpdatedTree",
                result.getName()
        );

        assertEquals(
                "UpdatedCategory",
                result.getCategory()
        );

        assertEquals(
                2,
                result.getBranches().size()
        );

        verify(repo).findById(101);
        verify(repo).save(tree);
    }


    // ========================================
    // UPDATE - NOT FOUND
    // ========================================

    @Test
    void update_shouldThrowException_whenTreeDoesNotExist() {

        when(repo.findById(9999))
                .thenReturn(Optional.empty());

        assertThrows(
                TreeNotFoundException.class,
                () -> treeService.update(
                        9999,
                        tree
                )
        );

        verify(repo, never())
                .save(any());
    }


    // ========================================
    // DELETE - SUCCESS
    // ========================================

    @Test
    void deleteTree_shouldDeleteTree_whenTreeExists() {

        when(repo.existsById(101))
                .thenReturn(true);

        boolean result =
                treeService.deleteTree(101);

        assertTrue(result);

        verify(repo).deleteById(101);
    }


    // ========================================
    // DELETE - NOT FOUND
    // ========================================

    @Test
    void deleteTree_shouldThrowException_whenTreeDoesNotExist() {

        when(repo.existsById(9999))
                .thenReturn(false);

        assertThrows(
                TreeNotFoundException.class,
                () -> treeService.deleteTree(9999)
        );

        verify(repo, never())
                .deleteById(anyInt());
    }


    // ========================================
    // PATCH - ALL VALUES
    // ========================================

    @Test
    void patchTree_shouldUpdateAllProvidedFields() {

        TreeModel patch = new TreeModel();

        patch.setName("PatchedTree");
        patch.setCategory("PatchedCategory");

        patch.setBranches(
                Arrays.asList("PatchedBranch")
        );

        when(repo.findById(101))
                .thenReturn(Optional.of(tree));

        when(repo.save(any(TreeModel.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0)
                );

        TreeModel result =
                treeService.patchTree(
                        101,
                        patch
                );

        assertEquals(
                "PatchedTree",
                result.getName()
        );

        assertEquals(
                "PatchedCategory",
                result.getCategory()
        );

        assertEquals(
                1,
                result.getBranches().size()
        );

        verify(repo).save(tree);
    }


    // ========================================
    // PATCH - NULL VALUES
    // ========================================

    @Test
    void patchTree_shouldIgnoreNullValues() {

        TreeModel patch = new TreeModel();

        /*
         * TreeModel initializes branches as an empty list.
         *
         * Set branches explicitly to null because this test
         * is specifically checking that null values are ignored.
         */
        patch.setBranches(null);


        when(repo.findById(101))
                .thenReturn(Optional.of(tree));


        when(repo.save(any(TreeModel.class)))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0)
                );


        TreeModel result =
                treeService.patchTree(
                        101,
                        patch
                );


	        assertEquals(
	                "FirstTree",
	                result.getName()
	        );
	
	
	        assertEquals(
	                "One",
	                result.getCategory()
	        );
	
	
	        assertEquals(
	                3,
	                result.getBranches().size()
	        );
	
	
	        assertEquals(
	                "Branch-1",
	                result.getBranches().get(0)
	        );
	
	
	        verify(repo).findById(101);
	
	        verify(repo).save(tree);
	    }

    // ========================================
    // PATCH - NOT FOUND
    // ========================================

    @Test
    void patchTree_shouldThrowException_whenTreeDoesNotExist() {

        when(repo.findById(9999))
                .thenReturn(Optional.empty());

        assertThrows(
                TreeNotFoundException.class,
                () -> treeService.patchTree(
                        9999,
                        tree
                )
        );

        verify(repo, never())
                .save(any());
    }
}