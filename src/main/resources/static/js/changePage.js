var queryParams = new URLSearchParams(window.location.search);

function changePage(pageNo) {
queryParams.set("page", pageNo);
window.location.replace("?" + queryParams.toString())
}