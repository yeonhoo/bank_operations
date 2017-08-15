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

## Running 

    sbt run

## Running the tests

    sbt test

