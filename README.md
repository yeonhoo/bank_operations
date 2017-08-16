# README #

This is the solution for a exercise proposed by Nubank for software engineer position.

## Main tools

* Playframework 2.6.2
* Scala 2.12.2
* sbt 0.13.15

## Running 

    sbt run

## Running the tests

    sbt test

## Endpoints

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

* request : POST /opertions    
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

