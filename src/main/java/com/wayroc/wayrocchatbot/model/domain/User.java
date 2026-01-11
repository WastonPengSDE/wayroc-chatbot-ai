package com.wayroc.wayrocchatbot.model.domain;



import lombok.Data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "wayroc_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Primary key ID

    @Column(name = "user_account", nullable = false, unique = true, length = 64)
    private String userAccount;  // User account

    @Column(name = "user_password", nullable = false, length = 128)
    private String userPassword; // Encrypted password

    @Column(name = "gender")              // TINYINT → Java 用 Integer/Short 都行，常用 Integer
    private Integer gender;               // 0-unknown, 1-male, 2-female

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "user_role", length = 16)
    private String userRole;              // user / admin

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", updatable = false)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;            // 0-active, 1-deleted
}
