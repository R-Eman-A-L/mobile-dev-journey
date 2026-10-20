# Learning Log

## Day 0 — Oct 12

Built: Flutter + Android Studio setup; largest-number function in Dart and Kotlin
Broke: starting the max at 0 returned 0 for all-negative lists
Fixed: start from the first element and loop from index 1; return null for an empty list

## Day 1 — Oct 13

Built: habit list screen in Flutter (ListView.builder) and Compose (LazyColumn)
Broke: Studio auto-imported the wrong ListItem (android.graphics.pdf); hit errors because Compose syntax differs from Flutter
Fixed: removed the wrong import, kept androidx.compose.material3.ListItem
To do: research how LazyColumn and ListItem are built in Compose

## Day 2 — Oct 15
Built: tap-to-toggle check mark (Flutter setState + Set; Compose remember + mutableStateOf)
Broke: Icons unresolved in Compose; removing setState / remember stopped the toggle
Fixed: added material-icons-core dependency. Flutter mutates the Set and calls setState;
       Compose assigns a new Set (done += name), and the assignment triggers the redraw

## Day 3 — Oct 15 (catch-up)
Built: add-habit form with validation (Flutter Form + validator; Compose OutlinedTextField + error state)
Broke: spaces and "Gym " slipped through — I validated the raw text but added the trimmed text
Fixed: trim once first, then validate and add that same value
Noticed: rotation keeps habits in Flutter, resets everything in Compose


## Day 4 — Oct 17 (catch-up)
Built: long-press delete + "No habits yet" empty state (Flutter onLongPress; Compose combinedClickable)
Broke: deleting only from the list left a ghost check — re-adding "Gym" came back checked
Fixed: delete removes from habits AND done (two sources of truth → single source in week 2)
       Compose: if without else drew both views; switched to if/else


## Day 5 — Oct 17 (catch-up)
Built: rememberSaveable for habits + done in Compose; survival table for rotation / background kill / swipe away
Broke: PowerShell rejected %LOCALAPPDATA% (CMD syntax); Flutter debug build froze on splash after process kill
Fixed: used $env:LOCALAPPDATA + Set-Alias for adb; verified the Compose kill with pidof
Biggest difference this week: Flutter mutates and calls setState; Compose assigns new values that it watches
Survives nothing on either platform: swipe-away → needs real storage (week 4)

| | Rotation | Background kill | Swipe away |
|---|---|---|---|
| Flutter (State field) | ✅ | ❌ (expected) | ❌ |
| Compose remember | ❌ | ❌ | ❌ |
| Compose rememberSaveable | ✅ | ✅ (tested) | ❌ |


## Day 6 — Oct 18 (week 2 starts early)
Built: Habit model (id, name, doneToday) on both platforms; removed the done Set
Broke: rememberSaveable seemed to work with List<Habit> on rotation (in-memory) but can't survive a background kill, and nextId reset separately → duplicate ids
Fixed: back to remember for habits + nextId; ViewModel tomorrow keeps related state together
Pattern: immutable update = find (by id) → copy (copyWith/copy) → swap
Proof: check Gym → delete → re-add → unchecked, with zero cleanup code

## Day 7 — Oct 19–20
Built: HabitListViewModel on both platforms; screens only display + forward taps (ids, not objects)
Broke: Flutter VM read from the public getter (copies the list); Compose keyed rows by index
Fixed: work on _habits inside the VM; key = { it.id }; guard clause in addHabit; trim inside validate
Method: Goal → Have → Steps (comments) → Lines
Rotation (Android, ViewModel): ✅
Background kill (Android, ViewModel):