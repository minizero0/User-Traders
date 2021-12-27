package com.company.usertradersback.service;

import com.company.usertradersback.config.s3.AwsS3;
import com.company.usertradersback.dto.board.BoardImageDto;
import com.company.usertradersback.dto.board.BoardRequestDto;
import com.company.usertradersback.dto.board.BoardResponseDto;
import com.company.usertradersback.dto.board.BoardResponseLoginDto;
import com.company.usertradersback.dto.category.BoardCategoryDto;
import com.company.usertradersback.dto.category.BoardSubCategoryDto;
import com.company.usertradersback.dto.comment.BoardChildCommentDto;
import com.company.usertradersback.dto.comment.BoardChildCommentResponseDto;
import com.company.usertradersback.dto.comment.BoardParentCommentResponseDto;
import com.company.usertradersback.dto.comment.BoardParentCommentDto;
import com.company.usertradersback.dto.declaration.BoardDeclarationDto;
import com.company.usertradersback.dto.like.BoardLikeUserDto;
import com.company.usertradersback.entity.*;
import com.company.usertradersback.exception.ApiIllegalArgumentException;
import com.company.usertradersback.exception.ApiNullPointerException;
import com.company.usertradersback.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardCategoryRepository boardCategoryRepository;

    @Autowired
    private BoardSubCategoryRepository boardSubCategoryRepository;

    @Autowired
    private BoardImageRepository boardImageRepository;

    @Autowired
    private BoardLikeUserRepository boardLikeUserRepository;

    @Autowired
    private BoardDeclaraionRepository boardDeclaraionRepository;

    @Autowired
    private BoardParentCommentRepository boardParentCommentRepository;

    @Autowired
    private BoardChildCommenRepository boardChildCommenRepository;

    @Autowired
    private BoardQueryDsl boardQueryDsl;

    @Autowired
    private UserGradesRepository userGradesRepository;

    @Autowired
    private AwsS3 awsS3;


    //게시물 전체 조회
    //java 8 버전부터 사용가능한 stream() -> 기존에 자바 컬렉션이나 배열의 원소를 for문 foreach문으로 하나씩 가공->
    //stream을 사용하여 람다 함수 형식으로 간결한 처리
    //stream은 map, filter , sorted 등 있고, 그 중 map을 통해 인덱스의 값 하나하나를 변환하여 최종 Collectors.toList() -> 리스트로 변환한다.
    @Transactional
    public List<BoardResponseDto> listAll() {
        //boardEntityList에 DB Board테이블에 있는 전부를 넣음.
        List<BoardEntity> boardEntityList = boardRepository.findAll();


        //List<BoardEntity> boardEntityList의 요소 하나하나인 BoardEntity boardEntity를 순서대로 변환(가공)
        //여기서 boardEntity는 인자로 사용.
        List<BoardResponseDto> results = boardEntityList.stream().map(boardEntity -> {
            BoardResponseDto boardResponseDto = BoardResponseDto.builder()
                    .build().convertEntityToDto(boardEntity
                            , boardLikeUserRepository.selectCountBoardId(boardEntity.getId())
                            , boardParentCommentRepository.selectCountBoardIdBCC(boardEntity.getId()) +
                                    boardParentCommentRepository.selectCountBoardIdBPC(boardEntity.getId())
                            , boardQueryDsl.selectPath(boardEntity.getId())
                            , boardQueryDsl.selectGrade(boardEntity.getUserId().getId())
                    );
            return boardResponseDto;
        }).collect(Collectors.toList());
        return results;


    }

    // 무한 스크롤 페이지 네이션
    @Transactional
    public List<BoardResponseDto> listAllInfinite(Integer limit) {
        //전체 게시물을 desc 순으로 (날짜)큰것 -> (날짜)작은것 순으로 3개씩 나눈다. page는 0부터 시작.
        //defaultValue가 1이기 때문에 , page는 limit - 1
        Page<BoardEntity> page = boardRepository.findAll(PageRequest.of(limit - 1, 3, Sort.by(Sort.Direction.DESC, "createAt")));
        List<BoardResponseDto> results = page.stream().map(boardEntity -> {
            BoardResponseDto boardResponseDto = BoardResponseDto.builder()
                    .build().convertEntityToDto(boardEntity, boardLikeUserRepository.selectCountBoardId(boardEntity.getId())
                            , boardParentCommentRepository.selectCountBoardIdBCC(boardEntity.getId()) +
                                    boardParentCommentRepository.selectCountBoardIdBPC(boardEntity.getId())
                            , boardQueryDsl.selectPath(boardEntity.getId())
                            , boardQueryDsl.selectGrade(boardEntity.getUserId().getId()));
            return boardResponseDto;
        }).collect(Collectors.toList());
        return results;
    }


    //게시물 id를 통해 게시물 목록 1개 조회
    // => 게시물 테이블 ,게시물 - 카테고리, 게시물 - 이미지, 게시물 - 회원 - 회원 닉네임과 이미지
    // => 게시물 댓글 , 게시물 대댓글, 게시물 댓글 수
    // => 게시물 좋아요 수
    // => 게시물 - 회원 - 평점
    @Transactional
    public BoardResponseLoginDto listDetail(Integer id, Integer userId) {
        BoardEntity boardEntity = boardRepository.findById(id)
                .orElseThrow(() -> new ApiIllegalArgumentException("해당되는 게시물 번호 " + id + " 값의 상세정보가 없습니다."));
        //session 확인으로 조회수 증가는 나중에 구현, 일단 상세정보 한번 볼때마다 views = views + 1
        boardRepository.saveViews(id);
        BoardResponseLoginDto board = BoardResponseLoginDto.builder().build().convertEntityToDto(boardEntity, boardLikeUserRepository.selectCountBoardId(boardEntity.getId())
                , boardParentCommentRepository.selectCountBoardIdBCC(boardEntity.getId()) +
                        boardParentCommentRepository.selectCountBoardIdBPC(boardEntity.getId())
                , boardQueryDsl.selectPath(boardEntity.getId())
                , boardQueryDsl.selectGrade(boardEntity.getUserId().getId())
                , boardQueryDsl.existLikeWhether(id, userId)
                , this.listComment(id, userId));
        return board;
    }

    //한 게시물안에 들어있는 부모댓글 조회 entity -> dto
    @Transactional
    public List<BoardParentCommentResponseDto> listComment(Integer boardId, Integer userId) {
        List<BoardParentCommentEntity> boardParentCommentEntityList = boardParentCommentRepository.findAllByBoardId_Id(boardId);
        List<BoardParentCommentResponseDto> result = boardParentCommentEntityList.stream().map(
                boardParentCommentEntity -> {
                    BoardParentCommentResponseDto boardParentCommentResponseDto = BoardParentCommentResponseDto.builder()
                            .build().convertEntityToDto(boardParentCommentEntity
                                    , this.listChildComment(boardParentCommentEntity.getId())
                            );
                    return boardParentCommentResponseDto;
                }).collect(Collectors.toList());
        return result;
    }

    // 부모댓글의 대댓글 조회 entity -> dto
    @Transactional
    public List<BoardChildCommentResponseDto> listChildComment(Integer pcommnetId) {
        List<BoardChildCommentEntity> boardChildCommentEntityList = boardChildCommenRepository.findAllByPcommentId_Id(pcommnetId);
        List<BoardChildCommentResponseDto> result = boardChildCommentEntityList.stream().map(
                boardChildCommentEntity -> {
                    BoardChildCommentResponseDto boardChildCommentResponseDto = BoardChildCommentResponseDto.builder()
                            .build().convertEntityToDto(boardChildCommentEntity);
                    return boardChildCommentResponseDto;
                }).collect(Collectors.toList());
        return result;
    }

    //검색 keyword를 통해 게시물 title에 keword가 속한 게시물 목록 조회
    @Transactional
    public List<BoardResponseDto> searchTitle(String keyword) {
        List<BoardEntity> boardEntityList = boardRepository.findByTitleContaining(keyword);
        List<BoardResponseDto> results = boardEntityList.stream().map(boardEntity -> {
            BoardResponseDto boardResponseDto = BoardResponseDto.builder()
                    .build().convertEntityToDto(boardEntity, boardLikeUserRepository.selectCountBoardId(boardEntity.getId())
                            , boardParentCommentRepository.selectCountBoardIdBCC(boardEntity.getId()) +
                                    boardParentCommentRepository.selectCountBoardIdBPC(boardEntity.getId())
                            , boardQueryDsl.selectPath(boardEntity.getId())
                            , boardQueryDsl.selectGrade(boardEntity.getUserId().getId()));
            return boardResponseDto;
        }).collect(Collectors.toList());
        return results;
    }

    //대분류 카테고리 + 서브카테고리를 통해 해당 게시물 전체 목록 조회
    @Transactional
    public List<BoardResponseDto> searchCategory(Integer categoryId, Integer subCategoryId) {
        List<BoardEntity> boardEntityList = boardRepository.selectAll(categoryId, subCategoryId);
        List<BoardResponseDto> results = boardEntityList.stream().map(boardEntity -> {
            BoardResponseDto boardResponseDto = BoardResponseDto.builder()
                    .build().convertEntityToDto(boardEntity, boardLikeUserRepository.selectCountBoardId(boardEntity.getId())
                            , boardParentCommentRepository.selectCountBoardIdBCC(boardEntity.getId()) +
                                    boardParentCommentRepository.selectCountBoardIdBPC(boardEntity.getId())
                            , boardQueryDsl.selectPath(boardEntity.getId())
                            , boardQueryDsl.selectGrade(boardEntity.getUserId().getId()));
            return boardResponseDto;
        }).collect(Collectors.toList());

        return results;
    }

    //회원 정보를 이용하여 해당 회원의 게시물 전체 목록 조회
    @Transactional
    public List<BoardResponseDto> listMyBoards(UserEntity userEntity) {
        if (userEntity == null) {
            throw new ApiNullPointerException("유저정보가 없습니다.");
        }
        List<BoardEntity> userBoardList = boardRepository.findAllByUserId_Id(userEntity.getId());
        List<BoardResponseDto> results = userBoardList.stream().map(boardEntity -> {
            BoardResponseDto boardResponseDto = BoardResponseDto.builder()
                    .build().convertEntityToDto(boardEntity, boardLikeUserRepository.selectCountBoardId(boardEntity.getId())
                            , boardParentCommentRepository.selectCountBoardIdBCC(boardEntity.getId()) +
                                    boardParentCommentRepository.selectCountBoardIdBPC(boardEntity.getId())
                            , boardQueryDsl.selectPath(boardEntity.getId())
                            , boardQueryDsl.selectGrade(boardEntity.getUserId().getId()));
            return boardResponseDto;
        }).collect(Collectors.toList());
        return results;
    }

    //해당 userId를 통해서 해당 회원의 게시물 전체 목록 조회
    @Transactional
    public List<BoardResponseDto> findAllByUserId(Integer userId) {

        List<BoardEntity> userBoardList = boardRepository.findAllByUserId_Id(userId);
        List<BoardResponseDto> results = userBoardList.stream().map(boardEntity -> {
            BoardResponseDto boardResponseDto = BoardResponseDto.builder()
                    .build().convertEntityToDto(boardEntity, boardLikeUserRepository.selectCountBoardId(boardEntity.getId())
                            , boardParentCommentRepository.selectCountBoardIdBCC(boardEntity.getId()) +
                                    boardParentCommentRepository.selectCountBoardIdBPC(boardEntity.getId())
                            , boardQueryDsl.selectPath(boardEntity.getId())
                            , boardQueryDsl.selectGrade(boardEntity.getUserId().getId()));
            return boardResponseDto;
        }).collect(Collectors.toList());
        return results;
    }

    //대분류 카테고리 조회
    @Transactional
    public List<BoardSubCategoryDto> listSubCategoryId() {
        List<BoardSubCategoryEntity> categoryEntityList = boardSubCategoryRepository.findAll();

        List<BoardSubCategoryDto> results = categoryEntityList.stream().map(categoryEntity -> {
            BoardSubCategoryDto categoryDto = BoardSubCategoryDto.builder()
                    .id(categoryEntity.getId())
                    .name(categoryEntity.getName())
                    .build();
            return categoryDto;
        }).collect(Collectors.toList());

        return results;
    }

    //서브 카테고리 조회
    @Transactional
    public List<BoardCategoryDto> listCategoryId(Integer subCategoryId) {
        List<BoardCategoryEntity> categoryEntityList = boardCategoryRepository.findAllBySubCategoryId_Id(subCategoryId);

        List<BoardCategoryDto> results = categoryEntityList.stream().map(categoryEntity -> {
            BoardCategoryDto categoryDto = BoardCategoryDto.builder()
                    .id(categoryEntity.getId())
                    .subCategoryId(categoryEntity.getSubCategoryId())
                    .name(categoryEntity.getName())
                    .build();
            return categoryDto;
        }).collect(Collectors.toList());

        return results;
    }

    //게시물 저장
    @Transactional
    public Integer register(BoardRequestDto boardRequestDto, List<MultipartFile> files, UserEntity user) { // 한 객체를 보드 테이블에 저장, 파일까지 저장

        String basePath = "board/";

        //files에 담긴 originalFilename,contenttype,size를 담을 공간
        ArrayList<String> fileName = new ArrayList<String>();
        ArrayList<String> fileType = new ArrayList<String>();
        ArrayList<Long> fileLength = new ArrayList<Long>();

        try {
            for (int i = 0; i < files.size(); i++) {
                fileName.add(LocalDateTime.now().toString()
                        + "_" + files.get(i).getOriginalFilename());
                fileType.add(files.get(i).getContentType());
                fileLength.add(files.get(i).getSize());
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        // 업로드 될 버킷 객체 url
        String[] url = new String[files.size()];

        // 버킷 객체 url , 데이터 베이스에 들어갈 url
        String[] imagePath = new String[files.size()];

        //aws에 files에 담겨져온 이미지 파일을 업로드
        for (int i = 0; i < files.size(); i++) {
            try {
                url[i] = awsS3.upload(files.get(i), basePath + fileName.get(i)
                        , fileType.get(i), fileLength.get(i));

                // 버킷 객체 url , 데이터 베이스에 들어갈 url
                imagePath[i]
                        = "https://usertradersbucket.s3.ap-northeast-2.amazonaws.com/" + url[i];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //게시물 정보 저장
        BoardEntity boardEntity = boardRepository.save(
                BoardEntity.builder()
                        .title(boardRequestDto.getTitle())
                        .userId(user)
                        .content(boardRequestDto.getContent())
                        .price(boardRequestDto.getPrice())
                        .categoryId(boardRequestDto.getCategoryId())
                        .views(0)
                        .grade(boardRequestDto.getGrade())
                        .status(boardRequestDto.getStatus())
                        .createAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .build());

        //게시물 이미지 정보 저장
        for (int i = 0; i < files.size(); i++) {
            boardImageRepository.save(BoardImageEntity.builder()
                    .boardId(boardEntity)
                    .path(imagePath[i])
                    .type(fileType.get(i))
                    .size(fileLength.get(i))
                    .createAt(boardEntity.getCreateAt())
                    .modifiedAt(boardEntity.getModifiedAt())
                    .build()
            ).getId();
        }
        //게시물 정보 저장 
        return boardEntity.getId();
    }

    //게시물 수정
    @Transactional
    public Integer update(List<MultipartFile> files, BoardRequestDto boardRequestDto,
                          UserEntity user) {

        String basePath = "board/";

        //files에 담긴 originalFilename,contenttype,size를 담을 공간
        ArrayList<String> fileName = new ArrayList<String>();
        ArrayList<String> fileType = new ArrayList<String>();
        ArrayList<Long> fileLength = new ArrayList<Long>();

        try {
            for (int i = 0; i < files.size(); i++) {
                fileName.add(LocalDateTime.now().toString()
                        + "_" + files.get(i).getOriginalFilename());
                fileType.add(files.get(i).getContentType());
                fileLength.add(files.get(i).getSize());
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }


        // 업로드 될 버킷 객체 url
        String[] url = new String[files.size()];
        System.out.println(boardRequestDto.getId());
        //현재 해당 유저가 가지고 있는 board의 id로 들어있는 이미지 경로
        List<String> Cur_imagePath = boardImageRepository.findByPath(boardRequestDto.getId());

        //원래 있었던 s3에 있는 이미지 삭제
        String[] del_imagePath_key = new String[Cur_imagePath.size()];

        for (int i = 0; i < Cur_imagePath.size(); i++) {
            if (!(Cur_imagePath.get(i) == null)
            ) {
                del_imagePath_key[i] = Cur_imagePath.get(i).split("/")[3] + "/" + Cur_imagePath.get(i).split("/")[4];
                awsS3.delete(del_imagePath_key[i]);
            }

        }
        // 버킷 객체 url , 데이터 베이스에 들어갈 url
        List<String> imagePath = new ArrayList<>();

        //aws에 files에 담겨져온 이미지 파일을 업로드
        for (int i = 0; i < files.size(); i++) {
            try {
                url[i] = awsS3.upload(files.get(i), basePath + fileName.get(i)
                        , fileType.get(i), fileLength.get(i));

                // 버킷 객체 url , 데이터 베이스에 들어갈 url
                imagePath.add(i, "https://usertradersbucket.s3.ap-northeast-2.amazonaws.com/" + url[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //게시물 정보 수정
        // 요청 받은 수정할 객체 정보를 건내받고 , 그 객체의 아이디를 뽑아서 수정전 정보를 wrapper에 담고
        // 수정 할 객체 정보를 수정전 객체 정보에 저장 ,  수정 끝
        //Optinal 클래스의 ifPresent 함수의 사용: 수정값에 null이 있는지 확인하는 if문을 줄이기 위함
        Optional<BoardEntity> boardEntityWrapper = boardRepository.findById(boardRequestDto.getId());
        boardEntityWrapper.ifPresent(boardEntity -> {
            boardEntity = BoardEntity.builder()
                    .id(boardRequestDto.getId())
                    .title(boardRequestDto.getTitle())
                    .userId(user)
                    .content(boardRequestDto.getContent())
                    .price(boardRequestDto.getPrice())
                    .categoryId(boardRequestDto.getCategoryId())
                    .views(boardRequestDto.getViews())
                    .grade(boardRequestDto.getGrade())
                    .status(boardRequestDto.getStatus())
                    .createAt(boardEntityWrapper.get().getCreateAt())
                    .modifiedAt(LocalDateTime.now())
                    .build();
            boardRepository.save(boardEntity);
        });


        //게시물 이미지 정보 수정 -> 해당 게시물 이미지 삭제 후 다시 저장
        //원래 게시물 이미지 삭제
        boardImageRepository.deleteAllByBoardId(boardRequestDto.getId());
        // 새로운 이미지 저장
        for (int i = 0; i < files.size(); i++) {
            boardImageRepository.save(BoardImageEntity.builder()
                    .boardId(boardEntityWrapper.get())
                    .path(imagePath.get(i))
                    .type(fileType.get(i))
                    .size(fileLength.get(i))
                    .createAt(boardEntityWrapper.get().getCreateAt())
                    .modifiedAt(boardEntityWrapper.get().getModifiedAt())
                    .build()
            ).getId();
        }

        return boardEntityWrapper.get().getId();
    }

    @Transactional
    public void deleteById(Integer id) {
        if (id == null) {
            throw new ApiNullPointerException("삭제하려는 게시물" + id + "아이디가 없습니다.");
        }
        boardRepository.deleteById(id);
    }

    // 유저 - 좋아요 - 게시물
    @Transactional
    public boolean like(UserEntity user, BoardLikeUserDto boardLikeUserDto) {
        if (boardLikeUserRepository.exist(user.getId(), boardLikeUserDto.getBoardId().getId()) >= 1) {
            Integer a = boardLikeUserRepository.deleteById(user.getId(), boardLikeUserDto.getBoardId().getId());
            if (a > 0) {
                return true;
            } else throw new ApiIllegalArgumentException("좋아요 취소 실패!!");

        } else {
            Integer a = boardLikeUserRepository.save(
                    BoardLikeUserEntity.builder()
                            .boardId(boardLikeUserDto.getBoardId())
                            .userId(user)
                            .createtAt(LocalDateTime.now())
                            .build()
            ).getId();

            if (a > 0) {
                return false;
            } else throw new ApiIllegalArgumentException("좋아요 실패!!");
        }
    }

    @Transactional
    //내가 좋아하는 게시물 리스트
    public List<BoardLikeUserDto> likeList(UserEntity user) {
        List<BoardLikeUserEntity> boardEntityList =
                boardLikeUserRepository.findAllByUserId_Id(user.getId());
        List<BoardLikeUserDto> results = boardEntityList.stream().map(boardLikeUserEntity -> {
            BoardLikeUserDto boardLikeUserDto = BoardLikeUserDto.builder()
                    .id(boardLikeUserEntity.getId())
                    .boardId(boardLikeUserEntity.getBoardId())
                    .boardImageDto(this.listImage(boardLikeUserEntity.getBoardId().getId()).get(0)
                    )
                    .build();
            return boardLikeUserDto;
        }).collect(Collectors.toList());
        return results;
    }

    @Transactional
    //해당 게시물 신고하기 , 저장
    public Integer declaration(UserEntity user, BoardDeclarationDto boardDeclarationDto) {
        return boardDeclaraionRepository.save(
                BoardDeclarationEntity.builder()
                        .boardId(boardDeclarationDto.getBoardId())
                        .userId(user)
                        .content(boardDeclarationDto.getContent())
                        .otherContent(boardDeclarationDto.getOtherContent())
                        .createAt(LocalDateTime.now())
                        .build()
        ).getId();
    }

    //해당 게시물 신고 중복 검사
    public Integer declarationVaild(Integer userId, Integer boardId) {
        return boardDeclaraionRepository.exist(userId, boardId);
    }

    @Transactional
    // 댓글 저장
    public Integer pComment(UserEntity user, BoardParentCommentDto boardParentCommentDto) {
        return boardParentCommentRepository.save(
                BoardParentCommentEntity.builder()
                        .userId(user)
                        .boardId(boardParentCommentDto.getBoardId())
                        .comment(boardParentCommentDto.getComment())
                        .createAt(LocalDateTime.now())
                        .build()
        ).getId();
    }

    @Transactional
    //댓글삭제
    public void pCommentDelete(UserEntity user, Integer id) {
        boardParentCommentRepository.deleteById(user, id);
    }

    @Transactional
    //해당 댓글이 내 댓글인지 검사,존재여부
    public Integer pCommentDeleteValid(Integer userId, Integer id) {
        return boardParentCommentRepository.exist(userId, id);
    }

    @Transactional
    // 대댓글 저장
    public Integer cComment(UserEntity user, BoardChildCommentDto boardChildCommentDto) {
        return boardChildCommenRepository.save(
                BoardChildCommentEntity.builder()
                        .userId(user)
                        .pcommentId(boardChildCommentDto.getPcommentId())
                        .comment(boardChildCommentDto.getComment())
                        .createAt(LocalDateTime.now())
                        .build()
        ).getId();
    }

    @Transactional
    //대댓글 삭제
    public void cCommentDelete(UserEntity user, Integer id) {
        boardChildCommenRepository.deleteById(user, id);
    }

    @Transactional
    //해당 대댓글이 내 댓글인지 검사,존재여부
    public Integer cCommentDeleteValid(Integer userId, Integer id) {
        return boardChildCommenRepository.exist(userId, id);
    }

    //해당 게시물의 들어있는 이미지 리스트
    @Transactional
    public List<BoardImageDto> listImage(Integer id) {
        List<BoardImageEntity> boardImageEntities = boardImageRepository.findAllByBoardId_Id(id);
        List<BoardImageDto> result = boardImageEntities.stream().map(
                boardImageEntity -> {
                    BoardImageDto boardImageDto = BoardImageDto.builder()
                            .build().convertEntityToDto(boardImageEntity);
                    return boardImageDto;
                }).collect(Collectors.toList());
        return result;
    }
}
