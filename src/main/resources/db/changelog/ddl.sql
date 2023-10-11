create table batchstt.item
(
    id                  bigint                                      not null
        primary key,
    processed_timestamp datetime(6)                                 null,
    text_filter_hash    varchar(64)                                 null,
    file_path_text      varchar(255)                                null,
    file_path_video     varchar(255)                                null,
    processing_status   enum ('FINISHED', 'IN_PROGRESS', 'PENDING') null,
    video_file_name     varchar(255)                                null,
    constraint UK_6ygjsyrorli15kwqmbhow8p6o
        unique (file_path_video),
    constraint UK_e41r3dh4254ukhajg5g9y8at2
        unique (file_path_text)
);

create table batchstt.item_seq
(
    next_val bigint null
);

INSERT INTO batchstt.item_seq (next_val) values (1);

