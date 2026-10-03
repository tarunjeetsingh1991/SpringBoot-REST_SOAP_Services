# Tree Management Application

A Spring Boot application demonstrating a complete 
**Tree Management System** 
using both **REST API** and **SOAP Web Services** 
over the same persistence layer.

The project demonstrates:

- Spring Boot
- Spring MVC / REST API
- Spring Web Services (SOAP)
- Spring Data JPA
- Hibernate
- MySQL
- JAXB / XSD
- WSDL
- Maven
- Global Exception Handling
- SOAP Fault Handling
- Spring AOP
- Transaction Management
- Lazy Collection Handling

---

# 1. Project Overview

The application manages `Tree` records stored in a MySQL database.

Each tree contains:

- ID
- Name
- Category
- List of branches

Example:

```json
{
    "id": 101,
    "name": "FirstTree",
    "category": "One",
    "branches": [
        "Branch-1",
        "Branch-2",
        "Branch-3"
    ]
}
```

The same data can be accessed using either:

```text
REST API
   ↓
JSON

or

SOAP API
   ↓
XML
```

Both APIs ultimately communicate with the same MySQL database.

---

# 2. Application Architecture

```text
                         CLIENT
                           │
             ┌─────────────┴─────────────┐
             │                           │
          REST Client                 SOAP Client
          (Postman)                   (Postman)
             │                           │
             ▼                           ▼
      TreeController                TreeEndpoint
             │                           │
             ▼                           ▼
        TreeService                TreeSoapService
             │                           │
             └────────────┬──────────────┘
                          │
                          ▼
                  TreeRepository
                          │
                          ▼
                    Spring Data JPA
                          │
                          ▼
                       Hibernate
                          │
                          ▼
                        MySQL
```

Spring AOP intercepts calls to both service classes:

```text
TreeService
     ↑
Spring AOP

TreeSoapService
     ↑
Spring AOP
```

This allows logging, execution-time measurement, return-value inspection, and exception logging without putting those concerns directly inside the service methods.

---

# 3. Project Structure

```text
TestSpringPersistent
│
├── pom.xml
│
├── mvnw
├── mvnw.cmd
│
└── src
    └── main
        ├── java
        │   └── com.testSpring
        │
        │       ├── TestSpringPersistentApplication.java
        │       │
        │       ├── aspect
        │       │   └── TreeServiceAspect.java
        │       │
        │       ├── config
        │       │   └── WebServiceConfig.java
        │       │
        │       ├── controller
        │       │   └── TreeController.java
        │       │
        │       ├── endpoint
        │       │   └── TreeEndpoint.java
        │       │
        │       ├── exception
        │       │   ├── ErrorResponse.java
        │       │   ├── GlobalExceptionHandler.java
        │       │   ├── SoapExceptionResolver.java
        │       │   ├── TreeAlreadyExistsException.java
        │       │   └── TreeNotFoundException.java
        │       │
        │       ├── model
        │       │   └── TreeModel.java
        │       │
        │       ├── repository
        │       │   └── TreeRepository.java
        │       │
        │       └── service
        │           ├── TreeService.java
        │           └── TreeSoapService.java
        │
        └── resources
            ├── application.properties
            ├── trees.xsd
            ├── static
            └── templates
```

JAXB-generated SOAP classes are created automatically during the Maven build:

```text
target
└── generated-sources
    └── jaxb
        └── com
            └── testSpring
                └── soap
                    └── generated
```

Generated classes include:

```text
Tree.java

GetTreeRequest.java
GetTreeResponse.java

GetAllTreesRequest.java
GetAllTreesResponse.java

AddTreeRequest.java
AddTreeResponse.java

AddMultipleTreesRequest.java
AddMultipleTreesResponse.java

UpdateTreeRequest.java
UpdateTreeResponse.java

PatchTreeRequest.java
PatchTreeResponse.java

DeleteTreeRequest.java
DeleteTreeResponse.java

ObjectFactory.java
package-info.java
```

These files should not normally be edited manually because Maven regenerates them from `trees.xsd`.

---

# 4. Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 3.5.5 | Application framework |
| Spring MVC | REST API |
| Spring Web Services | SOAP API |
| Spring Data JPA | Repository abstraction |
| Hibernate | ORM |
| MySQL | Database |
| JAXB | XML-to-Java binding |
| XSD | SOAP message contract |
| WSDL | SOAP service description |
| Spring AOP | Cross-cutting concerns |
| AspectJ annotations | Pointcuts and advice |
| Maven | Dependency management and build |
| Postman | REST/SOAP testing |

---

# 5. Domain Model

The main JPA entity is:

```text
TreeModel
```

The entity contains:

```text
id
name
category
branches
```

The `branches` field is represented as:

```java
@ElementCollection
```

and stored separately from the main `trees` table.

The relationship is conceptually:

```text
trees
  │
  │ id
  ▼
tree_branches
```

Example database data:

```text
trees

+-----+----------+------------+
| id  | category | name       |
+-----+----------+------------+
| 101 | One      | FirstTree  |
| 201 | Two      | SecondTree |
| 301 | Three    | ThirdTree  |
| 601 | Fourth   | FinalTree  |
+-----+----------+------------+
```

Branches are stored in the associated `tree_branches` table.

---

# 6. Repository Layer

The repository is:

```text
TreeRepository
```

It extends:

```java
JpaRepository<TreeModel, Integer>
```

Therefore Spring Data JPA automatically provides operations such as:

```java
findAll()
findById()
save()
saveAll()
existsById()
deleteById()
```

No manual SQL is required for these standard CRUD operations.

---

# 7. REST Service Layer

REST business logic is handled by:

```text
TreeService
```

It provides operations for:

```text
Get all trees
Get one tree
Add a tree
Add multiple trees
Update a tree
Patch a tree
Delete a tree
```

The service communicates with:

```text
TreeRepository
```

instead of communicating directly with Hibernate or MySQL.

---

# 8. REST Controller

REST requests are handled by:

```text
TreeController
```

The application provides the following REST operations:

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/home` | Test/welcome endpoint |
| GET | `/all` | Get all trees |
| GET | `/all/{id}` | Get tree by ID |
| POST | `/add` | Add one tree |
| POST | `/addAll` | Add multiple trees |
| PUT | `/update/{id}` | Fully update tree |
| PATCH | `/patch/{id}` | Partially update tree |
| DELETE | `/delete/{id}` | Delete tree |
| GET | `/count` | Get total number of trees |

---

# 9. Example REST Request

## Get All Trees

```text
GET /all
```

Example response:

```json
[
    {
        "id": 101,
        "name": "FirstTree",
        "category": "One",
        "branches": [
            "Branch-1",
            "Branch-2",
            "Branch-3"
        ]
    },
    {
        "id": 201,
        "name": "SecondTree",
        "category": "Two",
        "branches": [
            "Branch-1",
            "Branch-2"
        ]
    }
]
```

---

# 10. SOAP Web Service

SOAP support was added alongside the existing REST API.

The REST implementation was not replaced.

Therefore the application supports both:

```text
REST
+
SOAP
```

against the same data.

SOAP requests are handled by:

```text
TreeEndpoint
```

and SOAP-specific business logic is handled by:

```text
TreeSoapService
```

---

# 11. SOAP Configuration

SOAP configuration is contained in:

```text
WebServiceConfig
```

The Spring Web Services `MessageDispatcherServlet` handles SOAP requests.

The SOAP base path is:

```text
/ws
```

The SOAP namespace is:

```text
http://testSpring.com/trees
```

The WSDL is generated from the XSD configuration.

---

# 12. XSD Contract

The SOAP contract is defined in:

```text
src/main/resources/trees.xsd
```

The XSD defines the SOAP request and response structures for operations including:

```text
getTreeRequest
getTreeResponse

getAllTreesRequest
getAllTreesResponse

addTreeRequest
addTreeResponse

addMultipleTreesRequest
addMultipleTreesResponse

updateTreeRequest
updateTreeResponse

patchTreeRequest
patchTreeResponse

deleteTreeRequest
deleteTreeResponse
```

---

# 13. JAXB Code Generation

SOAP DTO classes are generated from:

```text
trees.xsd
```

using the JAXB Maven plugin.

Run:

```bash
./mvnw clean compile
```

Maven generates the classes under:

```text
target/generated-sources/jaxb
```

For example:

```text
AddTreeRequest.java
AddTreeResponse.java
GetTreeRequest.java
GetTreeResponse.java
Tree.java
```

A successful build should end with:

```text
BUILD SUCCESS
```

---

# 14. SOAP Endpoint

`TreeEndpoint` maps incoming SOAP messages to Java methods using:

```java
@Endpoint
```

and:

```java
@PayloadRoot
```

For example:

```java
@PayloadRoot(
    namespace = NAMESPACE,
    localPart = "getTreeRequest"
)
```

means that an incoming SOAP body containing:

```xml
<tre:getTreeRequest>
```

is routed to the corresponding endpoint method.

---

# 15. Example SOAP Request

To retrieve tree `101`:

```xml
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:tre="http://testSpring.com/trees">

    <soapenv:Header/>

    <soapenv:Body>

        <tre:getTreeRequest>
            <tre:id>101</tre:id>
        </tre:getTreeRequest>

    </soapenv:Body>

</soapenv:Envelope>
```

---

# 16. Example SOAP Response

```xml
<SOAP-ENV:Envelope
    xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">

    <SOAP-ENV:Header/>

    <SOAP-ENV:Body>

        <ns2:getTreeResponse
            xmlns:ns2="http://testSpring.com/trees">

            <ns2:tree>

                <ns2:id>101</ns2:id>

                <ns2:name>
                    FirstTree
                </ns2:name>

                <ns2:category>
                    One
                </ns2:category>

                <ns2:branches>
                    Branch-1
                </ns2:branches>

                <ns2:branches>
                    Branch-2
                </ns2:branches>

                <ns2:branches>
                    Branch-3
                </ns2:branches>

            </ns2:tree>

        </ns2:getTreeResponse>

    </SOAP-ENV:Body>

</SOAP-ENV:Envelope>
```

---

# 17. Lazy Loading and SOAP

`TreeModel.branches` is an element collection.

When the entity was originally loaded and later converted to the SOAP DTO outside the Hibernate session, Hibernate produced an error similar to:

```text
failed to lazily initialize a collection of role:
com.testSpring.model.TreeModel.branches:
could not initialize proxy - no Session
```

The SOAP service solves this by using transactions and initializing the collection while the Hibernate session is still active.

For example:

```java
@Transactional(readOnly = true)
public Optional<TreeModel> getTreeById(int id) {

    Optional<TreeModel> tree =
            repo.findById(id);

    tree.ifPresent(
            t -> t.getBranches().size()
    );

    return tree;
}
```

This causes Hibernate to retrieve the branches before leaving the transaction.

The console can therefore show two queries:

```text
Hibernate:
select ...
from trees
where id=?

Hibernate:
select ...
from tree_branches
where tree_id=?
```

---

# 18. Global Exception Handling

The project contains custom exception handling for REST and SOAP.

Custom exceptions include:

```text
TreeNotFoundException
TreeAlreadyExistsException
```

This avoids returning `null` or silently returning empty responses when an operation fails.

---

# 19. REST Global Exception Handling

REST exceptions are handled by:

```text
GlobalExceptionHandler
```

For example:

```text
GET /all/9999
```

can cause:

```java
throw new TreeNotFoundException(...);
```

The global exception handler converts this into an appropriate HTTP error response.

Conceptually:

```text
REST Request
     ↓
TreeController
     ↓
TreeService
     ↓
TreeNotFoundException
     ↓
GlobalExceptionHandler
     ↓
HTTP Error Response
```

Example:

```json
{
    "status": 404,
    "error": "Not Found",
    "message": "Tree not found with id: 9999",
    "path": "/all/9999"
}
```

---

# 20. SOAP Exception Handling

SOAP exceptions are handled separately using:

```text
SoapExceptionResolver
```

REST errors should be represented as HTTP/JSON responses, while SOAP errors should be represented using SOAP Faults.

Conceptually:

```text
SOAP Request
     ↓
TreeEndpoint
     ↓
TreeSoapService
     ↓
TreeNotFoundException
     ↓
SoapExceptionResolver
     ↓
SOAP Fault
```

This maintains the correct error format for each API style.

---

# 21. Spring AOP

Spring AOP was added using:

```text
spring-boot-starter-aop
```

The main aspect is:

```text
TreeServiceAspect
```

It intercepts methods from both:

```text
TreeService
TreeSoapService
```

---

# 22. AOP Concepts Demonstrated

The project demonstrates the following Spring AOP concepts.

## Aspect

The class containing cross-cutting logic:

```java
@Aspect
@Component
public class TreeServiceAspect
```

## Pointcut

Defines which methods should be intercepted.

REST service:

```java
@Pointcut(
    "execution(* com.testSpring.service.TreeService.*(..))"
)
```

SOAP service:

```java
@Pointcut(
    "execution(* com.testSpring.service.TreeSoapService.*(..))"
)
```

The two pointcuts are combined so that the same logging and timing behavior can apply to both APIs.

---

# 23. AOP Advice Types

The aspect demonstrates:

```text
@Before
@After
@AfterReturning
@AfterThrowing
@Around
```

## `@Before`

Runs before the service method.

Example:

```text
AOP @Before
Method: getOne
Arguments: [101]
```

## `@AfterReturning`

Runs only when the method completes successfully.

Example:

```text
AOP @AfterReturning
Method completed successfully: getOne
Returned value:
Optional[TreeModel [...]]
```

## `@After`

Runs after the intercepted method exits, whether it returns normally or throws an exception.

Example:

```text
AOP @After
Method finished: getOne
```

## `@AfterThrowing`

Runs when the intercepted method throws an exception.

It is used for logging the exception rather than converting it into an HTTP or SOAP response.

REST errors continue to:

```text
GlobalExceptionHandler
```

SOAP errors continue to:

```text
SoapExceptionResolver
```

## `@Around`

Surrounds the method execution and can control when the original method is executed.

The project uses it to measure execution time.

Example:

```text
AOP @Around
Method: getOne
Execution time: 32.05775 ms
```

---

# 24. AOP Example Output

Calling:

```text
GET /all/101
```

produced:

```text
AOP @Before
Method: getOne
Arguments: [101]
========================================

Hibernate:
select tm1_0.id,
       tm1_0.category,
       tm1_0.name
from trees tm1_0
where tm1_0.id=?

Hibernate:
select b1_0.tree_id,
       b1_0.branch
from tree_branches b1_0
where b1_0.tree_id=?

AOP @AfterReturning
Method completed successfully: getOne

Returned value:
Optional[
    TreeModel [
        id=101,
        name=FirstTree,
        category=One,
        branches=[
            Branch-1,
            Branch-2,
            Branch-3
        ]
    ]
]

AOP @After
Method finished: getOne

AOP @Around
Method: getOne
Execution time: 32.05775 ms
```

This confirms that Spring AOP is intercepting the service call successfully.

---

# 25. AOP Execution Flow

The successful REST execution can be visualized as:

```text
GET /all/101
       │
       ▼
TreeController
       │
       ▼
Spring AOP Proxy
       │
       ├── @Around starts timer
       │
       ├── @Before
       │
       ▼
TreeService.getOne(101)
       │
       ▼
TreeRepository
       │
       ▼
Hibernate
       │
       ▼
MySQL
       │
       ▼
@AfterReturning
       │
       ▼
@After
       │
       ▼
@Around calculates execution time
       │
       ▼
TreeController
       │
       ▼
JSON Response
```

For an exception:

```text
GET /all/9999
       │
       ▼
TreeController
       │
       ▼
AOP Proxy
       │
       ├── @Before
       │
       ▼
TreeService
       │
       ▼
TreeNotFoundException
       │
       ├── @AfterThrowing
       ├── @After
       └── @Around timing
       │
       ▼
GlobalExceptionHandler
       │
       ▼
404 Response
```

---

# 26. AOP and Exception Handling

AOP is **not** being used as a replacement for global exception handling.

The responsibilities are separated:

```text
TreeServiceAspect
      │
      └── Logging
          Performance measurement
          Method monitoring
          Exception logging
```

while:

```text
GlobalExceptionHandler
      │
      └── REST error responses
```

and:

```text
SoapExceptionResolver
      │
      └── SOAP Fault responses
```

This keeps cross-cutting concerns separate from API-specific error representation.

---

# 27. Transaction Management

`TreeSoapService` uses:

```java
@Transactional
```

and read-only operations can use:

```java
@Transactional(readOnly = true)
```

This ensures that the Hibernate persistence context remains available while required entity data is accessed.

Transaction management is especially important for the lazily loaded `branches` collection.

Spring transaction management also uses proxy-based infrastructure, making it conceptually related to the proxy mechanism demonstrated by Spring AOP.

---

# 28. REST vs SOAP in This Project

| REST | SOAP |
|---|---|
| `TreeController` | `TreeEndpoint` |
| `TreeService` | `TreeSoapService` |
| JSON | XML |
| HTTP methods | SOAP operations |
| URLs identify operations/resources | XML payload identifies operation |
| `@RestController` | `@Endpoint` |
| `@GetMapping` | `@PayloadRoot` |
| `@PostMapping` | `@PayloadRoot` |
| `@RequestBody` | `@RequestPayload` |
| `ResponseEntity` | SOAP response DTO |
| GlobalExceptionHandler | SoapExceptionResolver |
| JSON errors | SOAP Faults |

Both eventually use:

```text
TreeRepository
      ↓
Hibernate
      ↓
MySQL
```

---

# 29. Running the Application

Make sure MySQL is running and that the database configuration in:

```text
src/main/resources/application.properties
```

matches your local MySQL configuration.

Build the project:

```bash
./mvnw clean compile
```

A successful build should display:

```text
BUILD SUCCESS
```

Then run:

```bash
./mvnw spring-boot:run
```

Alternatively, run:

```text
TestSpringPersistentApplication.java
```

directly from Eclipse as a Spring Boot application or Java application.

---

# 30. Eclipse Setup

Import the project using:

```text
File
  ↓
Import
  ↓
Maven
  ↓
Existing Maven Projects
```

Select the project directory containing:

```text
pom.xml
```

After import:

```text
Right-click project
  ↓
Maven
  ↓
Update Project
```

If necessary, select:

```text
Force Update of Snapshots/Releases
```

Then run Maven compilation so JAXB generates the SOAP classes.

```bash
./mvnw clean compile
```

Refresh the project in Eclipse afterward if the generated classes are not immediately visible.

---

# 31. Testing With Postman

The project can be tested with Postman for both REST and SOAP.

For REST requests, use:

```text
Content-Type: application/json
```

for operations containing JSON request bodies.

Example:

```json
{
    "id": 1101,
    "name": "Pine",
    "category": "Evergreen",
    "branches": [
        "North Branch",
        "South Branch"
    ]
}
```

For SOAP requests, use an XML body and an appropriate XML/SOAP content type.

Example body:

```xml
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:tre="http://testSpring.com/trees">

    <soapenv:Header/>

    <soapenv:Body>

        <tre:getTreeRequest>
            <tre:id>101</tre:id>
        </tre:getTreeRequest>

    </soapenv:Body>

</soapenv:Envelope>
```

---

# 32. Maven Dependencies

The project includes dependencies for the major application features:

```text
Spring Boot Web
Spring Data JPA
MySQL Connector
Spring Web Services
WSDL4J
Spring AOP
Testing
Lombok
```

Spring AOP support is provided by:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

Since the project uses the Spring Boot parent, a separate version does not need to be specified for the starter.

---

# 33. Important Design Principle

The project separates responsibilities into layers:

```text
Controller / Endpoint
        │
        │ API communication
        ▼
Service
        │
        │ Business logic
        ▼
Repository
        │
        │ Persistence
        ▼
Database
```

Cross-cutting concerns are handled separately:

```text
Aspect
   ├── Logging
   ├── Method interception
   ├── Execution timing
   └── Exception logging
```

Error representation is also separated:

```text
REST
 ↓
GlobalExceptionHandler

SOAP
 ↓
SoapExceptionResolver
```

This prevents controllers, endpoints, and services from becoming overloaded with unrelated responsibilities.

---

# 34. Features Demonstrated

This project now demonstrates:

- REST CRUD operations
- SOAP CRUD-style operations
- REST and SOAP running in the same Spring Boot application
- MySQL persistence
- Spring Data JPA repositories
- Hibernate ORM
- `@ElementCollection`
- Lazy loading
- Transaction management
- JAXB code generation
- XSD-first SOAP development
- WSDL generation
- SOAP request/response handling
- Entity-to-SOAP DTO conversion
- SOAP DTO-to-entity conversion
- REST global exception handling
- SOAP Fault handling
- Custom exceptions
- Spring AOP
- Pointcuts
- Join points
- `JoinPoint`
- `ProceedingJoinPoint`
- `@Before`
- `@After`
- `@AfterReturning`
- `@AfterThrowing`
- `@Around`
- Execution-time measurement
- Service-layer logging
- Maven build lifecycle
- Postman REST testing
- Postman SOAP testing

---

# 35. Final Architecture

```text
                         CLIENTS
                            │
              ┌─────────────┴─────────────┐
              │                           │
           REST/JSON                   SOAP/XML
              │                           │
              ▼                           ▼
       TreeController                TreeEndpoint
              │                           │
              ▼                           ▼
        ┌────────────┐              ┌───────────────┐
        │TreeService │              │TreeSoapService│
        └─────┬──────┘              └───────┬───────┘
              │                             │
              └─────────────┬───────────────┘
                            │
                      Spring AOP Proxy
                            │
                 ┌──────────┼──────────┐
                 │          │          │
              Logging     Timing    Exception
                                     Logging
                            │
                            ▼
                     TreeRepository
                            │
                            ▼
                    Spring Data JPA
                            │
                            ▼
                       Hibernate
                            │
                            ▼
                         MySQL
```

The application therefore provides two API styles over the same underlying domain and persistence model while demonstrating Spring's service, persistence, transaction, exception-handling, SOAP, REST, and AOP capabilities.



# 36. Automated Testing

The project includes an automated testing suite covering the REST controller, REST service, SOAP service, SOAP endpoint, Spring AOP aspect, and an end-to-end REST integration test.

The testing strategy combines:

- JUnit 5
- Mockito
- Spring MockMvc
- Spring Boot integration testing
- H2 in-memory database
- JaCoCo code coverage

This provides multiple levels of testing rather than relying only on manual Postman testing.

The overall testing approach is:

```text
                    Testing Strategy
                           │
          ┌────────────────┴────────────────┐
          │                                 │
          ▼                                 ▼
     Unit Testing                   Integration Testing
          │                                 │
          ├── TreeServiceTest               └── TreeRestIntegrationTest
          │
          ├── TreeSoapServiceTest
          │
          ├── TreeControllerTest
          │
          ├── TreeEndpointTest
          │
          └── TreeServiceAspectTest
```

---

# 37. Unit Testing

Unit tests are located under:

```text
src/test/java/com/testSpring
```

The project contains tests for the major application layers.

```text
src
└── test
    ├── java
    │   └── com.testSpring
    │       │
    │       ├── TestSpringPersistentApplicationTests.java
    │       │
    │       ├── aspect
    │       │   └── TreeServiceAspectTest.java
    │       │
    │       ├── controller
    │       │   └── TreeControllerTest.java
    │       │
    │       ├── endpoint
    │       │   └── TreeEndpointTest.java
    │       │
    │       ├── integration
    │       │   └── TreeRestIntegrationTest.java
    │       │
    │       └── service
    │           ├── TreeServiceTest.java
    │           └── TreeSoapServiceTest.java
    │
    └── resources
        └── application.properties
```

The unit tests isolate individual classes by mocking their dependencies where appropriate.

---

# 38. REST Service Unit Testing

`TreeServiceTest` tests the REST business/service layer.

The repository dependency is mocked using Mockito so that the tests focus on the behavior of `TreeService` rather than communicating with a real database.

The following service operations are tested:

```text
getAll()

getOne()

addTree()

addMultipleTrees()

update()

patchTree()

deleteTree()
```

Both successful and failure scenarios are tested.

Examples include:

```text
Tree exists
     ↓
Repository returns TreeModel
     ↓
Service returns expected result
```

and:

```text
Tree does not exist
     ↓
Repository returns empty
     ↓
TreeNotFoundException
```

The tests also verify interactions with `TreeRepository` using Mockito.

---

# 39. SOAP Service Unit Testing

`TreeSoapServiceTest` tests the SOAP-specific service layer.

The following operations are covered:

```text
getAllTrees()

getTreeById()

addTree()

addMultipleTrees()

updateTree()

patchTree()

deleteTree()
```

The tests verify:

- successful retrieval
- missing tree handling
- creation
- multiple-tree creation
- full updates
- partial updates
- deletion
- repository interaction

The SOAP service also initializes the lazily loaded `branches` collection while the Hibernate session is active.

Unit testing this service helps ensure that SOAP-specific business behavior works independently of the SOAP XML transport layer.

---

# 40. REST Controller Unit Testing

`TreeControllerTest` tests the REST API layer using Spring `MockMvc`.

`MockMvc` allows HTTP requests to be simulated without starting an external web server.

The controller is tested for endpoints such as:

```text
GET     /home

GET     /all

GET     /all/{id}

POST    /add

POST    /addAll

PUT     /update/{id}

PATCH   /patch/{id}

DELETE  /delete/{id}

GET     /count
```

The tests verify HTTP behavior including:

```text
HTTP status codes

JSON response bodies

JSON fields

service method calls

success responses

error responses
```

Conceptually:

```text
MockMvc
   │
   ▼
TreeController
   │
   ▼
Mock TreeService
   │
   ▼
HTTP / JSON assertions
```

This tests the REST controller independently from the real database.

---

# 41. SOAP Endpoint Unit Testing

`TreeEndpointTest` tests the SOAP endpoint layer.

It verifies that the endpoint correctly converts between:

```text
SOAP generated DTO
        ↕
TreeModel
```

The SOAP operations tested include:

```text
getTree

getAllTrees

addTree

addMultipleTrees

updateTree

patchTree

deleteTree
```

The tests verify both successful and failure scenarios.

For example:

```text
DeleteTreeRequest
       │
       ▼
TreeEndpoint
       │
       ▼
TreeSoapService.deleteTree()
       │
       ▼
DeleteTreeResponse
```

A successful deletion should produce a response containing:

```text
success = true
message = Tree deleted successfully!
```

while a missing tree produces the appropriate failure behavior.

---

# 42. Spring AOP Unit Testing

`TreeServiceAspectTest` tests the AOP logic implemented by:

```text
TreeServiceAspect
```

The aspect contains the following advice types:

```text
@Before

@After

@AfterReturning

@AfterThrowing

@Around
```

The tests exercise the advice methods and verify that the aspect can process mocked `JoinPoint` and `ProceedingJoinPoint` objects.

The `@Around` advice is especially important because it calls:

```java
joinPoint.proceed();
```

to execute the intercepted method while measuring its execution time.

The aspect therefore remains independently testable from the REST and SOAP services.

---

# 43. Integration Testing

In addition to isolated unit tests, the project contains an end-to-end REST integration test:

```text
TreeRestIntegrationTest
```

located under:

```text
src/test/java/com/testSpring/integration
```

Unlike the controller unit tests, this test loads the Spring application context and exercises multiple real application layers together.

The integration flow is:

```text
MockMvc HTTP Request
        │
        ▼
TreeController
        │
        ▼
Spring AOP Proxy
        │
        ▼
TreeService
        │
        ▼
TreeRepository
        │
        ▼
Spring Data JPA
        │
        ▼
Hibernate
        │
        ▼
H2 Test Database
```

This verifies that the layers work correctly together rather than only verifying them independently.

---

# 44. H2 Test Database

The integration test uses an H2 in-memory database.

This is intentionally separate from the MySQL database used when running the actual application.

Production/development runtime:

```text
Spring Boot
    │
    ▼
MySQL
```

Automated integration testing:

```text
Spring Boot Test
    │
    ▼
H2
```

This prevents integration tests from modifying real MySQL development data.

Test-specific configuration is stored under:

```text
src/test/resources/application.properties
```

Spring uses this configuration during testing.

The database exists only for the test lifecycle and can be recreated for subsequent test executions.

---

# 45. REST End-to-End CRUD Integration Test

`TreeRestIntegrationTest` performs a complete REST CRUD lifecycle.

The test verifies that data can move through the complete application stack.

The lifecycle includes operations conceptually equivalent to:

```text
CREATE
   │
   ▼
READ
   │
   ▼
UPDATE
   │
   ▼
PATCH
   │
   ▼
DELETE
   │
   ▼
VERIFY NOT FOUND
```

The test exercises the real:

```text
TreeController

TreeService

TreeRepository

JPA / Hibernate

H2 Database

GlobalExceptionHandler

Spring AOP
```

rather than replacing the service or repository with Mockito mocks.

This makes it fundamentally different from the controller and service unit tests.

---

# 46. Integration Test CRUD Flow

The complete integration-test flow can be visualized as:

```text
POST /add
     │
     ▼
Create Tree
     │
     ▼
GET /all/{id}
     │
     ▼
Verify Created Tree
     │
     ▼
PUT /update/{id}
     │
     ▼
Verify Full Update
     │
     ▼
PATCH /patch/{id}
     │
     ▼
Verify Partial Update
     │
     ▼
DELETE /delete/{id}
     │
     ▼
Delete Tree
     │
     ▼
GET /all/{id}
     │
     ▼
TreeNotFoundException
     │
     ▼
GlobalExceptionHandler
     │
     ▼
404 Response
```

This verifies not only individual endpoints but also that persistence changes made by one operation are visible to subsequent operations.

---

# 47. Testing Exception Handling End-to-End

The REST integration test also exercises the global exception-handling path.

For example, after a tree has been deleted, attempting to retrieve the same tree causes:

```text
GET /all/{id}
      │
      ▼
TreeController
      │
      ▼
TreeService.getOne()
      │
      ▼
TreeRepository
      │
      ▼
Tree does not exist
      │
      ▼
TreeNotFoundException
      │
      ▼
GlobalExceptionHandler
      │
      ▼
HTTP 404
```

This verifies that exception handling works correctly when all application layers are connected.

The AOP exception path is also executed during this scenario.

Example console output:

```text
AOP @AfterThrowing
Exception in method: getOne
Exception type: TreeNotFoundException
Exception message: Tree not found with id: 9001

AOP @After
Method finished: getOne

AOP @Around
Method: getOne
Execution time: ... ms
```

This confirms that the exception travels through the service AOP proxy and is subsequently handled by the REST exception-handling infrastructure.

---

# 48. Running the Automated Tests

Run all tests using Maven:

```bash
./mvnw test
```

or, if Maven is installed globally:

```bash
mvn test
```

A successful execution should end with output similar to:

```text
Tests run: ...
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

A specific integration test can also be executed independently:

```bash
./mvnw -Dtest=TreeRestIntegrationTest test
```

This is useful when developing or debugging the end-to-end REST flow.

---

# 49. JaCoCo Code Coverage

The project uses JaCoCo to measure automated test coverage.

JaCoCo records which application instructions, branches, lines, methods, and classes are executed while the automated tests run.

The project achieved approximately:

```text
85% overall code coverage
```

after running the complete automated test suite.

Coverage is generated from a combination of:

```text
Unit Tests
    +
REST Controller Tests
    +
SOAP Endpoint Tests
    +
Service Tests
    +
AOP Tests
    +
REST Integration Test
```

The coverage percentage includes project classes considered by the configured JaCoCo report.

---

# 50. Generating the JaCoCo Report

Run:

```bash
./mvnw clean test
```

or:

```bash
mvn clean test
```

If the JaCoCo report goal is configured as part of the Maven lifecycle, the build output includes:

```text
jacoco:report
```

and output similar to:

```text
Loading execution data file .../target/jacoco.exec

Analyzed bundle 'TestSpringPersistent' with 29 classes

BUILD SUCCESS
```

The HTML report is generated under:

```text
target/site/jacoco/
```

The main report page is:

```text
target/site/jacoco/index.html
```

On macOS it can be opened from the project directory with:

```bash
open target/site/jacoco/index.html
```

---

# 51. Understanding the JaCoCo Report

The JaCoCo HTML report provides several coverage measurements.

| Metric | Meaning |
|---|---|
| Instructions | JVM bytecode instructions executed by tests |
| Branches | Conditional branches exercised |
| Complexity | Cyclomatic complexity coverage |
| Lines | Source-code lines executed |
| Methods | Methods executed |
| Classes | Classes executed |

The HTML report also allows navigation from:

```text
Project
   │
   ▼
Package
   │
   ▼
Class
   │
   ▼
Method / Source Line
```

This makes it possible to identify exactly which sections of the application still require additional testing.

---

# 52. Unit Tests vs Integration Test

The project intentionally uses both approaches.

| Unit Tests | Integration Test |
|---|---|
| Test classes in isolation | Tests multiple layers together |
| Dependencies can be mocked | Uses real Spring beans |
| Mockito | Spring application context |
| Very fast | More realistic |
| No real persistence required | Uses H2 persistence |
| Useful for edge cases | Useful for complete application flow |
| Tests individual behavior | Tests component interaction |

For example:

```text
TreeServiceTest

TreeService
    │
    ▼
Mock TreeRepository
```

compared with:

```text
TreeRestIntegrationTest

MockMvc
   │
   ▼
TreeController
   │
   ▼
TreeService
   │
   ▼
TreeRepository
   │
   ▼
Hibernate
   │
   ▼
H2
```

Using both provides significantly better confidence than relying on either approach alone.

---

# 53. Complete Testing Architecture

The final testing architecture of the application is:

```text
                         TEST SUITE
                             │
          ┌──────────────────┴──────────────────┐
          │                                     │
          ▼                                     ▼
      UNIT TESTS                        INTEGRATION TEST
          │                                     │
          │                                     ▼
          │                                  MockMvc
          │                                     │
          │                                     ▼
          │                               TreeController
          │                                     │
          │                                     ▼
          │                               Spring AOP Proxy
          │                                     │
          │                                     ▼
          │                                TreeService
          │                                     │
          │                                     ▼
          │                               TreeRepository
          │                                     │
          │                                     ▼
          │                                  Hibernate
          │                                     │
          │                                     ▼
          │                                     H2
          │
          ├── TreeControllerTest
          │
          ├── TreeServiceTest
          │
          ├── TreeSoapServiceTest
          │
          ├── TreeEndpointTest
          │
          └── TreeServiceAspectTest
                             │
                             ▼
                           JaCoCo
                             │
                             ▼
                    Code Coverage Report
                             │
                             ▼
                    ~85% Overall Coverage
```

---

# 54. Testing Features Demonstrated

The project now additionally demonstrates:

- JUnit 5 unit testing
- Mockito mocking
- Repository mocking
- Service-layer unit testing
- REST controller unit testing
- SOAP service unit testing
- SOAP endpoint unit testing
- Spring AOP unit testing
- `JoinPoint` testing
- `ProceedingJoinPoint` testing
- MockMvc
- HTTP status assertions
- JSON response assertions
- Spring Boot integration testing
- H2 in-memory database testing
- Test-specific Spring configuration
- End-to-end REST CRUD testing
- Persistence verification
- Exception-path integration testing
- AOP execution during integration testing
- Global exception handling during integration testing
- JaCoCo code coverage
- HTML coverage reports
- Approximately 85% overall automated test coverage

The application therefore demonstrates not only REST, SOAP, persistence, exception handling, transaction management, and AOP, but also a layered automated testing strategy covering both isolated components and integrated application behavior.

CI/CD pipeline configured with Jenkins.
