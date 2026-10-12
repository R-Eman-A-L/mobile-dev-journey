int? findHighestInt(List num) {
  if (num.isEmpty) return null;

  int temp = 0;
  for (int i = 0; i <= num.length; i++) {
    if (num[i] > temp) temp = num[i];
  }
  return temp;
}
