let page = 1;
let tag = undefined;

function getGifts() {
    let substr = document.getElementById('search-substr').value;
    console.log(substr);

    let url = `http://localhost:8091/gifts?sort_by=-createDate&page=${page}&limit=40&substr=${substr}`;
    console.log(url);
    if(tag !== undefined) {
        url += `&tag=${tag}`;
    }
    console.log(url);
    fetch(url)
        .then(response => response.json())
        .then(data => {
            let giftsArray = data._embedded.giftAndTagDtoList;

            giftsArray.forEach(gift => {
                let price = gift.price;
                price = price.toFixed(1);
                let html = `<div class="image"></div>
                            <h3>${gift.name}</h3> 
                            <p>${gift.description}</p> <br>
                            <div class="price-and-button">
                                <h3>${price}$</h3>
                                <button onclick="location.href = 'item-details.html'" type="button">Add to card</button>
                            </div>`;
                let newDiv = document.createElement("div");
                newDiv.innerHTML = html;
                document.getElementById("infinity-container").appendChild(newDiv);

            });


        });


}

getGifts();

const input = document.getElementById("search-substr");

input.addEventListener('input', () => {
    page = 1;
    document.getElementById("infinity-container").innerHTML = '';
    getGifts();
});


window.addEventListener('scroll', () => {
    if (window.scrollY + window.innerHeight >= document.documentElement.scrollHeight) {
        page++;
        getGifts();
    }
})

const showOnPx = 100;
const backToTopButton = document.querySelector(".back-to-top")

const scrollContainer = () => {
    return document.documentElement || document.body;
};

document.addEventListener("scroll", () => {
    if (scrollContainer().scrollTop > showOnPx) {
        backToTopButton.classList.remove("hidden")
    } else {
        backToTopButton.classList.add("hidden")
    }
})

const goToTop = () => {
    document.body.scrollIntoView({
        behavior: "smooth",
    });
};

backToTopButton.addEventListener("click", goToTop)

/* Когда пользователь нажимает на кнопку,
переключение между скрытием и отображением раскрывающегося содержимого */
function myFunction() {
    document.getElementById("myDropdown").classList.toggle("show");
}

// Закройте выпадающее меню, если пользователь щелкает за его пределами
window.onclick = function(event) {
    if (!event.target.matches('.dropbtn')) {
        let dropdowns = document.getElementsByClassName("dropdown-content");
        let i;
        for (i = 0; i < dropdowns.length; i++) {
            let openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }
}

const list =  document.getElementById("myDropdown");

list.addEventListener('click', (ev => {
    let category = ev.target;
    if(category.textContent === "show all") {
        tag = undefined;
        document.getElementById("dropbth").textContent = "Most Popular Tags";
    } else {
        tag = category.textContent;
        document.getElementById("dropbth").textContent = tag;
    }
    document.getElementById("infinity-container").innerHTML = '';
    getGifts();
}))

window.addEventListener("DOMContentLoaded", function() {
    if (localStorage.getItem('scrollpos') === undefined) {
        scrollContainer().scrollTop = 0;
        console.log(localStorage.getItem('scrollpos'));
        console.log(0);
    } else {
        scrollContainer().scrollTop = sessionStorage.getItem('scrollpos');
        console.log(localStorage.getItem('scrollpos'));
    }
    console.log("load");
});
