# Notes App

The Notes App is a simple Android application that allows users to manage their notes. The app includes features such as Google Sign-In, adding, updating, and deleting notes. It follows the MVVM architecture and utilizes XML, Room Database, and Credential Manager for Google Sign-In.

## Features

- **Single Activity**: The app uses a single activity with multiple fragments.
- **Google Sign-In**: Users can sign in using their Google account.
- **Note Management**: Users can add, update, and delete notes.
- **RecyclerView**: Notes are displayed in a RecyclerView.
- **MVVM Architecture**: The app is organized using the MVVM architecture pattern.
- **Room Database**: Notes are stored locally using Room Database, while Firebase Realtime Database is utilized to synchronize notes across various devices

## Screenshots

<img src = "https://github.com/ayush19sinha/NotesApp/blob/main/Screenshots/Register.png" width = "180">
<img src = "https://github.com/ayush19sinha/NotesApp/blob/main/Screenshots/Login%20new.png" width = "180">
<img src = "https://github.com/ayush19sinha/NotesApp/blob/main/Screenshots/Empty%20screen%20new.png" width = "180">
<img src = "https://github.com/ayush19sinha/NotesApp/blob/main/Screenshots/Add%20Edit%20Notess.png" width = "180">
<img src = "https://github.com/ayush19sinha/NotesApp/blob/main/Screenshots/note.png" width = "180">
<img src = "https://github.com/ayush19sinha/NotesApp/blob/main/Screenshots/search%20result.png" width = "180">
<img src = "https://github.com/ayush19sinha/NotesApp/blob/main/Screenshots/no%20search%20result.png" width = "180">
<img src = "https://github.com/ayush19sinha/NotesApp/blob/main/Screenshots/Made%20with.png" width = "180">



## Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/ayush19sinha/NotesApp.git
    cd notes-app
    ```

2. **Open in Android Studio**:
    - Open Android Studio.
    - Select `Open an existing project`.
    - Navigate to the cloned repository and select it.

3. **Setup Google API**:
    - Go to the [Google API Console](https://console.developers.google.com/).
    - Create a new project or select an existing project.
    - Configure the OAuth consent screen with necessary details.
    - Create OAuth 2.0 credentials for Android and Web applications.
    - Download the `google-services.json` file and place it in the `app` directory.

4. **Build the project**:
    - Sync the project with Gradle files.
    - Build and run the app on an emulator or a physical device.

## Usage

1. **Login**:
   
- Open the app and sign in using your Google account.

2. **View Notes**:
- After signing in, you will see a list of all your notes.

4. **Add Note**:
   
- Click the "Add Note" button to create a new note.

6. **Edit Note**:
   
- Click on a note to edit it.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.
