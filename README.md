PRODUCT REQUIREMENTS DOCUMENT (PRD)

Project: Call Recorder (Pixel-Compatible)

---

1. Overview

Build a native Android call recording application using Kotlin, designed to work reliably on restricted devices like the Google Pixel 9a.

The application will:

- Automatically detect phone calls
- Record audio using microphone (speaker-assisted)
- Save and organize recordings
- Run reliably in background using a foreground service

---

2. Goals

Primary Goals

- Reliable call detection (incoming and outgoing)
- Automatic start and stop recording
- Stable background recording using foreground service
- Structured file storage with metadata

Secondary Goals

- Simple playback UI
- Search and filter recordings
- Future support for transcription

---

3. Constraints

- Internal call audio access is not available (Android restriction)
- Audio must be recorded using microphone (MIC)
- Must comply with Android 13+ background execution limits
- Foreground service is mandatory for recording

---

4. Tech Stack

- Language: Kotlin
- Architecture: MVVM (lightweight)
- Build System: Gradle
- Base Template:
- Min SDK: 24
- Target SDK: 34

---

5. Core Features

5.1 Call Detection

Description:
Detect call lifecycle events.

Implementation:

- Use TelephonyManager with PhoneStateListener
- Detect states:
  - CALL_STATE_RINGING
  - CALL_STATE_OFFHOOK
  - CALL_STATE_IDLE

Behavior:

- Start recording when state becomes OFFHOOK
- Stop recording when state becomes IDLE

---

5.2 Audio Recording

Description:
Record call audio via microphone.

Implementation:

- Use MediaRecorder
- Audio source: MIC
- Output format: MPEG_4 or 3GP
- Audio encoder: AAC

Enhancement:

- Enable speakerphone using AudioManager for better capture

---

5.3 Foreground Service

Description:
Ensure recording continues in background.

Implementation:

- Create RecordingService
- Use persistent notification
- Start service when call begins
- Stop service when call ends

---

5.4 File Management

Description:
Store recordings in structured format.

Naming Convention:
Call_<number>_<YYYYMMDD_HHMMSS>.mp3

Storage:

- App-specific internal storage

---

5.5 Recording List UI

Description:
Display all saved recordings.

Features:

- RecyclerView list
- Show:
  - Phone number
  - Date and time
  - Duration (optional)

---

5.6 Playback

Description:
Play recorded files.

Implementation:

- Use MediaPlayer

---

6. Permissions

Required permissions:

- RECORD_AUDIO
- READ_PHONE_STATE
- FOREGROUND_SERVICE
- READ_CALL_LOG (optional)

All permissions must be requested at runtime.

---

7. Architecture

Folder Structure

app/
├── ui/
├── service/
├── recorder/
├── telephony/
├── data/

---

Modules

telephony/

- CallStateManager
- Handles call detection

recorder/

- RecorderManager
- Manages MediaRecorder lifecycle

service/

- RecordingService
- Handles foreground execution

data/

- Recording model
- File metadata handling

---

8. Flow

1. App initializes call listener
2. Call detected (incoming or outgoing)
3. State changes to OFFHOOK
4. Foreground service starts
5. Recording starts
6. Call ends (IDLE state)
7. Recording stops
8. File is saved
9. UI updates with new recording

---

9. Edge Cases

- Permission denied: show error and disable feature
- App killed: ensure service restart strategy if possible
- Recorder failure: handle exceptions and retry safely
- Low storage: stop recording and notify user

---

10. Non-Functional Requirements

- App must not crash during calls
- Recording must start within 1 second of call connection
- Battery usage must be optimized
- Code must follow modular architecture
- Logs should be added for debugging

---

11. Future Enhancements

- Speech-to-text transcription
- Cloud backup integration
- Contact name resolution
- Search by keywords
- AI-based summaries

---

12. Agent Instructions

- Do not modify base project structure from template
- Implement features incrementally
- Ensure Gradle builds successfully after each change
- Use Kotlin only (no Java)
- Use foreground service for all recording operations
- Validate runtime permissions before execution

---