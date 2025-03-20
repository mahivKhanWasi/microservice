package com.mahivkhanwasi.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name= "users")
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity{

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String firstname;
    private String lastName;
    private String role;

}
