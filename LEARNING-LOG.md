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
Predict: 1) row tap → "Habit #2" 2) checkbox → check + "done today" goes up 3) back (arrow and phone) → list, checks unchanged 4) Android rotate on detail → stays on detail (@Serializable back stack)
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

## Day 14 — Oct 30–31

Built: Habit.completedDates (Set of dates) replaces Habit.doneToday; Clock interface (SystemClock + FakeClock) handed down like the repository; repository.toggle(id, date); HabitListUiState.doneTodayIds; HabitDetailUiState.doneToday now calculated
Broke: (planning) mixed up what the clock does; the row class was one concept too many
Fixed: the clock only answers "what's today?"; used Day 2's Set (doneTodayIds) instead of a new row class; learned the chain "work backwards from the screen"
Predict / Result: SystemClock → Gym unchecked, 0 of 4 ✅; FakeClock(Oct 30) → Gym checked ✅; check Read → detail "done today" ✅; time travel = one line in one file per platform ✅
Learned: We store dates because we store facts and calculate answers: a date is a fact, "done today" is an answer calculated from it. "Today" comes from an interface (Clock) because it reaches outside the app and tests need a fake, so a FakeClock can be swapped in with one line.

## Day 15 — Oct 31–Nov 1

Built: HabitDetailUiState.last7Days (7 yes/no, oldest first) + HabitDetailUiState.currentStreak; two private calculation functions in HabitDetailViewModel; 7 circles + streak text on the detail screen
Broke: (plan) streak loop never moved its day → infinite loop, and the morning rule skipped today; (Flutter) circles overflowed by 317 px (margin 40); last7Days checked today.day instead of day.day, so the circles never changed
Fixed: while day is in the set → count + 1, day − 1, starting at today if done, else yesterday; margin 4 (7 × 104 − 411 = 317 proved the cause); every line in the loop uses the moving day; print to split data vs screen
Predict / Result: Oct 29–31 → 3 ✅; Oct 29–30 → 2 ✅; Oct 28–29 → 0 ✅; tick Gym on Oct 31 → 3 ✅
Learned: A streak is calculated by starting at today (or yesterday if today isn't done yet) and walking backwards one day at a time while the day is marked, counting as you go. The most common loop bug is using the fixed start value instead of the variable that moves, so walk the steps like a computer before coding.

## Challenge 2: Birthday Tracker (Flutter), Nov 1–4, 2026

- Built: a two-screen birthday app. The list adds (name + YYYY-MM-DD birthday, with errors),
  deletes with a long-press, and opens the detail through a callback with the id.
  The detail shows "Turns N in X days" / "🎂 Birthday today! Turns N" / "Friend not found".
- Architecture: FriendRepository + Clock interfaces, created once in main() and handed down;
  one ViewModel and one UiState per screen; the plan (Step 0 first) is at the top of each ViewModel.
- Bugs fixed: the age used today's year (Jo 36 → 37, fixed with the next birthday date);
  difference().inDays lost a day at the March time change (18 → 19, fixed by counting calendar days);
  "yyyy-mm-dd" showed minutes instead of months; the list box started empty (refresh in the constructor).
- Learned: find the one thing two answers share before calculating them, and count days on the
  calendar, not in hours.

  ## Day 16 (Week 4: Saved data), Nov 5, 2026

- Recap quiz: 6/6.
- Seen: closing StreakBook (not rotating) loses every change, because InMemoryHabitRepository
  keeps its data in memory (the whiteboard), not in storage (the notebook).
- Planned SavedHabitRepository: on creation, read the notebook (empty → starting habits);
  after add / toggle / delete, change the fields like InMemoryHabitRepository, then write
  the habits AND nextId to the notebook. Only the composition root changes.
- Learned: everything the repository keeps must be saved, not just the list. Forgetting
  nextId gives two habits the same id after reopening.

  ## Day 17 (Week 4: Saved data), Nov 5, 2026

- Async in DartPad: a Future is a buzzer ("the answer will come later"); async = this
  function may wait; await = wait for the buzzer without freezing the worker. Without
  await you get the buzzer itself: Instance of 'Future<List<String>>'.
- Why: one worker (the main thread) draws the screen; making it wait freezes the app.
- StreakBook plan: main() becomes async, waits once for the notebook, then creates
  SavedHabitRepository with full fields. getHabits() stays instant, so the ViewModels
  and screens don't change.
- Learned: a constructor can't wait; do the slow part before it and pass in the result.
  Never hand the screens a second copy of data the repository already holds.

## Day 18 (Week 4: Saved data), Nov 5, 2026

- The notebook (shared_preferences) only takes simple values, so habits must become text.
- JSON: Habit.toJson() → a form (Map) with 'id', 'name', 'completedDates';
  jsonEncode → text. jsonDecode → form; Habit.fromJson(form) reads each label with `as`
  and builds the Habit (a factory constructor). Dates ↔ "yyyy-MM-dd" texts, Set ↔ list.
- Bug: `as List<String>` passed the shortcut test but failed after jsonDecode
  ("List<dynamic> is not a subtype of List<String>"): check the box as List, each item as String.
- Learned: test the path the real app will take; a shortcut test can pass while the real thing fails.

## Day 19 (Week 4: Saved data), Nov 7, 2026

- Recap quiz: 5/5.
- Built SavedHabitRepository (Flutter) with shared_preferences (SharedPreferencesAsync):
  pages 'habits' (JSON text) and 'nextId'. main() is async: ensureInitialized, read both
  pages, then create the repository with full fields. \_save() writes after add/toggle/delete
  without waiting. Only main() changed; no ViewModel or screen did.
- Bugs caught: a missing `return` hidden by `habitsText!` (first launch would crash);
  nextId fallback must be the biggest id + 1, not the biggest id.
- Tests: fresh install, "Read" survives, checks survive, Walk/Read ids don't clash. All pass.
- Learned: a `!` that makes an error go away is a promise, so check it's true; reading is
  the saving steps in reverse.

  ## Day 20 (Week 4: Saved data, Android), Nov 7–8, 2026

- Recap quiz: 5/5.
- Kotlin's buzzer: suspend (may wait), launch (start a task on the side), scope (the owner
  of the tasks). A suspend function can't be called from a normal function: red error
  "can only be called from a coroutine or another suspend function".
- Playground stopwatch: line 2 at 7 ms, the launched line at 2021 ms → launch doesn't block.
  Two launches with 2 s each both finish near 2000 ms: they run side by side, and the
  finishing order isn't guaranteed.
- A scope owns many tasks (one per launch). When it closes, its unfinished tasks are
  cancelled: viewModelScope → the user really leaves the screen (not rotation);
  lifecycleScope → the Activity is destroyed (rotation too); appScope → the process ends.
  Swiping the app away stops everything.
- Which scope? "If the user leaves this screen right now, must this task still finish?"
  Yes → appScope (saving). No → where the code lives: ViewModel → viewModelScope,
  composable → rememberCoroutineScope, Activity → lifecycleScope.
- appScope isn't built in: created once in StreakBookApp (CoroutineScope(SupervisorJob())),
  one for the whole app, shared with every repository that needs it.
- StreakBookApp = the one Application class (manifest), created by Android before any
  screen: it creates the shared things. MainActivity shows the app. Flutter's main() does both.
- Android plan: MainActivity → lifecycleScope.launch { load(); setContent {...} }
  (read THEN show). save() → appScope.launch { write }. Reading and writing are both slow.
  - Recompose (redraw parts, Activity kept) ≠ destroy + recreate (configuration change:
    rotation, dark mode, language, size → new Activity, old lifecycleScope cancelled,
    ViewModels survive) ≠ destroy for good (back, swipe away). So load() needs a guard:
    already loaded → stop.
- Learned: slow work goes on a separate task so the app isn't blocked; if I need the
  result, the next steps go inside the same task after the wait. The wait goes where the
  "show" is.


## Day 21 (Week 4: Saved data, Android), Nov 8, 2026
- DataStore: one notebook (val Context.habitStore by preferencesDataStore), pages HABITS and
  NEXT_ID. Read once with data.first(); write both pages in ONE edit { } (no half-written state).
- StreakBookApp creates the repository in onCreate, because the context (this) is only ready
  there. MainActivity: lifecycleScope.launch { load(); setContent {...} }.
- JSON: @Serializable HabitForm; Habit.toForm() / HabitForm.toHabit();
  Json.encodeToString / decodeFromString<List<HabitForm>>. No 'as' needed.
- Traps: return in one branch would skip loaded = true (use if / else); pages[HABITS] isn't
  smart-cast (copy it into a local val); compute the text before launch.
- Tests: fresh install, survives closing, checks survive, ids don't clash, tap + rotate. All pass.
- Learned: early return only when nothing after it is needed. Week 4 done on both platforms.


## Day 22 (Week 5: Network), Nov 9, 2026
- Recap quiz: 6/6.
- Three situations, one box: Way A (isLoading + error + quote) allows 8 boxes, only 3 make
  sense; Way B, a sealed type (QuoteState: Loading / Error(message) / Success(quote)),
  allows only the 3, and `when` must handle all of them.
- Sealed = a TYPE we create (like String): a closed list of situations, always exactly one,
  each with its own data. Needs a parameter → data class; no parameter → data object.
- Naming: UiState = a screen's whole box only, so the card's type is QuoteState, one field
  in HabitListUiState.
- Planned QUOTE-1: HabitListViewModel.loadQuote() on viewModelScope; Loading first, then
  ask; copy the box as it is when the answer arrives.
- Learned: refresh() must COPY the box now: building a new box would reset quote to Loading.