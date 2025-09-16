# Demo 🎥

[Demo](https://github.com/SaileshLimbu/damaru/raw/theone/demo.gif)


# Damaru – Screen Sharing & Remote Control Suite

Damaru is a suite of Android applications built on **WebRTC** that enables secure remote screen sharing and device control.  
The codebase includes **three apps**:

1. **Admin App** – Manage users, devices, and permissions.  
2. **Client App** – Register, connect to devices, and initiate remote sessions.  
3. **Server App** – Run on a device to allow remote access and control.

---

## 📱 Features

### 🔑 Registration & User Management
- Clients can register with the system.
- Admin can manage users and assign multiple devices to them.
- Primary users can invite secondary users and share access to specific devices.
- Secondary users’ activity logs include:
  - Login date & time  
  - Location (if enabled)  
  - Session duration  

### 🎥 Remote Screen Sharing & Control
- Low-latency screen streaming using WebRTC.  
- Full remote control (touch events, navigation, input).  
- Multi-user access supported.  

### 🖥️ Apps Overview
- **Admin App**
  - Manage user accounts  
  - Assign devices to primary/secondary users  
  - View user activity logs  

- **Client App**
  - Register and log in  
  - Connect to assigned devices  
  - Share device access with secondary users  

- **Server App**
  - Install on the target device  
  - Allows Client App users to access and control the device remotely  

---

## ⚙️ Tech Stack
- **Language:** Kotlin (Android)  
- **Architecture:** MVVM + Hilt  
- **Networking:** WebRTC, WebSockets  
- **Database:** Room / SQLite (local), with server-side user management  
- **Authentication:** JWT / Token-based  

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Giraffe or later  
- Android 10+ devices (tested on Android 14)  
- Internet connectivity for WebRTC signaling  

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/SaileshLimbu/damaru
   cd damaru
