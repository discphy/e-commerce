package kr.hhplus.be.ecommerce.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Builder
    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public static User create(String username) {
        return User.builder()
            .username(username)
            .build();
    }
}
