package com.github.kakaopayserver.repository.scatter;

import com.github.kakaopayserver.controller.scatter.Receiver;
import com.github.kakaopayserver.model.scatter.PickUp;
import com.github.kakaopayserver.model.scatter.Scatter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.github.kakaopayserver.util.DateTimeUtils.dateTimeOf;
import static com.github.kakaopayserver.util.DateTimeUtils.timestampOf;
import static java.util.Optional.ofNullable;

@Repository
public class JdbcScatterRepository implements ScatterRepository {

  private final JdbcTemplate jdbcTemplate;

  public JdbcScatterRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Scatter insert(Scatter scatter) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(conn -> {
      PreparedStatement ps = conn.prepareStatement("INSERT INTO scatters(seq,token,room_seq,user_seq,amount,receiver_count,create_at) VALUES (null,?,?,?,?,?,?)", new String[]{"seq"});
      ps.setString(1, scatter.getToken());
      ps.setString(2, scatter.getRoomId());
      ps.setLong(3, scatter.getUserId());
      ps.setLong(4, scatter.getCash());
      ps.setInt(5, scatter.getRecipientNum());
      ps.setTimestamp(6, timestampOf(scatter.getCreateAt()));
      return ps;
    }, keyHolder);

    Number key = keyHolder.getKey();
    long generatedSeq = key != null ? key.longValue() : -1;
    return new Scatter.Builder(scatter)
      .seq(generatedSeq)
      .build();
  }


  @Override
  public Optional<Scatter> findByTokenAndCreateAt(String token, LocalDateTime scatterCreateAt) {
    Scatter scatter = jdbcTemplate.query(
      "SELECT " +
        "s.*, " +
        "p.seq as picked_seq, p.scatter_seq as scatter_seq, p.amount as picked_cash, p.user_seq as picked_user_seq,  p.picked_at as picked_at, p.create_at as picked_create_at " +
        "FROM scatters s " +
        "LEFT JOIN pickups p ON s.seq=p.scatter_seq " +
        "WHERE s.create_at >= ? AND s.token = ?",
      joinMapper,
      new Object[]{timestampOf(scatterCreateAt), token});

    return ofNullable(scatter);
  }

  /*
   * JOIN한 테이블의 여러 레코드를 하나의 객체로 가져오기 위해 ResultSetExtractor 사용.
   * 코드가 복잡해지는 단점이 있으나 매 요청마다 쿼리 2번씩 날리는 것 보단 1번 날리는게 낫다고 판단
   */
@Override
  public Optional<Scatter> findByToken(String token) {
  Scatter scatter = jdbcTemplate.query(
      "SELECT " +
        "s.*, " +
        "p.seq as picked_seq, p.scatter_seq as scatter_seq, p.amount as picked_cash, p.user_seq as picked_user_seq,  p.picked_at as picked_at, p.create_at as picked_create_at " +
        "FROM scatters s " +
        "LEFT JOIN pickups p ON s.seq=p.scatter_seq " +
        "WHERE s.token = ?",
      joinMapper,
      token);

    return ofNullable(scatter);
  }

  static ResultSetExtractor<Scatter> joinMapper = rs -> {
    if (!rs.next()) {
      return null;
    }
    Scatter scatter = new Scatter.Builder()
      .seq(rs.getLong("seq"))
      .token(rs.getString("token"))
      .userId(rs.getLong("user_seq"))
      .roomId(rs.getString("room_seq"))
      .cash(rs.getLong("amount"))
      .recipientNum(rs.getInt("receiver_count"))
      .createAt(dateTimeOf(rs.getTimestamp("create_at")))
      .build();
    do {
      PickUp pickup = new PickUp.Builder()
        .seq(rs.getLong("picked_seq"))
        .scatterId(rs.getLong("scatter_seq"))
        .cash(rs.getLong("picked_cash"))
        .userId(rs.getObject("picked_user_seq", Long.class))
        .pickedAt(dateTimeOf(rs.getTimestamp("picked_at")))
        .createAt(dateTimeOf(rs.getTimestamp("picked_create_at")))
        .build();
      if (pickup.picked()) {
        scatter.getReceivers().add(new Receiver(pickup.getCash(), pickup.getUser_seq()));
        scatter.increaseDistribute(pickup.getCash());
      }
      scatter.getPickUps().add(pickup);
    } while (rs.next());
    return scatter;
  };
}
