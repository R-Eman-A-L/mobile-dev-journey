# Learning Log

## Day 0 — Oct 12

Built: Flutter + Android Studio setup; largest-number function in Dart and Kotlin
Broke: starting the max at 0 returned 0 for all-negative lists
Fixed: start from the first element and loop from index 1; return null for an empty list

## Day 1 — Oct 13

Built: habit list screen in Flutter (ListView.builder) and Compose (LazyColumn)
Broke: Studio auto-imported the wrong ListItem (android.graphics.pdf); hit errors because Compose syntax differs from Flutter
Fixed: removed the wrong import, kept androidx.compose.material3.ListItem
To do: research how LazyColumn and ListItem are built in Compose

## Day 2 — Oct 15
Built: tap-to-toggle check mark (Flutter setState + Set; Compose remember + mutableStateOf)
Broke: Icons unresolved in Compose; removing setState / remember stopped the toggle
Fixed: added material-icons-core dependency. Flutter mutates the Set and calls setState;
       Compose assigns a new Set (done += name), and the assignment triggers the redraw