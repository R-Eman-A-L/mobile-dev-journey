import 'package:birthday_tracker/data/clock.dart';
import 'package:birthday_tracker/data/friend_repository.dart';
import 'package:birthday_tracker/model/friend.dart';
import 'package:birthday_tracker/ui/friend_list/friend_list_ui_state.dart';
import 'package:flutter/foundation.dart';
import 'package:intl/intl.dart';

class FriendListViewModel extends ChangeNotifier {
  final FriendRepository _repository;
  final Clock _clock;

  FriendListViewModel({required this._repository, required this._clock}) {
    _refresh();
  }

  FriendListUiState _state = const FriendListUiState();
  FriendListUiState get state => _state;

  void _refresh([String? nameError, String? birthdayError]) {
    final List<Friend> friends = _repository.getFriends();
    _state = FriendListUiState(
      friends: friends,
      nameError: nameError,
      birthdayError: birthdayError,
    );

    notifyListeners();
  }

  bool onAddFriend(String nameText, String birthdayText) {
    String? nameError;
    String? birthdayError;
    final String name = nameText.trim();
    final DateTime? birthday = DateFormat(
      "yyyy-MM-dd",
    ).tryParseStrict(birthdayText);

    if (name.isEmpty) nameError = "Enter a name";
    if (birthday == null) {
      birthdayError = "Use the format YYYY-MM-DD";
    } else if (birthday.isAfter(_clock.today())) {
      birthdayError = "A birthday can't be in the future";
    }

    if (nameError != null || birthdayError != null) {
      _refresh(nameError, birthdayError);
      return false;
    }

    _repository.add(name, birthday!);
    _refresh();
    return true;
  }

  void onDeleteFriend(int id) {
    _repository.delete(id);
    _refresh();
  }
}
