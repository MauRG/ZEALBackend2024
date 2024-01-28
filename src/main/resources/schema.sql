CREATE TABLE IF NOT EXISTS tenants (
    id       VARCHAR(60)  PRIMARY KEY,
    name     VARCHAR      NOT NULL
);

CREATE TABLE IF NOT EXISTS customers (
    id       VARCHAR(60)  PRIMARY KEY,
    name     VARCHAR      NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
    id          VARCHAR(60)     PRIMARY KEY,
    amount      INT             NOT NULL,
    void        BIT             NOT NULL DEFAULT 0,
    tenantId    VARCHAR(60)     NOT NULL,
    customerId  VARCHAR(60)     NOT NULL
);

CREATE TABLE IF NOT EXISTS auditLogs (
    id          VARCHAR(60)     PRIMARY KEY,
    message     VARCHAR         NOT NULL,
    action      VARCHAR         NOT NULL,
    userId      VARCHAR         NOT NULL,
    timeStamp   VARCHAR         NOT NULL
);
