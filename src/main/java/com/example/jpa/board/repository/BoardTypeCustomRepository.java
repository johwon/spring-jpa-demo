package com.example.jpa.board.repository;

import com.example.jpa.board.model.BoardTypeCount;
import com.example.jpa.user.model.UserLogCount;
import com.example.jpa.user.model.UserNoticeCount;
import lombok.RequiredArgsConstructor;
import org.qlrm.mapper.JpaResultMapper;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class BoardTypeCustomRepository {

    private final EntityManager entityManager;

    public List<BoardTypeCount> getBoardTypeCount() {

        String sql = "select bt.id, bt.board_name, bt.reg_date, bt.using_yn," +
                "(select count(*) from board b where b.board_type_id = bt.id) as board_count " +
                "from board_type bt " +
                "where using_yn = 1";

//        List<BoardTypeCount> resultList = entityManager.createNativeQuery(sql).getResultList();
//        List<Object[]> result = entityManager.createNativeQuery(sql).getResultList();

        /*
        1.
        List<BoardTypeCount> resultList = result.stream().map(e-> new BoardTypeCount(e))
                .collect(Collectors.toList());

         */

        Query nativeQuery = entityManager.createNativeQuery(sql);
        JpaResultMapper jpaResultMapper = new JpaResultMapper();
        List<BoardTypeCount> resultList = jpaResultMapper.list(nativeQuery, BoardTypeCount.class);


        return resultList;
    }
}
