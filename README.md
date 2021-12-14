# Vault

#### Secure way to protect the files.

Vault is a secure, encrypted file database. It is written in Java, with combination of SQLite3. It can be used for storing personal information. Files and texts are encrypted using AES-256, and the key is hashed using SHA-512. Importing and exporting as file binary is supported, and for the safety, the encryption/decryption key is not saved in the database nor stays in the memory. The decryption key is calculated on decryption request. The database is stored in the local file system. Quick note is available, and multi-users are supported.



## Roadmap for Beta 1.0

- [x] Create user account
- [x] Create entry using text
- [x] Create entry using file
- [x] Encryption using AES-256
- [x] Password Hash
- [x] Home View
- [x] Entry detail window
- [x] Create entry window
- [x] Adaptive GUI
- [x] Realtime list update
- [ ] Cleaner / Structured GUI
- [x] Edit entry info in entry detail window
- [ ] Searching system
- [ ] Create SQLite3 DB on start if not exists
