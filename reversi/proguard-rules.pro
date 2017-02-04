## This is a configuration file for ProGuard.
## http://proguard.sourceforge.net/index.html#manual/usage.html
#
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-verbose
#-dontwarn
#
## Optimization is turned off by default. Dex does not like code run
## through the ProGuard optimize and preverify steps (and performs some
## of these optimizations on its own).
#-dontoptimize
#-dontpreverify
## Note that if you want to enable optimization, you cannot just
## include optimization flags in your own project configuration file;
## instead you will need to point to the
## "proguard-android-optimize.txt" file instead of this one from your
## project.properties file.
#
#-keepattributes *Annotation*
#-keep public class com.google.vending.licensing.ILicensingService
#-keep public class com.android.vending.licensing.ILicensingService
#
## For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
## keep setters in Views so that animations can still work.
## see http://proguard.sourceforge.net/manual/examples.html#beans
#-keepclassmembers public class * extends android.view.View {
#   void set*(***);
#   *** get*();
#}
#
## We want to keep methods in Activity that could be used in the XML attribute onClick
#-keepclassmembers class * extends android.app.Activity {
#   public void *(android.view.View);
#}
#
## For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
#-keep class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator *;
#}
#
#-keepclassmembers class **.R$* {
#    public static <fields>;
#}
#
#-keep class android.support.v4.** {*;}
#
#-keep class org.xmlpull.** {*;}
#
#-keep class com.hyphenate.* {*;}
#-keep class com.hyphenate.chat.** {*;}
#-keep class org.jivesoftware.** {*;}
#-keep class org.apache.** {*;}
##另外，demo中发送表情的时候使用到反射，需要keep SmileUtils,注意前面的包名，
##不要SmileUtils复制到自己的项目下keep的时候还是写的demo里的包名
#-keep class com.hyphenate.easeui.utils.SmileUtils {*;}
#
#
#-keep class com.qq.e.** {
#    public protected *;
#}
#
#-keep class android.support.v4.app.NotificationCompat**{
#    public *;
#}
#
#-keep class android.support.v7.** {*;}
#
#-keep class com.ue.chess_life.**{*;}
#-keep class com.ue.common.**{*;}
#-keep class com.ue.chess.**{*;}
#-keep class com.ue.cnchess.**{*;}
#-keep class com.ue.gobang.**{*;}
#-keep class com.ue.reversi.**{*;}
#-keep class com.bumptech.glide.**{*;}