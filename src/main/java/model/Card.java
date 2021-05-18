package model;

import java.util.Objects;

public class Card {
    private Integer account_id;
    private String  type;
    private String  title;
    private String  number;
    private String  currency;
    private Double  limit;
    private Boolean approved;
    private Boolean active;


    public Integer getAccount_id() {
        return account_id;
    }

    public void setAccount_id(Integer account_id) {
        this.account_id = account_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getLimit() {
        return limit;
    }

    public void setLimit(Double limit) {
        this.limit = limit;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return account_id.equals(card.account_id) && type.equals(card.type) && Objects.equals(title, card.title) && number.equals(card.number) && currency.equals(card.currency) && Objects.equals(limit, card.limit) && Objects.equals(approved, card.approved) && Objects.equals(active, card.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account_id, type, title, number, currency, limit, approved, active);
    }

    @Override
    public String toString() {
        return "Card{" +
                "account_id=" + account_id +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", number=" + number +
                ", currency='" + currency + '\'' +
                ", limit=" + limit +
                ", approved=" + approved +
                ", active=" + active +
                '}';
    }



}
