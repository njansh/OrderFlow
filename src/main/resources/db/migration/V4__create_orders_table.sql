CREATE TABLE orders (
                        id UUID NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        CONSTRAINT pk_orders PRIMARY KEY (id)
);