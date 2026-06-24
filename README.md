# poc-hexa-architecture
## Business context
This is a banking POC idea that is rich enough to cover concurrency, transactions, and persistence challenges, while still being feasible to implement in 1 to 2 days.

A bank offers a virtual prepaid card service for online payments.

Each card has:
- an available balance,
- a daily spending limit,
- a status (`ACTIVE`, `BLOCKED`),
- an operation history.

## Main use case

An e-commerce partner sends payment authorization requests.

For each request, the system must:
1. Verify that the card exists and is active.
2. Verify that the daily limit has not been exceeded.
3. Verify that the balance is sufficient.
4. Immediately reserve the amount.
5. Return `ACCEPTED` or `REFUSED`.


## Core domain model
- Account
- Card
- Payment (abstract)
- CardPayment (extends Payment)
- PaymentStatus
