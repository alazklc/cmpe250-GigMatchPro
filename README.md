# GigMatch Pro: Freelance Marketplace Simulation 💼🚀

> **CMPE250 - Data Structures and Algorithms Project**
>
> *A high-performance simulation of a gig economy platform (similar to Upwork/Armut), built with custom data structures to optimize real-time matching under strict algorithmic constraints.*

## 📖 About the Project
**GigMatch Pro** is a comprehensive system designed to match customers with freelancers based on skills, ratings, and availability. The platform manages thousands of users and service requests simultaneously, simulating a living economy with dynamic mechanics like freelancer burnout, skill evolution, and customer loyalty tiers.

Unlike standard applications, this project was built with **strict constraints**: usage of standard Java Collections (HashMap, HashSet, PriorityQueue) was forbidden to enforce deep algorithmic understanding.

## 🚀 Technical Highlights
To achieve efficiency and handle large-scale datasets (up to 500k users), the following custom data structures were implemented from scratch:

* **Custom `IndexedMaxHeap`:** A specialized Priority Queue that supports $O(\log N)$ updates. It allows for efficient retrieval of the "best" freelancer while enabling real-time updates to their position in the heap when their stats (rating, availability) change.
* **Custom `MyHashMap`:** A bucket-based hash map implementation handling collisions with chaining (LinkedLists) and dynamic resizing to ensure $O(1)$ average-time complexity for user lookups.
* **Composite Ranking Algorithm:** Freelancers are ranked using a complex integer-based scoring formula that weighs technical skills, communication, reliability, and burnout status.

## 📂 Project Structure

```text
gigmatch-pro/
├── src/
│   ├── Main.java             # Entry point and command processor
│   ├── GigMatchPro.java      # Core system logic (Facade pattern)
│   ├── IndexedMaxHeap.java   # Custom Priority Queue with index tracking
│   ├── MyHashMap.java        # Custom Hash Map implementation
│   ├── Freelancer.java       # User entity with dynamic skills & state
│   ├── Customer.java         # User entity with loyalty & budget logic
│   ├── ServiceHelper.java    # Static helper for skill/service definitions
│   └── User.java             # Abstract base class
