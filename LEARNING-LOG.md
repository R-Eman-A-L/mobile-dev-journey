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

|                          | Rotation | Background kill | Swipe away |
| ------------------------ | -------- | --------------- | ---------- |
| Flutter (State field)    | ✅       | ❌ (expected)   | ❌         |
| Compose remember         | ❌       | ❌              | ❌         |
| Compose rememberSaveable | ✅       | ✅ (tested)     | ❌         |

## Day 6 — Oct 18 (week 2 starts early)

Built: Habit model (id, name, doneToday) on both platforms; removed the done Set
Broke: rememberSaveable seemed to work with List<Habit> on rotation (in-memory) but can't survive a background kill, and nextId reset separately → duplicate ids
Fixed: back to remember for habits + nextId; ViewModel tomorrow keeps related state together
Pattern: immutable update = find (by id) → copy (copyWith/copy) → swap
Proof: check Gym → delete → re-add → unchecked, with zero cleanup code

## Day 7 — Oct 19–20

Built: HabitListViewModel on both platforms; screens only display + forward taps (ids, not objects)
Broke: Flutter VM read from the public getter (copies the list); Compose keyed rows by index
Fixed: work on \_habits inside the VM; key = { it.id }; guard clause in addHabit; trim inside validate
Method: Goal → Have → Steps (comments) → Lines
Rotation (Android, ViewModel): ✅
Background kill (Android, ViewModel):

## Day 8 — Oct 20

Built: HabitRepository (abstract class / interface) + InMemoryHabitRepository on both platforms; ViewModel receives the repository through its constructor
Broke: My Android screen did not update after add, toggle or delete
Fixed: map / filter / + return a NEW list and leave the old one unchanged; I had to store the result back (habits = habits.map { ... })
Predict (Fake repository): only Test A and Test B on screen; only the one line in main changes
Result: matched ✅
Learned: A repository is the single place that stores and changes the data; the ViewModel asks it for data and asks it to make changes, but never edits the data itself. We use an abstract class (Dart) or interface (Kotlin) so the ViewModel only knows the job description, not the worker, which means swapping to another repository (fake, database) changes one line instead of the whole codebase.
Interface test: verbs that reach outside (save, load, fetch, send, play) → interface; verbs that stay inside (calculate, format, represent) → plain class
Interface rule: Reach out, swap out, or fake out → interface. None → keep it plain.

## Day 9 — Oct 21

Built: HabitListUiState (one box per screen); ViewModel events onToggle / onDelete / onAdd / onTextChanged; "X of Y done today" line
Broke: the screen wrote into the box (state.error = null) and did the brain's job in the Add button; Flutter rang the bell twice
Fixed: the screen only reads the box and reports events; UiState fields are val/final; refresh() owns building the box and the bell
Predict: 1) tap → count goes up 2) typing clears the error 3) Android rotation keeps the error
Result: all matched ✅
Learned: The UiState is a snapshot of everything on the screen that can change, built by the ViewModel. The screen pushes events up, and the ViewModel pushes a new state down after the changes are made.

## Day 9 — Oct 21

|                          | Rotation | Background kill | Swipe away |
| ------------------------ | -------- | --------------- | ---------- |
| Android: habits + checks | ✅       | ✅              | ❌         |
| Android: half-typed text | ✅       | ❌              | ❌         |
| Flutter: habits + checks | ✅       | ✅              | ❌         |

|                          | Rotation | Background kill                                                                    | Swipe away |
| ------------------------ | -------- | ---------------------------------------------------------------------------------- | ---------- |
| Android: habits + checks | ✅       | ❌ viewMOdel dies with the app                                                     | ❌         |
| Android: half-typed text | ✅       | ✅ rememberSaveable writes to Android’s saved bundle, which lives outside the app. | ❌         |
| Flutter: habits + checks | ✅       | N/A                                                                                | ❌         |

| Job                                     | Flutter               | Android             |
| --------------------------------------- | --------------------- | ------------------- |
| The brain's base class                  | the viewModel class   | the viewModel class |
| Ring the bell (tell the screen)         | notifyListeners       | ?                   |
| The screen listens with                 | ?                     | ?                   |
| Private writable, public read-only      | ?                     | ?                   |
| The box (UiState) is declared as        | class                 | data class          |
| The job description (interface) keyword | abstract              | interface           |
| "Signs" the job description             | override              | override            |
| Copy an object with one change          | object.copyWith()     | object.copy()       |
| Code that runs once at creation         | init()                | init()              |
| Count the habits that are done          | habits.where().length | habits.count()      |

**answers:**
| Job | Flutter | Android |
|---|---|---|
| The brain's base class | `ChangeNotifier` | `ViewModel` |
| Ring the bell (tell the screen) | `notifyListeners()` | give the `MutableStateFlow` a new value (`_state.value = ...`) |
| The screen listens with | `ListenableBuilder` | `collectAsStateWithLifecycle()` |
| Private writable, public read-only | `_state` + a getter | `_state` + `asStateFlow()` |
| The box (UiState) is declared as | `class` with `final` fields | `data class` with `val` fields |
| The job description (interface) keyword | `abstract class` | `interface` |
| "Signs" the job description | `implements` (+ `@override` on each function) | `:` (+ `override` on each function) |
| Copy an object with one change | `copyWith()` (written by hand) | `copy()` (free with `data class`) |
| Code that runs once at creation | the constructor body `{ }` | `init { }` |
| Count the habits that are done | `habits.where((h) => h.doneToday).length` | `habits.count { it.doneToday }` |

**Biggest difference:** On rotation, Flutter keeps the screen's state automatically, while Android rebuilds the screen, so Android needs a ViewModel (or `rememberSaveable`) to keep data. On a background kill, Android offers a saved bundle (`rememberSaveable`), and Flutter has nothing by default. Swipe-away loses everything on both.

## Challenge 1 — Tip Splitter (Oct 22–27)

Built: TipSplit (Android) from a ticket: UiState with inputs + results, 4 events, refresh with guard clauses, stepper, segmented buttons, number keyboard
Broke: + didn't refresh; valid exit rewrote the user's text; validator secretly wrote the box; tip choice kept in the screen (lost on rotation); silenced Locale warning (French phone → $12,68)
Fixed: refresh after every event; copy the box in every exit; all box-writing in refresh; selected = state.tipPercent == tip; Locale.US in one formatAmount helper
Interface: no — nothing saved or from outside, no second way, math testable directly
Test plan: 7/7 ✅
Learned: Planning a ticket means deciding what goes in the box (inputs and results), which events the user triggers, and whose job each step is, before writing any code. I still need practice: my first plans mixed up layers and forgot to store the inputs.

## Day 11 — Oct 27
Built: folders (model / data / ui); navigation to a detail screen (Navigator on Flutter, Navigation 3 on Android); checkbox on the row; row tap opens the detail through a callback (onOpenHabit)
Broke: Android detail screen didn't show "Habit #2" — the Text was hidden under the TopAppBar
Fixed: used the Scaffold's innerPadding (Box with fillMaxSize().padding(innerPadding), centred)
Predict: 1) row tap → "Habit #2"  2) checkbox → check + "done today" goes up  3) back (arrow and phone) → list, checks unchanged  4) Android rotate on detail → stays on detail (@Serializable back stack)
Result: all matched ✅
Learned: The back stack is a pile of open screens: opening a screen puts it on top, back removes it, and you only see the top one. The screen doesn't open anything itself; it calls a callback ("phone number"), and the top level of the app (MainActivity / main.dart) answers and changes the back stack.

## Day 12 — Oct 29
Built: getHabit(id): Habit? in the repository (+ every class that signed it); HabitDetailUiState (habitName, doneToday, isNotFound); HabitDetailViewModel with the id; detail screen reacts; Android: viewModel { } recipe + one brain per back-stack sheet
Broke: return not allowed in a Kotlin init block; Flutter widget.habitId undefined (field was not on the widget class)
Fixed: init { load() } with the guard clause inside load(); habitId moved onto the StatefulWidget class
Predict / Result: Gym → name + status ✅; Gym → back → Read → Read ✅; Run → not found ✅; kill → not found ✅; check Gym → detail NOT done ✅ (two pantries)
Learned: Screens that show the same data must share one data source: each `InMemoryHabitRepository()` is a separate pantry, so a change on one screen never reaches the other. The ViewModel decides, the screen reacts.

## Day 13 — Oct 29
Built: one shared repository: created once at the top (Flutter main(), Android StreakBookApp : Application) and handed down to every ViewModel (manual dependency injection)
Broke: lint warning ViewModelConstructorInComposable in the Android preview
Fixed: removed the ViewModel default values (compiler listed every spot); suppressed the warning only on the @Preview, with a comment explaining why
Predict / Result: check Gym → detail done ✅; Run found ✅; uncheck → detail not done ✅; rotate → still done ✅ (repository lives in the Application); kill → data lost ✅ (whole app dies, data only in memory → week 4)
Learned: Something several screens share is created once at the top of the app (Flutter main(), Android Application class) and handed down to each ViewModel. Those places are created only once and outlive rotation, so every screen gets the same one.