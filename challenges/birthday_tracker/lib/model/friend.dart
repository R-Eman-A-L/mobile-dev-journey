class Friend {
  final int id;
  final String name;
  final DateTime birthday;

  const Friend({required this.id, required this.name, required this.birthday});


  Friend copyWith({String? name, DateTime? birthday}) {
    return Friend(
      id: id,
      name: name ?? this.name,
      birthday: birthday ?? this.birthday,
    );
  }
}
