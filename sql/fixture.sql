-- 안전 모드 해제 (다량의 데이터 삽입을 위해)
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- 트랜잭션 시작
START TRANSACTION;

-- 테이블 초기화
TRUNCATE TABLE stock;
TRUNCATE TABLE product;
TRUNCATE TABLE balance;
TRUNCATE TABLE balance_transaction;
TRUNCATE TABLE coupon;
TRUNCATE TABLE user_coupon;
TRUNCATE TABLE orders;
TRUNCATE TABLE order_product;
TRUNCATE TABLE payment;

-- 상품 및 재고 데이터 생성을 위한 프로시저
DELIMITER //
CREATE PROCEDURE generate_product_stock_data()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE product_id INT;

  WHILE i <= 10000 DO
    -- 상품 추가
    INSERT INTO product (name, price, sell_status)
    VALUES (CONCAT('상품명', i), 1000, 'SELLING');

    -- 방금 추가된 상품의 ID 가져오기
    SET product_id = LAST_INSERT_ID();

    -- 재고 추가
    INSERT INTO stock (product_id, quantity)
    VALUES (product_id, 1000);

    SET i = i + 1;
  END WHILE;
END //
DELIMITER ;

-- 잔액 데이터 생성을 위한 프로시저
DELIMITER //
CREATE PROCEDURE generate_balance_data()
BEGIN
  DECLARE i INT DEFAULT 1;

  WHILE i <= 10000 DO
    INSERT INTO balance (user_id, amount, version)
    VALUES (i, 1000000, 0);

    SET i = i + 1;
  END WHILE;
END //
DELIMITER ;

-- 쿠폰 데이터 생성을 위한 프로시저
DELIMITER //
CREATE PROCEDURE generate_coupon_data()
BEGIN
  DECLARE i INT DEFAULT 1;

  INSERT INTO coupon (name, quantity, discount_rate, expired_at, status)
  VALUES (CONCAT('쿠폰명', i), 100000, 0.3, DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY), 'PUBLISHABLE');

END //
DELIMITER ;

-- 프로시저 실행
CALL generate_product_stock_data();
CALL generate_balance_data();
CALL generate_coupon_data();

-- 프로시저 삭제
DROP PROCEDURE IF EXISTS generate_product_stock_data;
DROP PROCEDURE IF EXISTS generate_balance_data;
DROP PROCEDURE IF EXISTS generate_coupon_data;

-- 변경사항 커밋
COMMIT;

-- 안전 모드 복원
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

