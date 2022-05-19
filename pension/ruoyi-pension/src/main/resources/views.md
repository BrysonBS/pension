> v_device_gw_categories 创建
```sql
SELECT 
	b.gw_name,get_fullName(a.dept_id) as deptFullName,
	a.* ,
	c.id cid,c.display_name
FROM `owon_device` a
INNER JOIN owon_gateway b ON a.gw_code = b.gw_code AND b.`status` = 0
LEFT JOIN owon_device_categories c ON c.dev_model = a.dev_model OR a.categories_id = c.id
```

> v_ble_data_report
```sql
CREATE VIEW v_ble_data_report AS
SELECT 
	a.*,
	b.id bid,b.type,b.command
	,c.id cid,c.ieee,c.ep,c.`status`,c.dev_type,c.heart_rate
	,c.respiratory_rate,c.sleep_flag
	,c.status_value,c.data_type,c.awake_flag,c.bat_charge,c.bat_lvl
FROM owon_report a
INNER JOIN owon_datapacket b ON b.operation = 20060 AND a.id = b.report_id
INNER JOIN owon_argument c ON b.id = c.dp_id AND c.heart_rate != 0
```

> v_bioland_device_categories
```SQL
CREATE VIEW v_bioland_device_categories AS
SELECT a.*,get_fullName(a.dept_id) deptFullName,c.`name` typeName FROM bioland_device a
LEFT JOIN bioland_device_categories c ON a.categories_id = c.id
```

> proc_del_owon_report_cascade\
> 授予权限 : 先赋予可查询存储过程权限,再赋予执行存储过程权限\
> GRANT SELECT ON mysql.proc TO '用户名'@'%';\
> grant execute on PROCEDURE proc_del_owon_report_cascade to '用户名'@'%'; \
> 刷新权限:  FLUSH PRIVILEGES;\
> 执行: CALL proc_del_owon_report_cascade('2022-04-13')
```SQL
DELIMITER //
	CREATE PROCEDURE proc_del_owon_report_cascade(IN endDate VARCHAR(20))
		BEGIN
			DECLARE dpId INT DEFAULT 0;
			
			SELECT MAX(b.id) INTO dpId FROM owon_report a
			INNER JOIN owon_datapacket b ON a.id = b.report_id
			WHERE a.created < endDate;
			
			IF dpId > 0 THEN		
				START TRANSACTION; -- 开启事务
					DELETE a,b FROM owon_report a
					INNER JOIN owon_datapacket b ON a.id = b.report_id
					WHERE b.id <= dpId;

					DELETE FROM owon_argument WHERE dp_id <= dpId;
					DELETE FROM owon_response WHERE dp_id <= dpId;
				COMMIT;
			END IF;
    END
    //
DELIMITER ;