package ru.gb.main.utils.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@ToString
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", length = 30, unique = true)
    private String login;
    @Column(name = "password", length = 30)
    private String password;
    @Transient
    private boolean isLoginn;
    @Transient
    private boolean isSystem;
    @Transient
    private boolean isDropped;
    @Transient
    private boolean isFirstRegister;
    @Transient
    private boolean isAlreadyRegistered;
    @Transient
    private boolean isExists = true;

    public User(String login, String password) {
        if (login.length() > 30) throw new RuntimeException("Length must be less than 30");
        if (password != null && password.length() > 30) throw new RuntimeException("Length must be less than 30");
        this.login = login;
        this.password = password;
    }
}
