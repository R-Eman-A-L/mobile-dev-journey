import 'package:birthday_tracker/model/friend.dart';

abstract class FriendRepository {
  List<Friend> getFriends();
  void add(String name, DateTime birthday);
  void delete(int id);
  Friend? getFriend(int id);
}
