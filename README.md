- [The Birthday Presents Party](#the-birthday-presents-party)
  - [Folder Structure and Run Instructions](#folder-structure-and-run-instructions)
  - [Design](#design)
  - [Progress Guarantees](#progress-guarantees)
  - [Efficiency](#efficiency)
  - [Correctness](#correctness)
    - [Linerizable](#linerizable)
- [Atmospheric Temperature Reading Module](#atmospheric-temperature-reading-module)
  - [Folder Structure and Run Instructions](#folder-structure-and-run-instructions-1)
  - [Design](#design-1)
  - [Progress Guarantees](#progress-guarantees-1)
    - [Lock-Free](#lock-free)
  - [Efficiency](#efficiency-1)
  - [Correctness](#correctness-1)

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
`Servant.addGift()` consists of polling the next presents from the sack (ConcurrentLinkedQueue) and adding it to the `presentList`.
`Servant.writeThankYouNote()` consists of polling the first present from the list (ConcurrentLinkedPresentList) and adding if it returns a non-null Integer we increment the atomic integer keeping track of the total number of presents.

## Progress Guarantees

- Wait-Free
  - All threads always make progress.

## Efficiency

Due to the large amount of contention seen in this scenario we would expect to see a lot of re-traversals resulting in a longer runtime.

## Correctness

Correctness is proven by keeping a running tally of Thank-You notes successfully written.
Therefore, we can ensure that the number of presents received is equal to the number of Thank-You notes.

### Linerizable

- The Non-Blocking Linked List has clear linearization points for all operations
  - add
  - Remove
    - linearized when the mark is set
  - Contains
    - A successful call is linearized when an unmarked matching node is found
    - Unsuccessful contains() within its execution interval at the earlier of the following points: (1) the point where a removed matching node, or a node with a key greater than the one being searched for is found, and (2) the point immediately before a new matching node is added to the list.

# Atmospheric Temperature Reading Module

## Folder Structure and Run Instructions

- An exposed main file `MainTempModule.java` is in the root directory for your convenience
- All necessary Files are found in the `./temps` directory
  - `Temps.java` is the Main file for this problem which also represents the main processing module
  - `TempSensor.java` is the thread runnable representing the temperature sensors described in the problem.

## Design

The problem requires that all sensors take reading every minute. Further, the temperature module as a whole analyzes the temperature readings in 60 minute intervals.
As such, each sensor thread stores their `TempReading`'s in a shared `ConcurrentSkipListSet`. Once the time interval as elapsed, the Set is pushed onto the `ConcurrentLinkedDequeue` Shared between the greater Temperature Module and the sensors.

Sensors keep track of time locally and globally though a shared atomic integer. The thread which completes its temperature reading last will increment the `globalTimeStamp`.

## Progress Guarantees

### Lock-Free

While there are no locks used in this implementation, threads are required to periodically wait for other threads to finish before completing and pushing the current data block onto the set.

## Efficiency

The `TempReading` comparator keeps the readings sorted by TimeStamp, Temp,
followed by motorId. This allows us to quickly grab the highest and lowest temp
reading for each timestamp by navigating through the array representation by
blocks of `THREAD_COUNT`.

## Correctness

Though the use of atomic integer flags we can ensure that each sensor take a
valid reading during each time interval. Firstly, once a reading has been
taken, we increment the `numComplete` atomic integer which denotes the number of
threads waiting. The last thread checks its last `if(numComplete.get() == Temps.THREAD_COUNT)`. Only the last thread to complete its temperature reading
can increment the `globalTimeStamp`, the rest begin their wait until the next
reading interval. In the unlikely case that the slowest thread from the last
temperature reading has still yet to complete it's reading then we will continue
to wait, we do this by checking if the `globalTimeStamp` matches with the
Thread's local timeStamp.
