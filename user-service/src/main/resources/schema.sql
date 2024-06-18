CREATE TABLE IF NOT EXISTS cash_user (
                                         id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                                         name varchar(50) NOT NULL,
    password varchar(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true
    );

CREATE TABLE IF NOT EXISTS authority(
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        role VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS user_authority(
                                             user_id BIGINT,
                                             authority_id BIGINT,
                                             FOREIGN KEY (user_id) REFERENCES cash_user(id),
    FOREIGN KEY (authority_id) REFERENCES authority(id),
    PRIMARY KEY (user_id, authority_id)
    );