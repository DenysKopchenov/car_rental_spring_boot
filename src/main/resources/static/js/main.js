var queryParams = new URLSearchParams(window.location.search);

function changePage(pageNo) {
queryParams.set("page", pageNo);
window.location.replace("?" + queryParams.toString())
}

function selectEn() {
queryParams.set("locale", "en");
window.location.replace("?" + queryParams.toString())
}

function selectUk() {
queryParams.set("locale", "uk");
window.location.replace("?" + queryParams.toString())
}