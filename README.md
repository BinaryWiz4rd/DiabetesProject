# Project Description

My Mobile Application Development project is an app for diabetic patients to help them monitor and manage their health. The app allows users to track blood glucose levels (visualize it on a graph, and export to a CSV file), calculate carbohydrates in a product, and see basic information about their condition.

## Functionalities Overview

### User Authentication
- Users can create an account using their email and password.
- Users with created accounts can log in to access their personalized data.
- Password reset is available for those who forgot it.

### Glucose Level Tracking
- Users can add their glucose levels.
- Measurements are stored in a Firestore database.
- Users can view a list of their past glucose measurements (retrieved from a Firestore database).
- Each entry displays the glucose value, time of measurement, and options to edit or delete the entry.
- Glucose measurements are visualized on a graph, with the latest glucose level highlighted. Color coding indicates normal (green) and abnormal (red) levels.

### Carbohydrate Calculator
- Users can input the amount of carbohydrates, fiber, and weight of a food product to calculate net carbohydrates.

### Data Export
- Users can export their glucose measurement history into a CSV file for doctor meetings.

### Notifications
- The app sends 1 notification every 30 minutes asking the user to add a new glucose measurement.
- Users can delete the permission to not receive notifications.
- Toast messages are displayed for successful logins, updates, and errors.

### Educational Information
- Basic information about diabetes and its types.

### User Interface
- A bottom navigation bar is used for easier access to different fragments: Measurements, History, Calculator, Library, and an add button (floating action button).

## Application Presentation

### Notifications
- Notifications are displayed every 30 minutes, and clicking on them opens the app.

### Login & SignUp Screen
- Users can securely sign in using their credentials used in signup.
- If they forgot their password, they can retrieve it via an authentication link sent to their email.
- If the user enters wrong credentials, an error message is displayed.

### Chart View
- Overview of glucose levels, with text set to red if the glucose notation is out of healthy bounds and green if it is within healthy bounds.
- Users can add a new measurement by clicking on the add button.

### History View
- Users can see the history of glucose measurements.
- Deletion and editing of entries are possible.
- Export to a CSV file is also available.

### Carbohydrate Calculator
- Users can check carbohydrate concentration in their food products.

### Educational Page
- A section that provides important information about diabetes.

## Conclusions

Overall, the project looks a lot different than what I expected it to look like. It was supposed to receive NFC signals from the NovoNordisk insulin pen, display them on a graph, and provide a deep analysis of glucose measurements and insulin dosages using AI models or machine learning. However, this was not possible due to company policy. 

This project was challenging, as I struggled to let go of certain ideas and held onto them for too long, which indicated poor work organization. This is why I'm not 100% proud of the outcomes, as I wanted to implement some features that didn’t work in the end. Nevertheless, I learned a lot of concepts along the way and have started several paths that I would like to explore further in the future.

## What Was Achieved?
- User authentication using Firebase Authentication (login and register, with password).
- RecyclerView to view the history of added glucose measurements retrieved from Firebase Firestore (adding, updating, deleting is possible).
- Notifications are displayed.
- Asynchronous programming was used to ensure smooth operation of the app.
- Documentation is present.
- Glucose measurements are displayed on an interactive graph.
- Exporting to a CSV file of all glucose measurements, with time and user details, is possible.

## What Was Not Completed and Why? (Design Changes)
I wanted to add a MediSearch API; however, even though WebSocket was active, it was not recognized by my app, suggesting insufficient permissions to receive a response. An API was used, which was visible on a graph (with up to 1000 free usages).

## Future Development Plans
In the future, I would like to resolve the challenge I faced with the MediSearch API and improve the appearance of the graph. To do so, I need to learn more about WebSocket and how graphs work.
