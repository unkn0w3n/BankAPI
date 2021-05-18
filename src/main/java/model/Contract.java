package model;

import java.util.Date;
import java.util.Objects;

public class Contract {
    private String unique_id;
    private String description;
    private Integer user_id_from;
    private Integer user_id_to;
    private String account_from;
    private String account_to;
    private String c_status;
    private Date created_at;
    private Date updated_at;

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUser_id_from() {
        return user_id_from;
    }

    public void setUser_id_from(Integer user_id_from) {
        this.user_id_from = user_id_from;
    }

    public Integer getUser_id_to() {
        return user_id_to;
    }

    public void setUser_id_to(Integer user_id_to) {
        this.user_id_to = user_id_to;
    }

    public String getAccount_from() {
        return account_from;
    }

    public void setAccount_from(String account_from) {
        this.account_from = account_from;
    }

    public String getAccount_to() {
        return account_to;
    }

    public void setAccount_to(String account_to) {
        this.account_to = account_to;
    }

    public String getC_status() {
        return c_status;
    }

    public void setC_status(String c_status) {
        this.c_status = c_status;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return Objects.equals(unique_id, contract.unique_id) && Objects.equals(description, contract.description) && Objects.equals(user_id_from, contract.user_id_from) && Objects.equals(user_id_to, contract.user_id_to) && Objects.equals(account_from, contract.account_from) && Objects.equals(account_to, contract.account_to) && Objects.equals(c_status, contract.c_status) && Objects.equals(created_at, contract.created_at) && Objects.equals(updated_at, contract.updated_at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unique_id, description, user_id_from, user_id_to, account_from, account_to, c_status, created_at, updated_at);
    }

    @Override
    public String toString() {
        return "Contract{" +
                "unique_id='" + unique_id + '\'' +
                ", description='" + description + '\'' +
                ", user_id_from=" + user_id_from +
                ", user_id_to=" + user_id_to +
                ", account_from='" + account_from + '\'' +
                ", account_to='" + account_to + '\'' +
                ", c_status='" + c_status + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
