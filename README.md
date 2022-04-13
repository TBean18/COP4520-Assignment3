- [The Birthday Presents Party](#the-birthday-presents-party)
  - [Folder Structure and Run Instructions](#folder-structure-and-run-instructions)
  - [Design](#design)
  - [Progress Guarantees](#progress-guarantees)
  - [Efficiency](#efficiency)
  - [Correctness](#correctness)
  - [Experimental Evaluation](#experimental-evaluation)
- [Atmospheric Temperature Reading Module](#atmospheric-temperature-reading-module)
  - [Folder Structure and Run Instructions](#folder-structure-and-run-instructions-1)
  - [Design](#design-1)
  - [Progress Guarantees](#progress-guarantees-1)
  - [Efficiency](#efficiency-1)
  - [Correctness](#correctness-1)
    - [Lock-Free](#lock-free)

# The Birthday Presents Party

## Folder Structure and Run Instructions

- An exposed main file `MainPresent.java` is in the root directory for you convenience
- All necessary Files are found in the `./presents` directory
  - `Presents.java` is the Main file for this problem
  - `ConcurrentLinkedPresentList.java` is the LinkedList Implementation for this problem
  - `Servant.java` is the thread runnable representing the Servants described in the problem.

## Design

The problem requires that we implement a concurrent linked list.
Thankfully, we have learned how to do so from our recent chapters in the textbook.
As a result, I review the book's implementation of the Lock Free Linked List and implemented it myself in `ConcurrentLinkedPresentList.java`.

Servants are told to alternate between adding presents from the sack onto the list and write thankyou notes for a present on the list.
`Servant.addGift()` consists of polling the next presents from the sack (ConcurrentLinkedDequeue) and adding it to the `presentList`.
`Servant.writeThankYouNote()` consists of polling the first present from the list (ConcurrentLinkedPresentList) and adding if it returns a non-null Integer we increment the atomic integer keeping track of the total number of presents.

## Progress Guarantees

- Wait-Free
  - All threads always make progress.
- Linerizable
  - The Non-Blocking Linked List has clear linearization points for all operations
    - add
    - Remove
      - linearized when the mark is set
    - Contains
      - A successful call is linearized when an unmarked matching node is found
      - Unsuccessful contains() within its execution interval at the earlier of the following points: (1) the point where a removed matching node, or a node with a key greater than the one being searched for is found, and (2) the point immediately before a new matching node is added to the list.

## Efficiency

Due to the large amount of contention seen in this scenario we would expect to see a lot of re-traversals resulting in a longer runtime.

## Correctness

Correctness is proven by keeping a running tally of Thank-You notes successfully written.
Therefore, we can ensure that the number of presents received is equal to the number of Thank-You notes.

## Experimental Evaluation

| Thread # | Avg. Runtime |
| :------: | ------------ |
|    1     |              |
|    2     |              |
|    4     |              |

# Atmospheric Temperature Reading Module

## Folder Structure and Run Instructions

- An exposed main file `MainTempModule.java` is in the root directory for your convenience
- All necessary Files are found in the `./temps` directory
  - `Temps.java` is the Main file for this problem which also represents the main processing module
  - `TempSensor.java` is the thread runnable representing the temperature sensors described in the problem.

## Design

The problem requires that all sensors

## Progress Guarantees

## Efficiency

## Correctness

### Lock-Free
