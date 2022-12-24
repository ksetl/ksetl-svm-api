package org.ksetl.svm.mapping;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.ksetl.svm.SecurityRoles;
import org.ksetl.svm.ServiceException;
import org.ksetl.svm.system.System;
import org.ksetl.svm.system.SystemService;

import java.net.URI;
import java.util.Objects;

@Path("/mappings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "mapping", description = "Mapping Operations")
public class MappingResource {

    private final MappingService mappingService;
    private final SystemService systemService;
    private final MappingMapper mappingMapper;

    public MappingResource(MappingService mappingService, SystemService systemService, MappingMapper mappingMapper) {
        this.mappingService = mappingService;
        this.systemService = systemService;
        this.mappingMapper = mappingMapper;
    }

    @GET
    @APIResponse(
            responseCode = "200",
            description = "Get All Mappings",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(type = SchemaType.ARRAY, implementation = MappingView.class)
            )
    )
    @RolesAllowed({ SecurityRoles.ROLE_SVM_ADMIN, SecurityRoles.ROLE_SVM_MAPPING_READ })
    public Response get() {
        return Response.ok(mappingService.findAll().stream().map(mappingMapper::toView)).build();
    }

    @GET
    @Path("/{mappingId}")
    @APIResponse(
            responseCode = "200",
            description = "Get Mapping by mappingId",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(type = SchemaType.OBJECT, implementation = MappingView.class)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Mapping does not exist for mappingId",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @RolesAllowed({ SecurityRoles.ROLE_SVM_ADMIN, SecurityRoles.ROLE_SVM_MAPPING_READ })
    public Response getById(@Parameter(name = "mappingId", required = true) @PathParam("mappingId") Integer mappingId) {
        return mappingService.findById(mappingId)
                .map(mapping -> Response.ok(mappingMapper.toView(mapping)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @APIResponse(
            responseCode = "201",
            description = "Mapping Created",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(type = SchemaType.OBJECT, implementation = Mapping.class)
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid Mapping",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @APIResponse(
            responseCode = "400",
            description = "Mapping already exists for mappingId",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @RolesAllowed({ SecurityRoles.ROLE_SVM_ADMIN, SecurityRoles.ROLE_SVM_MAPPING_WRITE })
    public Response post(@NotNull @Valid MappingView mappingView, @Context UriInfo uriInfo) {
        if (!Objects.isNull(mappingView.mappingId())){
            throw new ServiceException("Mapping contains mappingId[%s]", mappingView.mappingId());
        }
        Mapping incoming = this.createFromView(mappingView);
        Mapping saved = mappingService.save(incoming);
        URI uri = uriInfo.getAbsolutePathBuilder().path(Integer.toString(saved.mappingId())).build();
        return Response.created(uri).entity(mappingMapper.toView(saved)).build();
    }

    @PUT
    @Path("/{mappingId}")
    @APIResponse(
            responseCode = "204",
            description = "Mapping updated",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(type = SchemaType.OBJECT, implementation = Mapping.class)
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Invalid Mapping",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @APIResponse(
            responseCode = "400",
            description = "Mapping object does not have mappingId",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @APIResponse(
            responseCode = "400",
            description = "Path variable mappingId does not match mapping.mappingId",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @APIResponse(
            responseCode = "404",
            description = "No Mapping found for mappingId provided",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @RolesAllowed({ SecurityRoles.ROLE_SVM_ADMIN, SecurityRoles.ROLE_SVM_MAPPING_WRITE })
    public Response put(@Parameter(name = "mappingId", required = true) @PathParam("mappingId") Integer mappingId, @NotNull @Valid MappingView mappingView) {
        if (!Objects.equals(mappingId, mappingView.mappingId())) {
            throw new ServiceException("Path variable MappingId does not match Mapping.MappingId");
        }
        Mapping incoming = this.createFromView(mappingView);
        mappingService.update(incoming);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private Mapping createFromView(MappingView mappingView) {
        System sourceSystem = systemService.findById(mappingView.sourceSystemId())
                .orElseThrow(() -> new ServiceException("No Source System found for sourceSystemId[%s]", mappingView.sourceSystemId()));
        System targetSystem = systemService.findById(mappingView.targetSystemId())
                .orElseThrow(() -> new ServiceException("No Target System found for targetSystemId[%s]", mappingView.targetSystemId()));
        return new Mapping(mappingView.mappingId(), sourceSystem, mappingView.sourceFieldName(), mappingView.sourceValue(),
                targetSystem, mappingView.targetValue(), mappingView.targetValueType());
    }

}