# Full-stack development of a smartphone and web application of RespirHò: a wearable system for continuous respiratory and activity monitoring
# Intro
This is the project I realized for my Master Thesis in Biomedical Engineering at the Politecnico Di Milano in 2021.<br>
These applications are not intended to be downloaded here. The project has been uploaded for demonstration purposes only.

# Index
- [Telemedicine architecture](#telemedicine-architecture) 
- [Tools](#tools) 
- [Smartphone App](#smartphone-app)
  - [Splash screen and logo page](#splash-screen-and-logo-page) 
  - [Authentication](#authentication) 
  - [Patients list](#patients-list)  
  - [Lateral menu and support page](#lateral-menu-and-support-page) 
  - [Patient data](#patient-data) 
  - [Initialization of the recording](#initialization-of-the-recording) 
  - [Acquisition setting](#acquisition-setting) 
  - [Recording](#recording) 
  - [Background functions](#background-functions) 
  - [End of the recording](#end-of-the-recording) 
- [Web App](#web-app)
  - [Authentication](#authentication-1)
  - [Patients list](#patients-list-1)
  - [Lateral menu and support page](#lateral-menu-and-support-page-1)
  - [Patient files](#patient-files)

# Telemedicine architecture
The telemedicine architecture developed for this project is the following:
* The three IMU sensors, already patented, placed on the body of the patient communicate via [ANT protocol](https://www.thisisant.com/developer/ant-plus/ant-plus-basics) with the smartphone app.
* The smartphone app, which acts as the master node of the communication, collects the data and upload them on the [Firebase](https://firebase.google.com/?gclid=Cj0KCQjw8uOWBhDXARIsAOxKJ2GBW6TrvxR1M6czZbgBi6nBZsJPYzKU2G4Kv10tXpPREZsCDaFDw_0aAn0-EALw_wcB&gclsrc=aw.ds) database which implements some backend services needed for:
  * the authentication, 
  * the storage in real time of the data 
  * the storage of the recording files. 
* The web app takes all the information needed from the same database and display the stored files.
* The extraction of the respiratory parameters has been made offline using Python through the data collected in the database.

![1](https://user-images.githubusercontent.com/109110970/180602786-268de707-fb59-4f53-9aef-d20e6c464a69.PNG)

# Tools

* [Android Studio IDE](https://developer.android.com/studio?gclid=Cj0KCQjw8uOWBhDXARIsAOxKJ2EyYjz4vPbfKBb9hLeEKy90ODIC68RMUBl7UCAyFYdbiSengvuz1FQaAgjpEALw_wcB&gclsrc=aw.ds) - smartphone application (Java, XML)
* [Visual Studio Code](https://code.visualstudio.com/) - web application (HTML, CSS, Javascript)
* [Firebase](https://firebase.google.com/?gclid=Cj0KCQjw8uOWBhDXARIsAOxKJ2GBW6TrvxR1M6czZbgBi6nBZsJPYzKU2G4Kv10tXpPREZsCDaFDw_0aAn0-EALw_wcB&gclsrc=aw.ds) - backend services 
* [PyCharm](https://www.jetbrains.com/pycharm/) - offline extraction of the respiratory parameters (Python)

# Smartphone App

The main features of the Android smartphone application, fully explained below in the paragraphs, are the following:
* to acquire and record data
* to manage the user authentication
* to store the patients’ data in a cloud 
* to automatically reconnect the sensors in case they disconnect 
* to back up the recording files periodically 
* to provide to the user the needed feedback for a correct acquisition

A flowchart summarizing the main workflow of the application is here presented.

<p align="center" width="100%">
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180604379-eb646e6e-d29f-4b1d-b85a-4ada88a8bd27.png" width="600">
</p>

## Splash screen and logo page

- A simple logo has been designed on [Inkscape](https://inkscape.org/) 
- the first page asks to the user only once the permissions needed for the application to work.

<p align="center" width="100%">
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180604781-3bdd7791-26c6-401c-8d9b-87e76ecefd37.png" width="20%">
  &nbsp &nbsp
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180604069-38e395bf-a63c-462e-af97-63ddaaed28e6.gif" width="22%">
</p>

## Authentication

<img align="right" src="https://user-images.githubusercontent.com/109110970/181107061-a5b2756b-6f6e-44b5-a08f-fb6358b924ba.PNG" width="35%" style="padding-right:100px">

The traditional authentication process exploit the features of Firebase Authentication and here it's possible to:
- login with email and password
- login automatically through Google Authentcation 
- sign in
- reset the password 

<p align="center" width="100%">
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180607863-c5f340b0-6791-4c16-a501-8b8bcd2a6ffb.PNG" width="85%">
</p>


## Patients list

After the authentication the user can see all the patients saved, he can add or edit patients information. 
The patient added must have an ID patient, needed to create a personal folder on the database and also locally on the smartphone to store the information of the recordings. 

<p align="center" width="100%">
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180609428-84d2ef32-fbac-46a4-a37e-1710ec43f6ad.PNG" width="70%">
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180609555-963d9432-6539-4a2f-8433-1605f50cbe0c.gif" width="20%">
</p>

## Lateral menu and support page

A lateral menu can be always opened and from this is possible:
- to go to the support page where the user can download the user guides stored on the database or send an email with a pre compiled format
- log out 
- close the app safely.

<p align="center" width="100%" >
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180654291-81deb100-b850-49f4-84ce-72dea4f02d41.jpg" width="21%">
  &nbsp 
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180654301-6a9c518f-cd6c-4dc0-967b-546a1928826f.jpg" width="21%">
  &nbsp 
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180654305-1660eec6-f077-43ee-b219-cea46eb7b499.jpg" width="21%">
  &nbsp 
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180609858-087143a0-6014-4173-b383-c093577144ab.gif" width="21%">
</p>

## Patient data

Selecting a patient, the user can see:
- the patients’ information added previously
- an archive with all the previous personal recordings files synced from Firebase Storage
- the sensors available to perform acquisitions

At this stage of the project, only the sensors related to the breathing and activity are available. 

<p align="center" width="100%" >
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180655603-7d8c55fe-a95c-4e4e-a2a6-1fb995eca8bb.jpg" width="21%">
  &nbsp 
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180655615-dd629c91-577b-40f3-88cc-302602774d8b.jpg" width="21%">
  &nbsp 
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180655619-0131d8cd-2cc7-4f59-8116-0557b49c014f.jpg" width="21%">
  &nbsp 
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180655592-0b1069d5-c00c-4c09-9586-e04c50d84f1c.gif" width="21%">
</p>

## Initialization of the recording

Selecting the sensor, firstly, there’s the initialization of the ANT communication, then the user will be asked to switch on the sensors units in order and it can check the correct status in the app in real time.

<p align="center" width="100%" >
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180655932-7306d3bb-98cf-4bf2-99ca-4e94eeae605c.jpg" width="21%">
  &nbsp 
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180655940-6d5587bb-8dd0-4ed2-bfb1-34e2827d1832.jpg" width="21%">
  &nbsp 
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180655950-6cd25d12-66f2-4805-98a1-fe3e6353e0a9.jpg" width="21%">
  &nbsp 
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180655953-74778228-905d-4ff6-86b6-a20190095cdf.gif" width="21%">
</p>

## Acquisition setting

Then, he can choose:
- to calibrate the sensors
- to insert the information of the recording
- to choose the recording type: manual or timer.

<p align="center" width="100%" >
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180656087-65c4aa75-00f4-426e-a76e-a544e5e2c202.PNG" width="90%">
</p>

## Recording

### Recording (TX)

When a recording is started, a proper folder named with the corresponding ID patients and a subfolder with the date and time of the start of the recording are created on the firebase realtime database. 
This is the folder arranged to collect the data. 
In the meantime a temporary file is saving the data on the smartphone locally. 
Here the smartphone keeps calling the three sensor units recursively and waits their response.

<p align="center" width="100%" >
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180656954-cb08416c-4c78-40d0-bbf6-96416f83f8ba.jpg" width="21%"> 
  &nbsp
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180656815-d9c74186-118f-4ce6-bd0c-e885f4d885ea.png" width="63%">
</p>

### Recording (RX)

If the smartphone receives the message from the unit, after some checks, uploads the message on the realtime database just created and append it in the temporary file.

<p align="center" width="100%" >
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180657135-582a4c70-177c-4442-aef7-309ee42be16d.jpg" width="21%"> 
  &nbsp
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180657144-2a12ee74-06ab-4733-b4b2-a3b7a9e21af5.png" width="63%">
</p>

## Background functions

### Update info and Backup file on Firebase Storage

During the whole length of the recording, it’s possible to set some additional information describing the current posture or extra info of the patient. 
This information will be visible in the header of the output file.

For backup an safety purposes, the current file of the recording is uploaded on the database if ANT disconnection occurs.
But also each 10 minutes while recording.

<p align="center" width="100%" >
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180657258-5d4d3ac9-1ece-4a8c-8766-094477e6fce5.gif" width="21%">
  &nbsp
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180657298-0b56ad16-e333-4741-ab21-ba3e22bfef78.PNG" width="63%">
</p>

### Sensors disconnection algorithm

In case the smartphone is not receiving one or more messages from some units, proper warnings are shown to the user.
The algorithm behind this implementation is based on a counter. 

<p align="center" width="100%" >
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180657815-ca63804a-8754-40af-9570-263115dd8868.gif" width="21%">
  &nbsp
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180657973-bd9143ae-1157-4617-9b28-01611ec668f0.png" width="63%">
</p>

### Low battery detection

Accessing a specific byte of the data message and converting the value through a function it’s possible to retrieve the battery voltage level of the unit and in case show the warning.

<p align="center" width="100%" >
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180658843-41bb9ca7-2f4a-4d0e-9651-56750ff6e7e3.jpg" width="21%">
  &nbsp
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180658849-6362c46b-a3bc-493a-bdae-7396cb4b0e0e.jpg" width="21%">
</p>

### ANT disconnection

If the ANT channel completely disconnect itself, a warning dialog pops up and force the user to restart the initialization.
However, the data of the recording won't be lost, because of a background backup function explained before.

<p align="center" width="100%" >
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180658968-4f88cbe4-84b5-4db7-9937-4c28a9f2917e.gif" width="21%">
  &nbsp
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180658984-195bc7f4-7e59-4ac2-b69a-58d4ce463f01.jpg" width="21%">
  &nbsp
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180658992-d565859a-ec82-4241-8a83-553910f1a688.jpg" width="21%">
</p>

## End of the recording

At the end of a recording, the application saves all the needed information added during the recording in the header of the file and upload it to the Firebase Storage. 

The user can choose to download the data or go to a new recording without initializing again the communication because the sensors are kept in pause mode with their LEDs on.

<p align="center" width="100%" >
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180659132-b56cb01f-d5af-4877-8be6-e6eec6550bb7.jpg" width="21%">
  &nbsp 
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180659144-f5006473-29a3-4e12-9f51-ac4158252cac.png" width="63%">
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180659312-736b1330-cdf3-4dab-9712-76e67c68f865.jpg" width="21%">
  &nbsp
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180659314-1bb5067a-45c4-41bd-9964-156dd74df529.jpg" width="21%">
  &nbsp
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180659138-84b96a4d-cde5-4489-83e0-5ead5c499a89.png" width="42%">
</p>

# Web App

The web application is configured as a “read-only” web application in the sense that it is only possible to see and download data from it, without adding or changing any information. So, it’s not intended to record acquisitions. The structure of the web application is quite like the mobile one. 

The main features of the web application, fully explained below in the paragraphs, are the following:
* to manage the user authentication
* to display all the patients
* to download any file for a selected patient

A flowchart summarizing the main workflow of the application is here presented.

<p align="center" width="100%">
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180839250-7786460e-c6c0-412c-82d8-d45de37d678d.png" width="600">
</p>

## Authentication

The authentication is like the mobile one, but it’s not possible to register. 

<p align="center" width="100%">
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180870105-a50cf96b-8390-4660-b1a5-dd38fc80ab7c.gif">
</p>

## Patients list

Then all the patients from all the accounts authenticated in the smartphone application are displayed.

<p align="center" width="100%">
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180870974-5807b7fc-765a-4bd3-84f1-3bf12183e1e0.png">
</p>

## Lateral menu and support page

There is a lateral menu totally equal to the mobile one [previously described](#lateral-menu-and-support-page).

<p align="center" width="100%">
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180871685-10be7a92-49e4-45d1-a455-6c7eb324931a.gif">
</p>

## Patient files

Clicking on the desired patient, the user can see and download his recording files.

<p align="center" width="100%">
  <img align="center" src="https://user-images.githubusercontent.com/109110970/180871848-c4c033f8-a90e-4185-8595-e338526e3aa3.png">
</p>

## License

Politecnico Di Milano


