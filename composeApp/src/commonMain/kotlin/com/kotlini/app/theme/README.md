# Adding Vazir Font to the Project

This document provides instructions on how to properly add the Vazir font to the project.

## Steps to Add Vazir Font

1. Download the Vazir font files from the official source: [Vazir Font on GitHub](https://github.com/rastikerdar/vazir-font)

2. Create a `font` directory in the `composeApp/src/commonMain/composeResources` directory if it doesn't already exist:
   ```
   mkdir -p composeApp/src/commonMain/composeResources/font
   ```

3. Copy the following font files to the `font` directory:
   - `Vazir-Regular.ttf`
   - `Vazir-Bold.ttf`
   - `Vazir-Medium.ttf`
   - `Vazir-Light.ttf`

4. Rebuild the project to generate the necessary resource files:
   ```
   ./gradlew build
   ```

5. Update the `Type.kt` file to use the Vazir font resources:
   ```kotlin
   package com.kotlini.app.theme

   import androidx.compose.material3.Typography
   import androidx.compose.ui.text.TextStyle
   import androidx.compose.ui.text.font.FontFamily
   import androidx.compose.ui.text.font.FontWeight
   import androidx.compose.ui.unit.sp
   import androidx.compose.runtime.Composable
   import org.jetbrains.compose.resources.ExperimentalResourceApi
   import org.jetbrains.compose.resources.Font
   import kotlini.composeapp.generated.resources.Res
   import kotlini.composeapp.generated.resources.vazir_regular
   import kotlini.composeapp.generated.resources.vazir_bold
   import kotlini.composeapp.generated.resources.vazir_medium
   import kotlini.composeapp.generated.resources.vazir_light

   @OptIn(ExperimentalResourceApi::class)
   @Composable
   fun vazirFontFamily(): FontFamily {
       return FontFamily(
           Font(Res.font.vazir_regular, FontWeight.Normal),
           Font(Res.font.vazir_bold, FontWeight.Bold),
           Font(Res.font.vazir_medium, FontWeight.Medium),
           Font(Res.font.vazir_light, FontWeight.Light)
       )
   }

   @Composable
   fun getAppTypography(): Typography {
       val vazirFamily = vazirFontFamily()
       
       return Typography(
           // Use the same TextStyle definitions as in the current AppTypography,
           // but replace VazirFontFamily with vazirFamily
           // ...
       )
   }
   ```

6. Update the `KotliniTheme.kt` file to use the composable typography:
   ```kotlin
   @Composable
   fun KotliniTheme(
       content: @Composable () -> Unit
   ) {
       val isDarkTheme = isSystemInDarkTheme()
       
       MaterialTheme(
           colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme,
           typography = getAppTypography()
       ) {
           Surface(
               modifier = Modifier.fillMaxSize()
           ) {
               content()
           }
       }
   }
   ```

## Current Implementation

Currently, the project is using `FontFamily.Default` as a placeholder for the Vazir font. Once you add the actual font files and rebuild the project, you can update the code to use the real Vazir font.