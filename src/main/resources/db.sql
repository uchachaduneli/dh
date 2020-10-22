-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.7.20-log - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             10.1.0.5464
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for dhl
CREATE DATABASE IF NOT EXISTS `dhl` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `dhl`;

-- Dumping structure for table dhl.config
CREATE TABLE IF NOT EXISTS `config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `value` varchar(50) DEFAULT NULL,
  `type_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table dhl.departments
CREATE TABLE IF NOT EXISTS `departments` (
  `department_id` int(10) NOT NULL AUTO_INCREMENT,
  `department_name` varchar(255) NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`department_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table dhl.emails
CREATE TABLE IF NOT EXISTS `emails` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mail` varchar(100) NOT NULL,
  `lead_id` int(50) NOT NULL,
  `confirmed` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 არავალიდური /  2 ვალიდური',
  `note` varchar(100) DEFAULT NULL,
  `activation_code` varchar(10) DEFAULT NULL,
  `create_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mail` (`mail`,`lead_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26766 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for procedure dhl.expired
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `expired`()
BEGIN
insert into expire_leads(lead_id,old_expiration_date,term,new_expiration_date) 
select l.lead_id,l.expiration_date,
round(DATEDIFF(l.expiration_date,l.contraction_date)/365) term,
date_add(l.expiration_date, interval round(DATEDIFF(l.expiration_date,l.contraction_date)/365) year) new_expiration_date 
from leads l
where l.status_id=1 and datediff(now(),l.expiration_date)>=0 and l.term_type=1;
if ROW_COUNT()>0 then
	commit;
	update leads l
	set l.expiration_date=date_add(l.expiration_date, interval round(DATEDIFF(l.expiration_date,l.contraction_date)/365) year)
	where  l.status_id=1 and datediff(now(),l.expiration_date)>=0 and l.term_type=1;
end if;
END//
DELIMITER ;

-- Dumping structure for table dhl.expire_leads
CREATE TABLE IF NOT EXISTS `expire_leads` (
  `expire_id` int(11) NOT NULL AUTO_INCREMENT,
  `lead_id` int(11) NOT NULL,
  `old_expiration_date` date NOT NULL,
  `term` int(11) NOT NULL,
  `new_expiration_date` date NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `send_mail` tinyint(2) NOT NULL DEFAULT '1' COMMENT '1-ar aris gagzavnili 2-aris gagzavnili',
  PRIMARY KEY (`expire_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2110979 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table dhl.leads
CREATE TABLE IF NOT EXISTS `leads` (
  `lead_id` int(11) NOT NULL AUTO_INCREMENT,
  `company_name` varchar(500) NOT NULL,
  `company_id_number` varchar(50) NOT NULL,
  `address` varchar(5000) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `mail` varchar(250) DEFAULT NULL,
  `contact_person` varchar(255) DEFAULT NULL,
  `contraction_date` date DEFAULT NULL,
  `expiration_date` date DEFAULT NULL,
  `operator_id` int(11) NOT NULL COMMENT 'operatoris aidi romelmad daamata',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `has_contract` int(11) NOT NULL DEFAULT '2' COMMENT '1 iani tu aqvs 2 iani tu ar aqvs',
  `sail_or_iurist_id` int(11) DEFAULT NULL COMMENT 'seilis an iuristis aidi romelmac daamata xelshekruleba',
  `interested_departs` varchar(500) DEFAULT NULL,
  `contr_depart_id` int(11) NOT NULL DEFAULT '1',
  `payment_alert` int(11) NOT NULL DEFAULT '0',
  `sale_percents` int(11) DEFAULT NULL,
  `account_number` varchar(50) DEFAULT NULL,
  `oper_date` date DEFAULT NULL,
  `note` varchar(3000) DEFAULT NULL,
  `status_id` int(11) DEFAULT '1' COMMENT '1-aqtiuri 2-pasiuri',
  `term_type` int(11) DEFAULT NULL COMMENT ' 1-avtomaturi 2-notificationi',
  `answer_id` int(11) DEFAULT '1' COMMENT '1 undat 2 ar undat',
  `service_type` varchar(500) DEFAULT NULL,
  `limited` int(11) DEFAULT '2' COMMENT '1 limitirebuli xelshekruleba 2 ara',
  `davalianeba` int(11) DEFAULT '2' COMMENT '1 ki 2 ara',
  `contract_code` varchar(50) DEFAULT NULL,
  `next_call` date DEFAULT NULL,
  `type_id` int(11) NOT NULL,
  `contr_departs` varchar(500) DEFAULT NULL,
  `max_limit` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`lead_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10337 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table dhl.lead_depts
CREATE TABLE IF NOT EXISTS `lead_depts` (
  `lead_dept_id` int(10) NOT NULL AUTO_INCREMENT,
  `lead_id` int(10) DEFAULT NULL,
  `dept_id` int(10) DEFAULT NULL,
  `acc_number` varchar(10) DEFAULT NULL,
  `sale_percent` int(10) DEFAULT NULL,
  PRIMARY KEY (`lead_dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25396 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table dhl.lead_docs
CREATE TABLE IF NOT EXISTS `lead_docs` (
  `doc_id` int(10) NOT NULL AUTO_INCREMENT,
  `lead_id` int(10) NOT NULL,
  `doc_name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `operator_id` int(10) NOT NULL COMMENT 'seilis an iuristis aidi romelmac daamata xelshekruleba',
  PRIMARY KEY (`doc_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13151 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Data exporting was unselected.
-- Dumping structure for table dhl.limits_history
CREATE TABLE IF NOT EXISTS `limits_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lead_id` int(11) NOT NULL,
  `decrease_amount` double NOT NULL,
  `start_amount` double NOT NULL,
  `left_amount` double NOT NULL,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` int(11) NOT NULL DEFAULT '0' COMMENT '=1 when deleted',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table dhl.permissions
CREATE TABLE IF NOT EXISTS `permissions` (
  `persmission_id` int(10) NOT NULL AUTO_INCREMENT,
  `permission_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`persmission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for procedure dhl.prc_check_leads
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_check_leads`(IN `p_lead_id` INT)
BEGIN
select * from leads l
where l.expiration_date=now;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_check_user
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_check_user`(IN `p_user_id` INT, OUT `p_result_id` INT)
BEGIN
set p_result_id=0;
select p.type_id into p_result_id from users p
where p.user_id=p_user_id;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_depart
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_depart`(IN `p_depart_id` INT, IN `p_depart_name` vaRCHAR(200), OUT `p_result_id` INT)
BEGIN
set p_result_id = 0;
if p_depart_id=0 then
 insert into departments (department_name)values(p_depart_name);
 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   end if;
elseif p_depart_id>0 then
 update departments set department_name=p_depart_name
 where department_id=p_depart_id;
 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   end if;	   
elseif p_depart_id<0 then
 delete from departments 
      where department_id=p_depart_id*(-1);
 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   end if;
end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_departments
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_departments`(IN `p_result_id` INT, IN `p_dept_id` INT, IN `p_dept_name` INT)
BEGIN
set p_result_id = 0;
if p_dept_id=0 then
 insert into departments
 (department_id,department_name)
 values(
 p_dept_id,
 p_dept_name
 );
 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   end if;
elseif p_dept_id>0 then
 update departments
 set department_name=p_dept_name
 where department_id=p_dept_id;

 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   end if;
	   
elseif p_dept_id<0 then
 
 delete from departments 
      where department_id=p_dept_id*(-1);
 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   end if;
end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_email
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_email`(
	IN `p_id` INT,
	IN `p_mail` VARCHAR(200),
	IN `p_lead_id` INT,
	IN `p_confirmed` INT,
	IN `p_note` VARCHAR(500),
	IN `p_activation_code` VARCHAR(20),
	OUT `p_result_id` INT

)
BEGIN
	set p_result_id = 0;
    if p_id=0 then
        INSERT INTO `emails` (`mail`, `lead_id`, `confirmed`, `note`, `activation_code`) VALUES (p_mail, p_lead_id, p_confirmed, p_note, p_activation_code);
        if ROW_COUNT()>0 then
            commit;
            set p_result_id=1;
        end if;

    elseif p_id>0 then

        update emails set mail=p_mail, lead_id=p_lead_id, confirmed=p_confirmed, note=p_note, activation_code=p_activation_code WHERE id=p_id;

        if ROW_COUNT()>0 then
            commit;
            set p_result_id=1;
        end if;
    elseif p_id<0 then
        delete from departments
        where id=p_id*(-1);
        if ROW_COUNT()>0 then
            commit;
            set p_result_id=1;
        end if;
    end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_contract
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_contract`(IN `p_lead_id` INT, IN `p_doc_id` INT)
BEGIN
if (p_lead_id>0 and p_doc_id=0) then
select 
doc_id,
project_id,
doc_name,
doc_type_id,
doc_type_name,
create_date
 from lead_docs 
where lead_id=p_lead_id;
elseif (p_lead_id=0 and p_doc_id>0) then
select 
doc_id,
project_id,
doc_name,
doc_type_id,
doc_type_name,
create_date
 from lead_docs 
where doc_id=p_doc_id;
elseif (p_lead_id>0 and p_doc_id>0) then
select 
doc_id,
project_id,
doc_name,
doc_type_id,
doc_type_name,
create_date
 from lead_docs 
where lead_id=p_lead_id and doc_id=p_doc_id;
end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_departments
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_departments`(IN `p_dept_id` INT)
BEGIN
if p_dept_id=0 then
select * from departments;
else 
select * from departments where department_id=p_dept_id;
end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_inv_user
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_inv_user`(IN `p_user_name` varchar(45), IN `p_user_pass` varchar(200), IN `p_user_id` INT)
BEGIN
	if p_user_id=-1 then
		select * from vi_users u where u.user_name=p_user_name and u.user_password=p_user_pass and u.inv_access=1 order by u.user_id asc;
	elseif p_user_id=0 then
		select * from vi_users u where u.inv_access=1 order by u.user_id asc;
	elseif p_user_id>0 then
		select * from vi_users u where u.user_id=p_user_id and u.inv_access=1 order by u.user_id asc;
	end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_lead
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_lead`(IN `p_lead_id` INT, IN `p_user_id` INT, IN `p_from_date` DATE, IN `p_to_date` DATE, IN `p_status_id` INT, IN `p_pagestart` INT, IN `p_pagelimit` INT, IN `p_company_name` VARCHAR(500), IN `p_company_id_number` VARCHAR(50), IN `p_account_number` VARCHAR(50), IN `p_sale_percents` INT, IN `p_davalianeba` INT, IN `p_contr_depart_id` INT, IN `p_operator_id` INT, IN `p_sail_or_iurist_id` INT, IN `p_contract_code` VARCHAR(50), IN `p_expired_leads` INT, IN `p_limited_leads` INT, IN `p_service_type` VARCHAR(500), IN `p_call_alert` INT, IN `p_pay_alert` INT, IN `p_empty_ident` INT, IN `p_mail` VARCHAR(100), IN `p_phone` VARCHAR(100))
BEGIN
declare v_type int default 0;
call prc_check_user(p_user_id,v_type);
SET @limit =concat(' limit ',p_pagestart,',',p_pagelimit,';');
SET @where ='';
SET @order =' order by case when (DATEDIFF(now()+INTERVAL 10 DAY,v.expiration_date)>=0 and v.status_id=1 and v.term_type=2) then 1 else 0 end desc,call_alert desc,v.payment_alert desc, v.limited asc';
SET @query =concat('select v.*,
case when (DATEDIFF(now()+INTERVAL 10 DAY,v.expiration_date)>=0 and v.status_id=1 and v.term_type=2) then 1 else 0 end check_lead,
case when (v.next_call <= CURDATE() and v.status_id=1) then 1 else 0 end call_alert
from vi_leads v where 1=1 ');

if (p_from_date is not null or p_to_date is not null) then
	if (p_from_date is not null and p_to_date is not null) then
		set @where = concat(@where,' and v.contraction_date BETWEEN ''',p_from_date,'''',' and ','''',p_to_date,'''');
	elseif p_from_date is not null then
		set @where = concat(@where,' and v.contraction_date>=''',p_from_date,'''');
	elseif p_to_date is not null then
		set @where = concat(@where,' and v.contraction_date<=''',p_to_date,'''');
	end if;
end if;

if v_type=1 then 
	set @where = concat(@where,' and ((v.has_contract=1 and v.type_id <>3) or v.operator_id=',p_user_id,') ');
elseif v_type=2 then
	set @where=concat(@where,' and v.type_id <>3 ');
elseif v_type=3 then 
		set @where = concat(@where,' and (v.has_contract=1 or v.type_id =3) ');
end if;

if p_mail <>''  then 
	SET @where = concat(@where,' and v.mail like ''','%',p_mail,'%','''');
end if;
if p_phone <>''  then 
	SET @where = concat(@where,' and v.phone like ''','%',p_phone,'%','''');
end if;

if p_company_name <>''  then 
	SET @where = concat(@where,' and v.company_name like ''','%',p_company_name,'%','''');
end if;
if p_company_id_number <>''  then 
	SET @where = concat(@where,' and v.company_id_number like ''','%',p_company_id_number,'%','''');
end if;
if p_account_number <>''  then 
	SET @where = concat(@where,' and v.account_number like ''','%',p_account_number,'%','''');
end if;
if p_sale_percents>0 then 
	set @where = concat(@where,' and v.sale_percents=',p_sale_percents);
end if;
if p_davalianeba>0 then 
	set @where = concat(@where,' and v.davalianeba=',p_davalianeba);
end if;
if p_contr_depart_id>0 then 
	set @where = concat(@where,' and ', p_contr_depart_id ,' in (select dept_id from lead_depts where lead_id = v.lead_id)');
end if;
if p_operator_id>0 then 
	set @where = concat(@where,' and v.operator_id=',p_operator_id);
end if;
if p_sail_or_iurist_id>0 then 
	set @where = concat(@where,' and v.sail_or_iurist_id=',p_sail_or_iurist_id);
end if;
if p_contract_code <>''  then 
	SET @where = concat(@where,' and v.contract_code like ''','%',p_contract_code,'%','''');
end if;
if p_status_id>0 then 
	set @where = concat(@where,' and v.status_id=',p_status_id);
end if;
if p_expired_leads=1 then 
	set @where = concat(@where,' and (DATEDIFF(now()+INTERVAL 10 DAY,v.expiration_date)>=0 and v.status_id=1 and v.term_type=2)');
end if;
if p_limited_leads > 0 then 
	set @where = concat(@where,' and v.limited=', p_limited_leads);
end if;
if p_service_type <>''  then 
	SET @where = concat(@where,' and v.service_type like ''','%',p_service_type,'%','''');
end if;
if p_call_alert > 0 then 
	set @where = concat(@where,' and v.next_call <= CURDATE()');
end if;
if p_pay_alert > 0 then 
	set @where = concat(@where,' and v.payment_alert=', p_pay_alert);
end if;
if p_empty_ident > 0 then 
	set @where = concat(@where,' and (v.company_id_number is null or length(v.company_id_number)=0)');
end if;
SET @query=CONCAT(@query,@where,@order,@limit);
PREPARE stmt FROM @query;
 EXECUTE stmt;
 DEALLOCATE PREPARE stmt; 


END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_leads_count
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_leads_count`(IN `p_lead_id` INT, IN `p_user_id` INT, IN `p_from_date` DATE, IN `p_to_date` DATE, IN `p_status_id` INT, IN `p_company_name` VARCHAR(500), IN `p_company_id_number` VARCHAR(50), IN `p_account_number` VARCHAR(50), IN `p_sale_percents` INT, IN `p_davalianeba` INT, IN `p_contr_depart_id` INT, IN `p_operator_id` INT, IN `p_sail_or_iurist_id` INT, IN `p_contract_code` VARCHAR(50), IN `p_expired_leads` INT, IN `p_limited_leads` INT, IN `p_service_type` VARCHAR(500), IN `p_call_alert` INT, IN `p_pay_alert` INT, IN `p_empty_ident` INT, IN `p_mail` VARCHAR(100), IN `p_phone` VARCHAR(100))
BEGIN
declare v_type int default 0;
call prc_check_user(p_user_id,v_type);
SET @where ='';
SET @query =concat('select count(v.lead_id) from vi_leads v where 1=1 ');
if (p_from_date is not null or p_to_date is not null) then
	if (p_from_date is not null and p_to_date is not null) then
		set @where = concat(@where,' and v.contraction_date BETWEEN ''',p_from_date,'''',' and ','''',p_to_date,'''');
	elseif p_from_date is not null then
		set @where = concat(@where,' and v.contraction_date>=''',p_from_date,'''');
	elseif p_to_date is not null then
		set @where = concat(@where,' and v.contraction_date<=''',p_to_date,'''');
	end if;
end if;

if v_type=1 then 
	set @where = concat(@where,' and ((v.has_contract=1 and v.operator_id in (select user_id from users where type_id<>3)) or v.operator_id=',p_user_id,') ');
elseif v_type=2 then
	set @where=concat(@where,' and v.operator_id in (select user_id from users where type_id<>3) ');
elseif v_type=3 then 
		set @where = concat(@where,' and (v.has_contract=1 or v.operator_id in (select user_id from users where type_id=3)) ');
end if;

if p_mail <>''  then 
	SET @where = concat(@where,' and v.mail like ''','%',p_mail,'%','''');
end if;
if p_phone <>''  then 
	SET @where = concat(@where,' and v.phone like ''','%',p_phone,'%','''');
end if;

if p_company_name <>''  then 
	SET @where = concat(@where,' and v.company_name like ''','%',p_company_name,'%','''');
end if;
if p_company_id_number <>''  then 
	SET @where = concat(@where,' and v.company_id_number like ''','%',p_company_id_number,'%','''');
end if;
if p_account_number <>''  then 
	SET @where = concat(@where,' and v.account_number like ''','%',p_account_number,'%','''');
end if;
if p_sale_percents>0 then 
	set @where = concat(@where,' and v.sale_percents=',p_sale_percents);
end if;
if p_davalianeba>0 then 
	set @where = concat(@where,' and v.davalianeba=',p_davalianeba);
end if;
if p_contr_depart_id>0 then 
	set @where = concat(@where,' and ', p_contr_depart_id ,' in (select dept_id from lead_depts where lead_id = v.lead_id)');
end if;
if p_operator_id>0 then 
	set @where = concat(@where,' and v.operator_id=',p_operator_id);
end if;
if p_sail_or_iurist_id>0 then 
	set @where = concat(@where,' and v.sail_or_iurist_id=',p_sail_or_iurist_id);
end if;
if p_contract_code <>''  then 
	SET @where = concat(@where,' and v.contract_code like ''','%',p_contract_code,'%','''');
end if;
if p_status_id>0 then 
	set @where = concat(@where,' and v.status_id=',p_status_id);
end if;
if p_expired_leads=1 then 
	set @where = concat(@where,' and (DATEDIFF(now()+INTERVAL 10 DAY,v.expiration_date)>=0 and v.status_id=1 and v.term_type=2)');
end if;
if p_limited_leads > 0 then 
	set @where = concat(@where,' and v.limited=', p_limited_leads);
end if;
if p_service_type <>''  then 
	SET @where = concat(@where,' and v.service_type like ''','%',p_service_type,'%','''');
end if;
if p_call_alert > 0 then 
	set @where = concat(@where,' and v.next_call <= CURDATE()');
end if;
if p_pay_alert > 0 then 
	set @where = concat(@where,' and v.payment_alert=', p_pay_alert);
end if;
if p_empty_ident > 0 then 
	set @where = concat(@where,' and (v.company_id_number is null or length(v.company_id_number)=0)');
end if;
SET @query=CONCAT(@query,@where);
PREPARE stmt FROM @query;
 EXECUTE stmt;
 DEALLOCATE PREPARE stmt; 

END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_lead_depts
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_lead_depts`(IN `p_lead_id` INT)
BEGIN
if p_lead_id>0 then
select * from vi_lead_depts where lead_id=p_lead_id;
end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_lead_docs
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_lead_docs`(IN `p_lead_id` INT)
BEGIN
if p_lead_id=0 then
select * from lead_docs;
else 
select * from lead_docs where lead_id=p_lead_id;
end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_lead_old
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_lead_old`()
BEGIN
declare v_admin int default 0;
call prc_check_user(p_user_id,v_admin);
if p_lead_id>0
		then 
		select * from vi_leads where lead_id=p_lead_id;
	elseif p_from_date>0 and p_to_date>0 then
		select * from vi_leads v 
		where v.expiration_date between p_from_date and p_to_date;
	elseif p_from_date>0 then
		select * from vi_leads v 
		where v.expiration_date>=p_from_date;
	elseif p_to_date>0 then
		select * from vi_leads v 
		where v.expiration_date<=p_to_date;
	else 
		select * from vi_leads v;
end if;

END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_mail_leads
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_mail_leads`()
BEGIN
select 
e.lead_id,
e.old_expiration_date,
e.term,
e.new_expiration_date,
l.company_name,
l.company_id_number,
l.mail
 from expire_leads e
 join leads l on e.lead_id=l.lead_id
where l.send_mail=1;

END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_permissions
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_permissions`(IN `p_user_id` INT)
BEGIN
if p_user_id=0 then
select * from user_permissions;
else 
select * from user_permissions where user_id=p_user_id;
end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_sent_emails
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_sent_emails`(IN p_pagestart int, IN p_pagelimit int, IN p_to varchar(50),
                                                     IN p_status int, IN p_ident_number varchar(100), IN p_lead_id int,
                                                     IN p_from_date date, IN p_to_date date, IN p_confirmed int,
                                                     IN p_subject varchar(100))
BEGIN
SET @limit =concat(' limit ',p_pagestart,',',p_pagelimit,';');
SET @where ='';
SET @order =' order by v.id desc';
SET @query =concat('select v.* from vi_sent_emails v where 1=1 ');

if p_status>0 then 
	set @where = concat(@where,' and v.`status`=',p_status);
end if;

if p_to <>''  then 
	SET @where = concat(@where,' and v.mail like ''','%',p_to,'%','''');
end if;

if p_ident_number <>''  then 
	SET @where = concat(@where,' and v.ident_number like ''','%',p_ident_number,'%','''');
end if;

if p_subject <>''  then
    SET @where = concat(@where,' and v.subject like ''','%',p_subject,'%','''');
end if;

if p_confirmed>0 then 
	set @where = concat(@where,' and v.`confirmed`=',p_confirmed);
end if;

if p_lead_id>0 then 
	set @where = concat(@where,' and v.lead_id=',p_lead_id);
end if;

if p_confirmed>0 then 
	set @where = concat(@where,' and v.confirmed=',p_confirmed);
end if;

if (p_from_date is not null or p_to_date is not null) then
	if (p_from_date is not null and p_to_date is not null) then
		set @where = concat(@where,' and v.create_date BETWEEN ''',p_from_date,'''',' and ','''',p_to_date,'''');
	elseif p_from_date is not null then
		set @where = concat(@where,' and v.create_date>=''',p_from_date,'''');
	elseif p_to_date is not null then
		set @where = concat(@where,' and v.create_date<=''',p_to_date,'''');
	end if;
end if;

	SET @query=CONCAT(@query,@where,@order,@limit);
	PREPARE stmt FROM @query;
 	EXECUTE stmt;
	 DEALLOCATE PREPARE stmt; 
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_sent_emails_count
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_sent_emails_count`(IN p_to varchar(50), IN p_status int,
                                                           IN p_ident_number varchar(100), IN p_lead_id int,
                                                           IN p_confirmed int, IN p_from_date date, IN p_to_date date,
                                                           IN p_subject varchar(100))
BEGIN
SET @where ='';
SET @query =concat('select count(v.id) from vi_sent_emails v where 1=1 ');

if p_status>0 then 
	set @where = concat(@where,' and v.`status`=',p_status);
end if;

if p_to <>''  then 
	SET @where = concat(@where,' and v.mail like ''','%',p_to,'%','''');
end if;

if p_ident_number <>''  then 
	SET @where = concat(@where,' and v.ident_number like ''','%',p_ident_number,'%','''');
end if;

if p_subject <>''  then
    SET @where = concat(@where,' and v.subject like ''','%',p_subject,'%','''');
end if;

if p_confirmed>0 then 
	set @where = concat(@where,' and v.`confirmed`=',p_confirmed);
end if;

if p_lead_id>0 then 
	set @where = concat(@where,' and v.lead_id=',p_lead_id);
end if;

if p_confirmed>0 then 
	set @where = concat(@where,' and v.confirmed=',p_confirmed);
end if;
if (p_from_date is not null or p_to_date is not null) then
	if (p_from_date is not null and p_to_date is not null) then
		set @where = concat(@where,' and v.create_date BETWEEN ''',p_from_date,'''',' and ','''',p_to_date,'''');
	elseif p_from_date is not null then
		set @where = concat(@where,' and v.create_date>=''',p_from_date,'''');
	elseif p_to_date is not null then
		set @where = concat(@where,' and v.create_date<=''',p_to_date,'''');
	end if;
end if;

	SET @query=CONCAT(@query,@where);
	PREPARE stmt FROM @query;
 	EXECUTE stmt;
	 DEALLOCATE PREPARE stmt; 
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_sent_sms
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_sent_sms`(IN `p_from_date` DATE, IN `p_to_date` DATE, IN `p_to` VARCHAR(50), IN `p_status_id` INT, IN `p_text` VARCHAR(100), IN `p_pagestart` INT, IN `p_pagelimit` INT
, IN `p_lead_data` VARCHAR(50)

)
BEGIN
SET @limit =concat(' limit ',p_pagestart,',',p_pagelimit,';');
SET @where ='';
SET @order =' order by v.id desc';
SET @query =concat('select v.* from vi_sent_sms v where 1=1 ');

if p_status_id>0 then 
	set @where = concat(@where,' and v.status_id=',p_status_id);
end if;

if p_to <>''  then 
	SET @where = concat(@where,' and v.to like ''','%',p_to,'%','''');
end if;

if p_text <>''  then 
	SET @where = concat(@where,' and v.text like ''','%',p_text,'%','''');
end if;

if p_lead_data <>''  then 
	SET @where = concat(@where,' and v.lead_data like ''','%',p_lead_data,'%','''');
end if;

if (p_from_date is not null or p_to_date is not null) then
	if (p_from_date is not null and p_to_date is not null) then
		set @where = concat(@where,' and v.create_date BETWEEN ''',p_from_date,'''',' and ','''',p_to_date,'''');
	elseif p_from_date is not null then
		set @where = concat(@where,' and v.create_date>=''',p_from_date,'''');
	elseif p_to_date is not null then
		set @where = concat(@where,' and v.create_date<=''',p_to_date,'''');
	end if;
end if;

	SET @query=CONCAT(@query,@where,@order,@limit);
	PREPARE stmt FROM @query;
 	EXECUTE stmt;
	 DEALLOCATE PREPARE stmt; 
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_sent_sms_count
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_sent_sms_count`(IN `p_from_date` DATE, IN `p_to_date` DATE, IN `p_to` VARCHAR(50), IN `p_status_id` INT, IN `p_text` VARCHAR(100)

)
BEGIN
SET @where ='';
SET @query =concat('select count(v.id) from vi_sent_sms v where 1=1 ');

if p_status_id>0 then 
	set @where = concat(@where,' and v.status_id=',p_status_id);
end if;

if p_to <>''  then 
	SET @where = concat(@where,' and v.to like ''','%',p_to,'%','''');
end if;

if p_text <>''  then 
	SET @where = concat(@where,' and v.text like ''','%',p_text,'%','''');
end if;

if (p_from_date is not null or p_to_date is not null) then
	if (p_from_date is not null and p_to_date is not null) then
		set @where = concat(@where,' and v.create_date BETWEEN ''',p_from_date,'''',' and ','''',p_to_date,'''');
	elseif p_from_date is not null then
		set @where = concat(@where,' and v.create_date>=''',p_from_date,'''');
	elseif p_to_date is not null then
		set @where = concat(@where,' and v.create_date<=''',p_to_date,'''');
	end if;
end if;

	SET @query=CONCAT(@query,@where);
	PREPARE stmt FROM @query;
 	EXECUTE stmt;
	 DEALLOCATE PREPARE stmt; 
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_user
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_user`(IN `p_user_name` varchar(45), IN `p_user_pass` varchar(200), IN `p_user_id` INT)
BEGIN
if p_user_id=-1 then
select * from vi_users u where u.user_name=p_user_name and u.user_password=p_user_pass order by u.user_id asc;
elseif p_user_id=0 then
select * from vi_users u order by u.user_id asc;
elseif p_user_id>0 then
select * from vi_users u where u.user_id=p_user_id order by u.user_id asc;
end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_get_user_types
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_get_user_types`(IN `p_type_id` INT)
BEGIN
	if p_type_id = 0
		then 
			select * from user_types;
	else
		select * from user_types where user_type_id=p_type_id;
	end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_leads
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_leads`(OUT `p_result_id` INT, IN `p_lead_id` INT, IN `p_company_name` vaRCHAR(500), IN `p_company_id_number` vaRCHAR(50), IN `p_address` VARCHAR(5000), IN `p_phone` vaRCHAR(50), IN `p_contact_person` vaRCHAR(255), IN `p_contraction_date` daTE, IN `p_expiration_date` daTE, IN `p_operator_id` INT, IN `p_mail` varCHAR(255), IN `p_interestedDeparts` VARCHAR(500), IN `p_note` VARCHAR(3000), IN `p_contr_depart_id` INT, IN `p_sale_percents` INT, IN `p_account_number` VARCHAR(50), IN `p_status_id` INT, IN `p_term_type` INT, IN `p_has_contract` INT, IN `p_sail_or_admin_id` INT, IN `p_answer_id` INT, IN `p_service_type` VARCHAR(500), IN `p_limited` INT, IN `p_davalianeba` INT, IN `p_contract_code` vARCHAR(50), IN `p_next_call` DATE, IN `p_permission_id` INT, IN `p_max_limit` DOUBLE)
BEGIN
set p_result_id = 0;
if p_lead_id=0 then
 insert into leads
 (company_name,
 company_id_number,
 address,
 phone,
 mail,
 contact_person,
 contraction_date,
 expiration_date,
 operator_id,
 interested_departs, 
 note,
 contr_depart_id,
 sale_percents,
 account_number,
 has_contract,
 sail_or_iurist_id,
 answer_id,
 service_type,
 limited,
 davalianeba,
 contract_code,
 term_type,
 next_call,
 type_id,
 max_limit )
 values(
 p_company_name,
 p_company_id_number,
 p_address,
 p_phone,
 p_mail,
 p_contact_person,
 p_contraction_date,
 p_expiration_date,
 p_operator_id,
 p_interestedDeparts,
 p_note,
 p_contr_depart_id,
 p_sale_percents,
 p_account_number,
 p_has_contract,
 p_sail_or_admin_id,
 p_answer_id,
 p_service_type,
 p_limited,
 p_davalianeba,
 p_contract_code,
 p_term_type,
 p_next_call,
 (select u.type_id from users u where u.user_id=p_operator_id),
 p_max_limit
 );
 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=(select max(lead_id) from leads);
	   
	/*	select GROUP_CONCAT(department_name SEPARATOR ',') into @depart_name
 		from vi_lead_depts where lead_id =p_result_id;
 		
 		update leads set contr_departs = @depart_name where lead_id =p_result_id;*/
 end if;
if p_permission_id > 0 then
 	update leads set type_id=p_permission_id where lead_id=p_result_id;
end if;
 
elseif p_lead_id>0 then

 update leads
 set 
 company_name=p_company_name,
 company_id_number=p_company_id_number,
 address=p_address,
 phone=p_phone,
 mail=p_mail,
 contact_person=p_contact_person,
 contraction_date=p_contraction_date,
 expiration_date=p_expiration_date,
 sail_or_iurist_id=p_operator_id,
 oper_date=now(),
 interested_departs = p_interestedDeparts,
 note=p_note,
 contr_depart_id=p_contr_depart_id,
 sale_percents=p_sale_percents,
 account_number=p_account_number,
 status_id=p_status_id,
 term_type=p_term_type,
 has_contract=p_has_contract,
 sail_or_iurist_id=p_sail_or_admin_id,
 answer_id=p_answer_id,
 service_type=p_service_type,
 limited=p_limited,
 davalianeba=p_davalianeba,
 contract_code=p_contract_code,
 next_call=p_next_call,
 type_id=(select u.type_id from users u where u.user_id=p_operator_id),
 max_limit = p_max_limit
 where lead_id=p_lead_id;

 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=p_lead_id;
 end if;
if p_permission_id > 0 then
 	update leads set type_id=p_permission_id where lead_id=p_lead_id;
end if;
elseif p_lead_id<0 then
 
 delete from leads 
      where lead_id=p_lead_id*(-1);
 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   end if;
end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_lead_depts
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_lead_depts`(OUT `p_result_id` INT, IN `p_lead_dept_id` INT, IN `p_lead_id` INT, IN `p_dept_id` INT)
BEGIN
set p_result_id = 0;
select count(*) into @exist from lead_depts where lead_id=p_lead_id and dept_id=p_dept_id;
if p_lead_dept_id=0 then
	if @exist > 0 then 
		set p_result_id=1;
	else 
 		insert into lead_depts (lead_id,dept_id) values(p_lead_id, p_dept_id);
 		if ROW_COUNT()>0 then
			commit;
	   	set p_result_id=1;
	   	
	   	select GROUP_CONCAT(department_name SEPARATOR ',') into @departs from vi_lead_depts where lead_id = p_lead_id;
	   	update leads set contr_departs = @departs where lead_id=p_lead_id;
	   end if;
	   
	end if;
	   
elseif p_lead_dept_id<0 then
 
 select lead_id into @lead_id from lead_depts where lead_dept_id=abs(p_lead_dept_id);
 
 delete from lead_depts where lead_dept_id=p_lead_dept_id*(-1);
 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   select GROUP_CONCAT(department_name SEPARATOR ',') into @departs from vi_lead_depts where lead_id = @lead_id;
	   update leads set contr_departs = @departs where lead_id=@lead_id;
	   end if;
end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_lead_docs
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_lead_docs`(IN `p_doc_id` INT, IN `p_lead_id` INT, IN `p_doc_name` VARCHAR(500), OUT `p_result_id` INT, IN `p_operator_id` INT)
BEGIN
set p_result_id = 0;
if p_doc_id=0 then
 insert into lead_docs
 (lead_id,doc_name,operator_id)
 values(
 p_lead_id,
 p_doc_name,
 p_operator_id
 );
 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   end if;
elseif p_doc_id>0 then
 update lead_docs
 set lead_id=p_lead_id,
 doc_name=p_doc_name,
 operator_id=p_operator_id
 where doc_id=p_doc_id;

 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   end if;
	   
elseif p_doc_id<0 then
 
 delete from lead_docs 
      where doc_id=abs(p_doc_id);
 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   end if;
end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_limit_history
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_limit_history`(OUT `p_result_id` INT, IN `p_id` INT, IN `p_lead_id` INT, IN `p_start_amount` DOUBLE, IN `p_decrease_amount` DOUBLE
)
BEGIN
set p_result_id = 0;
if p_id=0 then

 insert into limits_history (lead_id, decrease_amount, start_amount, left_amount) 
 VALUES( p_lead_id, p_decrease_amount, p_start_amount, p_start_amount - p_decrease_amount);
 
 if ROW_COUNT()>0 then
 update leads l set l.max_limit = l.max_limit - p_decrease_amount where l.lead_id = p_lead_id;
		commit;
	   set p_result_id=(select MAX(id) from limits_history);
 end if;
 

elseif p_id<0 then

	select decrease_amount into @decreasedAmnt from limits_history WHERE id=p_id*(-1);
	
 	update limits_history h set h.deleted=1 WHERE id=p_id*(-1);
 
 if ROW_COUNT()>0 then
 update leads l set l.max_limit = l.max_limit + @decreasedAmnt where l.lead_id = p_lead_id;
		commit;
	   set p_result_id=1;
	   end if;
end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_send_mail
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_send_mail`(IN `p_lead_id` INT, OUT `p_result_id` INT)
BEGIN
set p_result_id=0;
update expire_leads l
set l.send_mail=2
where l.lead_id=p_lead_id;
if row_count()>0 then
commit;
set p_result_id=1;
end if;
END//
DELIMITER ;

-- Dumping structure for procedure dhl.prc_users
DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `prc_users`(OUT `p_result_id` INT, IN `p_user_id` INT, IN `p_username` VARCHAR(500), IN `p_description` VARCHAR(500), IN `p_password` VARCHAR(500), IN `p_status_id` INT, IN `p_admin_id` INT, IN `p_type_id` INT, IN `p_pass_edit` INT, IN `p_inv_access` INT, IN `p_inv_type_id` INT)
BEGIN
set p_result_id = 0;
if p_user_id=0 then
 insert into users
 (user_description,user_name,user_password,status_id,type_id,create_date,add_user_id, inv_access, inv_type_id)
 values(
 p_description,
 p_username,
 p_password,
 p_status_id,
 p_type_id,
 now(),
 p_admin_id,
 p_inv_access,
 p_inv_type_id);
 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   end if;
	   
elseif p_user_id>0 then
if p_user_id != 7 then
 	if p_pass_edit=1
 		then
 update users
 set user_name=p_username,
 user_description=p_description,
 user_password=p_password,
 status_id=p_status_id,
 oper_id=p_admin_id,
 oper_date=now(),
 type_id=p_type_id,
 inv_access = p_inv_access,
 inv_type_id = p_inv_type_id
 where user_id=p_user_id;
 else
 update users set user_name=p_username,
 user_description=p_description,
 status_id=p_status_id,
 oper_id=p_admin_id,
 oper_date=now(),
 type_id=p_type_id,
 inv_access = p_inv_access,
 inv_type_id = p_inv_type_id
 where user_id=p_user_id;
 end if;
end if;
 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   end if;
	   
elseif p_user_id<0 then
 
 if abs(p_user_id)!= 7 then
 delete from users 
      where user_id=abs(p_user_id);
 
 if ROW_COUNT()>0 then
		commit;
	   set p_result_id=1;
	   end if;
end if;
end if;
END//
DELIMITER ;

-- Dumping structure for table dhl.sent_emails
CREATE TABLE IF NOT EXISTS `sent_emails` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email_id` int(11) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 sent / 2 exception',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `subject` varchar(200) DEFAULT NULL,
  `body_text` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table dhl.sent_sms
CREATE TABLE IF NOT EXISTS `sent_sms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lead_id` int(11) DEFAULT NULL,
  `to` varchar(50) NOT NULL,
  `text` text NOT NULL,
  `response` varchar(50) NOT NULL COMMENT 'Return codes:\\r\\n0000 - message_id : Operation successful. message_id is used in\\r\\nmessage status tracking query.\\r\\n0001 : Internal error\\r\\n0003 : Invalid Request\\r\\n0004 : Invalid query\\r\\n0005 : Empty message\\r\\n0006 : Prefix error\\r\\n0007 : MSISDN error\\r\\n',
  `status_id` int(11) NOT NULL DEFAULT '0' COMMENT '1 - success / 2 - failed',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=405 DEFAULT CHARSET=utf8 COMMENT='Return codes:\r\n0000 - message_id : Operation successful. message_id is used in\r\nmessage status tracking query.\r\n0001 : Internal error\r\n0003 : Invalid Request\r\n0004 : Invalid query\r\n0005 : Empty message\r\n0006 : Prefix error\r\n0007 : MSISDN error\r\n';

-- Data exporting was unselected.
-- Dumping structure for table dhl.updates_log
CREATE TABLE IF NOT EXISTS `updates_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lead_id` int(11) NOT NULL,
  `lead_create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `has_contract` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `oper_date` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for event dhl.update_leads
DELIMITER //
CREATE DEFINER=`root`@`%` EVENT `update_leads` ON SCHEDULE EVERY 1 DAY STARTS '2013-12-28 02:17:26' ON COMPLETION PRESERVE ENABLE DO BEGIN
insert into expire_leads(lead_id,old_expiration_date,term,new_expiration_date) 
select l.lead_id,l.expiration_date,
round(DATEDIFF(l.expiration_date,l.contraction_date)/365) term,
date_add(l.expiration_date, interval round(DATEDIFF(l.expiration_date,l.contraction_date)/365) year) new_expiration_date 
from leads l
where l.status_id=1 and datediff(now(),l.expiration_date)>=0 and l.term_type=1;
if ROW_COUNT()>0 then
	commit;
	update leads l
	set l.expiration_date=date_add(l.expiration_date, interval round(DATEDIFF(l.expiration_date,l.contraction_date)/365) year)
	where  l.status_id=1 and datediff(now(),l.expiration_date)>=0 and l.term_type=1;
end if;

END//
DELIMITER ;

-- Dumping structure for table dhl.users
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_description` varchar(45) DEFAULT NULL,
  `user_name` varchar(45) NOT NULL,
  `status_id` int(45) NOT NULL,
  `user_password` varchar(200) NOT NULL,
  `type_id` int(11) DEFAULT '0',
  `add_user_id` int(11) DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `oper_id` int(11) DEFAULT NULL,
  `oper_date` date DEFAULT NULL,
  `inv_access` tinyint(4) DEFAULT NULL COMMENT 'წვდომა ინვენტარიზაციაზე',
  `inv_type_id` int(11) DEFAULT NULL COMMENT 'ინვენტარიზაციაზე წვდომის დონე',
  PRIMARY KEY (`user_id`),
  KEY `FK_users_config` (`type_id`),
  KEY `FK_users_user_types` (`inv_type_id`),
  CONSTRAINT `FK_users_config` FOREIGN KEY (`type_id`) REFERENCES `user_types` (`user_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=138 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table dhl.user_permissions
CREATE TABLE IF NOT EXISTS `user_permissions` (
  `user_permission_id` int(10) DEFAULT NULL,
  `user_type_id` int(10) DEFAULT NULL,
  `permission_id` int(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table dhl.user_types
CREATE TABLE IF NOT EXISTS `user_types` (
  `user_type_id` int(5) NOT NULL AUTO_INCREMENT,
  `user_type_name` varchar(200) NOT NULL,
  PRIMARY KEY (`user_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for view dhl.vi_leads
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `vi_leads` (
	`lead_id` INT(11) NOT NULL,
	`company_name` VARCHAR(500) NOT NULL COLLATE 'utf8_general_ci',
	`company_id_number` VARCHAR(50) NOT NULL COLLATE 'utf8_general_ci',
	`address` VARCHAR(5000) NULL COLLATE 'utf8_general_ci',
	`phone` VARCHAR(50) NULL COLLATE 'utf8_general_ci',
	`mail` VARCHAR(250) NULL COLLATE 'utf8_general_ci',
	`contact_person` VARCHAR(255) NULL COLLATE 'utf8_general_ci',
	`contraction_date` DATE NULL,
	`expiration_date` DATE NULL,
	`operator_id` INT(11) NOT NULL COMMENT 'operatoris aidi romelmad daamata',
	`create_date` TIMESTAMP NOT NULL,
	`has_contract` INT(11) NOT NULL COMMENT '1 iani tu aqvs 2 iani tu ar aqvs',
	`sail_or_iurist_id` INT(11) NULL COMMENT 'seilis an iuristis aidi romelmac daamata xelshekruleba',
	`interested_departs` VARCHAR(500) NULL COLLATE 'utf8_general_ci',
	`contr_depart_id` INT(11) NOT NULL,
	`payment_alert` INT(11) NOT NULL,
	`sale_percents` INT(11) NULL,
	`account_number` VARCHAR(50) NULL COLLATE 'utf8_general_ci',
	`oper_date` DATE NULL,
	`note` VARCHAR(3000) NULL COLLATE 'utf8_general_ci',
	`status_id` INT(11) NULL COMMENT '1-aqtiuri 2-pasiuri',
	`term_type` INT(11) NULL COMMENT ' 1-avtomaturi 2-notificationi',
	`answer_id` INT(11) NULL COMMENT '1 undat 2 ar undat',
	`service_type` VARCHAR(500) NULL COLLATE 'utf8_general_ci',
	`limited` INT(11) NULL COMMENT '1 limitirebuli xelshekruleba 2 ara',
	`davalianeba` INT(11) NULL COMMENT '1 ki 2 ara',
	`contract_code` VARCHAR(50) NULL COLLATE 'utf8_general_ci',
	`next_call` DATE NULL,
	`type_id` INT(11) NOT NULL,
	`max_limit` DOUBLE NOT NULL,
	`department_name` VARCHAR(500) NULL COLLATE 'utf8_general_ci',
	`operator_name` VARCHAR(45) NULL COLLATE 'utf8_general_ci',
	`admin_name` VARCHAR(45) NULL COLLATE 'utf8_general_ci'
) ENGINE=MyISAM;

-- Dumping structure for view dhl.vi_lead_depts
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `vi_lead_depts` (
	`lead_dept_id` INT(10) NOT NULL,
	`lead_id` INT(10) NULL,
	`dept_id` INT(10) NULL,
	`acc_number` VARCHAR(10) NULL COLLATE 'utf8_general_ci',
	`sale_percent` INT(10) NULL,
	`department_name` VARCHAR(255) NOT NULL COLLATE 'utf8_general_ci'
) ENGINE=MyISAM;

-- Dumping structure for view dhl.vi_sent_emails
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `vi_sent_emails` (
	`id` INT(11) NOT NULL,
	`lead_id` INT(11) NOT NULL,
	`mail` VARCHAR(100) NOT NULL COLLATE 'utf8_general_ci',
	`note` VARCHAR(100) NULL COLLATE 'utf8_general_ci',
	`confirmed` TINYINT(4) NOT NULL COMMENT '1 არავალიდური /  2 ვალიდური',
	`status` TINYINT(4) NOT NULL COMMENT '1 sent / 2 exception',
	`create_date` TIMESTAMP NOT NULL,
	`body_text` TEXT NULL COLLATE 'utf8_general_ci',
	`subject` VARCHAR(200) NULL COLLATE 'utf8_general_ci',
	`ident_number` VARCHAR(50) NOT NULL COLLATE 'utf8_general_ci',
	`company` VARCHAR(500) NOT NULL COLLATE 'utf8_general_ci'
) ENGINE=MyISAM;

-- Dumping structure for view dhl.vi_sent_sms
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `vi_sent_sms` (
	`id` INT(11) NOT NULL,
	`lead_id` INT(11) NULL,
	`to` VARCHAR(50) NOT NULL COLLATE 'utf8_general_ci',
	`text` TEXT NOT NULL COLLATE 'utf8_general_ci',
	`response` VARCHAR(50) NOT NULL COMMENT 'Return codes:\\r\\n0000 - message_id : Operation successful. message_id is used in\\r\\nmessage status tracking query.\\r\\n0001 : Internal error\\r\\n0003 : Invalid Request\\r\\n0004 : Invalid query\\r\\n0005 : Empty message\\r\\n0006 : Prefix error\\r\\n0007 : MSISDN error\\r\\n' COLLATE 'utf8_general_ci',
	`status_id` INT(11) NOT NULL COMMENT '1 - success / 2 - failed',
	`create_date` TIMESTAMP NOT NULL,
	`lead_data` TEXT NULL COLLATE 'utf8_general_ci'
) ENGINE=MyISAM;

-- Dumping structure for view dhl.vi_users
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `vi_users` (
	`user_id` INT(11) NOT NULL,
	`user_description` VARCHAR(45) NULL COLLATE 'utf8_general_ci',
	`user_name` VARCHAR(45) NOT NULL COLLATE 'utf8_general_ci',
	`status_id` INT(45) NOT NULL,
	`user_password` VARCHAR(200) NOT NULL COLLATE 'utf8_general_ci',
	`type_id` INT(11) NULL,
	`add_user_id` INT(11) NULL,
	`create_date` DATE NULL,
	`oper_id` INT(11) NULL,
	`oper_date` DATE NULL,
	`inv_access` TINYINT(4) NULL COMMENT 'წვდომა ინვენტარიზაციაზე',
	`inv_type_id` INT(11) NULL COMMENT 'ინვენტარიზაციაზე წვდომის დონე',
	`user_type_name` VARCHAR(200) NULL COLLATE 'utf8_general_ci',
	`inv_user_type_name` VARCHAR(200) NULL COLLATE 'utf8_general_ci'
) ENGINE=MyISAM;

-- Dumping structure for view dhl.vi_user_permissions
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `vi_user_permissions` (
	`user_permission_id` INT(10) NULL,
	`user_type_id` INT(10) NULL,
	`permission_id` INT(10) NULL,
	`user_description` VARCHAR(45) NULL COLLATE 'utf8_general_ci',
	`permission_name` VARCHAR(50) NULL COLLATE 'utf8_general_ci'
) ENGINE=MyISAM;

-- Dumping structure for view dhl.vi_leads
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `vi_leads`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `vi_leads` AS select `l`.`lead_id` AS `lead_id`,`l`.`company_name` AS `company_name`,`l`.`company_id_number` AS `company_id_number`,`l`.`address` AS `address`,`l`.`phone` AS `phone`,`l`.`mail` AS `mail`,`l`.`contact_person` AS `contact_person`,`l`.`contraction_date` AS `contraction_date`,`l`.`expiration_date` AS `expiration_date`,`l`.`operator_id` AS `operator_id`,`l`.`create_date` AS `create_date`,`l`.`has_contract` AS `has_contract`,`l`.`sail_or_iurist_id` AS `sail_or_iurist_id`,`l`.`interested_departs` AS `interested_departs`,`l`.`contr_depart_id` AS `contr_depart_id`,`l`.`payment_alert` AS `payment_alert`,`l`.`sale_percents` AS `sale_percents`,`l`.`account_number` AS `account_number`,`l`.`oper_date` AS `oper_date`,`l`.`note` AS `note`,`l`.`status_id` AS `status_id`,`l`.`term_type` AS `term_type`,`l`.`answer_id` AS `answer_id`,`l`.`service_type` AS `service_type`,`l`.`limited` AS `limited`,`l`.`davalianeba` AS `davalianeba`,`l`.`contract_code` AS `contract_code`,`l`.`next_call` AS `next_call`,`l`.`type_id` AS `type_id`,`l`.`max_limit` AS `max_limit`,`l`.`contr_departs` AS `department_name`,`o`.`user_description` AS `operator_name`,`a`.`user_description` AS `admin_name` from ((`leads` `l` left join `users` `o` on((`l`.`operator_id` = `o`.`user_id`))) left join `users` `a` on((`l`.`sail_or_iurist_id` = `a`.`user_id`))) order by `l`.`lead_id` desc;

-- Dumping structure for view dhl.vi_lead_depts
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `vi_lead_depts`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `vi_lead_depts` AS select `ld`.`lead_dept_id` AS `lead_dept_id`,`ld`.`lead_id` AS `lead_id`,`ld`.`dept_id` AS `dept_id`,`ld`.`acc_number` AS `acc_number`,`ld`.`sale_percent` AS `sale_percent`,`d`.`department_name` AS `department_name` from ((`lead_depts` `ld` join `leads` `l` on((`ld`.`lead_id` = `l`.`lead_id`))) join `departments` `d` on((`ld`.`dept_id` = `d`.`department_id`)));

-- Dumping structure for view dhl.vi_sent_emails
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `vi_sent_emails`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vi_sent_emails` AS select `m`.`id` AS `id`,`l`.`lead_id` AS `lead_id`,`m`.`mail` AS `mail`,`m`.`note` AS `note`,`m`.`confirmed` AS `confirmed`,`s`.`status` AS `status`,`s`.`create_date` AS `create_date`,`s`.`body_text` AS `body_text`,`s`.`subject` AS `subject`,`l`.`company_id_number` AS `ident_number`,`l`.`company_name` AS `company` from ((`sent_emails` `s` join `emails` `m` on((`s`.`email_id` = `m`.`id`))) join `leads` `l` on((`m`.`lead_id` = `l`.`lead_id`)));

-- Dumping structure for view dhl.vi_sent_sms
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `vi_sent_sms`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `vi_sent_sms` AS select `s`.`id` AS `id`,`s`.`lead_id` AS `lead_id`,`s`.`to` AS `to`,`s`.`text` AS `text`,`s`.`response` AS `response`,`s`.`status_id` AS `status_id`,`s`.`create_date` AS `create_date`,(case when (`s`.`lead_id` is not null) then (select concat(`l`.`company_name`,'  ( ',`l`.`company_id_number`,' )') from `leads` `l` where (`l`.`lead_id` = `s`.`lead_id`)) else '-' end) AS `lead_data` from `sent_sms` `s`;

-- Dumping structure for view dhl.vi_users
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `vi_users`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `vi_users` AS select `u`.`user_id` AS `user_id`,`u`.`user_description` AS `user_description`,`u`.`user_name` AS `user_name`,`u`.`status_id` AS `status_id`,`u`.`user_password` AS `user_password`,`u`.`type_id` AS `type_id`,`u`.`add_user_id` AS `add_user_id`,`u`.`create_date` AS `create_date`,`u`.`oper_id` AS `oper_id`,`u`.`oper_date` AS `oper_date`,`u`.`inv_access` AS `inv_access`,`u`.`inv_type_id` AS `inv_type_id`,`t`.`user_type_name` AS `user_type_name`,`inv_t`.`user_type_name` AS `inv_user_type_name` from ((`users` `u` left join `user_types` `t` on((`u`.`type_id` = `t`.`user_type_id`))) left join `user_types` `inv_t` on((`u`.`inv_type_id` = `inv_t`.`user_type_id`)));

-- Dumping structure for view dhl.vi_user_permissions
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `vi_user_permissions`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `vi_user_permissions` AS select `up`.`user_permission_id` AS `user_permission_id`,`up`.`user_type_id` AS `user_type_id`,`up`.`permission_id` AS `permission_id`,`u`.`user_description` AS `user_description`,`p`.`permission_name` AS `permission_name` from ((`user_permissions` `up` left join `users` `u` on((`up`.`user_type_id` = `u`.`type_id`))) left join `permissions` `p` on((`up`.`permission_id` = `p`.`persmission_id`)));

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
