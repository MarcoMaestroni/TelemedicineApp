// Import the functions you need from the SDKs you need
import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.1.2/firebase-app.js' ; 
import { getAuth,signOut } from 'https://www.gstatic.com/firebasejs/9.1.2/firebase-auth.js';
import { getStorage, ref, getDownloadURL } from 'https://www.gstatic.com/firebasejs/9.1.2/firebase-storage.js';
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyC0i-Thrw8nRfFdqFePyheynP4f-Izr0HU",
  authDomain: "respirho-70008.firebaseapp.com",
  databaseURL: "https://respirho-70008-default-rtdb.firebaseio.com",
  projectId: "respirho-70008",
  storageBucket: "respirho-70008.appspot.com",
  messagingSenderId: "107390019239",
  appId: "1:107390019239:web:cbf5b6902d04c9a75add82",
  measurementId: "G-YHQBW75Y7S"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const storage = getStorage(app);

//DRAWER
//display user and mail in drawer
var userdrawer = localStorage.getItem("userdrawer");
var maildrawer = localStorage.getItem("maildrawer");

//alert(userdrawer+maildrawer);

document.getElementById("userdrawer").innerText=userdrawer;
document.getElementById("maildrawer").innerText=maildrawer;

// Create a reference under which you want to download the user guide
const userguideRef = ref(storage, 'Documentation/Guida_utente_app_1.pdf');

document.getElementById("userguidebutton").onclick=function(){
  // Get the download URL
  getDownloadURL(userguideRef)
  .then((url) => {
    //alert("url is: " + url);
    window.open(url);
  })
  .catch((error) => {
    //alert("error");
    // A full list of error codes is available at
    // https://firebase.google.com/docs/storage/web/handle-errors
    switch (error.code) {
      case 'storage/object-not-found':
        // File doesn't exist
        alert("File doesn't exist");
        break;
      case 'storage/unauthorized':
        // User doesn't have permission to access the object
        alert("User doesn't have permission to access the object");
        break;
      case 'storage/canceled':
        // User canceled the upload
        alert("User canceled the upload");
        break;

      case 'storage/unknown':
        // Unknown error occurred, inspect the server response
        alert("Unknown error occurred, inspect the server response");
        break;
    }
  });
}

//LOGOUT BUTTON
document.getElementById("logout_nd").addEventListener('click', function(){

  signOut(auth).then(() => {
    //alert("SIGN OUT CORRECTLY")
    location.href='/Login/login.html';
  }).catch((error) => {
    // An error happened.
    alert("SIGN OUT ERROR")
  });

});















