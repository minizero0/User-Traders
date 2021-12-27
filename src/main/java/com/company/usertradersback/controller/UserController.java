package com.company.usertradersback.controller;


import com.company.usertradersback.dto.department.UserDepartmentDto;
import com.company.usertradersback.dto.department.UserDepartmentListDto;
import com.company.usertradersback.dto.grades.UserGradesDto;
import com.company.usertradersback.dto.user.UserDto;
import com.company.usertradersback.dto.user.UserLoginDto;
import com.company.usertradersback.dto.user.UserTokenDto;
import com.company.usertradersback.dto.user.UserValidDto;
import com.company.usertradersback.dto.usercheck.UserEmailCheckDto;
import com.company.usertradersback.dto.usercheck.UserNicknameCheckDto;
import com.company.usertradersback.entity.UserEntity;
import com.company.usertradersback.env.Url;
import com.company.usertradersback.payload.Payload;
import com.company.usertradersback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = Url.url)
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {


    private final UserService userService;

    // 회원 토큰 값 유효성 검사
    @GetMapping(value = "/valid")
    public ResponseEntity validToken(@RequestParam("token") String token) {
        try {
            boolean valid = userService.validToken(token);
            Payload payload = Payload.builder()
                    .message("토큰 유효성 검사에 성공하였습니다.")
                    .isSuccess(true)
                    .httpStatus(HttpStatus.OK)
                    .build();
            UserValidDto userValidDto = UserValidDto.builder()
                    .payload(payload)
                    .valid(valid)
                    .build();
            return new ResponseEntity<>(userValidDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("토큰 유효성 검사에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String, String> user) {
        try {
            if(!user.containsKey("email")){
                return new ResponseEntity<>("이메일을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if(!user.containsKey("password")){
                return new ResponseEntity<>("비밀번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            //로그인, 반환값 token 및 로그인 실패 message

            Map<String,String> tokenAndMessage = userService.login(user); ;
            String token = tokenAndMessage.get("token");
            String message = tokenAndMessage.get("message");
            Integer userId;
            if(tokenAndMessage.get("userId")== ""){
                userId = null;
            }else {
                userId= Integer.parseInt(tokenAndMessage.get("userId"));
            }

            String email = tokenAndMessage.get("email");
            String nickname = tokenAndMessage.get("nickname");
            String imagePath = tokenAndMessage.get("imagePath");

            // 고정 응답값
            Payload payload = Payload.builder()
                    .message(message)
                    .isSuccess(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

            //응답값 + 로그인 반환값 token
            UserTokenDto userTokenDto = UserTokenDto.builder()
                    .payload(payload)
                    .token(token)
                    .user(UserLoginDto.builder()
                            .userId(userId)
                            .email(email)
                            .nickcname(nickname)
                            .imagePath(imagePath)
                            .build())
                    .build();

            return new ResponseEntity<>(userTokenDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("로그인에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // 로그 아웃은 서버에서 jwt 토큰을 삭제하지 않고 프론트 로컬 storage에
    // 들어있는 jwt를 지운다. 그리고 UserIsLogined 날짜 및 상태값 수정하여 DB 저장
    @PostMapping(value = "/logout")
    public ResponseEntity logout(@RequestHeader("token") String token
                                 , HttpServletRequest request, HttpServletResponse response
    ) {
        try {
            if(token == null){
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            userService.logout(token);
            new SecurityContextLogoutHandler()
                    .logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            Payload payload = Payload.builder()
                    .message("로그아웃에 성공하였습니다.")
                    .isSuccess(true)
                    .httpStatus(HttpStatus.OK)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("로그아웃에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //회원 가입
    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestBody UserDto userDto) {
        try {
            if(userDto.getEmail()==null){
                return new ResponseEntity<>("이메일을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if(userDto.getEmail().length()>25){
                return new ResponseEntity<>("이메일은 25자를 넘을 수 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if(userDto.getPassword()==null){
                return new ResponseEntity<>("비밀번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if(userDto.getPassword().length()<8){
                return new ResponseEntity<>("비밀번호는 8자 이상 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if(userDto.getUserNamed()==null){
                return new ResponseEntity<>("이름을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if(userDto.getNickname()==null){
                return new ResponseEntity<>("닉네임을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if(userDto.getNickname().length()>10){
                return new ResponseEntity<>("닉네임은 10자를 넘을 수 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if(userDto.getNickname().length()<2){
                return new ResponseEntity<>("닉네임은 2자 이상 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if(userDto.getDepartmentId()==null){
                return new ResponseEntity<>("학과번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if(userDto.getGender()==null){
                return new ResponseEntity<>("성별을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if(userDto.getLoginType()==null){
                return new ResponseEntity<>("로그인 유형을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            userService.register(userDto);
            Payload payload = Payload.builder()
                    .message("회원가입에 성공하였습니다.")
                    .isSuccess(true)
                    .httpStatus(HttpStatus.OK)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("회원가입에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //이메일 중복검사 API
    @GetMapping(value = "/email-check")
    public ResponseEntity emailCheck(
            @RequestParam("email") String email) {
        try {
            if(email == null){
                return new ResponseEntity<>("이메일을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            UserEmailCheckDto userEmailCheckDto
                    = UserEmailCheckDto.builder()
                    .check(userService.emailCheck(email))
                    .email(email)
                    .build();
            Payload payload = Payload.builder()
                    .message("이메일 검사에 성공하였습니다.")
                    .isSuccess(true)
                    .httpStatus(HttpStatus.OK)
                    .build();
            userEmailCheckDto.setPayload(payload);

            return new ResponseEntity<>(userEmailCheckDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("이메일 검사에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //닉네임 중복검사 API
    @GetMapping(value = "/nickname-check")
    public ResponseEntity nicknameCheck(
            @RequestParam("nickname") String nickname) {
        try {
            if(nickname == null){
                return new ResponseEntity<>("닉네임을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            UserNicknameCheckDto userNicknameCheckDto
                    = UserNicknameCheckDto.builder()
                    .check(userService.nickNameCheck(nickname))
                    .nickname(nickname)
                    .build();
            Payload payload = Payload.builder()
                    .message("닉네임 검사에 성공하였습니다.")
                    .isSuccess(true)
                    .httpStatus(HttpStatus.OK)
                    .build();
            userNicknameCheckDto.setPayload(payload);

            return new ResponseEntity<>(userNicknameCheckDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("닉네임 검사에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 회원 한명의 프로필 조회
    @GetMapping(value = "/profile") // 한 유저 상세 정보 단, 토큰 값이 있어야 가능
    public ResponseEntity profile(
            @RequestHeader("token") String token
    ) {
        try {
            if(token == null){
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {
                UserDto userDto = userService.profile(token);
                Payload payload = Payload.builder()
                        .message("프로필 조회에 성공하였습니다.")
                        .isSuccess(true)
                        .httpStatus(HttpStatus.OK)
                        .build();
                userDto.setPayload(payload);
                return new ResponseEntity<>(userDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("프로필 조회에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 회원 한명의 프로필 수정.
    @PatchMapping(value = "profile/update")
    public ResponseEntity profile(
            @RequestHeader("token") String token,
            UserDto userDto,
            List<MultipartFile> files
    ) {
        try {
//            if(userDto.getEmail()==null){
//                return new ResponseEntity<>("이메일을 입력해주세요.", HttpStatus.BAD_REQUEST);
//            }
//            if(userDto.getEmail().length()>25){
//                return new ResponseEntity<>("이메일은 25자를 넘을 수 없습니다.", HttpStatus.BAD_REQUEST);
//            }
//            if(userDto.getPassword()==null){
//                return new ResponseEntity<>("비밀번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
//            }
//            if(userDto.getPassword().length()<8){
//                return new ResponseEntity<>("비밀번호는 8자 이상 입력해주세요.", HttpStatus.BAD_REQUEST);
//            }
            if(userDto.getUserNamed()==null){
                return new ResponseEntity<>("이름을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
//            if(userDto.getNickname()==null){
//                return new ResponseEntity<>("닉네임을 입력해주세요.", HttpStatus.BAD_REQUEST);
//            }
//            if(userDto.getNickname().length()>10){
//                return new ResponseEntity<>("닉네임은 10자를 넘을 수 없습니다.", HttpStatus.BAD_REQUEST);
//            }
//            if(userDto.getNickname().length()<2){
//                return new ResponseEntity<>("닉네임은 2자 이상 입력해주세요.", HttpStatus.BAD_REQUEST);
//            }
//            if(userDto.getDepartmentId()==null){
//                return new ResponseEntity<>("학과번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
//            }
            if(userDto.getGender()==null){
                return new ResponseEntity<>("성별을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if(userDto.getLoginType()==null){
                return new ResponseEntity<>("로그인 유형을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if (files.get(0).getSize() == 0) {
                return new ResponseEntity<>("파일이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if(token == null){
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (files.get(0).getSize() == 0) {
                return new ResponseEntity<>("파일이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {

                userService.profileUpdate(userDto, token, files);

                Payload payload = Payload.builder()
                        .message("프로필 수정에 성공하였습니다.")
                        .isSuccess(true)
                        .httpStatus(HttpStatus.OK)
                        .build();
                return new ResponseEntity<>(payload, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("프로필 수정에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //회원 학과 전체 조회
    @GetMapping(value = "/list/department")
    public ResponseEntity listDepartment(){
        try {
               List<UserDepartmentDto> List
                       = userService.listDepartment();

                Payload payload = Payload.builder()
                        .message("전체 학과 조회에 성공하였습니다.")
                        .isSuccess(true)
                        .httpStatus(HttpStatus.OK)
                        .build();

                UserDepartmentListDto userDepartmentListDto
                        = UserDepartmentListDto.builder()
                        .payload(payload)
                        .userDepartmentDtoList(List)
                        .build();

                return new ResponseEntity<>(userDepartmentListDto, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("전체 학과 조회에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //회원 탈퇴 (예정)
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Integer id) {
        userService.deleteById(id);
        return ResponseEntity.ok("회원이 탈퇴 되었습니다.");
    }

    //회원 점수 부여,저장
    @PostMapping(value = "/grades")
    public ResponseEntity grades(
            @RequestBody UserGradesDto userGradesDto
            , @RequestHeader("token") String token
            , @AuthenticationPrincipal UserEntity user
    ){
        try {
            if(userGradesDto.getUserRecvId() == null){
                return new ResponseEntity<>("요청값에 userRecvId(받는 회원)객체가 없습니다.", HttpStatus.BAD_REQUEST);

            }
            if(userGradesDto.getGrade() == null){
                return new ResponseEntity<>("요청값에 grade가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if(userGradesDto.getGrade() > 5){
                return new ResponseEntity<>(" grade가 5점 초과 일 수 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if(userGradesDto.getGrade() < 0){
                return new ResponseEntity<>("grade가 0점 미만 일 수 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (token == null) {
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {
                    userService.grades(userGradesDto,user);
                    Payload payload = Payload.builder()
                            .message("해당 게시물 유저에게 점수 부여에 성공하였습니다.")
                            .isSuccess(true)
                            .httpStatus(HttpStatus.OK)
                            .build();

                    return new ResponseEntity<>(payload, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("해당 게시물 유저에게 점수 부여에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}