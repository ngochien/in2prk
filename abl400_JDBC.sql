SELECT CUSTOMER.NAME,
  ADDRESS.STREET
FROM CUSTOMER
INNER JOIN ADDRESS
ON ADDRESS.ID = CUSTOMER.HOME_ADDRESS_ID
INNER JOIN BANK_CUSTOMER
ON CUSTOMER.ID = BANK_CUSTOMER.CUSTOMER_ID
INNER JOIN BANK
ON BANK.ID = BANK_CUSTOMER.BANK_ID
INNER JOIN BANK_OFFICE
ON ADDRESS.ID = BANK_OFFICE.ADDRESS_ID
AND BANK.ID   = BANK_OFFICE.BANK_ID