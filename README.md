# Getting Started

## How to run

- The application has a valid `Dockerfile` to run the application or tests. It has to be the target for running.
- There is only a single test file called `IntegrationTests` which does a small setup and tests the cases requested in 
document.
- The application is using H2 as database which is a version o in memory SQL.

## Information
 - The application implements 4 entities:
    - Customer, which is the customer using the system
    - Tenant, which represents the organizations
    - Transaction, which represents the transactions that are done for a customer
      - Amount was implemented as an Integer for simplicity sake. Could easily be changed to a decimal if desired
    - AuditLog, keeps track of changes that happen within the application

- There is no authorization or authentication layer and just assumes every request is valid. This would be done by other part of the ecosystem
- The user who made the request would come as part of the header and validated with the authentication layer. So this was disregarded and hardcoded. Every controller simply tells the audit log the user was "userId" 
- Multitenancy is implemented within the application layer and is done by making every request requiring a `tenantId` to be provided
- The application does not assume customer and user are the same thing, due to a service rep being a user but not a customer.
- Database connection was implemented using `JdbcTemplate`. Others could be used but the application is not complex enough to need anything else.

- Reading the instructions again, it seems I misunderstood how the customers are used within the system. I won't change now but my customers are global while it seems that the document required customers to be per tenant.

## API
Pagination was not implemented and the minimum amount of endpoints was created to get the application running. That is why there is not complete CRUD for all endpoints 
### GET /customer
Retrieve all customers
### POST /customer
Create new customers
### GET /tenant
Retrieve all tenants
### POST /tenant
Create new tenants
### GET /audit_log
Retrieving all audit logs.
### GET /tenant/{tenantId}/customer/{customerId}
Retrieve all transactions belonging to a customer within a tenant
### POST /transaction
Creates a new transaction, expects the body to contain the amount, tenantId and customerId
### POST /tenant/{tenantId}/void/customer/{customerId}/transaction/{id}/void
Voids a transaction.