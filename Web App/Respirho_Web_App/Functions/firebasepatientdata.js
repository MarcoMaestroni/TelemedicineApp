// Import the functions you need from the SDKs you need
import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.1.2/firebase-app.js' ; 
import { getAuth,onAuthStateChanged, signOut } from 'https://www.gstatic.com/firebasejs/9.1.2/firebase-auth.js';
import { getStorage, ref, listAll, getDownloadURL } from 'https://www.gstatic.com/firebasejs/9.1.2/firebase-storage.js';
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

//check the idpatient clicked in the previous patientslist page and load his files
var idpatient=localStorage.getItem("idpatientclicked");
//show the id patient as a title
document.getElementById("patientdata_idpatient_title").innerText=idpatient;

// Create a reference under which you want to list
const listRef = ref(storage, 'Patients/' + idpatient + '/');

//alert("listRef is: " + listRef);

//create an array to store the folders names and filenames
var items=new Array ();

// Find all the prefixes and items.
listAll(listRef)
  .then((res) => {
    res.items.forEach((itemRef) => {
      // All the items under listRef.
      items.push(itemRef.name)

    });
    //then, add patients name to the list uploading the array content into div and p
    for(var i = 0; i < items.length; i++) {
      var itemname=items[i];

      const div = document.createElement('div');
      const p = document.createElement('p');

      div.className='view_item';
      div.id=itemname;

      p.textContent=itemname;

      document.getElementById('patientdata').appendChild(div);

      document.getElementById(div.id).appendChild(p);
    }


    //add download function when a div of a file is clicked
    var divfile = document.getElementsByClassName('view_item'); 
    //alert("idpatient.length is: " + divfile.length);

    //set the function to call for each patients in the list
    for (var j = 0; j < divfile.length; j++) {

      divfile[j].onclick = function (){
        
        //when the divfile is clicked download the file
        var filename=this.id;
        //alert(filename);
        // Create the reference to the file path
        const fileRef = ref(storage, 'Patients/' + idpatient + '/'+ filename);
        
        //alert(fileRef); //OOOKKK

        // Get the download URL
        getDownloadURL(fileRef)
        .then((url) => {
          //alert("url is: " + url);
          window.open(url);
        })
        .catch((error) => {
          alert("error");
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
    }

  }).catch((error) => {
      // Uh-oh, an error occurred!
      alert("ERROR: can't load the files");
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















