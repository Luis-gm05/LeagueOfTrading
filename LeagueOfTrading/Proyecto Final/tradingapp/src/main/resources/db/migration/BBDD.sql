CREATE DATABASE  IF NOT EXISTS `leagueoftrading` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `leagueoftrading`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: leagueoftrading
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activos`
--

DROP TABLE IF EXISTS `activos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activos`
--

LOCK TABLES `activos` WRITE;
/*!40000 ALTER TABLE `activos` DISABLE KEYS */;
INSERT INTO `activos` VALUES (1,'Bitcoin'),(2,'Ethereum'),(3,'Tether'),(4,'BNB'),(5,'USD Coin'),(6,'XRP'),(7,'Cardano'),(8,'Dogecoin'),(9,'Solana'),(10,'Polkadot'),(11,'Litecoin'),(12,'Shiba Inu'),(13,'TRON'),(14,'Avalanche'),(15,'Dai'),(16,'Wrapped Bitcoin'),(17,'Uniswap'),(18,'Chainlink'),(19,'LEO Token'),(20,'Cosmos Hub'),(21,'Monero'),(22,'Ethereum Classic'),(23,'Stellar'),(24,'Bitcoin Cash'),(25,'Toncoin'),(26,'Internet Computer'),(27,'TrueUSD'),(28,'Filecoin'),(29,'Lido DAO'),(30,'Hedera'),(31,'Arbitrum'),(32,'Quant'),(33,'Cronos'),(34,'NEAR Protocol'),(35,'VeChain'),(36,'Aptos'),(37,'ApeCoin'),(38,'Algorand'),(39,'The Graph'),(40,'Frax'),(41,'Pax Dollar'),(42,'EOS'),(43,'MultiversX'),(44,'Rocket Pool'),(45,'Synthetix Network'),(46,'Immutable X'),(47,'Render Token'),(48,'Fantom'),(49,'Decentraland');
/*!40000 ALTER TABLE `activos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operacion`
--

DROP TABLE IF EXISTS `operacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operacion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `idusuario` bigint NOT NULL,
  `idactivo` bigint NOT NULL,
  `tipo_operacion` enum('compra','venta') NOT NULL,
  `cantidad` decimal(15,2) NOT NULL,
  `precio` decimal(15,2) NOT NULL,
  `valor_total` decimal(15,2) NOT NULL,
  `cerrada` tinyint(1) NOT NULL DEFAULT '0',
  `beneficio_perdida` decimal(15,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `FKmbul5px8pvsbjmoey92boblfj` (`idactivo`),
  KEY `FKa2kgsw33590atvcv1qtnrddrs` (`idusuario`),
  CONSTRAINT `FKa2kgsw33590atvcv1qtnrddrs` FOREIGN KEY (`idusuario`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `FKmbul5px8pvsbjmoey92boblfj` FOREIGN KEY (`idactivo`) REFERENCES `activos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operacion`
--

LOCK TABLES `operacion` WRITE;
/*!40000 ALTER TABLE `operacion` DISABLE KEYS */;
INSERT INTO `operacion` VALUES (8,1,1,'compra',5.00,1200.50,6002.50,0,0.00),(9,1,2,'venta',2.00,1500.75,3001.50,1,250.00),(10,1,3,'compra',10.00,450.00,4500.00,1,-100.50),(11,1,4,'venta',6.00,800.00,4800.00,1,500.00),(12,1,2,'compra',3.00,1000.00,3000.00,0,0.00),(13,1,3,'venta',4.00,950.00,3800.00,1,-200.75),(14,1,1,'compra',7.00,750.00,5250.00,1,300.00),(16,1,1,'compra',1.00,83210.05,83210.05,1,-584.13),(17,1,1,'compra',1.00,83873.62,83873.62,1,10.53),(18,1,1,'compra',1.00,83793.60,83793.60,1,-35.39),(19,1,1,'compra',0.50,92165.41,46082.71,1,-12.91),(20,1,1,'compra',0.50,91985.77,45992.88,1,-3.66),(21,1,1,'compra',0.50,91847.05,45923.53,1,48.03),(22,1,1,'compra',0.50,92775.97,46387.98,1,-25.85),(23,1,1,'compra',0.50,94761.09,47380.54,1,33.92),(24,1,1,'compra',0.50,94820.21,47410.11,1,-9.18),(25,1,1,'venta',0.20,94904.78,18980.96,1,2.21),(26,1,1,'compra',0.01,94235.37,942.35,1,-0.29),(27,1,1,'compra',0.20,94108.70,18821.74,1,-3.08),(28,1,1,'compra',0.04,94047.72,3761.91,1,0.13),(29,1,1,'compra',0.05,94067.74,4233.05,1,10.77),(30,1,1,'compra',0.04,104008.38,4420.36,1,-2.18),(31,1,1,'venta',0.43,104028.23,44212.00,1,-94.89),(32,1,1,'venta',0.50,104253.91,52126.96,1,-10.41),(33,1,1,'venta',1.00,104149.64,104149.64,1,10.14),(34,1,1,'venta',1.00,104089.90,104089.90,1,-4.73),(35,1,1,'compra',1.00,104089.90,104089.90,1,4.73),(36,1,1,'venta',1.00,103947.11,103947.11,1,-6.46),(37,1,1,'venta',1.00,104110.08,104110.08,1,-28.28);
/*!40000 ALTER TABLE `operacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `contra_hash` varchar(255) NOT NULL,
  `saldo` decimal(15,2) NOT NULL DEFAULT '100000.00',
  `puntuacion` varchar(255) NOT NULL DEFAULT '500',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'admin','admin@gmail.com','$2a$12$W1569AGYOeVkj.spBcD92u9aZV1WBipNrTMYf0XmteuANpwi7VVyG',405096.82,'496'),(2,'prueba','prueba@gmail.com','$2a$10$PR56Rs2FW9V514gg0AG1reqc1XWqbGpa1EmyH4PRMZ.0jb8jwWBJC',100000.00,'700'),(4,'hola','hola@gmail.com','$2a$10$T5HZHlz5LkXSk8OeQKgRY.RfeN8.4pufqE9nfdJRrFsEeMf.9IhHm',100000.00,'500');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-12 22:37:47
