
var headers = new Headers();
headers.set('Accept', 'application/json');
function login(){

    var user = document.getElementsByName("user")[0].value.replace('@', '%40');
    var password = document.getElementsByName("password")[0].value;
    fetch('http://localhost:8080/auth/' + user + "/" + password ,{
        method: 'GET',
        headers: headers
    }).then(function(res){
        console.log('hit response');
        return res.json().then((result) => {
            console.log(result.credit_cards[0].id)
            alert(result.credit_cards[0].id)
        });
    }).catch((error) =>{
        console.error(error);
    });
}