package com.example.jpa.board.service;

import com.example.jpa.board.entity.*;
import com.example.jpa.board.model.*;
import com.example.jpa.board.repository.*;
import com.example.jpa.common.exception.BizException;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardTypeRepository boardTypeRepository;
    private final BoardTypeCustomRepository boardTypeCustomRepository;
    private final BoardRepository boardRepository;
    private final BoardHitsRepository boardHitsRepository;
    private final UserRepository userRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardBadReportRepository boardBadReportRepository;
    private final BoardScrapRepository boardScrapRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final BoardCommentRepository boardCommentRepository;

    @Override
    public ServiceResult addBoard(BoardTypeInput boardTypeInput) {

        BoardType boardType = boardTypeRepository.findByBoardName(boardTypeInput.getName());
        if(boardType != null && boardTypeInput.getName().equals(boardType.getBoardName())){
            return ServiceResult.fail("이미 존재하는 게시판입니다.");
        }

        BoardType addBoardType = BoardType.builder()
                .boardName(boardTypeInput.getName())
                .regDate(LocalDateTime.now())
                .build();
        boardTypeRepository.save(addBoardType);

        return ServiceResult.success();

    }

    @Override
    public ServiceResult updateBoard(Long id, BoardTypeInput boardTypeInput) {

        Optional<BoardType> optionalBoardType = boardTypeRepository.findById(id);
        if(!optionalBoardType.isPresent()){
            return ServiceResult.fail("수정할 게시판 타입이 없습니다.");
        }
        BoardType updateBoardType = optionalBoardType.get();

        BoardType boardType = boardTypeRepository.findByBoardName(boardTypeInput.getName());
        if(boardType != null && boardTypeInput.getName().equals(boardType.getBoardName())){
            return ServiceResult.fail("이미 존재하는 게시판입니다.");
        }


        updateBoardType.setBoardName(boardTypeInput.getName());
        updateBoardType.setUpdateDate(LocalDateTime.now());
        boardTypeRepository.save(updateBoardType);

        return ServiceResult.success();

    }

    @Override
    public ServiceResult deleteBoard(Long id) {

        Optional<BoardType> optionalBoardType = boardTypeRepository.findById(id);
        if(!optionalBoardType.isPresent()){
            return ServiceResult.fail("삭제할 게시판 타입이 없습니다.");
        }
        BoardType boardType = optionalBoardType.get();

        if(boardRepository.countByBoardType(boardType)>0){
            return ServiceResult.fail("해당 게시판에 게시물이 존재합니다. 게시물을 모두 삭제 후 다시 시도해주세요.");
        };

        boardTypeRepository.deleteById(id);
        return ServiceResult.success();
    }

    @Override
    public List<BoardType> getAllBoardType() {
        return boardTypeRepository.findAll();
    }

    @Override
    public ServiceResult setBoardTypeUsing(Long id, BoardTypeUsing boardTypeUsing) {
        Optional<BoardType> optionalBoardType = boardTypeRepository.findById(id);
        if(!optionalBoardType.isPresent()){
            return ServiceResult.fail("게시판 타입이 없습니다.");
        }

        BoardType boardType = optionalBoardType.get();

        boardType.setUsingYn(boardTypeUsing.isUsingYn());
        boardTypeRepository.save(boardType);

        return ServiceResult.success();
    }

    @Override
    public List<BoardTypeCount> getBoardTypeCount() {

        return boardTypeCustomRepository.getBoardTypeCount();
    }

    @Override
    public ServiceResult setBoardTop(Long id, boolean topYn) {

        Optional<Board> optionalBoard = boardRepository.findById(id);
        if(!optionalBoard.isPresent()){
            return ServiceResult.fail("게시물이 없습니다.");
        }

        Board board = optionalBoard.get();
        if(board.isTopYn()==topYn){
            if (topYn) {
                return ServiceResult.fail("이미 최상단 게시물입니다.");
            } else{
                return ServiceResult.fail("이미 최상단 배치가 해제된 게시물입니다.");
            }
        }


        board.setTopYn(topYn);
        boardRepository.save(board);

        return ServiceResult.success();

    }

    @Override
    public ServiceResult setBoardPeriod(Long id, BoardPeriod boardPeriod) {

        Optional<Board> optionalBoard = boardRepository.findById(id);
        if(!optionalBoard.isPresent()){
            return ServiceResult.fail("게시물이 없습니다.");
        }
        Board board = optionalBoard.get();

        board.setPublishStartDate(boardPeriod.getStartDate());
        board.setPublishEndDate(boardPeriod.getEndDate());
        boardRepository.save(board);

        return ServiceResult.success();

    }

    @Override
    public ServiceResult setBoardHits(Long id, String email) {

        Optional<Board> optionalBoard = boardRepository.findById(id);
        if(!optionalBoard.isPresent()){
            return ServiceResult.fail("게시물이 없습니다.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            return ServiceResult.fail("사용자가 없습니다.");
        }
        User user = optionalUser.get();

        if(boardHitsRepository.countByBoardAndUser(board, user)>0){
            return ServiceResult.fail("이미 조회수가 증가되었습니다.");
        }

        boardHitsRepository.save(BoardHits.builder()
                .board(board)
                .user(user)
                .regDate(LocalDateTime.now())
                .build());
        return ServiceResult.success();

    }

    @Override
    public ServiceResult setBoardLike(Long id, String email) {

        Optional<Board> optionalBoard = boardRepository.findById(id);
        if(!optionalBoard.isPresent()){
            return ServiceResult.fail("게시물이 없습니다.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            return ServiceResult.fail("사용자가 없습니다.");
        }
        User user = optionalUser.get();

        if(boardLikeRepository.countByBoardAndUser(board, user)>0){
            return ServiceResult.fail("이미 좋아요한 게시글입니다.");
        }

        boardLikeRepository.save(BoardLike.builder()
                .board(board)
                .user(user)
                .regDate(LocalDateTime.now())
                .build());

        return ServiceResult.success();
    }

    @Override
    public ServiceResult setBoardUnLike(Long id, String email) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if(!optionalBoard.isPresent()){
            return ServiceResult.fail("게시물이 없습니다.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            return ServiceResult.fail("사용자가 없습니다.");
        }
        User user = optionalUser.get();

        Optional<BoardLike> optionalBoardLike = boardLikeRepository.findByBoardAndUser(board, user);

        if(!optionalBoardLike.isPresent()){
            return ServiceResult.fail("좋아요를 누르지 않은 게시물입니다.");
        }

        BoardLike boardLike = optionalBoardLike.get();

        boardLikeRepository.delete(boardLike);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult addBadReport(Long id, String email, BoardBadReportInput boardBadReportInput) {

        Optional<Board> optionalBoard = boardRepository.findById(id);
        if(!optionalBoard.isPresent()){
            return ServiceResult.fail("게시물이 없습니다.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            return ServiceResult.fail("사용자가 없습니다.");
        }
        User user = optionalUser.get();

        BoardBadReport boardBadReport = BoardBadReport.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .userEmail(user.getEmail())

                .boardId(board.getId())
                .boardUserId(board.getUser().getId())
                .boardTitle(board.getTitle())
                .boardContents(board.getContents())
                .boardRegDate(board.getRegDate())

                .comments(boardBadReportInput.getComments())
                .regDate(LocalDateTime.now())
                .build();

        boardBadReportRepository.save(boardBadReport);

        return ServiceResult.success();
    }

    @Override
    public List<BoardBadReport> getBadReportList(){ return boardBadReportRepository.findAll();}

    @Override
    public ServiceResult scrap(Long id, String email) {

        Optional<Board> optionalBoard = boardRepository.findById(id);
        if(!optionalBoard.isPresent()){
            return ServiceResult.fail("게시물이 없습니다.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            return ServiceResult.fail("사용자가 없습니다.");
        }
        User user = optionalUser.get();

        BoardScrap boardScrap = BoardScrap.builder()
                .user(user)
                .boardTypeId(board.getBoardType().getId())
                .boardId(board.getId())
                .boardTitle(board.getTitle())
                .boardContents(board.getContents())
                .boardRegDate(board.getRegDate())
                .regDate(LocalDateTime.now())
                .build();

        boardScrapRepository.save(boardScrap);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult deleteScrap(Long id, String email) {
        Optional<BoardScrap> optionalBoardScrap = boardScrapRepository.findById(id);
        if(!optionalBoardScrap.isPresent()){
            return ServiceResult.fail("스크랩이 없습니다.");
        }
        BoardScrap boardScrap = optionalBoardScrap.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            return ServiceResult.fail("사용자가 없습니다.");
        }
        User user = optionalUser.get();

        // 내 스크랩인지 확인 필요
        if(user.getId()!=(boardScrap.getUser().getId())){
            return ServiceResult.fail("본인의 스크랩만 삭제할 수 있습니다.");
        }

        boardScrapRepository.delete(boardScrap);
        return ServiceResult.success();

    }


    private String getBoardUrl(long boardId){
        return String.format("/board/%d", boardId);
    }

    @Override
    public ServiceResult addBookmark(Long id, String email) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if(!optionalBoard.isPresent()){
            return ServiceResult.fail("게시물이 없습니다.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            return ServiceResult.fail("사용자가 없습니다.");
        }
        User user = optionalUser.get();

        BoardBookmark boardBookmark = BoardBookmark.builder()
                .user(user)
                .boardId(board.getId())
                .boardTypeId(board.getBoardType().getId())
                .boardTitle(board.getTitle())
                .boardUrl(getBoardUrl(board.getId()))
                .regDate(LocalDateTime.now())
                .build();

        boardBookmarkRepository.save(boardBookmark);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult deleteBookmark(Long id, String email) {

        Optional<BoardBookmark> optionalBoardBookmark = boardBookmarkRepository.findById(id);
        if(!optionalBoardBookmark.isPresent()){
            return ServiceResult.fail("삭제할 북마크가 없습니다.");
        }
        BoardBookmark boardBookmark = optionalBoardBookmark.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            return ServiceResult.fail("사용자가 없습니다.");
        }
        User user = optionalUser.get();

        // 내 북마크인지 확인 필요
        if(user.getId()!=(boardBookmark.getUser().getId())){
            return ServiceResult.fail("본인의 북마크만 삭제할 수 있습니다.");
        }
        
        boardBookmarkRepository.delete(boardBookmark);
        return ServiceResult.success();

    }

    @Override
    public List<Board> postList(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            throw new BizException("사용자가 없습니다.");
        }
        User user = optionalUser.get();

        List<Board> boardList = boardRepository.findByUser(user);
        return boardList;
    }

    @Override
    public List<BoardComment> commentList(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            throw new BizException("사용자가 없습니다.");
        }
        User user = optionalUser.get();

        List<BoardComment> list = boardCommentRepository.findByUser(user);
        return list;
    }

    @Override
    public Board detail(Long id) {

        Optional<Board> optionalBoard = boardRepository.findById(id);
        if(!optionalBoard.isPresent()){
            throw new BizException("게시물이 없습니다.");
        }
       return optionalBoard.get();

    }

}
