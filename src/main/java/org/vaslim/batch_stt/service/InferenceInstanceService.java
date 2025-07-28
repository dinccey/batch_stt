package org.vaslim.batch_stt.service;

import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.dto.InferenceInstanceDTO;

import java.util.Set;

@Service
public interface InferenceInstanceService {
    InferenceInstanceDTO addInferenceInstance(InferenceInstanceDTO inferenceInstanceDTO, String username);

    InferenceInstanceDTO removeInferenceInstance(InferenceInstanceDTO inferenceInstanceDTO, String username);

    InferenceInstanceDTO disableInferenceInstance(InferenceInstanceDTO inferenceInstanceDTO, String username);

    InferenceInstanceDTO enableInferenceInstance(InferenceInstanceDTO inferenceInstanceDTO, String username);

    Set<InferenceInstanceDTO> getAll(String username);

    Boolean checkIsReachable(String basePath);

    Boolean checkIsWhisperAvailable(String basePath);
}
