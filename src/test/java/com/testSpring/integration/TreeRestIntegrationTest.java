package com.testSpring.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testSpring.model.TreeModel;
import com.testSpring.repository.TreeRepository;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:integrationdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TreeRestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TreeRepository treeRepository;


    // =============================================
    // CLEAN DATABASE BEFORE EACH TEST
    // =============================================

    @BeforeEach
    void setUp() {

        /*
         * This deletes all records from the H2
         * test database before every test.
         *
         * It makes the integration test independent
         * and repeatable.
         *
         * This does NOT touch the real MySQL database
         * because the "test" profile uses H2.
         */

        treeRepository.deleteAll();
    }


    // =============================================
    // COMPLETE REST API INTEGRATION TEST
    // =============================================
    //
    // This test verifies the complete REST flow:
    //
    // POST
    //   ↓
    // GET
    //   ↓
    // PATCH
    //   ↓
    // PUT
    //   ↓
    // GET ALL
    //   ↓
    // DELETE
    //   ↓
    // GET deleted tree
    //
    // The complete application flow is:
    //
    // MockMvc
    //   ↓
    // TreeController
    //   ↓
    // TreeService
    //   ↓
    // TreeRepository
    //   ↓
    // H2 Database
    //
    // No service or repository is mocked.
    // =============================================

    @Test
    void completeTreeCrudLifecycle_shouldWorkEndToEnd()
            throws Exception {

        // =========================================
        // STEP 1 - CREATE TREE
        // =========================================

        TreeModel newTree = new TreeModel();

        newTree.setId(9001);
        newTree.setName("Integration Tree");
        newTree.setCategory("Integration");

        newTree.setBranches(
                Arrays.asList(
                        "Branch-1",
                        "Branch-2",
                        "Branch-3"
                )
        );


        mockMvc.perform(
                post("/add")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(
                                objectMapper.writeValueAsString(
                                        newTree
                                )
                        )
        )
        .andExpect(
                status().isCreated()
        )
        .andExpect(
                jsonPath("$.id")
                        .value(9001)
        )
        .andExpect(
                jsonPath("$.name")
                        .value("Integration Tree")
        )
        .andExpect(
                jsonPath("$.category")
                        .value("Integration")
        )
        .andExpect(
                jsonPath("$.branches.length()")
                        .value(3)
        );


        // =========================================
        // STEP 2 - GET TREE BY ID
        // =========================================

        mockMvc.perform(
                get("/all/{id}", 9001)
                        .accept(
                                MediaType.APPLICATION_JSON
                        )
        )
        .andExpect(
                status().isOk()
        )
        .andExpect(
                jsonPath("$.id")
                        .value(9001)
        )
        .andExpect(
                jsonPath("$.name")
                        .value("Integration Tree")
        )
        .andExpect(
                jsonPath("$.category")
                        .value("Integration")
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


        // =========================================
        // STEP 3 - PATCH TREE
        // =========================================

        /*
         * We only want to change the name.
         *
         * Category and branches should remain
         * unchanged.
         */

        String patchJson =
                """
                {
                    "name": "Patched Integration Tree"
                }
                """;


        mockMvc.perform(
                patch("/patch/{id}", 9001)
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(patchJson)
        )
        .andExpect(
                status().isOk()
        )
        .andExpect(
                jsonPath("$.id")
                        .value(9001)
        )
        .andExpect(
                jsonPath("$.name")
                        .value(
                                "Patched Integration Tree"
                        )
        )
        .andExpect(
                jsonPath("$.category")
                        .value("Integration")
        )
        .andExpect(
                jsonPath("$.branches.length()")
                        .value(3)
        );


        // =========================================
        // STEP 4 - VERIFY PATCH IN DATABASE
        // =========================================

        mockMvc.perform(
                get("/all/{id}", 9001)
        )
        .andExpect(
                status().isOk()
        )
        .andExpect(
                jsonPath("$.name")
                        .value(
                                "Patched Integration Tree"
                        )
        )
        .andExpect(
                jsonPath("$.category")
                        .value("Integration")
        );


        // =========================================
        // STEP 5 - UPDATE TREE USING PUT
        // =========================================

        TreeModel updatedTree =
                new TreeModel();

        updatedTree.setId(9001);

        updatedTree.setName(
                "Updated Integration Tree"
        );

        updatedTree.setCategory(
                "Updated Category"
        );

        updatedTree.setBranches(
                Arrays.asList(
                        "Updated-Branch-1",
                        "Updated-Branch-2"
                )
        );


        mockMvc.perform(
                put("/update/{id}", 9001)
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(
                                objectMapper.writeValueAsString(
                                        updatedTree
                                )
                        )
        )
        .andExpect(
                status().isOk()
        )
        .andExpect(
                jsonPath("$.id")
                        .value(9001)
        )
        .andExpect(
                jsonPath("$.name")
                        .value(
                                "Updated Integration Tree"
                        )
        )
        .andExpect(
                jsonPath("$.category")
                        .value(
                                "Updated Category"
                        )
        )
        .andExpect(
                jsonPath("$.branches.length()")
                        .value(2)
        );


        // =========================================
        // STEP 6 - VERIFY PUT
        // =========================================

        mockMvc.perform(
                get("/all/{id}", 9001)
        )
        .andExpect(
                status().isOk()
        )
        .andExpect(
                jsonPath("$.id")
                        .value(9001)
        )
        .andExpect(
                jsonPath("$.name")
                        .value(
                                "Updated Integration Tree"
                        )
        )
        .andExpect(
                jsonPath("$.category")
                        .value(
                                "Updated Category"
                        )
        )
        .andExpect(
                jsonPath("$.branches[0]")
                        .value(
                                "Updated-Branch-1"
                        )
        )
        .andExpect(
                jsonPath("$.branches[1]")
                        .value(
                                "Updated-Branch-2"
                        )
        );


        // =========================================
        // STEP 7 - GET ALL TREES
        // =========================================

        mockMvc.perform(
                get("/all")
                        .accept(
                                MediaType.APPLICATION_JSON
                        )
        )
        .andExpect(
                status().isOk()
        )
        .andExpect(
                jsonPath("$")
                        .isArray()
        )
        .andExpect(
                jsonPath("$.length()")
                        .value(1)
        )
        .andExpect(
                jsonPath("$[0].id")
                        .value(9001)
        )
        .andExpect(
                jsonPath("$[0].name")
                        .value(
                                "Updated Integration Tree"
                        )
        );


        // =========================================
        // STEP 8 - DELETE TREE
        // =========================================

        mockMvc.perform(
                delete("/delete/{id}", 9001)
        )
        .andExpect(
                status().isOk()
        )
        .andExpect(
                content().string(
                        "Tree deleted successfully!"
                )
        );


        // =========================================
        // STEP 9 - VERIFY DATABASE IS EMPTY
        // =========================================

        mockMvc.perform(
                get("/all")
        )
        .andExpect(
                status().isOk()
        )
        .andExpect(
                jsonPath("$")
                        .isArray()
        )
        .andExpect(
                jsonPath("$.length()")
                        .value(0)
        );


        // =========================================
        // STEP 10 - GET DELETED TREE
        // =========================================
        //
        // Since TreeService now throws
        // TreeNotFoundException, the global
        // exception handler should convert it
        // into the appropriate HTTP response.
        // =========================================

        mockMvc.perform(
                get("/all/{id}", 9001)
                        .accept(
                                MediaType.APPLICATION_JSON
                        )
        )
        .andExpect(
                status().isNotFound()
        );
    }
}