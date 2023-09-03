create table if not exists batchstt.item
(
    id                  bigint                                      not null
    primary key,
    processed_timestamp datetime(6)                                 null,
    file_path_text      varchar(255)                                null,
    file_path_video     varchar(255)                                null,
    processing_status   enum ('FINISHED', 'IN_PROGRESS', 'PENDING') null,
    video_file_name     varchar(255)                                null,
    constraint UK_6ygjsyrorli15kwqmbhow8p6o
    unique (file_path_video),
    constraint UK_e41r3dh4254ukhajg5g9y8at2
    unique (file_path_text)
    );

create table if not exists batchstt.item_seq
(
    next_val bigint null
);

