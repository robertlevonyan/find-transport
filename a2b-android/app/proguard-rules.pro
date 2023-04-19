# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes Annotation

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class robert.findtransport.data.entity.** { *; }
-keepclassmembernames class robert.findtransport.data.entity.Stop { !transient <fields>; <methods>;}
-keepclassmembernames class robert.findtransport.data.entity.StopLocation { !transient <fields>; <methods>; }
-keepclassmembernames class robert.findtransport.data.entity.Transport { !transient <fields>; <methods>; }
-keepclassmembernames class robert.findtransport.data.entity.TransportStopJoin { !transient <fields>; <methods>; }
-keep class robert.findtransport.data.api.RetrofitClient { <fields>; }

-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class kotlin.coroutines.Continuation

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep class android.view.**

#-keep class robert.findtransport.data.entity.TransportStopJoin**
#-keep class robert.findtransport.data.entity.Transport**
#-keep class robert.findtransport.data.entity.TransportRoute**
#-keep class robert.findtransport.data.entity.Stop**
#-keep class robert.findtransport.data.entity.StopLocation
#-keepclassmembers class robert.findtransport.data.entity.TransportStopJoin** {<fields>; <methods>;}
#-keepclassmembers class robert.findtransport.data.entity.Transport** {<fields>; <methods>;}
#-keepclassmembers class robert.findtransport.data.entity.TransportRoute** {<fields>; <methods>;}
#-keepclassmembers class robert.findtransport.data.entity.Stop** {<fields>; <methods>;}
#-keepclassmembers class robert.findtransport.data.entity.StopLocation** {<fields>; <methods>;}
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
