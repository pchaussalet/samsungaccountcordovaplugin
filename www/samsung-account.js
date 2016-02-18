
var SamsungAccount = {

    login: function(clientId,clientSecret,callback,callback_error) {
       cordova.exec(callback,callback_error,"SumsungAccount", "login", [clientId,clientSecret]);
    }
};

module.exports = SamsungAccount;