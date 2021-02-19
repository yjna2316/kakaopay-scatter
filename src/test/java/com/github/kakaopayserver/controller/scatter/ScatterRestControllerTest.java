package com.github.kakaopayserver.controller.scatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kakaopayserver.controller.ApiResult;
import com.github.kakaopayserver.model.scatter.PickUp;
import com.github.kakaopayserver.model.scatter.Scatter;
import com.github.kakaopayserver.service.scatter.ScatterService;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static com.github.kakaopayserver.controller.ApiResult.OK;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ScatterRestController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ScatterRestControllerTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean // 가짜 객체. 특정 행위 지정해서 실제 객체처럼 동작하게 만들 수 있다.
  private ScatterService scatterMockService;

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
    token = "XYZ";
  }

  @Test
  @Order(1)
  public void 돈을_뿌린다() throws Exception {
    ScatteringRequest request = new ScatteringRequest(money, recipientNum);
    Scatter scatter = request.newScatter(userId, roomId, request.getMoney(), request.getRecipientNum());
    scatter.updateToken(token);
    ApiResult apiResult = OK(new ScatterDto(scatter.getToken()));

    given(scatterMockService.scatter(any())).willReturn(scatter);

    mockMvc.perform(
      post("/api/scatters")
      .contentType(MediaType.APPLICATION_JSON)
      .header("X-ROOM-ID", roomId)
      .header("X-USER-ID", userId)
      .content(objectMapper.writeValueAsString(request))
    )
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.success", is(true)))
      .andExpect(content().string(objectMapper.writeValueAsString(apiResult)))
      .andDo(print());
  }
}