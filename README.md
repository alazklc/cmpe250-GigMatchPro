# GigMatch Pro: Freelance Marketplace Simulation

**Course:** CMPE250 - Data Structures and Algorithms  
**Context:** System Design & Custom Data Structure Implementation

## Project Overview

GigMatch Pro is a high-performance simulation of a gig economy platform, modeled after real-world systems like Upwork or Armut. The application facilitates real-time matching between customers and freelancers based on multi-dimensional criteria including technical skills, user ratings, and availability.

The simulation manages a dynamic ecosystem involving thousands of concurrent users and service requests. It incorporates complex mechanics such as freelancer burnout, skill evolution, and customer loyalty tiers to model a "living" economy.

**Key Design Constraint:** This project strictly prohibited the use of standard Java Collection libraries (e.g., `HashMap`, `HashSet`, `PriorityQueue`) to enforce a deep, first-principles understanding of algorithmic efficiency and memory management.

## Core Capabilities

* **Real-Time Matching:** Instantly pairs service requests with the most suitable freelancer using a composite scoring system.
* **Dynamic State Management:** Tracks fluctuating stats such as freelancer availability and rating changes, requiring constant re-balancing of data structures.
* **Scalable User Management:** Efficiently handles datasets of up to 500,000 users with optimized lookups and storage.

## Technical Implementation

To achieve the necessary throughput and latency targets without standard libraries, the following custom data structures and algorithms were implemented:

### 1. Optimized Priority Management (`IndexedMaxHeap`)
A specialized priority queue was built to manage freelancer rankings. Unlike a standard binary heap, this implementation maintains an internal index map, allowing for arbitrary updates to an element's priority in $O(\log N)$ time.
* **Use Case:** When a freelancer completes a job or their rating changes, their position in the matching queue is instantly updated without a costly linear search.

### 2. High-Performance Storage (`MyHashMap`)
A custom hash map implementation utilizing the bucket array approach with separate chaining (LinkedLists) for collision resolution.
* **Performance:** Tuned for $O(1)$ average-time complexity for user retrieval.
* **Features:** Includes dynamic resizing (rehashing) to maintain a low load factor as the user base grows.

### 3. Composite Ranking Algorithm
Freelancers are not ranked by a single metric but by a weighted formula designed to balance quality and fairness. The ranking engine considers:
* **Skill Proficiency:** Technical alignment with the job.
* **Reliability:** Historical performance and ratings.
* **Availability & Burnout:** Penalties applied to overworked freelancers to simulate fatigue.

## Project Structure

```text
gigmatch-pro/
├── src/
│   ├── Main.java           # Entry point and command processor
│   ├── GigMatchPro.java    # Core system logic (Facade pattern)
│   ├── IndexedMaxHeap.java # Custom Priority Queue with index tracking
│   ├── MyHashMap.java      # Custom Hash Map implementation
│   ├── Freelancer.java     # User entity with dynamic skills & state
│   ├── Customer.java       # User entity with loyalty & budget logic
│   ├── ServiceHelper.java  # Static helper for skill/service definitions
│   └── User.java           # Abstract base class for system entities
