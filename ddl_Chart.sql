CREATE TABLE chart
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    goal        TEXT                  NULL,
    chart_data  TEXT                  NULL,
    chart_type  VARCHAR(255)          NULL,
    gen_chart   TEXT                  NULL,
    gen_result  TEXT                  NULL,
    user_id     BIGINT                NULL,
    create_time datetime              NULL,
    update_time datetime              NULL,
    is_delete   INT                   NULL,
    CONSTRAINT pk_chart PRIMARY KEY (id)
);