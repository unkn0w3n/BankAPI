package model;

import java.util.Objects;

public class Account {
    private String title;
    private String number;
    private String currency;
    private int user_id;
    private Double balance;

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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return user_id == account.user_id && Objects.equals(title, account.title) && number.equals(account.number) && currency.equals(account.currency) && Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, number, currency, user_id, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                ", title='" + title + '\'' +
                ", number='" + number + '\'' +
                ", currency='" + currency + '\'' +
                ", user_id=" + user_id +
                ", balance=" + balance +
                '}';
    }

}
