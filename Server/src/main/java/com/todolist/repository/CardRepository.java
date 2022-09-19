package com.todolist.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.todolist.domain.Card;
import com.todolist.domain.dto.CardPatchDto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class CardRepository {

    private static final long INTERVAL = 1000;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Card> findAll(Integer userId) {
        String sql = "SELECT * FROM card WHERE removed = FALSE AND userid = :userId ORDER BY boardidx";
        SqlParameterSource source = new MapSqlParameterSource("userId", userId);

        return namedParameterJdbcTemplate.query(sql, source, cardRowMapper());
    }

    public Integer delete(Integer cardId) {
        String sql = "UPDATE card SET removed = TRUE WHERE cardid = :cardId";
        SqlParameterSource source = new MapSqlParameterSource("cardId", cardId);
        return namedParameterJdbcTemplate.update(sql, source);
    }

    public Integer save(Card card) {
        String sql = "INSERT INTO card (userid, cardtitle, cardcontent, boardname, boardidx, createdtime) VALUES (:userId, :cardTitle, :cardContent, :boardName, :boardIdx, :createdTime)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(card), keyHolder,
            new String[] {"cardId"});
        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    public Integer move(Card card) {
        String sql = "UPDATE card SET boardname = :boardName, boardidx = :boardIdx WHERE cardid = :cardId";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("boardName", card.getBoardName());
        parameterSource.addValue("boardIdx", card.getBoardIdx());
        parameterSource.addValue("cardId", card.getCardId());

        namedParameterJdbcTemplate.update(sql, parameterSource);
        return card.getCardId();
    }

    public Card findCard(Integer cardId) {
        String sql = "SELECT * FROM card WHERE cardid = :cardId";
        SqlParameterSource source = new MapSqlParameterSource("cardId", cardId);

        return namedParameterJdbcTemplate.queryForObject(sql, source, cardRowMapper());
    }

    public void patch(Integer cardId, CardPatchDto cardPatchDto) {
        String sql = "UPDATE card SET cardtitle = :cardTitle, cardcontent = :cardContent WHERE cardid = :cardId";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("cardId", cardId);
        parameterSource.addValue("cardTitle", cardPatchDto.getCardTitle());
        parameterSource.addValue("cardContent", cardPatchDto.getCardContent());

        namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    public Long findLastCardIdx(String boardName) {
        String sql = "SELECT MAX(boardidx) FROM card WHERE boardname = :boardName";
        SqlParameterSource namedParameter = new MapSqlParameterSource("boardName", boardName);

        return namedParameterJdbcTemplate.queryForObject(sql, namedParameter, Long.class);
    }

    /**
     * batch update - 삭제 예정
     */
    public void updateIdxByBoardName(String movedBoardName) {
        String sql = "UPDATE card SET boardIdx = :boardIdx where cardId = :cardId";

        List<Card> cards = findAllByBoardName(movedBoardName);

        long idx = INTERVAL;
        List<MapSqlParameterSource> params = new ArrayList<>();
        for (Card card : cards) {
            MapSqlParameterSource source = new MapSqlParameterSource();
            source.addValue("boardIdx", idx);
            source.addValue("cardId", card.getCardId());
            params.add(source);
            idx += INTERVAL;
        }
        namedParameterJdbcTemplate.batchUpdate(sql, params.toArray(MapSqlParameterSource[]::new));
    }

    /**
     * no-batch update
     */
    public void updateIdxWithNoBatch(String movedBoardName) {
        String sql = "UPDATE card SET boardidx = (boardidx * 1000) WHERE boardname = :boardName";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("boardName", movedBoardName);
        namedParameterJdbcTemplate.update(sql, source);
    }

    public boolean checkIfExistBeforeCard(String movedBoardName, long boardIdx) {
        String sql = "SELECT * FROM card WHERE boardname = :boardName AND boardidx = :boardIdx";
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("boardName", movedBoardName);
        source.addValue("boardIdx", boardIdx);

        try {
            namedParameterJdbcTemplate.queryForObject(sql, source, cardRowMapper());
            return true;
        } catch (EmptyResultDataAccessException exception) {
            return false;
        }
    }

    public List<Card> findAllByBoardName(String boardName) {
        String sql = "SELECT * FROM card WHERE removed = FALSE AND boardname = :boardName ORDER BY boardidx";
        SqlParameterSource source = new MapSqlParameterSource("boardName", boardName);

        return namedParameterJdbcTemplate.query(sql, source, cardRowMapper());
    }

    private RowMapper<Card> cardRowMapper() {
        return (resultSet, rowNum) ->
            Card.builder()
                .cardId(resultSet.getInt("cardId"))
                .userId(resultSet.getInt("userId"))
                .cardTitle(resultSet.getString("cardTitle"))
                .cardContent(resultSet.getString("cardContent"))
                .boardName(resultSet.getString("boardName"))
                .boardIdx(resultSet.getLong("boardIdx"))
                .createdTime(resultSet.getTimestamp("createdTime").toLocalDateTime())
                .build();
    }
}
