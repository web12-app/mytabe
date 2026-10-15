# YouTube Clone for Android (Pips Tube)

A sleek, responsive YouTube clone Android application powered by Jetpack Compose and the live Pips Tube API, providing dedicated authentication, home feed discovery, high-fidelity video playback, channel pages, likes, and comment interactions in a classic YouTube dark aesthetic.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following user preferences were confirmed during the planning phase:
> - **Authentication Strategy**: Dedicated login and register screens on first launch with token persistence and quick-fill demo access for instant testing.
> - **Core Feature Priority**: Home video feed with category filters, full interactive video player with controls, channel pages with subscribe actions, and video comments with voting.
> - **Visual Style**: Modern YouTube Dark theme featuring deep charcoal surfaces (`#0F0F0F`), clean typography, crisp borders (`#272727`), and YouTube red accents (`#FF0000`).

---

## 1. Overview & Core Concept

- **What It Does**: Connects to the live Pips Tube API (`https://precision-pledge-fled.ngrok-free.dev`) to deliver an authentic YouTube mobile experience. Users can sign up or log in, browse published videos and reels, stream videos with full playback controls, like and dislike content, subscribe to creator channels, explore channel uploads, and post and vote on comments.
- **Target Audience / Persona**: Mobile video viewers and creators seeking a fast, familiar YouTube interface with real-time video playback and community interactions.
- **Key Value**: Direct, low-latency streaming and social participation using standard REST endpoints with Bearer token authentication, styled to match YouTube's iconic mobile application.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Authentication Flow (First Launch)**
   - Crisp branded splash screen with the YouTube Play icon.
   - Clean Tabbed / Toggle view between **Sign In** and **Create Account**.
   - Input validation (username, password), error feedback, and a **"Quick Test Login"** button for immediate one-tap evaluation.
   - Automatic token (`sat_...`) and session storage for persistent access.

2. **Home Feed & Discovery Flow**
   - **Top App Bar**: YouTube logo, notification bell, search icon, and circular user avatar.
   - **Category Chips**: Filter chips ("All", "Videos", "Reels", "Posts") with smooth horizontal scroll and active pill highlighting.
   - **Video Cards**: High-res thumbnail / video preview container, duration pill badge (`MM:SS`), channel avatar, video title, channel name, views count, and relative publish date ("3 days ago"), plus a 3-dot overflow menu for quick sharing.
   - **Pull-to-Refresh**: Seamless swipe-down feed refresh.

3. **Watch & Video Player Flow**
   - Seamless transition into the Watch Screen.
   - **Player**: 16:9 video player with custom controls overlay (Play/Pause, scrubbable progress bar, current time / total duration, fullscreen toggle, replay). Automatic view tracking via `POST /api/tube/videos/:fileId/view`.
   - **Metadata Row**: Video title, views, upload date, expandable description box.
   - **Action Bar**: Like button (with live count), Dislike button, Share, and Save.
   - **Channel Row**: Channel logo, channel title, subscriber count, and dynamic "Subscribe" / "Subscribed" button.
   - **Comments Bottom Section**: Top comment preview with tap-to-expand sheet or inline comments list, input bar to submit new comments (`POST /api/tube/videos/:fileId/comments`), and vote buttons on comments.

4. **Channel Detail Screen**
   - Channel header banner, circular avatar, subscriber count, bio description, and Subscribe button.
   - Tabbed section showing channel videos, shorts/reels, and channel stats.

5. **Profile / Settings Sheet**
   - Current username display, active token status indicator, and clean Sign Out option.

### Visual Identity & Theme

- **Aesthetic Direction**: Modern YouTube Dark theme — clean, content-first, high contrast.
- **Color Palette**:
  - Background: `#0F0F0F` (True dark background)
  - Surface & Card: `#181818` / `#212121`
  - Elevated Container / Borders: `#282828` / `#383838`
  - Primary Brand Accent: `#FF0000` (YouTube Red)
  - High Emphasis Text: `#FFFFFF`
  - Medium Emphasis Text: `#AAAAAA`
  - Interactive Accents: `#3EA6FF` (Links & verified highlights)
- **Edge-to-Edge**: Full support for Android system insets (`enableEdgeToEdge()`) with `WindowInsets.safeDrawing` handling.
- **App Launcher Icon**: Custom adaptive launcher icon with bold YouTube Play button styling on dark red background.

---

## 3. Key Product Decisions & Trade-Offs

- **Backend Protocol**: The live Pips Tube server requires an `Authorization: Bearer sat_...` header (or session cookie) and `ngrok-skip-browser-warning: true` header to bypass ngrok warning pages. We implement a central OkHttp interceptor that automatically attaches credentials to all API calls.
- **Video Playback**: Use Android's native `MediaPlayer` / `VideoView` backed by Jetpack Compose `AndroidView`, configured with headers for preview streaming (`/api/files/{fileId}/preview`), ensuring responsive video buffering without unnecessary bloat.
- **State Management**: Clean MVVM architecture with `VideoViewModel` and `AuthViewModel` using `StateFlow` and immutable UI states (`Loading`, `Success`, `Error`).
- **Persistence**: Store the active token and user credentials locally in `SharedPreferences` so the app launches directly into the home feed on subsequent visits without re-prompting.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                      Android UI                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ Auth Screen  │  │  Feed Screen │  │ Watch Screen │  │
│  │(Login/Signup)│  │ (Cards+Chips)│  │ (Player+Docs)│  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
└─────────┼─────────────────┼─────────────────┼──────────┘
          ▼                 ▼                 ▼
┌────────────────────────────────────────────────────────┐
│                   ViewModels Layer                     │
│    • AuthViewModel      • TubeViewModel                │
│    (StateFlow<AuthUiState>, StateFlow<FeedUiState>)    │
└───────────────────────────┬────────────────────────────┘
                            ▼
┌────────────────────────────────────────────────────────┐
│                   Repository Layer                     │
│    • AuthRepository     • TubeRepository               │
│    (Token Management, Session Storage, Cache)          │
└───────────────────────────┬────────────────────────────┘
                            ▼
┌────────────────────────────────────────────────────────┐
│                   Network / API Layer                  │
│    • Retrofit TubeApiService                           │
│    • OkHttp Interceptor (Bearer token, ngrok header)   │
│    • Endpoints:                                        │
│      - POST /api/signup, POST /api/login               │
│      - GET  /api/tube/feed                             │
│      - GET  /api/tube/channels/{user}                  │
│      - GET  /api/tube/videos/{id}/comments             │
│      - POST /api/tube/videos/{id}/comments             │
│      - POST /api/tube/videos/{id}/vote                 │
│      - POST /api/tube/channels/{user}/subscribe        │
│      - POST /api/tube/videos/{id}/view                 │
│      - Video Stream: /api/files/{fileId}/preview       │
└────────────────────────────────────────────────────────┘
```

### Core Entities & Data Mapping

- **UserSession**: `username`, `token`, `userId`
- **VideoItem**: `fileId`, `title`, `description`, `owner`, `channelName`, `channelLogoUrl`, `thumbnailUrl`, `kind` (`video`/`reel`/`post`), `views`, `likes`, `dislikes`, `myVote`, `publishedAt`, `size`, `durationMs`
- **Channel**: `username`, `name`, `description`, `logoUrl`, `subscribers`, `createdAt`
- **Comment**: `id`, `user`, `text`, `createdAt`, `likes`, `myVote`
- **VoteRequest**: `vote: 1` (like) / `-1` (dislike) / `0` (clear)
