package org.ksetl.svm.mapping;

import io.quarkus.logging.Log;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.ksetl.svm.ServiceException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class MappingService {

    private final MappingRepository mappingRepository;
    private final MappingMapper mappingMapper;

    public MappingService(MappingRepository mappingRepository, MappingMapper mappingMapper) {
        this.mappingRepository = mappingRepository;
        this.mappingMapper = mappingMapper;
    }

    public List<Mapping> findAll() {
        return this.mappingMapper.toDomainList(mappingRepository.findAll().list());
    }

    public Optional<Mapping> findById(@Nonnull Integer mappingId) {
        return mappingRepository.findByIdOptional(mappingId)
                .map(mappingMapper::toDomain);
    }

    @Transactional
    public Mapping save(@Valid Mapping mapping) {
        Log.debugf("Saving %s", mapping);
        MappingEntity entity = mappingMapper.toEntity(mapping);
        mappingRepository.persist(entity);
        return mappingMapper.toDomain(entity);
    }

    @Transactional
    public Mapping update(@Valid Mapping mapping) {
        Log.debugf("Updating %s", mapping);
        if (Objects.isNull(mapping.mappingId())) {
            throw new ServiceException("Mapping does not have a MappingId");
        }
        MappingEntity entity = mappingRepository.findByIdOptional(mapping.mappingId())
                .orElseThrow(() -> new ServiceException("No Mapping found for MappingId[%s]", mapping.mappingId()));
        mappingMapper.updateEntityFromDomain(mapping, entity);
        mappingRepository.persist(entity);
        return mappingMapper.toDomain(entity);
    }

}
