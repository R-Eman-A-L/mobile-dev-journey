import 'package:birthday_tracker/model/friend.dart';

class FriendListUiState {
  final List<Friend> friends;
  final String? nameError;
  final String? birthdayError;

  const FriendListUiState({
    this.friends = const [],
    this.nameError,
    this.birthdayError,
  });
}
