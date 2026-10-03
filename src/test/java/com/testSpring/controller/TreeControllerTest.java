package com.testSpring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testSpring.model.TreeModel;
import com.testSpring.service.TreeService;


@ExtendWith(MockitoExtension.class)
class TreeControllerTest {


    // ========================================
    // MOCK SERVICE
    // ========================================

    @Mock
    private TreeService treeService;


    // ========================================
    // CONTROLLER UNDER TEST
    // ========================================

    @InjectMocks
    private TreeController treeController;


    // ========================================
    // MOCK MVC
    // ========================================

    private MockMvc mockMvc;


    // ========================================
    // JSON CONVERTER
    // ========================================

    private ObjectMapper objectMapper;


    // ========================================
    // TEST DATA
    // ========================================

    private TreeModel tree;


    // ========================================
    // SETUP
    // ========================================

    @BeforeEach
    void setUp() {

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(treeController)
                        .build();

        objectMapper =
                new ObjectMapper();


        tree =
                new TreeModel();

        tree.setId(101);

        tree.setName(
                "FirstTree"
        );

        tree.setCategory(
                "One"
        );

        tree.setBranches(
                Arrays.asList(
                        "Branch-1",
                        "Branch-2",
                        "Branch-3"
                )
        );
    }


    // ========================================
    // TEST HOME
    // ========================================

    @Test
    void home_shouldReturnWelcome()
            throws Exception {

        mockMvc.perform(
                get("/home")
        )
        .andExpect(
                status().isOk()
        )
        .andExpect(
                content().string("Welcome")
        );
    }


    // ========================================
    // GET ALL TREES
    // ========================================

    @Test
    void getAllTrees_shouldReturnAllTrees()
            throws Exception {

        TreeModel tree2 =
                new TreeModel();

        tree2.setId(201);

        tree2.setName(
                "SecondTree"
        );

        tree2.setCategory(
                "Two"
        );

        tree2.setBranches(
                Arrays.asList(
                        "Branch-A",
                        "Branch-B"
                )
        );


        List<TreeModel> trees =
                Arrays.asList(
                        tree,
                        tree2
                );


        when(treeService.getAll())
                .thenReturn(trees);


        mockMvc.perform(
                get("/all")
        )

        .andExpect(
                status().isOk()
        )

        .andExpect(
                content()
                        .contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
        )

        .andExpect(
                jsonPath("$[0].id")
                        .value(101)
        )

        .andExpect(
                jsonPath("$[0].name")
                        .value("FirstTree")
        )

        .andExpect(
                jsonPath("$[0].category")
                        .value("One")
        )

        .andExpect(
                jsonPath("$[0].branches[0]")
                        .value("Branch-1")
        )

        .andExpect(
                jsonPath("$[1].id")
                        .value(201)
        )

        .andExpect(
                jsonPath("$[1].name")
                        .value("SecondTree")
        );


        verify(
                treeService,
                times(1)
        ).getAll();
    }


    // ========================================
    // GET ONE TREE
    // ========================================

    @Test
    void getOne_shouldReturnTree()
            throws Exception {

        when(
                treeService.getOne(101)
        )
        .thenReturn(
                Optional.of(tree)
        );


        mockMvc.perform(
                get("/all/101")
        )

        .andExpect(
                status().isOk()
        )

        .andExpect(
                jsonPath("$.id")
                        .value(101)
        )

        .andExpect(
                jsonPath("$.name")
                        .value("FirstTree")
        )

        .andExpect(
                jsonPath("$.category")
                        .value("One")
        )

        .andExpect(
                jsonPath("$.branches[0]")
                        .value("Branch-1")
        )

        .andExpect(
                jsonPath("$.branches[1]")
                        .value("Branch-2")
        )

        .andExpect(
                jsonPath("$.branches[2]")
                        .value("Branch-3")
        );


        verify(
                treeService,
                times(1)
        ).getOne(101);
    }


    // ========================================
    // ADD ONE TREE
    // ========================================

    @Test
    void addTree_shouldCreateTree()
            throws Exception {

        when(
                treeService.addTree(
                        any(TreeModel.class)
                )
        )
        .thenReturn(tree);


        mockMvc.perform(

                post("/add")

                .contentType(
                        MediaType.APPLICATION_JSON
                )

                .content(
                        objectMapper
                                .writeValueAsString(tree)
                )
        )

        .andExpect(
                status().isCreated()
        )

        .andExpect(
                jsonPath("$.id")
                        .value(101)
        )

        .andExpect(
                jsonPath("$.name")
                        .value("FirstTree")
        )

        .andExpect(
                jsonPath("$.category")
                        .value("One")
        )

        .andExpect(
                jsonPath("$.branches.length()")
                        .value(3)
        );


        verify(
                treeService,
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
    void addMultiple_shouldCreateMultipleTrees()
            throws Exception {

        TreeModel tree2 =
                new TreeModel();

        tree2.setId(201);

        tree2.setName(
                "SecondTree"
        );

        tree2.setCategory(
                "Two"
        );

        tree2.setBranches(
                Arrays.asList(
                        "Branch-A",
                        "Branch-B"
                )
        );


        List<TreeModel> trees =
                Arrays.asList(
                        tree,
                        tree2
                );


        when(
                treeService.addMultipleTrees(
                        anyList()
                )
        )
        .thenReturn(trees);


        mockMvc.perform(

                post("/addAll")

                .contentType(
                        MediaType.APPLICATION_JSON
                )

                .content(
                        objectMapper
                                .writeValueAsString(trees)
                )
        )

        .andExpect(
                status().isCreated()
        )

        .andExpect(
                jsonPath("$.length()")
                        .value(2)
        )

        .andExpect(
                jsonPath("$[0].id")
                        .value(101)
        )

        .andExpect(
                jsonPath("$[0].name")
                        .value("FirstTree")
        )

        .andExpect(
                jsonPath("$[1].id")
                        .value(201)
        )

        .andExpect(
                jsonPath("$[1].name")
                        .value("SecondTree")
        );


        verify(
                treeService,
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
    void updateTree_shouldReturnUpdatedTree()
            throws Exception {

        TreeModel updatedTree =
                new TreeModel();

        updatedTree.setId(101);

        updatedTree.setName(
                "UpdatedTree"
        );

        updatedTree.setCategory(
                "UpdatedCategory"
        );

        updatedTree.setBranches(
                Arrays.asList(
                        "Updated-Branch-1",
                        "Updated-Branch-2"
                )
        );


        when(
                treeService.update(
                        eq(101),
                        any(TreeModel.class)
                )
        )
        .thenReturn(updatedTree);


        mockMvc.perform(

                put("/update/101")

                .contentType(
                        MediaType.APPLICATION_JSON
                )

                .content(
                        objectMapper
                                .writeValueAsString(
                                        updatedTree
                                )
                )
        )

        .andExpect(
                status().isOk()
        )

        .andExpect(
                jsonPath("$.id")
                        .value(101)
        )

        .andExpect(
                jsonPath("$.name")
                        .value("UpdatedTree")
        )

        .andExpect(
                jsonPath("$.category")
                        .value(
                                "UpdatedCategory"
                        )
        )

        .andExpect(
                jsonPath("$.branches.length()")
                        .value(2)
        );


        verify(
                treeService,
                times(1)
        )
        .update(
                eq(101),
                any(TreeModel.class)
        );
    }


    // ========================================
    // PATCH TREE
    // ========================================

    @Test
    void patchTree_shouldReturnPatchedTree()
            throws Exception {

        TreeModel patchRequest =
                new TreeModel();

        patchRequest.setName(
                "PatchedTree"
        );


        TreeModel patchedTree =
                new TreeModel();

        patchedTree.setId(101);

        patchedTree.setName(
                "PatchedTree"
        );

        patchedTree.setCategory(
                "One"
        );

        patchedTree.setBranches(
                Arrays.asList(
                        "Branch-1",
                        "Branch-2",
                        "Branch-3"
                )
        );


        when(
                treeService.patchTree(
                        eq(101),
                        any(TreeModel.class)
                )
        )
        .thenReturn(patchedTree);


        mockMvc.perform(

                patch("/patch/101")

                .contentType(
                        MediaType.APPLICATION_JSON
                )

                .content(
                        objectMapper
                                .writeValueAsString(
                                        patchRequest
                                )
                )
        )

        .andExpect(
                status().isOk()
        )

        .andExpect(
                jsonPath("$.id")
                        .value(101)
        )

        .andExpect(
                jsonPath("$.name")
                        .value("PatchedTree")
        )

        .andExpect(
                jsonPath("$.category")
                        .value("One")
        );


        verify(
                treeService,
                times(1)
        )
        .patchTree(
                eq(101),
                any(TreeModel.class)
        );
    }


    // ========================================
    // DELETE TREE
    // ========================================

    @Test
    void deleteTree_shouldDeleteTree()
            throws Exception {

        when(
                treeService.deleteTree(101)
        )
        .thenReturn(true);


        mockMvc.perform(
                delete("/delete/101")
        )

        .andExpect(
                status().isOk()
        )

        .andExpect(
                content().string(
                        "Tree deleted successfully!"
                )
        );


        verify(
                treeService,
                times(1)
        )
        .deleteTree(101);
    }


    // ========================================
    // COUNT TREES
    // ========================================

    @Test
    void getTreeCount_shouldReturnNumberOfTrees()
            throws Exception {

        TreeModel tree2 =
                new TreeModel();

        tree2.setId(201);

        TreeModel tree3 =
                new TreeModel();

        tree3.setId(301);


        List<TreeModel> trees =
                Arrays.asList(
                        tree,
                        tree2,
                        tree3
                );


        when(
                treeService.getAll()
        )
        .thenReturn(trees);


        mockMvc.perform(
                get("/count")
        )

        .andExpect(
                status().isOk()
        )

        .andExpect(
                content().string(
                        "Total number of trees: 3"
                )
        );


        verify(
                treeService,
                times(1)
        )
        .getAll();
    }
}