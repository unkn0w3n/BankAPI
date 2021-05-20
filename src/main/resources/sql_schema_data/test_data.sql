/* Table of account */
INSERT INTO accounts (title, number, currency, user_id, balance) VALUES
('Personal account',            '40817810255863910001', 'RUB', 1, 101000),
('Saving account',              '40817810255863910002', 'RUB', 1, 1539000),
('Personal account',            '40817810255863910003', 'RUB', 2, 1539000),
('SPEC Commercial account',     '40817810255863910004', 'RUB', 3, 11001000),
('DHL Commercial  account',     '40817810255863910005', 'RUB', 4, 49005700);


/* Cards table */
INSERT INTO cards(account_id, type, title, number, currency, `limit`, approved, active) VALUES
(1, 'VISA', 'VISA CLASSIC',             '4377759828000001', 'RUB', 100000, true, true),
(1, 'VISA', 'VISA INFINITE',            '4377759828000002', 'RUB', 900000, true, true),
(2, 'VISA', 'VISA CLASSIC',             '4377759828000003', 'RUB', 900000, true, true),
(3, 'VISA', 'VISA CLASSIC COMMERCIAL',  '4377759828000004', 'RUB', 300000, true, true),
(4, 'VISA', 'MASTERCARD COMMERCIAL',    '5213759828000001', 'RUB', 400000, true, true);


/* Payments */
INSERT INTO transactions(t_type, account_from, account_to, amount, approved_by_id, status, created_at, updated_at) VALUES
('EXTERNAL_P2P'       ,'40817810255863910001', '40817810255863910003', 100501, 4,    'APPROVED_BY_OPERATOR', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('INTERNAL'           ,'40817810255863910001', '40817810255863910002', 201301, 777,  'AUTO_APPROVED', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('CONTRACT_B2B'       ,'40817810255863910004', '40817810255863910005', 501301, 777,  'AUTO_APPROVED', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());


/* users */
INSERT INTO users(login, password, full_name, phone, role) VALUES
('p_ivanov'      ,'123123',  'Иванов Иван Иванович',      '79811888777',   'PERSONAL'),
('p_petrov'      ,'123123',  'Петров Сергей Констанивич', '79117095511',   'PERSONAL'),
('corp_dhl'      ,'123123',  'DHL CORPORATE ACCOUNT',     '79117095755',   'CORP'),
('corp_spec'     ,'123123',  'SPEC CORPORATE ACCOUNT',    '79141115353',   'CORP'),
('oper_007'      ,'123123',  'Григорьев Иван Андреевич',  '79057770101',   'OPERATOR');


/* logs */
INSERT INTO logs(type, message) VALUES
('error'         ,'Попытка входа: пользователя p_ivanov c неправильным паролем'),
('warning'       ,'p_petrov заблокирован. 3 раза введен неверный пароль'),
('info'          ,'транзакция с id=30 одобрен оператором Григорьев Иван[id=5]'),
('info'          ,'В систему доавблен новый контракт');


/* contracts */
INSERT INTO contracts(unique_id, description, user_id_from, user_id_to, account_from, account_to, c_status) VALUES
('c64e6372-1724-45fe-922d-56842c91410e', 'Контракт на поставку цемента', 3, 4, '40817810255863910004', '40817810255863910004', 'DONE');








