package org.vaslim.batch_stt.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;
import org.vaslim.batch_stt.dto.InferenceInstanceDTO;
import org.vaslim.batch_stt.service.InferenceInstanceService;
import org.vaslim.batch_stt.util.JwtUtils;

import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/instances")
public class InferenceInstanceController {

    @Value("${batchstt_jwt}")
    private String cookieName;

    private final JwtUtils jwtUtils;

    private final InferenceInstanceService inferenceInstanceService;

    public InferenceInstanceController(JwtUtils jwtUtils, InferenceInstanceService inferenceInstanceService) {
        this.jwtUtils = jwtUtils;
        this.inferenceInstanceService = inferenceInstanceService;
    }

    @PostMapping("/add")
    public ResponseEntity<InferenceInstanceDTO> addInferenceInstance(@Valid @RequestBody InferenceInstanceDTO inferenceInstanceDTO, final HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        return ResponseEntity.ok(inferenceInstanceService.addInferenceInstance(inferenceInstanceDTO,username));
    }

    @PostMapping("/remove")
    public ResponseEntity<InferenceInstanceDTO> removeInferenceInstance(@Valid @RequestBody InferenceInstanceDTO inferenceInstanceDTO, final HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());


        return ResponseEntity.ok(inferenceInstanceService.removeInferenceInstance(inferenceInstanceDTO,username));
    }

    @PostMapping("/disable")
    public ResponseEntity<?> disableInferenceInstance(@Valid @RequestBody InferenceInstanceDTO inferenceInstanceDTO, final HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        return ResponseEntity.ok(inferenceInstanceService.disableInferenceInstance(inferenceInstanceDTO,username));
    }

    @PostMapping("/enable")
    public ResponseEntity<?> enableInferenceInstance(@Valid @RequestBody InferenceInstanceDTO inferenceInstanceDTO, final HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        return ResponseEntity.ok(inferenceInstanceService.enableInferenceInstance(inferenceInstanceDTO,username));
    }

    @GetMapping("/all")
    public ResponseEntity<Set<InferenceInstanceDTO>> authenticateUser(final HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        return ResponseEntity.ok(inferenceInstanceService.getAll(username));
    }

}
