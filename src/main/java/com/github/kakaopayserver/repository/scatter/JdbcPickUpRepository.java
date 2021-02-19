package com.github.kakaopayserver.repository.scatter;

import com.github.kakaopayserver.model.scatter.PickUp;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.github.kakaopayserver.util.DateTimeUtils.dateTimeOf;
import static com.github.kakaopayserver.util.DateTimeUtils.timestampOf;
import static java.time.LocalDateTime.now;
import static java.util.Optional.ofNullable;

@Repository
public class JdbcPickUpRepository implements PickUpRepository {

  @Value("${batchSize}")
  private int batchSize;

  private final JdbcTemplate jdbcTemplate;

  public JdbcPickUpRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public PickUp insert(PickUp pickUp) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(conn -> {
      PreparedStatement ps = conn.prepareStatement("INSERT INTO pickups(seq, scatter_seq, amount, user_seq, picked_at, create_at) VALUES (null,?,?,null,null,?)", new String[]{"seq"});
      ps.setLong(1, pickUp.getScatter_seq());
      ps.setLong(2, pickUp.getCash());
      ps.setTimestamp(3, timestampOf(pickUp.getCreateAt()));
      return ps;
    }, keyHolder);

    Number key = keyHolder.getKey();
    long generatedSeq = key != null ? key.longValue() : -1;
    return new PickUp.Builder(pickUp)
      .seq(generatedSeq)
      .build();
  }

  @Override
  public void update(PickUp pickUp) {
    jdbcTemplate.update(
      "UPDATE pickups SET user_seq=?, picked_at=? WHERE seq=?",
      pickUp.getUser_seq(),
      pickUp.getPickedAt(),
      pickUp.getSeq()
    );
  }

  @Override @Transactional
  public void batchInsert(List<Long> cashes, long scatterId) {
    List<List<Long>> batchLists = Lists.partition(cashes, batchSize);

    for(List<Long> batch : batchLists) {
      jdbcTemplate.batchUpdate("INSERT INTO pickups(seq, scatter_seq, amount, user_seq, picked_at, create_at) VALUES (null,?,?,null,null,?)",
        new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i)
          throws SQLException {
          Long cash = batch.get(i);
          ps.setLong(1, scatterId);
          ps.setLong(2, cash);
          ps.setTimestamp(3, timestampOf(now()));
        }

        @Override
        public int getBatchSize() {
          return batch.size();
        }
      });
    }
  }

  @Override
  public Optional<List<PickUp>> findByScatterId(Long scatterId) {
    List<PickUp> pickUps = jdbcTemplate.query(
      "SELECT * FROM pickups p WHERE p.scatter_seq=?",
      new Object[]{scatterId},
      mapper
    );
    return ofNullable(pickUps);
  }

  static RowMapper<PickUp> mapper = (rs, rowNum) -> new PickUp.Builder()
    .seq(rs.getLong("seq"))
    .scatterId(rs.getLong("scatter_seq"))
    .cash(rs.getLong("amount"))
    .userId(rs.getObject("user_seq", Long.class)) // null을 받아오기 위해 getLong()대신 getObject 사용 / getLong()은 null이면 0으로 반환
    .pickedAt(dateTimeOf(rs.getTimestamp("picked_at")))
    .createAt(dateTimeOf(rs.getTimestamp("create_at")))
    .build();
}
