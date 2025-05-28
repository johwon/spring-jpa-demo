package com.example.jpa.user.controller;

import com.example.jpa.notice.repository.NoticeRepository;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.entity.UserLoginHistory;
import com.example.jpa.user.exception.UserNotFoundException;
import com.example.jpa.user.model.*;
import com.example.jpa.user.repository.UserLoginHistoryRepository;
import com.example.jpa.user.repository.UserRepository;
import com.example.jpa.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/user")
public class ApiAdminUserController {

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final UserLoginHistoryRepository userLoginHistoryRepository;

    private final UserService userService;

    /*
    // 사용자 목록과 사용자수 출력
    @GetMapping
    public ResponseMessage userList(){
        List<User> userList = userRepository.findAll();
        long totalUserCount = userRepository.count();

        return ResponseMessage.builder()
                .totalCount(totalUserCount)
                .data(userList)
                .build();
    }
    */


    @GetMapping("/{id}")
    public ResponseEntity<?> userDetail(@PathVariable Long id){

        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()){
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 없습니다."), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().body(ResponseMessage.success(user));
    }

    // 이메일, 연락처, 이름으로 회원 검색
    @GetMapping("/search")
    public ResponseEntity<?> findUser(@RequestBody UserSearch userSearch){

        // email like "%" || email || "%"
        // email like concat('%',email,'%')
        List<User> userList =
            userRepository.findByEmailContainsOrPhoneContainsOrUserNameContains(
                userSearch.getEmail(),
                userSearch.getPhone(),
                userSearch.getUserName());

        return ResponseEntity.ok().body(ResponseMessage.success(userList));
    }

    // 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> userStatus(@PathVariable Long id, @RequestBody UserStatusInput userStatusInput){

        Optional<User> optionalUser = userRepository.findById(id);
        if(!optionalUser.isPresent()){
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 없습니다."), HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        user.setStatus(userStatusInput.getStatus());
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    // 회원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){

        Optional<User> optionalUser = userRepository.findById(id);
        if(!optionalUser.isPresent()){
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 없습니다."), HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        if(noticeRepository.countByUser(user) > 0){
            return new ResponseEntity<>(ResponseMessage.fail("사용자가 작성한 공지사항이 있습니다."), HttpStatus.BAD_REQUEST);
        }

        userRepository.delete(user);

        return ResponseEntity.ok().build();
    }

    // 로그인 접속 이력 저장된다고 했을 때, 로그인 이력 조회
    @GetMapping("/login/history")
    public ResponseEntity<?> userLoginHistory(){

        List<UserLoginHistory> userLoginHistories = userLoginHistoryRepository.findAll();

        return ResponseEntity.ok().body(userLoginHistories);
    }

    // 회원 접속 제한
    @PatchMapping("/{id}/lock")
    public ResponseEntity<?> userLock(@PathVariable Long id){

        Optional<User> optionalUser = userRepository.findById(id);
        if(!optionalUser.isPresent()){
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 없습니다."), HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        if(user.isLockYn()){
            return new ResponseEntity<>(ResponseMessage.fail("이미 접속제한이 된 사용자입니다."), HttpStatus.BAD_REQUEST);
        }

        user.setLockYn(true);
        userRepository.save(user);

        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    // 회원 접속 제한 해제
    @PatchMapping("/{id}/unlock")
    public ResponseEntity<?> userUnLock(@PathVariable Long id){

        Optional<User> optionalUser = userRepository.findById(id);
        if(!optionalUser.isPresent()){
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 없습니다."), HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        if(!user.isLockYn()){
            return new ResponseEntity<>(ResponseMessage.fail("이미 접속제한이 해제된 사용자입니다."), HttpStatus.BAD_REQUEST);
        }

        user.setLockYn(false);
        userRepository.save(user);

        return ResponseEntity.ok().body(ResponseMessage.success());
    }


    // 회원 전체 수와 상태별 회원 수 조회
    @GetMapping("/status/count")
    public ResponseEntity<?> userStatusCount(){

        UserSummary userSummary = userService.getUserStatusCount();

        return ResponseEntity.ok().body(ResponseMessage.success(userSummary));
    }

    // 오늘 가입한 회원 목록
    @GetMapping("/today")
    public ResponseEntity<?> todayUser(){

        List<User> users = userService.getTodayUsers();

        return ResponseEntity.ok().body(ResponseMessage.success(users));
    }

    // 사용자별 공지사항 게시글 수 조회
    @GetMapping("/notice/count")
    public ResponseEntity<?> userNoticeCount(){

        List<UserNoticeCount> userNoticeCountList = userService.getUserNoticeCount();

        return ResponseEntity.ok().body(ResponseMessage.success(userNoticeCountList));
    }

    // 사용자별 공지사항 게시글 수, 좋아요 수 조회
    @GetMapping("/log/count")
    public ResponseEntity<?> userNoticeCountAndLikeCount(){

        List<UserLogCount> userLogCounts = userService.getUserLogCount();

        return ResponseEntity.ok().body(ResponseMessage.success(userLogCounts));
    }
    
    // 좋아요를 가장 많이 한 회원 목록(10개) 조회
    @GetMapping("/like/best")
    public ResponseEntity<?> bestLikeCount(){

        List<UserLogCount> userLogCounts = userService.getUserLikeBest();

        return ResponseEntity.ok().body(ResponseMessage.success(userLogCounts));
    }
}
