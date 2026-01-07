public class Freelancer extends User {

    //Job Info
    public String serviceType;
    public int price;
    public boolean isAvailable = true;
    public Customer currentCustomer = null;

    //Store average rating using running average formula
    private double averageRating;

    public int completedJobs = 0;
    public int cancelledJobs = 0;

    //Monthly Counters
    public int completedJobsThisMonth = 0;
    public int cancelledJobsThisMonth = 0;
    public boolean isBurnedOut = false;

    //Skills {T, C, R, E, A}
    public int[] skills = new int[5];

    //Ranking
    public int compositeScore;

    //Service Change
    public String pendingServiceChange = null;
    public int pendingPrice = 0;

    //OPTIMIZATION: Store heap index directly to avoid HashMap lookups.
    public int heapIndex = -1;

    public Freelancer(String id, String service, int price, int t, int c, int r, int e, int a) {
        super(id);
        this.serviceType = service;
        this.price = price;
        this.skills[0] = t;
        this.skills[1] = c;
        this.skills[2] = r;
        this.skills[3] = e;
        this.skills[4] = a;

        //Start with initial average rating of 5.0 (counts as 1 rating)
        this.averageRating = 5.0;
        this.completedJobs = 0;

        recalculateCompositeScore();
    }

    public double getAverageRating() {
        return averageRating;
    }

    /*
     * Updates the average rating using the formula:
     * NewAvg = ((OldAvg * N) + Rating) / (N + 1).
     * Where N is the total count of completed AND cancelled jobs.
     */
    public void addRating(int rating) {
        int currentCount = completedJobs + cancelledJobs + 1; // +1 for initial 5.0 rating
        averageRating = (averageRating * currentCount + rating) / (currentCount + 1.0);
    }

    public void applySkillGains() {
        ServiceHelper.applySkillGains(this.skills, this.serviceType);
    }

    public void applySkillDegradation() {
        for (int i = 0; i < 5; i++) {
            this.skills[i] = Math.max(0, this.skills[i] - 3);
        }
    }

    public void resetMonthlyCounters() {
        this.completedJobsThisMonth = 0;
        this.cancelledJobsThisMonth = 0;
    }

    /*
     * Calculates the integer composite score used for ranking.
     * Formula: (SkillScore * 0.55) + (RatingScore * 0.25) + (ReliabilityScore * 0.20) - BurnoutPenalty.
     * - SkillScore: Dot product of freelancer skills and service requirements.
     * - BurnoutPenalty: 0.45 if burned out, 0.0 otherwise.
     * Result is floored and multiplied by 10000 to return an integer.
     */
    public void recalculateCompositeScore() {
        int[] serviceProfile = ServiceHelper.getSkillProfile(this.serviceType);
        int maxScore = ServiceHelper.getTheoreticalMaxScore(this.serviceType);

        // 1. Skill Score
        int dotProduct = ServiceHelper.calculateSkillDotProduct(this.skills, serviceProfile);
        double skillScore = (double) dotProduct / maxScore;

        // 2. Rating Score
        double ratingScore = getAverageRating() / 5.0;

        // 3. Reliability Score
        double reliabilityScore;
        int totalJobs = this.completedJobs + this.cancelledJobs;
        if (totalJobs == 0) {
            reliabilityScore = 1.0;
        } else {
            reliabilityScore = (double) this.completedJobs / totalJobs;
        }

        // 4. Burnout Penalty
        double burnoutPenalty = this.isBurnedOut ? 0.45 : 0.0;

        // 5. Final Weighted Sum
        double weightedSum = (0.55 * skillScore) +
                (0.25 * ratingScore) +
                (0.20 * reliabilityScore) -
                burnoutPenalty;

        this.compositeScore = (int) Math.floor(10000 * weightedSum);
    }

    public String query() {
        //String.format to ensure correct output formatting (e.g., %.1f)
        return String.format("%s: %s, price: %d, rating: %.1f, completed: %d, " +
                        "cancelled: %d, skills: (%d,%d,%d,%d,%d), " +
                        "available: %s, burnout: %s",
                this.id, this.serviceType, this.price,
                getAverageRating(),
                this.completedJobs, this.cancelledJobs,
                this.skills[0], this.skills[1], this.skills[2],
                this.skills[3], this.skills[4],
                this.isAvailable ? "yes" : "no",
                this.isBurnedOut ? "yes" : "no");
    }
}