-- MySQL dump 10.16  Distrib 10.1.26-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: seedling_dev
-- ------------------------------------------------------
-- Server version	10.1.26-MariaDB-0+deb9u1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table role
--

DROP TABLE IF EXISTS role;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE role (
  id   BIGINT (20) UNSIGNED NOT NULL AUTO_INCREMENT
  COMMENT '角色Id',
  name VARCHAR(64) DEFAULT NULL
  COMMENT '角色名称',
  PRIMARY KEY (id),
  UNIQUE KEY name ( name )
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 4
DEFAULT CHARSET = utf8mb4
COMMENT ='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table role
--

LOCK TABLES role WRITE;
/*!40000 ALTER TABLE role
  DISABLE KEYS */;
INSERT INTO role VALUES (1, 'USER');
INSERT INTO role VALUES (2, 'ADMIN');
INSERT INTO role VALUES (3, 'TEST');
/*!40000 ALTER TABLE role
  ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table account
--

DROP TABLE IF EXISTS account;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account (
  id            BIGINT (20) UNSIGNED NOT NULL AUTO_INCREMENT
  COMMENT '账户Id',
  email         VARCHAR(64)
  CHARACTER SET utf8
  COLLATE utf8_bin NOT NULL
  COMMENT '邮箱',
  name          VARCHAR(32)
  CHARACTER SET utf8
  COLLATE utf8_bin NOT NULL
  COMMENT '账户名',
  password      VARCHAR(256)
  CHARACTER SET utf8
  COLLATE utf8_bin NOT NULL
  COMMENT '密码',
  register_time DATETIME DEFAULT CURRENT_TIMESTAMP
  COMMENT '注册时间',
  login_time    DATETIME DEFAULT NULL
  COMMENT '上一次登录时间',
  PRIMARY KEY (id),
  UNIQUE KEY ix_account_name ( name ),
  UNIQUE KEY ix_account_email (email)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 3
DEFAULT CHARSET = utf8mb4
COMMENT ='账户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table account
--

LOCK TABLES account WRITE;
/*!40000 ALTER TABLE account
  DISABLE KEYS */;
INSERT INTO account VALUES
  (1, 'admin@qq.com', 'admin', '$2a$10$OG1zaFHT2LUy4SGcQ4EnRu9sPQMjMGEE6jARz61aQwRQ3316N6ikG', '2018-01-01 00:00:00',
   '2018-02-01 00:00:00');
INSERT INTO account VALUES
  (2, 'account@qq.com', 'account', '$2a$10$yjfcoyNWgoUh3QQ3I6Lwmux57rCz3mZP1j8V4BK60EIVdwT3SkwFO',
   '2018-01-01 00:00:00',
   '2018-02-01 00:00:00');
/*!40000 ALTER TABLE account
  ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table account_role
--

DROP TABLE IF EXISTS account_role;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE account_role (
  account_id BIGINT (20) UNSIGNED DEFAULT NULL
  COMMENT '账户Id',
  role_id    BIGINT (20) UNSIGNED DEFAULT NULL
  COMMENT '角色Id',
  PRIMARY KEY (account_id, role_id),
  CONSTRAINT fk_ref_account FOREIGN KEY (account_id) REFERENCES account (id),
  CONSTRAINT fk_ref_role FOREIGN KEY (role_id) REFERENCES role (id)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 4
DEFAULT CHARSET = utf8mb4
COMMENT ='账户角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table account_role
--

LOCK TABLES account_role WRITE;
/*!40000 ALTER TABLE account_role
  DISABLE KEYS */;
INSERT INTO account_role VALUES (1, 2);
INSERT INTO account_role VALUES (2, 1);
INSERT INTO account_role VALUES (1, 3);
/*!40000 ALTER TABLE account_role
  ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2018-02-04  7:19:23
