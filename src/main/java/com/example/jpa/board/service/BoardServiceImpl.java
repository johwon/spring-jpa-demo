package com.example.jpa.board.service;

import com.example.jpa.board.entity.BoardType;
import com.example.jpa.board.model.BoardTypeCount;
import com.example.jpa.board.model.BoardTypeInput;
import com.example.jpa.board.model.BoardTypeUsing;
import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.board.repository.BoardRepository;
import com.example.jpa.board.repository.BoardTypeCustomRepository;
import com.example.jpa.board.repository.BoardTypeRepository;
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

//        boardTypeRepository.
//        boardRepository.findByBoardType

        return boardTypeCustomRepository.getBoardTypeCount();
    }

}
