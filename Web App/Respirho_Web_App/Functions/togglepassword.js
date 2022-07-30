const togglePassword = document.querySelector('#togglePassword');
const password = document.querySelector('#password');

const toggleConfirmPassword = document.querySelector('#toggleConfirmPassword');
const confirmpassword = document.querySelector('#confirmpassword');

togglePassword.addEventListener('click', function (e) {
    if(password.getAttribute('type')==='password'){
        password.setAttribute('type','text'); //visible
        document.getElementById("togglePassword").style.
        color='blue';
    }

    else{
        password.setAttribute('type','password'); //dots
        document.getElementById("togglePassword").style.
        color='grey';
    }
});

toggleConfirmPassword.addEventListener('click', function (e) {
    if(confirmpassword.getAttribute('type')==='password'){
        confirmpassword.setAttribute('type','text'); //visible
        document.getElementById("toggleConfirmPassword").style.
        color='blue';
    }

    else{
        confirmpassword.setAttribute('type','password'); //dots
        document.getElementById("toggleConfirmPassword").style.
        color='grey';
    }
});
