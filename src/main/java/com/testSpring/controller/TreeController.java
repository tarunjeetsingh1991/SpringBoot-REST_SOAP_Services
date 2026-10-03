package com.testSpring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.testSpring.model.TreeModel;
import com.testSpring.service.TreeService;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:5173")
public class TreeController 
{

    @Autowired
    private TreeService treeService;


    // ========================================
    // HOME
    // ========================================

    @GetMapping("/home")
    public String home() 
    {
        return "Welcome";
    }


    // ========================================
    // GET ALL TREES
    // ========================================

    @GetMapping("/all")
    public List<TreeModel> getAllTrees() 
    {
        return treeService.getAll();
    }


    // ========================================
    // GET ONE TREE
    // ========================================

    /*
     * OLD CODE:
     *
     * This worked when the service returned Optional.empty()
     * when the tree was not found.
     *
     * Now TreeService throws TreeNotFoundException.
     * GlobalExceptionHandler handles that exception and
     * returns HTTP 404.
     */
    
    /*
    @GetMapping("/all/{id}")
    public ResponseEntity<TreeModel> getOne(@PathVariable int id) 
    {
        return treeService.getOne(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    */


    // NEW CODE:
    
    @GetMapping("/all/{id}")
    public ResponseEntity<TreeModel> getOne(@PathVariable int id) 
    {
        TreeModel tree = treeService.getOne(id).get();

        return ResponseEntity.ok(tree);
    }


    // ========================================
    // ADD ONE TREE
    // ========================================

    /*
     * OLD CODE:
     *
     * This directly returned the saved object.
     */
    
    /*
    @PostMapping("/add")
    public TreeModel addTree(@RequestBody TreeModel model) 
    {
        return treeService.addTree(model);
    }
    */


    // NEW CODE:
    //
    // ResponseEntity gives us better control over
    // the HTTP response and status code.
    
    @PostMapping("/add")
    public ResponseEntity<TreeModel> addTree(
            @RequestBody TreeModel model) 
    {
        TreeModel savedTree =
                treeService.addTree(model);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedTree);
    }


    // ========================================
    // ADD MULTIPLE TREES
    // ========================================

    /*
     * OLD CODE:
     *
     * @PostMapping("/addAll")
     * public List<TreeModel> addMultiple(
     *         @RequestBody List<TreeModel> models) 
     * {
     *     return treeService.addMultipleTrees(models);
     * }
     */


    // NEW CODE:

    @PostMapping("/addAll")
    public ResponseEntity<List<TreeModel>> addMultiple(
            @RequestBody List<TreeModel> models) 
    {
        List<TreeModel> savedTrees =
                treeService.addMultipleTrees(models);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedTrees);
    }


    // ========================================
    // UPDATE TREE
    // ========================================

    /*
     * OLD CODE:
     *
     * Previously the service returned null when
     * the tree did not exist.
     *
     * The controller therefore had to check:
     *
     * updated != null
     */
    
    /*
    @PutMapping("/update/{id}")
    public ResponseEntity<TreeModel> updateTree(
            @RequestBody TreeModel model,
            @PathVariable int id) 
    {
        TreeModel updated =
                treeService.update(id, model);

        return updated != null
                ? ResponseEntity.ok(updated)
                : ResponseEntity.notFound().build();
    }
    */


    // NEW CODE:
    //
    // TreeService now throws TreeNotFoundException
    // if the ID doesn't exist.
    //
    // GlobalExceptionHandler catches it.

    @PutMapping("/update/{id}")
    public ResponseEntity<TreeModel> updateTree(
            @RequestBody TreeModel model,
            @PathVariable int id) 
    {
        TreeModel updated =
                treeService.update(id, model);

        return ResponseEntity.ok(updated);
    }


    // ========================================
    // DELETE TREE
    // ========================================

    /*
     * OLD CODE:
     *
     * The service previously returned:
     *
     * true  -> deleted
     * false -> not found
     */
    
    /*
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTree(
            @PathVariable int id) 
    {
        boolean deleted =
                treeService.deleteTree(id);

        return deleted
                ? ResponseEntity.ok(
                        "Tree deleted successfully!")
                : ResponseEntity.notFound().build();
    }
    */


    // NEW CODE:
    //
    // deleteTree() now throws TreeNotFoundException
    // if the tree does not exist.

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTree(
            @PathVariable int id) 
    {
        treeService.deleteTree(id);

        return ResponseEntity.ok(
                "Tree deleted successfully!"
        );
    }


    // ========================================
    // PATCH TREE
    // ========================================

    /*
     * OLD CODE:
     *
     * Previously patchTree() returned null when
     * the tree was not found.
     */
    
    /*
    @PatchMapping("/patch/{id}")
    public ResponseEntity<TreeModel> patchTree(
            @PathVariable int id,
            @RequestBody TreeModel partialUpdate) 
    {
        TreeModel updated =
                treeService.patchTree(
                        id,
                        partialUpdate
                );

        if (updated != null) 
        {
            return ResponseEntity.ok(updated);
        } 
        else 
        {
            return ResponseEntity.notFound().build();
        }
    }
    */


    // NEW CODE:
    //
    // TreeNotFoundException is now handled globally.

    @PatchMapping("/patch/{id}")
    public ResponseEntity<TreeModel> patchTree(
            @PathVariable int id,
            @RequestBody TreeModel partialUpdate) 
    {
        TreeModel updated =
                treeService.patchTree(
                        id,
                        partialUpdate
                );

        return ResponseEntity.ok(updated);
    }


    // ========================================
    // NEW FEATURE - COUNT TREES
    // ========================================
    //
    // This is an additional endpoint.
    //
    // Example:
    //
    // GET /count
    //
    // Response:
    //
    // Total number of trees: 6
    //
    // This doesn't modify any of the existing
    // REST endpoints.

    @GetMapping("/count")
    public ResponseEntity<String> getTreeCount() 
    {
        int count =
                treeService.getAll().size();

        return ResponseEntity.ok(
                "Total number of trees: " + count
        );
    }
}