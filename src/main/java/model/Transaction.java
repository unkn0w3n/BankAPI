package model;

import java.util.Date;
import java.util.Objects;

public class Transaction {
    private String t_type;
    private String account_from;
    private String account_to;
    private Double amount;
    private Integer approved_by_id;
    private String status;
    private Date created_at;
    private Date updated_at;

    public String getT_type() {
        return t_type;
    }

    public void setT_type(String t_type) {
        this.t_type = t_type;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getApproved_by_id() {
        return approved_by_id;
    }

    public void setApproved_by_id(Integer approved_by_id) {
        this.approved_by_id = approved_by_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
    public String toString() {
        return "Transaction{" +
                "t_type='" + t_type + '\'' +
                ", account_from='" + account_from + '\'' +
                ", account_to='" + account_to + '\'' +
                ", amount=" + amount +
                ", approved_by_id=" + approved_by_id +
                ", status='" + status + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction transaction = (Transaction) o;
        return t_type.equals(transaction.t_type) && account_from.equals(transaction.account_from) && account_to.equals(transaction.account_to) && Objects.equals(amount, transaction.amount) && Objects.equals(approved_by_id, transaction.approved_by_id) && Objects.equals(status, transaction.status) && Objects.equals(created_at, transaction.created_at) && Objects.equals(updated_at, transaction.updated_at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t_type, account_from, account_to, amount, approved_by_id, status, created_at, updated_at);
    }


}
