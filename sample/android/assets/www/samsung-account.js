var SamsungAccount = function(){};
SamsungAccount.login = function(clientId,clientSecret,callback,callback_error){
 cordova.exec(callback,callback_error,"SumsungAccount", "login", [clientId,clientSecret]);
}
window.samsungAccount = SamsungAccount;