connect 'jdbc:derby:splice:wombat';


drop table category;
drop table category_sub;
drop table customer;
drop table item;
drop table subcategory;
drop table order_line;


-- 101,Apparel,Apparel_DE,Apparel_FR,Apparel_ES,Apparel_IT,Apparel_PO,Apparel_JA,Apparel_SCH,Apparel_KO
CREATE TABLE category (
	cat_id			INT,
	cat_name		VARCHAR(128),
	cat_name_de		VARCHAR(128),
	cat_name_fr		VARCHAR(128),
	cat_name_es		VARCHAR(128),
	cat_name_it		VARCHAR(128),
	cat_name_po		VARCHAR(128),
	cat_name_ja		VARCHAR(128),
	cat_name_sch		VARCHAR(128),
	cat_name_ko		VARCHAR(128));
call SYSCS_UTIL.SYSCS_IMPORT_DATA (null, 'CATEGORY', null, null, '/data/Splice/microstrategydata/category.csv', ',', '"', null);


-- "SUBCAT_ID","SUBCAT_DESC","CATEGORY_ID","SUBCAT_DESC_DE","SUBCAT_DESC_FR","SUBCAT_DESC_ES","SUBCAT_DESC_IT","SUBCAT_DESC_PO","SUBCAT_DESC_JA","SUBCAT_DESC_SCH","SUBCAT_DESC_KO",
CREATE TABLE category_sub (
	sbc_id			INT,
	sbc_desc		VARCHAR(128),
	sbc_category_id 	INT,
	sbc_desc_de		VARCHAR(128),
	sbc_desc_fr		VARCHAR(128),
	sbc_desc_es		VARCHAR(128),
	sbc_desc_it		VARCHAR(128),
	sbc_desc_po		VARCHAR(128),
	sbc_desc_ja		VARCHAR(128),
	sbc_desc_sch		VARCHAR(128),
	sbc_desc_ko		VARCHAR(128));
call SYSCS_UTIL.SYSCS_IMPORT_DATA (null, 'CATEGORY_SUB', null, null, '/data/Splice/microstrategydata/category_sub.csv', ',', '"', null);



-- "CUSTOMER_ID","CUST_LAST_NAME","CUST_FIRST_NAME","GENDER_ID","CUST_BIRTHDATE","EMAIL","ADDRESS","ZIPCODE","INCOME_ID","CUST_CITY_ID","AGE_YEARS","AGERANGE_ID",
-- "MARITALSTATUS_ID","EDUCATION_ID","HOUSINGTYPE_ID","HOUSEHOLDCOUNT_ID","PLAN_ID","FIRST_ORDER","LAST_ORDER","TENURE","RECENCY","STATUS_ID",
CREATE TABLE customer (
	cst_id 			INT, 
	cst_last_name 		VARCHAR(64), 
	cst_first_name 		VARCHAR(64),
	cst_gender_id		INT, 
	cst_birthdate		TIMESTAMP,
	cst_email		VARCHAR(64),
	cst_address		VARCHAR(64),
	cst_zipcode		VARCHAR(16),
	cst_income_id		INT,
	cst_city_id		INT,
	cst_age_years		INT,
	cst_agerange_id		INT,
	cst_maritalstatus_id	INT,
	cst_education_id	INT,
	cst_housingtype_id	INT,
	cst_householdcount_id	INT,
	cst_plan_id		INT,
	cst_first_order		TIMESTAMP,
	cst_last_order		TIMESTAMP,
	cst_tenure		INT,
	cst_recency		INT,
	cst_status_id		INT);
call SYSCS_UTIL.SYSCS_IMPORT_DATA (null, 'CUSTOMER', null, null, '/data/Splice/microstrategydata/customer_iso.csv', ',', '"', null);


-- "ITEM_ID","ITEM_NAME","ITEM_LONG_DESC","ITEM_FOREIGN_NAME","ITEM_URL","DISC_CD","ITEM_UPC","WARRANTY","UNIT_PRICE","UNIT_COST","SUBCAT_ID","SUPPLIER_ID","BRAND_ID",
-- "ITEM_NAME_DE","ITEM_NAME_FR","ITEM_NAME_ES","ITEM_NAME_IT","ITEM_NAME_PO","ITEM_NAME_JA","ITEM_NAME_SCH","ITEM_NAME_KO",
-- "ITEM_LONG_DESC_DE","ITEM_LONG_DESC_FR","ITEM_LONG_DESC_ES","ITEM_LONG_DESC_IT","ITEM_LONG_DESC_PO","ITEM_LONG_DESC_JA","ITEM_LONG_DESC_SCH","ITEM_LONG_DESC_KO",
CREATE TABLE item (
	itm_id			INT,
	itm_name		VARCHAR(128),
	itm_long_desc		VARCHAR(32672),
	itm_foreign_name	VARCHAR(128),
	itm_url			VARCHAR(1024),
	itm_disc_cd		VARCHAR(64),
	itm_upc			VARCHAR(64),
	itm_warranty		VARCHAR(1),
	itm_unit_price		FLOAT,
	itm_unit_cost		FLOAT,
	itm_subcat_id		INT,
	itm_supplier_id		INT,
	itm_brand_id		INT,
	itm_name_de		VARCHAR(128),
	itm_name_fr		VARCHAR(128),
	itm_name_es		VARCHAR(128),
	itm_name_it		VARCHAR(128),
	itm_name_po		VARCHAR(128),
	itm_name_ja		VARCHAR(128),
	itm_name_sch		VARCHAR(128),
	itm_name_ko		VARCHAR(128),
	itm_long_desc_de	VARCHAR(32672),
	itm_long_desc_fr	VARCHAR(32672),
	itm_long_desc_es	VARCHAR(32672),
	itm_long_desc_it	VARCHAR(32672),
	itm_long_desc_po	VARCHAR(32672),
	itm_itm_long_desc_ja	VARCHAR(32672),
	itm_long_desc_sch	VARCHAR(32672),
	itm_long_desc_ko	VARCHAR(32672));
call SYSCS_UTIL.SYSCS_IMPORT_DATA (null, 'ITEM', null, null, '/data/Splice/microstrategydata/item.csv', ',', '"', null);


-- "ORDER_ID","ITEM_ID","ORDER_AMT","ORDER_AMT","ORDER_DATE","EMP_ID","PROMOTION_ID","QTY_SOLD","UNIT_PRICE","UNIT_COST","DISCOUNT","CUSTOMER_ID"
CREATE TABLE order_line (
	orl_order_id 		VARCHAR(50), 
	orl_item_id 		INT, 
	orl_amt 		INT, 
	orl_date 		TIMESTAMP, 
	orl_emp_id 		INT, 
	orl_promotion_id 	INT, 
	orl_qty_sold 		INT, 
	orl_unit_price 		FLOAT, 
	orl_unit_cost 		FLOAT, 
	orl_discount 		FLOAT, 
	orl_customer_id 	INT);
call SYSCS_UTIL.SYSCS_IMPORT_DATA (null, 'ORDER_LINE', null, null, '/data/Splice/microstrategydata/order_line_500K.csv', ',', '"', null);


show tables; /* Doesn't return anything ??? */


SELECT COUNT(1) FROM category;
SELECT COUNT(1) FROM category_sub;
SELECT COUNT(1) FROM customer;
SELECT COUNT(1) FROM item;
SELECT COUNT(1) FROM order_line;

