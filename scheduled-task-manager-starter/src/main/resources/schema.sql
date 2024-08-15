CREATE TABLE IF NOT EXISTS ecs_job
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    `key`           VARCHAR(255) NOT NULL UNIQUE KEY,
    `desc`          TEXT,
    bean_class_name VARCHAR(255) NOT NULL,
    bean_name       VARCHAR(255) NOT NULL,
    method_name     VARCHAR(255) NOT NULL,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ecs_exec_detail
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    `key`          VARCHAR(255) NOT NULL UNIQUE KEY,
    `desc`         TEXT,
    cron_expr      VARCHAR(255) NOT NULL,
    job_key        VARCHAR(255) NOT NULL,
    init_time      DATETIME     NOT NULL,
    end_time       DATETIME              DEFAULT NULL,
    last_exec_time DATETIME              DEFAULT NULL,
    exec_count     INT          NOT NULL DEFAULT 0,
    state          INT          NOT NULL DEFAULT 0,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

