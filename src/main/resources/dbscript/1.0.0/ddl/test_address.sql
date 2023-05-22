CREATE TABLE `test_address` (
  `ADDRESS_ID` bigint(20) NOT NULL,
  `LOCATION` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `INSERT_BY` bigint(20) DEFAULT NULL,
  `INSERT_TIME` datetime DEFAULT NULL,
  `UPDATE_BY` bigint(20) DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `ACCOUNT_ID` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;