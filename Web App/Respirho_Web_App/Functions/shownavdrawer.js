const navdrawer=document.querySelector(".navdrawer")
const container=document.querySelector(".container")

function openNav() {
    navdrawer.style.width = "300px";
    container.style.display = "block"; //displays overlay
}

function closeNav() {
    navdrawer.style.width = "0";
    container.style.display = "grid";
} 




