# WildLens 🌿 — SDG 15: Life on Land

> A wildlife observation app that helps users identify, log, and share biodiversity sightings in Malaysia.

---

## 👤 Student Info

| Field | Details |
|-------|---------|
| Name | YANG KAI |
| Matric No. | A207408 |
| Instructor | Cikgu Izwan |
| Course | TM2213 Mobile Programming |

---

## 🌍 SDG Theme

**SDG 15 — Life on Land**

WildLens encourages users to actively participate in biodiversity conservation by identifying and recording wildlife sightings. Every observation contributes to a shared community database, raising awareness of Malaysia's protected species.

---

## 📱 App Screens (7 Screens)

| # | Screen | Description |
|---|--------|-------------|
| 1 | Home | Species grid with search and view mode toggle |
| 2 | Menu | Browse species by category (Birds, Mammals, etc.) |
| 3 | Identify | Scan and identify nearby species using AI simulation |
| 4 | Profile | Set username and detect real location via GPS |
| 5 | Activity | View personal sighting log saved locally via Room |
| 6 | Discover | Live species observations in Malaysia from iNaturalist API |
| 7 | Community | Real-time community sightings synced via Firebase Firestore |

---

## ⚙️ Features

### 🗄️ Local Persistence — Room Database
- Every species identified is saved permanently to a local Room database
- Data is accessible offline and persists across app restarts

### ☁️ Cloud Integration — Firebase Firestore
- Users can share their sightings to a public community feed
- CommunityScreen listens to Firestore in real-time using `callbackFlow`

### 🌐 REST API — iNaturalist
- DiscoverScreen fetches live, research-grade wildlife observations from Malaysia
- Implemented using Retrofit 2 with Gson converter
- Displays species name, scientific name, location, and real photo via Coil

### 📡 Hardware Sensor — GPS / Location
- ProfileScreen uses `FusedLocationProviderClient` to get the user's real coordinates
- Coordinates are reverse-geocoded into a human-readable location name via `Geocoder`
- Location is used to tag each wildlife sighting

---

## 🛠️ Tech Stack

| Technology | Usage |
|------------|-------|
| Jetpack Compose | UI framework |
| Navigation Compose | Multi-screen navigation |
| ViewModel + StateFlow | State management |
| Room Database | Local data persistence |
| Firebase Firestore | Cloud data sync |
| Retrofit 2 + Gson | REST API calls |
| Coil | Network image loading |
| FusedLocationProvider | GPS sensor integration |
| Geocoder | Reverse geocoding |

---

## 🚀 How to Run

1. Clone this repository
```bash
git clone https://github.com/KKKKY668/A207408_YangKai_CikguIzwan_Project2.git
```
2. Open in Android Studio (Hedgehog or later)
3. Add your own `google-services.json` from Firebase Console into the `app/` folder
4. Sync Gradle and run on a physical device or emulator (API 24+)

> ⚠️ **Note:** GPS features require a physical device. The `google-services.json` file is excluded from this repo for security — you will need to create your own Firebase project.

---

## 📦 Dependencies
Retrofit 2.11.0
Coil 2.6.0
Firebase BOM 33.1.0
Room 2.7.0-alpha11
Play Services Location 21.3.0
Navigation Compose 2.7.7
