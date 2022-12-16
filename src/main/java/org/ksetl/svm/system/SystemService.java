package org.ksetl.svm.system;

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
public class SystemService {

    private final SystemRepository systemRepository;
    private final SystemMapper systemMapper;

    public SystemService(SystemRepository systemRepository, SystemMapper systemMapper) {
        this.systemRepository = systemRepository;
        this.systemMapper = systemMapper;
    }

    public List<System> findAll() {
        return this.systemMapper.toDomainList(systemRepository.findAll().list());
    }

    public Optional<System> findById(@Nonnull Integer systemId) {
        return systemRepository.findByIdOptional(systemId)
                .map(systemMapper::toDomain);
    }

    @Transactional
    public System save(@Valid System system) {
        Log.debugf("Saving %s", system);
        SystemEntity entity = systemMapper.toEntity(system);
        systemRepository.persist(entity);
        return systemMapper.toDomain(entity);
    }

    @Transactional
    public System update(@Valid System system) {
        Log.debugf("Updating %s", system);
        if (Objects.isNull(system.systemId())) {
            throw new ServiceException("System does not have a systemId");
        }
        SystemEntity entity = systemRepository.findByIdOptional(system.systemId())
                .orElseThrow(() -> new ServiceException("No System found for systemId[%s]", system.systemId()));
        systemMapper.updateEntityFromDomain(system, entity);
        systemRepository.persist(entity);
        return systemMapper.toDomain(entity);
    }

}
