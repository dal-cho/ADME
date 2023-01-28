package com.dalcho.adme.service;

import com.dalcho.adme.domain.User;
import com.dalcho.adme.dto.video.VideoRequestDto;
import com.dalcho.adme.dto.video.VideoResponseDto;
import com.dalcho.adme.dto.video.VideoResultDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VideoService {
    VideoResultDto uploadFile(User user, VideoRequestDto videoRequestDto, MultipartFile file, MultipartFile thumbnail) throws Exception;
    List<VideoResponseDto> getList(Pageable pageable) throws Exception;
    VideoResultDto update(Long id, VideoRequestDto videoRequestDto) throws IOException;
    void delete(Long id);
}
