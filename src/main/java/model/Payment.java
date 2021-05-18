package model;

import java.util.Date;
import java.util.Objects;

public class Payment {
    private String p_type;
    private String account_from;
    private String account_to;
    private Double amount;
    private Integer approved_by_id;
    private String status;
    private Date created_at;
    private Date updated_at;

    public String getP_type() {
        return p_type;
    }

    public void setP_type(String p_type) {
        this.p_type = p_type;
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
        return "Payment{" +
                "p_type='" + p_type + '\'' +
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
        Payment payment = (Payment) o;
        return p_type.equals(payment.p_type) && account_from.equals(payment.account_from) && account_to.equals(payment.account_to) && Objects.equals(amount, payment.amount) && Objects.equals(approved_by_id, payment.approved_by_id) && Objects.equals(status, payment.status) && Objects.equals(created_at, payment.created_at) && Objects.equals(updated_at, payment.updated_at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(p_type, account_from, account_to, amount, approved_by_id, status, created_at, updated_at);
    }


}
