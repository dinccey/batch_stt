create table app_user
(
    admin           bit          null,
    items_processed int          null,
    password        varchar(255) null,
    username        varchar(255) not null
        primary key
);


create table inference_instance
(
    available       bit          null,
    items_processed int          null,
    id              bigint       not null
        primary key,
    app_user        varchar(255) not null,
    instance_url    varchar(255) not null,
    constraint FKq7g6n0onkc20wkevyjq42s1or
        foreign key (app_user) references app_user (username)
);

create table inference_instance_seq
(
    next_val bigint null
);

create table item
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


create table item_seq
(
    next_val bigint null
);



INSERT INTO item_seq (next_val) values (1);

INSERT INTO inference_instance_seq (next_val) values (1);
