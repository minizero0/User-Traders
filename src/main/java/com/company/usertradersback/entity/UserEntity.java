package com.company.usertradersback.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "User")
public class UserEntity implements UserDetails {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "userNamed")
    private String userNamed;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "tel")
    private String tel;

    @ManyToOne
    @JoinColumn(name = "departmentId")
    private UserDepartmentEntity departmentId;

    @Column(name = "studentId")
    private String studentId;

    @Column(name = "gender")
    private Integer gender;


    @Column(name = "loginType")
    private Integer loginType;

    @Column(name = "imagePath")
    private String imagePath;

    @Column(name = "createAt")
    private LocalDateTime createAt;

    @Column(name = "modifiedAt")
    private LocalDateTime modifiedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "role",
            joinColumns = @JoinColumn(name = "id")
    )
    private List<String> roles = new ArrayList<>();

    @Builder
    public UserEntity(Integer id, String email, String password, String userNamed, String nickname,
                      String tel,
                      UserDepartmentEntity departmentId, String studentId,
                      Integer gender, List<String> roles, Integer loginType, String imagePath,
                      LocalDateTime createAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userNamed = userNamed;
        this.nickname = nickname;
        this.tel = tel;
        this.departmentId = departmentId;
        this.studentId = studentId;
        this.gender = gender;
        this.roles = roles;
        this.loginType = loginType;
        this.imagePath = imagePath;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // 사용자의 email를 반환 (unique한 값)
    @Override
    public String getUsername() {
        return email;
    }

    // 사용자의 password를 반환
    @Override
    public String getPassword() {
        return password;
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        // 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않았음
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금되었는지 확인하는 로직
        return true; // true -> 잠금되지 않았음
    }

    // 패스워드의 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        // 패스워드가 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않았음
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        // 계정이 사용 가능한지 확인하는 로직
        return true; // true -> 사용 가능
    }
}
