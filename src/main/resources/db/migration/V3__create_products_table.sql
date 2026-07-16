CREATE TABLE products (
                       id UUID NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       price NUMERIC(10, 2) NOT NULL,
                       CONSTRAINT pk_products PRIMARY KEY (id));
