# JavaBackEndProject
# Pet Health Application

## Introduction

The Pet Health Application allows users to manage their pets' health records seamlessly. Each pet added to the system is associated with a health record containing information about weight and events.

This README provides an overview of the application's functionalities for different user roles: administrators, owners, and veterinarians.

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

## Controllers and Routes Structure

The application follows a structured approach to handling HTTP requests using controllers and routes. Here's an overview of the controllers and their corresponding routes:

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
    - ...

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




## Functionalities for Owners:

### Create Owner:

Owners can register their own accounts by providing basic information such as name and email.

### Update Owner Details:

Owners can update their account information by specifying their ID and providing updated data such as name or email.

### Delete Owner:

Owners can delete their own accounts from the system by specifying their ID.

### Get Owner Details by ID:

Owners can retrieve detailed information about their own account by providing their ID.

### Get Owner's Pets:

Owners can access data about their own pets. To retrieve all pets belonging to a specific owner, the owner's ID needs to be provided.

- **Endpoint GET:** `api/pets/{petId}`
  - **Parameters:** Pet ID

### Add a New Pet to Profile:

Owners can add pets to their own profile. They need to provide pet data without including the "owner" field; the logged-in user will be automatically assigned as the owner.

- **Endpoint POST:** `api/pets`

### Update All Pet Details:

Owners can update all data of one of their pets. They cannot access data of other owners' pets.

- **Endpoint PUT:** `api/pets/{petId}`
  - **Parameters:** Complete Pet object, Pet ID

### Partially Update Pet Details:

Owners can update one or more details of one of their pets. They cannot access data of other owners' pets.

- **Endpoint PATCH:** `api/pets/{petId}`
  - **Parameters:** Complete Pet object, Pet ID

### Delete Own Pet:

Owners can delete one of their own pets. They cannot delete pets belonging to other owners.

- **Endpoint DELETE:** `api/pets/{petId}`
  - **Parameters:** Pet ID

### Assign Veterinarian to Pet:

Owners can assign a veterinarian to one of their pets. They cannot assign veterinarians to pets belonging to other owners.

- **Endpoint PATCH:** `api/pets/veterinarians/{petId}/{vetId}`
  - **Parameters:** Pet ID, Veterinarian ID

### Remove Veterinarian from Pet:

Owners can remove a veterinarian assigned to one of their pets. They cannot remove veterinarians from pets belonging to other owners.

- **Endpoint DELETE:** `api/pets/veterinarians/{petId}/{vetId}`
  - **Parameters:** Pet ID, Veterinarian ID

### Manage Health Record Data of Pet:

Owners can view the health record of an assigned pet. They need to select the pet (provide its ID) and will be able to see a summary of all health data for that pet: registered weights, health events, etc.

### Weight:

- **Add Weight to Pet's Health Record:**
  - Owners can add a weight to the health record of an assigned pet. They need to select the pet (provide its ID), enter the date, and the weight in kilograms. For example:
    ```json
    {
      "petId": 1,
      "date": "2024-04-10",
      "weightInKg": 3.5
    }
    ```

- **Delete Weight from Pet's Health Record:**
  - Owners can delete a weight from the health record of an assigned pet. They need to provide the ID of the weight.

- **Retrieve Weights of Pet Within a Time Period:**
  - Owners can query all weights of a pet within a specified time period. They need to provide the pet's ID, start date, and end date.

### Events:

- **Add Events to Pet's Health Record:**
  - Owners can add different events to the health record of an assigned pet.

- **Update Specific Health Event of Pet:**
  - Owners can update a specific health event of an assigned pet using the PUT method.

- **Modify Specific Health Event Data of Pet:**
  - Owners can change data of a specific health event of an assigned pet using the PATCH method.

- **Delete Health Event of Pet:**
  - Owners can delete a health event from the health record of an assigned pet.

- **List Health Events of Pet by Types and Dates:**
  - Owners can list health events of a pet by types and dates.

## Functionalities for Veterinarians:

- **Create Veterinarian:**
  - A veterinarian can create a Veterinarian account in the application by providing basic information such as name and email.

- **Get List of Pets Assigned to Veterinarian:**
  - Veterinarians can access data of the pets they have assigned. To access all pets assigned to a specific veterinarian, the veterinarian's ID needs to be provided.
    - **Endpoint GET:** `api/pets`
    - **Parameters:** None

- **Get Assigned Pet Details:**
  - Veterinarians can retrieve details of a pet assigned to them.
    - **Endpoint GET:** `api/pets/{petId}`
    - **Parameters:** Complete Pet object, Pet ID

- **Get List of Health Records of Assigned Pet:**
  - Veterinarians can access the health record data of a pet assigned to them. They can only view, not modify or delete.

## Future Work