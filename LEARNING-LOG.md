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

## Day 23 (Week 5: Network), Nov 10, 2026

- Recap quiz: 5/5.
- HTTP: a request to a URL; a response with a status (2xx OK, 4xx asked wrong, 5xx server
  broke) and a body (JSON). dummyjson.com/quotes/random → {"id","quote","author"}.
  A missing id gave a JSON message; a typo URL gave back a web page, not JSON.
- No reply (no internet, timeout) = no status: the library throws by itself. An unreadable
  body throws by itself too. 404/500 don't throw by themselves: the repository checks for
  2xx and throws "Bad status: N". (408/504 have "timeout" in the name but are real replies.)
- Exceptions: the repository throws; the ViewModel catches (try / catch), logs the technical
  message, and shows "Couldn't load the quote" + Try again. Never show technical text.
- Kinds of errors: different message or action → a different kind (a sealed list of app
  errors); same message and same action → one kind is enough (QUOTE-1).
- Planned QuoteRepository.getQuote() → Quote or throws; QuoteForm(id, quote, author)
  → toQuote() → Quote(text, author); FakeQuoteRepository(shouldFail) never knows QuoteState.
- Learned: a slow function → ask "Does the caller need the result?" Yes → suspend
  (getQuote waits inside loadQuote's viewModelScope ticket). No → a normal function with
  scope.launch inside (save() starts its own appScope ticket and returns).

  ## Day 24 (Week 5: Network, Flutter), Nov 11, 2026

- Recap quiz: 5/5.
- http package: client.get(Uri.parse(url)).timeout(10 s); response.statusCode, response.body.
  http.Client created once in main() and handed to NetworkQuoteRepository. INTERNET
  permission added for release builds.
- QuoteForm.fromJson → toQuote(); FakeQuoteRepository(shouldFail) with a 1 s delay to see Loading.
- Airplane mode: "ClientException with SocketException: Failed host lookup" = no reply,
  no status; caught by the ViewModel → "Couldn't load the quote" + Try again, no crash.
- sealed QuoteState (QuoteLoading / QuoteError / QuoteSuccess, not Error: Dart has one);
  QuoteCard widget: data down, events up, theme colors.
- Bugs: status check must be a range (< 200 || >= 300); copyWith(error: null) can't clear
  (refresh builds a new box and names quote: \_state.quote).
- Learned: a Dart copyWith with ?? can never set null; leave nullable fields out of it.

## Day 25 (Week 5: Network, Android), Nov 12, 2026

- News: Navigation 3 v1.2 adds type-safe deep links with @Serializable keys (ADS, Nov 4).
- Recap quiz: 5/5.
- Ktor 3.6.1: HttpClient(OkHttp) { install(HttpTimeout) { requestTimeoutMillis = 10_000 } },
  created once in StreakBookApp. client.get(url); status !in 200..299 → throw;
  Json.decodeFromString<QuoteForm>(bodyAsText()). INTERNET permission is required.
- Airplane mode: UnknownHostException (same as Flutter's "Failed host lookup").
- throw = stop here, pass the problem up the chain of callers; the first matching catch
  handles it; no catch at the top = crash. Rethrow CancellationException (the manager's
  stop signal) before catch (e: Exception).
- \_uiState.update { it.copy(...) } = copy the box as it is now; refresh() copies (quote stays).
- Bug: the app crashed after "Quote failed" was logged → the same exception twice = two
  callers; leftover test code called getQuote() with no net. Found with Alt+F7.
- Learned: red in Logcat isn't a crash; FATAL EXCEPTION is. The try must be in the same
  ticket as the throwing line. Week 5 done on both platforms.

## Day 26 (Week 6: Dependency injection), Nov 14, 2026

- Did you know: Hilt is built on Dagger, which checks the dependency graph at compile time
  (missing or circular dependencies fail the build).
- Recap quiz: 5/5.
- DI = one place that knows how to make each dependency (and whether it's shared); a part
  that needs one asks, and receives it through its constructor; nobody carries what they
  don't use (no more parameter drilling).
- StreakBook's map: shared http.Client, notebook, quote repository, habit repository, clock.
  HabitListViewModel ← habit repo, clock, quote repo; HabitDetailViewModel ← habit repo, clock.
  Carriers to clean: MyApp, HabitListScreen, HabitDetailScreen (and their States).
- Special case: the habit repository needs an await first, so main() builds it and hands
  the tool a ready-made one.
- Tests swap: FakeQuoteRepository, FakeClock, FakeHabitRepository.
- Learned: draw the dependency map before installing a DI tool.

## Day 27 (Week 6: Dependency injection, Flutter), Nov 15, 2026

- News: Flutter 3.50 (Nov 11) deprecates the bundled Material/Cupertino → material_ui packages.
- Recap quiz: 5/5.
- Riverpod 3.5.0: each dependency is a Provider (a recipe; lazy and shared by default);
  ProviderScope wraps the app; screens are ConsumerStatefulWidget and use ref.read in
  initState to build their ViewModel. MyApp and the screens no longer carry dependencies.
- habitRepositoryProvider throws "must be provided in main()"; main() awaits the notebook,
  builds SavedHabitRepository, and hands it over with overrideWith.
- ref.watch = use the newest value and redo my work when it changes (recipes, build);
  ref.read = the value right now, once, for an action (initState, onPressed).
- Tests: one-line FakeQuoteRepository override → error card, no other file changed.
  Removing the habit override: the build succeeds, then a runtime ProviderException with my message.
- Learned: Riverpod checks at runtime, so a missing piece needs a clear error message.
  Ran on my physical phone when the emulator was full.

## Day 28 (Week 6: Dependency injection, Android), Nov 15, 2026

- Did you know: hiltViewModel() moved to hilt-lifecycle-viewmodel-compose (1.4.0),
  so apps don't pull in the old Navigation library.
- DI = Dependency Injection: a class receives what it needs from outside.
- Hilt map: @HiltAndroidApp (the app's supply room), @AndroidEntryPoint (a screen that asks),
  @Inject constructor (my classes), @Provides (classes I didn't write: HttpClient, DataStore,
  appScope), @Binds (interface → implementation), @Singleton (one for the whole app),
  @HiltViewModel + hiltViewModel(), assisted injection for the habitId.
- A class asked for by its interface needs @Inject constructor AND @Binds.
- @Singleton on SavedHabitRepository: MainActivity and the ViewModels get the same object.
- Learned: on Android, the habit repository needs no await to be made (load() waits later),
  so Hilt can make it from a normal recipe. The one-line swap is the @Binds line.

  ## Day 29 (Week 6: Dependency injection, Android), Nov 15, 2026

- Did you know: Hilt 2.59+ needs AGP 9 + Gradle 9.1; this project already has them.
- Setup checked against current versions: Hilt 2.60.1, KSP 2.3.14 (not tied to Kotlin
  since 2.3.0), androidx.hilt 1.4.0, Java 17. kapt doesn't work with AGP 9.
- KSP reads annotations during the build and writes code into build/generated.
- AppModule: @Provides (HttpClient, DataStore with @ApplicationContext, appScope);
  BindingsModule: @Binds (Clock, QuoteRepository, HabitRepository).
  @Inject constructor + @Singleton on my classes; @HiltViewModel; assisted injection for
  HabitDetailViewModel (habitId); MainActivity: @Inject lateinit var habitRepository.
- Test: removing bindQuoteRepository → the build fails with [Dagger/MissingBinding]
  "QuoteRepository … is injected at HabitListViewModel(…, quoteRepository)". No app at all.
- Learned: compile-time DI catches a missing recipe before the app exists; read the error
  for WHAT is missing and WHO asked; never edit generated code. Week 6 done on both platforms.

  ## Day 30 (Week 7: Testing), Nov 16, 2026

- News: Kotlin 2.5.0-RC turns on the unused return value checker (catches a copy() that's
  thrown away).
- A test = Arrange / Act / Assert. Unit test: one behavior, repeatable, fast, isolated (no
  phone, no network). Screen test: does what a user does and checks exact text on screen.
- Planned 14 unit tests + 3 screen tests for Flutter StreakBook, using FakeClock,
  FakeHabitRepository, FakeQuoteRepository; regression tests (R) for real bugs (the add
  error not clearing, refresh() resetting the quote, the 200/299 status check).
- Tests that call the real internet aren't unit tests → MockClient (package:http/testing)
  gives fixed replies with no network.
- Learned: when the Act starts something slow (the quote), the test must wait before the
  Assert; assert what the USER sees ("Couldn't load the quote"), not the technical message;
  real values everywhere, so anyone can check a line without asking me a question.

  ## Day 31 (Week 7: Testing, Flutter), Nov 17, 2026

- Did you know: flaky tests (pass/fail with no code change) make teams ignore red tests.
- Recap quiz (harder): 5/5.
- flutter test runs on the computer (no phone). test(), group(), setUp() (fresh fakes for
  each test), expect() with matchers (isTrue, isA<T>(), startsWith, throwsA, having).
- Async: FakeQuoteRepository(delay: Duration.zero) + wait ~10 ms after the Act.
  Check isA<QuoteSuccess>() BEFORE casting with as.
- MockClient (package:http/testing) gives fixed replies: a 500 test FOUND A REAL BUG:
  statusCode < 200 && statusCode >= 300 can never be true → fixed with ||.
- Broke the app on purpose (the Day 24 200/299 bug): test 13 turned red with
  "Bad status: 201", then green after the fix.
- Learned: tests find bugs manual testing can't reach; never trust a test you've never
  seen fail. 14 unit tests passing.

  ## Day 32 (Week 7: Testing, Flutter), Nov 19, 2026

- News: Android Studio channels (Canary, Beta/RC, Stable); stay on Stable while learning.
- Recap quiz (harder): 5/5.
- Widget tests: testWidgets + tester; pumpWidget draws the screen; find.text / find.byType;
  tap, longPress, enterText; findsOneWidget / findsNothing. Flutter doesn't redraw by itself
  in tests: pump() once, pumpAndSettle() until nothing moves. Time is fake in testWidgets.
- The app is drawn inside ProviderScope(overrides: [fakes]) + MyApp: DI makes screen tests
  possible with no storage, no internet, a fixed day.
- Broke the long-press on purpose: S3 failed with "Found 1 widget with text Read", which
  shows what was really on screen.
- Learned: check the starting point before the Act, so the test proves the action caused
  the change. 17 Flutter tests passing.


## Day 33 (Nov 19): Android unit tests

Did: 10 JUnit tests for HabitListViewModel (add, blank, duplicate, trim (R),
delete, error clears, Loading, Success, Error, toggle keeps the quote).
MainDispatcherRule + runTest + advanceUntilIdle. FakeHabitRepository in
src/main/data. AppLogger interface (AndroidLogger + FakeLogger, @Binds).

Problem: Removing trim() left tests green. Breaking on purpose showed onAdd
validated the trimmed name but saved the untrimmed one, and FakeHabitRepository
was trimming and hiding it. Test 9 failed with "Method e in android.util.Log
not mocked". Test 10 crashed with ClassCastException.

Fix: Trim once in onAdd, validate and add the same value, isEmpty() so the trim
is the only guard. Removed trim from the fake and SavedHabitRepository. Moved
Log.e behind AppLogger. Checked the type before the cast.

Learned: A fake must behave exactly like the real class, or the tests check a
world that doesn't exist. When a test stays green after a break, it tells me
what that test does NOT protect. Android classes are empty shells in unit
tests, so Android calls go behind an interface, like the Clock.

## Day 34 (Nov 20): Android network and detail tests

Did: NetworkQuoteRepositoryTest with Ktor's MockEngine (200 becomes a Quote,
500 throws). HabitDetailViewModelTest (unknown id shows "not found", done
yesterday and the day before = 2-day streak). 14 unit tests in src/test.

Problem: None blocking. Each test was broken once on purpose (&& in the status
check, isNotFound always false, streak starting at 1) and each turned red.

Fix: n/a

Learned: Whatever comes in through the constructor can be swapped in a test:
HttpClient(MockEngine) instead of OkHttp. To test that code throws, call it,
then fail(), and catch the exception. fail() throws an AssertionError (an Error,
not an Exception), so catch (e: Exception) doesn't hide it, but
catch (e: Throwable) would.


## Day 35 (Nov 21): Android screen tests (Week 7 done)

Did: 4 Compose screen tests in src/androidTest with createComposeRule:
checking Read updates the checkbox and "1 of 4 done today", long-press deletes
Gym, tapping Gym asks to open id 2 (the test plays the navigation), the detail
screen for id 2 shows Gym and its status. Week 7 goal reached on both platforms.

Problem: createComposeRule couldn't be imported. The screen test crashed with
NoSuchMethodException: InputManager.getInstance. assertIsOn failed on the
row. A "tap opens the detail screen" test was red every time.

Fix: Moved the test file from src/test to src/androidTest. Updated Espresso
to 3.7.0 and androidx.test.ext:junit to 1.3.0. Moved the test tag from the row
to the Checkbox. Split the navigation test into "the list asks for id 2" and
"the detail screen shows Gym"; the full-app test needs Hilt's test setup
(parked for later).

Learned: Each test folder only sees the libraries added for it. A
NoSuchMethodException on a hidden Android method means a library is too old
for this Android. A failed Compose check prints the node it found, so I can
read what I really found. A test that can never pass must be deleted, not
ignored.