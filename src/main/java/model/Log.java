package model;

import java.util.Date;
import java.util.Objects;

public class Log {
    private String type;
    private String message;
    private Date created_at;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return type.equals(log.type) && message.equals(log.message) && Objects.equals(created_at, log.created_at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, message, created_at);
    }

    @Override
    public String toString() {
        return "Log{" +
                "type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", created_at=" + created_at +
                '}';
    }
}
