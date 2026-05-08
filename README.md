# Train Ticketing System

A robust backend application designed for managing train schedules and ticket bookings. Developed entirely in **Java Spring Boot**, this RESTful API focuses on intelligent routing, automated email notifications, data validation (overbooking prevention), and secure administrative control. 
<br>

*(Note: For the optional **Problem 2**, I have implemented a dynamic PDF e-ticket generator with scannable QR codes for offline validation. Details can be found at the end of this README).*
<br>

The API was thoroughly tested and documented using **Postman**.
<br>

---

<br>

## Key Features

* **Intelligent Routing:** Find direct or multi-train (1-change) routes between stations.
* **Booking Engine:** Real-time ticket reservation with overbooking prevention.
* **Automated Notifications:** Booking confirmations and delay alerts sent via Gmail SMTP.
* **Secure Admin Dashboard:** Protected area for managing trains, stations, and schedules.
* **Spring Security:** Administrative endpoints secured using **Basic Authentication**.

<br>

---

<br>

##  API Documentation & Test Evidence

###  1. Passenger Journey (Public Endpoints)

First, let's explore the user's side of the application. 

<br>

####  Available Stations
Users can retrieve a list of all available stations representing various locations in the system:

<img width="443" height="512" alt="image" src="https://github.com/user-attachments/assets/cd7ac493-6726-46ce-a854-d1b4d18b3073" />

<br>

####  Train Schedules (Database View)
Below is a snapshot of the train schedules from MySQL Workbench to provide context for the routing algorithm:
* **Train 1:** Travels from Station 1 to Station 2.
* **Train 2:** Travels from Station 2 to Station 3.
* **Train 3:** Travels from Station 4 to Station 2, then to Station 3.

<img width="622" height="580" alt="image" src="https://github.com/user-attachments/assets/c65ddf74-adb2-49d2-a4fb-b2374edd6537" />

<br>
<br>

####  Intelligent Route Search
A user can search and find possible connections between stations.
**Algorithm Logic (how it works under the hood):**
> The routing engine uses a two-step process to evaluate all possible train schedules:
> 
> 1. **Direct Routes:** It checks if a single train serves both the start and end stations. It uses the `stopOrder` property to guarantee the train is traveling in the correct direction (the starting stop order must be strictly less than the ending stop order).
> 2. **1-Change Routes:** For multi-train journeys, it fetches the full routes of two different trains and searches for a common transfer station. It validates three critical conditions:
>    * Train 1 reaches the transfer station *after* the initial departure.
>    * Train 2 leaves the transfer station *before* the final destination.
>    * A valid timeframe exists (Train 1's arrival time is strictly *before* Train 2's departure time).
> 
> *If no combinations meet these criteria, the system throws a custom `RouteNotFoundException`.*
 <br>
The algorithm handles 3 distinct scenarios:
<br>
<br>

**Scenario 1: Direct Route**
> The user finds a direct train. The transfer details (station, time, and second train name) are `null`, as no changeover is needed.

<img width="443" height="510" alt="image" src="https://github.com/user-attachments/assets/10ee839e-89c2-416e-810d-41ad2da28419" />

<br>
<br>

**Scenario 2: Route with 1 Changeover**
> The user needs to switch trains. In this case, they change at Brasov, where they catch a connecting train to Bucharest.

<img width="442" height="510" alt="image" src="https://github.com/user-attachments/assets/8a44c0c6-9eb6-4dce-8361-ae9927710d7d" />

<br>
<br>

**Scenario 3: No Possible Link**
> If there is no possible link between the start and end stations, a proper error message is returned.

<img width="448" height="512" alt="image" src="https://github.com/user-attachments/assets/73d91ff7-e58a-408b-9a6a-873eee472c82" />

<br>

####  Ticket Booking & Validation
After selecting a route, the user can proceed to book their tickets. The booking engine includes several validation layers to ensure data integrity and prevent errors.

<br>

**1. Successful Booking & Email Confirmation**
> To book a ticket, the user provides the desired train and station details. Upon a successful booking, the system registers the transaction and automatically dispatches a confirmation email to the customer using Gmail SMTP.

<img width="436" height="510" alt="image" src="https://github.com/user-attachments/assets/8dd9e0be-62e8-4de4-948b-709f6b080e6b" />
<br>
<img width="1670" height="407" alt="image" src="https://github.com/user-attachments/assets/10583939-4611-438d-b504-a9a99980dc31" />

<br>
<br>

**2. Invalid Route Validation**
> If a user attempts to book a ticket for a destination where the selected train does not stop (or the start station is invalid for that train), the system blocks the transaction and returns a specific error.

<img width="439" height="512" alt="image" src="https://github.com/user-attachments/assets/400cc41a-d908-4294-87a3-2c956df9376a" />

<br>
<br>

**3. Overbooking Prevention**
> To prevent exceeding the train's physical capacity, the system dynamically calculates the available seats (Total Seats - Sold Tickets). If the requested number of tickets exceeds the remaining seats, an overbooking error is triggered.

<img width="443" height="500" alt="image" src="https://github.com/user-attachments/assets/aa71e640-374e-43c5-a5be-625d42197ad8" />

<br>

---

###  2. Administrator Operations (Secured Endpoints)
All administrative operations are protected using **Spring Security (Basic Authentication)**. The administrator must provide valid credentials to access these endpoints.
<img width="440" height="509" alt="image" src="https://github.com/user-attachments/assets/76c42544-5b9c-470e-853b-3960c446ef5e" />

####  2.1 Manage Trains (Add / Modify / Remove)
After login, administrators can add new trains, update their capacity, or remove them from the system.
<img width="442" height="509" alt="image" src="https://github.com/user-attachments/assets/740fb510-ae1a-439b-9b5c-2542afc4ea98" />
<br>
<img width="442" height="504" alt="image" src="https://github.com/user-attachments/assets/ab634d18-32a8-4a6b-a17d-3f5a3f65b6e5" />
<br>
<img width="440" height="503" alt="image" src="https://github.com/user-attachments/assets/ea361757-a922-4b58-b445-6dc332f1c352" />


<br>
<br>

####  2.2 Manage Stations and Routes
The system allows full administrative control over the stations and the exact timetables (routes) for each train.

<br>

**1. Station Management**
<br>
Administrators can register new stations, update their names, or remove them entirely from the network.

<img width="436" height="509" alt="image" src="https://github.com/user-attachments/assets/3eebedbd-dcb7-49a7-a9f0-f8ee1f72cc67" />
<br>
<img width="441" height="509" alt="image" src="https://github.com/user-attachments/assets/6bd87caa-5dbe-43c8-bc64-12cfccf7f023" />
<br>
<img width="435" height="509" alt="image" src="https://github.com/user-attachments/assets/81db33d4-224b-4659-8373-d226d7ec8fe9" />

<br>

**2. Schedule & Route Management**
A train's complete route is built by adding multiple schedules. The `stopOrder` parameter is crucial, as it defines the chronological sequence of the stations visited by the train (e.g., stop 1, stop 2, stop 3).

<img width="439" height="514" alt="image" src="https://github.com/user-attachments/assets/a60338d5-fc50-4776-b7e9-9599b28f5f90" />
<br>
The administrator can update schedule details.
<img width="436" height="512" alt="image" src="https://github.com/user-attachments/assets/8d2c5122-238c-4b2c-be20-81f233ceb9b0" />
<br>
<img width="438" height="510" alt="image" src="https://github.com/user-attachments/assets/8dd018f8-7b4a-4384-9a3a-b32517a9fbd1" />
<br>

The administrator has the ability to inspect the entire sequence of stops for a specific train, including arrival and departure times for each station.
<img width="437" height="514" alt="image" src="https://github.com/user-attachments/assets/9a0d70ad-1ff8-4225-842e-6d091926c4bc" />

<br>

####  2.3 View Bookings for a Specific Train
The administrator can retrieve a list of all active bookings for any given train to monitor capacity and passengers.

<img width="438" height="506" alt="image" src="https://github.com/user-attachments/assets/b390f6a4-2694-4c81-812a-26ea386d4269" />



<br>

####  2.4 Delay Notifications (Email Dispatch)
If a train encounters a delay, the administrator can trigger an endpoint that automatically fetches all passengers with valid tickets for that train and sends them an email alert using Gmail SMTP.

<img width="437" height="511" alt="image" src="https://github.com/user-attachments/assets/16de1bc2-5797-4aa0-829e-583a36baa6b8" />
<br>
<img width="841" height="398" alt="image" src="https://github.com/user-attachments/assets/a91090ba-46d7-479b-a625-e101cb275377" />


<br>

---

## Problem 2: Offline E-Tickets with QR Code Integration

**The Problem Defined:** While a plain text email is sufficient to confirm a booking, it lacks a standardized format for quick validation by train conductors. Passengers need a portable e-ticket that can be downloaded in advance and easily scanned during their journey, ensuring a smooth verification process.

**The Solution Implemented:** I integrated a dynamic PDF generation system using `OpenPDF` and the `ZXing` library. Upon a successful booking, the system automatically generates a formatted e-ticket containing the passenger's itinerary and a **scannable QR Code** embedded with the booking's validation details.
<br>

**Test Evidence:**
> The customer receives a multipart email with the attached PDF. When the conductor scans the QR code, it reads the encrypted ticket validation data (e.g., `Valid Ticket | ID: 1 | Train: InterRegio`).

<img width="560" height="304" alt="image" src="https://github.com/user-attachments/assets/19172d1f-8e92-4818-ac51-551e4b5c9135" />

<img width="500" height="400" alt="WhatsApp Image 2026-05-08 at 22 20 56" src="https://github.com/user-attachments/assets/b464b41e-01fb-4228-893b-70ec29269e6e" />




##  Conclusion

This project was developed to demonstrate core backend engineering principles, focusing on clean architecture, RESTful API design, and practical algorithmic problem-solving. It fulfills all the mandatory requirements of the assignment, ensuring data integrity and security.

Thank you for taking the time to review my project!

