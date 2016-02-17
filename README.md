Cordova plugin for Samsung Account
============
Will build a cordova plugin for integration Samsung account
##### How to use in Android
1. Download or clone  code from github repository
2. Copy *src/android* Java source code to project
3. Copy *www/samsung-account.js* to www folder
4. Add plugin definition in *res/xml/config.xml*
<pre><code>
	<feature name="SumsungAccount">
        <param name="android-package" value="com.samsung.cordova.plugin.Account" />
    </feature>
</code></pre>

5. In  AndroidManifest.xml add <pre><code><uses-permission android:name="android.permission.GET_ACCOUNTS" /></code></pre>

6. In index.html adding  <pre><code><script type="text/javascript" src="samsung-account.js"></script></code></pre>

7. Use
<pre><code>
 function login(){
    	window.samsungAccount.login('x0n2h8rlgd','4584B4C082D2FF2C639F7983FDA0F62B',function(result){
    		alert(result);
    	},function(error){
    		alert(error);
    	});
    	return false;
    }
</code></pre>

#### Sample code
Sample code in *sample* folder