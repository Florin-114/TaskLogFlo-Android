# Conversation: Android App Build
**Date:** 2026-04-21  
**Project:** TaskLogFlo Android  

---

## Summary

Built a native Android (Kotlin + Jetpack Compose) version of the existing Task Log Flo Electron desktop app.

---

## What was built

- **Stack:** Kotlin, Jetpack Compose, ViewModel + StateFlow, Gson (JSON file storage), sh.calvin.reorderable (drag-to-reorder)
- **Source:** `d:\0. 0 Files\Desktop\TaskLogFlo-Android`
- **GitHub:** https://github.com/Florin-114/TaskLogFlo-Android
- **Reference app (Electron):** `d:\0. 0 Files\Desktop\rompack-app`

### Features
- Task list with 22 default pre-loaded tasks
- Drag-to-reorder active tasks (drag handle left of card)
- Tap card to expand: add/edit/delete/strikethrough notes per task
- "Completed" button in task header row (always visible)
- "Restore" button on completed tasks → switches back to Active tab automatically
- Active / Completed tabs
- Stats bar: Total, Open, Done, Updated Today
- Dark theme matching Electron app colours exactly
- Data persisted as JSON in app internal storage

---

## Files created

```
app/build.gradle.kts
app/src/main/AndroidManifest.xml
app/src/main/java/com/flo/tasklog/
    MainActivity.kt
    TaskViewModel.kt
    data/Models.kt
    data/TaskRepository.kt
    data/DefaultTasks.kt
    ui/theme/Color.kt
    ui/theme/Theme.kt
    ui/theme/Type.kt
    ui/MainScreen.kt
    ui/components/StatsBar.kt
    ui/components/TaskCard.kt
build.gradle.kts
settings.gradle.kts
gradle.properties
gradle/wrapper/gradle-wrapper.properties
```

---

## Fixes applied during session

| Issue | Fix |
|---|---|
| `Theme.Material.NoTitleBar` not found | Changed to `Theme.Material.Light.NoActionBar` |
| Missing `ic_launcher` mipmap | Removed icon refs from AndroidManifest |
| `collectAsStateWithLifecycle` unresolved | Added `lifecycle-runtime-compose:2.7.0` dependency |
| `horizontalAlignment` on Row | Changed to `verticalAlignment` |
| `tabIndicatorOffset` experimental error | Removed custom indicator from TabRow |
| `remember` inside `if` block | Moved `rememberLazyListState` and `rememberReorderableLazyListState` outside `if` |
| Restored tasks misaligned | Moved drag handle outside TaskCard into MainScreen Row |
| Expanded card behind other tasks | Added `zIndex(1f)` and `shadowElevation` to expanded Surface |
| "Completed" button hidden when expanded | Moved button to card header row, always visible |
| Note date not struck when note marked done | Added `TextDecoration.LineThrough` to date span |
| Note action menu resets after done | Lifted `activeNoteMenu` state to TaskCard level |
| Multiple note menus open at once | Changed map to single `Int` index — only one open at a time |
| Add-update field tap doesn't close menu | Added `onFocusChanged` to clear `activeNoteMenu` |
| Keyboard stays after tapping outside field | Added `LocalFocusManager.clearFocus()` on card header and note row taps |
| Restore adds unwanted auto-note | Removed auto-note on complete and restore |

---

## Colour palette (matches Electron app)

| Name | Hex |
|---|---|
| Background | `#0F0F13` |
| Surface | `#1A1A24` |
| Accent (red) | `#FF4D6D` |
| Gold | `#F5A623` |
| Green | `#2DD4A0` |
| Text | `#E8E8F0` |
| Border | `#2E2E42` |
