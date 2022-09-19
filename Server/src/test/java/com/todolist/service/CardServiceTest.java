package com.todolist.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import com.todolist.domain.Card;
import com.todolist.domain.dto.CardMoveDto;
import com.todolist.domain.dto.CardPatchDto;
import com.todolist.exception.NotFoundCardException;
import com.todolist.repository.CardRepository;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @DisplayName("카드 patch를 시도할 때 cardId가 없어 예외가 발생하면, NotFoundCardException으로 감싸서 발생한다.")
    @Test
    void patch_fail_if_no_cardId() {
        // given
        given(cardRepository.findCard(anyInt()))
            .willThrow(EmptyResultDataAccessException.class);

        // then
        Assertions.assertThrows(NotFoundCardException.class, () -> {
            cardService.patch(1, new CardPatchDto("title1", "content1"));
        });
    }

    @DisplayName("카드 patch 시도 시, cardId가 유효하다면 카드 patch가 정상적으로 호출된다.")
    @Test
    void patch_success_if_cardId_is_valid() {
        // given
        given(cardRepository.findCard(anyInt()))
            .willReturn(any(Card.class));

        // when
        cardService.patch(1, new CardPatchDto("title1", "content1"));

        // then
        verify(cardRepository, times(1)).patch(anyInt(), any(CardPatchDto.class));
    }

    @DisplayName("카드 move 시도 시 이전에 카드가 없었다면 cardRepository 대상으로 move 메서드만 호출된다.")
    @Test
    void move_only_if_no_exist_card() {
        // when
        cardService.move(new CardMoveDto(1, "TODO", -1));

        // then
        verify(cardRepository, times(1)).move(any(Card.class));
        verify(cardRepository, never()).findLastCardIdx(anyString());
        verify(cardRepository, never()).checkIfExistBeforeCard(anyString(), anyLong());
        verify(cardRepository, never()).updateIdxByBoardName(anyString());
    }

    @DisplayName("카드 move 시도 시 마지막 인덱스에 대한 요청이라면 cardRepository 대상으로 move 메서드만 호출된다.")
    @Test
    void move_request_if_last_index() {
        // when
        cardService.move(new CardMoveDto(1, "TODO", -2));

        // then
        verify(cardRepository, times(1)).move(any(Card.class));
        verify(cardRepository, times(1)).findLastCardIdx(anyString());
        verify(cardRepository, never()).checkIfExistBeforeCard(anyString(), anyLong());
        verify(cardRepository, never()).updateIdxByBoardName(anyString());
    }
}
