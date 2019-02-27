/**
 * ==== ADD_MONTHS(date, integer) ====
 * SYSDATE                   ADD_MONTH
 * 2017/9/5 下午 05:09:44    2017/10/5 下午 05:09:44
 */
select SYSDATE, ADD_MONTHS(SYSDATE, 1) ADD_MONTH from dual;

/**
 * ==== LAST_DAY(date) ====
 * SYSDATE                   LAST_DATE                 DAYS_LAFT
 * 2017/9/5 下午 05:33:51    2017/9/30 下午 05:33:51    25
 */
select SYSDATE, LAST_DAY(SYSDATE) LAST_DATE, (LAST_DAY(SYSDATE) - SYSDATE) DAYS_LAFT from dual;

/**
 * ==== NEXT_DAY(date, char) ====
 * SUNDAY:SUN:1, MONDAY:MON:2, TUESDAY:TUE:3, WEDNESDAY:WED:4, THURSDAY:THU:5, FRIDAY:FRI:6, SATURDAY:SAT:7
 *
 * NEXT_TUESDAY
 * 2017/9/12
 */
select NEXT_DAY(TO_DATE('2017/09/05', 'YYYY/MM/DD'), 'TUESDAY') NEXT_TUESDAY from dual;
select NEXT_DAY(TO_DATE('2017/09/05', 'YYYY/MM/DD'), 3) NEXT_TUESDAY from dual;

/*
 * ==== ROUND and TRUNC ====
 * ROUND(date [, format])
 *   1. Oracle 預設使用了按日期時間的格式，當超過了 12:00 AM 這個中界線，日期會進位一天，時間則為 00:00:00
 * TRUNC(date [, format])
 *   1. Oracle 直接截取前面日期部分，時間則回傳 00:00:00
 */
select TO_DATE('2017/10/16 14:32:13', 'YYYY/MM/DD HH24:MI:SS') DATE_TIME from dual;        -- 2017/10/16 14:32:13
-- TRUNC()
select TRUNC(TO_DATE('2017/10/16 11:32:13', 'YYYY/MM/DD HH24:MI:SS')) DATE_TIME from dual; -- 2017/10/16
select TRUNC(TO_DATE('2017/10/16 14:32:13', 'YYYY/MM/DD HH24:MI:SS')) DATE_TIME from dual; -- 2017/10/16
-- ROUND()
select ROUND(TO_DATE('2017/10/16 11:32:13', 'YYYY/MM/DD HH24:MI:SS')) DATE_TIME from dual; -- 2017/10/16
select ROUND(TO_DATE('2017/10/16 14:32:13', 'YYYY/MM/DD HH24:MI:SS')) DATE_TIME from dual; -- 2017/10/17
-- ROUND() with format
select ROUND(TO_DATE('2017/10/16 14:32:13', 'YYYY/MM/DD HH24:MI:SS'), 'YYYY') DATE_TIME from dual;  -- 2018/1/1
select ROUND(TO_DATE('2017/10/16 14:32:13', 'YYYY/MM/DD HH24:MI:SS'), 'Q') DATE_TIME from dual;     -- 2017/10/1
select ROUND(TO_DATE('2017/10/16 14:32:13', 'YYYY/MM/DD HH24:MI:SS'), 'Month') DATE_TIME from dual; -- 2017/11/1
select ROUND(TO_DATE('2017/10/16 14:32:13', 'YYYY/MM/DD HH24:MI:SS'), 'WW') DATE_TIME from dual;    -- 2017/10/15
select ROUND(TO_DATE('2017/10/16 14:32:13', 'YYYY/MM/DD HH24:MI:SS'), 'DD') DATE_TIME from dual;    -- 2017/10/17
select ROUND(TO_DATE('2017/10/16 14:32:13', 'YYYY/MM/DD HH24:MI:SS'), 'Day') DATE_TIME from dual;   -- 2017/10/15
select ROUND(TO_DATE('2017/10/16 14:32:13', 'YYYY/MM/DD HH24:MI:SS'), 'HH12') DATE_TIME from dual;  -- 2017/10/16 下午 03:00:00
select ROUND(TO_DATE('2017/10/16 14:32:13', 'YYYY/MM/DD HH24:MI:SS'), 'HH24') DATE_TIME from dual;  -- 2017/10/16 下午 03:00:00
select ROUND(TO_DATE('2017/10/16 14:32:13', 'YYYY/MM/DD HH24:MI:SS'), 'MI') DATE_TIME from dual;    -- 2017/10/16 下午 02:32:00

/**
 * ==== 時間間隔 ====
 *   date1 - date2             -> Date   (        date1 - date2      >= 1  -> date1 和 date2 區間大於等於 1 天 )
 *   (date1 - date2) * 24      -> Hour   ( (date1 - date2) * 24      >= 10 -> date1 和 date2 間隔大於等於 10 小時 )
 *   (date1 - date2) * 24 * 60 -> Minute ( (date1 - date2) * 24 * 60 >= 30 -> date1 和 date2 間隔大於等於 30 分鐘 )
 */
select
  TO_DATE('2019/01/01 07:30:31', 'yyyy/mm/dd hh24:mi:ss') THE_DATE,
  TO_NUMBER(TO_DATE('2019/01/01 07:30:31', 'yyyy/mm/dd hh24:mi:ss') - TO_DATE('2019/01/01 06:00:00', 'yyyy/mm/dd hh24:mi:ss')) as DAYS, -- 1.53171296296296
  TRUNC(TO_NUMBER(TO_DATE('2019/01/01 07:30:31', 'yyyy/mm/dd hh24:mi:ss') - TO_DATE('2019/01/01 06:00:00', 'yyyy/mm/dd hh24:mi:ss'))) DAYS_TRUNC, -- 1
  ROUND(TO_NUMBER(TO_DATE('2019/01/01 07:30:31', 'yyyy/mm/dd hh24:mi:ss') - TO_DATE('2019/01/01 06:00:00', 'yyyy/mm/dd hh24:mi:ss'))) DAYS_ROUND, -- 2
  TO_NUMBER(TO_DATE('2019/01/01 07:30:31', 'yyyy/mm/dd hh24:mi:ss') - TO_DATE('2019/01/01 06:00:00', 'yyyy/mm/dd hh24:mi:ss')) * 24 as HOURS, -- 36.7611111111111
  TRUNC(TO_NUMBER(TO_DATE('2019/01/01 07:30:31', 'yyyy/mm/dd hh24:mi:ss') - TO_DATE('2019/01/01 06:00:00', 'yyyy/mm/dd hh24:mi:ss')) * 24) HOURS_TRUNC, -- 36
  ROUND(TO_NUMBER(TO_DATE('2019/01/01 07:30:31', 'yyyy/mm/dd hh24:mi:ss') - TO_DATE('2019/01/01 06:00:00', 'yyyy/mm/dd hh24:mi:ss')) * 24) HOURS_ROUND, -- 37
  TO_NUMBER(TO_DATE('2019/01/01 07:30:31', 'yyyy/mm/dd hh24:mi:ss') - TO_DATE('2019/01/01 06:00:00', 'yyyy/mm/dd hh24:mi:ss')) * 24 * 60 as MINUTES,    -- 2205.66666666667
  TRUNC(TO_NUMBER(TO_DATE('2019/01/01 07:30:31', 'yyyy/mm/dd hh24:mi:ss') - TO_DATE('2019/01/01 06:00:00', 'yyyy/mm/dd hh24:mi:ss')) * 24 * 60) MINUTES_TRUNC, -- 2205
  ROUND(TO_NUMBER(TO_DATE('2019/01/01 07:30:31', 'yyyy/mm/dd hh24:mi:ss') - TO_DATE('2019/01/01 06:00:00', 'yyyy/mm/dd hh24:mi:ss')) * 24 * 60) MINUTES_ROUND, -- 2206
  TO_NUMBER(TO_DATE('2019/01/01 07:30:31', 'yyyy/mm/dd hh24:mi:ss') - TO_DATE('2019/01/01 06:00:00', 'yyyy/mm/dd hh24:mi:ss')) * 24 * 60 * 60 as SECONDS, -- 132340
  TRUNC(TO_NUMBER(TO_DATE('2019/01/01 07:30:31', 'yyyy/mm/dd hh24:mi:ss') - TO_DATE('2019/01/01 06:00:00', 'yyyy/mm/dd hh24:mi:ss')) * 24 * 60 * 60) SECONDS_TRUNC, --132339
  ROUND(TO_NUMBER(TO_DATE('2019/01/01 07:30:31', 'yyyy/mm/dd hh24:mi:ss') - TO_DATE('2019/01/01 06:00:00', 'yyyy/mm/dd hh24:mi:ss')) * 24 * 60 * 60) SECONDS_ROUND --132340
from dual
;

--First day of current week(sunday)
select TRUNC(SYSDATE, 'Day') from dual;
--First day of next week(sunday)
select TRUNC(SYSDATE + 7 , 'Day') from dual;
--First day of previous week(sunday)
select TRUNC(SYSDATE - 7, 'Day') from dual;
--First day of current month
select TRUNC(SYSDATE, 'Month') from dual;
--First day of previous month
select TRUNC(TRUNC(SYSDATE , 'Month') - 1, 'Month') from dual;
--First day of next month
select TRUNC(LAST_DAY(SYSDATE) + 1, 'Month') from dual;
--First day of current year
select TRUNC(SYSDATE, 'Year') from dual;
--First day of previous year
select TRUNC(TRUNC(SYSDATE, 'Year') - 1 , 'Year') from dual;
--First day of next year
select ADD_MONTHS(TRUNC(SYSDATE, 'Year'), 12) from dual;
-- First Day of Current quater
select TRUNC(SYSDATE, 'Q') from dual;
--  First Day of Previous Quarter
select ADD_MONTHS(TRUNC(SYSDATE, 'Q'), -3) from dual;
--  First Day of Next Quarter
select ADD_MONTHS(TRUNC(SYSDATE, 'Q'), 3) from dual;

--Last day of current week(sunday)
select TRUNC(SYSDATE, 'Day') + 6 from dual;
--Last day of next week(sunday)
select TRUNC(SYSDATE + 7, 'Day') + 6 from dual;
--Last day of previous week(sunday)
select TRUNC(SYSDATE - 7, 'Day') + 6 from dual;
--Last day of current month
select LAST_DAY(TRUNC(SYSDATE, 'Month')) from dual;
--Last day of previous month
select LAST_DAY(TRUNC(TRUNC(SYSDATE, 'Month') - 1 , 'Month')) from dual;
--Last day of next month
select LAST_DAY(TRUNC(LAST_DAY(SYSDATE) + 1, 'Month')) from dual;
--Last day of current year
select LAST_DAY(ADD_MONTHS(TRUNC(SYSDATE, 'Year'), 11)) from dual;
--Last day of previous year
select LAST_DAY(ADD_MONTHS(TRUNC(TRUNC(SYSDATE, 'Year') - 1 , 'Year'), 11)) from dual;
--Last day of next year
select LAST_DAY(ADD_MONTHS(TRUNC(TRUNC(SYSDATE, 'Year') - 1 , 'Year'), -13)) from dual;
-- Last Day of Current quater
select LAST_DAY(ADD_MONTHS(TRUNC(SYSDATE, 'Q'), 2)) from dual;
--  Last Day of Previous Quarter
select TRUNC(SYSDATE, 'Q') - 1 from dual;
--  Last Day of Next Quarter
select LAST_DAY(ADD_MONTHS(TRUNC(SYSDATE, 'Q'), 5)) from dual;


/* TO_CHAR( value [, format_mask] [, nls_language] ) */
select
  TO_CHAR(0.3, '000.0'),   -- 001.3
  TO_CHAR(0.3, '000.9'),   -- 000.3
  TO_CHAR(0.3, '990.9'),   --   0.3
  TO_CHAR(0.3, '000.000'), -- 000.300
  TO_CHAR(0.3, '999.099'), --    .300
  TO_CHAR(0.3, '999.999')  --    .300
from dual
; -- 查詢的結果會有空白
-- https://www.techonthenet.com/oracle/index.php
-- https://docs.oracle.com/cd/B19306_01/server.102/b14200/sql_elements004.htm
