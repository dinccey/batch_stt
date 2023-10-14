package org.vaslim.batch_stt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vaslim.batch_stt.model.AppUser;
import org.vaslim.batch_stt.model.InferenceInstance;

import java.util.Optional;
import java.util.Set;

@Repository
public interface InferenceInstanceRepository extends JpaRepository<InferenceInstance, Long> {
    Optional<InferenceInstance> findByInstanceUrl(String instanceUrl);

    Set<InferenceInstance> findAllByAppUser(AppUser appUser);
}
