CREATE TABLE wayroc_user
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    user_account  VARCHAR(64)           NOT NULL,
    user_password VARCHAR(128)          NOT NULL,
    gender        INT                   NULL,
    email         VARCHAR(128)          NULL,
    phone         VARCHAR(32)           NULL,
    user_role     VARCHAR(16)           NULL,
    create_time   datetime              NULL,
    update_time   datetime              NULL,
    is_deleted    INT                   NULL,
    CONSTRAINT pk_wayroc_user PRIMARY KEY (id)
);

ALTER TABLE wayroc_user
