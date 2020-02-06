package org.eugene.telegram.dao;

import javax.persistence.*;

/**
 * User subscribe hibernate entity
 */
@Entity
public class Subscribe {

    @Id
    private Long chat_id;
    private String userName;
    private String city;

    public Subscribe() {
    }

    public Subscribe(Long chat_id, String userName, String city) {
        this.chat_id = chat_id;
        this.userName = userName;
        this.city = city;
    }

    public Long getChat_id() {
        return chat_id;
    }

    public void setChat_id(Long chat_id) {
        this.chat_id = chat_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Subscribe{" +
                "chat_id=" + chat_id +
                ", userName='" + userName + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
