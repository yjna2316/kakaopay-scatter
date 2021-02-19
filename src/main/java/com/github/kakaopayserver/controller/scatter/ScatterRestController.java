package com.github.kakaopayserver.controller.scatter;

import com.github.kakaopayserver.controller.ApiResult;
import com.github.kakaopayserver.controller.ErrorCode;
import com.github.kakaopayserver.error.NotFoundException;
import com.github.kakaopayserver.service.scatter.ScatterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.github.kakaopayserver.controller.ApiResult.OK;

@RestController
@RequestMapping("api/scatters")
public class ScatterRestController {

  private final ScatterService scatterService;

  public ScatterRestController(ScatterService scatterService) {
    this.scatterService = scatterService;
  }

  /* 뿌리기 */
  @PostMapping
  public ApiResult<ScatterDto> scattering(
    @RequestHeader(value = "X-USER-ID") Long userId,
    @RequestHeader(value = "X-ROOM-ID") String roomId,
    @RequestBody ScatteringRequest request) {
    return OK(
      new ScatterDto(
        scatterService.scatter(
          request.newScatter(userId, roomId, request.getMoney(), request.getRecipientNum())
        ).getToken()
      )
    );
  }

  /* 뿌린기 받기 */
  @PutMapping(path = "receive")
  public ApiResult<PickUpDto> pickup(
    @RequestHeader(value = "X-USER-ID") Long userId,
    @RequestHeader(value = "X-ROOM-ID") String roomId,
    @RequestBody Map<String, String> request) {
    return OK(
      scatterService.pickup(userId, roomId, request.get("token"))
      .map(PickUpDto::new)
      .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND))
    );
  }

  /* 뿌린기 조회 */
  @GetMapping("/{token}")
  public ApiResult<ScatterStatusDto> status(
    @RequestHeader(value = "X-USER-ID") Long userId,
    @PathVariable("token") String token) {
    return OK(
      scatterService.status(userId, token)
        .map(scatter -> new ScatterStatusDto(
          scatter.getCreateAt(),
          scatter.getCash(),
          scatter.getCurrentDistributed(),
          scatter.getReceivers()
        )).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND))
    );
  }
}
