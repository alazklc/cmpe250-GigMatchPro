import java.util.ArrayList;

public class GigMatchPro {

    private MyHashMap<String, Customer> allCustomers;
    private MyHashMap<String, Freelancer> allFreelancers;
    private MyHashMap<String, IndexedMaxHeap> serviceHeaps;

    public GigMatchPro() {
        //Optimization: I am start ing with larger capacity
        this.allCustomers = new MyHashMap<>(10000);
        this.allFreelancers = new MyHashMap<>(10000);
        this.serviceHeaps = new MyHashMap<>();

        for (String serviceName : ServiceHelper.getServiceNames()) {
            serviceHeaps.put(serviceName, new IndexedMaxHeap());
        }
    }

    public String registerCustomer(String id) {
        if (allCustomers.containsKey(id) || allFreelancers.containsKey(id)) {
            return "Some error occurred in register_customer.";
        }
        Customer c = new Customer(id);
        allCustomers.put(id, c);
        return "registered customer " + id;
    }

    public String registerFreelancer(String id, String service, int price, int t, int c, int r, int e, int a) {
        if (allCustomers.containsKey(id) || allFreelancers.containsKey(id)) {
            return "Some error occurred in register_freelancer.";
        }
        if (price <= 0 || t < 0 || t > 100 || c < 0 || c > 100 || r < 0 || r > 100 ||
                e < 0 || e > 100 || a < 0 || a > 100) {
            return "Some error occurred in register_freelancer.";
        }
        if (ServiceHelper.getSkillProfile(service) == null) {
            return "Some error occurred in register_freelancer.";
        }

        Freelancer f = new Freelancer(id, service, price, t, c, r, e, a);
        allFreelancers.put(id, f);
        serviceHeaps.get(service).insert(f);
        return "registered freelancer " + id;
    }

    /*
     * Requests a job for a customer for a specific service.
     * 1. Extracts the top 'k' candidates from the service heap based on composite score.
     * 2. Skips freelancers who are unavailable or blacklisted by this specific customer.
     * 3. Displays the candidates and automatically employs the best-ranked freelancer.
     * 4. Re-inserts non-selected candidates back into the heap.
     */
    public String requestJob(String customerId, String service, int k) {
        Customer c = allCustomers.get(customerId);
        IndexedMaxHeap heap = serviceHeaps.get(service);

        if (c == null || heap == null) {
            return "Some error occurred in request_job.";
        }

        ArrayList<Freelancer> candidates = new ArrayList<>();
        ArrayList<Freelancer> toReinsert = new ArrayList<>();

        while (candidates.size() < k && !heap.isEmpty()) {
            Freelancer f = heap.extractMax();

            if (f.isAvailable && !c.isBlacklisted(f.getId())) {
                candidates.add(f);
            } else if (f.isAvailable) {
                toReinsert.add(f);
            }
        }

        for (Freelancer f : toReinsert) {
            heap.insert(f);
        }

        if (candidates.isEmpty()) {
            return "no freelancers available";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("available freelancers for ").append(service).append(" (top ").append(candidates.size()).append("):");

        for (Freelancer f : candidates) {
            //String.format ensures %.1f formatting (e.g.: 4.7 vs 4.6666)
            sb.append(String.format("\n%s - composite: %d, price: %d, rating: %.1f",
                    f.getId(), f.compositeScore, f.price, f.getAverageRating()));
        }

        Freelancer best = candidates.get(0);

        for (int i = 1; i < candidates.size(); i++) {
            heap.insert(candidates.get(i));
        }

        best.isAvailable = false;
        best.currentCustomer = c;
        c.addEmployment(best);

        sb.append("\nauto-employed best freelancer: ").append(best.getId())
                .append(" for customer ").append(c.getId());

        return sb.toString();
    }

    /*
     * Manually employs a specific freelancer for a customer.
     * Validates that the freelancer exists, is available, and is not blacklisted by the customer.
     * Upon success, marks the freelancer as unavailable and removes them from the service heap to prevent double-booking.
     */
    public String employFreelancer(String customerId, String freelancerId) {
        Customer c = allCustomers.get(customerId);
        Freelancer f = allFreelancers.get(freelancerId);

        if (c == null || f == null || !f.isAvailable || c.isBlacklisted(freelancerId)) {
            return "Some error occurred in employ.";
        }

        f.isAvailable = false;
        f.currentCustomer = c;
        c.addEmployment(f);

        //OPTIMIZATION: I will pass object directly
        serviceHeaps.get(f.serviceType).remove(f);

        return String.format("%s employed %s for %s",
                c.getId(), f.getId(), f.serviceType);
    }

    /*
     * Completes an active job and updates the freelancer's rating.
     * 1. Calculates payment (applying customer loyalty subsidy) and updates customer spending.
     * 2. Updates freelancer's average rating using the weighted formula.
     * 3. If rating >= 4, triggers skill gain mechanism (Primary +2, Secondary +1).
     * 4. Recalculates composite score and re-inserts freelancer into the heap.
     */
    public String completeAndRate(String freelancerId, int rating) {
        Freelancer f = allFreelancers.get(freelancerId);

        if (f == null || f.isAvailable || f.currentCustomer == null || rating < 0 || rating > 5) {
            return "Some error occurred in complete_and_rate.";
        }

        Customer c = f.currentCustomer;

        int payment = c.getPaymentAmount(f.price);
        c.addPayment(payment);
        c.removeEmployment(f.getId());

        f.isAvailable = true;
        f.currentCustomer = null;

        f.addRating(rating);
        f.completedJobs++;
        f.completedJobsThisMonth++;

        if (rating >= 4) {
            f.applySkillGains();
        }

        f.recalculateCompositeScore();
        serviceHeaps.get(f.serviceType).insert(f);

        return String.format("%s completed job for %s with rating %d",
                f.getId(), c.getId(), rating);
    }

    /*
     * Handles a job cancellation initiated by the freelancer.
     * Penalties applied:
     * 1. Receives a 0-star rating.
     * 2. Suffers skill degradation (-3 to all skills).
     * 3. If 5 or more cancellations occur in one month, the freelancer is permanently banned (platform blacklist).
     */
    public String cancelByFreelancer(String freelancerId) {
        Freelancer f = allFreelancers.get(freelancerId);

        if (f == null || f.isAvailable || f.currentCustomer == null) {
            return "Some error occurred in cancel_by_freelancer.";
        }

        Customer c = f.currentCustomer;
        String customerId = c.getId();

        c.removeEmployment(f.getId());
        f.isAvailable = true;
        f.currentCustomer = null;

        f.addRating(0);
        f.cancelledJobs++;
        f.cancelledJobsThisMonth++;
        f.applySkillDegradation();

        f.recalculateCompositeScore();

        String output = String.format("cancelled by freelancer: %s cancelled %s",
                f.getId(), customerId);

        if (f.cancelledJobsThisMonth >= 5) {
            allFreelancers.remove(f.getId());
            //OPTIMIZATION: Passing the object
            serviceHeaps.get(f.serviceType).remove(f);
            output += "\nplatform banned freelancer: " + f.getId();
        } else {
            serviceHeaps.get(f.serviceType).insert(f);
        }

        return output;
    }

    /*
     * Handles a job cancellation initiated by the customer.
     * The customer loses loyalty progress equivalent to $250 of spending per cancellation.
     * The freelancer is made available again without rating or skill penalties.
     */
    public String cancelByCustomer(String customerId, String freelancerId) {
        Customer c = allCustomers.get(customerId);

        if (c == null) {
            return "Some error occurred in cancel_by_customer.";
        }

        Freelancer f = c.getEmployment(freelancerId);
        if (f == null) {
            return "Some error occurred in cancel_by_customer.";
        }

        c.incrementCancellations();
        c.removeEmployment(freelancerId);
        f.isAvailable = true;
        f.currentCustomer = null;

        if (allFreelancers.containsKey(freelancerId)) {
            serviceHeaps.get(f.serviceType).insert(f);
        }

        return String.format("cancelled by customer: %s cancelled %s",
                customerId, freelancerId);
    }

    public String blacklist(String customerId, String freelancerId) {
        Customer c = allCustomers.get(customerId);
        if (c == null || !allFreelancers.containsKey(freelancerId)) {
            return "Some error occurred in blacklist.";
        }
        if (c.isBlacklisted(freelancerId)) {
            return "Some error occurred in blacklist.";
        }
        c.blacklistFreelancer(freelancerId);
        return String.format("%s blacklisted %s", customerId, freelancerId);
    }

    public String unblacklist(String customerId, String freelancerId) {
        Customer c = allCustomers.get(customerId);
        if (c == null || !allFreelancers.containsKey(freelancerId)) {
            return "Some error occurred in unblacklist.";
        }
        if (!c.unblacklistFreelancer(freelancerId)) {
            return "Some error occurred in unblacklist.";
        }
        return String.format("%s unblacklisted %s", customerId, freelancerId);
    }

    public String changeService(String freelancerId, String newService, int newPrice) {
        Freelancer f = allFreelancers.get(freelancerId);

        if (f == null) {
            return "Some error occurred in change_service.";
        }

        if (ServiceHelper.getSkillProfile(newService) == null) {
            return "Some error occurred in change_service.";
        }

        if (newPrice <= 0) {
            return "Some error occurred in change_service.";
        }

        String oldService = f.serviceType;

        f.pendingServiceChange = newService;
        f.pendingPrice = newPrice;

        return String.format("service change for %s queued from %s to %s",
                freelancerId, oldService, newService);
    }

    /*
     * Simulates the passage of one month.
     * 1. Checks Burnout: Marks freelancers as burned out if completed jobs >= 5. Recovers if jobs <= 2.
     * 2. Service Changes: Applies any queued service/price changes and updates skill profiles.
     * 3. Loyalty: Updates customer loyalty tiers based on total effective spending.
     * 4. Resets monthly job counters for all freelancers.
     */
    public String simulateMonth() {
        ArrayList<Freelancer> freelancers = allFreelancers.getAllValues();
        ArrayList<Customer> customers = allCustomers.getAllValues();

        for (Freelancer f : freelancers) {
            boolean needsScoreRecalculation = false;
            boolean serviceChanged = false;

            if (f.isBurnedOut) {
                if (f.completedJobsThisMonth <= 2) {
                    f.isBurnedOut = false;
                    needsScoreRecalculation = true;
                }
            } else {
                if (f.completedJobsThisMonth >= 5) {
                    f.isBurnedOut = true;
                    needsScoreRecalculation = true;
                }
            }

            if (f.pendingServiceChange != null) {
                String oldService = f.serviceType;
                String newService = f.pendingServiceChange;

                if (f.isAvailable) {
                    // OPTIMIZATION: Pass object directly
                    serviceHeaps.get(oldService).remove(f);
                }

                f.serviceType = newService;
                f.price = f.pendingPrice;

                f.pendingServiceChange = null;
                f.pendingPrice = 0;

                serviceChanged = true;
                needsScoreRecalculation = true;
            }

            f.resetMonthlyCounters();

            if (needsScoreRecalculation) {
                f.recalculateCompositeScore();
            }

            if (f.isAvailable) {
                if (serviceChanged) {
                    serviceHeaps.get(f.serviceType).insert(f);
                } else if (needsScoreRecalculation) {
                    serviceHeaps.get(f.serviceType).insert(f);
                }
            }
        }

        for (Customer c : customers) {
            c.updateLoyaltyTier();
        }

        return "month complete";
    }

    public String queryFreelancer(String freelancerId) {
        Freelancer f = allFreelancers.get(freelancerId);
        if (f == null) {
            return "Some error occurred in query_freelancer.";
        }
        return f.query();
    }

    public String queryCustomer(String customerId) {
        Customer c = allCustomers.get(customerId);
        if (c == null) {
            return "Some error occurred in query_customer.";
        }
        return c.query();
    }

    public String updateSkill(String freelancerId, int t, int c, int r, int e, int a) {
        Freelancer f = allFreelancers.get(freelancerId);
        if (f == null || t < 0 || t > 100 || c < 0 || c > 100 || r < 0 || r > 100 ||
                e < 0 || e > 100 || a < 0 || a > 100) {
            return "Some error occurred in update_skill.";
        }

        f.skills[0] = t;
        f.skills[1] = c;
        f.skills[2] = r;
        f.skills[3] = e;
        f.skills[4] = a;

        f.recalculateCompositeScore();

        if (f.isAvailable) {
            serviceHeaps.get(f.serviceType).insert(f);
        }

        return "updated skills of " + freelancerId + " for " + f.serviceType;
    }
}