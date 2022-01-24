const container = document.querySelector('.container');

const URL = 'https://dog.ceo/api/breeds/image/random'

// get the images

function loadImages(numImages = 10){
    let i=0;
    while(i < numImages){
        fetch('https://dog.ceo/api/breeds/image/random')
            .then(response=>response.json())
            .then(data=>{
                console.log(data.message)
                const img =  document.createElement('img');
                img.src = `${data.message}`
                container.appendChild(img)
            })
        i++;
    }
}

loadImages();



// listen for scroll event and load more images if we reach the bottom of window
window.addEventListener('scroll',()=>{
    console.log("scrolled", window.scrollY) //scrolled from top
    console.log(window.innerHeight) //visible part of screen
    if(window.scrollY + window.innerHeight >= document.documentElement.scrollHeight){
        loadImages();
    }
})


//////////////////////////////////////////////////////////////////////////
async function getGifts() {
    let page = 1;
    let url = `http://localhost:8091/gifts?sort_by=id&page=${page}&limit=24`;
    try {
        let res = await fetch(url);
        return await res.json();
    } catch (error) {
        console.log(error);
    }

}
async function renderGifts() {
    let gifts = await getGifts();
    let giftsArray = gifts._embedded.giftAndTagDtoList;

    let html = '';
    giftsArray.forEach(gift => {
        let htmlSegment = `<div class="gift">
                            <h5>id = ${gift.id}</h5>
                            <h5>name = ${gift.name}</h5>
                            <h5>duration = ${gift.duration}</h5>
                            <h5>description =  ${gift.description}</h5>
                            <h5>price = ${gift.price}</h5>
                        </div>`;

        html += htmlSegment;
    });


    let container = document.querySelector('.body-container');
    container.innerHTML = html;


}

renderGifts();

window.addEventListener('scroll',()=>{
    console.log("scrolled", window.scrollY) //scrolled from top
    console.log(window.innerHeight) //visible part of screen
    if(window.scrollY + window.innerHeight >= document.documentElement.scrollHeight){

        renderGifts();
    }
})