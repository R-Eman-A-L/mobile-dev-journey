import 'package:birthday_tracker/data/clock.dart';
import 'package:birthday_tracker/data/friend_repository.dart';
import 'package:birthday_tracker/ui/friend_detail/friend_detail_view_model.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class FriendDetailScreen extends StatefulWidget {
  final int friendId;
  final FriendRepository repository;
  final Clock clock;

  const FriendDetailScreen({
    super.key,
    required this.friendId,
    required this.repository,
    required this.clock,
  });

  @override
  State<FriendDetailScreen> createState() => _FriendDetailScreenState();
}

class _FriendDetailScreenState extends State<FriendDetailScreen> {
  late final FriendDetailViewModel _viewModel;

  @override
  void initState() {
    super.initState();
    _viewModel = FriendDetailViewModel(
      friendId: widget.friendId,
      repository: widget.repository,
      clock: widget.clock,
    );
  }

  @override
  void dispose() {
    _viewModel.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Friend detail screen')),
      body: ListenableBuilder(
        listenable: _viewModel,
        builder: (context, _) {
          final state = _viewModel.state;

          return (state.isNotFound)
              ? Center(child: Text('Friend not found'))
              : Center(
                child: Column(
                  mainAxisAlignment: .center,
                    children: [
                      Text(state.name ?? ''),
                      Text(state.birthday == null ? '': DateFormat("yyyy-MM-dd").format(state.birthday!)),
                      Text(
                        state.daysRemaining == 0
                            ? "🎂 Birthday today! Turns ${state.age}"
                            : "Turns ${state.age} in ${state.daysRemaining} days",
                      ),
                    ],
                  ),
              );
        },
      ),
    );
  }
}
