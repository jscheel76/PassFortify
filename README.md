# PassFortify

This is a JavaFX-based Password Manager application designed to securely store and manage your passwords. The application provides features such as password encryption, master password functionality, account management, password generation, and more.

## Table of Contents

- [Introduction](#Introduction)
- [Features](#features)
- [Usage](#usage)
- [Security](#security)
- [License](#license)
- [Contact](#contact)

## Introduction
PassFortify is a simple GUI based password manager using the JavaFX framework. This is my first time working on a project of this size and the first time that I use JavaFX. The project was done in the context of an apprenticeship as a software developer. My company allowed me to conceptualise a project of my own to help me work on my skills. I came up with the idea of a password manager and began working on this project. A lot of this was new to me, which is why this project might not be perfect.

## Features

- **Secure Encryption**: AES256 used for secure encryption, all data is saved in encrypted files.
- **Account Management**: Add, delete, and view stored accounts, usernames, and passwords.
- **Password Generator**: Generate strong and unique passwords.
- **Easy Password Management**: Need to change password? Using PassFortify it's as easy as can be. Generate a new password, click the change password option in the menu and copy it whenever you need it.
- **Passphrase Generator**: Generate six-word long passphrases, using a modified DiceWare wordlist.
- **Settings**: Hide your passwords, check for duplicate passwords or enable to check for leaks on right click.
- **Password Strength Check**: Evaluate the strength of entered passwords.
- **Backup and Restore**: Create backups of your data for added security.

## Usage

Optimize your experience with the PassFortify.jar file provided in the repository. Place it in a designated folder for organized password file storage, and initiate the application using the command 'java -jar PassFortify.jar' or by creating a convenient desktop batch script.

**IMPORTANT**: Move the wordlist.txt into the same folder as the PassFortify.jar file, otherwise generating passphrases is not possible.
- **Set Master password**: On first launch you will be prompted to choose a master password.
- **Access Password Manager**: To access the password manager simply enter your chosen master password.
- **Manage your accounts**: Using the input field and buttons, add Services, Usernames and Passwords.
- **Generate passwords**: Need a secure password? Use the "generate password" button to quickly generate a safe password
- **Check password strength**: Evaluate strength of your passwords
- **Backups**: Use Menu->Files->Create Backup to create backups of your encrypted data
- **Change master password**: Pick a new masterpassword to access the password manager. All files will be encrypted using the new master password.

## Security

- **Secure Algorithm**: Your data is encrypted using AES256 with Galois Counter Mode
- **Operational Security**: The master password is always required in the password field to use any of the features.
- **Duplicate Password Warning**: Unless turned off, you will be warned when reusing the same password for multiple accounts
- **Password Strength Checker**: Evaluate the strength of your passwords
- **Hide your passwords**: Simply use the setting "hide passwords" to hide your passwords and clear your master password. No one can reveal them without your password.
- **Logout**: Want to keep the application open but still be safe? Use the logout feature to return to the login window. No one will be able to enter the application without your master password, and you won't have to restart the application.

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Contact
 You can reach me through my E-Mail: passfortify@proton.me
