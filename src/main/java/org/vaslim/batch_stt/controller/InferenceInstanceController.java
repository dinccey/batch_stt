package org.vaslim.batch_stt.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;
import org.vaslim.batch_stt.dto.InferenceInstanceDTO;
import org.vaslim.batch_stt.dto.StatisticsDTO;
import org.vaslim.batch_stt.pool.ConnectionPool;
import org.vaslim.batch_stt.service.InferenceInstanceService;
import org.vaslim.batch_stt.service.StatisticsService;
import org.vaslim.batch_stt.util.JwtUtils;

import java.util.Set;

//@CrossOrigin(origins = "${frontend.origin}", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/v1/instances")
public class InferenceInstanceController {

    @Value("${batchstt.jwtCookieName}")
    private String cookieName;

    private final JwtUtils jwtUtils;

    private final InferenceInstanceService inferenceInstanceService;

    private final ConnectionPool connectionPool;

    private final StatisticsService statisticsService;

    public InferenceInstanceController(JwtUtils jwtUtils, InferenceInstanceService inferenceInstanceService, ConnectionPool connectionPool, StatisticsService statisticsService) {
        this.jwtUtils = jwtUtils;
        this.inferenceInstanceService = inferenceInstanceService;
        this.connectionPool = connectionPool;
        this.statisticsService = statisticsService;
    }

    @PostMapping("/add")
    public ResponseEntity<InferenceInstanceDTO> addInferenceInstance(@Valid @RequestBody InferenceInstanceDTO inferenceInstanceDTO, final HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        InferenceInstanceDTO toReturn = inferenceInstanceService.addInferenceInstance(inferenceInstanceDTO,username);
        connectionPool.refreshUrlsFromDatabase();

        return ResponseEntity.ok(toReturn);
    }

    @PostMapping("/remove")
    public ResponseEntity<InferenceInstanceDTO> removeInferenceInstance(@Valid @RequestBody InferenceInstanceDTO inferenceInstanceDTO, final HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        InferenceInstanceDTO toReturn = inferenceInstanceService.removeInferenceInstance(inferenceInstanceDTO,username);
        connectionPool.refreshUrlsFromDatabase();

        return ResponseEntity.ok(toReturn);
    }

    @PostMapping("/disable")
    public ResponseEntity<?> disableInferenceInstance(@Valid @RequestBody InferenceInstanceDTO inferenceInstanceDTO, final HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        InferenceInstanceDTO toReturn = inferenceInstanceService.disableInferenceInstance(inferenceInstanceDTO,username);
        connectionPool.refreshUrlsFromDatabase();

        return ResponseEntity.ok(toReturn);
    }

    @PostMapping("/enable")
    public ResponseEntity<?> enableInferenceInstance(@Valid @RequestBody InferenceInstanceDTO inferenceInstanceDTO, final HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        InferenceInstanceDTO toReturn = inferenceInstanceService.enableInferenceInstance(inferenceInstanceDTO,username);
        connectionPool.refreshUrlsFromDatabase();

        return ResponseEntity.ok(toReturn);
    }

    @GetMapping("/all")
    public ResponseEntity<Set<InferenceInstanceDTO>> getAllInstances(final HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        return ResponseEntity.ok(inferenceInstanceService.getAll(username));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkIsOnline(@RequestParam(name = "basePath") final String basePath){
        return ResponseEntity.ok(inferenceInstanceService.checkIsReachable(basePath));
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsDTO> getStatistics(final HttpServletRequest httpServletRequest){
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        return ResponseEntity.ok(statisticsService.getUserStatistics(username));
    }

}
