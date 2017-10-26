## Introduction
Small set of bank operations(deposits, withdrawals, statement, charge interest) written in Scala. This handles transactions in a concurrent-safe way.

A checking account from a bank allows for putting (deposits, salaries, credits)
or taking (purchases, withdrawals, debits) money at any given time.

You can also check what is your current balance or your account statement,
containing all operations that happened between two dates, along with the
account's daily balance.

While you should only be able to take an amount of money that was put there
previously, some banks give you a "free" credit line, so it can lend you some
money instantly, at a "reasonable" interest rate.

## Main tools

* Playframework 2.6.2
* Scala 2.12.2
* sbt 0.13.15

## Implemented operations
 
    
#### 1) Add operations into the checking account    

Add an operation to a given checking account, identified by : account number, description, amount, date
Deposits can take days to be acknowledged properly, this means the dates might not be ordered as operations are inserted.

E.g:

    Deposit 1000.00 at 15/10
    Purchase on Amazon 3.34 at 16/10
    Purchase on Uber 45.23 at 16/10
    Withdrawal 180.00 at 17/10
 
#### 2) Get the current balance

Create a HTTP endpoint which returns the current balance of a given account.
  This balance is the sum of all operations until today, so the customer can
  know how much money they still have.

E.g: for the sample above, the customer would have
     
    1000.00 - 3.34 - 45.23 - 180.00 = 771.43
    
#### 3) Get the bank statement

Returns the bank statement of a period of dates.
  This statement will contain the operations of each day and the balance at the
  end of each day.
  
E.g:
    
    15/10:
    - Deposit 1000.00
    Balance: 1000.00
  
    16/10:
    - Purchase on Amazon 3.34
    - Purchase on Uber 45.23
    Balance: 951.43
  
    17/10:
    - Withdrawal 180.00
    Balance: 771.43
  
#### 4) Compute period of debt

Returns the periods which the account's balance was negative, i.e, periods when the bank can charge interest on that account.


  E.g: if we have two more operations (current balance is 771.43):
  
    Purchase of a flight ticket 800.00 at 18/10
    Deposit 100.00 at 25/10

The endpoint would return:
   
    - Principal: 28.57
    Start: 18/10
    End: 24/10

  This endpoint should return multiple periods, if applicable, and omit the "End:"
  date if the account's balance is currently negative.

## Running 

    sbt run

## Running the tests

    sbt test

## Endpoints

All HTTP endpoints accept and return JSON payloads as requests and responses. There is no HTML visualizations.

### Insert a new operation
    POST /opertions

### Get the balance for the given "account"
    GET /balance/:account


### Get the bank statement for the given "account" and the period which is specified by "dateFrom" and "dateTo" 
    GET /statement/:account/:dateFrom/:dateTo

### Get debt period for the given "account" 
    GET /debt/:account


## Endpoint request example

### Test dataset is given as below 
```
#!scala
private var list: List[Operation] = {
    List(
      Operation(
        "Credit",
        "12345",
        "Deposit",
        1000.00,
        LocalDate.parse("15102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "12345",
        "Purchase on Amazon",
        3.34,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "11111",
        "Purchase on Ebay",
        120.00,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "11111",
        "Purchase on Amazon",
        200.00,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "11111",
        "Withdrawal",
        200.00,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "22222",
        "Purchase on Amazon",
        200.00,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Credit",
        "22222",
        "Deposit",
        500.00,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "54321",
        "Purchase on Uber",
        25.65,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "12345",
        "Purchase on Uber",
        45.23,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "12345",
        "Withdrawal",
        180.00,
        LocalDate.parse("17102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "12345",
        "Purchase flight ticket",
        800.00,
        LocalDate.parse("18102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Credit",
        "12345",
        "Deposit",
        100.00,
        LocalDate.parse("25102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "12345",
        "Withdrawal",
        300.00,
        LocalDate.parse("30102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "12345",
        "Withdrawal",
        400.00,
        LocalDate.parse("03112017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Credit",
        "12345",
        "Deposit",
        100.00,
        LocalDate.parse("05112017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      )
    )
  }
```

### Tests have been done on implemented Endpoints as below 

* request : POST /operation 
```
#!json    
{"op_type": "Credit", "account": "12345", "description": "deposit","amount": 200, "date": "2017-10-17"}
```

* response :
```
#!json
{
    "status": "Ok", 
    "message": "operation : Credit, description : deposit, amount : 200, date : 2017-10-17 saved."
}
```

* request : GET /balance/12345
* response : 
```
#!json
-328.57
```

* request : GET /statement/12345/15102017/17102017    
* response : 

```
#!json
[
    {
        "date": "2017-10-15",
        "info": [
            {
                "description": "Deposit",
                "amount": 1000
            }
        ],
        "balance": 1000
    },
    {
        "date": "2017-10-16",
        "info": [
            {
                "description": "Purchase on Uber",
                "amount": 45.23
            },
            {
                "description": "Purchase on Amazon",
                "amount": 3.34
            }
        ],
        "balance": 951.43
    },
    {
        "date": "2017-10-17",
        "info": [
            {
                "description": "Withdrawal",
                "amount": 180
            },
            {
                "description": "deposit",
                "amount": 200
            }
        ],
        "balance": 971.43
    }
]
```

* request : GET /debt/12345
* response : 
```
#!json
[
    {
        "principal": 28.57,
        "start": "2017-10-18",
        "end": "2017-10-24"
    },
    {
        "principal": 228.57,
        "start": "2017-10-30",
        "end": "2017-11-02"
    },
    {
        "principal": 628.57,
        "start": "2017-11-03",
        "end": "2017-11-04"
    },
    {
        "principal": 528.57,
        "start": "2017-11-05",
        "end": null
    }
]
```

