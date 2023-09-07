-- Customer Table
CREATE TABLE customers
(
    uid                      UUID DEFAULT gen_random_uuid() NOT NULL,
    first_name               VARCHAR(32)                    NOT NULL,
    last_name                VARCHAR(32)                    NOT NULL,
    email                    VARCHAR(255)                   NOT NULL,
    country                  VARCHAR(3),
    city                     VARCHAR(255),
    zip                      VARCHAR(255),
    address                  VARCHAR(255),
    phone_number             VARCHAR(15),
    device_id                VARCHAR(255),
    account_creation_date    TIMESTAMP,
    account_creation_country VARCHAR(255),
    PRIMARY KEY (uid)
);

-- Transaction Table
CREATE TABLE transactions
(
    uid                        UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    type                       VARCHAR(255),
    status                     VARCHAR(255),
    message                    TEXT,
    amount                     INT          NOT NULL,
    currency                   VARCHAR(255) NOT NULL,
    notification_url           VARCHAR(255),
    return_url                 VARCHAR(255),
    test_mode                  BOOLEAN,
    created_at                 TIMESTAMP,
    updated_at                 TIMESTAMP,
    yoda_wallet_transaction_id UUID,
    customer_id                UUID REFERENCES customers (uid)
);