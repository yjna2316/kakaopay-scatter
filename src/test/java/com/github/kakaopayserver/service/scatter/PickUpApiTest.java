package com.github.kakaopayserver.service.scatter;

import com.github.kakaopayserver.error.BadRequestException;
import com.github.kakaopayserver.model.scatter.PickUp;
import com.github.kakaopayserver.model.scatter.Scatter;
import com.github.kakaopayserver.repository.scatter.PickUpRepository;
import com.github.kakaopayserver.repository.scatter.ScatterRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PickUpApiTest {

  @Autowired
  private ScatterService scatterService;

  @Autowired
  private ScatterRepository scatterRepository;

  @Autowired
  private PickUpRepository pickUpRepository;

  private Long userId;

  private String roomId;

  private String token;

  private Scatter scatter;

  @BeforeAll
  void setUp() {
    userId = 100L;
    roomId = "room01";
    scatter = scatterService.scatter(new Scatter(userId, roomId, 10000L, 3));
    token = scatter.getToken();
  }

  @Test
  @Order(1)
  void 성공_돈을_줍는다() {
    // given
    LocalDateTime startAt = now();
    Long reqUserId = 901L;
    String reqRoomId = roomId;


    // when
    PickUp pickup = scatterService.pickup(reqUserId, reqRoomId, token).orElse(null);

    // then
    assertThat(pickup, is(notNullValue()));
    assertThat(pickup.getUser_seq(), is(reqUserId));
    assertThat(pickup.getScatter_seq(), is(scatter.getSeq()));
    assertTrue(pickup.getPickedAt().isAfter(startAt) && pickup.getPickedAt().isBefore(now()));
  }



  @Test
  @Order(2)
  void 실패_유저한명당_뿌린기별로_한번만_받아갈_수_있다() {
    // given
    Long reqUserId = 901L;
    String reqRoomId = roomId;

    // when then
    Assertions.assertThrows(BadRequestException.class, () -> scatterService.pickup(reqUserId, reqRoomId, token));
  }

  @Test
  @Order(3)
  void 실패_본인이_생성한_뿌린기는_받아갈수_없다() {
    // given
    Long reqUserId = userId;
    String reqRoomId = roomId;

    // when then
    Assertions.assertThrows(BadRequestException.class, () -> scatterService.pickup(reqUserId, reqRoomId, token));
  }

  @Test
  @Order(4)
  void 실패_뿌린기가_호출된_대화방과_동일한_방에_속한_사용자만_받아갈_수_있다() {
    // given
    Long reqUserId = 801L;
    String reqRoomId = "otherRoom";

    // when then
    Assertions.assertThrows(BadRequestException.class, () -> scatterService.pickup(reqUserId, reqRoomId, token));
  }

  @Test
  @Order(5)
  void 실패_뿌린건은_10분간만_유효하다() {
    // given
    Long reqUserId = 801L;
    String reqRoomId = roomId;
    String expiredToken = "zzz";

    Scatter expired = scatterRepository.insert(new Scatter.Builder()
      .userId(userId)
      .roomId(roomId)
      .cash(100L)
      .recipientNum(1)
      .token(expiredToken)
      .createAt(scatter.getCreateAt().minusMinutes(10))
      .build());

    pickUpRepository.insert(new PickUp.Builder()
      .scatterId(expired.getSeq())
      .cash(expired.getCash())
      .build());

    // when then
    Assertions.assertThrows(BadRequestException.class, () -> scatterService.pickup(reqUserId, reqRoomId, expiredToken));

  }

  @Test
  @Order(6)
  void 실패_분배가_모두_끝난_뿌린기는_받아갈수_없다() {
    // given
    Long reqUserId = 701L;
    String reqRoomId = roomId;
    Scatter newScatter = scatterService.scatter(new Scatter(userId, roomId, 1000L, 2));
    String newToken = newScatter.getToken();

    // when
    scatterService.pickup(11L, reqRoomId, newToken);
    scatterService.pickup(12L, reqRoomId, newToken);

    // then
    Assertions.assertThrows(BadRequestException.class, () -> scatterService.pickup(reqUserId, reqRoomId, newToken));

  }
}
