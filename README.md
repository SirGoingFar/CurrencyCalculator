# Currency Calculator

This Android application makes it easy to convert amount value from one currency to another using a real-time 
rate data from Fixer.io.

The trend of the currency rate over a period of 30 and 90 days are represented on a line graph as well.

To build this codebase, clone it using the Android Studio, then follow these steps:

1. Create a developer account on https://fixer.io/product - get the free tier package

2. A unique ACCESS_KEY will be generated for you, copy it.

3. Open the .gitignore file, add "gradle.properties" (without quotation marks) if it's not added by default

4. On the codebase in Android Studio, open gradle.properties file.

5. In the file, add:

FIXER_IO_ACCESS_KEY = "{{access_key}}", e.g. FIXER_IO_ACCESS_KEY = "81f4f2520xxxxxxxx28640e95f"

6. Build the codebase and enjoy the Calculator!
