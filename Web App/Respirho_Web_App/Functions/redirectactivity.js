function redirectActivity(id) {
    document.getElementById(id).onclick = function () {
        location.href = "www.yoursite.com";
  };
}