package com.github.kakaopayserver.service.scatter;

import com.github.kakaopayserver.controller.scatter.Receiver;
import com.github.kakaopayserver.error.BadRequestException;
import com.github.kakaopayserver.model.scatter.PickUp;
import com.github.kakaopayserver.model.scatter.Scatter;
import com.github.kakaopayserver.repository.scatter.PickUpRepository;
import com.github.kakaopayserver.repository.scatter.ScatterRepository;
import org.junit.After;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatusApiTest {
  @Autowired
  private ScatterService scatterService;

  @Autowired
  private ScatterRepository scatterRepository;

  @Autowired
  private PickUpRepository pickUpRepository;

  private Long userId;

  private String roomId;

  private Long money;

  private int recipientNum;

  private String token;

  private Scatter scatter;

  @BeforeAll
  void setUp() {
    userId = 100L;
    roomId = "room01";
    money = 10000L;
    recipientNum = 3;

    scatter = scatterService.scatter(new Scatter(userId, roomId, money, recipientNum));
    token = scatter.getToken();
  }

  @Test
  @Order(1)
  void 받아간_유저가_없는_뿌린기_상태를_조회한다() {
    // given
    Long reqUserId = userId;
    String reqToken = token;

    // when then
    Scatter status = scatterService.status(reqUserId, reqToken).orElse(null);
    assertThat(status, is(notNullValue()));
    assertThat(status.getCreateAt(), is(scatter.getCreateAt()));
    assertThat(status.getCash(), is(money));
    assertThat(status.getReceivers().size(), is(0));
  }

  @Test
  @Order(2)
  void 받아간_유저가_있는_뿌린기_상태를_조회한다() {
    // given
    Long reqUserId = userId;
    String reqToken = token;
    Long newUserId = 101L;

    // when
    scatterService.pickup(newUserId, roomId, token).get();
    Scatter afterPick = scatterRepository.findByToken(scatter.getToken()).get();

    // then
    Scatter status = scatterService.status(reqUserId, reqToken).orElse(null);
    assertThat(status, is(notNullValue()));
    assertThat(status.getCreateAt(), is(scatter.getCreateAt()));
    assertThat(status.getCash(), is(money));
    assertThat(status.getReceivers().size(), is(afterPick.getReceivers().size()));
    assertThat(status.getCurrentDistributed(), is(afterPick.getCurrentDistributed()));

    Receiver statusReceiver = status.getReceivers().get(0);
    assertTrue(afterPick.getReceivers().stream()
                                  .anyMatch(receiver ->
                                    receiver.getReceiverId() == statusReceiver.getReceiverId() &&
                                    receiver.getReceivedAmount() == statusReceiver.getReceivedAmount())
    );
  }

  @Test
  @Order(3)
  void 실패_뿌린사람_본인만_상태조회가_가능하다() {
    // given
    Long reqUserId = 801L;
    String reqToken = token;

    // when then
    Assertions.assertThrows(BadRequestException.class, () -> scatterService.status(reqUserId, reqToken));
  }

  @Test
  @Order(4)
  void 실패_유효하지_않는_토큰으로_조회_할수없다() {
    // given
    Long reqUserId = userId;
    String notExistToken = "fff";

    // when then
    assertThat(scatterService.status(reqUserId, notExistToken), is(Optional.empty()));
  }

  @Test
  @Order(5)
  void 실패_7일이_지난_뿌린기는_조회_할수없다() {
    // given
    Long reqUserId = userId;
    String oldToken = "ggg";

    Scatter old = scatterRepository.insert(new Scatter.Builder()
      .userId(userId)
      .roomId(roomId)
      .cash(100L)
      .recipientNum(1)
      .token(oldToken)
      .createAt(scatter.getCreateAt().minusDays(8))
      .build());

    pickUpRepository.insert(new PickUp.Builder()
      .scatterId(old.getSeq())
      .cash(old.getCash())
      .build());

    // when then
    assertThat(scatterService.status(reqUserId, old.getToken()), is(Optional.empty()));

  }
}
