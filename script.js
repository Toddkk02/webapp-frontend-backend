// post date script
let date = document.getElementById("release-date");
let currentDate = new Date();
let day = String(currentDate.getDate()).padStart(2, '0');
let month = String(currentDate.getMonth() + 1).padStart(2, '0');
let year = currentDate.getFullYear();
date.innerHTML = day + '/' + month + '/' + year;
