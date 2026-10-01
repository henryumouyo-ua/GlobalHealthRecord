# Hospital Appointment API

A Spring Boot + Maven REST API that lets a patient find a doctor at a
hospital in their own country, state, or province, and book, cancel, or
reschedule an appointment with them. Built with a layered architecture:
**Model → Repository → Service → Controller**, backed by PostgreSQL.

## Project structure

```
src/main/java/com/hospital/appointment/
├── model/       Entities: Country, StateProvince, Hospital, Doctor, Patient,
│                Appointment, AppointmentStatus
├── repository/   Spring Data JPA repositories
├── service/      Service interfaces
│   └── impl/     Service implementations (business logic)
├── controller/   REST controllers
├── dto/          Request/response DTOs
└── exception/    Custom exceptions + global exception handler
```

See `docs/DATA_MODEL.md` for the full canonical data model — entities, ER
diagram, the appointment status state machine, constraints, and known gaps.

## Prerequisites

- Java 17+
- Maven 3.8+ (or use the included wrapper if you generate one)
- PostgreSQL 13+ running locally
- VS Code with the "Extension Pack for Java" and "Spring Boot Extension Pack"
  (recommended, not required)

## 1. Set up PostgreSQL

PostgreSQL won't create the database for you automatically — create it once
before running the app:

```bash
createdb hospital_appointments
```

or from `psql`:

```sql
CREATE DATABASE hospital_appointments;
```

## 2. Configure the connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hospital_appointments
spring.datasource.username=postgres
spring.datasource.password=your_postgres_password
```

Replace the username/password with your own PostgreSQL credentials.

## 3. Run the app

From the project root:

```bash
mvn spring-boot:run
```

Or in VS Code: open the folder, let it import as a Maven project, then
run/debug `HospitalAppointmentApiApplication.java` directly (right-click →
"Run Java").

The API starts on `http://localhost:8080`.

Hibernate auto-creates all tables on first run
(`spring.jpa.hibernate.ddl-auto=update`). **If you have an existing database
from before the location hierarchy was added**, see the migration note at
the bottom of `docs/DATA_MODEL.md` — `appointments.hospital_id` and
`doctors.hospital_id` are new `NOT NULL` columns that can't be added to a
table that already has rows.

## 4. Try it out — a full walkthrough

The location hierarchy has to be seeded before anything else can reference
it: a `Hospital` needs a `Country` (and optionally a `StateProvince`), and a
`Doctor` needs a `Hospital`.

### Create a country
```bash
curl -X POST http://localhost:8080/api/countries \
  -H "Content-Type: application/json" \
  -d '{"name": "United States", "isoCode": "US"}'
```
→ note the returned `id` (say, `1`).

### Create a state/province (optional, but useful for narrowing searches)
```bash
curl -X POST http://localhost:8080/api/states-provinces \
  -H "Content-Type: application/json" \
  -d '{"name": "California", "code": "CA", "countryId": 1}'
```
→ note the returned `id` (say, `1`).

### Create a hospital
```bash
curl -X POST http://localhost:8080/api/hospitals \
  -H "Content-Type: application/json" \
  -d '{
        "name": "St. Mary General Hospital",
        "address": "123 Main St, San Francisco, CA",
        "countryId": 1,
        "stateProvinceId": 1
      }'
```
→ note the returned `id` (say, `1`).

### Create a doctor at that hospital
```bash
curl -X POST http://localhost:8080/api/doctors \
  -H "Content-Type: application/json" \
  -d '{"fullName": "Dr. Jane Smith", "specialization": "Cardiology", "hospitalId": 1}'
```
→ note the returned `id` (say, `1`).

### Create a patient with their own home location
```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{
        "fullName": "John Doe",
        "email": "john@example.com",
        "phoneNumber": "555-1234",
        "countryId": 1,
        "stateProvinceId": 1
      }'
```
→ note the returned `id` (say, `1`).

### Find a doctor in your own country/state/province
This is the main "find a doctor near me" endpoint — every filter is
optional, so search by country alone, country + state/province, add a
specialization, or any combination:
```bash
curl "http://localhost:8080/api/doctors/search?countryId=1&stateProvinceId=1&specialization=cardio"
```

You can search hospitals the same way:
```bash
curl "http://localhost:8080/api/hospitals/search?countryId=1&stateProvinceId=1"
```

### Book an appointment
The hospital is derived automatically from the doctor's current hospital —
you don't need to pass it:
```bash
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -d '{
        "patientId": 1,
        "doctorId": 1,
        "appointmentDateTime": "2026-10-15T10:00:00",
        "reasonForVisit": "Annual checkup"
      }'
```

### Cancel an appointment
```bash
curl -X PATCH http://localhost:8080/api/appointments/1/cancel
```

### Reschedule an appointment
```bash
curl -X PATCH http://localhost:8080/api/appointments/1/reschedule \
  -H "Content-Type: application/json" \
  -d '{"newAppointmentDateTime": "2026-10-16T14:30:00"}'
```

### Mark an appointment completed
```bash
curl -X PATCH http://localhost:8080/api/appointments/1/complete
```

### View appointments
```bash
curl http://localhost:8080/api/appointments
curl http://localhost:8080/api/appointments/patient/1
curl http://localhost:8080/api/appointments/doctor/1
```

## API summary

| Method | Endpoint                                 | Description                      |
|--------|---------------------------------------------|------------------------------------|
| POST   | `/api/countries`                         | Create a country                 |
| GET    | `/api/countries`                         | List countries                   |
| GET    | `/api/countries/{id}`                    | Get a country                    |
| POST   | `/api/states-provinces`                  | Create a state/province          |
| GET    | `/api/states-provinces?countryId=`       | List states/provinces (optionally scoped to a country) |
| GET    | `/api/states-provinces/{id}`             | Get a state/province             |
| POST   | `/api/hospitals`                         | Create a hospital                |
| GET    | `/api/hospitals`                         | List active hospitals            |
| GET    | `/api/hospitals/{id}`                    | Get a hospital                   |
| GET    | `/api/hospitals/search?countryId=&stateProvinceId=` | Find hospitals by location |
| DELETE | `/api/hospitals/{id}`                    | Deactivate a hospital (soft delete) |
| POST   | `/api/patients`                          | Create a patient                 |
| GET    | `/api/patients`                          | List active patients             |
| GET    | `/api/patients/{id}`                     | Get a patient                    |
| DELETE | `/api/patients/{id}`                     | Deactivate a patient (soft delete) |
| POST   | `/api/doctors`                           | Create a doctor                  |
| GET    | `/api/doctors`                           | List active doctors              |
| GET    | `/api/doctors/{id}`                      | Get a doctor                     |
| GET    | `/api/doctors/search?countryId=&stateProvinceId=&hospitalId=&specialization=` | Find a doctor by location/specialization |
| DELETE | `/api/doctors/{id}`                      | Deactivate a doctor (soft delete) |
| POST   | `/api/appointments`                      | Book an appointment              |
| GET    | `/api/appointments`                      | List all appointments            |
| GET    | `/api/appointments/{id}`                 | Get one appointment              |
| PATCH  | `/api/appointments/{id}/cancel`          | Cancel an appointment            |
| PATCH  | `/api/appointments/{id}/reschedule`      | Reschedule an appointment        |
| PATCH  | `/api/appointments/{id}/complete`        | Mark an appointment completed    |
| GET    | `/api/appointments/patient/{patientId}`  | List a patient's appointments    |
| GET    | `/api/appointments/doctor/{doctorId}`    | List a doctor's appointments     |

## Notes / things worth knowing

- A `Hospital` requires a `countryId`; `stateProvinceId` is optional (not
  every country is meaningfully subdivided). A `Patient` works the same way.
  If you do supply a `stateProvinceId`, it must actually belong to the
  `countryId` you supplied — otherwise you get a `400` with a clear message.
- A `Doctor` belongs to exactly one `Hospital`. An `Appointment` captures
  that hospital automatically at booking time, so appointment history stays
  accurate even if the doctor later moves to a different hospital.
- Booking and rescheduling both check that the doctor isn't already booked
  at that exact date/time (excluding cancelled appointments), and reject
  with `409 Conflict` if so. Booking also checks that the doctor and their
  hospital are both active.
- `appointmentDateTime` must be in the future (validated via `@Future`).
- Validation errors return `400` with a `fieldErrors` map; cross-entity
  mismatches (like a state/province that doesn't belong to the given
  country) return `400` with a plain error message; not-found resources
  return `404`; scheduling conflicts return `409`.
- `DELETE /api/patients/{id}`, `DELETE /api/doctors/{id}`, and
  `DELETE /api/hospitals/{id}` are **soft** deletes — they set
  `active = false` rather than removing the row, so appointment history is
  preserved. Deactivated records drop out of the default `GET` list and
  search endpoints but can still be fetched directly by id. There's
  currently no "reactivate" endpoint for any of them.
- `Country` and `StateProvince` have no delete endpoint — see
  `docs/DATA_MODEL.md` section 6.3 for why.
- This is a starting point, not a production-hardened service — there's no
  authentication/authorization layer, so you'll want to add Spring Security
  (or similar) before exposing this beyond local development.
