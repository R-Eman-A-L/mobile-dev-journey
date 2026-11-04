class FriendDetailUiState {
  final String? name;
  final DateTime? birthday;
  final int age;
  final int daysRemaining;
  final bool isNotFound;

  const FriendDetailUiState({
    this.name,
    this.birthday,
    this.age = 0,
    this.daysRemaining = 0,
    this.isNotFound = false,
  });
}
