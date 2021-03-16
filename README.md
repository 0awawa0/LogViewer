# LogViewer

Tiny application I created to colorize log files. Application uses following regular expressions to colorize file:
  - DEBUG level entry: `^\d\d.\d\d.\d\d\d\d \d\d:\d\d:\d\d -> D/`;
  - INFO level entry: `^\d\d.\d\d.\d\d\d\d \d\d:\d\d:\d\d -> I/`;
  - WARNING level entry: `^\d\d.\d\d.\d\d\d\d \d\d:\d\d:\d\d -> W/`;
  - ERROR level entry: `^\d\d.\d\d.\d\d\d\d \d\d:\d\d:\d\d -> E/`;
  - ASSERT level entry: `^\d\d.\d\d.\d\d\d\d \d\d:\d\d:\d\d -> A/`;
