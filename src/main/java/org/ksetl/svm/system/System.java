package org.ksetl.svm.system;

import jakarta.validation.constraints.NotEmpty;

public record System(Integer systemId, @NotEmpty(message = "{System.name.required}") String name) {
}