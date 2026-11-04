import 'package:birthday_tracker/data/friend_repository.dart';
import 'package:birthday_tracker/model/friend.dart';
import 'package:collection/collection.dart';

class InMemoryFriendRepository implements FriendRepository {
  final List<Friend> _friends = [];
  int _nextFriendId = 1;

  @override
  List<Friend> getFriends() => List.unmodifiable(_friends);

  @override
  void add(String name, DateTime birthday) =>
      _friends.add(Friend(id: _nextFriendId++, name: name, birthday: birthday));

  @override
  void delete(int id) => _friends.removeWhere((friend) => friend.id == id);

  @override
  Friend? getFriend(int id) =>
      _friends.firstWhereOrNull((friend) => friend.id == id);
}
