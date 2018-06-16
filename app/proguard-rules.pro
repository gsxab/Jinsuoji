# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontskipnonpubliclibraryclassmembers

-keepattributes *Annotation*,EnclosingMethod

-keepnames class org.codehaus.jackson.** { *; }
-keepnames class com.fasterxml.jackson.annotation.** { *; }
-keep class com.haibin.calendarview.WeekBar {
    public <init>(android.content.Context);
}
-keep class org.jinsuoji.jinsuoji.calendar.ColorfulMonthView {
    public <init>(android.content.Context);
}
-keepclassmembers class org.jinsuoji.jinsuoji.account.Account { *; }
-keepclassmembers class org.jinsuoji.jinsuoji.net.AccountBean { *; }
-keepclassmembers class org.jinsuoji.jinsuoji.net.SaltTask$SaltBean { *; }
-keepclassmembers class org.jinsuoji.jinsuoji.net.TokenBean { *; }
-keepclassmembers class org.jinsuoji.jinsuoji.data_access.Serializer$* { *; }
-keepclassmembers class org.jinsuoji.jinsuoji.net.ErrorBean { *; }

-dontwarn javax.xml.**
-dontwarn javax.xml.stream.events.**
-dontwarn com.fasterxml.jackson.databind.**
