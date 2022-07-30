// Import the functions you need from the SDKs you need
import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.1.2/firebase-app.js' ; 
import { getAuth, sendPasswordResetEmail} from 'https://www.gstatic.com/firebasejs/9.1.2/firebase-auth.js';
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

//FORGOT PASSWORD
document.getElementById("resetpasswordbutton").addEventListener('click', function(){

  const emailReset = document.getElementById("emailReset").value;

  if(emailReset == ""){
    alert("Email text can't be empty")
    return false;
  }

  sendPasswordResetEmail(auth, emailReset)
  .then(() => {
    location.href='/Login/login.html';
    alert("Check your email to reset the password and then login")
    })
  .catch((error) => {
    const errorCode = error.code;
    const errorMessage = error.message;
    alert(errorMessage);
  });
});











