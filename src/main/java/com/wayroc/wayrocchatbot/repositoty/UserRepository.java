package com.wayroc.wayrocchatbot.repositoty;

import com.wayroc.wayrocchatbot.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUserAccount(String userAccount);

    // 根据账号 + 密码查询用户（用于登录）
    Optional<User> findByUserAccountAndUserPassword(String userAccount, String userPassword);

}
