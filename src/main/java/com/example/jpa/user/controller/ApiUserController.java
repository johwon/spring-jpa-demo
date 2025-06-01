package com.example.jpa.user.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.jpa.board.entity.Board;
import com.example.jpa.board.entity.BoardComment;
import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.board.service.BoardService;
import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.notice.entity.Notice;
import com.example.jpa.notice.entity.NoticeLike;
import com.example.jpa.notice.model.NoticeResponse;
import com.example.jpa.notice.model.ResponseError;
import com.example.jpa.notice.repository.NoticeLikeRepository;
import com.example.jpa.notice.repository.NoticeRepository;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.exception.ExistEmailException;
import com.example.jpa.user.exception.PasswordNotMatchException;
import com.example.jpa.user.exception.UserNotFoundException;
import com.example.jpa.user.model.*;
import com.example.jpa.user.repository.UserRepository;
import com.example.jpa.user.service.PointService;
import com.example.jpa.util.JWTUtils;
import com.example.jpa.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.PushBuilder;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;

import static com.auth0.jwt.JWT.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ApiUserController {

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeLikeRepository noticeLikeRepository;

    private final BoardService boardService;
    private final PointService pointService;


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                 @RequestBody UserUpdate userUpdate, Errors errors) {

        List<ResponseError> responseErrorList = new ArrayList<>();

        if(errors.hasErrors()){
            errors.getAllErrors().forEach((e)->{
                responseErrorList.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("사용자 정보가 없습니다."));

        user.setPhone(userUpdate.getPhone());
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> UserNotFoundExceptionHandler (UserNotFoundException exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("사용자 정보가 없습니다."));

//        UserResponse userResponse = new UserResponse(user);
        UserResponse userResponse = UserResponse.of(user);

        return userResponse;

    }

    @GetMapping("/{id}/notice")
    public List<NoticeResponse> userNotice(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("사용자 정보가 없습니다."));

        List<Notice> noticeList = noticeRepository.findByUser(user);

        List<NoticeResponse> noticeResponsesList = new ArrayList<>();

        noticeList.stream().forEach((e)->{
            noticeResponsesList.add(NoticeResponse.of(e));
        });

        return noticeResponsesList;
    }

    private String getEncryptPassword(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody @Valid UserInput userInput, Errors errors){

        List<ResponseError> responseErrorList = new ArrayList<>();
        if(errors.hasErrors()){
            errors.getAllErrors().stream().forEach((e)->{
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        if(userRepository.countByEmail(userInput.getEmail())>0){
            throw new ExistEmailException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(userInput.getEmail())
                .userName(userInput.getUserName())
                .phone(userInput.getPhone())
                .password(getEncryptPassword(userInput.getPassword()))
                .regDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value= {ExistEmailException.class, PasswordNotMatchException.class})
    public ResponseEntity<?> ExceptionHandler(RuntimeException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 비밀번호 변경
    @Transactional
    @PatchMapping("/{id}/password")
    public ResponseEntity<?> updateUserPassword(@PathVariable Long id,
                                                @RequestBody UserInputPassword userInputPassword, Errors errors) {
        List<ResponseError> responseErrorList = new ArrayList<>();
        if(errors.hasErrors()){
            errors.getAllErrors().stream().forEach((e)->{
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByIdAndPassword(id, userInputPassword.getPassword())
                .orElseThrow(()->new PasswordNotMatchException("비밀번호가 일치하지 않습니다."));
        user.setPassword(userInputPassword.getNewPassword());

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    // 회원 탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("사용자 정보가 없습니다."));

        try {
            userRepository.delete(user);
        }catch (DataIntegrityViolationException e){
            String message = "제약조건에 문제가 발생하였습니다.";
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            String message = "회원 탈퇴 중 문제가 발생하였습니다.";
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().build();
    }

    // 회원 아이디(이메일) 찾기
    @GetMapping
    public ResponseEntity<?> findUser(@RequestBody UserInputFind userInputFind){
        User user = userRepository.findByUserNameAndPhone(userInputFind.getUserName(), userInputFind.getPhone())
                .orElseThrow(()->new UserNotFoundException("일치하는 회원이 없습니다."));

        return ResponseEntity.ok().body(user.getEmail());
    }

    private String getResetPassword() {
        return UUID.randomUUID().toString().replaceAll("-","").substring(0, 10);
    }

    // 비밀번호 초기화 요청
    @GetMapping("/{id}/password/reset")
    public ResponseEntity<?> resetUserPassword(@PathVariable Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("일치하는 회원이 없습니다."));

        // 비밀번호 초기화
        String resetPassword = getResetPassword();
        String resetEncryptPassword = getEncryptPassword(resetPassword);
        user.setPassword(resetEncryptPassword);
        userRepository.save(user);

        String message = String.format("[%s]님의 임시 비밀번호는 [%s] 입니다.",
                user.getUserName(), resetPassword);
        sendSMS(message);

        return ResponseEntity.ok().build();

    }

    void sendSMS(String message){
        System.out.println("[문자메시지전송]");
        System.out.println(message);
    }

    @GetMapping("/{id}/notice/like")
    public List<NoticeLike> likeNotice(@PathVariable Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("일치하는 회원이 없습니다."));

        List<NoticeLike> noticeLikeList = noticeLikeRepository.findByUser(user);

        return noticeLikeList;
    }

    // JWT 토큰 발행
    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestBody @Valid UserLogin userLogin, Errors errors){

        List<ResponseError> responseErrorList = new ArrayList<>();
        if(errors.hasErrors()){
            errors.getAllErrors().stream().forEach((e)->{
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(userLogin.getEmail())
                .orElseThrow(()->new UserNotFoundException("일치하는 회원이 없습니다."));

        if(!PasswordUtils.equalPassword(userLogin.getPassword(), user.getPassword())){
            throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다.");
        }

        LocalDateTime expiredDateTime = LocalDateTime.now().plusMonths(1);
        Date expiredDate = java.sql.Timestamp.valueOf(expiredDateTime);

        // 토큰 발행
        String token = JWT.create()
                .withExpiresAt(expiredDate)
                .withClaim("user_id",user.getId())
                .withSubject(user.getUserName())
                .withIssuer(user.getEmail())
                .sign(Algorithm.HMAC512("fastcampus".getBytes(StandardCharsets.UTF_8)));


        return ResponseEntity.ok().body(UserLoginToken.builder().token(token).build());
    }
    
    // 토큰 재발행
    @PatchMapping("/login")
    public ResponseEntity<?> refreshToken(@RequestHeader(name = "F-TOKEN") String token){

        String email = "";

        try {
            email = JWT.require(Algorithm.HMAC512("fastcampus".getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .verify(token)
                    .getIssuer();
        }catch (SignatureVerificationException e){
            throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new UserNotFoundException("사용자 정보가 없습니다."));

        LocalDateTime expiredDateTime = LocalDateTime.now().plusMonths(1);
        Date expiredDate = java.sql.Timestamp.valueOf(expiredDateTime);

        String newToken = JWT.create()
                .withExpiresAt(expiredDate)
                .withClaim("user_id",user.getId())
                .withSubject(user.getUserName())
                .withIssuer(user.getEmail())
                .sign(Algorithm.HMAC512("fastcampus".getBytes(StandardCharsets.UTF_8)));

        return ResponseEntity.ok().body(UserLoginToken.builder().token(newToken).build());

    }

    // 토큰 삭제
//    @DeleteMapping("/login")
//    public ResponseEntity<?> removeToken(@RequestHeader("F-TOKEN") String token){
//
//        String email = "";
//
//        try {
//            email = JWTUtils.getIssuer(token);
//        }catch (SignatureVerificationException e){
//            return new ResponseEntity<>("토큰 정보가 정확하지 않습니다.", HttpStatus.BAD_REQUEST);
//        }
//
//        return ResponseEntity.ok().build();
//    }


    // 내 게시글 조회
    @GetMapping("/board/post")
    public ResponseEntity<?> myPost(@RequestHeader("F-TOKEN") String token){

        String email = "";
        try {
            email = JWTUtils.getIssuer(token);
        }catch (SignatureVerificationException e){
            return ResponseResult.fail("토큰이 유효하지 않습니다.");
        }

        List<Board> list = boardService.postList(email);

        return ResponseResult.success(list);
    }

    // 내 코멘트 목록 조회
    @GetMapping("/board/comment")
    public ResponseEntity<?> myComments(@RequestHeader("F-TOKEN") String token){

        String email = "";
        try {
            email = JWTUtils.getIssuer(token);
        }catch (SignatureVerificationException e){
            return ResponseResult.fail("토큰이 유효하지 않습니다.");
        }

        List<BoardComment> list = boardService.commentList(email);

        return ResponseResult.success(list);
    }

    // 게시글 작성 시 포인트
    @PostMapping("/point")
    public ResponseEntity<?> userPoint(@RequestHeader("F-TOKEN") String token,
                                       @RequestBody UserPointInput userPointInput){
        String email = "";
        try {
            email = JWTUtils.getIssuer(token);
        }catch (SignatureVerificationException e){
            return ResponseResult.fail("토큰이 유효하지 않습니다.");
        }

        ServiceResult result = pointService.addPoint(email, userPointInput);
        return ResponseResult.result(result);
    }

}
