# Java Random Access File Stream Manager

A desktop application built in Java featuring dual Swing GUIs for generating, persisting, and searching fixed-width binary records using RandomAccessFile streams.

## 📌 Overview

This project demonstrates binary file stream operations and low-level data persistence in Java. By enforcing fixed-length character padding across string fields, the system achieves predictable byte-length records (240 bytes per record), enabling efficient non-sequential file positioning, record creation, input validation, and partial-text search querying.

---

## ✨ Key Features

* **Fixed-Width Binary Sizing:** Pads string attributes with trailing spaces to ensure constant 240-byte record lengths for direct byte-level offset positioning.
* **Record Entry GUI (RandProductMaker):** Form-driven interface with input validation, real-time record counting, and appended binary file storage[cite: 50].
* **Search GUI (RandProductSearch):** Real-time search tool that scans binary records and returns matching products based on partial string queries[cite: 54].
* **Robust Validation:** Safeguards against empty inputs and invalid data types before performing disk writes[cite: 50].

---

## 🛠️ Data Model Sizing Architecture

| Field | Data Type | Character Limit | Encoding Sizing | Byte Size |
| :--- | :--- | :--- | :--- | :--- |
| **ID** | String | 6 chars | 2 bytes / char | 12 bytes |
| **Name** | String | 35 chars | 2 bytes / char | 70 bytes |
| **Description** | String | 75 chars | 2 bytes / char | 150 bytes |
| **Cost** | Double | N/A | 8-byte IEEE 754 | 8 bytes |
| **Total Record Size** | | | | **240 Bytes** |

---

## 📁 Repository Structure

    src/
    ├── Product.java              # Data model & fixed-width padding logic
    ├── RandProductMaker.java     # Entry GUI for saving binary records
    └── RandProductSearch.java    # Search GUI for querying binary records
    README.md

---

## 🚀 How to Run

### Prerequisites
* Java Development Kit (JDK 17 or higher)
* Java IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Execution Steps
1. Clone the repository:

   git clone https://github.com/deshyah/java-random-access-file-manager.git

2. Open the project in your Java IDE.
3. Run RandProductMaker.java to enter product records and create the ProductData.dat file.
4. Run RandProductSearch.java to search records in ProductData.dat.

---

## 💡 Key Engineering Takeaways

* **Constant-Time Positioning:** Fixed record lengths enable $O(1)$ lookup positioning via offset calculations ($\text{offset} = \text{index} \times 240$) using `RandomAccessFile.seek()`.
* **Binary Stream Overhead:** Storing structured binary data avoids text-parsing overhead and ensures strict layout control on disk.
* **Field Normalization:** Utility padding methods handle string truncation and space padding before writing data to the binary stream.
