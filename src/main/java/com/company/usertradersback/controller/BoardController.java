package com.company.usertradersback.controller;


import com.company.usertradersback.dto.board.BoardDetailDto;
import com.company.usertradersback.dto.board.BoardRequestDto;
import com.company.usertradersback.dto.board.BoardListDto;
import com.company.usertradersback.dto.board.BoardResponseDto;
import com.company.usertradersback.dto.category.BoardCategoryDto;
import com.company.usertradersback.dto.category.BoardCategoryListDto;
import com.company.usertradersback.dto.category.BoardSubCategoryDto;
import com.company.usertradersback.dto.category.BoardSubCategoryListDto;
import com.company.usertradersback.dto.comment.BoardChildCommentDto;
import com.company.usertradersback.dto.comment.BoardParentCommentDto;
import com.company.usertradersback.dto.declaration.BoardDeclarationDto;
import com.company.usertradersback.dto.like.BoardLikeListDto;
import com.company.usertradersback.dto.like.BoardLikeUserDto;
import com.company.usertradersback.entity.UserEntity;
import com.company.usertradersback.env.Url;
import com.company.usertradersback.payload.Payload;
import com.company.usertradersback.service.BoardService;
import com.company.usertradersback.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@CrossOrigin(origins = Url.url)
@RequestMapping(value = "/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private UserService userService;

    // 모든 게시물 리스트 조회
    // 게시물 리스트 정보 : BoardResponseDto(likeCount와 chatCount 추가),UserConciseDto,boardImageDto 중 썸네일
    @GetMapping(value = "/list/all")
    public ResponseEntity listAll() {

        try {
            List<BoardResponseDto> List = boardService.listAll();

            Payload payload = Payload.builder()
                    .message("전체 게시물 리스트 조회에 성공하였습니다.")
                    .isSuccess(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

            BoardListDto boardListDto = BoardListDto.builder()
                    .payload(payload)
                    .boardResponseDtoList(List)
                    .build();

            return new ResponseEntity<>(boardListDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("전체 게시물 리스트 조회에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/list/page") // 모든 게시물을 페이지 네이션 한 리스트 조회
    public ResponseEntity listAllInfinite(@RequestParam(value = "limit", defaultValue = "1") Integer limit) {
        // 여기서 요청 인자(RequestParam) limit는 page를 말한다.
        try {
            if (limit == null) {
                return new ResponseEntity<>("요청값에 limit값이 존재 하지 않습니다..", HttpStatus.BAD_REQUEST);
            }

            List<BoardResponseDto> List = boardService.listAllInfinite(limit);

            Payload payload = Payload.builder()
                    .message("해당 페이지 게시물 조회에 성공하였습니다.")
                    .isSuccess(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

            BoardListDto boardListDto = BoardListDto.builder()
                    .payload(payload)
                    .boardResponseDtoList(List)
                    .build();

            return new ResponseEntity<>(boardListDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("해당 페이지 게시물 조회에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 한 게시물 의 id 안에 들어 있는 정보를 조회
    //@PathVariable :url 파라 미터 값 id를 인자로 받음.
    // => 게시물 테이블 ok ,게시물 - 카테고리 ok, 게시물 - 이미지 ok, 게시물 - 회원 - 회원 닉네임 과 이미지 ok
    // => 게시물 댓글 ok , 게시물 대댓글 ok, 게시물 댓글 수 ok
    // => 게시물 좋아요 수 ok, 채팅 수 ok , 좋아요 인지 아닌지 ok
    // => 게시물 - 회원 - 평점 - 평균 ok
    // 토큰값으로 조회수 views 1증가 , 중복 증가 x
    @GetMapping(value = "/list/detail")
    public ResponseEntity listDetail(
                @RequestParam("id")Integer id
            , @RequestHeader("token") String token
            , @AuthenticationPrincipal UserEntity userEntity
    ) {
        try {
            if (id == null) {
                return new ResponseEntity<>("요청값에 게시물 id값이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
            } if (token == null) {
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {
                Payload payload = Payload.builder()
                        .message("해당 페이지 게시물 조회에 성공하였습니다.")
                        .isSuccess(true)
                        .httpStatus(HttpStatus.OK)
                        .build();
                BoardDetailDto boardDetailDto = BoardDetailDto.builder()
                        .payload(payload)
                        .boardResponseLoginDto(boardService.listDetail(id,userEntity.getId()))
                        .boardImageDto(boardService.listImage(id))
                        .build();

                return new ResponseEntity<>(boardDetailDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("해당 페이지 게시물 조회에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //keword를 받아서 게시물 제목을 비교하여 게시물 조회(검색)
    @GetMapping(value = "/search/title")
    public ResponseEntity searchTitleOrContent(@RequestParam String keyword) {

        try {
            List<BoardResponseDto> List = boardService.searchTitle(keyword);

            Payload payload = Payload.builder()
                    .message("제목으로 게시물 검색에 성공하였습니다.")
                    .isSuccess(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

            BoardListDto boardListDto = BoardListDto.builder()
                    .payload(payload)
                    .boardResponseDtoList(List)
                    .build();

            return new ResponseEntity<>(boardListDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("제목으로 게시물 검색에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //대분류 카테고리Id와 서브 카테고리 Id를 통한 게시물 조회(검색)
    @GetMapping(value = "/search/category")
    public ResponseEntity searchCategory(@RequestParam("categoryId") Integer categoryId
            , @RequestParam("subCategoryId") Integer subCategoryId
    ) {
        if (categoryId == null) {
            return new ResponseEntity<>("요청값에 categoryId가 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (subCategoryId == null) {
            return new ResponseEntity<>("요청값에 subCategoryId가 없습니다.", HttpStatus.BAD_REQUEST);
        }
        try {
            List<BoardResponseDto> List =
                    boardService.searchCategory(categoryId, subCategoryId);

            Payload payload = Payload.builder()
                    .message("카테고리로 게시물 검색에 성공하였습니다.")
                    .isSuccess(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

            BoardListDto boardListDto = BoardListDto.builder()
                    .payload(payload)
                    .boardResponseDtoList(List)
                    .build();

            return new ResponseEntity<>(boardListDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("카테고리로 게시물 검색에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //나의 게시물 전체 조회
    @GetMapping(value = "/list/my")//
    public ResponseEntity listMyBoards(@RequestHeader("token") String token,
                                       @AuthenticationPrincipal UserEntity userEntity) {
        try {
            if (token == null) {
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {
                List<BoardResponseDto>
                        List = boardService.listMyBoards(userEntity);

                Payload payload = Payload.builder()
                        .message("나의 게시물 조회에 성공하였습니다.")
                        .isSuccess(true)
                        .httpStatus(HttpStatus.OK)
                        .build();

                BoardListDto boardListDto = BoardListDto.builder()
                        .payload(payload)
                        .boardResponseDtoList(List)
                        .build();

                return new ResponseEntity<>(boardListDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("나의 게시물 조회에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 해당 userId를 가지는 회원의 전체 게시물
    @GetMapping(value = "/list/userId")
    public ResponseEntity listUserId(
            @RequestHeader("token") String token,
            @RequestParam(value = "userId") Integer userId) {
        try {

            if (userId == null) {
                return new ResponseEntity<>("요청값에 해당 userId가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (token == null) {
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {
                List<BoardResponseDto>
                        List = boardService.findAllByUserId(userId);

                Payload payload = Payload.builder()
                        .message("해당 " + userId + "번 회원님의 게시물 조회에 성공하였습니다.")
                        .isSuccess(true)
                        .httpStatus(HttpStatus.OK)
                        .build();

                BoardListDto boardListDto = BoardListDto.builder()
                        .payload(payload)
                        .boardResponseDtoList(List)
                        .build();

                return new ResponseEntity<>(boardListDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("해당 " + userId + "번 회원님의 게시물 조회에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //대분류 카테고리 조회
    @GetMapping(value = "/list/category")
    public ResponseEntity listSubCategoryId() {
        try {

            List<BoardSubCategoryDto> List = boardService.listSubCategoryId();

            Payload payload = Payload.builder()
                    .message("대분류 카테고리 조회에 성공하였습니다.")
                    .isSuccess(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

            BoardSubCategoryListDto boardListDto = BoardSubCategoryListDto.builder()
                    .payload(payload)
                    .boardSubCategoryDtoList(List)
                    .build();

            return new ResponseEntity<>(boardListDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("대분류 카테고리 조회에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //대분류 카테 고리 Id를 통해 서브 카테고리 조회
    @GetMapping(value = "/list/category/sub")
    public ResponseEntity listCategoryId(
            @RequestParam("subCategoryId") Integer subCategoryId
    ) {
        try {
            if (subCategoryId == null) {
                return new ResponseEntity<>("요청값에 subCategoryId가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            List<BoardCategoryDto> List = boardService.listCategoryId(subCategoryId);

            Payload payload = Payload.builder()
                    .message("서브 카테고리 조회에 성공하였습니다.")
                    .isSuccess(true)
                    .httpStatus(HttpStatus.OK)
                    .build();

            BoardCategoryListDto boardListDto = BoardCategoryListDto.builder()
                    .payload(payload)
                    .boardCategoryDtoList(List)
                    .build();

            return new ResponseEntity<>(boardListDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("서브 카테고리 조회에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //게시물 저장 , 등록
    //@RequestBody :HTTP 요청 몸체를 자바 객체로 변환
    //하지만!! 여기서는 요청 body가 formdata 이기 때문에 @RequsetBody를 사용하지않는다.
    @PostMapping(value = "/register")
    public ResponseEntity register(BoardRequestDto boardRequestDto
            , List<MultipartFile> files
            , @RequestHeader("token") String token
            , @AuthenticationPrincipal UserEntity user) {
        try {
            if (files.size() > 6) {
                return new ResponseEntity<>("이미지 파일은 5개 까지 가능합니다.", HttpStatus.BAD_REQUEST);
            }
            if (boardRequestDto.getTitle() == null) {
                return new ResponseEntity<>("게시물 제목을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if (boardRequestDto.getContent() == null) {
                return new ResponseEntity<>("게시물 내용을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if (boardRequestDto.getPrice() == null) {
                return new ResponseEntity<>("게시물 가격을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if (boardRequestDto.getCategoryId() == null) {
                return new ResponseEntity<>("요청값에 categoryId를 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if (boardRequestDto.getGrade() == null) {
                return new ResponseEntity<>("게시물 상품 등급을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if (token == null) {
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (files.get(0).getSize() == 0) {
                return new ResponseEntity<>("파일이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {
                boardService.register(boardRequestDto, files, user);

                Payload payload = Payload.builder()
                        .message("게시물 등록에 성공하였습니다.")
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
                    .message("게시물 등록에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // 한 게시물 수정
    @PatchMapping(value = "/update") // 한 게시물의 id 를 받아서 그 안에 들어 있는 게시물 정보 수정.
    public ResponseEntity update(
            BoardRequestDto boardRequestDto
            , List<MultipartFile> files
            , @RequestHeader("token") String token
            , @AuthenticationPrincipal UserEntity user
    ) {

        try {
            if (boardRequestDto.getTitle() == null) {
                return new ResponseEntity<>("게시물 제목을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if (boardRequestDto.getContent() == null) {
                return new ResponseEntity<>("게시물 내용을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if (boardRequestDto.getPrice() == null) {
                return new ResponseEntity<>("게시물 가격을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if (boardRequestDto.getCategoryId() == null) {
                return new ResponseEntity<>("요청값에 categoryId를 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if (boardRequestDto.getGrade() == null) {
                return new ResponseEntity<>("게시물 상품 등급을 입력해주세요.", HttpStatus.BAD_REQUEST);
            }
            if (token == null) {
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (files.get(0).getSize() == 0) {
                return new ResponseEntity<>("파일이 없습니다.", HttpStatus.BAD_REQUEST);
            }

            if (userService.validToken(token)) {
                boardService.update(files, boardRequestDto, user);

                Payload payload = Payload.builder()
                        .message(boardRequestDto.getId() + "번 게시물이 수정에 성공하였습니다.")
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
                    .message(boardRequestDto.getId() + "번 게시물이 수정에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 한 게시물 삭제(예정)
    @DeleteMapping(value = "/list/{id}")
    public ResponseEntity delete(
            @PathVariable("id") Integer id) {
        boardService.deleteById(id);
        return ResponseEntity.ok(id + "번 게시물이 삭제되었습니다.");
    }

    // 게시물 - 좋아요(장바구니) -유저
    @PostMapping(value = "/like")
    public ResponseEntity like(
            @RequestBody BoardLikeUserDto boardLikeUserDto
            , @RequestHeader("token") String token
            , @AuthenticationPrincipal UserEntity user
    ) {
        try {
            if (boardLikeUserDto.getBoardId() == null) {
                return new ResponseEntity<>("요청값에 해당 게시물 boardId 객체가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (token == null) {
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {
                boolean valid = boardService.like(user, boardLikeUserDto);

                if (valid) {
                    Payload payload = Payload.builder()
                            .message(boardLikeUserDto.getBoardId().getId() + "번 게시물을 장바구니에 담기 취소에 성공하였습니다.")
                            .isSuccess(true)
                            .httpStatus(HttpStatus.OK)
                            .build();

                    return new ResponseEntity<>(payload, HttpStatus.OK);
                } else {
                    Payload payload = Payload.builder()
                            .message(boardLikeUserDto.getBoardId().getId() + "번 게시물을 장바구니에 담기에 성공하였습니다.")
                            .isSuccess(true)
                            .httpStatus(HttpStatus.OK)
                            .build();
                    return new ResponseEntity<>(payload, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>("토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message(boardLikeUserDto.getBoardId().getId() + "번 게시물을 장바구니에 담기에  실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //내가 좋아하는 게시물 리스트
    @GetMapping(value = "/like/list")
    public ResponseEntity likeList(
            @RequestHeader("token") String token
            , @AuthenticationPrincipal UserEntity user
    ) {
        try {
            if (token == null) {
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {
                List<BoardLikeUserDto> List = boardService.likeList(user);

                Payload payload = Payload.builder()
                        .message("나의 장바구니 조회에 성공하였습니다.")
                        .isSuccess(true)
                        .httpStatus(HttpStatus.OK)
                        .build();
                BoardLikeListDto boardLikeListDto = BoardLikeListDto.builder()
                        .payload(payload)
                        .boardLikeUserDtoList(List)
                        .build();
                return new ResponseEntity<>(boardLikeListDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("나의 장바구니 조회에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //게시물 신고
    @PostMapping(value = "/declaration")
    public ResponseEntity declaration(
            @RequestBody BoardDeclarationDto boardDeclarationDto
            , @RequestHeader("token") String token
            , @AuthenticationPrincipal UserEntity user
    ) {
        try {
            if (boardDeclarationDto.getBoardId() == null) {
                return new ResponseEntity<>("요청값에 해당 게시물 boardId 객체가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (boardDeclarationDto.getContent() == null) {
                return new ResponseEntity<>("요청값에 해당 게시물 content(신고내용)가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (token == null) {
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {
                if (boardService.declarationVaild(user.getId(), boardDeclarationDto.getBoardId().getId()) >= 1) {
                    Payload payload = Payload.builder()
                            .message("해당 게시물은 이미 신고하였습니다.")
                            .isSuccess(true)
                            .httpStatus(HttpStatus.OK)
                            .build();
                    return new ResponseEntity<>(payload, HttpStatus.OK);
                } else {
                    boardService.declaration(user, boardDeclarationDto);
                    Payload payload = Payload.builder()
                            .message("해당 게시물 신고 저장에 성공하였습니다.")
                            .isSuccess(true)
                            .httpStatus(HttpStatus.OK)
                            .build();
                    return new ResponseEntity<>(payload, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>("토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("해당 게시물 신고 저장에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //댓글 저장
    @PostMapping(value = "/parent/comment")
    public ResponseEntity pComment(
            @RequestBody BoardParentCommentDto boardParentCommentDto
            , @RequestHeader("token") String token
            , @AuthenticationPrincipal UserEntity user
    ) {
        try {
            if (boardParentCommentDto.getBoardId() == null) {
                return new ResponseEntity<>("요청값에 해당 게시물 boardId 객체가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (boardParentCommentDto.getComment() == null) {
                return new ResponseEntity<>("요청값에 댓글 comment가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (token == null) {
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {

                boardService.pComment(user, boardParentCommentDto);
                Payload payload = Payload.builder()
                        .message("해당 게시물 댓글 저장에 성공하였습니다.")
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
                    .message("해당 게시물 댓글 저장에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //댓글 삭제 -> 댓글 삭제시 대댓글도 한번에 삭제됩니다.
    @DeleteMapping(value = "/parent/comment")
    public ResponseEntity pCommentDelete(
            @RequestParam("pCommentId") Integer id
            , @RequestHeader("token") String token
            , @AuthenticationPrincipal UserEntity user
    ) {
        try {
            if (id == null) {
                return new ResponseEntity<>("요청값에 해당 댓글 pCommentId가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (token == null) {
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {
                if (boardService.pCommentDeleteValid(user.getId(), id) >= 1) {
                    boardService.pCommentDelete(user, id);
                    Payload payload = Payload.builder()
                            .message("해당 게시물 댓글 삭제에 성공하였습니다.")
                            .isSuccess(true)
                            .httpStatus(HttpStatus.OK)
                            .build();

                    return new ResponseEntity<>(payload, HttpStatus.OK);
                } else {
                    Payload payload = Payload.builder()
                            .message("자기자신의 댓글만 삭제 할 수 있습니다.")
                            .isSuccess(true)
                            .httpStatus(HttpStatus.OK)
                            .build();

                    return new ResponseEntity<>(payload, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>("토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("해당 게시물 댓글 삭제에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //대댓글 저장
    @PostMapping(value = "/child/comment")
    public ResponseEntity cComment(
            @RequestBody BoardChildCommentDto boardChildCommentDto
            , @RequestHeader("token") String token
            , @AuthenticationPrincipal UserEntity user
    ) {
        try {
            if (boardChildCommentDto.getPcommentId().getId() == null) {
                return new ResponseEntity<>("요청값에 해당 댓글 pCommentId 객체가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (boardChildCommentDto.getComment() == null) {
                return new ResponseEntity<>("요청값에 댓글 comment가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (token == null) {
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {

                boardService.cComment(user, boardChildCommentDto);
                Payload payload = Payload.builder()
                        .message(boardChildCommentDto.getPcommentId().getId() + "번 댓글에 " +
                                "대댓글 저장을 성공하였습니다.")
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
                    .message(boardChildCommentDto.getPcommentId().getId() + "번 댓글에 " +
                            "대댓글 저장을 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //대댓글 삭제
    @DeleteMapping(value = "/child/comment")
    public ResponseEntity cCommentDelete(
            @RequestParam("cCommentId") Integer id
            , @RequestHeader("token") String token
            , @AuthenticationPrincipal UserEntity user
    ) {
        try {
            if (id == null) {
                return new ResponseEntity<>("요청값에 해당 댓글 cCommentId가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (token == null) {
                return new ResponseEntity<>("요청값에 토큰값이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            if (userService.validToken(token)) {
                if (boardService.cCommentDeleteValid(user.getId(), id) >= 1) {
                    boardService.cCommentDelete(user, id);
                    Payload payload = Payload.builder()
                            .message("해당 게시물 댓글 삭제에 성공하였습니다.")
                            .isSuccess(true)
                            .httpStatus(HttpStatus.OK)
                            .build();

                    return new ResponseEntity<>(payload, HttpStatus.OK);
                } else {
                    Payload payload = Payload.builder()
                            .message("자기자신의 댓글만 삭제 할 수 있습니다.")
                            .isSuccess(true)
                            .httpStatus(HttpStatus.OK)
                            .build();

                    return new ResponseEntity<>(payload, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>("토큰이 만료 되었습니다.", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Payload payload = Payload.builder()
                    .message("해당 게시물 댓글 삭제에 실패하였습니다.")
                    .isSuccess(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
            return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}