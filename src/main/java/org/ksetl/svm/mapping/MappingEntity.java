package org.ksetl.svm.mapping;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.ksetl.svm.system.SystemEntity;

import java.util.Objects;

@Entity(name = "Mapping")
@Table(name = "Mapping")
public class MappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Integer mappingId;

    @ManyToOne
    @JoinColumn(name = "source_system_id", nullable = false)
    @NotNull
    private SystemEntity sourceSystem;

    @Column(name = "source_field_name", nullable = false)
    @NotEmpty
    private String sourceFieldName;

    @Column(name = "source_value", nullable = false)
    @NotEmpty
    private String sourceValue;

    @ManyToOne
    @JoinColumn(name = "target_system_id", nullable = false)
    @NotNull
    private SystemEntity targetSystem;

    @Column(name = "target_value", nullable = false)
    @NotEmpty
    private String targetValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_value_type", nullable = false)
    @NotNull
    private ValueType targetValueType;

    public Integer getMappingId() {
        return mappingId;
    }

    public void setMappingId(Integer mappingId) {
        this.mappingId = mappingId;
    }

    public SystemEntity getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(SystemEntity sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getSourceFieldName() {
        return sourceFieldName;
    }

    public void setSourceFieldName(String sourceFieldName) {
        this.sourceFieldName = sourceFieldName;
    }

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public SystemEntity getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(SystemEntity targetSystem) {
        this.targetSystem = targetSystem;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public ValueType getTargetValueType() {
        return targetValueType;
    }

    public void setTargetValueType(ValueType targetValueType) {
        this.targetValueType = targetValueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MappingEntity that = (MappingEntity) o;

        if (!Objects.equals(mappingId, that.mappingId)) return false;
        if (!Objects.equals(sourceSystem, that.sourceSystem)) return false;
        if (!Objects.equals(sourceFieldName, that.sourceFieldName))
            return false;
        if (!Objects.equals(sourceValue, that.sourceValue)) return false;
        if (!Objects.equals(targetSystem, that.targetSystem)) return false;
        if (!Objects.equals(targetValue, that.targetValue)) return false;
        return targetValueType == that.targetValueType;
    }

    @Override
    public int hashCode() {
        int result = mappingId != null ? mappingId.hashCode() : 0;
        result = 31 * result + (sourceSystem != null ? sourceSystem.hashCode() : 0);
        result = 31 * result + (sourceFieldName != null ? sourceFieldName.hashCode() : 0);
        result = 31 * result + (sourceValue != null ? sourceValue.hashCode() : 0);
        result = 31 * result + (targetSystem != null ? targetSystem.hashCode() : 0);
        result = 31 * result + (targetValue != null ? targetValue.hashCode() : 0);
        result = 31 * result + (targetValueType != null ? targetValueType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MappingEntity" + '[' +
                "mappingId=" + mappingId +
                ", sourceSystem=" + sourceSystem +
                ", sourceFieldName='" + sourceFieldName + '\'' +
                ", sourceValue='" + sourceValue + '\'' +
                ", targetSystem=" + targetSystem +
                ", targetValue='" + targetValue + '\'' +
                ", targetValueType=" + targetValueType +
                ']';
    }
}