package org.ksetl.svm.mapping;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MappingRepository implements PanacheRepositoryBase<MappingEntity, Integer> {

}