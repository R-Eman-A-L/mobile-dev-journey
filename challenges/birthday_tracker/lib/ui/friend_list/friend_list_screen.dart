import 'package:birthday_tracker/data/clock.dart';
import 'package:birthday_tracker/data/friend_repository.dart';
import 'package:birthday_tracker/ui/friend_list/friend_list_view_model.dart';
import 'package:flutter/material.dart';

class FriendListScreen extends StatefulWidget {
  final FriendRepository repository;
  final Clock clock;
  final void Function(int id) onOpenFriend;

  const FriendListScreen({
    super.key,
    required this.repository,
    required this.clock,
    required this.onOpenFriend,
  });

  @override
  State<FriendListScreen> createState() => _FriendListScreenState();
}

class _FriendListScreenState extends State<FriendListScreen> {
  late final FriendListViewModel _viewModel;
  final _nameController = TextEditingController();
  final _birthdayController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _viewModel = FriendListViewModel(
      repository: widget.repository,
      clock: widget.clock,
    );
  }

  @override
  void dispose() {
    _nameController.dispose();
    _birthdayController.dispose();
    _viewModel.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Friend list screen')),
      body: ListenableBuilder(
        listenable: _viewModel,
        builder: (context, _) {
          final state = _viewModel.state;
          final friends = state.friends;
          return Column(
            children: [
              TextField(
                controller: _nameController,
                decoration: InputDecoration(
                  labelText: 'Friend Name',
                  errorText: state.nameError,
                ),
              ),
              TextField(
                controller: _birthdayController,
                decoration: InputDecoration(
                  labelText: 'Birthday',
                  errorText: state.birthdayError,
                ),
              ),

              FilledButton(
                onPressed: () {
                  if (_viewModel.onAddFriend(
                    _nameController.text,
                    _birthdayController.text,
                  )) {
                    _nameController.clear();
                    _birthdayController.clear();
                  }
                },
                child: Text('Add'),
              ),
              Expanded(
                child: friends.isEmpty
                    ? const Center(child: Text('No friends yet'))
                    : ListView.builder(
                        itemCount: friends.length,
                        itemBuilder: (context, index) {
                          final friend = friends[index];
                          return ListTile(
                            title: Text(friend.name),
                            onTap: () => widget.onOpenFriend(friend.id),
                            onLongPress: () =>
                                _viewModel.onDeleteFriend(friend.id),
                          );
                        },
                      ),
              ),
            ],
          );
        },
      ),
    );
  }
}
