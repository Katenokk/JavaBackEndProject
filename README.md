# JavaBackEndProject
# Pet Health Application
## Index

1. Introduction
2. Description
3. Setup
4. Technologies Used
5. Testing and Task Management
6. Controllers and Routes Structure
7. Functionalities
  - For Owners
  - For Veterinarians
  - For Admins
8. Future Work

## Introduction

This README provides an overview of the application's functionalities for different user roles: administrators, owners, and veterinarians.

The Pet Health Application allows users to manage their pets' health records seamlessly. Each pet added to the system is associated with a health record containing information about weight and events.


## Description

The Pet Health Application is a Spring Boot project aimed at providing pet owners with a platform to store, analyze, and share data related to their pets' health with veterinarians. The application allows users to manage their pets' health records seamlessly, with each pet having an associated health record containing information about weight and events.

## Setup

To set up the Pet Health Application on your local machine, follow these steps:

1. Clone the repository to your local machine.
2. Open the project in your preferred IDE.
3. Configure the MySQL database using Workbench or your preferred MySQL client.
4. Update the `application.properties` file with your MySQL database credentials.
5. Build the project using Maven: `mvn clean install`.
6. Run the application: `mvn spring-boot:run`.

## Technologies Used

The Pet Health Application is built using the following technologies:

- Spring Boot
- Maven
- MySQL
- Lombok
- Jakarta Validation
- Spring Security (Bearer Authentication)

## Testing and Task Management

### Endpoint Testing and Test Plan

For thorough testing of API endpoints and creating a comprehensive test plan, I utilized Postman. Postman allowed me to send requests to my endpoints with various HTTP methods and payloads, ensuring that my API functions as expected under different scenarios. Additionally, I created a detailed test plan within Postman, covering all possible methods and edge cases to ensure the robustness of the API.

### Task Management and Backlog

To manage our project tasks and backlog efficiently, I employed Kanban boards in my GitHub repository. Kanban provided me with a visual representation of my workflow, allowing me to track the progress of tasks from conception to completion. With Kanban, I could easily prioritize tasks and monitor their status, ensuring smooth project execution and delivery.

### Repository Contents

- The repository includes a UML class diagram (`PetHealthDiagramm.png`) depicting the project's architecture and relationships between different components. This diagram provides a visual representation of the system's structure, making it easier to understand the organization of classes and their interactions.


## Controllers and Routes Structure

The application follows a structured approach to handling HTTP requests using controllers and routes. Here's an overview of the controllers and their corresponding routes:

The application provides open endpoints for user authentication and registration:

- Manages user login functionality.
  - Routes:
    - POST `/api/login` (Allows users to log in)

- `UserController`: Handles user registration.
  - Routes:
    - POST `/api/register` (Allows users to register new accounts)


Access to these endpoints is configured as follows:


- `UserController`: Manages user-related operations such as registration, updates, and deletion.
  - Routes:
    - GET `/api/users`
    - POST `/api/users`
    - PUT `/api/users/{id}`
    - DELETE `/api/users/{id}`
    - ...

- `RoleController`: Handles role-related operations and is accessible only to administrators.
  - Routes:
    - POST `/api/roles`
    - POST `/api/roles/addtouser`
  

- `PetController`: Manages pet-related operations such as fetching, adding, updating, and deleting pets.
  - Routes:
    - GET `/api/pets/{id}`
    - GET `/api/pets`
    - POST `/api/pets`
    - PUT `/api/pets/{id}`
    - DELETE `/api/pets/{id}`
    - ...

- `HealthRecordController`: Handles health record-related operations such as adding weights and events to pets, fetching pet health records, and managing events.
  - Routes:
    - POST `/health-records/weights/{petId}`
    - POST `/health-records/events/{petId}`
    - PATCH `/health-records/events/{eventId}`
    - GET `/{petId}`
    - ...




## Functionalities:

### for Owners:

#### Create Owner:

Owners can register their own accounts by providing basic information such as name and email.

#### Update Owner Details:

Owners can update their account information by specifying their ID and providing updated data such as name or email.

- **Endpoint GET:** `api/owners/{id}`
  - **Parameters:** Owner object

#### Delete Owner:

Owners can delete their own accounts from the system by specifying their ID.

- **Endpoint DELETE:** `api/owners/{id}`
  - **Parameters:** Owner id

#### See all veterinarians available:

Owners can list all the veterinarians registered in the application so that they can choose one.

- **Endpoint GET:** `api/veterinarians`
  - **Parameters:** none

#### Get list of Owner's Pets:

Owners can access data about their own pets.

- **Endpoint GET:** `api/pets/owners`
  - **Parameters:** none

#### Add a New Pet to Profile:

Owners can add pets to their own profile. They need to provide pet data without including the "owner" field; the logged-in user will be automatically assigned as the owner.

- **Endpoint POST:** `api/pets`
- **Parameters:** PetDTO object

#### Find a Pet:

Owners can find a pet by its id, they can only find their own pets.

- **Endpoint POST:** `api/pets/{petId}`
- **Parameters:** Pet id

#### Update All Pet Details:

Owners can update all data of one of their pets. They cannot access data of other owners' pets.

- **Endpoint PUT:** `api/pets/{petId}`
  - **Parameters:** Complete Pet object, Pet ID

#### Partially Update Pet Details:

Owners can update one or more details of one of their pets. They cannot access data of other owners' pets.

- **Endpoint PUT:** `api/pets/{petId}`
  - **Parameters:** Complete Pet object, Pet ID
 
- **Endpoint PATCH:** `api/pets/{petId}`
- **Parameters:** PetDTO object, Pet ID

#### Delete Own Pet:

Owners can delete one of their own pets. They cannot delete pets belonging to other owners.

- **Endpoint DELETE:** `api/pets/{petId}`
  - **Parameters:** Pet ID

#### Assign Veterinarian to Pet:

Owners can assign a veterinarian to one of their pets. They cannot assign veterinarians to pets belonging to other owners.

- **Endpoint PATCH:** `api/pets/veterinarians/{petId}/{vetId}`
  - **Parameters:** Pet ID, Veterinarian ID

#### Remove Veterinarian from Pet:

Owners can remove a veterinarian assigned to one of their pets. They cannot remove veterinarians from pets belonging to other owners.

- **Endpoint DELETE:** `api/pets/veterinarians/{petId}/{vetId}`
  - **Parameters:** Pet ID, Veterinarian ID

#### Manage Health Record Data of Pet:

Owners can view the health record of an assigned pet. They need to select the pet (provide its ID) and will be able to see a summary of all health data for that pet: registered weights, health events, etc.

### Weight:

- **Add Weight to Pet's Health Record:**
  - Owners can add a weight to the health record of an assigned pet. They need to select the pet (provide its ID), enter the date, and the weight in kilograms. For example:
    ```json
    {
      "petId": 1,
      "date": "2024-04-10 12:00",
      "weightInKg": 3.5
    }
    ```
- **Endpoint POST:** `health-records/weights/{petId}`
  - **Parameters:** Pet ID, date, weight in kg

- **Delete Weight from Pet's Health Record:**
  - Owners can delete a weight from the health record of an assigned pet. They need to provide the ID of the weight.
  
- **Endpoint DELETE:** `health-records/weights/{weightId}/{petId}`
  - **Parameters:** Weight id, Pet ID

- **Retrieve Weights of Pet Within a Time Period:**
  - Owners can query all weights of a pet within a specified time period. They need to provide the pet's ID, start date, and end date.
  
- **Endpoint GET:** `health-records/weights/{petId}`
  - **Parameters:** Pet ID, start date, end date

### Events:

- **Add Events to Pet's Health Record:**
  - Owners can add different events to the health record of an assigned pet.

- **Endpoint POST:** `health-records/events/{petId}`
  - **Parameters:** Pet ID, EventDTO object
  
- **Get the Health Record of own Pet:**
  - Owners can get the complete health record of an assigned pet using the GET method.

- **Endpoint GET:** `health-records/{petId}`
  - **Parameters:** Pet ID

- **Get all Health Events of own Pet:**
  - Owners can get a list of health events of an assigned pet using the GET method.

- **Endpoint GET:** `health-records/events/{petId}`
  - **Parameters:** Pet ID 
 
- **Update Specific Health Event of Pet:**
  - Owners can update a specific health event of an assigned pet using the PUT method.

- **Endpoint PUT:** `health-records/events/{petId}`
  - **Parameters:** Pet ID, Event object
  
- **Modify Specific Health Event Data of Pet:**
  - Owners can change data of a specific health event of an assigned pet using the PATCH method.

- **Endpoint PATCH:** `health-records/events/{petId}`
  - **Parameters:** Pet ID, EventDTO object

- **Delete Health Event of Pet:**
  - Owners can delete a health event from the health record of an assigned pet.

- **Endpoint PATCH:** `health-records/events/{petId}`
  - **Parameters:** Pet ID


### for Veterinarians:

- **Create Veterinarian:**
  - A veterinarian can register a Veterinarian account in the application by providing basic information such as name and email.
  
- **Manage own account**
- Create, update and delete own account.

- **Get List of Pets Assigned to them:**
  - Veterinarians can access data of the pets they have assigned.
    - **Endpoint GET:** `api/pets/veterinarians`
    - **Parameters:** None

- **Get Assigned Pet Details:**
  - Veterinarians can retrieve details of a pet assigned to them.
    - **Endpoint GET:** `api/pets/{petId}`
    - **Parameters:** Pet ID

- **Get the Health Record of an Assigned Pet:**
  - Veterinarians can access the health record data of a pet assigned to them. They can only view, not modify or delete.

### for Admins:

- **All the permissions of other users:**

- **List all users**

- **Endpoint GET:** `api/users`
  - **Parameters:** none

- **Find user by username**

- **Endpoint GET:** `api/users/username`
  - **Parameters:** username

- **Add any user account**

- **Manage accounts**
  - Create, update and delete admin accounts.
  
- **Find all pets**

- **Endpoint GET:** `api/pets`
  - **Parameters:** none

- **Add roles to users**

- **Endpoint POST:** `api/roles/addtouser`
  - **Parameters:** RoleToUserDTO object

## Future Work

- **Add more Events:**
  - Add more types of health events to the system, such as surgeries, vaccinations, stool, injury, etc.

- **List Health Events of Pet by Types and Dates:**

- **Add image files to Events**

- **List Health Events of Pet by Types and Dates along with Weights:**
  - Make reports of specific Events within a time period, along with the weights of the pet. Store these reports in a PDF file.