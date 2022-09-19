package com.todolist.domain.dto;

import java.time.LocalDateTime;

import com.todolist.domain.BoardType;
import com.todolist.domain.Card;
import com.todolist.exception.ExceptionType;
import com.todolist.exception.GlobalException;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CardCreateDto {

    @NotNull(message = "userId 값이 비었습니다.")
    private final Integer userId;

    @NotBlank(message = "카드 제목을 입력해주세요.")
    @Size(min = 1, max = 30)
    private final String cardTitle;

    @NotBlank(message = "카드 내용을 입력해주세요.")
    @Size(min = 1, max = 500)
    private final String cardContent;

    @NotBlank(message = "boardName 값이 비었습니다.")
    private final String boardName;

    private Long boardIdx;

    public CardCreateDto(Integer userId, String cardTitle, String cardContent, String boardName) {
        this.userId = userId;
        this.cardTitle = cardTitle;
        this.cardContent = cardContent;
        this.boardName = resolveBoardName(boardName);
    }

    public Card toCard() {
        return Card.builder()
            .userId(userId)
            .cardTitle(cardTitle)
            .cardContent(cardContent)
            .boardName(boardName)
            .boardIdx(boardIdx)
            .createdTime(LocalDateTime.now())
            .build();
    }

    private String resolveBoardName(String boardName) {
        for (BoardType value : BoardType.values()) {
            if (value.name().equals(boardName)) {
                return boardName;
            }
        }
        throw new GlobalException(ExceptionType.INVALID_BOARD_NAME);
    }

    public void setBoardIdx(Long boardIdx) {
        this.boardIdx = boardIdx;
    }
}
