package com.example.jpa.user.service;

import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.model.UserLogCount;
import com.example.jpa.user.model.UserNoticeCount;
import com.example.jpa.user.model.UserSummary;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {

    UserSummary getUserStatusCount();

    List<User> getTodayUsers();

    List<UserNoticeCount> getUserNoticeCount();

    List<UserLogCount> getUserLogCount();

    // 좋아요를 가장 많이 한 회원 목록
    List<UserLogCount> getUserLikeBest();

    ServiceResult addInterestUser(Long id, String email);

    ServiceResult deleteInterestUser(Long interestId, String email);
}
