import 'package:birthday_tracker/data/clock.dart';

class SystemClock implements Clock {
  @override
  DateTime today() {
    DateTime now = DateTime.now();
    return DateTime(now.year, now.month, now.day);
  }
}
