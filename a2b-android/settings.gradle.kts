include(":app")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication { create<BasicAuthentication>("basic") }
            credentials {
                username = "mapbox"
                val properties = java.util.Properties()
                properties.load(File("${rootDir.path}/local.properties").inputStream())
                password = properties.getProperty("MAPBOX_DOWNLOADS_TOKEN")
            }
        }
    }
}
