package com.dalcho.adme.serviceImpl;

import com.dalcho.adme.domain.Registry;
import com.dalcho.adme.repository.RegistryRepository;
import com.dalcho.adme.service.Impl.RegistryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistryPagingTest {
    @Mock
    RegistryRepository registryRepository;
    @Mock
    Page<Registry> registryPage;
    @InjectMocks
    RegistryServiceImpl registryService;
    int curPage = 1;

    @Test
    void paging() throws Exception {
        when(registryRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(registryPage);
        registryService.getBoards(curPage);
        verify(registryRepository).findAllByOrderByCreatedAtDesc(any(Pageable.class));

        int count = registryService.getBoards(curPage).getCount();
        int start = registryService.getBoards(curPage).getStartPage();
        int end = registryService.getBoards(curPage).getEndPage();
        boolean prev = registryService.getBoards(curPage).isPrev();
        boolean next = registryService.getBoards(curPage).isNext();

        if(end >= count) {
            end = count;
            next = false;
        }

        if (prev) {
            assertThat(start).isGreaterThan(5);
            System.out.println("5페이지 이상이므로 이전 버튼이 생겼습니다.");
        }else{
            assertThat(start).isLessThan(6);
            System.out.println("5페이지 미만이므로 이전 버튼이 생기지 않았습니다.");
        }

        if (next) {
            assertThat(count).isGreaterThan(5);
            System.out.println("총 페이지 수가 5페이지 이상이므로 다음 버튼이 생겼습니다.");
        } else {
            assertThat(count).isLessThan(6);
            System.out.println("총 페이지 수가 5페이지 미만이므로 다음 버튼이 생기지 않았습니다.");
        }
    }
}
// https://stackoverflow.com/questions/57045711/how-to-mock-pageable-object-using-mockito