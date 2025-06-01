package com.example.jpa.user.service;

import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.entity.UserInterest;
import com.example.jpa.user.model.UserLogCount;
import com.example.jpa.user.model.UserNoticeCount;
import com.example.jpa.user.model.UserStatus;
import com.example.jpa.user.model.UserSummary;
import com.example.jpa.user.repository.UserCustomRepository;
import com.example.jpa.user.repository.UserInterestRepository;
import com.example.jpa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCustomRepository userCustomRepository;
    private final UserInterestRepository userInterestRepository;

    @Override
    public UserSummary getUserStatusCount() {

        long usingCount = userRepository.countByStatus(UserStatus.Using);
        long stopCount = userRepository.countByStatus(UserStatus.Stop);
        long totalUserCount = userRepository.count();

        return UserSummary.builder()
                .usingUserCount(usingCount)
                .stopUserCount(stopCount)
                .totalUserCount(totalUserCount)
                .build();

    }

    @Override
    public List<User> getTodayUsers() {

        LocalDateTime t = LocalDateTime.now();

        LocalDateTime startDate = LocalDateTime.of(t.getYear(), t.getMonth(), t.getDayOfMonth(), 0, 0, 0);
        LocalDateTime endDate = startDate.plusDays(1);


        return userRepository.findToday(startDate, endDate);
    }

    @Override
    public List<UserNoticeCount> getUserNoticeCount() {

        return userCustomRepository.findUserNoticeCount();
    }

    @Override
    public List<UserLogCount> getUserLogCount() {return userCustomRepository.findUserLogCount();}

    @Override
    public List<UserLogCount> getUserLikeBest() { return userCustomRepository.findUserLikeBest();}

    @Override
    public ServiceResult addInterestUser(Long id, String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            return ServiceResult.fail("사용자가 없습니다.");
        }
        User user = optionalUser.get();

        Optional<User> optionalInterestUser = userRepository.findById(id);
        if(!optionalInterestUser.isPresent()){
            return ServiceResult.fail("관심사용자에 추가할 사용자가 없습니다.");
        }
        User interestUser = optionalInterestUser.get();

        // 내가 나를 추가하는 경우
        if(user.getId().equals(interestUser.getId())){
            return ServiceResult.fail("자신을 관심사용자에 추가할 수 없습니다.");
        }

        if(userInterestRepository.countByUserAndInterestUser(user, interestUser)>0){
            return ServiceResult.fail("이미 관심사용자에 등록되어 있습니다.");
        }

        UserInterest userInterest = UserInterest.builder()
                .user(user)
                .interestUser(interestUser)
                .regDate(LocalDateTime.now())
                .build();

        userInterestRepository.save(userInterest);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult deleteInterestUser(Long interestId, String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(!optionalUser.isPresent()){
            return ServiceResult.fail("사용자가 없습니다.");
        }
        User user = optionalUser.get();

        Optional<UserInterest> optionalUserInterest = userInterestRepository.findById(interestId);
        if(!optionalUserInterest.isPresent()){
            return ServiceResult.fail("관심사용자에 삭제할 사용자가 없습니다.");
        }

        UserInterest userInterest = optionalUserInterest.get();

        if(userInterest.getUser().getId()!=user.getId()){
            return ServiceResult.fail("본인의 관심자 정보만 삭제할 수 있습니다.");
        }

        userInterestRepository.delete(userInterest);
        return ServiceResult.success();
    }
}
