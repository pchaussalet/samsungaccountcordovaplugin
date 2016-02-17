Cordova plugin for Samsung Account
============
Will build a cordova plugin for integration Samsung account
##### How to use in Android

* Download or clone  code from github repository
* Copy src/android Java source code to project
* Copy www/samsung-account.js to www folder
* Add plugin definition in res/xml/config.xml
```HTML
<feature name="SumsungAccount">
   <param name="android-package" value="com.samsung.cordova.plugin.Account" />
</feature>
```
* In  AndroidManifest.xml add
```HTML
<uses-permission android:name="android.permission.GET_ACCOUNTS" />
```
* In index.html adding 
```HTML
<script type="text/javascript" src="samsung-account.js"></script>
```
* Use
```JAVASCRIPT
 function login(){
    	window.samsungAccount.login('x0n2h8rlgd','4584B4C082D2FF2C639F7983FDA0F62B',function(result){
    		alert(result);
    	},function(error){
    		alert(error);
    	});
    	return false;
    }
```
#### Sample code
Sample code in *sample* folder
