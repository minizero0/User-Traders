package com.company.usertradersback.dto.user;

import com.company.usertradersback.entity.UserDepartmentEntity;
import com.company.usertradersback.entity.UserEntity;
import com.company.usertradersback.payload.Payload;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
//회원 가입을 위한 회원 한명 전체 UserDto (requestDto) 및
//회원 한명 전체를 조회하기 위한 UserDto (responseDto)
public class UserDto {

    // 고정 페이로드
    private Payload payload;

    // 회원 고유번호
    private Integer id;

    // 회원 이메일
    private String email;

    // 회원 비밀번호
    private String password;

    // 회원 이름
    private String userNamed;

    // 회원 닉네임
    private String nickname;

    // 회원 전화번호
    private String tel;

    // 회원 학과 고유번호
    private UserDepartmentEntity departmentId;

    // 회원 학번
    private String studentId;

    // 회원 성별 0:남자,1:여자
    private Integer gender;

    // 회원 역활
    private List<String> roles = new ArrayList<>();

    // 회원 로그인 종류 0:일반,1:카카오
    private Integer loginType;

    // 회원 프로필 이미지
    private String imagePath;

    // 회원 등록 날짜
    private LocalDateTime createAt;

    // 회원 수정 날짜
    private LocalDateTime modifiedAt;

    // 장바구니 수
    private Integer likeCount;

    @Builder
    public UserDto(Payload payload, Integer id, String email, String password, String userNamed, String nickname,
                   String tel,
                   UserDepartmentEntity departmentId, String studentId,
                   Integer gender, List<String> roles, Integer loginType, String imagePath,
                   LocalDateTime createAt, LocalDateTime modifiedAt
    ,Integer likeCount) {
        this.payload =payload;
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
        this.likeCount = likeCount;
    }

    //회원 가입 정보 추가를 위한 UserDto -> UserEntity 변환
    // 그러나 안쓰고 직접 빌더 패턴을 이용하였다.
    public UserEntity convertDtoToEntity() {
        return UserEntity.builder()
                .id(id)
                .email(email)
                .password(password)
                .userNamed(userNamed)
                .nickname(nickname)
                .tel(tel)
                .departmentId(departmentId)
                .studentId(studentId)
                .gender(gender)
                .roles(roles)
                .loginType(loginType)
                .imagePath(imagePath)
                .createAt(createAt)
                .modifiedAt(modifiedAt)
                .build();
    }

    //프로필 조회할 때 DB에서 꺼낸 전체 UserEntity -> UserDto 바꿈
    public UserDto UserEntityToDto(UserEntity userEntity,Integer likeCount) {
        return UserDto.builder()
                .payload(null)
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .userNamed(userEntity.getUserNamed())
                .nickname(userEntity.getNickname())
                .tel(userEntity.getTel())
                .departmentId(userEntity.getDepartmentId())
                .studentId(userEntity.getStudentId())
                .gender(userEntity.getGender())
                .roles(userEntity.getRoles())
                .loginType(userEntity.getLoginType())
                .imagePath(userEntity.getImagePath())
                .createAt(userEntity.getCreateAt())
                .modifiedAt(userEntity.getModifiedAt())
                .likeCount(likeCount)
                .build();
    }


}