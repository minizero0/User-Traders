package com.company.usertradersback.service;

import com.company.usertradersback.config.jwt.JwtTokenProvider;
import com.company.usertradersback.config.s3.AwsS3;
import com.company.usertradersback.dto.department.UserDepartmentDto;
import com.company.usertradersback.dto.grades.UserGradesDto;
import com.company.usertradersback.dto.user.UserDto;
import com.company.usertradersback.entity.UserDepartmentEntity;
import com.company.usertradersback.entity.UserEntity;
import com.company.usertradersback.entity.UserGradesEntity;
import com.company.usertradersback.entity.UserIsLoginedEntity;
import com.company.usertradersback.exception.ApiIllegalArgumentException;
import com.company.usertradersback.repository.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserService implements UserDetailsService {
    // 여기서 @AutoWired 를쓰면 파라미터 값이 1개가 아니라서 안되고,
    // @RequiredArgsConstructor 써서 final 객체 부르면 스프링 빈 종속성 문제 에서 순환 참조 문제가 발생 된다.
    // 따라서 생성자 주입 해줄 때 @LAZY로 지연 로딩 시켜준다. 허나 이건 임시 방편이다.

    private final UserRepository userRepository;
    private final UserIsLoginedRepository userIsLoginedRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AwsS3 awsS3;
    private final UserDepartmentRepository userDepartmentRepository;
    private final UserGradesRepository userGradesRepository;
    private final BoardLikeUserRepository boardLikeUserRepository;

    public UserService(@Lazy UserRepository userRepository,
                       @Lazy UserIsLoginedRepository userIsLoginedRepository,
                       @Lazy JwtTokenProvider jwtTokenProvider,
                       @Lazy PasswordEncoder passwordEncoder,
                       @Lazy AwsS3 awsS3,
                       @Lazy UserDepartmentRepository userDepartmentRepository,
                       @Lazy UserGradesRepository userGradesRepository,
                       @Lazy BoardLikeUserRepository boardLikeUserRepository
    ) {
        this.userRepository = userRepository;
        this.userIsLoginedRepository = userIsLoginedRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.awsS3 = awsS3;
        this.userDepartmentRepository = userDepartmentRepository;
        this.userGradesRepository =userGradesRepository;
        this.boardLikeUserRepository = boardLikeUserRepository;
    }

//     Spring Security 필수 메소드 구현
//     @param userid 이메일 아이디
//     @return UserDetails
//     @throws UsernameNotFoundException 유저가 없을 때 예외 발생

    // Spring security 필수 메소드 구현
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 회원 로그인 , 한 회원 이메일, 비밀번호 조회
    @Transactional
    public Map<String,String> login(Map<String, String> user) {
        Map<String, String> map = new HashMap<>();

        if (userRepository.selectEmailCount(user.get("email")) >= 1){

            UserEntity userEntity = userRepository.findByEmail(user.get("email"))
                    .orElseThrow(() -> new ApiIllegalArgumentException("가입 되지 않은 email 입니다."));

        if (!passwordEncoder.matches(user.get("password"), userEntity.getPassword())) {
            map.put("token","");
            map.put("message","비밀번호를 잘못 입력 하셨습니다.");
            map.put("userId","");
            map.put("email","");
            map.put("nickname","");
            map.put("imagePath","");
            return map;
        }
//            UserLoginDto userLoginDto = UserLoginDto.builder()
//                    .email(userEntity.getEmail())
//                    .nickcname(userEntity.getNickname())
//                    .imagePath(userEntity.getImagePath())
//                    .build();

        int a = userIsLoginedRepository.checkId(userEntity.getId());

        if (a >= 1) {
            LocalDateTime logoutAt = userIsLoginedRepository.findByLogoutAt(userEntity.getId());
            userIsLoginedRepository.updateLoginAt(logoutAt, LocalDateTime.now(), userEntity.getId());
        } else {
            userIsLoginedRepository.save(
                    UserIsLoginedEntity.builder()
                            .id(userEntity.getId())
                            .status(1)
                            .loginAt(LocalDateTime.now())
                            .build()
            );
        }
        map.put("token",jwtTokenProvider.createToken(userEntity.getUsername(), userEntity.getRoles()));
        map.put("message","로그인에 성공하였습니다.");
        map.put("userId",userEntity.getId().toString());
        map.put("email",userEntity.getEmail());
        map.put("nickname",userEntity.getNickname());
        map.put("imagePath",userEntity.getImagePath());
            return map;
        }else {
            map.put("token","");
            map.put("message","가입 되지 않은 email 입니다.");
            map.put("userId","");
            map.put("email","");
            map.put("nickname","");
            map.put("imagePath","");
            return map;

        }

    }

    //회원 토큰 값 유효성 검사
    @Transactional
    public boolean validToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    //토큰값을 받아서 로그아웃
    @Transactional
    public String logout(String token) {
        String email = jwtTokenProvider.getUserPk(token);
        Integer id = this.selectId(email);
        LocalDateTime loginAt = userIsLoginedRepository.findByLoginAt(id);
        userIsLoginedRepository.updateLogoutAt(LocalDateTime.now(), loginAt, id);
        return "로그 아웃을 완료하였습니다.";
    }

    // 회원 가입, 회원 정보 저장
    @Transactional
    public Integer register(UserDto userDto) {
        return userRepository.save(UserEntity.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .userNamed(userDto.getUserNamed())
                .nickname(userDto.getNickname())
                .tel(userDto.getTel())
                .departmentId(userDto.getDepartmentId())
                .studentId(userDto.getStudentId())
                .gender(userDto.getGender())
                .loginType(userDto.getLoginType())
                .imagePath("https://usertradersbucket.s3.ap-northeast-2.amazonaws.com/basic/profile_img.gif")
                .createAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .roles(Collections.singletonList("일반회원"))
                .build()).getId();
    }


    @Transactional
    public String emailCheck(String email) {
        if (userRepository.selectEmailCount(email) >= 1)
            return "중복된 이메일 입니다.";
        else return "사용 가능한 이메일 입니다.";
    }

    @Transactional
    public String nickNameCheck(String nickname) {
        if (userRepository.selectNicknameCount(nickname) >= 1)
            return "중복된 닉네임 입니다.";
        else return "사용 가능한 닉네임 입니다.";
    }

    //unique한 email로 해당 회원 한명 프로필 정보 조회
    @Transactional
    public UserDto profile(String token) {
        String email = jwtTokenProvider.getUserPk(token);
        Optional<UserEntity> userEntityWrapper = userRepository.findByEmail(email);
        UserEntity userEntity = userEntityWrapper.get();
        Integer likeCount = boardLikeUserRepository.selectCountUserId(userEntity.getId());
        return UserDto.builder().build().UserEntityToDto(userEntity,likeCount);
    }

    // pk인 id로 해당 회원 한명 프로필 정보 조회
    @Transactional
    public UserDto findUserById(Integer id) {
        Optional<UserEntity> userEntityWrapper = userRepository.findById(id);
        UserEntity userEntity = userEntityWrapper.get();
        Integer likeCount = boardLikeUserRepository.selectCountUserId(userEntity.getId());
        return UserDto.builder().build().UserEntityToDto(userEntity,likeCount);
    }

    //token 값으로 구한 email로 pk,id 조회
    @Transactional
    public Integer selectId(String email) {
        return userRepository.findIdByEmail(email);
    }

    //token 값으로 id를 구하여 회원 한명 프로필 정보 조회 (복잡한 코드 사용x)
    @Transactional
    public UserDto findUserByToken(String token) {
        String email = jwtTokenProvider.getUserPk(token);
        Integer id = this.selectId(email);
        Optional<UserEntity> userEntityWrapper = userRepository.findById(id);
        UserEntity userEntity = userEntityWrapper.get();
        Integer likeCount = boardLikeUserRepository.selectCountUserId(userEntity.getId());
        return UserDto.builder().build().UserEntityToDto(userEntity,likeCount);
    }

    // user 객체로 회원 한명 프로필 정보 조회 (객체를 받는건 비효율,사용 x)
    @Transactional
    public UserDto findUserByUser(UserEntity user) {

        Optional<UserEntity> userEntityWrapper = userRepository.findById(user.getId());
        UserEntity userEntity = userEntityWrapper.get();
        Integer likeCount = boardLikeUserRepository.selectCountUserId(userEntity.getId());
        return UserDto.builder().build().UserEntityToDto(userEntity,likeCount);
    }

    // 변경할 회원 정보와 , 해당 회원의 pk인 id로 ,회원 한명 프로필 정보 수정
    @Transactional
    public Integer profileUpdate(UserDto userDto,
                                 String token,
                                 List<MultipartFile> files) throws ApiIllegalArgumentException {
        if (files.isEmpty()) {
            new ApiIllegalArgumentException("파일이 없습니다.");
        }
        String basePath = "profile/";

        //files에 담긴 originalFilename,contenttype,size를 담을 공간
        ArrayList<String> fileName = new ArrayList<String>();
        ArrayList<String> fileType = new ArrayList<String>();
        ArrayList<Long> fileLength = new ArrayList<Long>();

        try {
            for (int i = 0; i < files.size(); i++) {
                fileName.add(LocalDateTime.now().toString()
                        +"_"+files.get(i).getOriginalFilename());

                fileType.add(files.get(i).getContentType());
                fileLength.add(files.get(i).getSize());
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        // 업로드 될 버킷 객체 url
        String[] url  = new String[files.size()];

        String Cur_imagePath = this.findUserByToken(token).getImagePath();
        String del_imagePath_key ;

        if(!(Cur_imagePath
                == "https://usertradersbucket.s3.ap-northeast-2.amazonaws.com/basic/profile_img.gif" ||
        Cur_imagePath == null)
        ){
                del_imagePath_key = Cur_imagePath.split("/")[3]+"/"+Cur_imagePath.split("/")[4];
                awsS3.delete(del_imagePath_key);
        }

        //aws에 files에 담겨져온 이미지 파일을 업로드
        for (int i = 0; i < files.size(); i++) {
            try {
                url[i] = awsS3.upload(files.get(i), basePath + fileName.get(i)
                        , fileType.get(i), fileLength.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //token에서 해당 유저 정보중 변경 불가능 한것들
        Integer id = this.findUserByToken(token).getId();
        String email = this.findUserByToken(token).getEmail();
        String password = this.findUserByToken(token).getPassword();
        String nickname = this.findUserByToken(token).getNickname();
        UserDepartmentEntity departmentId = this.findUserByToken(token).getDepartmentId();
        LocalDateTime createAt = this.findUserByToken(token).getCreateAt();
        // 버킷 객체 url
        String imagePath
                = "https://usertradersbucket.s3.ap-northeast-2.amazonaws.com/"+url[0];

        //수정 시작
        Optional<UserEntity> userEntityWrapper = userRepository.findById(id);
        userEntityWrapper.ifPresent(userEntity -> {
            userEntity = UserEntity.builder()
                    .id(id)
                    .email(email)
                    .password(password)
                    .userNamed(userDto.getUserNamed())
                    .nickname(nickname)
                    .tel(userDto.getTel())
                    .departmentId(departmentId)
                    .studentId(userDto.getStudentId())
                    .gender(userDto.getGender())
                    .loginType(userDto.getLoginType())
                    .imagePath(imagePath)
                    .createAt(createAt)
                    .modifiedAt(LocalDateTime.now())
                    .build();
            userRepository.save(userEntity);
        });
        return userEntityWrapper.get().getId();
    }

    // 전체 학과 조회
    @Transactional
    public List<UserDepartmentDto> listDepartment() {
        List<UserDepartmentEntity> departmentEntityList = userDepartmentRepository.findAll();
        List<UserDepartmentDto> results = departmentEntityList.stream().map(departmentEntity -> {
            UserDepartmentDto departmentDto = UserDepartmentDto.builder()
                    .id(departmentEntity.getId())
                    .name(departmentEntity.getName())
                    .build();
            return departmentDto;
        }).collect(Collectors.toList());
        return results;
    }

    //회원 한명 정보 삭제
    @Transactional
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    //회원 점수 부여, 저장
    @Transactional
    public Integer grades(UserGradesDto userGradesDto,
                          UserEntity userEntity){
        return userGradesRepository.save(
                UserGradesEntity.builder()
                        .userSendId(userEntity)
                        .userRecvId(userGradesDto.getUserRecvId())
                        .grade(userGradesDto.getGrade())
                        .createAt(LocalDateTime.now())
                        .build()
        ).getId();

    }
    //해당 회원 총 점수 평균 값 조회


}
