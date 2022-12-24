package org.ksetl.svm.mapping;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface MappingMapper {

    List<Mapping> toDomainList(List<MappingEntity> entities);

    Mapping toDomain(MappingEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    MappingEntity toEntity(Mapping domain);

    void updateEntityFromDomain(Mapping domain, @MappingTarget MappingEntity entity);

    void updateDomainFromEntity(MappingEntity entity, @MappingTarget Mapping domain);

    @org.mapstruct.Mapping(target = "sourceSystemId", source = "sourceSystem.systemId")
    @org.mapstruct.Mapping(target = "targetSystemId", source = "targetSystem.systemId")
    MappingView toView(Mapping mapping);

}