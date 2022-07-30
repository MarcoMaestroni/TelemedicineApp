// Import the functions you need from the SDKs you need
import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.1.2/firebase-app.js' ; 
import { getAuth,onAuthStateChanged, signOut } from 'https://www.gstatic.com/firebasejs/9.1.2/firebase-auth.js';
import { getStorage, ref, listAll } from 'https://www.gstatic.com/firebasejs/9.1.2/firebase-storage.js';
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
onAuthStateChanged(auth, (user) => {
  if (user) {

    const userdrawer = user.displayName;
    const maildrawer = user.email;

    localStorage.setItem("userdrawer", userdrawer);
    localStorage.setItem("maildrawer", maildrawer);
  
    document.getElementById("userdrawer").innerText=userdrawer;
    document.getElementById("maildrawer").innerText=maildrawer;
  } else {
    // User is signed out
    //alert("user errror");
  }
});

//PATIENTS LIST
// Create a reference under which you want to list
const listRef = ref(storage, 'Patients/');

//alert("listRef is: " + listRef);

//create an array to store the folders names and filenames
var folders=new Array ();

// Find all the prefixes and items.
listAll(listRef)
  .then((res) => {
    res.prefixes.forEach((folderRef) => {
      // All the folders under listRef.
      folders.push(folderRef.name);
      //alert(folderRef.name);
    });
    //then, add patients name to the list uploading the array content into div and p
    for(var i = 0; i < folders.length; i++) {
      var foldername=folders[i];

      const div = document.createElement('div');
      const p = document.createElement('p');

      div.className='view_item';
      div.id=foldername;

      p.textContent=foldername;

      document.getElementById('patientslist').appendChild(div);

      document.getElementById(div.id).appendChild(p);
    }

    //add onclick functions when an div of a idpatient is clicked
    var dividpatient = document.getElementsByClassName('view_item'); 
    //alert("idpatient.length is: " + dividpatient.length);

    //set the function to call for each patients in the list
    for (var j = 0; j < dividpatient.length; j++) {

      dividpatient[j].onclick = function (){
        //when the folder is clicked save the name in localStorage 
        //to display thei files in patientdata page
        localStorage.setItem("idpatientclicked", this.id);
        location.href='/PatientData/patientdata.html';
      }  
    }

  }).catch((error) => {
      // Uh-oh, an error occurred!
  });

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
  













