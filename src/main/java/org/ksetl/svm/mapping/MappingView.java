package org.ksetl.svm.mapping;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record MappingView (
        Integer mappingId,
        @NotNull(message = "{MappingView.sourceSystemId.required}") Integer sourceSystemId,
        @NotEmpty(message = "{MappingView.sourceFieldName.required}") String sourceFieldName,
        @NotEmpty(message = "{MappingView.sourceValue.required}") String sourceValue,
        @NotNull(message = "{MappingView.targetSystemId.required}") Integer targetSystemId,
        @NotEmpty(message = "{MappingView.targetValue.required}") String targetValue,
        @NotNull(message = "{MappingView.targetValueType.required}") ValueType targetValueType
        ) {
}