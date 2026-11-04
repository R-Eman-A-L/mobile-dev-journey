import 'package:birthday_tracker/data/clock.dart';
import 'package:birthday_tracker/data/friend_repository.dart';
import 'package:birthday_tracker/model/friend.dart';
import 'package:birthday_tracker/ui/friend_detail/friend_detail_ui_state.dart';
import 'package:flutter/foundation.dart';

/*

  ===== 0. WORK BACKWARDS FROM THE SCREEN (once per thing the screen shows) =====

  Thing: the friend's name
  1. What does it show, and what does it use?
     Shows a text: the name of the friend the user tapped on the list screen.
     Uses the friend id the tap sent (not shown), to find the right friend.
  2. Which box does the screen read it from? FriendDetailUiState
  3. Is there already a field in that box that holds it? No → keep going.
  4. What stored information is it made from?
     Friend.name, from the Friend that repository.getFriend(id) gives back.
  5. Is it the stored fact itself, or an answer calculated from facts?
     A stored FACT: Friend.name is copied as is. Finding the friend with getFriend(id)
     is how we get it, not a calculation.
  6. Which ViewModel prepares it, and in which function? FriendDetailViewModel.load()
  7. The new field: FriendDetailUiState.name : String? = null

  Thing: the friend's birthday
  1. What does it show, and what does it use?
     Shows a date: the friend's birthday (e.g. 1995-11-15).
  2. Which box does the screen read it from? FriendDetailUiState
  3. Is there already a field in that box that holds it? No → keep going.
  4. What stored information is it made from?
     Friend.birthday, from the Friend that repository.getFriend(id) gives back.
  5. Is it the stored fact itself, or an answer calculated from facts?
     A stored FACT: Friend.birthday is copied as is. The screen only turns it into text.
  6. Which ViewModel prepares it, and in which function? FriendDetailViewModel.load()
  7. The new field: FriendDetailUiState.birthday : DateTime? = null

  Thing: the age (the N in "Turns N")
  1. What does it show, and what does it use?
     Shows a whole number: the age the friend will turn at their next birthday.
  2. Which box does the screen read it from? FriendDetailUiState
  3. Is there already a field in that box that holds it? No → keep going.
  4. What stored information is it made from?
     Friend.birthday (from getFriend(id)) and Clock.today().
  5. Is it the stored fact itself, or an answer calculated from facts?
     An ANSWER: first find the next birthday date from Friend.birthday and Clock.today(),
     then age = next birthday's year − Friend.birthday's year.
  6. Which ViewModel prepares it, and in which function? FriendDetailViewModel.load()
     (with the helper nextBirthday)
  7. The new field: FriendDetailUiState.age : int = 0

  Thing: the days left (the X in "in X days")
  1. What does it show, and what does it use?
     Shows a whole number: how many days until the friend's next birthday.
  2. Which box does the screen read it from? FriendDetailUiState
  3. Is there already a field in that box that holds it? No → keep going.
  4. What stored information is it made from?
     Friend.birthday (from getFriend(id)) and Clock.today().
  5. Is it the stored fact itself, or an answer calculated from facts?
     An ANSWER: from the SAME next birthday date as the age,
     daysRemaining = the number of days from Clock.today() to the next birthday.
  6. Which ViewModel prepares it, and in which function? FriendDetailViewModel.load()
     (with the helper nextBirthday)
  7. The new field: FriendDetailUiState.daysRemaining : int = 0

  Thing: "🎂 Birthday today!"
  1. What does it show, and what does it use?
     Shows a fixed text, but only when the birthday is today (a yes/no decides it).
  2. Which box does the screen read it from? FriendDetailUiState
  3. Is there already a field in that box that holds it?
     YES → FriendDetailUiState.daysRemaining == 0 means the birthday is today. Done, no new field.

  Thing: "Friend not found"
  1. What does it show, and what does it use?
     Shows a yes/no: whether to show the message. The text "Friend not found" never
     changes, so it stays in the screen.
  2. Which box does the screen read it from? FriendDetailUiState
  3. Is there already a field in that box that holds it? No → keep going.
  4. What stored information is it made from?
     Whether repository.getFriend(id) gives back a Friend or nothing.
  5. Is it the stored fact itself, or an answer calculated from facts?
     An ANSWER: true when getFriend(id) gives back nothing, otherwise false.
  6. Which ViewModel prepares it, and in which function? FriendDetailViewModel.load()
  7. The new field: FriendDetailUiState.isNotFound : bool = false


  ===== 2. UNKNOWNS =====
    - how to count the days between two dates in Flutter (the time-change trap from Day 15)

  ===== 3. INTERFACES =====
  No new interface: FriendRepository and Clock already exist (reasons in Part 1).

  ===== 3b. REPOSITORY JOB DESCRIPTION (new duty) =====
    - getFriend(id) → Friend, or nothing if the id doesn't exist
      (receives the id the tap sent, not typed text)

  ===== 4. UISTATE: FriendDetailUiState =====
    - name          | String?   | null  | set by load()
    - birthday      | DateTime? | null  | set by load()
    - age           | int       | 0     | set by load()
    - daysRemaining | int       | 0     | set by load()
    - isNotFound    | bool      | false | set by load()

  ===== 7. STEPS =====
  The ViewModel: FriendDetailViewModel(friendId, repository, clock)
    - friendId:   which friend to show (the tap sent it)
    - repository: to get that one friend
    - clock:      to know today, so it can find the next birthday
 
  nextBirthday(birthday, today) → a date   (helper: gives back one date, touches nothing else)
  Goal: returns the date of the friend's next birthday; a birthday today counts as "not passed".
  Have: birthday (Friend.birthday), today (Clock.today())
    1. thisYear = a date with today's year and the birthday's month and day
    2. if thisYear is before today: return the same month and day, with today's year + 1
    3. otherwise: return thisYear
  Walk-through (today = 2026-11-01):
    Alex → 2026-11-15 is not before today → 2026-11-15
    Sam  → 2026-11-01 is not before today → 2026-11-01 (today)
    Jo   → 2026-03-10 is before today     → 2027-03-10
 
  load()   (called once, when the ViewModel is created)
  Goal: the box holds the friend's name, birthday, age and days left, and the user sees them;
        or, if the id doesn't exist, isNotFound is true and the user sees only "Friend not found".
  Have: friendId, repository, clock
    1. friend = repository.getFriend(friendId)
    2. if friend is nothing: build a new box with isNotFound = true, ring the bell, STOP
    3. today = clock.today()
    4. next = nextBirthday(friend.birthday, today)
    5. calculate age = next's year − friend.birthday's year
    6. calculate daysRemaining = days from today to next
    7. build a new box: name, birthday, age, daysRemaining, isNotFound = false
    8. ring the bell

  ===== 8. THE SCREEN =====
  Reads: name at the top, then birthday, then the birthday line.
    - daysRemaining == 0 → "🎂 Birthday today! Turns {age}"
    - otherwise          → "Turns {age} in {daysRemaining} days"
    - isNotFound true    → only "Friend not found" (no name, no birthday, no line)
  Calls: nothing (the back arrow is handled by Flutter)

  ===== COMPOSITION ROOT =====
  main() creates ONCE: InMemoryFriendRepository() and SystemClock()
    (the one line to change for testing: FakeClock(DateTime(2026, 11, 1)))
  main → MyApp(repository, clock)
    → FriendListScreen → its State → FriendListViewModel(repository, clock)
    → tap → onOpenFriend(id) → the top level opens FriendDetailScreen(friendId, repository, clock)
      → its State → FriendDetailViewModel(friendId, repository, clock)

  ===== 9. EDGE CASES =====
    - birthday is today                → next birthday is today → 0 days → "🎂 Birthday today! Turns N"
    - birthday already passed this year → next year's date → age is one more (Jo)
    - id doesn't exist                 → "Friend not found" only
    - February 29                      → out of scope (ticket)

  ===== 10. TEST PLAN (FakeClock 2026-11-01) =====
    - Alex 1995-11-15 → "Turns 31 in 14 days"
    - Sam 2000-11-01  → "🎂 Birthday today! Turns 26"
    - Jo 1990-03-10   → "Turns 37 in 129 days"
    - tap Alex        → 1995-11-15 shown on the detail screen
    - open the detail with id 99 (one line changed for the test) → "Friend not found" only
    - back arrow      → returns to the list

 */

class FriendDetailViewModel extends ChangeNotifier {
  final int friendId;
  final FriendRepository _repository;
  final Clock _clock;

  FriendDetailViewModel({
    required this.friendId,
    required this._repository,
    required this._clock,
  }) {
    _load();
  }

  FriendDetailUiState _state = FriendDetailUiState();
  FriendDetailUiState get state => _state;

  void _load() {
    final Friend? friend = _repository.getFriend(friendId);
    if (friend == null) {
      _state = FriendDetailUiState(isNotFound: true);
      notifyListeners();
      return;
    }

    final today = _clock.today();
    final nextBirthday = _nextBirthday(friend.birthday, today);
    final age = nextBirthday.year - friend.birthday.year;
    final daysRemaining = _daysRemaining(today, nextBirthday);

    _state = FriendDetailUiState(
      name: friend.name,
      birthday: friend.birthday,
      age: age,
      daysRemaining: daysRemaining,
    );

    notifyListeners();
  }

  int _daysRemaining(DateTime date, DateTime nextBirthday) {
    DateTime day = date;
    int count = 0;

    while (day.isBefore(nextBirthday)) {
      day = DateTime(day.year, day.month, day.day + 1);
      count++;
    }
    return count;
  }

  DateTime _nextBirthday(DateTime birthday, DateTime today) {
    DateTime thisYear = DateTime(today.year, birthday.month, birthday.day);

    if (thisYear.isBefore(today)) {
      return DateTime(today.year + 1, thisYear.month, thisYear.day);
    }

    return thisYear;
  }
}
