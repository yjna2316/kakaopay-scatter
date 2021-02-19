package com.github.kakaopayserver.service.scatter;

import com.github.kakaopayserver.controller.scatter.ScatteringRequest;
import com.github.kakaopayserver.model.scatter.PickUp;
import com.github.kakaopayserver.model.scatter.Scatter;
import com.github.kakaopayserver.repository.scatter.PickUpRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ScatterApiTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private ScatterService scatterService;

  @Autowired
  private PickUpRepository pickUpRepository;

  private Long userId;

  private String roomId;

  @BeforeAll
  void setUp() {
    userId = 100L;
    roomId = "room01";
  }

  @Test
  @Order(1)
  void 성공_돈을_뿌린다() {
    // given
    ScatteringRequest request = new ScatteringRequest(10000L, 3);

    // when
    Scatter scatter = scatterService.scatter(request.newScatter(
      userId,
      roomId,
      request.getMoney(),
      request.getRecipientNum()
    ));

    log.info("Created Scatter: {}", scatter);

    // 뿌리기 생성 확인
    assertThat(scatter, is(notNullValue()));
    assertThat(scatter.getToken().length(), is(3));
    assertThat(scatter.getUserId(), is(userId));
    assertThat(scatter.getRoomId(), is(roomId));
    assertThat(scatter.getCash(), is(request.getMoney()));
    assertThat(scatter.getRecipientNum(), is(request.getRecipientNum()));

    // 분배금 생성 확인
    List<PickUp> distributions = pickUpRepository.findByScatterId(scatter.getSeq()).orElse(emptyList());
    assertThat(distributions.size(), is(scatter.getRecipientNum()));
    assertThat(distributions.get(0).getUser_seq(), is(nullValue()));
    assertThat(distributions.stream().mapToLong(PickUp::getCash).sum(), is(scatter.getCash()));
    log.info("Created PickUps: {}", distributions);
  }
}
