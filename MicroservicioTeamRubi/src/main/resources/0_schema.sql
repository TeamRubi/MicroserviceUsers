CREATE TABLE user (
    id INT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    paymentmethod VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY (email)
);

CREATE TABLE favoriteproduct(
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id),
    PRIMARY KEY (id),
    UNIQUE KEY (user_id, product_id)
);



