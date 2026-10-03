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
class TreeSoapServiceTest {

    @Mock
    private TreeRepository repo;

    @InjectMocks
    private TreeSoapService service;

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
    void getAllTrees_shouldReturnTrees() {

        when(repo.findAll())
                .thenReturn(List.of(tree));

        List<TreeModel> result =
                service.getAllTrees();

        assertEquals(1, result.size());

        assertEquals(
                "FirstTree",
                result.get(0).getName()
        );

        assertEquals(
                3,
                result.get(0).getBranches().size()
        );
    }


    // ========================================
    // GET ONE - SUCCESS
    // ========================================

    @Test
    void getTreeById_shouldReturnTree() {

        when(repo.findById(101))
                .thenReturn(Optional.of(tree));

        Optional<TreeModel> result =
                service.getTreeById(101);

        assertTrue(result.isPresent());

        assertEquals(
                101,
                result.get().getId()
        );

        assertEquals(
                3,
                result.get().getBranches().size()
        );
    }


    // ========================================
    // GET ONE - NOT FOUND
    // ========================================

    @Test
    void getTreeById_shouldThrowException_whenNotFound() {

        when(repo.findById(9999))
                .thenReturn(Optional.empty());

        assertThrows(
                TreeNotFoundException.class,
                () -> service.getTreeById(9999)
        );
    }


    // ========================================
    // ADD
    // ========================================

    @Test
    void addTree_shouldSaveTree() {

        when(repo.existsById(101))
                .thenReturn(false);

        when(repo.save(tree))
                .thenReturn(tree);

        TreeModel result =
                service.addTree(tree);

        assertEquals(
                "FirstTree",
                result.getName()
        );

        verify(repo).save(tree);
    }


    // ========================================
    // ADD DUPLICATE
    // ========================================

    @Test
    void addTree_shouldThrowException_whenDuplicate() {

        when(repo.existsById(101))
                .thenReturn(true);

        assertThrows(
                TreeAlreadyExistsException.class,
                () -> service.addTree(tree)
        );

        verify(repo, never())
                .save(any());
    }


    // ========================================
    // UPDATE
    // ========================================

    @Test
    void updateTree_shouldUpdateExistingTree() {

        TreeModel update =
                new TreeModel();

        update.setName("Updated");
        update.setCategory("UpdatedCategory");

        update.setBranches(
                List.of("UpdatedBranch")
        );

        when(repo.findById(101))
                .thenReturn(Optional.of(tree));

        when(repo.save(any()))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0)
                );

        TreeModel result =
                service.updateTree(
                        101,
                        update
                );

        assertEquals(
                "Updated",
                result.getName()
        );

        assertEquals(
                "UpdatedCategory",
                result.getCategory()
        );

        assertEquals(
                1,
                result.getBranches().size()
        );
    }


    @Test
    void updateTree_shouldThrowException_whenNotFound() {

        when(repo.findById(9999))
                .thenReturn(Optional.empty());

        assertThrows(
                TreeNotFoundException.class,
                () -> service.updateTree(
                        9999,
                        tree
                )
        );
    }


    // ========================================
    // PATCH
    // ========================================

    @Test
    void patchTree_shouldUpdateProvidedValues() {

        TreeModel patch =
                new TreeModel();

        patch.setName("Patched");

        when(repo.findById(101))
                .thenReturn(Optional.of(tree));

        when(repo.save(any()))
                .thenAnswer(
                        invocation ->
                                invocation.getArgument(0)
                );

        TreeModel result =
                service.patchTree(
                        101,
                        patch
                );

        assertEquals(
                "Patched",
                result.getName()
        );

        assertEquals(
                "One",
                result.getCategory()
        );
    }


    @Test
    void patchTree_shouldThrowException_whenNotFound() {

        when(repo.findById(9999))
                .thenReturn(Optional.empty());

        assertThrows(
                TreeNotFoundException.class,
                () -> service.patchTree(
                        9999,
                        tree
                )
        );
    }


    // ========================================
    // DELETE
    // ========================================

    @Test
    void deleteTree_shouldDeleteExistingTree() {

        when(repo.existsById(101))
                .thenReturn(true);

        boolean result =
                service.deleteTree(101);

        assertTrue(result);

        verify(repo).deleteById(101);
    }


    @Test
    void deleteTree_shouldThrowException_whenNotFound() {

        when(repo.existsById(9999))
                .thenReturn(false);

        assertThrows(
                TreeNotFoundException.class,
                () -> service.deleteTree(9999)
        );

        verify(repo, never())
                .deleteById(anyInt());
    }
}