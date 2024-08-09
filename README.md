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

<img src = "https://github.com/user-attachments/assets/a89eb2b5-cf01-4d9d-99c6-e2108220f126" width = "180">
<img src = "https://github.com/user-attachments/assets/1a518a56-fda6-473c-98df-b1c6966d1155" width = "180">
<img src = "https://github.com/user-attachments/assets/40d3039f-615f-42b6-8bf1-d599ccf69342" width = "180">
<img src = "https://github.com/user-attachments/assets/1cb57819-6f05-4a31-88bb-3104bdb16254" width = "180">
<img src = "https://github.com/user-attachments/assets/a3807906-04b1-4f79-8f42-503b210d3b4e" width = "180">
<img src = "https://github.com/user-attachments/assets/388e41e3-3c69-4f8a-88d8-6b7f73984a44" width = "180">
<img src = "https://github.com/user-attachments/assets/a463e78e-a218-4ef6-b679-c075ba4d26e5" width = "180">



## Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/ayush19sinha/notes-app.git
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
