package org.vaslim.batch_stt.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vaslim.batch_stt.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByProcessedTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<Item> findAllByFilePathTextIsNull();

    boolean existsItemByFilePathVideoLike(String filePathVideo);

    Optional<Item> findByFilePathVideoStartingWith(String filePathVideoNoExtension);

    List<Item> findByTextFilterHashNotLikeOrTextFilterHashIsNull(String filterMapHash);

    Integer countItemByFilePathTextIsNull();

    Integer countItemByFilePathTextIsNotNull();

    Integer countItemsByFilePathVideoEndingWith(String extension);
    Set<Item> findItemsByFilePathVideoNotContaining(String extension);

    @Transactional
    void deleteItemByFilePathVideoStartingWith(String excludedPath);
}
