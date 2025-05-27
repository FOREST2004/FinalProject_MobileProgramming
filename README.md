# Photo Management App

A modern Android application for managing, editing, and sharing photos, built with Jetpack Compose and Material Design 3.

## Features

- **Photo Gallery**: View, add, and manage photos in a clean grid layout
- **Album Management**: Organize photos into custom albums
- **Photo Editing**: Basic editing features including rotate, flip, and crop
- **Photo Sharing**: Share photos directly to social media and messaging apps
- **Favorites**: Mark and filter favorite photos
- **Dark Mode**: Toggle between light and dark themes
- **Data Management**: Options to manage application data

## Screenshots

[Insert screenshots of your application here]

## Technologies Used

- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit for building native UI
- **MVVM Architecture**: Clean separation of UI, business logic, and data
- **Room Database**: Local storage for photos and albums
- **Kotlin Coroutines & Flow**: Asynchronous operations and reactive streams
- **Coil**: Image loading library
- **Material Design 3**: Modern design system

## Prerequisites

- Android Studio Arctic Fox (2021.3.1) or newer
- JDK 11 or higher
- Android SDK 33+ (compileSdk 35)
- Android device or emulator running API 24 (Android 7.0) or higher

## Setup & Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/photo-management.git
   cd photo-management
   ```

2. **Open the project in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned project directory and click "Open"

3. **Sync Gradle**
   - Android Studio should automatically sync the Gradle files
   - If not, select "File > Sync Project with Gradle Files"

4. **Configure local.properties (if needed)**
   - Make sure your `local.properties` file contains the correct path to your Android SDK
   ```
   sdk.dir=/path/to/your/Android/sdk
   ```

5. **Build the project**
   - Click on "Build > Make Project" or use the shortcut (Ctrl+F9 on Windows/Linux, Cmd+F9 on macOS)

## Running the Application

### On a Physical Device

1. Enable USB debugging on your Android device:
   - Go to "Settings > About phone"
   - Tap "Build number" seven times to enable Developer Options
   - Go back to Settings, navigate to "System > Developer options" and enable "USB debugging"

2. Connect your device to your computer using a USB cable

3. In Android Studio, click the "Run" button (green triangle) or press Shift+F10

4. Select your device from the list and click "OK"

### On an Emulator

1. In Android Studio, open the AVD Manager (Tools > AVD Manager)

2. Create a new virtual device if you don't have one, or select an existing one

3. Click the "Run" button (green triangle) in Android Studio or press Shift+F10

4. Select the emulator and click "OK"

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/photomanagement/
│   │   │   ├── data/
│   │   │   │   ├── db/               # Room Database classes
│   │   │   │   ├── model/            # Data models
│   │   │   │   ├── preferences/      # User preferences
│   │   │   │   └── repository/       # Data repositories
│   │   │   ├── ui/
│   │   │   │   ├── components/       # Reusable UI components
│   │   │   │   ├── navigation/       # Navigation components
│   │   │   │   ├── screen/           # Application screens
│   │   │   │   ├── theme/            # Theme and styling
│   │   │   │   └── viewmodel/        # ViewModels
│   │   │   └── utils/                # Utility classes
│   │   └── res/                      # Android resources
│   └── androidTest/                  # Instrumentation tests
└── build.gradle.kts                  # App-level Gradle config
```

## Permissions

The application requires the following permissions:

- `READ_EXTERNAL_STORAGE` - To access photos on the device
- `WRITE_EXTERNAL_STORAGE` - To save edited photos (for Android < 10)

These permissions will be requested at runtime when needed.

## Building for Release

1. Generate a signed APK/Bundle:
   - In Android Studio, select "Build > Generate Signed Bundle/APK"
   - Follow the instructions to create or select a key store
   - Choose the build variant (typically "release")
   - Click "Finish" to generate the APK or Bundle

2. The signed APK will be available in:
   ```
   app/release/app-release.apk
   ```

## Troubleshooting

- **Build fails with Gradle errors**:
  - Try "File > Invalidate Caches / Restart"
  - Update Gradle plugin and wrapper versions
  
- **App crashes when accessing photos**:
  - Check that you've granted the necessary permissions in Settings

- **Images not loading**:
  - Verify that the image URIs are valid and accessible
  - Check Logcat for specific error messages

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/my-new-feature`
5. Submit a pull request



## Contact

thuannguyen04.forwork@gmail.com

Project Link: https://github.com/FOREST2004/FinalProject_MobileProgramming
