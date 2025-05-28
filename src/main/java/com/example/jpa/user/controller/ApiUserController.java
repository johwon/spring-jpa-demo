package com.example.jpa.user.controller;

import com.example.jpa.notice.entity.Notice;
import com.example.jpa.notice.model.NoticeResponse;
import com.example.jpa.notice.model.ResponseError;
import com.example.jpa.notice.repository.NoticeRepository;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.exception.ExistEmailException;
import com.example.jpa.user.exception.PasswordNotMatchException;
import com.example.jpa.user.exception.UserNotFoundException;
import com.example.jpa.user.model.UserInput;
import com.example.jpa.user.model.UserInputPassword;
import com.example.jpa.user.model.UserResponse;
import com.example.jpa.user.model.UserUpdate;
import com.example.jpa.user.repository.UserRepository;
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

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ApiUserController {

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

//    @PostMapping
//    public ResponseEntity<?> addUser(@RequestBody @Valid UserInput userInput, Errors errors) {
//
//        List<ResponseError> responseErrorList = new ArrayList<>();
//
//        if(errors.hasErrors()){
//            errors.getAllErrors().forEach((e)->{
//                responseErrorList.add(ResponseError.of((FieldError) e));
//            });
//
//            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
//        }
//
//        return ResponseEntity.ok().build();
//    }

//    @PostMapping
//    public ResponseEntity<?> addUser(@RequestBody @Valid UserInput userInput, Errors errors) {
//
//        List<ResponseError> responseErrorList = new ArrayList<>();
//
//        if(errors.hasErrors()){
//            errors.getAllErrors().forEach((e)->{
//                responseErrorList.add(ResponseError.of((FieldError) e));
//            });
//
//            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
//        }
//
//        User user = User.builder()
//                .email(userInput.getEmail())
//                .userName(userInput.getUserName())
//                .password(userInput.getPassword())
//                .phone(userInput.getPhone())
//                .regDate(LocalDateTime.now())
//                .build();
//
//        userRepository.save(user);
//
//        return ResponseEntity.ok().build();
//    }

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

}
