DROP DATABASE if exists UserDB;
CREATE DATABASE UserDB;
USE UserDB;

CREATE TABLE Users (
	userID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    pass VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL
);

CREATE TABLE Favorites (
	userID INT NOT NULL,
    ticker VARCHAR(6) NOT NULL,
    FOREIGN KEY(userID) REFERENCES Users(userID)
);

CREATE TABLE Portfolio (
	userID INT NOT NULL,
    ticker VARCHAR(6) NOT NULL,
    numStock INT NOT NULL,
    price DOUBLE NOT NULL,
    dateBought DATE NOT NULL,
    FOREIGN KEY(userID) REFERENCES Users(userID)
);