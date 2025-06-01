package com.example.jpa.user.service;

import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.entity.UserPoint;
import com.example.jpa.user.model.UserPointInput;
import com.example.jpa.user.repository.UserPointRepository;
import com.example.jpa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;


    @Override
    public ServiceResult addPoint(String email, UserPointInput userPointInput) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            return ServiceResult.fail("사용자가 없습니다.");
        }
        User user = optionalUser.get();


        userPointRepository.save(UserPoint.builder()
                .user(user)
                .userPointType(userPointInput.getUserPointType())
                .point(userPointInput.getUserPointType().getValue() )
                .build());

        return ServiceResult.success();
    }
}
