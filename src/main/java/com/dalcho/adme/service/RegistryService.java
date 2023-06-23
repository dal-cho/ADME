package com.dalcho.adme.service;

import com.dalcho.adme.domain.User;
import com.dalcho.adme.dto.registry.PagingDto;
import com.dalcho.adme.dto.registry.RegistryRequestDto;
import com.dalcho.adme.dto.registry.RegistryResponseDto;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface RegistryService {

    RegistryResponseDto postUpload(RegistryRequestDto registryDto, User user) throws IOException;

    // 작성 글 페이징
    PagingDto getBoards(int curPage);

    // 게시글 상세 보기
    RegistryResponseDto getIdxRegistry(Long idx);

    PagingDto myPage(int curPage, User user);

    void deleteRegistry(Long registryId, User user);
}
