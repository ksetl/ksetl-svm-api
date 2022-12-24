package org.ksetl.svm.mapping;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.ksetl.svm.system.System;

public record Mapping(
        Integer mappingId,
        @NotNull(message = "{Mapping.sourceSystem.required}") System sourceSystem,
        @NotEmpty(message = "{Mapping.sourceFieldName.required}") String sourceFieldName,
        @NotEmpty(message = "{Mapping.sourceValue.required}") String sourceValue,
        @NotNull(message = "{Mapping.targetSystem.required}") System targetSystem,
        @NotEmpty(message = "{Mapping.targetValue.required}") String targetValue,
        @NotNull(message = "{Mapping.targetValueType.required}") ValueType targetValueType
        ) {
}