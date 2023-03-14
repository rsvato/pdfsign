PoC of external PDF signature with iText 5
====

This is a Spring Bootstrap application. You need Java 17 and Maven to build/run it.
It requires some plugin installed in your browser.
Tailor application-development.yaml at your needs, or use
normal profile and `-D` switches to set:
- PDF original directory
- Temp directory
- PDF output directory
- Temp files (PDFs with deferred signature) lifetime (5 minutes by default)

Note that multiple signing process isn't possible at the moment.
