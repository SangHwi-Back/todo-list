package com.todolist.controller;

import static com.todolist.exception.ExceptionType.*;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todolist.domain.dto.CardAllShowDto;
import com.todolist.domain.dto.CardCreateDto;
import com.todolist.domain.dto.CardInformationDto;
import com.todolist.domain.dto.CardPatchDto;
import com.todolist.domain.dto.CardResponseDto;
import com.todolist.exception.GlobalException;
import com.todolist.service.CardService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/todolist")
@RestController
public class CardController {

    private static final Integer FIXED_USER_ID = 1;

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public CardAllShowDto read(HttpServletRequest request) {

        // debug = true일 경우 쿠키 체크 안하고 1번 내용 돌려주도록 적용
        String debug = request.getHeader("debug");
        if (debug != null && debug.equals("true")) {
            return new CardAllShowDto(cardService.findAllCards(FIXED_USER_ID));
        }

        Cookie[] cookies = Optional.ofNullable(request.getCookies())
            .orElseThrow(() -> new GlobalException(NO_COOKIE));

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("userId")) {
                return new CardAllShowDto(cardService.findAllCards(Integer.parseInt(cookie.getValue())));
            }
        }
        throw new GlobalException(NO_USER_ID_IN_COOKIE);
    }

    @PostMapping
    public ResponseEntity<CardResponseDto> save(@RequestBody CardCreateDto cardCreateDto) {
        Integer cardId = cardService.save(cardCreateDto);
        return new ResponseEntity<>(new CardResponseDto(cardId), HttpStatus.CREATED);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<CardResponseDto> delete(@PathVariable Integer cardId) {
        Integer deletedId = cardService.delete(cardId);
        return ResponseEntity.ok(new CardResponseDto(deletedId));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardInformationDto> findCard(@PathVariable Integer cardId) {
        CardInformationDto cardInformation = cardService.findCard(cardId);
        return ResponseEntity.ok(cardInformation);
    }

    @PatchMapping("/{cardId}")
    public ResponseEntity<CardResponseDto> patch(@PathVariable Integer cardId, @RequestBody CardPatchDto cardPatchDto) {
        cardService.patch(cardId, cardPatchDto);
        return ResponseEntity.ok(new CardResponseDto(cardId));
    }
}
