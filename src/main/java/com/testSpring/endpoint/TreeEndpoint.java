package com.testSpring.endpoint;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

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


@Endpoint
public class TreeEndpoint {

    private static final String NAMESPACE =
            "http://testSpring.com/trees";

    private final TreeSoapService treeSoapService;


    // =============================================
    // CONSTRUCTOR INJECTION
    // =============================================

    public TreeEndpoint(TreeSoapService treeSoapService) {
        this.treeSoapService = treeSoapService;
    }


    // =============================================
    // GET ONE TREE
    // =============================================

    /*
     * OLD CODE:
     *
     * Previously getTreeById() could return
     * Optional.empty() when the tree was not found.
     *
     * treeSoapService
     *      .getTreeById(request.getId())
     *      .ifPresent(model ->
     *              response.setTree(
     *                      toSoapTree(model)
     *              )
     *      );
     *
     * Now TreeSoapService throws TreeNotFoundException.
     * SoapExceptionResolver converts that exception
     * into a SOAP Fault.
     */

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "getTreeRequest"
    )
    @ResponsePayload
    public GetTreeResponse getTree(
            @RequestPayload GetTreeRequest request) {

        TreeModel model =
                treeSoapService
                        .getTreeById(request.getId())
                        .get();

        GetTreeResponse response =
                new GetTreeResponse();

        response.setTree(
                toSoapTree(model)
        );

        return response;
    }


    // =============================================
    // GET ALL TREES
    // =============================================

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "getAllTreesRequest"
    )
    @ResponsePayload
    public GetAllTreesResponse getAllTrees(
            @RequestPayload GetAllTreesRequest request) {

        GetAllTreesResponse response =
                new GetAllTreesResponse();

        List<TreeModel> trees =
                treeSoapService.getAllTrees();

        for (TreeModel model : trees) {

            response.getTree().add(
                    toSoapTree(model)
            );
        }

        return response;
    }


    // =============================================
    // ADD ONE TREE
    // =============================================

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "addTreeRequest"
    )
    @ResponsePayload
    public AddTreeResponse addTree(
            @RequestPayload AddTreeRequest request) {

        TreeModel model =
                toTreeModel(request.getTree());

        /*
         * If the ID already exists,
         * TreeSoapService throws:
         *
         * TreeAlreadyExistsException
         *
         * SoapExceptionResolver will convert
         * it into a SOAP Client Fault.
         */
        TreeModel savedTree =
                treeSoapService.addTree(model);

        AddTreeResponse response =
                new AddTreeResponse();

        response.setTree(
                toSoapTree(savedTree)
        );

        return response;
    }


    // =============================================
    // ADD MULTIPLE TREES
    // =============================================

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "addMultipleTreesRequest"
    )
    @ResponsePayload
    public AddMultipleTreesResponse addMultipleTrees(
            @RequestPayload AddMultipleTreesRequest request) {

        List<TreeModel> models =
                new ArrayList<>();

        for (Tree soapTree : request.getTree()) {

            models.add(
                    toTreeModel(soapTree)
            );
        }

        /*
         * If any supplied tree ID already exists,
         * TreeSoapService throws
         * TreeAlreadyExistsException.
         */
        List<TreeModel> savedTrees =
                treeSoapService.addMultipleTrees(models);

        AddMultipleTreesResponse response =
                new AddMultipleTreesResponse();

        for (TreeModel model : savedTrees) {

            response.getTree().add(
                    toSoapTree(model)
            );
        }

        return response;
    }


    // =============================================
    // UPDATE TREE
    // =============================================

    /*
     * OLD CODE:
     *
     * TreeModel updatedTree =
     *         treeSoapService.updateTree(
     *                 request.getId(),
     *                 model
     *         );
     *
     * if (updatedTree != null) {
     *
     *     response.setTree(
     *             toSoapTree(updatedTree)
     *     );
     * }
     *
     * Previously null meant the tree was not found.
     *
     * Now TreeSoapService throws
     * TreeNotFoundException instead.
     */

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "updateTreeRequest"
    )
    @ResponsePayload
    public UpdateTreeResponse updateTree(
            @RequestPayload UpdateTreeRequest request) {

        TreeModel model =
                toTreeModel(request.getTree());

        TreeModel updatedTree =
                treeSoapService.updateTree(
                        request.getId(),
                        model
                );

        UpdateTreeResponse response =
                new UpdateTreeResponse();

        response.setTree(
                toSoapTree(updatedTree)
        );

        return response;
    }


    // =============================================
    // PATCH TREE
    // =============================================

    /*
     * OLD CODE:
     *
     * if (updatedTree != null) {
     *
     *     response.setTree(
     *             toSoapTree(updatedTree)
     *     );
     * }
     *
     * This null check is no longer required.
     *
     * If the ID doesn't exist,
     * TreeSoapService throws TreeNotFoundException.
     */

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "patchTreeRequest"
    )
    @ResponsePayload
    public PatchTreeResponse patchTree(
            @RequestPayload PatchTreeRequest request) {

        TreeModel partialUpdate =
                toTreeModel(request.getTree());

        TreeModel updatedTree =
                treeSoapService.patchTree(
                        request.getId(),
                        partialUpdate
                );

        PatchTreeResponse response =
                new PatchTreeResponse();

        response.setTree(
                toSoapTree(updatedTree)
        );

        return response;
    }


    // =============================================
    // DELETE TREE
    // =============================================

    /*
     * OLD CODE:
     *
     * boolean deleted =
     *         treeSoapService.deleteTree(
     *                 request.getId()
     *         );
     *
     * response.setSuccess(deleted);
     *
     * if (deleted) {
     *
     *     response.setMessage(
     *             "Tree deleted successfully!"
     *     );
     *
     * } else {
     *
     *     response.setMessage(
     *             "Tree not found!"
     *     );
     * }
     *
     *
     * Previously:
     *
     * true  -> deleted
     * false -> tree not found
     *
     * Now deleteTree() throws TreeNotFoundException
     * when the tree does not exist.
     */

    @PayloadRoot(
            namespace = NAMESPACE,
            localPart = "deleteTreeRequest"
    )
    @ResponsePayload
    public DeleteTreeResponse deleteTree(
            @RequestPayload DeleteTreeRequest request) {

        treeSoapService.deleteTree(
                request.getId()
        );

        DeleteTreeResponse response =
                new DeleteTreeResponse();

        response.setSuccess(true);

        response.setMessage(
                "Tree deleted successfully!"
        );

        return response;
    }


    // =============================================
    // JPA ENTITY -> SOAP DTO
    // =============================================

    private Tree toSoapTree(TreeModel model) {

        Tree tree =
                new Tree();

        tree.setId(
                model.getId()
        );

        tree.setName(
                model.getName()
        );

        tree.setCategory(
                model.getCategory()
        );

        if (model.getBranches() != null) {

            tree.getBranches().addAll(
                    model.getBranches()
            );
        }

        return tree;
    }


    // =============================================
    // SOAP DTO -> JPA ENTITY
    // =============================================

    private TreeModel toTreeModel(Tree tree) {

        TreeModel model =
                new TreeModel();

        model.setId(
                tree.getId()
        );

        model.setName(
                tree.getName()
        );

        model.setCategory(
                tree.getCategory()
        );

        model.setBranches(
                new ArrayList<>(
                        tree.getBranches()
                )
        );

        return model;
    }
}