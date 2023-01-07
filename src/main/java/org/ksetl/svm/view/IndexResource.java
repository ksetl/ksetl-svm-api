package org.ksetl.svm.view;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.net.URI;

@Path("/admin")
public class IndexResource {

    private static final URI uri = URI.create("/admin/mappings");

    @GET
    @Operation(hidden = true)
    public Response get() {
        return Response.temporaryRedirect(uri).build();
    }

}