package com.testSpring.endpoint;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
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

import com.testSpring.exception.TreeNotFoundException;
import com.testSpring.model.TreeModel;
import com.testSpring.service.TreeSoapService;

import com.testSpring.soap.generated.AddMultipleTreesRequest;
import com.testSpring.soap.generated.AddMultipleTreesResponse;
import com.testSpring.soap.generated.AddTreeRequest;
import com.testSpring.soap.generated.AddTreeResponse;
import com.testSpring.soap.generated.DeleteTreeRequest;
import com.testSpring.soap.generated.DeleteTreeResponse;
import com.testSpring.soap.generated.GetAllTreesRequest;
import com.testSpring.soap.generated.GetAllTreesResponse;
import com.testSpring.soap.generated.GetTreeRequest;
import com.testSpring.soap.generated.GetTreeResponse;
import com.testSpring.soap.generated.PatchTreeRequest;
import com.testSpring.soap.generated.PatchTreeResponse;
import com.testSpring.soap.generated.Tree;
import com.testSpring.soap.generated.UpdateTreeRequest;
import com.testSpring.soap.generated.UpdateTreeResponse;


@ExtendWith(MockitoExtension.class)
class TreeEndpointTest {


    // ========================================
    // MOCK SOAP SERVICE
    // ========================================

    @Mock
    private TreeSoapService treeSoapService;


    // ========================================
    // ENDPOINT UNDER TEST
    // ========================================

    @InjectMocks
    private TreeEndpoint treeEndpoint;


    // ========================================
    // TEST DATA
    // ========================================

    private TreeModel treeModel;


    // ========================================
    // SETUP
    // ========================================

    @BeforeEach
    void setUp() {

        treeModel = new TreeModel();

        treeModel.setId(101);
        treeModel.setName("FirstTree");
        treeModel.setCategory("One");

        treeModel.setBranches(
                Arrays.asList(
                        "Branch-1",
                        "Branch-2",
                        "Branch-3"
                )
        );
    }


    // ========================================
    // GET ONE TREE
    // ========================================

    @Test
    void getTree_shouldReturnTree() {

        GetTreeRequest request =
                new GetTreeRequest();

        request.setId(101);


        when(
                treeSoapService.getTreeById(101)
        )
        .thenReturn(
                Optional.of(treeModel)
        );


        GetTreeResponse response =
                treeEndpoint.getTree(request);


        assertNotNull(response);

        assertNotNull(
                response.getTree()
        );

        assertEquals(
                101,
                response.getTree().getId()
        );

        assertEquals(
                "FirstTree",
                response.getTree().getName()
        );

        assertEquals(
                "One",
                response.getTree().getCategory()
        );

        assertEquals(
                3,
                response.getTree()
                        .getBranches()
                        .size()
        );

        assertEquals(
                "Branch-1",
                response.getTree()
                        .getBranches()
                        .get(0)
        );


        verify(
                treeSoapService,
                times(1)
        )
        .getTreeById(101);
    }


    // ========================================
    // GET ALL TREES
    // ========================================

    @Test
    void getAllTrees_shouldReturnAllTrees() {

        TreeModel secondTree =
                new TreeModel();

        secondTree.setId(201);
        secondTree.setName("SecondTree");
        secondTree.setCategory("Two");

        secondTree.setBranches(
                Arrays.asList(
                        "Branch-A",
                        "Branch-B"
                )
        );


        List<TreeModel> trees =
                Arrays.asList(
                        treeModel,
                        secondTree
                );


        when(
                treeSoapService.getAllTrees()
        )
        .thenReturn(trees);


        GetAllTreesRequest request =
                new GetAllTreesRequest();


        GetAllTreesResponse response =
                treeEndpoint.getAllTrees(request);


        assertNotNull(response);

        assertNotNull(
                response.getTree()
        );

        assertEquals(
                2,
                response.getTree().size()
        );


        // First tree

        assertEquals(
                101,
                response.getTree()
                        .get(0)
                        .getId()
        );

        assertEquals(
                "FirstTree",
                response.getTree()
                        .get(0)
                        .getName()
        );

        assertEquals(
                3,
                response.getTree()
                        .get(0)
                        .getBranches()
                        .size()
        );


        // Second tree

        assertEquals(
                201,
                response.getTree()
                        .get(1)
                        .getId()
        );

        assertEquals(
                "SecondTree",
                response.getTree()
                        .get(1)
                        .getName()
        );


        verify(
                treeSoapService,
                times(1)
        )
        .getAllTrees();
    }


    // ========================================
    // ADD ONE TREE
    // ========================================

    @Test
    void addTree_shouldAddTree() {

        Tree soapTree =
                createSoapTree(
                        101,
                        "FirstTree",
                        "One",
                        "Branch-1",
                        "Branch-2",
                        "Branch-3"
                );


        AddTreeRequest request =
                new AddTreeRequest();

        request.setTree(soapTree);


        when(
                treeSoapService.addTree(
                        any(TreeModel.class)
                )
        )
        .thenReturn(treeModel);


        AddTreeResponse response =
                treeEndpoint.addTree(request);


        assertNotNull(response);

        assertNotNull(
                response.getTree()
        );

        assertEquals(
                101,
                response.getTree().getId()
        );

        assertEquals(
                "FirstTree",
                response.getTree().getName()
        );

        assertEquals(
                "One",
                response.getTree().getCategory()
        );

        assertEquals(
                3,
                response.getTree()
                        .getBranches()
                        .size()
        );


        verify(
                treeSoapService,
                times(1)
        )
        .addTree(
                any(TreeModel.class)
        );
    }


    // ========================================
    // ADD MULTIPLE TREES
    // ========================================

    @Test
    void addMultipleTrees_shouldAddMultipleTrees() {

        Tree soapTree1 =
                createSoapTree(
                        101,
                        "FirstTree",
                        "One",
                        "Branch-1",
                        "Branch-2"
                );


        Tree soapTree2 =
                createSoapTree(
                        201,
                        "SecondTree",
                        "Two",
                        "Branch-A",
                        "Branch-B"
                );


        AddMultipleTreesRequest request =
                new AddMultipleTreesRequest();

        request.getTree().add(soapTree1);
        request.getTree().add(soapTree2);


        TreeModel secondModel =
                new TreeModel();

        secondModel.setId(201);
        secondModel.setName("SecondTree");
        secondModel.setCategory("Two");

        secondModel.setBranches(
                Arrays.asList(
                        "Branch-A",
                        "Branch-B"
                )
        );


        List<TreeModel> savedTrees =
                Arrays.asList(
                        treeModel,
                        secondModel
                );


        when(
                treeSoapService.addMultipleTrees(
                        anyList()
                )
        )
        .thenReturn(savedTrees);


        AddMultipleTreesResponse response =
                treeEndpoint.addMultipleTrees(
                        request
                );


        assertNotNull(response);

        assertEquals(
                2,
                response.getTree().size()
        );


        assertEquals(
                101,
                response.getTree()
                        .get(0)
                        .getId()
        );

        assertEquals(
                "FirstTree",
                response.getTree()
                        .get(0)
                        .getName()
        );


        assertEquals(
                201,
                response.getTree()
                        .get(1)
                        .getId()
        );

        assertEquals(
                "SecondTree",
                response.getTree()
                        .get(1)
                        .getName()
        );


        verify(
                treeSoapService,
                times(1)
        )
        .addMultipleTrees(
                anyList()
        );
    }


    // ========================================
    // UPDATE TREE
    // ========================================

    @Test
    void updateTree_shouldUpdateTree() {

        Tree soapTree =
                createSoapTree(
                        101,
                        "UpdatedTree",
                        "UpdatedCategory",
                        "Updated-Branch-1",
                        "Updated-Branch-2"
                );


        UpdateTreeRequest request =
                new UpdateTreeRequest();

        request.setId(101);
        request.setTree(soapTree);


        TreeModel updatedModel =
                new TreeModel();

        updatedModel.setId(101);
        updatedModel.setName("UpdatedTree");
        updatedModel.setCategory("UpdatedCategory");

        updatedModel.setBranches(
                Arrays.asList(
                        "Updated-Branch-1",
                        "Updated-Branch-2"
                )
        );


        when(
                treeSoapService.updateTree(
                        eq(101),
                        any(TreeModel.class)
                )
        )
        .thenReturn(updatedModel);


        UpdateTreeResponse response =
                treeEndpoint.updateTree(request);


        assertNotNull(response);

        assertNotNull(
                response.getTree()
        );

        assertEquals(
                101,
                response.getTree().getId()
        );

        assertEquals(
                "UpdatedTree",
                response.getTree().getName()
        );

        assertEquals(
                "UpdatedCategory",
                response.getTree().getCategory()
        );

        assertEquals(
                2,
                response.getTree()
                        .getBranches()
                        .size()
        );


        verify(
                treeSoapService,
                times(1)
        )
        .updateTree(
                eq(101),
                any(TreeModel.class)
        );
    }


    // ========================================
    // PATCH TREE
    // ========================================

    @Test
    void patchTree_shouldPatchTree() {

        Tree soapTree =
                createSoapTree(
                        101,
                        "PatchedTree",
                        "One",
                        "Branch-1",
                        "Branch-2",
                        "Branch-3"
                );


        PatchTreeRequest request =
                new PatchTreeRequest();

        request.setId(101);
        request.setTree(soapTree);


        TreeModel patchedModel =
                new TreeModel();

        patchedModel.setId(101);
        patchedModel.setName("PatchedTree");
        patchedModel.setCategory("One");

        patchedModel.setBranches(
                Arrays.asList(
                        "Branch-1",
                        "Branch-2",
                        "Branch-3"
                )
        );


        when(
                treeSoapService.patchTree(
                        eq(101),
                        any(TreeModel.class)
                )
        )
        .thenReturn(patchedModel);


        PatchTreeResponse response =
                treeEndpoint.patchTree(request);


        assertNotNull(response);

        assertNotNull(
                response.getTree()
        );

        assertEquals(
                101,
                response.getTree().getId()
        );

        assertEquals(
                "PatchedTree",
                response.getTree().getName()
        );

        assertEquals(
                "One",
                response.getTree().getCategory()
        );


        verify(
                treeSoapService,
                times(1)
        )
        .patchTree(
                eq(101),
                any(TreeModel.class)
        );
    }


    // ========================================
    // DELETE TREE - SUCCESS
    // ========================================

    @Test
    void deleteTree_shouldDeleteTree() {

        DeleteTreeRequest request =
                new DeleteTreeRequest();

        request.setId(101);


        when(
                treeSoapService.deleteTree(101)
        )
        .thenReturn(true);


        DeleteTreeResponse response =
                treeEndpoint.deleteTree(request);


        assertNotNull(response);

        assertTrue(
                response.isSuccess()
        );

        assertEquals(
                "Tree deleted successfully!",
                response.getMessage()
        );


        verify(
                treeSoapService,
                times(1)
        )
        .deleteTree(101);
    }


    // ========================================
    // DELETE TREE - NOT FOUND
    // ========================================
    //
    // The service now uses global exception
    // handling.
    //
    // Instead of returning false when the tree
    // does not exist, TreeSoapService throws
    // TreeNotFoundException.
    //
    // TreeEndpoint does not handle the exception.
    // It is allowed to propagate to the SOAP
    // exception resolver.
    // ========================================

 // ========================================
 // DELETE TREE - NOT FOUND
 // ========================================
 //
 // TreeSoapService throws TreeNotFoundException
 // when the requested tree does not exist.
 //
 // The endpoint does not handle the exception.
 // It propagates to SoapExceptionResolver,
 // which converts it into a SOAP Fault.
 // ========================================

 @Test
 void deleteTree_shouldPropagateTreeNotFoundException() {

     DeleteTreeRequest request =
             new DeleteTreeRequest();

     request.setId(9999);


     when(
             treeSoapService.deleteTree(9999)
     )
     .thenThrow(
             new TreeNotFoundException(9999)
     );


     TreeNotFoundException exception =
             assertThrows(
                     TreeNotFoundException.class,
                     () ->
                             treeEndpoint.deleteTree(
                                     request
                             )
             );


     assertEquals(
             "Tree not found with id: 9999",
             exception.getMessage()
     );


     verify(
             treeSoapService,
             times(1)
     )
     .deleteTree(9999);
 }


    // ========================================
    // ENTITY -> SOAP DTO
    // BRANCHES NULL
    // ========================================

    @Test
    void getTree_shouldHandleNullBranches() {

        TreeModel model =
                new TreeModel();

        model.setId(301);
        model.setName("ThirdTree");
        model.setCategory("Three");
        model.setBranches(null);


        GetTreeRequest request =
                new GetTreeRequest();

        request.setId(301);


        when(
                treeSoapService.getTreeById(301)
        )
        .thenReturn(
                Optional.of(model)
        );


        GetTreeResponse response =
                treeEndpoint.getTree(request);


        assertNotNull(response);

        assertNotNull(
                response.getTree()
        );

        assertEquals(
                301,
                response.getTree().getId()
        );

        assertEquals(
                "ThirdTree",
                response.getTree().getName()
        );

        assertTrue(
                response.getTree()
                        .getBranches()
                        .isEmpty()
        );
    }


    // ========================================
    // HELPER METHOD
    // CREATE SOAP TREE
    // ========================================

    private Tree createSoapTree(
            int id,
            String name,
            String category,
            String... branches) {

        Tree tree =
                new Tree();

        tree.setId(id);

        tree.setName(name);

        tree.setCategory(category);

        tree.getBranches()
                .addAll(
                        Arrays.asList(branches)
                );

        return tree;
    }
}