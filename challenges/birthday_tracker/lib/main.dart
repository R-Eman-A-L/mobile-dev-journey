import 'package:birthday_tracker/data/clock.dart';
import 'package:birthday_tracker/data/fake_clock.dart';
import 'package:birthday_tracker/data/friend_repository.dart';
import 'package:birthday_tracker/data/in_memory_friend_repository.dart';
import 'package:birthday_tracker/data/system_clock.dart';
import 'package:birthday_tracker/ui/friend_detail/friend_detail_screen.dart';
import 'package:birthday_tracker/ui/friend_list/friend_list_screen.dart';
import 'package:flutter/material.dart';

void main() {
  final repository = InMemoryFriendRepository();
  final clock = SystemClock();
  runApp(MyApp(repository: repository, clock: clock));
}

class MyApp extends StatelessWidget {
  final FriendRepository repository;
  final Clock clock;

  const MyApp({super.key, required this.repository, required this.clock});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Birthday Tracker',
      theme: ThemeData(colorScheme: .fromSeed(seedColor: Colors.deepPurple)),
      home: Builder(
        builder: (context) {
          return FriendListScreen(
            repository: repository,
            clock: clock,
            onOpenFriend: (id) => Navigator.of(context).push(
              MaterialPageRoute(
                builder: (_) => FriendDetailScreen(
                  friendId: id,
                  repository: repository,
                  clock: clock,
                ),
              ),
            ),
          );
        }
      ),
    );
  }
}
