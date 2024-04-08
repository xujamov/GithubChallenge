CREATE TABLE IF NOT EXISTS contributors
(
    id    BIGINT PRIMARY KEY,
    login VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS projects
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner_id BIGINT  NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES contributors (id)
);

INSERT INTO contributors (id, login) VALUES (26714593, 'xujamov');
INSERT INTO projects (id, name, owner_id) VALUES (733087911, 'imageBot', 26714593);

CREATE TABLE IF NOT EXISTS pull_requests
(
    id             BIGINT PRIMARY KEY,
    project_id     BIGINT  NOT NULL,
    contributor_id BIGINT  NOT NULL,
    is_closed      BOOLEAN NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects (id),
    FOREIGN KEY (contributor_id) REFERENCES contributors (id)
);

CREATE TABLE IF NOT EXISTS commits
(
    id             VARCHAR(255) PRIMARY KEY,
    message        VARCHAR(255) NOT NULL,
    project_id     BIGINT       NOT NULL,
    contributor_id BIGINT       NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects (id),
    FOREIGN KEY (contributor_id) REFERENCES contributors (id)
);