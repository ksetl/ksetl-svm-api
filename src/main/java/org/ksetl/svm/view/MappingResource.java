package org.ksetl.svm.view;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.ksetl.svm.mapping.MappingService;

@Path("/admin/mappings")
public class MappingResource {

    @Inject
    Template mappings;

    @Inject
    MappingService mappingService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Blocking
    public TemplateInstance get() {
        return mappings.data("mappings", mappingService.findAll());
    }

}