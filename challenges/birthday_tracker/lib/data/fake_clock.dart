import 'package:birthday_tracker/data/clock.dart';

class FakeClock implements Clock {
  final DateTime date;

  const FakeClock({required this.date});

  @override
  DateTime today() {
    return date;
  }
}
