CREATE TABLE `test_user_log` (
  `log_id` bigint(20) NOT NULL,
  `USER_DATA` json DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `insert_by` bigint(20) DEFAULT NULL,
  `insert_time` datetime DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `USER_ID` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;