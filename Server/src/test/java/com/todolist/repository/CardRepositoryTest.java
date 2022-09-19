package com.todolist.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.todolist.domain.Card;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CardRepositoryTest {

    private final CardRepository cardRepository;

    @Autowired
    public CardRepositoryTest(NamedParameterJdbcTemplate jdbcTemplate) {
        this.cardRepository = new CardRepository(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 20; i++) {
            cardRepository.save(
                Card.builder()
                    .userId(1)
                    .cardTitle("title")
                    .cardContent("content")
                    .boardName("TODO")
                    .boardIdx((long)i)
                    .createdTime(LocalDateTime.now())
                    .build()
            );
        }
    }

    @DisplayName("userId에 해당하는 전체 카드 리스트를 찾는다.")
    @Test
    void find_card_by_user_Id() {
        List<Card> lists = cardRepository.findAll(1);
        Assertions.assertThat(lists.size()).isEqualTo(20);
    }

    @DisplayName("cardId에 해당하는 카드를 제거한다.")
    @Test
    void delete_card_by_cardId() {
        Card card = Card.builder()
            .userId(1)
            .cardTitle("title")
            .cardContent("content")
            .boardName("TODO")
            .boardIdx(1L)
            .createdTime(LocalDateTime.now())
            .build();

        Integer savedId = cardRepository.save(card);
        Integer deletedNumber = cardRepository.delete(savedId);
        Assertions.assertThat(deletedNumber).isEqualTo(1);
    }



}
