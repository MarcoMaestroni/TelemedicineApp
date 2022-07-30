// Import the functions you need from the SDKs you need
import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.1.2/firebase-app.js' ; 
import { getAuth,onAuthStateChanged, signInWithEmailAndPassword, signOut, signInWithPopup, GoogleAuthProvider} from 'https://www.gstatic.com/firebasejs/9.1.2/firebase-auth.js';
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
const provider = new GoogleAuthProvider();

/*
onAuthStateChanged(auth, (user) => {
  if (user) {
    // User is signed in, see docs for a list of available properties
    // https://firebase.google.com/docs/reference/js/firebase.User
    const uid = user.uid;
    // ...
  } else {
    // User is signed out
    // ...
  }
});
*/

//LOGIN FORM
document.getElementById("loginForm").addEventListener("submit",(event)=>{
  event.preventDefault()
})

//SIGN IN WITH GOOGLE
document.getElementById("signinwithgoogle").addEventListener('click', function(){

  signInWithPopup(auth, provider)
  .then((result) => {
    // This gives you a Google Access Token. You can use it to access the Google API.
    const credential = GoogleAuthProvider.credentialFromResult(result);
    const token = credential.accessToken;
    
    //localStorage.setItem("user", user);
    //localStorage.setItem("mail", mail);
    location.href='/PatientsList/patientslist.html'; //IT WORRRKKSSSS
  }).catch((error) => {
    // Handle Errors here.
    const errorCode = error.code;
    const errorMessage = error.message;
    // The email of the user's account used.
    //const email = error.email;
    // The AuthCredential type that was used.
    //const credential = GoogleAuthProvider.credentialFromError(error);
    //alert(errorCode + errorMessage);
  });
});

//LOGIN BUTTON
document.getElementById("loginbutton").addEventListener('click', function(){

  const emailText = document.getElementById("email").value
  const passwordText = document.getElementById("password").value 

  if(emailText == ""){
    return false;
  }

  if(passwordText == ""){
    return false;
  }

  signInWithEmailAndPassword(auth, emailText, passwordText)
  .then((userCredential) => {
      //alert("logged iiiinnnnn");
      location.href='/PatientsList/patientslist.html'; //IT WORRRKKSSSS
  })
  .catch((error) => {
      const errorCode = error.code;
      const errorMessage = error.message;

      alert("Credentials are wrong, try again");
  });
});










