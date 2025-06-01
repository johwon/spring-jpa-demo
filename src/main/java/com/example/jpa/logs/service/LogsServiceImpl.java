package com.example.jpa.logs.service;

import com.example.jpa.logs.entity.Logs;
import com.example.jpa.logs.repository.LogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogsServiceImpl implements LogsService{

    private final LogsRepository logsRepository;

    @Override
    public void add(String text) {

        logsRepository.save(Logs.builder()
                .text(text)
                .regDate(LocalDateTime.now())
                .build());
    }
}
