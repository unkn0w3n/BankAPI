package model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class OperationDTO {
    private String operation;
    @JsonProperty("entity")
    private String entityType;
    @JsonProperty("entity_number")
    private String entityNumber;
    @JsonProperty("double")
    private Double amount;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityNumber() {
        return entityNumber;
    }

    public void setEntityNumber(String entityNumber) {
        this.entityNumber = entityNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationDTO that = (OperationDTO) o;
        return Objects.equals(operation, that.operation) && Objects.equals(entityType, that.entityType) && Objects.equals(entityNumber, that.entityNumber) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, entityType, entityNumber, amount);
    }

    @Override
    public String toString() {
        return "DTOOperation{" +
                "operation='" + operation + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityNumber='" + entityNumber + '\'' +
                ", amount=" + amount +
                '}';
    }




}
