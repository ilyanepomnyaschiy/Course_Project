# Course Project: File parsing

## Description

The project reads input files containing money transfers, parses them, and outputs the results to files and to the console.


## Features

The program can perform the following actions:

- **Data reading**: the program loads information about balances and transfers, and correctly handles the absence of
  data input. `NoInputFilesException` is thrown when input files are missing.
- **Processing and validation**: the system checks the correctness of the data (for example, sufficient funds, data
  format).
- **Report generation**: the program can generate reports on completed and failed transfers.
- **Logging and output**: All actions are accompanied by console output.

## Project Structure

```
courseProject/
├── resources/                            # Runtime data (input & output)
│   ├── archive/                          # Processing results (output)
│   │   ├── allTransfer.txt               # All transfers
│   │   ├── completedTransfer.txt         # Successfully completed transfers
│   │   └── failedTransfer.txt            # Rejected / failed transfers
│   └── input/                            # Source data (input)
│       ├── accounts/
│       │   └── balance.txt               # Initial customer balances
│       └── transfers/
│           ├── input1.txt
│           ├── input2.txt
│           └── input3.txt
├── src/
│   ├── exception/                        # Custom exception classes
│   │   ├── AppException.java             # Base application exception
│   │   └── NoInputFilesException.java    # Raised when no input files are found
│   ├── io/                               # File I/O and transfer processing
│   │   ├── FileBalanceReader.java        # Reads customer balances
│   │   ├── FileFinder.java               # Scans directory for .txt files
│   │   ├── FileTransferReader.java       # Reads all transfer files
│   │   └── TransferProcessor.java        # Validates, writes, updates balances
│   ├── service/                          # Parsing and console output
│   │   ├── TransferPrint.java            # Prints transfers to the console
│   │   └── TransferWriter.java           # Writes processing results
│   ├── util/                             # Auxiliary classes
│   │   └── Constants.java                # Application-wide constants
│   ├── ConsoleApp.java                   # Console menu and user interaction
│   └── Main.java                         # Entry point (public static void main)
├── .editorconfig                         # Editor configuration
├── .gitignore                            # Git ignore rules
├── Diagramm_class.png                    # UML class diagram
└── Readme.md                             # Project documentation
```

## How to Run

1. Open the project in the IntelliJ IDEA development environment.
2. Wait for the indexing and dependency loading to complete.
3. Find the class containing the `public static void main(String[] args)`.
4. Right‑click this class and select **Run 'Main.main()'** (or press `Shift + F10`).
5. The main menu will open:
   **0 — exit**
   **1 — parsing transfer files from the input folder**
   **2 — display a list of all transfers from the report file**
6. When you select command 0 in any menu, the program exits.
   When you select command 1 from the main menu, the program reads the balance file and all transfer files from the
   input folder, parses them, and writes the results to `src/resources/archive/allTransfer.txt`. After parsing, a
   submenu opens with the following options:
   **0 — exit**
   **2 — display a list of all transfers from the report file**
   When you select command 2 from the main menu or from the post-parsing submenu, the transfers stored in
   `src/resources/archive/allTransfer.txt` are printed to the console. Once the list has been printed, the program
   asks: "Generate files with completed and failed transfers (y/n)?"
7. If you answer "y" to that question (case-insensitive), the program creates two separate files: src/resources/archive/completedTransfer.txt for completed transfers and src/resources/archive/failedTransfer.txt for failed ones. If you answer "n", the program exits.
8. After you answer "y" in step 7 and the files are created, the following menu opens:
   **0 — exit**
   **1 — display completed transfers**
   **2 — display failed transfers**
   Selecting 1 prints the completed transfers; selecting 2 prints the failed transfers. Step 8 is the last step of the scenario: once the selected list has been printed, the program exits.


## Requirements

To run the project, the following are required:

- Java Development Kit (JDK): 17 or higher (tested on JDK 21)
- Development environment: IntelliJ IDEA (Community or Ultimate).
- Operating system: Windows, macOS, or Linux.
- Build: intended to run directly from IntelliJ IDEA; Main and ConsoleApp are in the default package, and the project is
  not a Maven/Gradle build.

## Author

- **Ilya Nepomnyashchy** — *project developer*
- Contact information: ilyanep@mail.ru or https://github.com/ilyanepomnyaschiy/TMS_HomeWork/
