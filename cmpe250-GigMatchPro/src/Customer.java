/**
 * Represents a Customer user on the platform.
 * Manages spending, loyalty, and employment.
 */
public class Customer extends User {

    // Loyalty
    private int totalSpent;
    private int cancellationPenalties;
    private String cachedLoyaltyTier;

    // Blacklist
    private MyHashSet<String> blacklist;

    // Employment
    private MyHashMap<String, Freelancer> currentEmployments;
    public int totalEmploymentCount;

    public Customer(String id) {
        super(id);
        this.totalSpent = 0;
        this.cancellationPenalties = 0;
        this.totalEmploymentCount = 0;
        this.cachedLoyaltyTier = "BRONZE";
        this.blacklist = new MyHashSet<>();
        this.currentEmployments = new MyHashMap<>();
    }

    private int getEffectiveSpending() {
        int penalty = this.cancellationPenalties * 250;
        return Math.max(0, this.totalSpent - penalty);
    }

    /*
     * Determines loyalty tier based on total spending (minus cancellation penalties).
     * - PLATINUM: $5,000+ (15% subsidy)
     * - GOLD: $2,000 - $4,999 (10% subsidy)
     * - SILVER: $500 - $1,999 (5% subsidy)
     * - BRONZE: $0 - $499 (0% subsidy).
     */
    private String calculateLoyaltyTier() {
        int spending = getEffectiveSpending();
        if (spending >= 5000) return "PLATINUM";
        if (spending >= 2000) return "GOLD";
        if (spending >= 500) return "SILVER";
        return "BRONZE";
    }

    public String getLoyaltyTier() {
        return this.cachedLoyaltyTier;
    }

    public void updateLoyaltyTier() {
        this.cachedLoyaltyTier = calculateLoyaltyTier();
    }

    public double getSubsidyRate() {
        switch (this.cachedLoyaltyTier) {
            case "PLATINUM": return 0.15;
            case "GOLD":     return 0.10;
            case "SILVER":   return 0.05;
            default:         return 0.0;
        }
    }

    /*
     * Calculates the actual amount the customer pays after applying the loyalty subsidy.
     * Formula: floor(FreelancerPrice * (1 - SubsidyRate)).
     * Note: The freelancer still receives the full price; the platform covers the difference.
     */
    public int getPaymentAmount(int freelancerPrice) {
        double subsidy = getSubsidyRate();
        double payment = freelancerPrice * (1.0 - subsidy);
        return (int) Math.floor(payment);
    }

    public void addPayment(int amountPaid) {
        this.totalSpent += amountPaid;
    }

    public void incrementCancellations() {
        this.cancellationPenalties++;
    }

    public void addEmployment(Freelancer f) {
        if (!currentEmployments.containsKey(f.getId())) {
            this.totalEmploymentCount++;
        }
        currentEmployments.put(f.getId(), f);
    }

    public Freelancer removeEmployment(String freelancerId) {
        return currentEmployments.remove(freelancerId);
    }

    public boolean isEmploying(String freelancerId) {
        return currentEmployments.containsKey(freelancerId);
    }

    public Freelancer getEmployment(String freelancerId) {
        return currentEmployments.get(freelancerId);
    }

    public void blacklistFreelancer(String freelancerId) {
        blacklist.add(freelancerId);
    }

    public boolean unblacklistFreelancer(String freelancerId) {
        return blacklist.remove(freelancerId);
    }

    public boolean isBlacklisted(String freelancerId) {
        return blacklist.contains(freelancerId);
    }

    public String query() {
        return String.format("%s: total spent: $%d, loyalty tier: %s, " +
                        "blacklisted freelancer count: %d, total employment count: %d",
                this.id,
                this.totalSpent,
                getLoyaltyTier(),
                blacklist.size(),
                this.totalEmploymentCount);
    }
}
