drop table IF EXISTS `card`;

create TABLE `card` (
    `cardId` INT NOT NULL AUTO_INCREMENT,
    `userId` INT NOT NULL,
    `cardTitle` VARCHAR(50) NOT NULL,
    `cardContent` VARCHAR(255) NOT NULL,
    `boardName` VARCHAR(50) NOT NULL,
    `boardIdx` BIGINT UNSIGNED NOT NULL,
    `createdTime` TIMESTAMP NOT NULL,
    `removed` TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (`cardId`),
    FOREIGN KEY (`userId`)
        REFERENCES `user` (`userId`)
);

drop table IF EXISTS `history`;

create TABLE `history` (
    `historyId` INT NOT NULL AUTO_INCREMENT,
    `userId` INT NOT NULL,
    `actionHistory` VARCHAR(100) NOT NULL,
    `actionTime` TIMESTAMP NOT NULL,
    PRIMARY KEY (`historyId`),
    FOREIGN KEY (`userId`)
        REFERENCES `user` (`userId`)
);

drop table IF EXISTS `user`;

create TABLE `user` (
    `userId` INT NOT NULL AUTO_INCREMENT,
    `password` VARCHAR(50) NOT NULL,
    `removed` TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (`userId`)
);
