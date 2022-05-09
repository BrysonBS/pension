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