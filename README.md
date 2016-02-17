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
5. In  AndroidManifest.xml add <uses-permission android:name="android.permission.GET_ACCOUNTS" />
#### Sample code
Sample code in *sample* folder