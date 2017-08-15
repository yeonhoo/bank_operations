# README #

This is the solution for a exercise proposed by Nubank for software engineer position.

## Main tools

* Playframework 2.6.2
* Scala 2.12.2
* sbt 0.13.15

## Endpoints

* POST /opertions
    * Insert a new operation
	* json payload format is like : {"op_type": "Credit", "account": "12345", "description": "deposit","amount": 200, "date": "2017-10-17"}
	
* GET /balance/:account
    * Get the balance for the given "account"

* GET /statement/:account/:dateFrom/:dateTo
    * Get the bank statement for the given "account" and the period that is specified by "dateFrom" and "dateTo" 

* GET /debt/:account
    * Get debt period for the given "account" 

## Endpoint request example

* POST /opertions
    * input :
```
#!json    
        {"op_type": "Credit", "account": "12345", "description": "deposit","amount": 200, "date": "2017-10-17"}
```

    * output :
```
#!json
        {
            "status": "Ok", 
            "message": "operation : Credit, description : deposit, amount : 200, date : 2017-10-17 saved."
        }
```

* GET /balance/:account
    * input : /balance/12345
    * output : -328.57

* GET /statement/:account/:dateFrom/:dateTo
    * input : /statement/12345/15102017/17102017
    
    * output : 
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
* GET /debt/:account
    * input : /debt/12345
    
    * output : 
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

## Running 

    sbt run

## Running the tests

    sbt test

