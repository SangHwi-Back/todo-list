package com.todolist.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.todolist.domain.dto.CardMoveDto;
import com.todolist.exception.ExceptionType;
import com.todolist.exception.GlobalException;
import com.todolist.exception.NotFoundCardException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.todolist.domain.Card;
import com.todolist.domain.dto.CardCreateDto;
import com.todolist.domain.dto.CardInformationDto;
import com.todolist.domain.dto.CardPatchDto;
import com.todolist.repository.CardRepository;

@Service
public class CardService {

    private static final int NO_CARD_IN_BOARD = -1;
    private static final int INPUT_REQUEST_LAST_INDEX = -2;
    private static final int BEFORE = 1;
    private static final long INTERVAL = 1000;

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Map<String, List<CardInformationDto>> findAllCards(Integer userId) {
        List<Card> allCards = cardRepository.findAll(userId);

        if (allCards.size() == 0) {
            throw new GlobalException(ExceptionType.NO_USER_FOUNDED);
        }

        return allCards.stream()
            .map(CardInformationDto::of)
            .collect(Collectors.groupingBy(CardInformationDto::getBoardName));
    }

    public Integer save(CardCreateDto cardCreateDto) {
        Long idx = cardRepository.findLastCardIdx(cardCreateDto.getBoardName());
        if (idx == null) {
            cardCreateDto.setBoardIdx(1000L);
            return cardRepository.save(cardCreateDto.toCard());
        }
        cardCreateDto.setBoardIdx(idx + 1000);
        return cardRepository.save(cardCreateDto.toCard());
    }

    public void delete(Integer cardId) {
        Integer deletedValue = cardRepository.delete(cardId);
        if (deletedValue == 0) {
            throw new GlobalException(ExceptionType.NO_FOUND_CARD);
        }
    }

    public void patch(Integer cardId, CardPatchDto cardPatchDto) {
        findCard(cardId);
        cardRepository.patch(cardId, cardPatchDto);
    }

    public Integer move(CardMoveDto cardMoveDto) {

        Integer nextCardId = cardMoveDto.getNextCardId();
        String movedBoardName = cardMoveDto.getMovedBoardName();

        if (nextCardId == NO_CARD_IN_BOARD) {
            return cardRepository.move(cardMoveDto.toCard(INTERVAL));
        }
        if (nextCardId == INPUT_REQUEST_LAST_INDEX) {
            Long idx = cardRepository.findLastCardIdx(movedBoardName);
            return cardRepository.move(cardMoveDto.toCard(idx + INTERVAL));
        }

        Card nextCard = findCard(nextCardId);
        Long boardIdxOfNextCard = nextCard.getBoardIdx();

        if (cardRepository.checkIfExistBeforeCard(movedBoardName, boardIdxOfNextCard - BEFORE)) {
            cardRepository.updateIdxWithNoBatch(movedBoardName);
            nextCard = findCard(nextCardId);
        }

        return cardRepository.move(cardMoveDto.toCard(nextCard.getBoardIdx() - BEFORE));
    }

    public CardInformationDto findCardInformation(Integer cardId) {
        Card card = findCard(cardId);
        return CardInformationDto.of(card);
    }

    private Card findCard(Integer cardId) {
        Card card;
        try {
            card = cardRepository.findCard(cardId);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            throw new NotFoundCardException(ExceptionType.NO_FOUND_CARD, "cardId");
        }
        return card;
    }
}
